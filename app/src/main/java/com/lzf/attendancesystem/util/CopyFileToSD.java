package com.lzf.attendancesystem.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 复制相关内容到SD卡中以便可视化查看
 * Created by MJCoder on 2017-10-09.
 */
public class CopyFileToSD {
    /**
     * 将过大的内容或是过长的字符串复制到SD卡中以便可视化查看
     *
     * @param fileContent 过大的内容或是过长的字符串（例如：json、log、服务端返回的大数据等）
     * @param fileName    复制到SD卡的文件名称（切记：需要包含后缀）
     */
    public static void txtFile(String fileContent, String fileName) {
        FileOutputStream fos = null;
        try {
            //文件复制到sd卡中
            fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + fileName);
            fos.write(fileContent.getBytes());  //将String字符串以字节流的形式写入到输出流中
            fos.close();
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭数据流
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 将Database文件复制到SD卡中以便可视化查看
     *
     * @param packageName  该APP的包名
     * @param databaseName Database文件的名称（切记：仅仅是名称不包含后缀）
     */
    public static void databaseFile(String packageName, String databaseName) {
        //找到文件的路径  /data/data/包名/databases/数据库名称
        File databaseFile = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/" + packageName + "/databases/" + databaseName + ".db");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            //文件复制到sd卡中
            fis = new FileInputStream(databaseFile);
            fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + databaseName + ".db");
            int len = 0;
            byte[] buffer = new byte[2048];
            while (-1 != (len = fis.read(buffer))) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭数据流
            try {
                if (fos != null)
                    fos.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 将SharedPrefs文件复制到SD卡中以便可视化查看
     *
     * @param packageName     该APP的包名
     * @param sharedPrefsName SharedPrefs文件的名称（切记：仅仅是名称不包含后缀）
     */
    public static void sharedPrefsFile(String packageName, String sharedPrefsName) {
        //找到文件的路径  /data/data/包名/databases/数据库名称
        File sharedPrefsFile = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/" + packageName + "/shared_prefs/" + sharedPrefsName + ".xml");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            //文件复制到sd卡中
            fis = new FileInputStream(sharedPrefsFile);
            fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + sharedPrefsName + ".xml");
            int len = 0;
            byte[] buffer = new byte[2048];
            while (-1 != (len = fis.read(buffer))) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭数据流
            try {
                if (fos != null)
                    fos.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
