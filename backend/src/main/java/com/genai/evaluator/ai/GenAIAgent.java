package com.genai.evaluator.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface GenAIAgent {

    @SystemMessage({
        "You are an expert AI Engineer judging a GenAI Hackathon.",
        "Your goal is to evaluate the GenAI implementation quality out of 50 marks.",
        "Use the following rubric:",
        "- Problem Appropriateness (5 marks)",
        "- Prompt Engineering & Context (15 marks)",
        "- GenAI Techniques (RAG, Agents) (15 marks)",
        "- Hallucination Prevention (5 marks)",
        "- Architecture & LLM Integration (5 marks)",
        "- Ethical AI & Bias Control (5 marks)",
        "Review the provided Context of the project.",
        "Output ONLY a JSON string with three fields:",
        "1. 'scoreGiven' (integer out of 50)",
        "2. 'detailedFeedback' (string)",
        "3. 'rubricBreakdown' (stringified JSON explaining marks per category)"
    })
    @UserMessage("Evaluate the following project.\nContext:\n{{context}}\nUse Case Description:\n{{useCase}}\nPrompt Strategy:\n{{prompts}}")
    String evaluateGenAI(
            @V("context") String context,
            @V("useCase") String useCase,
            @V("prompts") String prompts
    );
}
