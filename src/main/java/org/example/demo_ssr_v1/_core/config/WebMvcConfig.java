package org.example.demo_ssr_v1._core.config;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.interceptor.LoginInterceptor;
import org.example.demo_ssr_v1._core.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정 클래스
 * @Controller, @Service, @Repository, @Component
 */
// @Component 클래스 내부에서 @Bean 어노테이션을 사용해야 된다면
@Configuration  // 내부도 IoC 대상 여부 확인
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    // DI 처리
    private final LoginInterceptor loginInterceptor;
    private final SessionInterceptor sessionInterceptor;

    // ps. 인터셉터는 당연히 여러 개 등록 가능
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sessionInterceptor)
                        .addPathPatterns("/**");

        // 1. 설정에 LoginInterceptor를 등록하는 코드
        // 2. 인터셉터가 동작할 URL 패턴 지정
        // 3. 어떤 URL 요청이 로그인 여부를 필요할지 확인 해야 함
        //    /board/** <-- 일단 이 엔트포인트 다 검사
        //    /user/**  <-- 일단 이 엔트포인트 다 검사
        //    -> 단, 특정 URL은 제외
        registry.addInterceptor(loginInterceptor)
                // /** <-- 모든 URL 제외 대상이 됨. 일단 사용 안함
                .addPathPatterns("/board/**", "/user/**", "/reply/**")
                .excludePathPatterns(
                        "/login",
                        "/join",
                        "/logout",
                        "/board/list",
                        "/",
                        "/board/{id:\\d+}",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.io",
                        "/h2-console/**"
                );
                // \\d+는 정규표현식으로 1개 이상의 숫자를 의미
                // /board/1, /board/1234 <-- 허용
                // /board/abc 같은 경우 매칭되지 않음

    }

    /**
     * 정적 리소스 핸들러
     * 업로드된 이미지 파일을 웹에서 접근할 수 있도록 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /**
         *  /images/** 경로로 요청이 들어오면 나의 폴더 images/ 디렉토리에서 찾게 설정
         */
        // 머스태치 이미지 태그에 src 경로에 /images/** 같은 경로로 설정 되어 있다면
        // 스프링이 알아서 내 폴더 file:(프로젝트 루트 디렉토리)안에 images/ 폴더를 찾게 한다.
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///D:/uploads/");
        //** file:/// 문법 설명
        // file: 파일 시스템을 가리킨다는 의미
        // 파일 시스템에서 절대 경로를 의미하는 URI 표기법은 -> ///: 이다.
        // file:images/는 앞에 슬러시가 없기 때문에 상대 경로를 의미한다.
        // file:///D:/upload/ <-- 내 컴퓨터 절대 경로를 의미한다.
    }
}
