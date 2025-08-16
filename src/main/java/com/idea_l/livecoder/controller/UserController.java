package com.idea_l.livecoder.controller;

import com.idea_l.livecoder.dto.AuthCheckResponse;
import com.idea_l.livecoder.dto.LoginRequest;
import com.idea_l.livecoder.dto.LoginResponse;
import com.idea_l.livecoder.dto.RegisterRequest;
import com.idea_l.livecoder.entity.User;
import com.idea_l.livecoder.repository.UserRepository;
import com.idea_l.livecoder.service.UserService;
import com.idea_l.livecoder.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    // 1. 모든 사용자 조회
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. 특정 사용자 조회
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + id);
        }
    }

    // 회원가입
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.ok().body("회원가입이 완료되었습니다. 사용자 ID: " + user.getUserId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        
        if (response.getUserId() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다")
    public ResponseEntity<String> logout() {
        // JWT는 stateless이므로 클라이언트에서 토큰을 삭제하면 됨
        return ResponseEntity.ok("로그아웃이 완료되었습니다");
    }

    // 인증 상태 확인
    @GetMapping("/auth-check")
    @Operation(summary = "인증 상태 확인", description = "현재 로그인 상태를 확인합니다")
    public ResponseEntity<AuthCheckResponse> checkAuth(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                
                // 사용자 정보 조회하여 nickname 가져오기
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    return ResponseEntity.ok(AuthCheckResponse.authenticated(userId, username, user.getNickname()));
                }
            }
        }
        
        return ResponseEntity.ok(AuthCheckResponse.notAuthenticated());
    }

    // 4. 사용자 정보 수정
    @Operation(summary = "사용자 정보 수정")
    @PutMapping("/{id}")
    public User updateUser(@Parameter(description = "사용자 ID") @PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));

        user.setNickname(userDetails.getNickname());
        user.setBio(userDetails.getBio());
        user.setGithubUrl(userDetails.getGithubUrl());

        return userRepository.save(user);
    }

    // 5. 사용자 삭제
    @Operation(summary = "사용자 삭제")
    @DeleteMapping("/{id}")
    public String deleteUser(@Parameter(description = "사용자 ID") @PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "사용자가 삭제되었습니다.";
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + id);
        }
    }

    // 6. 사용자명으로 검색
    @Operation(summary = "사용자명 검색")
    @GetMapping("/search")
    public Optional<User> getUserByUsername(@Parameter(description = "사용자명") @RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user;
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + username);
        }
    }
}
