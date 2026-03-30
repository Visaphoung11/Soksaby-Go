package com.smart.service.serviceimpl;

import com.smart.service.dtoRequest.UserProfileUpdateRequest;
import com.smart.service.dtoResponse.UserProfileResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.UserService;
import com.smart.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final com.smart.service.repository.ReviewRepository reviewRepository;

    @Override
    public UserProfileResponse getUserProfile(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserProfileResponse response = userMapper.toResponse(user);
        response.setRatingCount(reviewRepository.countByDriverId(user.getId()));
        return response;
    }

    @Override
    public UserProfileResponse updateUserProfile(String email, UserProfileUpdateRequest request) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        userMapper.updateUserFromDto(request, user);

        UserEntity updatedUser = userRepository.save(user);
        UserProfileResponse response = userMapper.toResponse(updatedUser);
        response.setRatingCount(reviewRepository.countByDriverId(user.getId()));
        return response;
    }
}
