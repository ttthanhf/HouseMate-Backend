/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import com.nimbusds.jose.shaded.gson.JsonObject;
import housemate.constants.PaymentMethod;
import housemate.constants.RegexConstants;
import housemate.entities.*;
import housemate.models.UserInfoOrderDTO;
import housemate.repositories.*;
import housemate.utils.AuthorizationUtil;
import housemate.utils.EncryptUtil;
import housemate.utils.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class PaymentService {

    private final String language = "en";
    private final String bankCode = "";

    @Autowired
    private AuthorizationUtil authorizationUtil;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PackageServiceItemRepository packageServiceItemRepository;
    @Autowired
    private UserUsageRepository userUsageRepository;

    // VNPAY
    @Value("${vnp.version}")
    private String vnp_Version;

    @Value("${vnp.pay_url}")
    private String vnp_PayUrl;

    @Value("${payment.return_url}")
    private String vnp_ReturnUrl;

    @Value("${vnp.api_url}")
    private String vnp_ApiUrl;

    @Value("${vnp.TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnp_IpAddr}")
    private String vnp_IpAddr;

    @Value("${vnp.secretKey}")
    private String vnp_SecretKey;

    // MoMo
    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String momoSecretKey;

    @Value("${payment.return_url}")
    private String redirectUrl;

    @Value("${payment.return_url}")
    private String ipnUrl;

    @Value("${momo.request-type}")
    private String requestType;

    @Value("${momo.api-url}")
    private String momoAPIUrl;

    public ResponseEntity<String> createPayment(HttpServletRequest request, UserInfoOrderDTO userInfoOrderDTO) throws UnsupportedEncodingException {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        UserAccount user = userRepository.findByUserId(userId);

        //if address null => set new address
        String address = userInfoOrderDTO.getAddress();
        if (user.getAddress() == null || user.getAddress().isBlank() || user.getAddress().isEmpty()) {
            user.setAddress(address);
        }

        //if phone null => set new phone
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank() || user.getPhoneNumber().isEmpty()) {
            String phone = userInfoOrderDTO.getPhone();
            user.setPhoneNumber(phone);
        }

        userRepository.save(user);

        Order order = orderRepository.getOrderNotCompleteByUserId(userId);

        //if user dont have order => can not pay
        if (order == null || order.getFinalPrice() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("This person does not have ordered before !");
        }

        // Get payment method
        PaymentMethod paymentMethod = userInfoOrderDTO.getPaymentMethod();

        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        orderRepository.save(order);

        long amount = order.getFinalPrice();

        if (paymentMethod.equals(PaymentMethod.VNPAY)) {
            return paymentWithVNPay(amount * 100);  //much to plus 100 => vnpay api faq
        }

        if (paymentMethod.equals(PaymentMethod.MOMO)) {
            return paymentWithMoMo(amount);
        }

        //check only support VNPAY and MoMo
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Only supports VNPAY and MoMo at the present time !");
    }

    private ResponseEntity<String> paymentWithVNPay(long amount) throws UnsupportedEncodingException {
        String vnp_TxnRef = RandomUtil.getRandomNumber(8);
        String vnp_Command = "pay";

        //create param for vnpay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        if (!bankCode.isBlank() || !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_Locale", language);
        vnp_Params.put("vnp_OrderType", "other"); //options, can remove
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = now.format(formatter);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);

        ZonedDateTime expireDate = now.plusMinutes(15);

        String vnp_ExpireDate = expireDate.format(formatter);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //encode all fields for export url
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();

        //create hash for checksum
        String vnp_SecureHash = EncryptUtil.hmacSHA512(vnp_SecretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        return ResponseEntity.status(HttpStatus.OK).body(paymentUrl);
    }

    private ResponseEntity<String> paymentWithMoMo(long amount) {
        // MoMo parameters
        String orderInfo = "HouseMate - Pay with MoMo";
        String extraData = "";
        String orderId = partnerCode + System.currentTimeMillis();

        // Create the raw data for the signature
        String rawData = generateRawHash(accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, orderId, requestType);

        // Calculate the HMAC SHA-256 signature
        String signature = EncryptUtil.hmacSHA256(momoSecretKey, rawData);

        // Create the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("accessKey", accessKey);
        requestBody.put("requestId", orderId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", redirectUrl);
        requestBody.put("ipnUrl", ipnUrl);
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);
        requestBody.put("lang", "en");

        // Set headers for JSON content
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HTTP entity for the request
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Use RestTemplate to send the POST request to MoMo
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(momoAPIUrl, requestEntity, Map.class);

        try {
            String payUrl = (String) response.getBody().get("payUrl");
            return ResponseEntity.status(HttpStatus.OK).body(payUrl);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong! Message: " + ex.getMessage());
        }
    }

    public ResponseEntity<?> checkVNPayPayment(HttpServletRequest request, String vnp_TxnRef, String vnp_TransactionDate) throws IOException {

        String vnp_RequestId = RandomUtil.getRandomNumber(8);
        String vnp_Command = "querydr";
        String vnp_OrderInfo = "Result: " + vnp_TxnRef;

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = now.format(formatter);

        //create param for vnpay
        JsonObject vnp_Params = new JsonObject();
        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        //create hash for checksum
        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef, vnp_TransactionDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = EncryptUtil.hmacSHA512(vnp_SecretKey, hash_Data);

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        //Send request for vnpay to get status payment info
        URL url = new URL(vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        //check response correct or not
        Pattern patternResponseCode = Pattern.compile(RegexConstants.PATTERN_RESPONSE_CODE_PAYMENT);
        Matcher matcherResponseCode = patternResponseCode.matcher(response.toString());
        Pattern patternResponseMessage = Pattern.compile(RegexConstants.PATTERN_RESPONSE_MASSAGE_PAYMENT);
        Matcher matcherResponseMessage = patternResponseMessage.matcher(response.toString());

        if (!matcherResponseCode.find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't find RsCode");
        }
        String responseCode = matcherResponseCode.group(1);

        if (!matcherResponseMessage.find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't find RsMsg");
        }
        String responseMessage = matcherResponseMessage.group(1);

        if (!"00".equals(responseCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        }

        //check if payment order is already done or not exist
        Pattern patternTransactionStatus = Pattern.compile(RegexConstants.PATTERN_TRANSACTION_STATUS_PAYMENT);
        Matcher matcherTransactionStatus = patternTransactionStatus.matcher(response.toString());
        if (!matcherTransactionStatus.find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't find TransactionStatus");
        }
        String transactionStatus = matcherTransactionStatus.group(1);
        if (!"00".equals(transactionStatus)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment fail");
        }

        return processToDatabase(request, vnp_TxnRef, vnp_TransactionDate);
    }

    public ResponseEntity<?> checkMoMoPayment(
            HttpServletRequest request,
            String partnerCode,
            String orderId,
            String requestId,
            long amount,
            String orderInfo,
            String orderType,
            String extraData,
            String momoSignature,
            String message,
            String payType,
            int resultCode,
            long transId,
            long responseTime
    ) {
        // Create the raw data for the signature
        String rawData = generateRawHash(accessKey, amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId, responseTime, resultCode, transId);

        // Validate signature
        String partnerSignature = EncryptUtil.hmacSHA256(momoSecretKey, rawData);
        if (!momoSignature.equals(partnerSignature)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request invalid, this transaction could be hacked!");
        }

        // Payment fail
        if (resultCode != 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment fail. Message: " + message);
        }

        // Payment success
        String transactionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(responseTime));
        return processToDatabase(request, String.valueOf(transId), transactionDate);
    }

    public ResponseEntity<?> processToDatabase(HttpServletRequest request, String transactionId, String transactionDate) {
        //remove all cart exist in order and set complete order to true
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        UserAccount user = userRepository.findByUserId(userId);

        //set each order Item
        Order order = orderRepository.getOrderNotCompleteByUserId(userId);
        List<OrderItem> listOrderItem = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
        for (OrderItem orderItem : listOrderItem) {
            Service service = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
            orderItem.setService(service);
            serviceRepository.updateNumberOfSoldByServiceId(orderItem.getServiceId(), orderItem.getQuantity());
            cartRepository.deleteCartByUserIdAndServiceId(userId, orderItem.getServiceId());

            //user usage
            if (service.isPackage()) {
                List<PackageServiceItem> listPackageServiceItem = packageServiceItemRepository.findAllSingleServiceIdByPackageServiceId(service.getServiceId());
                for (PackageServiceItem packageServiceItem : listPackageServiceItem) {
                    UserUsage userUsage = new UserUsage();

                    userUsage.setUserId(userId);
                    userUsage.setServiceId(packageServiceItem.getSingleServiceId());
                    userUsage.setRemaining(packageServiceItem.getQuantity() * orderItem.getQuantity());
                    userUsage.setTotal(packageServiceItem.getQuantity() * orderItem.getQuantity());

                    userUsage.setStartDate(order.getDate());
                    userUsage.setEndDate(orderItem.getExpireDate());

                    userUsage.setOrderItemId(orderItem.getOrderItemId());
                    userUsageRepository.save(userUsage);
                }

            } else {
                UserUsage userUsage = new UserUsage();
                userUsage.setUserId(userId);
                userUsage.setServiceId(service.getServiceId());
                userUsage.setRemaining(orderItem.getQuantity());
                userUsage.setTotal(orderItem.getQuantity());
                userUsage.setStartDate(order.getDate());
                userUsage.setEndDate(orderItem.getExpireDate());
                userUsage.setOrderItemId(orderItem.getOrderItemId());
                userUsageRepository.save(userUsage);
            }
        }

        //add all missing field in db
        order.setComplete(true);
        order.setDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        order.setTransactionId(transactionId);
        order.setTransactionDate(transactionDate);
        orderRepository.save(order);

        //add all missing field in response view
        order.setDiscountPrice(order.getSubTotal() - order.getFinalPrice());
        order.setListOrderItem(listOrderItem);
        order.setUser(user);

        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    public static String generateRawHash(String accessKey, long amount, String extraData, String ipnUrl,
                                        String orderId, String orderInfo, String partnerCode,
                                        String redirectUrl, String requestId, String requestType) {
        return "accessKey=" + accessKey + "&amount=" + amount + "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode + "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId + "&requestType=" + requestType;
    }

    public String generateRawHash(String accessKey, long amount, String extraData, String message, String orderId,
                                  String orderInfo, String orderType, String partnerCode, String payType,
                                  String requestId, long responseTime, int resultCode, long transId) {

        return "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&message=" + message +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&orderType=" + orderType +
                "&partnerCode=" + partnerCode +
                "&payType=" + payType +
                "&requestId=" + requestId +
                "&responseTime=" + responseTime +
                "&resultCode=" + resultCode +
                "&transId=" + transId;
    }
}
