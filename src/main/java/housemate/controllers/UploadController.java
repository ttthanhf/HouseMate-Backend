package housemate.controllers;

import housemate.services.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/service/{serviceId}")
    public ResponseEntity<String> uploadServiceImage(HttpServletRequest request, @RequestParam("file") MultipartFile[] files, @PathVariable int serviceId) {
        return s3Service.uploadImage(request, files, serviceId, false);
    }

    @PostMapping("/task/{taskId}")
    public ResponseEntity<String> uploadTaskImage(HttpServletRequest request, @RequestParam("file") MultipartFile[] files, @PathVariable int taskId) {
        return s3Service.uploadImage(request, files, taskId, true);
    }

    @DeleteMapping("/detele/{imageId}")
    public ResponseEntity<String> removeImage(HttpServletRequest request, @PathVariable int imageId) {
        return s3Service.removeImage(request, imageId);
    }
}
