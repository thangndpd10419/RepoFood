package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PageMapperUtils2 pageMapperUtils2;


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
    public AppUser findByEmail(String email) {
        AppUser appUser= userRepository.findByEmail(email)
                .orElseThrow(() ->new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND +email));
        return appUser;
    }

    @Override
    public UserResponseDTO updateById(UserUpdateDTO userUpdateDTO) {
        AppUser user = userRepository.findById(userUpdateDTO.getId())
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+ userUpdateDTO.getId()));

        String newPass = bCryptPasswordEncoder.encode(userUpdateDTO.getPassword());
        userMapper.updateToEntity(userUpdateDTO,newPass, user );
        return userMapper.toDto(userRepository.save(user));
    }
    // optional dùng khi dữ liệu trả về có thể null( search,..)
    // trả về đúng và bắt execptin khi dữ liệu trả về phải có mới hoạt đọng tiếp
}
