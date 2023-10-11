/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.filters;

import housemate.mappers.JwtPayloadMapper;
import housemate.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author ThanhF
 */
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    JwtPayloadMapper jwtPayloadMapper;

    private final List<String> excludedUrls = Arrays.asList("/swagger-ui", "/auth", "/v3/api-docs", "/comment/services", "/payment/check");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {

        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        if (isUrlExcluded(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            setResponseUnAuthorized(response, "Token empty");
            return;
        }

        try {
            String token = authHeader.substring(7);
            Map<String, Object> payloadMap = jwtUtil.extractClaim(token, claims -> claims.get("payload", Map.class));
            if (jwtUtil.isTokenExpired(token)) {
                setResponseUnAuthorized(response, "Token expired");
                return;
            }
            if (jwtUtil.isTokenValid(token, jwtPayloadMapper.mapFromMap(payloadMap))) {
                setResponseUnAuthorized(response, "Token Invalid");
                return;
            }
        } catch (Exception e) { //phải là exception mới bắt lỗi unvalid signature được
            setResponseUnAuthorized(response, "Token Invalid");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isUrlExcluded(String url) {
        for (String excludedUrl : excludedUrls) {
            if (url.startsWith(excludedUrl)) {
                return true;
            }
        }
        return false;
    }

    private void setResponseUnAuthorized(HttpServletResponse response, String text) throws java.io.IOException {
        response.setContentType("text/plain");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(text);
    }
}
