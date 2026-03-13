package com.genai.evaluator.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;
    private String projectTitle;
    
    @Column(columnDefinition = "TEXT")
    private String useCaseDescription;
    
    @Column(columnDefinition = "TEXT")
    private String architectureDetails;
    
    @Column(columnDefinition = "TEXT")
    private String promptStrategy;
    
    @CreationTimestamp
    private LocalDateTime submittedAt;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EvaluationResult evaluationResult;

    public Submission() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
    public String getUseCaseDescription() { return useCaseDescription; }
    public void setUseCaseDescription(String useCaseDescription) { this.useCaseDescription = useCaseDescription; }
    public String getArchitectureDetails() { return architectureDetails; }
    public void setArchitectureDetails(String architectureDetails) { this.architectureDetails = architectureDetails; }
    public String getPromptStrategy() { return promptStrategy; }
    public void setPromptStrategy(String promptStrategy) { this.promptStrategy = promptStrategy; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public EvaluationResult getEvaluationResult() { return evaluationResult; }
    public void setEvaluationResult(EvaluationResult evaluationResult) { this.evaluationResult = evaluationResult; }
}
