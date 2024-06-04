package com.dnanh01.backend.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtProvider {
	// Tạo khóa bí mật bằng thuật toán HMAC SHA với khóa bí mật từ
    // JwtConstant
    SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    // Phương thức tạo mã thông báo JWT dựa trên đối tượng Xác thực được cung cấp
    public String generateToken(Authentication auth) {
    	// Xây dựng mã thông báo JWT với ngày phát hành, ngày hết hạn và yêu cầu gửi email từ
        // đối tượng xác thực
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86000000))
                .claim("email", auth.getName())
                .signWith(key).compact();
        System.out.println();
        return jwt;
    }

    // Phương pháp trích xuất xác nhận quyền sở hữu email từ mã thông báo JWT nhất định
    public String getEmailFromToken(String jwt) {
        jwt = jwt.substring(7);// Remove the "Bearer " prefix from the JWT string
        // Phân tích mã thông báo JWT, xác thực chữ ký của nó và truy xuất các xác nhận quyền sở hữu
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        // Trích xuất và trả lại email xác nhận quyền sở hữu từ JWT
        String email = String.valueOf(claims.get("email"));
        return email;
    }
}
