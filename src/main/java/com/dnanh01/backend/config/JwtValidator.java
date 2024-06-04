package com.dnanh01.backend.config;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lớp này mở rộng OncePerRequestFilter để thực hiện xác thực JWT
 * trên các yêu cầu đến. Nó trích xuất mã thông báo JWT từ tiêu đề yêu cầu,
 * xác thực chữ ký và cấu trúc của nó, trích xuất các xác nhận quyền sở hữu và thiết lập
 * xác thực trong Spring Security SecurityContextHolder. Nếu mã thông báo là
 * không hợp lệ, nó sẽ ném BadCredentialsException. Bộ lọc sau đó tiếp tục quá trình
 * xử lý yêu cầu bằng cách gọi bộ lọc tiếp theo trong chuỗi.
 */
public class JwtValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
    	// Trích xuất mã thông báo JWT từ tiêu đề yêu cầu
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);
        // Kiểm tra xem mã thông báo JWT có tồn tại không
        if (jwt != null) {
            // Xóa tiền tố "Bearer" khỏi mã thông báo JWT
            jwt = jwt.substring(7);

            try {
            	// Tạo khóa bí mật bằng thuật toán HMAC SHA với khóa bí mật từ
                // JwtConstant
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                // Phân tích mã thông báo JWT, xác thực chữ ký của nó và truy xuất các xác nhận quyền sở hữu
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
                // Trích xuất email và các khiếu nại của chính quyền từ các khiếu nại của JWT
                String email = String.valueOf(claims.get("email"));
                String authorties = String.valueOf(claims.get("authories"));
                // Chuyển đổi yêu cầu của cơ quan có thẩm quyền thành danh sách các đối tượng GrantedAuthority
                List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorties);
             	// Tạo đối tượng xác thực bằng email và cơ quan có thẩm quyền, đặt nó vào
                // Bối cảnh bảo mật
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auths);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
            	// Ném BadCredentialsException nếu có vấn đề với mã thông báo
                throw new BadCredentialsException("Invalid token ... from jwt validator");
            }
        }
     // Tiếp tục chuỗi lọc
        filterChain.doFilter(request, response);
    }
}
