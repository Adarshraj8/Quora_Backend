package com.quora.quora_backend.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quora.quora_backend.Dto.AnswerDTO;
import com.quora.quora_backend.Dto.AnswerInputDTO;
import com.quora.quora_backend.Dto.QuestionSummaryDTO;
import com.quora.quora_backend.Dto.UserSummaryDTO;
import com.quora.quora_backend.Entity.AnswerEntity;
import com.quora.quora_backend.Entity.QuestionEntity;
import com.quora.quora_backend.Entity.UserEntity;
import com.quora.quora_backend.Exception.ResourceNotFoundException;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public AnswerService(AnswerRepository answerRepository, 
                        QuestionRepository questionRepository, 
                        UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public AnswerDTO createAnswer(AnswerInputDTO answerInput, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        QuestionEntity question = questionRepository.findById(answerInput.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", answerInput.getQuestionId()));

        AnswerEntity answer = new AnswerEntity();
        answer.setContent(answerInput.getContent());
        answer.setUser(user);
        answer.setQuestion(question);

        AnswerEntity savedAnswer = answerRepository.save(answer);
        return convertToDTO(savedAnswer);
    }

    public AnswerDTO getAnswerById(Long answerId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));
        return convertToDTO(answer);
    }

    public Page<AnswerDTO> getAnswersByQuestion(Long questionId, Pageable pageable) {
        return answerRepository.findByQuestion_Id(questionId, pageable)
                .map(this::convertToDTO);
    }

    public Page<AnswerDTO> getAnswersByUser(Long userId, Pageable pageable) {
        return answerRepository.findByUser_Id(userId, pageable)
                .map(this::convertToDTO);
    }

    public AnswerDTO updateAnswer(Long answerId, AnswerInputDTO answerInput, Long userId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));

        if (!answer.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own answers");
        }

        answer.setContent(answerInput.getContent());
        AnswerEntity updatedAnswer = answerRepository.save(answer);
        return convertToDTO(updatedAnswer);
    }

    public void deleteAnswer(Long answerId, Long userId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));

        if (!answer.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own answers");
        }

        answerRepository.delete(answer);
    }

    @Transactional
    public void likeAnswer(Long answerId, Long userId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (answer.getLikedByUsers().contains(user)) {
            throw new IllegalArgumentException("You have already liked this answer");
        }

        answer.getLikedByUsers().add(user);
        user.getLikedAnswers().add(answer);
        
        answerRepository.save(answer);
        userRepository.save(user);
    }

    @Transactional
    public void unlikeAnswer(Long answerId, Long userId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!answer.getLikedByUsers().contains(user)) {
            throw new IllegalArgumentException("You have not liked this answer");
        }

        answer.getLikedByUsers().remove(user);
        user.getLikedAnswers().remove(answer);
        
        answerRepository.save(answer);
        userRepository.save(user);
    }

    private AnswerDTO convertToDTO(AnswerEntity answer) {
        AnswerDTO dto = new AnswerDTO();
        dto.setId(answer.getId());
        dto.setContent(answer.getContent());
        
        UserSummaryDTO userSummary = new UserSummaryDTO();
        userSummary.setId(answer.getUser().getId());
        userSummary.setUsername(answer.getUser().getUsername());
        dto.setUser(userSummary);
        
        QuestionSummaryDTO questionSummary = new QuestionSummaryDTO();
        questionSummary.setId(answer.getQuestion().getId());
        questionSummary.setTitle(answer.getQuestion().getTitle());
        dto.setQuestion(questionSummary);
        
        dto.setCommentCount(answer.getComments().size());
        dto.setLikeCount(answer.getLikedByUsers().size());
        dto.setCreatedAt(answer.getCreatedAt());
        dto.setUpdatedAt(answer.getUpdatedAt());
        
        return dto;
    }
}