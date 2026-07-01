package com.spring.miniguestbook.controller;

import com.spring.miniguestbook.dto.CreateGuestbook;
import com.spring.miniguestbook.dto.GuestbookResponse;
import com.spring.miniguestbook.dto.UpdateGuestbook;
import com.spring.miniguestbook.service.GuestbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// @RestController : 데이터(Json)를 반환하는 컨트롤러 (반환값이 그대로 응답 본문이 됨)
@RestController
// final 필드를 받는 생성자를 자동으로 만들어준다. -> 생성자 주입(DI)에 사용
@RequiredArgsConstructor
// @RequestMapping("/guestbook") : 이 컨트롤러의 모든 경로 앞에 /guestbook 이 붙는다.
@RequestMapping("/guestbook")
public class GuestbookController {
    // 서비스를 주입받아 사용 ( 실제 로직은 컨트롤러가 아니라 서비스에 둔다.)
    private final GuestbookService guestbookService;

    // 등록 : POST /guestbook
    // @RequestBody : 요청 본문 (Json)을 CreateGuestbook 객체로 변환해서 받음
    @PostMapping
    public ResponseEntity<GuestbookResponse> create(@RequestBody CreateGuestbook createGuestbook) {
        GuestbookResponse saved = guestbookService.create(createGuestbook);
        // ResponseEntity : 응답 본문 + 상태 코드를 함께 지정 -> 새로 만들었으니 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 전체 조회 : GET /guestbook -> 200 OK
    @GetMapping
    public ResponseEntity<List<GuestbookResponse>> findAll() {
        List<GuestbookResponse> responseList = guestbookService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    // 단건 조회 : GET /guestbook/{id}
    // @PathVariable : URL 경로의 {id} 값을 파라미터로 받음 ( id에 해당 하는 guestbook이 없으면 서비스가 404를 던짐 )
    @GetMapping("/{id}")
    public ResponseEntity<GuestbookResponse> findOne(@PathVariable Long id) {
        GuestbookResponse guestbookResponse = guestbookService.findOne(id);
        return ResponseEntity.ok(guestbookResponse);
    }

    // 수정 : PUT /guestbook/{id} -> 경로의 id + 본문의 바뀔 값(Json) 둘 다 받음
    @PutMapping("/{id}")
    public ResponseEntity<GuestbookResponse> update(@PathVariable Long id, @RequestBody UpdateGuestbook updateGuestbook) {
        GuestbookResponse guestbookResponse = guestbookService.update(id, updateGuestbook);
        return ResponseEntity.ok(guestbookResponse);
    }

    // 삭제 : DELETE /guestbook/{id} -> 돌려줄 본문이 없으니 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        guestbookService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
