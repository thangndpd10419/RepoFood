package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.mapper.UserPendingMapper;
import com.example.foodbe.models.UserPending;
import com.example.foodbe.repositories.UserPendingRepository;
import com.example.foodbe.request.user.UserCreateDTO;
import com.example.foodbe.services.UserPendingService;
import com.example.foodbe.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPendingServiceImpl implements UserPendingService {
    private final UserPendingRepository userPendingRepository;
    private final UserPendingMapper userPendingMapper;

    @Override
    public void create(UserCreateDTO userCreateDTO) {
        if(userPendingRepository.existsByEmail(userCreateDTO.getEmail())){
            throw new NotFoundException(ConstantUtils.ExceptionMessage.EXISTS);
        }

         userPendingRepository.save(userPendingMapper.toEntity(userCreateDTO));
    }

    @Override
    public List<UserPending> findAll() {
        return userPendingRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        UserPending userPending= userPendingRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND));

        userPendingRepository.deleteById(id);
    }
}
