package housemate.controllers;

import housemate.services.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
@Tag(name = "Upload")
public class UploadController {

    @Autowired
    S3Service s3Service;

    @PostMapping()
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        s3Service.uploadFile(file);
        return "Uploaded";
    }
}
