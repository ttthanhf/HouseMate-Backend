/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import java.util.HashMap;
import java.util.Map;
import lombok.*;

/**
 *
 * @author ThanhF
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class JwtPayload {

    private int id;
    private String fullName;
    private String email;
    private String role;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("payload", this);
        return map;
    }
    
    public JwtPayload fromUserAccount(UserAccount account) {
        JwtPayload jwtPayload = new JwtPayload();

        jwtPayload.setId(account.getUserId());
        jwtPayload.setEmail(account.getEmailAddress());
        jwtPayload.setFullName(account.getFullName());
        jwtPayload.setRole(account.getRole().toString());
        
        return jwtPayload;
    }
}
