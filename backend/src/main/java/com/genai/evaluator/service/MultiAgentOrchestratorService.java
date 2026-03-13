package com.genai.evaluator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.evaluator.ai.*;
import com.genai.evaluator.model.AgentFeedback;
import com.genai.evaluator.model.EvaluationResult;
import com.genai.evaluator.model.Submission;
import com.genai.evaluator.repository.EvaluationResultRepository;
import com.genai.evaluator.repository.SubmissionRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MultiAgentOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(MultiAgentOrchestratorService.class);

    private final ChatLanguageModel chatModel;
    private final VectorDbService vectorDbService;
    private final EvaluationResultRepository evaluationResultRepository;
    private final SubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;

    private SoftwareEngineeringAgent seAgent;
    private GenAIAgent genAiAgent;
    private PromptQualityAgent promptAgent;
    private CodeAnalysisAgent codeAgent;
    private FinalScoringAgent finalAgent;

    public MultiAgentOrchestratorService(ChatLanguageModel chatModel, VectorDbService vectorDbService, 
                                         EvaluationResultRepository evaluationResultRepository, 
                                         SubmissionRepository submissionRepository, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.vectorDbService = vectorDbService;
        this.evaluationResultRepository = evaluationResultRepository;
        this.submissionRepository = submissionRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        this.seAgent = AiServices.builder(SoftwareEngineeringAgent.class).chatLanguageModel(chatModel).build();
        this.genAiAgent = AiServices.builder(GenAIAgent.class).chatLanguageModel(chatModel).build();
        this.promptAgent = AiServices.builder(PromptQualityAgent.class).chatLanguageModel(chatModel).build();
        this.codeAgent = AiServices.builder(CodeAnalysisAgent.class).chatLanguageModel(chatModel).build();
        this.finalAgent = AiServices.builder(FinalScoringAgent.class).chatLanguageModel(chatModel).build();
    }

    @Transactional
    public EvaluationResult evaluateSubmission(Long submissionId) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isEmpty()) {
            throw new IllegalArgumentException("Submission not found");
        }
        
        Submission submission = submissionOpt.get();

        // Check if already evaluated
        Optional<EvaluationResult> existing = evaluationResultRepository.findBySubmissionId(submissionId);
        if (existing.isPresent()) {
            return existing.get(); // Or clear it and run again, but let's just return to avoid API costs
        }

        // 1. Retrieve RAG context
        String context = vectorDbService.searchRelevantContext(submission.getId(), "architecture, design, prompts, use case", 10);
        
        EvaluationResult result = new EvaluationResult();
        result.setSubmission(submission);
        result.setAgentFeedbacks(new ArrayList<>());
        
        try {
            log.info("Starting Software Engineering Evaluation for submission ID: {}", submission.getId());
            String seJson = invokeWithRetry(() -> seAgent.evaluateSoftwareEngineering(context, submission.getUseCaseDescription(), submission.getArchitectureDetails()));
            JsonNode seNode = cleanAndParse(seJson);
            int seScore = seNode.has("scoreGiven") ? seNode.get("scoreGiven").asInt() : 0;
            result.setSoftwareEngineeringScore(seScore);
            result.getAgentFeedbacks().add(createFeedback("Software Engineering Agent", seScore, seNode, result));
            sleepSilently(4000);

            log.info("Starting GenAI Evaluation");
            String genAiJson = invokeWithRetry(() -> genAiAgent.evaluateGenAI(context, submission.getUseCaseDescription(), submission.getPromptStrategy()));
            JsonNode genAiNode = cleanAndParse(genAiJson);
            int genAiScore = genAiNode.has("scoreGiven") ? genAiNode.get("scoreGiven").asInt() : 0;
            result.setGenAiScore(genAiScore);
            result.getAgentFeedbacks().add(createFeedback("GenAI Agent", genAiScore, genAiNode, result));
            sleepSilently(4000);

            log.info("Starting Prompt Quality Evaluation");
            String promptJson = invokeWithRetry(() -> promptAgent.evaluatePromptQuality(submission.getPromptStrategy(), context));
            JsonNode promptNode = cleanAndParse(promptJson);
            result.getAgentFeedbacks().add(createFeedback("Prompt Quality Agent", 0, promptNode, result));
            sleepSilently(4000);

            log.info("Starting Code Analysis Evaluation");
            String codeJson = invokeWithRetry(() -> codeAgent.evaluateCode(submission.getArchitectureDetails(), context));
            JsonNode codeNode = cleanAndParse(codeJson);
            result.getAgentFeedbacks().add(createFeedback("Code Analysis Agent", 0, codeNode, result));
            sleepSilently(4000);

            log.info("Starting Final Recommendation");
            String seFeedback = seNode.has("detailedFeedback") ? seNode.get("detailedFeedback").asText() : "";
            String genFeedback = genAiNode.has("detailedFeedback") ? genAiNode.get("detailedFeedback").asText() : "";
            String prFeedback = promptNode.has("feedback") ? promptNode.get("feedback").asText() : "";
            String cdFeedback = codeNode.has("feedback") ? codeNode.get("feedback").asText() : "";
            
            String finalJson = invokeWithRetry(() -> finalAgent.generateFinalRecommendation(seFeedback, genFeedback, prFeedback, cdFeedback));
            JsonNode finalNode = cleanAndParse(finalJson);
            
            result.setOverallScore(seScore + genAiScore);
            result.setFinalRecommendation(finalNode.has("finalRecommendation") ? finalNode.get("finalRecommendation").asText() : "");

        } catch (Exception e) {
            log.error("Evaluation failed", e);
            result.setFinalRecommendation("Evaluation failed due to an error: " + e.getMessage());
        }

        // Save and return
        return evaluationResultRepository.save(result);
    }

    private JsonNode cleanAndParse(String jsonStr) throws Exception {
        // Remove markdown formatting if LLM adds it
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("```json")) {
            jsonStr = jsonStr.substring(7);
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
        }
        return objectMapper.readTree(jsonStr.trim());
    }

    private void sleepSilently(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String invokeWithRetry(java.util.function.Supplier<String> agentCall) {
        int maxRetries = 3;
        long delayMs = 15000; // 15 seconds initial delay if we hit a rate limit
        for (int i = 0; i < maxRetries; i++) {
            try {
                return agentCall.get();
            } catch (Exception e) {
                if (e.getMessage() != null && (e.getMessage().contains("429") || e.getMessage().contains("RESOURCE_EXHAUSTED") || e.getMessage().contains("Quota"))) {
                    log.warn("Rate limit hit (429/Quota). Retrying in {}ms... (Attempt {} of {})", delayMs, i + 1, maxRetries);
                    sleepSilently(delayMs);
                    delayMs *= 2; // exponential backoff (15s, 30s, 60s...)
                } else {
                    throw e; // rethrow if it's not a rate limit error
                }
            }
        }
        return agentCall.get(); // final attempt and throw if it fails
    }

    private AgentFeedback createFeedback(String agentName, int score, JsonNode node, EvaluationResult result) {
        AgentFeedback feedback = new AgentFeedback();
        feedback.setAgentName(agentName);
        feedback.setScoreGiven(score);
        feedback.setEvaluationResult(result);
        
        if (node.has("detailedFeedback")) {
            feedback.setDetailedFeedback(node.get("detailedFeedback").asText());
        } else if (node.has("feedback")) {
            feedback.setDetailedFeedback(node.get("feedback").asText());
        }
        
        if (node.has("rubricBreakdown")) {
            feedback.setRubricBreakdownJson(node.get("rubricBreakdown").toString());
        }
        
        return feedback;
    }
}
