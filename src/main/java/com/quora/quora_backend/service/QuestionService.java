package com.quora.quora_backend.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quora.quora_backend.Dto.QuestionDTO;
import com.quora.quora_backend.Dto.QuestionInputDTO;
import com.quora.quora_backend.Dto.QuestionSummaryDTO;
import com.quora.quora_backend.Dto.UserSummaryDTO;
import com.quora.quora_backend.Entity.QuestionEntity;
import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.Exception.ResourceNotFoundException;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public QuestionDTO createQuestion(QuestionInputDTO questionInput, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        QuestionEntity question = new QuestionEntity();
        question.setTitle(questionInput.getTitle());
        question.setDescription(questionInput.getDescription());
        question.setUser(user);

        QuestionEntity savedQuestion = questionRepository.save(question);
        return convertToDTO(savedQuestion);
    }

    public QuestionDTO getQuestionById(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        return convertToDTO(question);
    }

    public Page<QuestionDTO> getQuestionsByUser(Long userId, Pageable pageable) {
        return questionRepository.findByUser_Id(userId, pageable)
                .map(this::convertToDTO);
    }

    public Page<QuestionDTO> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public QuestionDTO updateQuestion(Long questionId, QuestionInputDTO questionInput, Long userId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own questions");
        }

        question.setTitle(questionInput.getTitle());
        question.setDescription(questionInput.getDescription());

        QuestionEntity updatedQuestion = questionRepository.save(question);
        return convertToDTO(updatedQuestion);
    }

    public void deleteQuestion(Long questionId, Long userId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own questions");
        }

        questionRepository.delete(question);
    }

    @Transactional
    public void likeQuestion(Long questionId, Long userId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (question.getLikedByUsers().contains(user)) {
            throw new IllegalArgumentException("You have already liked this question");
        }

        question.getLikedByUsers().add(user);
        user.getLikedQuestions().add(question);
        
        questionRepository.save(question);
        userRepository.save(user);
    }

    @Transactional
    public void unlikeQuestion(Long questionId, Long userId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!question.getLikedByUsers().contains(user)) {
            throw new IllegalArgumentException("You have not liked this question");
        }

        question.getLikedByUsers().remove(user);
        user.getLikedQuestions().remove(question);
        
        questionRepository.save(question);
        userRepository.save(user);
    }

    private QuestionDTO convertToDTO(QuestionEntity question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        
        UserSummaryDTO userSummary = new UserSummaryDTO();
        userSummary.setId(question.getUser().getId());
        userSummary.setUsername(question.getUser().getUsername());
        dto.setUser(userSummary);
        
        dto.setAnswerCount(question.getAnswers().size());
        dto.setLikeCount(question.getLikedByUsers().size());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        
        return dto;
    }

    public QuestionSummaryDTO getQuestionSummary(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        QuestionSummaryDTO summary = new QuestionSummaryDTO();
        summary.setId(question.getId());
        summary.setTitle(question.getTitle());
        
        return summary;
    }
}