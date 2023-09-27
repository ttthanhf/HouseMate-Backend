/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author hdang09
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterAccountDTO {
//   @Pattern(
//           regexp = "/^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$/", 
//           message = "Please enter a valid email"
//   )
    private String email;
    
//   @Min(value = 2)
//   @Max(value = 50)
    private String fullName;
    
//   @Pattern(
//           regexp = "/^(0|\\+?84)(3|5|7|8|9)[0-9]{8}$/", 
//           message = "Please enter a valid phone number."
//   )
    private int phoneNumber;
    
//   @Pattern(
//           regexp = "/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$/", 
//           message = "Must be 8 to 16 characters, include a number, an uppercase letter, and a lowercase letter"
//   )
    private String password;
}
