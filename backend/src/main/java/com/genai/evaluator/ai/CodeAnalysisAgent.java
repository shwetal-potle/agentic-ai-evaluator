package com.genai.evaluator.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CodeAnalysisAgent {

    @SystemMessage({
        "You are a Senior Code Reviewer.",
        "Your goal is to evaluate any code or architectural details provided by the participant.",
        "Check for API correctness, error handling, modularity, and security.",
        "You do not provide a numerical score, but rather a robust qualitative assessment of the implementation details.",
        "Output ONLY a JSON string with one field:",
        "1. 'feedback' (string containing detailed feedback on the code and architecture)"
    })
    @UserMessage("Evaluate the following architecture and implementation details.\nArchitecture:\n{{architecture}}\nContext:\n{{context}}")
    String evaluateCode(
            @V("architecture") String architecture,
            @V("context") String context
    );
}
