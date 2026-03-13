package com.genai.evaluator.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int overallScore;
    private int softwareEngineeringScore;
    private int genAiScore;

    @Column(columnDefinition = "TEXT")
    private String finalRecommendation;

    @OneToOne
    @JoinColumn(name = "submission_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Submission submission;

    @OneToMany(mappedBy = "evaluationResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgentFeedback> agentFeedbacks = new ArrayList<>();

    public EvaluationResult() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public int getSoftwareEngineeringScore() { return softwareEngineeringScore; }
    public void setSoftwareEngineeringScore(int softwareEngineeringScore) { this.softwareEngineeringScore = softwareEngineeringScore; }
    public int getGenAiScore() { return genAiScore; }
    public void setGenAiScore(int genAiScore) { this.genAiScore = genAiScore; }
    public String getFinalRecommendation() { return finalRecommendation; }
    public void setFinalRecommendation(String finalRecommendation) { this.finalRecommendation = finalRecommendation; }
    public Submission getSubmission() { return submission; }
    public void setSubmission(Submission submission) { this.submission = submission; }
    public List<AgentFeedback> getAgentFeedbacks() { return agentFeedbacks; }
    public void setAgentFeedbacks(List<AgentFeedback> agentFeedbacks) { this.agentFeedbacks = agentFeedbacks; }
}
