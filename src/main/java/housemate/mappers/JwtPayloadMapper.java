/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.entities.JwtPayload;
import housemate.entities.UserAccount;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class JwtPayloadMapper {

    public JwtPayload mapFromUserAccount(UserAccount account) {
        JwtPayload jwtPayload = new JwtPayload();

        jwtPayload.setId(account.getUserId());
        jwtPayload.setEmail(account.getEmailAddress());
        jwtPayload.setFullName(account.getFullName());
        jwtPayload.setRole(account.getRole().toString());

        return jwtPayload;
    }

    public JwtPayload mapFromMap(Map<String, Object> jwtPayloadMap) {
        JwtPayload jwtPayload = new JwtPayload();

        jwtPayload.setId((int) jwtPayloadMap.get("id"));
        jwtPayload.setEmail((String) jwtPayloadMap.get("email"));
        jwtPayload.setFullName((String) jwtPayloadMap.get("fullName"));
        jwtPayload.setRole((String) jwtPayloadMap.get("role"));

        return jwtPayload;
    }
}
