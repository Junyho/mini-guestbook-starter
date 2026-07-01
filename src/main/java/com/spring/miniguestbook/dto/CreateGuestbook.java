package com.spring.miniguestbook.dto;

import lombok.Getter;

// 요청 dto : 등록(post) 요청의 json 본문을 담는 그릇
// getter : jackson이 json을 이 객체로 바꿀 떄 필드를 인식하고,
// 서비스가 값을 꺼낼 때 사용
@Getter
public class CreateGuestbook {
    private String name;
    private String message;
}
