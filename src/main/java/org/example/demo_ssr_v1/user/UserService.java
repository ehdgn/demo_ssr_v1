package org.example.demo_ssr_v1.user;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1._core.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service --> 응답 DTO 설계 해서 전달 --> Controller

/**
 * 사용자 서비스 레이어
 *
 * 1. 역할
 *  - 비즈니스 로직을 처리하는 계층
 *  - Controller와 Repository 사이의 중간 계층
 *  - 트랜잭션 관리
 *  - 여러 Repository를 조합하여 복잡한 비즈니스 로직을 처리
 *
 */

@Service // IoC 대상 @Component의 특수한 형태
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    // 객체 지향 개념 --> SOLID 원칙
    // DIP - 추상화가 높은 녀석을 선언하는 것이 좋다.
    private final UserRepository userRepository;

    @Transactional
    public User 회원가입(UserRequest.JoinDTO joinDTO) {

        // 유효성 검사
        joinDTO.validate();

        // 1. 사용자명 중복 체크
        if (userRepository.findByUsername(joinDTO.getUsername()).isPresent()) {
            // isPresent -> 있으면 true 반환, 없으면 false 반환
            throw new Exception400("이미 존재하는 사용자 이름입니다.");
        }

        // User 엔티티에 저장할 때는 String 이어야 하고 null 값도 가질 수 있음
        String profileImageFileName = null;

        // 2. 회원 가입시 파일이 넘어 왔는지 확인
        if (joinDTO.getProfileImage() != null) {

            // 2-1. 유효성 검사 (이미지 파일 이어야함)
            try {
                if (!FileUtil.isImageFile(joinDTO.getProfileImage())) {
                    throw new Exception400("이미지 파일만 업로드 가능합니다.");
                }

                profileImageFileName = FileUtil.saveFile(joinDTO.getProfileImage());

            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        User user = joinDTO.toEntity(profileImageFileName);
        return userRepository.save(user);
    }

    public User 로그인(UserRequest.LoginDTO loginDTO) {

        // 사용자가 던진 값과 DB에 있는 사용자 이름과 비밀번호를 확인해 주어야 한다.
        User userEntity = userRepository.findByUsernameAndPassword(
                loginDTO.getUsername(),
                loginDTO.getPassword())
                .orElse(null); // 로그인 실패시 null 반환

        if (userEntity == null)
            throw new Exception400("사용자명 또는 비밀번호가 올바르지 않습니다.");

        return userEntity;
    }

    public User 회원정보수정화면(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        if (!userEntity.isOwner(userId)) {
            throw new Exception403("회원 정보 수정 권한이 없습니다.");
        }

        return userEntity;
    }

    // 데이터의 수정 (더티 체킹 -> 반드시 먼저 조회 -> 조회된 객체의 상태값 변경 -> 자동 변경)
    // 1. 회원 정보 조회
    // 2. 인가 검사
    // 3. 엔티티 상태 변경 (더티 체킹)
    // 4. 트랜잭션이 일어나고 변경된 User 엔티티 반환
    @Transactional
    public User 회원정보수정(UserRequest.UpdateDTO updateDTO, Long userId) {
        User userEntity =  userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        if (!userEntity.isOwner(userId))
            throw new Exception403("회원 정보 수정 권한이 없습니다.");

        // 객체 상태값 변경 (트랜잭션이 끝나면 자동으로 commit 및 반영)
        userEntity.update(updateDTO);

        return userEntity;
    }
}