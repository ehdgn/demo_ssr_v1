package org.example.demo_ssr_v1._core.errors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.demo_ssr_v1._core.errors.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// @ControllerAdvice - 모든 컨트롤러에서 발생하는 예외를 이 클래스에서 중앙 집중화 시킴
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {

    // 내가 지켜볼 예외를 명시를 해주면 ControllerAdvice가 가지고와서 처리
    @ExceptionHandler(Exception400.class)
    public String ex400(Exception400 e, HttpServletRequest request) {
        log.warn("=== 400 에러 발생 ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메세지 : {}", e.getMessage());
        log.warn("예외 클래스 : {}", e.getClass().getSimpleName());
        request.setAttribute("msg", e.getMessage());
        return "err/400";
    }

    // 401 인증 오류
    @ExceptionHandler(Exception401.class)
    @ResponseBody
    public ResponseEntity<String> ex401(Exception401 e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
                "location.href = '/login';" +
                "</script>";

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 403 인가 오류
    @ExceptionHandler(Exception403.class)
    @ResponseBody
    public ResponseEntity<String> ex403(Exception403 e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
        "history.back();" +
        "</script>";

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 404
    // 템플릿 파일에선 세션 정보와 / Request 객체를 바로 접근 못하게 막았음 (기본값)
    @ExceptionHandler(Exception404.class)
    public String ex404(Exception404 e, HttpServletRequest request, Model model) {
        log.warn("=== 404 에러 발생 ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메세지 : {}", e.getMessage());
        log.warn("예외 클래스 : {}", e.getClass().getSimpleName());
        // request.setAttribute("msg", e.getMessage());
        model.addAttribute("msg", e.getMessage());
        return "err/404";
    }

    // 500 서버 내부 오류
    @ExceptionHandler(Exception500.class)
    public String ex500(Exception500 e, HttpServletRequest request) {
        log.warn("=== 500 에러 발생 ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메세지 : {}", e.getMessage());
        log.warn("예외 클래스 : {}", e.getClass().getSimpleName());
        request.setAttribute("msg", e.getMessage());
        return "err/500";
    }

    // 데이터베이스 제약조건 위반 예외 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataViolationException(DataIntegrityViolationException e,
                                               HttpServletRequest request,
                                               Model model) {
        log.warn("=== 데이터베이스 제약조건 위반 오류 발생 ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메세지 : {}", e.getMessage());
        log.warn("예외 클래스 : {}", e.getClass().getSimpleName());

        // 외래키 제약조건 위반인 경우
        String errorMessage = e.getMessage();
        if (errorMessage != null && errorMessage.contains("FOREIGN KEY")) {
            model.addAttribute("msg", "관련된 데이터가 있어서 삭제할 수 없습니다.");
        } else {
            model.addAttribute("msg", "데이터베이스 제약 조건 위반");
        }

        return "err/500";
    }

    // 클래스 로딩 오류 처리 (NoClassDefFoundException, ClassNotFoundException 등)
    @ExceptionHandler(Error.class)
    public String handleError(Error e, HttpServletRequest request, Model model) {
        log.warn("=== 예상하지 못한 에러 발생 ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메세지 : {}", e.getMessage());
        log.warn("예외 클래스 : {}", e.getClass().getSimpleName());
        model.addAttribute("msg", "심각한 오류 발생(클래스를 찾을 수 없습니다.)");

        return "err/500";
    }

    // 기타 모든 실행시점 오류 처리
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.warn("=== 예상하지 못한 에러 발생 ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메세지 : {}", e.getMessage());
        log.warn("예외 클래스 : {}", e.getClass().getSimpleName());
        request.setAttribute("msg", e.getMessage());

        return "err/500";
    }
}