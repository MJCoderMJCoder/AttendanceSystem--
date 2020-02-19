package com.lzf.attendancesystem.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.webkit.*;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.lzf.attendancesystem.R;
import com.lzf.attendancesystem.ZffApplication;
import com.lzf.attendancesystem.bean.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SetAddressRangeActivity extends AppCompatActivity {

    private AttendanceAddressDao attendanceAddressDao = ZffApplication.getDaoSession(this).getAttendanceAddressDao();
    private AttendanceAddress attendanceAddress = null;
    private List<AttendanceAddress> attendanceAddresses = null;
    private MapView mapView;
    private AMap aMap;

    private MarkerOptions markerOptions = null;
    private Marker marker = null;
    private Animation markerAnimation = null;
    // 定义 Marker 点击事件监听
    private AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    };
    // 定义 Marker拖拽的监听
    private AMap.OnMarkerDragListener markerDragListener = new AMap.OnMarkerDragListener() {

        // 当marker开始被拖动时回调此方法, 这个marker的位置可以通过getPosition()方法返回。
        // 这个位置可能与拖动的之前的marker位置不一样。
        // marker 被拖动的marker对象。
        @Override
        public void onMarkerDragStart(Marker arg0) {
            // TODO Auto-generated method stub
            Log.v("onMarkerDragStart", arg0 + "");

        }

        // 在marker拖动完成后回调此方法, 这个marker的位置可以通过getPosition()方法返回。
        // 这个位置可能与拖动的之前的marker位置不一样。
        // marker 被拖动的marker对象。
        @Override
        public void onMarkerDragEnd(Marker arg0) {
            // TODO Auto-generated method stub
            Log.v("onMarkerDragEnd", arg0 + "");
            LatLng latLng = arg0.getPosition();
            attendanceAddress.setLatitude(latLng.latitude);
            attendanceAddress.setLongitude(latLng.longitude);
        }

        // 在marker拖动过程中回调此方法, 这个marker的位置可以通过getPosition()方法返回。
        // 这个位置可能与拖动的之前的marker位置不一样。
        // marker 被拖动的marker对象。
        @Override
        public void onMarkerDrag(Marker arg0) {
            // TODO Auto-generated method stub
            Log.v("onMarkerDrag", arg0 + "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address_range);
        mapView = (MapView) findViewById(R.id.mapView);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.showMyLocation(false);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.showIndoorMap(true);     //true：显示室内地图；false：不显示；
        UiSettings uiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        uiSettings.setMyLocationButtonEnabled(true); //设置默认定位按钮是否显示，非必需设置。
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        attendanceAddresses = attendanceAddressDao.queryBuilder().orderAsc(AttendanceAddressDao.Properties.AttendanceAddressId).list();
        if (attendanceAddresses != null && attendanceAddresses.size() > 0) {
            attendanceAddress = attendanceAddresses.get(0);
            LatLng latLng = new LatLng(attendanceAddress.getLatitude(), attendanceAddress.getLongitude());
            //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 30, 0));
            aMap.animateCamera(cameraUpdate);
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("  纸纷飞"); //点标记的标题
            markerOptions.snippet("考勤地址"); //点标记的内容
            markerOptions.visible(true); //点标记是否可见
            //                markerOptions.anchor(); //点标记的锚点
            //                markerOptions.alpha(); //点的透明度
            markerOptions.draggable(true);//设置Marker可拖动
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_on_black_24dp)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOptions.setFlat(false);//设置marker平贴地图效果：false-不平贴
            marker = aMap.addMarker(markerOptions);
            markerAnimation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 360, 0, 0, 0);
            markerAnimation.setDuration(1000L);
            markerAnimation.setInterpolator(new LinearInterpolator());
            marker.setAnimation(markerAnimation);
            marker.startAnimation();
            // 绑定marker拖拽事件
            aMap.setOnMarkerDragListener(markerDragListener);
            // 绑定 Marker 被点击事件
            aMap.setOnMarkerClickListener(markerClickListener);
        } else {
            if (attendanceAddress == null) {
                attendanceAddress = new AttendanceAddress();
            }
            aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 30, 0));
                    aMap.animateCamera(cameraUpdate);
                    if (null == markerOptions && null == marker && null == markerAnimation) {
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("  纸纷飞"); //点标记的标题
                        markerOptions.snippet("考勤地址"); //点标记的内容
                        markerOptions.visible(true); //点标记是否可见
                        //                markerOptions.anchor(); //点标记的锚点
                        //                markerOptions.alpha(); //点的透明度
                        markerOptions.draggable(true);//设置Marker可拖动
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_on_black_24dp)));
                        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                        markerOptions.setFlat(false);//设置marker平贴地图效果：false-不平贴
                        marker = aMap.addMarker(markerOptions);
                        markerAnimation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 360, 0, 0, 0);
                        markerAnimation.setDuration(1000L);
                        markerAnimation.setInterpolator(new LinearInterpolator());
                        marker.setAnimation(markerAnimation);
                        marker.startAnimation();
                        // 绑定marker拖拽事件
                        aMap.setOnMarkerDragListener(markerDragListener);
                        // 绑定 Marker 被点击事件
                        aMap.setOnMarkerClickListener(markerClickListener);
                    }
                }
            });
        }
    }

    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.submitBtn:
                if (attendanceAddresses != null && attendanceAddresses.size() > 0) {
                    attendanceAddressDao.update(attendanceAddress);
                } else {
                    attendanceAddressDao.insert(attendanceAddress);
                    attendanceAddresses = attendanceAddressDao.queryBuilder().orderAsc(AttendanceAddressDao.Properties.AttendanceAddressId).list();
                }
                Toast.makeText(this, "考勤地址设置成功！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mapView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mapView.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mapView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mapView.onDestroy();
        }
        finish();
    }
}
