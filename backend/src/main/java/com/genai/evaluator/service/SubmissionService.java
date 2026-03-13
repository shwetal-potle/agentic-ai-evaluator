package com.genai.evaluator.service;

import com.genai.evaluator.model.Submission;
import com.genai.evaluator.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final VectorDbService vectorDbService;

    public SubmissionService(SubmissionRepository submissionRepository, VectorDbService vectorDbService) {
        this.submissionRepository = submissionRepository;
        this.vectorDbService = vectorDbService;
    }

    public Submission saveSubmission(Submission submission, MultipartFile pdfFile, MultipartFile zipFile) {
        // 1. Save to DB
        Submission savedSubmission = submissionRepository.save(submission);

        // 2. Embed content into Vector DB
        StringBuilder textToEmbed = new StringBuilder();
        textToEmbed.append("Use Case Description: \n").append(savedSubmission.getUseCaseDescription()).append("\n\n");
        textToEmbed.append("Architecture Details: \n").append(savedSubmission.getArchitectureDetails()).append("\n\n");
        textToEmbed.append("Prompt Strategy: \n").append(savedSubmission.getPromptStrategy()).append("\n\n");

        // Parse PDF Document if provided
        if (pdfFile != null && !pdfFile.isEmpty()) {
            try (InputStream is = pdfFile.getInputStream()) {
                Document pdfDoc = new ApachePdfBoxDocumentParser().parse(is);
                textToEmbed.append("EXTRACTED PDF DOCUMENTATION:\n").append(pdfDoc.text()).append("\n\n");
            } catch (Exception e) {
                // Log and continue if PDF parsing fails
                System.err.println("Failed to parse PDF document: " + e.getMessage());
            }
        }

        // Parse Source Code ZIP if provided
        if (zipFile != null && !zipFile.isEmpty()) {
            textToEmbed.append("EXTRACTED SOURCE CODE FILES:\n");
            try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        String name = entry.getName();
                        // Filter for common code and text files
                        if (name.matches(".*\\.(java|js|jsx|ts|tsx|py|md|txt|html|css|json|xml|yaml|yml)$")) {
                            textToEmbed.append("File: ").append(name).append("\n```\n");
                            String content = new String(zis.readAllBytes(), StandardCharsets.UTF_8);
                            textToEmbed.append(content).append("\n```\n\n");
                        }
                    }
                    zis.closeEntry();
                }
            } catch (Exception e) {
                 System.err.println("Failed to extract ZIP source code: " + e.getMessage());
            }
        }

        vectorDbService.embedTextForSubmission(savedSubmission.getId(), textToEmbed.toString());

        return savedSubmission;
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Optional<Submission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }
}
