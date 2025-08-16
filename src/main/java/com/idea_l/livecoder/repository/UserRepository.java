package com.idea_l.livecoder.repository;

import com.idea_l.livecoder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    // 사용자명으로 찾기
    Optional<User> findByUsername(String username);

    // 이메일로 찾기
    Optional<User> findByEmail(String email);

    // 사용자명 또는 이메일로 찾기
    Optional<User> findByUsernameOrEmail(String username, String email);

    // 닉네임으로 검색 (부분 일치)
    List<User> findByNicknameContainingIgnoreCase(String nickname);

    // 해결한 문제 수가 특정 수 이상인 사용자들
    List<User> findByTotalSolvedGreaterThanEqual(Integer totalSolved);

    // 경험치 순으로 상위 N명 조회
    @Query("SELECT u FROM User u ORDER BY u.currentExp DESC")
    List<User> findTopUsersByExp(org.springframework.data.domain.Pageable pageable);

    // 사용자명 중복 체크
    boolean existsByUsername(String username);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

}
