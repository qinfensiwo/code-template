package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/1/29 15:08\
 */
public class FastDFSClient
{
    /***
     * 初始化tracker信息
     */
    static {
        try
        {
            //获取tracker的配置文件fdfs_client.conf的位置
            String filePath = new ClassPathResource("fdfs_client.conf").getPath();
            //加载tracker配置信息
            ClientGlobal.init(filePath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /****
     * 文件上传
     * @param file : 要上传的文件信息封装->FastDFSFile
     * @return String[]
     *          1:文件上传所存储的组名
     *          2:文件存储路径
     */
    public static String[] upload(FastDFSFile file){
        //获取文件作者
       /* NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0]= new NameValuePair(file.getAuthor());*/

        /***
         * 文件上传后的返回值
         * uploadResults[0]:文件上传所存储的组名，例如:group1
         * uploadResults[1]:文件存储路径,例如：M00/00/00/wKjThF0DBzaAP23MAAXz2mMp9oM26.jpeg
         */
        try
        {
            //创建TrackerClient 客户端对象
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer,null);
            //参数1 字节数组
            //参数2 扩展名(不带点)
            //参数3 元数据( 文件的大小,文件的作者,文件的创建时间戳)

            //获取文件作者
            NameValuePair[] meta_list = new org.csource.common.NameValuePair[]{new org.csource.common.NameValuePair(file.getAuthor()), new NameValuePair(file.getName())};

            String[] strings = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
            return strings;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    /***
     * 文件下载
     * @param groupName:组名
     * @param remoteFileName：文件存储完整名
     * @return
     */
    public static InputStream downFile(String groupName, String remoteFileName) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //5.创建stroageserver 对象
            //6.创建storageclient 对象
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //7.根据组名 和 文件名 下载图片

            //参数1:指定组名
            //参数2 :指定远程的文件名
            byte[] bytes = storageClient.download_file(groupName, remoteFileName);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            return byteArrayInputStream;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //图片删除

    public static void deleteFile(String groupName, String remoteFileName) {
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //5.创建stroageserver 对象
            //6.创建storageclient 对象
            StorageClient storageClient = new StorageClient(trackerServer, null);
            int i = storageClient.delete_file(groupName, remoteFileName);
            if (i == 0) {
                System.out.println("删除成功");
            } else {
                System.out.println("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据组名获取组的信息

    public static StorageServer getStorages(String groupName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            //参数1 指定traqckerserver 对象
            //参数2 指定组名
            StorageServer group1 = trackerClient.getStoreStorage(trackerServer, groupName);
            return group1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //根据文件名和组名获取文件的信息

    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            StorageClient storageClient = new StorageClient(trackerServer, null);

            //参数1 指定组名
            //参数2 指定文件的路径
            FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //根据文件名和组名 获取组信息的数组信息

    /**
     * 获取Storage的IP和端口信息
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName){
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
            return group1s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    //获取tracker 的ip和端口的信息
    //http://192.168.211.132:8080
    public static String getTrackerUrl(){
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //tracker 的ip的信息
            String hostString = trackerServer.getInetSocketAddress().getHostString();

            //http://192.168.211.132:8080/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg img
            int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();
            return "http://" + hostString + ":" + g_tracker_http_port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
