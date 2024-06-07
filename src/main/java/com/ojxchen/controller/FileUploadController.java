package com.ojxchen.controller;

import com.ojxchen.R.R;
import com.ojxchen.service.FileUploadService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;


    @ApiOperation("上传文件")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "上传失败"),
    })
    @PostMapping("/upload")
    @ResponseBody
    public R<?> uploadFile(@RequestParam("file") MultipartFile file,
                           @RequestParam("md5") String md5,
                           @RequestParam("chunkIndex") int chunkIndex,
                           @RequestParam("totalChunks") int totalChunks,
                           @RequestParam("fileSize") long fileSize,
                           @RequestParam("fileName") String fileName) throws Exception {
        return fileUploadService.uploadFile(file, md5, chunkIndex, totalChunks, fileSize, fileName);
    }

    @ApiOperation("检查文件")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功，返回data"),
    })
    @GetMapping("/check-file")
    @ResponseBody
    public R<?> checkFile(@RequestParam("md5") String md5, @RequestParam("fileSize") long fileSize) throws Exception {
        return fileUploadService.checkFile(md5, fileSize);
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/{fileName}")
    @ApiResponses({
            @ApiResponse(code = 206, message = "成功"),
    })
    public void downloadFile(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileUploadService.downloadFile(fileName, request, response);
    }

    @ApiOperation("查看文件列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功,返回data"),
    })
    @GetMapping("/files")
    @ResponseBody
    public R<?> listFiles() throws Exception {
        return fileUploadService.listFiles();
    }

    @ApiOperation("删除文件")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "删除失败"),
            @ApiResponse(code = 404, message = "找不到文件"),
    })
    @DeleteMapping("/delete/{filename}")
    @ResponseBody
    public R<?> deleteFile(@PathVariable String filename) throws Exception {
        return fileUploadService.deleteFile(filename);
    }
}

