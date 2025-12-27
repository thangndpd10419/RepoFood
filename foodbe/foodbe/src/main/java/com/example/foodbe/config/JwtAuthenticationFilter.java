package com.example.foodbe.config;

import com.example.foodbe.utils.ConstantUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Lấy token từ header Authorization
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith(ConstantUtils.BEARER)) {
                String token = authHeader.substring(7);

                // 2. Validate token trước khi lấy username
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);

                    // 3. Nếu token hợp lệ và chưa xác thực trước đó
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 4. Tạo một Authentication với thông tin lấy từ token
                        // Bạn có thể lấy roles trực tiếp từ token mà không cần phải load lại UserDetails từ DB
                        List<String> roles = jwtUtil.getRolesFromToken(token);  // lấy quyền từ token

                        // Tạo Authentication từ token và gắn vào SecurityContext
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        username,  // username từ token
                                        null,  // Password không cần thiết, JWT đã xác thực rồi
                                        roles.stream().map(r->new SimpleGrantedAuthority(r)).collect(Collectors.toList())  // chuyển roles thành GrantedAuthority
                                );
                        authToken.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                                .buildDetails(request)); // thêm các dữ liệu khác: như địa chỉ id,...

                        // Gắn authentication vào SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception ex) {
            // Log lỗi hoặc xử lý token không hợp lệ
            System.err.println("Cannot set user authentication: " + ex.getMessage());
        }

        // 5. Cho request đi tiếp
        filterChain.doFilter(request, response);
    }
}

