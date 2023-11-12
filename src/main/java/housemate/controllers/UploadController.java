package housemate.controllers;

import housemate.models.UploadDTO;
import housemate.services.UploadService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
@SecurityRequirement(name = "bearerAuth")
public class UploadController {

    @Autowired
    UploadService s3Service;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(HttpServletRequest request, @RequestPart("file") MultipartFile[] files, @Valid @ModelAttribute UploadDTO uploadDTO) {
        return s3Service.uploadImage(request, files, uploadDTO);
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<String> removeImage(HttpServletRequest request, @PathVariable int imageId) {
        return s3Service.removeImage(request, imageId);
    }
}
