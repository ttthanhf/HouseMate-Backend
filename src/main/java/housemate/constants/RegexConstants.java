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
}
