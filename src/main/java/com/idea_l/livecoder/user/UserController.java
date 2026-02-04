package com.idea_l.livecoder.user;

import com.idea_l.livecoder.user.AuthCheckResponse;
import com.idea_l.livecoder.user.LoginRequest;
import com.idea_l.livecoder.user.LoginResponse;
import com.idea_l.livecoder.user.RegisterRequest;
import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.user.UserRepository;
import com.idea_l.livecoder.user.UserService;
import com.idea_l.livecoder.common.JwtUtil;
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
@RequestMapping("/users")
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

    @GetMapping
    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자 목록을 조회합니다")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "특정 사용자 조회", description = "ID로 특정 사용자의 정보를 조회합니다")
    public User getUserById(@Parameter(description = "사용자 ID") @PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + id);
        }
    }

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

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃이 완료되었습니다");
    }

    @GetMapping("/auth-check")
    @Operation(summary = "인증 상태 확인", description = "현재 로그인 상태를 확인합니다")
    public ResponseEntity<AuthCheckResponse> checkAuth(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);

                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    return ResponseEntity.ok(AuthCheckResponse.authenticated(userId, username, user.getNickname()));
                }
            }
        }

        return ResponseEntity.ok(AuthCheckResponse.notAuthenticated());
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자의 닉네임, 소개, Github URL을 수정합니다")
    @PutMapping("/{id}")
    public User updateUser(@Parameter(description = "사용자 ID") @PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));

        user.setNickname(userDetails.getNickname());
        user.setBio(userDetails.getBio());
        user.setGithubUrl(userDetails.getGithubUrl());

        return userRepository.save(user);
    }

    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴를 처리합니다")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @Valid @RequestBody PasswordConfirmRequest request,
            HttpServletRequest httpRequest) {
        Long tokenUserId = getUserIdFromToken(httpRequest);
        if (!id.equals(tokenUserId)) {
            return ResponseEntity.status(403).body("본인만 탈퇴할 수 있습니다");
        }

        try {
            userService.deleteUserWithPasswordConfirm(id, request.getPassword());
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "사용자명 검색", description = "사용자명으로 사용자를 검색합니다")
    @GetMapping("/search")
    public Optional<User> getUserByUsername(@Parameter(description = "사용자명") @RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user;
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + username);
        }
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다")
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @Valid @RequestBody PasswordChangeRequest request,
            HttpServletRequest httpRequest) {
        Long tokenUserId = getUserIdFromToken(httpRequest);
        if (!id.equals(tokenUserId)) {
            return ResponseEntity.status(403).body("본인만 비밀번호를 변경할 수 있습니다");
        }

        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok("비밀번호가 변경되었습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "이메일 변경", description = "사용자의 이메일을 변경합니다")
    @PutMapping("/{id}/email")
    public ResponseEntity<?> changeEmail(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @Valid @RequestBody EmailChangeRequest request,
            HttpServletRequest httpRequest) {
        Long tokenUserId = getUserIdFromToken(httpRequest);
        if (!id.equals(tokenUserId)) {
            return ResponseEntity.status(403).body("본인만 이메일을 변경할 수 있습니다");
        }

        try {
            userService.changeEmail(id, request);
            return ResponseEntity.ok("이메일이 변경되었습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "풀이 공개 여부 변경", description = "사용자의 풀이 공개 여부를 변경합니다")
    @PutMapping("/{id}/solved")
    public ResponseEntity<?> updateSolvedVisibility(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @Valid @RequestBody SolvedVisibilityRequest request,
            HttpServletRequest httpRequest) {
        Long tokenUserId = getUserIdFromToken(httpRequest);
        if (!id.equals(tokenUserId)) {
            return ResponseEntity.status(403).body("본인만 설정을 변경할 수 있습니다");
        }

        try {
            userService.updateSolvedVisibility(id, request);
            return ResponseEntity.ok("풀이 공개 설정이 변경되었습니다");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserIdFromToken(token);
            }
        }
        return null;
    }
}
