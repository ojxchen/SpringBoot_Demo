package com.ojxchen.service.Impl;

import com.ojxchen.R.R;
import com.ojxchen.service.FileUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    // 文件上传目录
    private static final String UPLOAD_DIR = "E:\\upload\\";

    static {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    JedisPool jedisPool = new JedisPool();
    Jedis jedis = jedisPool.getResource();

    /**
     * 检查文件
     *
     * @param md5 文件的MD5值
     * @return 返回检查结果
     */
    @Override
    public R<?> checkFile(String md5, long fileSize) throws Exception {
        File uploadDir = new File(UPLOAD_DIR);
        File[] files = uploadDir.listFiles();
        long CHUNK_SIZE = 500 * 1024 * 1024;
        long uploadedFileSize = 0;
        boolean allChunksExist = true;
        int chunkIndex = 0;
        if (files != null) {
            for (File file : files) {
                String MD5 = jedis.get(file.getName());
                if (md5.equals(MD5) && fileSize == file.length()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("exists", true);
                    result.put("complete", true);
                    result.put("startByte", file.length());
                    return R.success(result);
                } else {
                    break;
                }
            }
            for (chunkIndex = 0; chunkIndex * CHUNK_SIZE < fileSize; chunkIndex++) {
                File partFile = new File(UPLOAD_DIR + md5 + ".part" + chunkIndex);
                if (partFile.exists()) {
                    uploadedFileSize += partFile.length();
                } else {
                    allChunksExist = false;
                    break;
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("exists", true);
            result.put("complete", allChunksExist && uploadedFileSize == fileSize);
            result.put("startByte", uploadedFileSize);
            return R.success(result);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("exists", false);
        result.put("complete", false);
        result.put("startByte", 0);
        return R.success(result);
    }

    /**
     * 上传文件
     *
     * @param file        分块数据
     * @param md5
     * @param chunkIndex  分块索引
     * @param totalChunks 总分块数
     * @param fileSize    文件大小
     * @param fileName    添加文件参数名
     * @return
     */
    @Override
    public R<?> uploadFile(MultipartFile file, String md5, int chunkIndex, int totalChunks, long fileSize, String fileName) {
        File tempFile = new File(UPLOAD_DIR + md5 + ".part" + chunkIndex);
        try {
            file.transferTo(tempFile);
            if (chunkIndex == totalChunks - 1) {
                File finalFile = new File(UPLOAD_DIR + fileName);
                try (FileOutputStream fos = new FileOutputStream(finalFile)) {
                    for (int i = 0; i < totalChunks; i++) {
                        File partFile = new File(UPLOAD_DIR + md5 + ".part" + i);
                        Files.copy(partFile.toPath(), fos);
                        partFile.delete(); // 合并后删除临时分块文件
                    }
                }
                if (finalFile.length() == fileSize) {
                    jedis.set(fileName, md5);
                    return R.success("文件上传成功");
                } else {
                    return R.failure("文件上传失败");
                }
            }

            return R.success("分块上传成功");
        } catch (IOException e) {
            return R.failure("文件上传失败");
        }
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    public void downloadFile(String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        File file = new File(UPLOAD_DIR + fileName);

        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        long contentLength = file.length();
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : contentLength - 1;

            long rangeLength = Math.min(contentLength - start, end - start + 1);

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + contentLength);
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(rangeLength));
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8080"); // 指定前端的源
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"); // 允许携带凭证
            response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Length, Content-Range");

            try (InputStream inputStream = new FileInputStream(file)) {
                inputStream.skip(start);
                byte[] data = new byte[(int) rangeLength];
                int bytesRead = inputStream.read(data);
                response.getOutputStream().write(data, 0, bytesRead);
            }
        } else {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8080"); // 指定前端的源
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"); // 允许携带凭证
            response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Length, Content-Range");

            try (InputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    /**
     * 获取文件列表
     *
     * @return 返回文件列表
     */
    @Override
    public R<?> listFiles() throws Exception {
        File uploadDir = new File(UPLOAD_DIR);
        File[] files = uploadDir.listFiles();
        List<Map<String, Object>> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                // 遍历文件列表，将文件信息存入Map中，再将Map添加到文件列表中
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("name", file.getName());
                fileData.put("size", file.length());
                fileData.put("path", file.getAbsolutePath());
                fileList.add(fileData);
            }
        }
        return R.success(fileList);
    }

    /**
     * 删除文件
     *
     * @param filename
     * @return
     * @throws Exception
     */
    @Override
    public R<?> deleteFile(String filename) throws Exception {
        File file = new File(UPLOAD_DIR + filename);
        if (file.exists()) {
            if (file.delete()) {
                jedis.del(filename);
                return R.success("文件删除成功");
            } else {
                return R.failure("文件删除失败");
            }
        } else {
            return R.nothing("没有找到文件");
        }
    }


}
