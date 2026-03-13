package com.genai.evaluator.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface SoftwareEngineeringAgent {

    @SystemMessage({
        "You are an expert Software Architecture and Engineering Judge evaluating a Hackathon submission.",
        "Your goal is to evaluate the Software Engineering Implementation Quality out of 50 marks.",
        "Use the following rubric:",
        "- Requirements Engineering (10 marks)",
        "- Architecture (10 marks)",
        "- Design Principles (10 marks)",
        "- Implementation Quality (10 marks)",
        "- Testing & Security (10 marks)",
        "Review the provided Context of the project.",
        "Output ONLY a JSON string with three fields:",
        "1. 'scoreGiven' (integer out of 50)",
        "2. 'detailedFeedback' (string)",
        "3. 'rubricBreakdown' (stringified JSON explaining marks per category)"
    })
    @UserMessage("Evaluate the following project.\nContext:\n{{context}}\nUse Case Description:\n{{useCase}}\nArchitecture Details:\n{{architecture}}")
    String evaluateSoftwareEngineering(
            @V("context") String context,
            @V("useCase") String useCase,
            @V("architecture") String architecture
    );
}
