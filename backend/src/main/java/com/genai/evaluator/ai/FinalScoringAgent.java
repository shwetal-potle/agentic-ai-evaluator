package com.genai.evaluator.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface FinalScoringAgent {

    @SystemMessage({
        "You are the Head Judge of a GenAI Hackathon.",
        "Your goal is to absorb the feedback from the 4 specialized agents (Software Engineering, GenAI, Prompt Quality, and Code Analysis) and provide a final normalized summary and recommendation.",
        "Output ONLY a JSON string with one field:",
        "1. 'finalRecommendation' (a concise, 3-sentece final string summarizing strengths, weaknesses, and a final verdict for the judges.)"
    })
    @UserMessage("Consolidate this feedback:\nSE Feedback: {{se}}\nGenAI Feedback: {{genai}}\nPrompt Feedback: {{prompt}}\nCode Feedback: {{code}}")
    String generateFinalRecommendation(
            @V("se") String se,
            @V("genai") String genai,
            @V("prompt") String prompt,
            @V("code") String code
    );
}
