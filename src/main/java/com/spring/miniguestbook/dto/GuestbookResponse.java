package com.spring.miniguestbook.dto;

import lombok.Getter;

import java.time.LocalDateTime;

// 응답 dto : 클라이언트에게 내보낼 데이터
// 엔터티(guestbook)를 그대로 노출하지 않고, 필요한 값만 골라 dto로 변환해 전달
@Getter
public class GuestbookResponse {
    private final Long id;
    private final String name;
    private final String message;
    private final LocalDateTime createAt;
    private final LocalDateTime modifiedAt;

    // 생성자로만 값을 받는 불변(immutable) 객체 - 응답 데이터는 만들어진 뒤 바뀔 일이 없다.
    public GuestbookResponse(Long id, String name, String message, LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }
}
