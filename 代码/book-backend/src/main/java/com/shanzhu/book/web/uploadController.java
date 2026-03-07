package com.shanzhu.book.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/upload")
public class uploadController {

    @RequestMapping("/uploadImg")
    @ResponseBody
    public Map<String, Object> uploadImg(HttpServletRequest req) {
        String resPath = upload(req);
        Map<String, Object> res = new HashMap<>();
        res.put("code", 0);
        res.put("data", resPath);
        return res;
    }

    private String upload(HttpServletRequest req) {
        String res = null;
        try {
            // 【终极方案】：直接写死你的电脑绝对路径（注意 Java 中路径要用正斜杠 / ）
            String staticDir = "E:/java/project/校园旧书漂流共享系统/代码/book-backend/src/main/resources/static/files/";
            File resDirFile = new File(staticDir);
            if (!resDirFile.exists()) {
                resDirFile.mkdirs();
            }

            if (ServletFileUpload.isMultipartContent(req)) {
                FileItemFactory fileItemFactory = new DiskFileItemFactory();
                ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
                List<FileItem> list = servletFileUpload.parseRequest(new ServletRequestContext(req));
                for (FileItem fileItem : list) {
                    if (!fileItem.isFormField()) {
                        String newFileName = System.currentTimeMillis() + "_" + fileItem.getName();
                        File file = new File(staticDir + newFileName);
                        fileItem.write(file);
                        // 返回给前端的相对地址
                        res = "/files/" + newFileName;
                    }
                }
            }
        } catch (Exception e) {
            log.error("文件上传失败！", e);
        }
        return res;
    }
}