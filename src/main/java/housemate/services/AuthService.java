/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.JwtPayload;
import housemate.entities.UserAccount;
import housemate.mappers.AccountMapper;
import housemate.mappers.JwtPayloadMapper;
import housemate.models.AccountDTO;
import housemate.repositories.UserRepository;
import housemate.utils.BcryptUtil;
import housemate.utils.JwtUtil;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author hdang09
 */
@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BcryptUtil bcryptUtil;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${url.client}")
    private String URL_CLIENT;

    public ResponseEntity<String> login(AccountDTO.Login loginAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(loginAccountDTO.getEmail());

        // Check email not in database
        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email haven't been created");
        }

        // Check correct password
        boolean isCorrect = bcryptUtil.checkpw(loginAccountDTO.getPassword(), accountDB.getPasswordHash());
        if (!isCorrect) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password not correct");
        }

        // Generate token
        JwtPayload jwtPayload = new JwtPayloadMapper().mapFromUserAccount(accountDB);
        Map<String, Object> payload = jwtPayload.toMap();
        String token = new JwtUtil().generateToken(payload);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    public ResponseEntity<String> register(AccountDTO.Register registerAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(registerAccountDTO.getEmail());

        // Check email exists database
        if (accountDB != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email have been created before");
        }

        // Insert to database
        UserAccount userAccount = accountMapper.mapToEntity(registerAccountDTO);
        userAccount = userRepository.save(userAccount);

        // Generate token
        JwtPayload jwtPayload = new JwtPayloadMapper().mapFromUserAccount(userAccount);
        Map<String, Object> payload = jwtPayload.toMap();
        String token = new JwtUtil().generateToken(payload);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    private void sendEmail(String recipientEmail, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("smtp.housemate@gmail.com", "HouseMate Security");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + resetPasswordLink + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 30);
    }

    public ResponseEntity<String> forgotPassword(String email) {
        String token = generateRandomString();
        try {
            // Check account in database
            UserAccount account = userRepository.findByEmailAddress(email);
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find this email!");
            }

            // Store reset_password_token to account
            account.setResetPasswordToken(token);
            userRepository.save(account);

            // Send email
            String resetPasswordLink = URL_CLIENT + "/set-password?token=" + token;
            sendEmail(email, resetPasswordLink);
            return ResponseEntity.status(HttpStatus.OK).body("We have sent a reset password link to your email. Please check.");
        } catch (UnsupportedEncodingException | MessagingException e) {
            return ResponseEntity.status(HttpStatus.OK).body("Error while sending email");
        }
    }

    public ResponseEntity<String> resetPassword(String token, String password) {
        UserAccount account = userRepository.findByResetPasswordToken(token);

        // Check email not in database
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token is invalid");
        }

        // Set new password
        String hash = bcryptUtil.hashPassword(password);
        account.setPasswordHash(hash);
        account.setResetPasswordToken(null);
        userRepository.save(account);
        return ResponseEntity.status(HttpStatus.OK).body("Set new password successfully!");
    }
}
