package com.genai.evaluator.model;

import jakarta.persistence.*;

@Entity
public class AgentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String agentName; // e.g., "Software Engineering Agent", "GenAI Agent"
    
    private int scoreGiven; // score out of total for that specific agent category

    @Column(columnDefinition = "TEXT")
    private String detailedFeedback;
    
    @Column(columnDefinition = "TEXT")
    private String rubricBreakdownJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_result_id")
    private EvaluationResult evaluationResult;

    public AgentFeedback() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public int getScoreGiven() { return scoreGiven; }
    public void setScoreGiven(int scoreGiven) { this.scoreGiven = scoreGiven; }
    public String getDetailedFeedback() { return detailedFeedback; }
    public void setDetailedFeedback(String detailedFeedback) { this.detailedFeedback = detailedFeedback; }
    public String getRubricBreakdownJson() { return rubricBreakdownJson; }
    public void setRubricBreakdownJson(String rubricBreakdownJson) { this.rubricBreakdownJson = rubricBreakdownJson; }
    public EvaluationResult getEvaluationResult() { return evaluationResult; }
    public void setEvaluationResult(EvaluationResult evaluationResult) { this.evaluationResult = evaluationResult; }
}
