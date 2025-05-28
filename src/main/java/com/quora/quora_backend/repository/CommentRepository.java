package com.quora.quora_backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quora.quora_backend.Entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findByAnswer_Id(Long answerId, Pageable pageable);
    Page<CommentEntity> findByParentComment_Id(Long parentCommentId, Pageable pageable);
    Page<CommentEntity> findByUser_Id(Long userId, Pageable pageable);
}
