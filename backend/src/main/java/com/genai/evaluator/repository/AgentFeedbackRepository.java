package com.genai.evaluator.repository;

import com.genai.evaluator.model.AgentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentFeedbackRepository extends JpaRepository<AgentFeedback, Long> {
    List<AgentFeedback> findByEvaluationResultId(Long evaluationResultId);
}
