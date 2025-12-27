package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.repositories.UserRepository;
import com.example.foodbe.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
    private  final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       AppUser user= userRepository.findByEmail(email)
                .orElseThrow(()->new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +email));

        return new org.springframework.security.core.userdetails.User(
                 user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()))
        );
    }
}
