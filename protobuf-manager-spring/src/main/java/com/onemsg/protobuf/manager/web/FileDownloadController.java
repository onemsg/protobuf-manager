package com.onemsg.protobuf.manager.web;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/files")
public class FileDownloadController {

    @GetMapping("/{filename}")
    public FileSystemResource downloadZipFile() {
        return null;
    }

}
