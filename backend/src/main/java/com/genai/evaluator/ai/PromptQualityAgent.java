package com.genai.evaluator.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface PromptQualityAgent {

    @SystemMessage({
        "You are a Prompt Engineering Expert.",
        "Your goal is to evaluate the prompts provided by the user in their Hackathon project.",
        "Check for structured prompting, chain-of-thought, system prompts, role-based prompting, and context density.",
        "You do not provide a numerical score, but rather a robust qualitative assessment of the prompt strategy.",
        "Output ONLY a JSON string with one field:",
        "1. 'feedback' (string containing detailed feedback on prompt quality)"
    })
    @UserMessage("Evaluate the following prompts and strategy.\nStrategy:\n{{prompts}}\nContext:\n{{context}}")
    String evaluatePromptQuality(
            @V("prompts") String prompts,
            @V("context") String context
    );
}
