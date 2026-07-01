package com.spring.miniguestbook.service;

import com.spring.miniguestbook.dto.CreateGuestbook;
import com.spring.miniguestbook.dto.GuestbookResponse;
import com.spring.miniguestbook.dto.UpdateGuestbook;
import com.spring.miniguestbook.entity.Guestbook;
import com.spring.miniguestbook.repository.GuestbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

// 서비스 계층임을 알려준다: 비지니스 로직을 담당하는 계층
@Service
// final 필드를 받아오는 생성자 자동 생성 -> 의존성 주입
@RequiredArgsConstructor
public class GuestbookService {
    private final GuestbookRepository guestbookRepository;

    // 전체 메서드를 하나의 트랜잭션으로 묶는다. 중간에 예외가 나면 DB 작업이 전부 롤백된다.
    // 등록 : 요청 dto -> entity로 변환 후 저장 -> 저장된 결과를 응답 dto로 변환해 반환하는 메서드
    @Transactional
    public GuestbookResponse create(CreateGuestbook createGuestbook) {

        // 1) 요청 dto의 값으로 엔터티 생성
        Guestbook guestbook = new Guestbook(
                createGuestbook.getName(),
                createGuestbook.getMessage()
        );

        // 2) db에 저장 (save가 id, 생성일이 채워진 엔터티를 돌려줌)
        Guestbook savedGuestbook = guestbookRepository.save(guestbook);


        // 3) 엔티티 값을 응답 dto에 담아서 반환
        return new GuestbookResponse(
                savedGuestbook.getId(),
                savedGuestbook.getName(),
                savedGuestbook.getMessage(),
                savedGuestbook.getCreatedAt(),
                savedGuestbook.getModifiedAt()
        );
    }

    // 전체 조회 : 엔터티 목록을 받아와서 dto 목록으로 반환
    // readOnly 변경 감지 같은 불필요한 작업을 줄요 조회 성능에 이점이 있다.
    @Transactional(readOnly = true)
    public List<GuestbookResponse> findAll() {
        List<GuestbookResponse> guestbookResponseList = new ArrayList<>();
        List<Guestbook> guestbookList = guestbookRepository.findAll();

        for (Guestbook guestbook : guestbookList) {
            GuestbookResponse guestbookResponse = new GuestbookResponse(
                    guestbook.getId(),
                    guestbook.getName(),
                    guestbook.getMessage(),
                    guestbook.getCreatedAt(),
                    guestbook.getModifiedAt()
            );
            guestbookResponseList.add(guestbookResponse);
        }
        return guestbookResponseList;
    }

    // 단건 조회: id로 하나 찾아 응답 dto로 변환 (없으면 getOrThrow가 404(NOT FOUND)
    // 조회만 하므로 읽기 전용 트랜잭션
    @Transactional(readOnly = true)
    public GuestbookResponse findOne(Long id) {
        Guestbook guestbook = getOrThrow(id);

        return new GuestbookResponse(
                guestbook.getId(),
                guestbook.getName(),
                guestbook.getMessage(),
                guestbook.getCreatedAt(),
                guestbook.getModifiedAt()
        );

    }

    // 수정 : 기존 글을 찾아 값만 바꾼 뒤 다시 저장
    // 조회 -> 변경 -> 저장을 한 묶음으로. 도중 실패 시 전부 롤백
    @Transactional
    public GuestbookResponse update(Long id, UpdateGuestbook updateGuestbook) {
        Guestbook guestbook = getOrThrow(id);

        guestbook.update(
                updateGuestbook.getName(),
                updateGuestbook.getMessage()
        );

        Guestbook updatedGuestbook = guestbookRepository.save(guestbook);

        return new GuestbookResponse(
                updatedGuestbook.getId(),
                updateGuestbook.getName(),
                updateGuestbook.getMessage(),
                updatedGuestbook.getCreatedAt(),
                updatedGuestbook.getModifiedAt()
        );
    }

    // 삭제 : 먼저 존재 여부를 확인 (없으면 404) 한 뒤 삭제
    // 조회와 삭제를 하나의 트랜잭션으로 묶음
    @Transactional
    public void delete(Long id) {
        Guestbook guestbook = getOrThrow(id);
        guestbookRepository.deleteById(id);
    }


    //내부 공통 메서드 : id로 엔터티를 찾고, 없으면 404(ResponseStatusException)를 던짐
    // private 라서 외부에서 못 쓰고, 이 서비스 안에서 중복을 줄이는 용도!
    private Guestbook getOrThrow(Long id) {
        return guestbookRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "방명록을 찾을 수 없어요: " + id));
    }
}
