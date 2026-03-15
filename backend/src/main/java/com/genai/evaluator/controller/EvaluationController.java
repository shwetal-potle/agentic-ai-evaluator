package com.genai.evaluator.controller;

import com.genai.evaluator.model.Submission;
import com.genai.evaluator.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final SubmissionService submissionService;
    private final com.genai.evaluator.service.MultiAgentOrchestratorService orchestratorService;

    public EvaluationController(SubmissionService submissionService, com.genai.evaluator.service.MultiAgentOrchestratorService orchestratorService) {
        this.submissionService = submissionService;
        this.orchestratorService = orchestratorService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Submission> createSubmission(
            @RequestParam("submission") String submissionJson,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile,
            @RequestPart(value = "zipFile", required = false) MultipartFile zipFile) {
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Submission submission = objectMapper.readValue(submissionJson, Submission.class);
            Submission saved = submissionService.saveSubmission(submission, pdfFile, zipFile);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Submission>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmission(@PathVariable Long id) {
        return submissionService.getSubmissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/evaluate")
    public ResponseEntity<com.genai.evaluator.model.EvaluationResult> evaluateSubmission(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orchestratorService.evaluateSubmission(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
