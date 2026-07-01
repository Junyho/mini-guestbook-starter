package com.spring.miniguestbook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
// 이 클래스가 db 테이블과 연결되는 객체라는 표시
@Entity
// 매핑될 실제 테이블 이름 지정 (생략하면 클래스명 사용)
@Table(name = "geustbooks")
// jpa는 기본 생성자가 필요하지만, 외부에서 함부로 빈 객체를 못 만들도록 protected로 막음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guestbook extends BaseEntity {

    // Id : 기본 키(pk) / @GeneratedValue.IDENTITY : DB에서 1씩 자동 증가시켜줌
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    // null이 들어오면 안되고 길이는 50으로 제한
    @Column(nullable = false, length = 50)
    private String name;


    @Column(nullable = false, length = 1000)
    private String message;

    // 필요한 값을 채워 새 글을 만들 때 쓰는 생성자
    public Guestbook(String name, String message) {
        this.name = name;
        this.message = message;
    }

    // "무엇을 바꾸는지" 의도가 드러나게 엔터티 안에 둠
    public void update(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
