/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.UserInfoOrderDTO;
import housemate.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    @Operation(summary = "Create payment and response URL for VNPAY and MoMo")
    public ResponseEntity<String> createPayment(HttpServletRequest request, @Valid @RequestBody UserInfoOrderDTO userInfoOrderDTO) throws UnsupportedEncodingException {
        return paymentService.createPayment(request, userInfoOrderDTO);
    }

    @GetMapping("/check/vnpay")
    @Operation(summary = "Check VNPAY payment success or not")
    public ResponseEntity<?> checkPayment(HttpServletRequest request, @RequestParam String vnp_TxnRef, @RequestParam String vnp_PayDate) throws IOException {
        return paymentService.checkVNPayPayment(request, vnp_TxnRef, vnp_PayDate);
    }

    @GetMapping("/check/momo")
    @Operation(summary = "Check MoMo payment success or not")
    public ResponseEntity<?> checkMoMoPayment(
            HttpServletRequest request,
            @RequestParam String partnerCode,
            @RequestParam String orderId,
            @RequestParam String requestId,
            @RequestParam long amount,
            @RequestParam String orderInfo,
            @RequestParam String orderType,
            @RequestParam String extraData,
            @RequestParam String signature,
            @RequestParam int resultCode,
            @RequestParam String message,
            @RequestParam String payType,
            @RequestParam long transId,
            @RequestParam long responseTime

    ) throws IOException {
        return paymentService.checkMoMoPayment(request, partnerCode, orderId, requestId, amount, orderInfo, orderType, extraData, signature, message, payType, resultCode, transId, responseTime);
    }
}
