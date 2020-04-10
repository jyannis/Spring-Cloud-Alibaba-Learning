package com.jyannis.serviceb;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class TestController {

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public String upload(@RequestPart("file") MultipartFile file){
        return file.getOriginalFilename();
    }

}
