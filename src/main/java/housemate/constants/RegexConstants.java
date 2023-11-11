/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.constants;

/**
 *
 * @author Admin
 */
public interface RegexConstants {

    String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$";
    String PHONE_NUMBER_REGEX = "^(0|\\+?84)(3|5|7|8|9)[0-9]{8}$";

    String REPLY_COMMENT_REGEX = "/comment/\\d+/reply";

    String PATTERN_RESPONSE_CODE_PAYMENT = "\"vnp_ResponseCode\":\"(\\d{2})\"";
    String PATTERN_RESPONSE_MASSAGE_PAYMENT = "\"vnp_Message\":\"([^\"]*)\"";
    String PATTERN_TRANSACTION_STATUS_PAYMENT = "\"vnp_TransactionStatus\":\"(\\d{2})\"";

    String IDENTITY_CARD = "^(0[0-9]{2})([0-9]{1})([0-9]{2})([0-9]{6})$";
}
