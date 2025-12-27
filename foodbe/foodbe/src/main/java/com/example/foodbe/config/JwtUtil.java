package com.example.foodbe.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;   // Chuỗi bí mật trong application.properties

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // Thời gian sống token (ms)

    private SecretKey key;      // SecretKey dùng để ký token

    // Khởi tạo SecretKey sau khi inject giá trị từ @Value
    @PostConstruct
    public void init() {
        // HS256 yêu cầu khóa ít nhất 256 bits → convert từ chuỗi
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Tạo token với danh sách roles
    public String generateToken(String username, List<String> role) {
        return Jwts.builder()
                .setSubject(username)                      // Subject (thường là username)
                .claim("role", role)                      // Gán danh sách roles
                .setIssuedAt(new Date())                   // Thời gian phát hành
                .setExpiration(Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS))) // Expiration
                .signWith(key, SignatureAlgorithm.HS256)   // Tạo signature
                .compact(); // Gộp 3 phần lại thành JWT
    }

    // Lấy username từ token
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody(); // Lấy payload (claims) của token
            return claims.getSubject(); // Trả về giá trị subject trong payload (thường là username)
        } catch (JwtException e) {
            // Log lỗi chung chung, không log chi tiết
            System.err.println("Error parsing token");
            return null; // Token không hợp lệ
        }
    }

    // Lấy roles từ token
    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return (List<String>) claims.get("role"); // Trả về role từ token (dưới dạng List)
        } catch (JwtException e) {
            System.err.println("Error parsing token roles");
            return null;
        }
    }

    // Kiểm tra token đã hết hạn chưa
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            System.err.println("Error checking token expiration");
            return true;  // Nếu có lỗi thì coi như token đã hết hạn
        }
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            // Kiểm tra token có hợp lệ và không hết hạn
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build() // tạo 1 đối tương jwtParse chưa secret key sẵn sàng validate
                    .parseClaimsJws(token);  // tác token ra 3 phần, lấy header payload để tạo signature mới với secret key trong serve, so sanh signature vừa tạo và signature token. kiểm tra thời gian hết hạn token.
            return true;
//            return !isTokenExpired(token);  // Kiểm tra nếu token chưa hết hạn
        } catch (JwtException | IllegalArgumentException e) {
            // Log lỗi chung chung
            System.err.println("JWT validation error");
            return false;
        }
    }

    // lấy thời gian hết hạn trong token
    public Date extractExpiration(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

        }catch (JwtException e){
            System.err.println("JWT validation error");
            return null;
        }
    }
}
