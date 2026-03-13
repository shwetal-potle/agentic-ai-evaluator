package com.genai.evaluator.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorDbService {

    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;

    @Autowired
    public VectorDbService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() {
        this.embeddingStore = new InMemoryEmbeddingStore<>();
    }

    public void embedTextForSubmission(Long submissionId, String textContent) {
        if (textContent == null || textContent.trim().isEmpty()) {
            return;
        }

        // Add metadata so we can isolate contexts per submission when searching
        dev.langchain4j.data.document.Metadata metadata = new dev.langchain4j.data.document.Metadata();
        metadata.put("submissionId", String.valueOf(submissionId));

        Document document = Document.from(textContent, metadata);
        
        // Chunk the document
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(600, 100);
        List<TextSegment> segments = documentSplitter.split(document);

        // Embed and store
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
    }

    public String searchRelevantContext(Long submissionId, String query, int maxResults) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        
        // Find relevant embeddings
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, maxResults, 0.7);
        
        // Filter by submissionId and collect text
        return relevant.stream()
                .filter(match -> String.valueOf(submissionId).equals(match.embedded().metadata().getString("submissionId")))
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n"));
    }
}
