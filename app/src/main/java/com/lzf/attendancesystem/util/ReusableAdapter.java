package com.lzf.attendancesystem.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 可复用（多功能）的适配器
 */
public abstract class ReusableAdapter<T> extends BaseAdapter {
    /**
     * 该适配器要处理的一系列原始数据
     */
    private List<T> listData;
    /**
     * 该适配器要处理显示的布局文件的资源ID
     */
    private int listItemResource;

    /**
     * 可复用（多功能）的适配器的有参构造方法
     *
     * @param listData         该适配器要处理的一系列原始数据
     * @param listItemResource 该适配器要处理显示的布局文件的资源ID
     */
    public ReusableAdapter(List<T> listData, int listItemResource) {
        this.listData = listData;
        this.listItemResource = listItemResource;
    }

    /**
     * 获取一共有多少列；及一共有多少原始数据。
     *
     * @return 有多少列/有多少原始数据
     */
    public int getCount() {
        // 如果listDara不为空，则返回listData.size;否则返回0
        return (listData != null) ? listData.size() : 0;
    }

    /**
     * 通过列表视图的某个位置获取该位置的原始数据
     *
     * @param position 列表视图的某个位置
     * @return 列表视图的某个位置获取该位置的原始数据
     */
    public Object getItem(int position) {
        return listData.get(position);
    }

    /**
     * 通过列表视图的某个位置获取该位置的原始数据的相应的项目ID
     *
     * @param position 列表视图的某个位置
     * @return 列表视图的某个位置获取该位置的原始数据的相应的项目ID
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * 有多少列就会调用多少次getView(有多少个Item，那么getView方法就会被调用多少次)
     *
     * @param position    列表视图的某个位置
     * @param convertView 是系统提供的可用的View 的缓存对象
     * @param parent      父视图容器组件
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(parent.getContext(), convertView, parent, listItemResource, position);
        bindView(holder, (T) getItem(position));
        return holder.getItemView();
    }

    /**
     * 定义一个抽象方法，完成ViewHolder与相关数据集的绑定；
     * 创建新的BaseAdapter的时候，只需实现这个方法就好！
     *
     * @param holder 视图持有者：用于对ListView中的item的View进行子视图渲染
     * @param obj    该适配器要处理的一系列原始数据中的某项数据的对象信息
     * @see ViewHolder
     */
    public abstract void bindView(ViewHolder holder, T obj);

    /**
     * ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
     */
    public static class ViewHolder {
        /**
         * 存储ListView 的 item中的View
         */
        private SparseArray<View> viewsOflistViewItem;
        /**
         * 存放convertView
         */
        private View storeConvertView;
        /**
         * 位置、定位
         */
        private int position;
        /**
         * Context环境/上下文
         */
        private Context context;
        /**
         * 该变量用于格式化时间，方便界面显示。
         */
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM--dd HH:mm");

        /**
         * 构造方法，完成相关初始化（将convertView搬到这里，就需要传递一个context对象；这里把需要获取的部分都写到构造方法中！）
         *
         * @param context          环境/上下文
         * @param parent           父视图组件
         * @param listItemResource 将要处理显示的布局文件的资源ID
         */
        private ViewHolder(Context context, ViewGroup parent, int listItemResource) {
            // 存储ListView 的 item中的View
            viewsOflistViewItem = new SparseArray<View>();
            this.context = context;
            // View android.view.LayoutInflater.inflate(int resource, ViewGroup
            // root, boolean attachToRoot)【LayoutInflater：布局填充器】
            View convertView = LayoutInflater.from(context).inflate(listItemResource, parent, false);
            convertView.setTag(this);
            storeConvertView = convertView; // 存放convertView
        }

        /**
         * 绑定ViewHolder与item
         *
         * @param context          环境/上下文
         * @param convertView      是系统提供的可用的View 的缓存对象
         * @param parent           父视图组件
         * @param listItemResource 将要处理显示的布局文件的资源ID
         * @param position         列表视图的某个位置
         * @return 与item绑定后的ViewHolder
         */
        public static ViewHolder bind(Context context, View convertView, ViewGroup parent, int listItemResource, int position) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(context, parent, listItemResource);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.storeConvertView = convertView;
            }
            holder.position = position;
            return holder;
        }

        /**
         * 查找控件的方法：根据id获取viewsOflistViewItem集合中保存的控件
         *
         * @param id  资源ID
         * @param <T> 泛型类；View的子类
         * @return 泛型类；View的子类
         */
        public <T extends View> T getView(int id) {
            T t = (T) viewsOflistViewItem.get(id);
            if (t == null) {
                t = (T) storeConvertView.findViewById(id);
                viewsOflistViewItem.put(id, t);
            }
            return t;
        }

        /**
         * 获取当前条目
         *
         * @return 当前条目对应的视图组件
         */
        public View getItemView() {
            return storeConvertView;
        }

        /**
         * 获取列表视图的某条目对应的位置
         */
        public int getItemPosition() {
            return position;
        }

        /**
         * 设置TextView的文本内容
         *
         * @param id   视图控件的资源id
         * @param text 设置的内容
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        /**
         * 设置TextView中的文本对齐
         *
         * @param id      视图控件的资源id
         * @param gravity 设置对齐的具体值
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setTVGravity(int id, int gravity) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setGravity(gravity);
            }
            return this;
        }

        /**
         * 设置TextView中的文本颜色
         *
         * @param id    视图控件的资源id
         * @param color 设置文本颜色的具体值
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setTextColor(int id, int color) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
            return this;
        }

        /**
         * 设置View的背景颜色
         *
         * @param id    视图控件的资源id
         * @param color 设置背景颜色的具体值
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setBackgroundColor(int id, int color) {
            View view = getView(id);
            view.setBackgroundColor(color);
            return this;
        }

        /**
         * 设置ImageView图片src
         *
         * @param id          视图控件的资源id
         * @param drawableRes 设置图片的资源ID
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setImageResource(int id, int drawableRes) {
            View view = getView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            } else {
                view.setBackgroundResource(drawableRes);
            }
            return this;
        }

        /**
         * 设置View的单击事件监听
         *
         * @param id       视图控件的资源id
         * @param listener 单击事件监听器
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
            getView(id).setOnClickListener(listener);
            return this;
        }


        /**
         * 设置View触摸事件监听
         *
         * @param id       视图控件的资源id
         * @param listener 触摸事件监听器
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setOnTouchListener(int id, View.OnTouchListener listener) {
            getView(id).setOnTouchListener(listener);
            return this;
        }

        /**
         * 设置View的可见状态
         *
         * @param id      视图控件的资源id
         * @param visible 可见状态的具体值：VISIBLE、INVISIBLE、GONE
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setVisibility(int id, int visible) {
            getView(id).setVisibility(visible);
            return this;
        }

        /**
         * 设置View的标签
         *
         * @param id  视图控件的资源id
         * @param obj 设置标签的具体值
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setTag(int id, Object obj) {
            getView(id).setTag(obj);
            return this;
        }

        /**
         * 设置ListView
         *
         * @param id      视图控件的资源id
         * @param adapter 设置ListView的具体适配器
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setListView(int id, final BaseAdapter adapter) {
            ListView view = (ListView) getView(id);
            if (view instanceof ListView) {
                view.setAdapter(adapter);
            } else {
            }
            return this;
        }

        /**
         * 设置ProgressBar的进度条
         *
         * @param id       视图控件的资源id
         * @param max      设置ProgressBar的最大值
         * @param progress 设置ProgressBar的进度
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setProgressBar(int id, int max, int progress) {
            View view = getView(id);
            if (view instanceof ProgressBar) {
                ((ProgressBar) view).setMax(max);
                ((ProgressBar) view).setProgress(progress);
            } else {
            }
            return this;
        }

        /**
         * 设置View的颜色渲染
         *
         * @param id        视图控件的资源id
         * @param tintColor 设置View的颜色渲染的具体值
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ViewHolder setBackgroundTint(int id, int tintColor) {
            View view = getView(id);
            view.getBackground().setTint(tintColor);
            return this;
        }

        /**
         * 设置背景
         *
         * @param id    视图控件的资源id
         * @param resid 设置View的背景的资源ID
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        public ViewHolder setBackground(int id, int resid) {
            View view = getView(id);
            view.setBackgroundResource(resid);
            return this;
        }

        /**
         * 设置 ImageView 的网络图片
         *
         * @param id      视图控件的资源id
         * @param obj     设置 ImageView 的网络图片的URL
         * @param context 环境/上下文
         * @return 与item绑定后的ViewHolder（视图持有者：用于对ListView中的item的View进行子视图渲染）
         */
        //        public ViewHolder setImageByGlide(int id, String obj, Context context) {
        //            View view = getView(id);
        //            if (view instanceof ImageView) {
        //                Glide.with(context)
        //                        .load(obj) //UrlUtils.URL_Image +
        //                        .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
        //                        .error(R.mipmap.ic_launcher)// 加载图片失败的时候显示的默认图
        //                        .dontAnimate().placeholder(R.mipmap.ic_launcher) //解决Glide 图片闪烁问题
        //                        .into((ImageView) view);
        //            } else {
        //            }
        //            return this;
        //        }
        // 其他方法可自行扩展
    }

    /**
     * 删除ListView的所有数据并刷新界面
     */
    public void clear() {
        if (listData != null) {
            listData.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 向ListView中添加数据并刷新界面
     *
     * @param data 向ListView中添加的数据
     */
    public void add(T data) {
        listData.add(data);
        notifyDataSetChanged();
    }

    /**
     * 向ListView的指定位置中添加数据并刷新界面
     *
     * @param position ListView的指定位置
     * @param data     向ListView的指定位置中添加的数据
     */
    public void add(int position, T data) {
        listData.add(position, data);
        notifyDataSetChanged();
    }

    /**
     * 删除ListView的指定数据并刷新界面
     *
     * @param data ListView的指定数据
     */
    public void delete(T data) {
        listData.remove(data);
        notifyDataSetChanged();
    }

    /**
     * 更新ListView的指定位置中的数据并刷新界面
     *
     * @param position ListView的指定位置
     * @param data     ListView的指定位置中的数据
     */
    public void update(int position, T data) {
        listData.set(position, data);
        notifyDataSetChanged();
    }

    /**
     * 更新ListView的所有数据并刷新界面
     *
     * @param listData ListView的所有数据
     */
    public void updateAll(List<T> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }
}
