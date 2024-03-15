package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.Code2Path;
import love.vincentcorleone.easypan.entity.po.LargeFile;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.FileVo;
import love.vincentcorleone.easypan.exception.HttpStatusEnum;
import love.vincentcorleone.easypan.exception.ResponseResult;
import love.vincentcorleone.easypan.service.FileService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static love.vincentcorleone.easypan.exception.ResponseResult.success;
import static love.vincentcorleone.easypan.util.FileUtils.initUserAttachmentDir;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseResult<Object> upload(HttpSession session, @RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "chunkIndex", required = false) Integer chunkIndex,
                                         @RequestParam(value = "chunks", required = false) Integer chunks,
                                         @RequestParam(value = "fileName", required = false) String fileName,
                                         @RequestParam(value = "currentPath") String currentPath,
                                         @RequestParam(value = "md5", required = false) String md5){
        User user =  (User)session.getAttribute(Constants.LOGIN_USER_KEY);
        if(chunkIndex == null){
            if(Objects.requireNonNull(file.getOriginalFilename()).contains(" ")){
                throw new RuntimeException("文件名中不能包含空格");
            }
            fileService.upload(user,currentPath, file);
            return success("文件上传成功");
        }else{
            if(fileName.contains(" ")){
                throw new RuntimeException("文件名中不能包含空格");
            }
            boolean uploaded = fileService.checkMd5(user, md5, currentPath, fileName);
            if (uploaded){
                return success(HttpStatusEnum.MD5_UPLOADED);
            }
            boolean finished = fileService.uploadByChunks(user,currentPath, file, chunkIndex, chunks, fileName, md5);
            if (finished){
                return success("文件上传成功");
            }else{
                return success( 203,String.format("第%s/%s个文件分片上传成功，请继续上传下一个文件分片",chunkIndex+1,chunks));
            }
        }
    }

    @PostMapping("/newFolder")
    public ResponseResult<Object> newFolder(HttpSession session, @RequestParam("currentPath") String currentPath, @RequestParam("folderName") String folderName){
        if(folderName.contains(" ")){
            throw new RuntimeException("文件夹名中不能包含空格");
        }
        String nickName =  ((User)session.getAttribute(Constants.LOGIN_USER_KEY)).getNickName();
        fileService.newFolder(nickName, currentPath, folderName);
        return ResponseResult.success("新建文件夹成功");
    }

    @GetMapping("/loadFiles")
    public ResponseResult<List<FileVo>> loadFiles(HttpSession session, @RequestParam("currentPath") String currentPath){
        User user =  (User)session.getAttribute(Constants.LOGIN_USER_KEY);
        List<FileVo> result = fileService.loadFiles(user, currentPath);
        return ResponseResult.success(result);
    }

    /**
     *
     * @param session
     * @param currentPath 以'/'开头和结尾
     * @param fileName
     * @return
     */
    @GetMapping("/createDownloadCode")
    public ResponseResult<Map<String, String>> createDownloadCode(HttpSession session, @RequestParam("currentPath") String currentPath, @RequestParam("fileName") String fileName){
        User user =  (User)session.getAttribute(Constants.LOGIN_USER_KEY);
        String code = fileService.createDownloadCode(user, currentPath, fileName);
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        return ResponseResult.success(map);
    }

    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletResponse response, @RequestParam("code") String code){
        Code2Path code2Path  = fileService.downloadFile(code);
        String filePath = code2Path.getPath();
        String fileName;
        if(code2Path.getFileName()==null){
            fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        }else{
            fileName = code2Path.getFileName();
        }


        File file = null;
        FileInputStream is = null;


        try {
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"");
            file = new File(filePath);
            is = new FileInputStream(file);
            ServletOutputStream os = response.getOutputStream();
            IOUtils.copy(is, os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException("文件流关闭失败");
                }
            }
        }

    }

    @GetMapping("/previewVideo/**")
    public void previewFile(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        User user =  (User)session.getAttribute(Constants.LOGIN_USER_KEY);
        String uri = request.getRequestURL().toString();

        String keyword = "previewVideo";

        String uriRight = uri.substring(uri.indexOf(keyword) + keyword.length());

        int tmp = uriRight.lastIndexOf("/");
        String attachment = uriRight.substring(tmp);
        String relativeFilePath = uriRight.substring(0,tmp);

        tmp = relativeFilePath.lastIndexOf("/");
        String fileName = relativeFilePath.substring(tmp+1);
        String currentPath = relativeFilePath.substring(0,tmp);

        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        String finalPath = null;
        if (Constants.videos.contains(suffix)) {
            String basePath = initUserAttachmentDir(user.getNickName());
            String absoluteFilePath = basePath + relativeFilePath;

            File file = new File(absoluteFilePath);
            LargeFile largeFile = fileService.getLargeFileBy3(user, currentPath, fileName);


            if (file.exists()) {
                //要下载的文件是私有的且小于10m
                //要下载的文件是私有的且大于10m
                finalPath = absoluteFilePath;
            } else if (largeFile != null && largeFile.isPublic()) {
                //要下载的文件是公有的
                finalPath = largeFile.getDiskPath();
            } else {
                //异常
                throw new RuntimeException("要下载的文件不存在");
            }

            finalPath = finalPath + attachment;
            readFile(response, finalPath);
        }
    }

    private void readFile(HttpServletResponse response, String relativeFilePath){
        FileInputStream in = null;
        ServletOutputStream out = null;
        try{
            File file = new File(relativeFilePath);
            if(!file.exists()){
                return;
            }
            in = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(bytes)) != -1){
                out.write(bytes,0,len);
            }
            out.flush();


        } catch (IOException e) {
            throw new RuntimeException("读取文件异常",e);
        } finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
