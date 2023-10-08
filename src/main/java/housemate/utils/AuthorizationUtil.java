/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.utils;

import housemate.constants.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class AuthorizationUtil {

    @Autowired
    private JwtUtil jwtUtil;

    public int getUserIdFromAuthorizationHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return (int) jwtUtil.extractPayload(token).get("id");
    }

    public String getRoleFromAuthorizationHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return (String) jwtUtil.extractPayload(token).get("role");
    }

}
