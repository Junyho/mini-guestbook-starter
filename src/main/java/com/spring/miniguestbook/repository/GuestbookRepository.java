package com.spring.miniguestbook.repository;

import com.spring.miniguestbook.entity.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;

// jpaRepository<Entity, PK 타입>를 상속 받아서 사용하게 되면
// 기본 CURD 메서드들을 쓸 수 있다. (save,findAll,findById,delete)
public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
}
