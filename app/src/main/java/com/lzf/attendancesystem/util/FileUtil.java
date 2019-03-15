package com.lzf.attendancesystem.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by MJCoder on 2019-03-14.
 */

public class FileUtil {

    /**
     * 获取新建的文件
     *
     * @param context  环境上下文
     * @param dirName  新建的文件所在的上级目录；可以为空
     * @param fileName
     * @return
     */
    public static File getNewFile(Context context, String dirName, String fileName) {
        File currentFile = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir;
                if (dirName != null) {
                    dir = new File(Environment.getExternalStorageDirectory(), "AttendanceSystem/." + dirName);
                } else {
                    dir = new File(Environment.getExternalStorageDirectory(), "AttendanceSystem");
                }
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //                File[] files = dir.listFiles();//获取文件列表
                //                for (int i = 0; i < files.length; i++) {
                //                    files[i].delete();//删除该文档下的所有文件
                //                }
                currentFile = new File(dir, fileName);
                if (!currentFile.exists()) {
                    currentFile.createNewFile();
                }
            } else {
                String dirTemp = null;
                StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                Class<?>[] paramClasses = {};
                Method getVolumePathsMethod;
                getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
                // 在反射调用之前将此对象的 accessible 标志设置为 true，以此来提升反射速度。
                getVolumePathsMethod.setAccessible(true);
                Object[] params = {};
                Object invoke = getVolumePathsMethod.invoke(storageManager, params);
                for (int i = 0; i < ((String[]) invoke).length; i++) {
                    if (!((String[]) invoke)[i].equals(Environment.getExternalStorageDirectory().toString())) {
                        dirTemp = ((String[]) invoke)[i];
                    }
                }
                File dir;
                if (dirName != null) {
                    dir = new File(dirTemp, "AttendanceSystem/." + dirName);
                } else {
                    dir = new File(dirTemp, "AttendanceSystem");
                }
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //                File[] files = dir.listFiles();//获取文件列表
                //                for (int i = 0; i < files.length; i++) {
                //                    files[i].delete();//删除该文档下的所有文件
                //                }
                currentFile = new File(dir, fileName);
                if (!currentFile.exists()) {
                    currentFile.createNewFile();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentFile;
    }
}
