package com.nanlt.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Upload {

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */

    public static void  downLoadFromUrl(String urlStr, String fileName, String savePath,LoadListener loadListener) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            loadListener.onStart(conn.getContentLength());
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //文件保存位置
            File saveDir = new File(savePath);
            if(!saveDir.exists()){
                saveDir.mkdir();
            }
            File file = new File(saveDir+File.separator+fileName);
            FileOutputStream fos = new FileOutputStream(file);
            //获取字节数组
            byte[] getData = readInputStream(loadListener,inputStream);

            fos.write(getData);
            fos.flush();
            fos.close();
            inputStream.close();
            loadListener.onSuccess();
        }

    }



    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(LoadListener loadListener,InputStream inputStream) throws IOException {
        int count = 0;
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            count += len;
            loadListener.onLoadIng(count);
        }
        bos.close();
        return bos.toByteArray();
    }

    public  interface  LoadListener{
        void onStart(int contentLength);
        void onLoadIng(int currentLength);
        void onSuccess();
    }
}
