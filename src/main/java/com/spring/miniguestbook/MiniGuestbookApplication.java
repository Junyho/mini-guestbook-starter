package com.spring.miniguestbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing : 생성일·수정일 자동 기록(Auditing)을 활성화 (BaseEntity와 짝)
@EnableJpaAuditing
// @SpringBootApplication : 이 클래스가 스프링 부트 앱의 시작점이라는 표시 (자동 설정 + 컴포넌트 스캔)
@SpringBootApplication
public class MiniGuestbookApplication {

    // 프로그램의 진입점: 여기서 내장 톰캣 서버가 뜨면서 8080 포트로 요청을 받음
    public static void main(String[] args) {
        SpringApplication.run(MiniGuestbookApplication.class, args);
    }

}
