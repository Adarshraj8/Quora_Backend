package com.quora.quora_backend.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quora.quora_backend.Dto.AnswerSummaryDTO;
import com.quora.quora_backend.Dto.CommentDTO;
import com.quora.quora_backend.Dto.CommentInputDTO;
import com.quora.quora_backend.Dto.CommentSummaryDTO;
import com.quora.quora_backend.Dto.UserSummaryDTO;
import com.quora.quora_backend.Entity.AnswerEntity;
import com.quora.quora_backend.Entity.CommentEntity;
import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.Exception.ResourceNotFoundException;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.CommentRepository;
import com.quora.quora_backend.repository.UserRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, 
                         AnswerRepository answerRepository, 
                         UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    public CommentDTO createComment(CommentInputDTO commentInput, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        AnswerEntity answer = answerRepository.findById(commentInput.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", commentInput.getAnswerId()));

        CommentEntity comment = new CommentEntity();
        comment.setContent(commentInput.getContent());
        comment.setUser(user);
        comment.setAnswer(answer);

        if (commentInput.getParentCommentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentInput.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentInput.getParentCommentId()));
            comment.setParentComment(parentComment);
        }

        CommentEntity savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    public CommentDTO getCommentById(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        return convertToDTO(comment);
    }

    public Page<CommentDTO> getCommentsByAnswer(Long answerId, Pageable pageable) {
        return commentRepository.findByAnswer_Id(answerId, pageable)
                .map(this::convertToDTO);
    }

    public Page<CommentDTO> getRepliesByComment(Long parentCommentId, Pageable pageable) {
        return commentRepository.findByParentComment_Id(parentCommentId, pageable)
                .map(this::convertToDTO);
    }

    public Page<CommentDTO> getCommentsByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUser_Id(userId, pageable)
                .map(this::convertToDTO);
    }

    public CommentDTO updateComment(Long commentId, CommentInputDTO commentInput, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own comments");
        }

        comment.setContent(commentInput.getContent());
        CommentEntity updatedComment = commentRepository.save(comment);
        return convertToDTO(updatedComment);
    }

    public void deleteComment(Long commentId, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentDTO convertToDTO(CommentEntity comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        
        UserSummaryDTO userSummary = new UserSummaryDTO();
        userSummary.setId(comment.getUser().getId());
        userSummary.setUsername(comment.getUser().getUsername());
        dto.setUser(userSummary);
        
        if (comment.getAnswer() != null) {
            AnswerSummaryDTO answerSummary = new AnswerSummaryDTO();
            answerSummary.setId(comment.getAnswer().getId());
            answerSummary.setContentPreview(comment.getAnswer().getContent().length() > 50 
                ? comment.getAnswer().getContent().substring(0, 50) + "..."
                : comment.getAnswer().getContent());
            dto.setAnswer(answerSummary);
        }
        
        if (comment.getParentComment() != null) {
            CommentSummaryDTO parentCommentSummary = new CommentSummaryDTO();
            parentCommentSummary.setId(comment.getParentComment().getId());
            parentCommentSummary.setContentPreview(comment.getParentComment().getContent().length() > 50 
                ? comment.getParentComment().getContent().substring(0, 50) + "..."
                : comment.getParentComment().getContent());
            dto.setParentComment(parentCommentSummary);
        }
        
        dto.setReplyCount(comment.getReplies() != null ? comment.getReplies().size() : 0);
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        
        return dto;
    }
}