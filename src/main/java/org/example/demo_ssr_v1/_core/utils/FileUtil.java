package org.example.demo_ssr_v1._core.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// IoC 대상 아님 static 메서드로 만들 예정
public class FileUtil {

    // 프로젝트 루트 폴더 아래에 images/ 폴더를 생성할 예정 (프로필 이미지만 넣을 예정)
    public static final String IMAGES_DIR = "D:/uploads/";

    public static String saveFile(MultipartFile file) throws IOException {
        return saveFile(file, IMAGES_DIR);
    }

    public static String saveFile(MultipartFile file, String uploadDir) throws IOException {
        // 1. 유효성 검사
        if (file == null || file.isEmpty()) {
            return null; // 파일이 없으면 null (왜 선택사항이므로 에러 아님)
        }

        // 2. 업로드 디렉토리 생성
        // Path : 파일 시스템 경로를 나타내는 객체
        // paths.get() : 문자열 경로를 Path 객체로 변환 시켜주는 메서드
        Path uploadPath = Paths.get(IMAGES_DIR); // new Path() 내부에서
        // 디렉토리가 있으면 새로운 폴더를 생성하지 않고 없으면 자동 생성
        if (!Files.exists(uploadPath)) {
            // 디렉토리 생성인데 상위 까지 알아서 다 만들어 줌
            // Root/Image/user/a/aaa.png
            Files.createDirectories(uploadPath);
        }

        // 3. 원본 파일명 가져오기
        // getOriginalFilename() -> 사용자가 입력한 파일 이름
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || originalFilename.isEmpty()) {
            throw  new IOException("파일명이 없습니다");
        }

        // 4. UUID를 사용한 고유한 파일명 생성
        // 왜 UUID 를 사용하나요?
        // 사용자들은 같은 이름에 파일명을 서버에 저장 시키고자 할 수 있다. 그럼 원본 파일 사라짐
        String uuid = UUID.randomUUID().toString(); // 12kjhsdkjfhas934-sadkjhasdfk
        String savedFileName = uuid + "_" + originalFilename;
        // 결과 예시 : 123123asdf_abc.png

        // 5. 파일을 디스크(물리적 저장 장치)에 저장
        Path filePath = uploadPath.resolve(savedFileName);

        // 실제 파일 생성
        Files.copy(file.getInputStream(), filePath);

        return savedFileName;
    }

    // 유효성 검사 기능
    public static boolean isImageFile(MultipartFile file) {
        // 파일 이미지가 없으면 이미지가 아님
        if(file == null || file.isEmpty()) {
            return false;
        }
        // Content-Type 가져오기
        // 예시 : "image/jpg", "image/png", "image/gif"
        String contentType = file.getContentType();
        // Content-Type 가 image/ 로 시작하는지 확인
        return contentType != null && contentType.startsWith("image/");
    }

    // 프로필 이미지 삭제 처리
    public static void deleteFile(String filename) throws IOException {
        deleteFile(filename, IMAGES_DIR);
    }

    public static void deleteFile(String filename, String uploadDir) throws IOException {
        // 방어적 코드 (파일 이름이 없다면 삭제할 것이 없음)
        if (filename == null || filename.isEmpty()) {
            return;
        }

        // 삭제할 파일의 전체 경로를 생성해야 함 (D:/uploads)
        // file:///D:uploads/ + a.png (경로 + 파일 이름)
        Path filePath = Paths.get(uploadDir, filename);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        // 파일이 없으면 그냥 종료 됨
    }
}