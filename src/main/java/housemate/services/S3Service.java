/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import housemate.constants.Role;
import housemate.entities.Image;
import housemate.models.UploadDTO;
import housemate.repositories.ImageRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ThanhF
 */
@Service
public class S3Service {

    private final AmazonS3 s3client;

    @Value("${s3.bucket}")
    private String bucket;

    @Value("${application.domainCDN}")
    private String domainCDN;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    public S3Service(
            @Value("${s3.endpoint}") String endpoint,
            @Value("${s3.region}") String region,
            @Value("${s3.access-key}") String accessKey,
            @Value("${s3.secret-key}") String secretKey,
            @Value("${s3.bucket}") String bucket
    ) {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
                .build();
    }

    @Async
    public ResponseEntity<String> uploadImage(HttpServletRequest request, MultipartFile[] files, UploadDTO uploadDTO) {

        if(files.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Select at least one file to upload"); 
        }
        
        for (MultipartFile file : files) {
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only image support");
            }

            if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image size must not exceed 5MB");
            }

            long currentTimeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            String extensionImage = ".webp";
            String imageName = uploadDTO.getEntityId() + "." + uploadDTO.getImageType() + "." + currentTimeStamp + extensionImage;

            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucket, imageName);
            InitiateMultipartUploadResult initResult = s3client.initiateMultipartUpload(initRequest);

            String uploadId = initResult.getUploadId();

            List<PartETag> partETags = new ArrayList<>();

            long partSize = 2 * 1024 * 1024; // 2MB
            long fileSize = file.getSize();
            long totalParts = (long) Math.ceil((double) fileSize / partSize);
            try {
                for (int partNumber = 1; partNumber <= totalParts; partNumber++) {
                    long start = (partNumber - 1) * partSize;
                    long end = Math.min(start + partSize, fileSize);

                    UploadPartRequest uploadPartRequest = new UploadPartRequest()
                            .withBucketName(bucket)
                            .withKey(imageName)
                            .withUploadId(uploadId)
                            .withPartNumber(partNumber)
                            .withPartSize(end - start)
                            .withInputStream(file.getInputStream());

                    UploadPartResult uploadPartResult = s3client.uploadPart(uploadPartRequest);
                    partETags.add(uploadPartResult.getPartETag());
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not read file !");
            }

            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest()
                    .withBucketName(bucket)
                    .withKey(imageName)
                    .withUploadId(uploadId)
                    .withPartETags(partETags);

            s3client.completeMultipartUpload(completeRequest);

            Image image = new Image();
            image.setImageUrl(domainCDN + imageName);
            image.setEntityId(uploadDTO.getEntityId());
            image.setImageType(uploadDTO.getImageType());
            image.setUserId(authorizationUtil.getUserIdFromAuthorizationHeader(request));
            imageRepository.save(image);

        }

        return ResponseEntity.status(HttpStatus.OK).body("Upload success");
    }

    public ResponseEntity<String> removeImage(HttpServletRequest request, int entityId) {

        String userRole = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!userRole.equals(Role.ADMIN.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin role");
        }
        Image image = imageRepository.findById(entityId);
        if (image == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image not found");
        }

        imageRepository.deleteById(entityId);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted !");
    }
}
