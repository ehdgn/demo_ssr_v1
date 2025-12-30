package org.example.demo_ssr_v1.user;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1._core.errors.exception.Exception500;
import org.example.demo_ssr_v1._core.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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
    private final PasswordEncoder passwordEncoder;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${tenco.key}")
    private String tencoKey;

    @Value("${oauth.kakao.client-secret}")
    private String secretKey;

    @Transactional
    public User 카카오소셜로그인(String code) {
        // 1. 인가 코드로 엑세스 토큰 발급
        UserResponse.OAuthToken oAuthToken = 카카오엑세스토큰발급(code);

        // 2. 엑세스 토큰으로 프로필 정보 조회
        UserResponse.KakaoProfile kakaoProfile = 카카오프로필조회(oAuthToken.getAccessToken());

        // 3. 프로필 정보로 사용자 생성 또는 조회
        User user = 카카오사용자생성또는조회(kakaoProfile);

        // 4. 컨트롤러 단으로 user 반환
        return user;
    }

    /**
     * 카카오 인가 코드로 엑세스 토큰 발급
     * @param code 카카오 인가 코드
     * @return Oauth 엑세스 토큰 정보
     */
    private UserResponse.OAuthToken 카카오엑세스토큰발급(String code) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", "0bbb731cd18ad2581c9e4faa384a97a3");
        tokenParams.add("redirect_uri", "http://localhost:8080/user/kakao");
        tokenParams.add("code", code);
        // TODO - env 파일에 옮겨야 함
        tokenParams.add("client_secret", secretKey);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);

        ResponseEntity<UserResponse.OAuthToken> tokenResponse = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                tokenRequest,
                UserResponse.OAuthToken.class
        );

        UserResponse.OAuthToken oAuthToken = tokenResponse.getBody();
        return oAuthToken;
    }

    /**
     * 카카오 엑세스 토큰으로 프로필 정보 조회
     * @param accessToken
     * @return 카카오 프로필 정보
     */
    private UserResponse.KakaoProfile 카카오프로필조회(String accessToken) {
        RestTemplate profileRt = new RestTemplate();

        HttpHeaders profileHeaders = new HttpHeaders();

        profileHeaders.add("Authorization", "Bearer " + accessToken);
        profileHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);

        ResponseEntity<UserResponse.KakaoProfile> profileResponse =
                profileRt.exchange(
                        "https://kapi.kakao.com/v2/user/me",
                        HttpMethod.POST,
                        profileRequest,
                        UserResponse.KakaoProfile.class
                );

        UserResponse.KakaoProfile kakaoProfile = profileResponse.getBody();
        return kakaoProfile;
    }

    @Transactional
    public User 카카오사용자생성또는조회(UserResponse.KakaoProfile kakaoProfile) {

        String username = kakaoProfile.getProperties().getNickname() + "_" + kakaoProfile.getId();

        User userOrigin = 사용자이름조회(username);
        if (userOrigin == null) {

            User newUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(tencoKey)) // 소셜 로그인은 임시 비밀번호로 설정 한다.
                    .email(username + "@kakao.com") // 선택 사항 (카카오 이메일 비즈니스 앱 신청)
                    .provider(OAuthProvider.KAKAO)
                    .build();

            String profileImage = kakaoProfile.getProperties().getProfile_image();
            if (profileImage != null && !profileImage.isEmpty()) {
                newUser.setProfileImage(profileImage);
            }

            소셜회원가입(newUser);
            userOrigin = newUser; // 필수
        }

        return userOrigin;
    }

    @Transactional
    public User 회원가입(UserRequest.JoinDTO joinDTO) {

        // 유효성 검사
        joinDTO.validate();

        // 1. 사용자명 중복 체크
        if (userRepository.findByUsername(joinDTO.getUsername()).isPresent()) {
            // isPresent -> 있으면 true 반환, 없으면 false 반환
            throw new Exception400("이미 존재하는 사용자 이름입니다.");
        }

        // 1.1 이메일 중복 체크
        if (userRepository.findByEmail(joinDTO.getEmail()).isPresent()) {
            throw new Exception400("이미 등록된 이메일입니다.");
        }

        // User 엔티티에 저장할 때는 String 이어야 하고 null 값도 가질 수 있음
        String profileImageFileName = null;

        // 2. 회원 가입시 파일이 넘어 왔는지 확인
        if (joinDTO.getProfileImage() != null && !joinDTO.getProfileImage().isEmpty()) {

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

        // 해싱 --> 해시값을 만들어줌
        String hashPwd = passwordEncoder.encode(joinDTO.getPassword());
        System.out.println("hashPwd: " + hashPwd);

        User user = joinDTO.toEntity(profileImageFileName);
        user.setPassword(hashPwd);

        return userRepository.save(user);
    }

    public User 로그인(UserRequest.LoginDTO loginDTO) {

        // 사용자가 던진 값과 DB에 있는 사용자 이름과 비밀번호를 확인해 주어야 한다.
        User userEntity = userRepository.findByUsernameAndWithRoles(loginDTO.getUsername())
                .orElse(null); // 로그인 실패시 null 반환

        if (userEntity == null)
            throw new Exception400("사용자가 존재 하지 않습니다.");

        // 비밀번호 검증 (BCrypt matches 메서드를 사용해서 비교 하면 된다)
        // 일치하면 true, 불일치 하면 false
        if (passwordEncoder.matches(loginDTO.getPassword(), userEntity.getPassword()) == false) {
            throw new Exception400("사용자명 또는 비밀번호가 올바르지 않습니다.");
        }

        // 기존 샘플 데이터로 회원가입된 사용자들로는 로그인을 못함
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

        // 추가 - 프로필 이미지 처리
        // 중요: 우리 프로젝트에서는 이미지 수정도 선택 사항
        // 새로운 이미지 파일을 생성하고 기존에 있던 이미지 파일을 삭제해야 한다.
        // 추가로 DB 정보도 업데이트 해야 한다.

        String oldProfileImage = userEntity.getProfileImage();
        // 분기 처리 - 이미지명이 있거나 또는 null 값이다.
        if (updateDTO.getProfileImage() != null && !updateDTO.getProfileImage().isEmpty()) {
            // 1. 이미지 파일인지 검증
            if (!FileUtil.isImageFile(updateDTO.getProfileImage())) {
                throw new Exception400("이미지 파일만 업로드 가능합니다.");
            }

            // 2. 새 이미지 저장
            try {
                String newProfileImageFilename = FileUtil.saveFile(updateDTO.getProfileImage());
                // 새로 만들어진 파일 이름을 잠시 DTO에 보관
                updateDTO.setProfileImageFilename(newProfileImageFilename);

                if (oldProfileImage != null && !oldProfileImage.isEmpty()) {
                    // 기존에 있던 이미지를 삭제 처리
                    FileUtil.deleteFile(oldProfileImage);
                }

            } catch (IOException e) {
                throw new Exception500("파일 저장에 실패했습니다.");
            }
            // end of 파일 검증
        } else {
            // 새 이미지가 업로드 되지 않았으면 기존 이미지 파일이름 유지
            updateDTO.setProfileImageFilename(oldProfileImage);
        }
        // 비밀번호 암호화 처리
        String hashPwd = passwordEncoder.encode(updateDTO.getPassword());
        updateDTO.setPassword(hashPwd);

        // 객체 상태값 변경 (트랜잭션이 끝나면 자동으로 commit 및 반영)
        userEntity.update(updateDTO);
        return userEntity;
    }

    public User 마이페이지(Long sessionUserId) {

        User user = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // 인가 처리
        if (!user.isOwner(sessionUserId)) {
            throw new Exception403("권한이 없습니다.");
        }

        return user;
    }

    @Transactional
    public User 프로필이미지삭제(Long sessionUserId) {
        // 1. 회원 정보 조회
        // 2. 회원 정보와 세션 id 값이 같은지 판단 (인가처리)
        // 3. 프로필 이미지가 있다면 삭제 (FileUtil) 헬퍼 클래스 사용 할 예정 (디스크에서 삭제)
        // 4. DB에서 프로필 이름 null로 업데이트 처리
        User userEntity = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        if (!userEntity.isOwner(sessionUserId))
            throw new Exception403("프로필 이미지 삭제 권한이 없습니다.");

        String profileImage = userEntity.getProfileImage();
        if (profileImage != null || !profileImage.isEmpty()) {
            try {
                FileUtil.deleteFile(profileImage);
            } catch (IOException e) {
                System.out.println("프로필 이미지 파일 삭제 실패");
            }
        }

        // 객체 상태값 변경 (트랜잭션이 끝나는 시점 더티 체킹)
        userEntity.setProfileImage(null);

        return userEntity;
    }

    public User 사용자이름조회(String username) {
        // 사용자 이름 여부만 조회하는 기능
        return userRepository.findByUsername(username).orElse(null);
    }

    public void 소셜회원가입(User user) {
        userRepository.save(user);
    }

    @Transactional
    public User 포인트충전(Long userId, Integer amount) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // 2. 포인트 충전
        user.chargePoint(amount);

        // 3. 변경된 엔티티 반환
        return userRepository.save(user);
    }
}