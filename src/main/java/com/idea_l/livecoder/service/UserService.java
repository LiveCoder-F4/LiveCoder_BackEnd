package com.idea_l.livecoder.service;

import com.idea_l.livecoder.dto.LoginRequest;
import com.idea_l.livecoder.dto.LoginResponse;
import com.idea_l.livecoder.dto.RegisterRequest;
import com.idea_l.livecoder.entity.User;
import com.idea_l.livecoder.repository.UserRepository;
import com.idea_l.livecoder.util.JwtUtil;
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

    // 저장
    public User createUser(User user) {
        // 1. 유효성 검증
        if (user.getUsername() == null || user.getUsername().length() < 3) {
            throw new IllegalArgumentException("사용자명은 3자 이상이어야 합니다");
        }

        // 2. 중복 체크
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 사용자명입니다");
        }

        // 3. 데이터 가공
        user.setUsername(user.getUsername().toLowerCase()); // 소문자로 변환
        user.setTotalSolved(0); // 초기값 설정

        // 4. 저장
        return userRepository.save(user);
    }

    // 전체 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ID로 조회
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 삭제
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // 존재 여부 확인
    public boolean existsUser(Long id) {
        return userRepository.existsById(id);
    }

    // 회원가입
    public User registerUser(RegisterRequest request) {
        // 1. 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 사용자명입니다");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다");
        }

        // 2. User 엔티티 생성
        User user = new User();
        user.setUsername(request.getUsername().toLowerCase());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 암호화
        user.setNickname(request.getNickname());
        user.setBio(request.getBio());
        user.setGithubUrl(request.getGithubUrl());
        user.setTotalSolved(0);
        user.setCurrentExp(0);
        user.setCreatedAt(LocalDateTime.now());

        // 3. 저장
        return userRepository.save(user);
    }

    // 로그인
    public LoginResponse login(LoginRequest request) {
        // 1. 사용자 찾기 (사용자명 또는 이메일로)
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), 
                request.getUsernameOrEmail()
        );

        if (userOpt.isEmpty()) {
            return LoginResponse.failure("사용자를 찾을 수 없습니다");
        }

        User user = userOpt.get();

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResponse.failure("비밀번호가 일치하지 않습니다");
        }

        // 3. 마지막 활동 시간 업데이트
        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);

        // 4. JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId());

        // 5. 성공 응답
        return LoginResponse.success(user.getUserId(), user.getUsername(), user.getNickname(), token);
    }
}
