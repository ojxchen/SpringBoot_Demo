package com.ojxchen.service;

import com.ojxchen.R.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FileUploadService {

    /**
     * 检查文件
     * @param md5
     * @param fileSize
     * @return
     * @throws Exception
     */
    R<?>  checkFile(String md5, long fileSize) throws Exception;

    /**
     * 上传文件
     * @param file
     * @param md5
     * @param chunkIndex
     * @param totalChunks
     * @param fileSize
     * @param fileName
     * @return
     * @throws Exception
     */
    R<?> uploadFile(MultipartFile file, String md5, int chunkIndex, int totalChunks, long fileSize, String fileName) throws Exception;

    /**
     * 下载文件
     * @param fileName
     * @param request
     * @param response
     * @throws Exception
     */
    void downloadFile(String fileName, HttpServletRequest request, HttpServletResponse response)throws Exception;

    /**
     * 查看文件列表
     * @return
     * @throws Exception
     */
    R<?> listFiles()throws Exception;

    /**
     * 删除文件
     * @param filename
     * @return
     * @throws Exception
     */
    R<?> deleteFile(String filename)throws Exception;
}
