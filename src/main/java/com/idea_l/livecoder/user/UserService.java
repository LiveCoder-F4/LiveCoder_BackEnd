package com.idea_l.livecoder.user;

import com.idea_l.livecoder.user.LoginRequest;
import com.idea_l.livecoder.user.LoginResponse;
import com.idea_l.livecoder.user.RegisterRequest;
import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.user.UserRepository;
import com.idea_l.livecoder.common.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User createUser(User user) {
        if (user.getUsername() == null || user.getUsername().length() < 3) {
            throw new IllegalArgumentException("사용자명은 3자 이상이어야 합니다");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 사용자명입니다");
        }

        user.setUsername(user.getUsername().toLowerCase());
        user.setTotalSolved(0);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsUser(Long id) {
        return userRepository.existsById(id);
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 사용자명입니다");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다");
        }

        User user = new User();
        user.setUsername(request.getUsername().toLowerCase());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setBio(request.getBio());
        user.setGithubUrl(request.getGithubUrl());
        user.setTotalSolved(0);
        user.setIsSolvedPublic(false);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(),
                request.getUsernameOrEmail()
        );

        if (userOpt.isEmpty()) {
            return LoginResponse.failure("사용자를 찾을 수 없습니다");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResponse.failure("비밀번호가 일치하지 않습니다");
        }

        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId());

        return LoginResponse.success(user.getUserId(), user.getUsername(), user.getNickname(), token);
    }
}
