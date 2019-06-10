package com.smile.myUtil.util;


import com.smile.myUtil.common.ResponseMessage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FTPUtil {

    private String host = "47.101.176.104";
    private int port = 21;
    private String ftpUser = "ftpuser";
    private String ftpPasswd = "123456";
    private FTPClient ftpClient;

    public FTPUtil(String host, int port, String ftpUser, String ftpPasswd) {
        this.host = host;
        this.port = port;
        this.ftpUser = ftpUser;
        this.ftpPasswd = ftpPasswd;
    }

    public FTPUtil() {}

    public boolean connectServer(){
        boolean isSuccess = true;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(this.host);
            isSuccess = ftpClient.login(this.ftpUser, this.ftpPasswd);
        } catch (IOException e) {
            System.out.println("连接失败！");
            e.printStackTrace();
        }
        return isSuccess;
    }

    public boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean isSuccess = true;
        FileInputStream fis = null;
        if(connectServer()){
            try {
                boolean hasPath = ftpClient.changeWorkingDirectory(remotePath); //设置上传路径
                if(!hasPath){
                    ftpClient.makeDirectory(remotePath);//创建上传的路径  该方法只能创建一级目录
                    ftpClient.changeWorkingDirectory(remotePath);
                }
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }
            } catch (IOException e) {
                System.out.println("上传文件异常");
                isSuccess = false;
                e.printStackTrace();
            }finally {
                if(fis != null)fis.close();
                ftpClient.disconnect();
            }
        }
        return isSuccess;
    }

    public List<File> getFiles(){
        //List<File> fileList = Lists.newArrayList();
        List<File> fileList = new ArrayList<>();
        File file = new File("upload.txt");
        fileList.add(file);
        return fileList;
    }

    public ResponseMessage downloadFile(String localPath, String remotePath, String fileName) throws IOException {

        boolean isDown = true;
        FileOutputStream fos = null;
        if(connectServer()){
            try {
                ftpClient.login(this.ftpUser, this.ftpPasswd);
                boolean hasPath = ftpClient.changeWorkingDirectory(remotePath);
                if(!hasPath){
                    return ResponseMessage.createErrorMessage("目标路径不存在！");
                }
                int reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    return ResponseMessage.createErrorMessage("ftp服务器不响应！");
                }
                ftpClient.enterLocalPassiveMode();
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setBufferSize(10240);

                for(FTPFile ftpFile : ftpClient.listFiles()){
                    System.out.println(ftpFile.getName());
                }


                File localFile = new File(localPath + File.separatorChar + fileName);
                fos = new FileOutputStream(localFile);

                isDown = ftpClient.retrieveFile(fileName, fos);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseMessage.createErrorMessage("下载文件失败！");
            }finally {
                if(fos != null)fos.close();
                ftpClient.disconnect();
            }
        }
        return ResponseMessage.createErrorMessage("下载文件成功");

    }

    public static void main(String[] args) throws IOException {
        FTPUtil  ftpUtil = new FTPUtil();
//        System.out.println(ftpUtil.connectServer());
//        boolean isUpload = ftpUtil.uploadFile("image", ftpUtil.getFiles());
//        if(isUpload) System.out.println("上传ftp成功 ！");
        ResponseMessage messageResponse = ftpUtil.downloadFile("C:\\Users\\Smile\\Desktop\\image", "image", "2.jpg");
        System.out.println();
    }


}

