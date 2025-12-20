package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.UserPendingMapper;
import com.example.foodbe.models.UserPending;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.user.UserCreateDTO;
import com.example.foodbe.request.user.UserUpdateDTO;
import com.example.foodbe.response.user.UserResponseDTO;
import com.example.foodbe.mapper.UserMapper;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.repositories.UserRepository;
import com.example.foodbe.services.UserService;
import com.example.foodbe.utils.ConstantUtils;
import com.example.foodbe.utils.PageMapperUtils2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PageMapperUtils2 pageMapperUtils2;
    private final UserPendingMapper userPendingMapper;

    @Override
    public PageResponse<UserResponseDTO> findByEmail(String email, Pageable pageable) {
        Page<AppUser> page = userRepository.findByEmailContaining(email, pageable);
        Function<AppUser, UserResponseDTO> mapper = userMapper::toDto;
        return pageMapperUtils2.toPageResponseDto(page, mapper);
    }

    @Override
    public void deleteById(Long id) {
        AppUser appUser = userRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +id));
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDTO findById(Long id) {
        AppUser user= userRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +id));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDTO create(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new NotFoundException(ConstantUtils.ExceptionMessage.EXISTS +userCreateDTO.getEmail());
        }
        String newPass=  bCryptPasswordEncoder.encode(userCreateDTO.getPassword());
        userCreateDTO.setPassword(newPass);
        AppUser user= userMapper.toEntity(userCreateDTO);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDTO create2(UserPending userPending) {
        if (userRepository.existsByEmail(userPending.getEmail())) {
            throw new NotFoundException(ConstantUtils.ExceptionMessage.EXISTS);
        }
        String newPass=  bCryptPasswordEncoder.encode(userPending.getPassword());
        userPending.setPassword(newPass);
        AppUser user= userPendingMapper.toAppUser(userPending);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public AppUser findByEmail(String email) {
        AppUser appUser= userRepository.findByEmail(email)
                .orElseThrow(() ->new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +email));
        return appUser;
    }

    @Override
    public UserResponseDTO update(Long id, UserUpdateDTO userUpdateDTO) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND + id));

        if (userUpdateDTO.getName() != null) {
            user.setName(userUpdateDTO.getName());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPhone() != null) {
            user.setPhone(userUpdateDTO.getPhone());
        }
        if (userUpdateDTO.getStatus() != null) {
            user.setStatus(userUpdateDTO.getStatus());
        }
        if (userUpdateDTO.getRole() != null) {
            user.setRole(userUpdateDTO.getRole());
        }

        return userMapper.toDto(userRepository.save(user));
    }

}
