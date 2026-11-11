package com.loay.engine.service;

import com.loay.engine.dto.request.CreateUserRequest;
import com.loay.engine.dto.request.UpdateUserRequest;
import com.loay.engine.dto.response.UserResponse;
import com.loay.engine.entity.User;
import com.loay.engine.exception.DuplicateResourceException;
import com.loay.engine.exception.ResourceNotFoundException;
import com.loay.engine.mapper.UserMapper;
import com.loay.engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        if (userRepository.existsByUsername(request.username())){
            throw new DuplicateResourceException("User", "username", request.username());
        }
        if (userRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("User", "email", request.email());
        }

        User user = userRepository.save(userMapper.toEntity(request));
        return userMapper.toResponse(user);
    }

    public UserResponse findUserById(UUID id){
        User user = getUserEntityById(id);
        return userMapper.toResponse(user);
    }

    public UserResponse findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = getUserEntityById(id);

        if (!user.getUsername().equals(request.username()) &&
                userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("User", "username", request.username());
        }

        if (!user.getEmail().equals(request.email()) &&
                userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        user.setUsername(request.username());
        user.setEmail(request.email());

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(UUID id){
        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User", "ID", id);
        }
        userRepository.deleteById(id);
    }

    public User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

}
