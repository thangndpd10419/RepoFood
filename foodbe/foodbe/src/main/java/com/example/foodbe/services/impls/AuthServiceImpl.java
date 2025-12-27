package com.example.foodbe.services.impls;

import com.example.foodbe.config.JwtUtil;
import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.exception_handler.exception.InvalidDataException;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Token;
import com.example.foodbe.request.auth.AuthRequest;
import com.example.foodbe.response.auth.AuthResponse;
import com.example.foodbe.services.AuthService;
import com.example.foodbe.services.TokenService;
import com.example.foodbe.services.UserService;
import com.example.foodbe.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final UserService userService;

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {

        // 1. Authenticate username/password (Spring Security lo hết)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );


        // 2. Lấy user từ authentication principal
        // Vì bạn return org.springframework.security.core.userdetails.User
        User userDetails = (User) authentication.getPrincipal();
        String email = userDetails.getUsername();
        AppUser appUser= userService.findByEmail(email);

        if(appUser == null){
            throw new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +email);
        }

        // 3. Lấy roles từ authorities
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();

        // 4. Tạo Access Token
        String accessToken = jwtUtil.generateToken(email, roles);
//        Instant expiresAt = jwtUtil.getExpiration(accessToken);

        // 5. Tạo Refresh Token (và lưu DB / Redis)
        String refreshToken = tokenService.createToken(appUser);

        // 6. Trả về
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshTokenRaw) {

        // 1. Validate refresh token
        Token token = tokenService.validateToken(refreshTokenRaw);

        AppUser appUser = token.getUser();

        // 2. Lấy roles của user
        List<String> roles = List.of("ROLE_" + appUser.getRole().name());

        // 3. Tạo access token mới
        String newAccessToken = jwtUtil.generateToken(appUser.getEmail(), roles);
//        Instant expiresAt = jwtUtil.getExpiration(newAccessToken);

        // 4. ROTATE refresh token: xóa cái cũ, tạo cái mới
        String t= token.getToken();
        tokenService.revokeToken(t);
        String newRefreshToken = tokenService.createToken(appUser);

        // 5. Trả về response
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

}
