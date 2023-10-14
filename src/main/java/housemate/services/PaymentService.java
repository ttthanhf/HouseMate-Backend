/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import com.nimbusds.jose.shaded.gson.JsonObject;
import housemate.entities.Order;
import housemate.entities.OrderItem;
import housemate.models.UserInfoOrderDTO;
import housemate.repositories.CartRepository;
import housemate.repositories.OrderItemRepository;
import housemate.utils.EncryptUtil;
import housemate.repositories.OrderRepository;
import housemate.utils.AuthorizationUtil;
import housemate.utils.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class PaymentService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    private final String language = "en";
    private final String vnp_IpAddr = "127.0.0.1";

    @Value("${vnp.version}")
    private String vnp_Version;

    @Value("${vnp.pay_url}")
    private String vnp_PayUrl;

    @Value("${vnp.return_url}")
    private String vnp_ReturnUrl;

    @Value("${vnp.api_url}")
    private String vnp_ApiUrl;

    @Value("${vnp.TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnp.secretKey}")
    private String secretKey;

    public ResponseEntity<String> createVNPayPayment(HttpServletRequest request, UserInfoOrderDTO userInfoOrderDTO) throws UnsupportedEncodingException {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        Order order = orderRepository.getOrderNotCompleteByUserId(userId);

        //if user dont have order => can not pay
        if (order == null || order.getTotalPrice() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User dont have order");
        }

        order.setAddress(userInfoOrderDTO.getAddress());
        order.setEmail(userInfoOrderDTO.getEmail());
        order.setPhone(userInfoOrderDTO.getPhone());
        order.setFullName(userInfoOrderDTO.getFullName());
        orderRepository.save(order);

        long amount = order.getTotalPrice() * 100;

        String vnp_TxnRef = RandomUtil.getRandomNumber(8);
        String vnp_Command = "pay";

        //create param for vnpay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_Locale", language);
        vnp_Params.put("vnp_OrderType", "other"); //options, can remove
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //encode all fields for export url
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();

        //create hash for checksum
        String vnp_SecureHash = EncryptUtil.hmacSHA512(secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        return ResponseEntity.status(HttpStatus.OK).body(paymentUrl);
    }

    public ResponseEntity<String> checkVNPayPayment(HttpServletRequest request, String vnp_TxnRef, String vnp_TransactionNo, String vnp_TransactionDate) throws IOException {

        String vnp_RequestId = RandomUtil.getRandomNumber(8);
        String vnp_Command = "querydr";
        String vnp_OrderInfo = "Result: " + vnp_TxnRef;

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        //create param for vnpay
        JsonObject vnp_Params = new JsonObject();
        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        //create hash for checksum
        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef, vnp_TransactionDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = EncryptUtil.hmacSHA512(secretKey, hash_Data);

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

        //check response
        Pattern patternResponseCode = Pattern.compile("\"vnp_ResponseCode\":\"(\\d{2})\"");
        Matcher matcherResponseCode = patternResponseCode.matcher(response.toString());

        if (!matcherResponseCode.find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't find RsCode");
        }
        String responseCode = matcherResponseCode.group(1);
        if (!"00".equals(responseCode)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Payment fail");
        }

        //remove all cart exist in order and set complete order to true
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Order order = orderRepository.getOrderNotCompleteByUserId(userId);
        List<OrderItem> listOrderItem = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
        for (OrderItem orderItem : listOrderItem) {
            cartRepository.deleteCartByUserIdAndServiceId(userId, orderItem.getServiceId());
        }
        order.setComplete(true);
        orderRepository.save(order);

        return ResponseEntity.status(HttpStatus.OK).body("Payment sucess");
    }
}
