package com.quora.quora_backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quora.quora_backend.Entity.AnswerEntity;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    Page<AnswerEntity> findByQuestion_Id(Long questionId, Pageable pageable);
    Page<AnswerEntity> findByUser_Id(Long userId, Pageable pageable);
}

