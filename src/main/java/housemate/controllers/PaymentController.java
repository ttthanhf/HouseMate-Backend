/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.services.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/payment")
@CrossOrigin
@Tag(name = "Payment")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @GetMapping("/create")
    public ResponseEntity<String> createPayment(HttpServletRequest request) throws UnsupportedEncodingException {
        return paymentService.createVNPayPayment(request);
    }

    @GetMapping("/check")
    public ResponseEntity<Object> checkPayment(@RequestParam String vnp_TxnRef, @RequestParam String vnp_TransactionNo, @RequestParam String vnp_PayDate) throws IOException {
        return paymentService.checkVNPayPayment(vnp_TxnRef, vnp_TransactionNo, vnp_PayDate);
    }
}
