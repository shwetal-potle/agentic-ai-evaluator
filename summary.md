# 🤖 SentriSolve: Multi-Agent Autonomous Support Orchestrator (Agentic AI Evaluation Tool)
## Comprehensive Project Documentation

---

## 📖 1. Executive Summary
The **Agentic AI Evaluation Tool (SentriSolve)** is a robust, cutting-edge, full-stack application engineered to autonomously evaluate, analyze, and score complex Generative AI Hackathon submissions. 

Rather than relying on human judges to manually parse through hundreds of project descriptions, architectures, and codebases, this application utilizes a **Multi-Agent Orchestration System** powered by LangChain4j and Google's Gemini 2.0 Flash model. The system ingests standard text, PDF architecture diagrams, and raw source code (via ZIP), processes them using Retrieval-Augmented Generation (RAG), and delegates the evaluation to a committee of specialized AI Agents.

---

## 🏗️ 2. Architectural Blueprint & Technology Stack

### 💾 2.1 Backend Core (Java & Spring Boot)
The backend is a monolithic Spring Boot application designed with clean architecture principles. It manages the RESTful API endpoints, persistent storage, AI orchestration, and complex file processing.
*   **Core Framework:** Java 17, Spring Boot 3.3.5
*   **Database Integration:** Spring Data JPA with Hibernate, storing data natively in MySQL 8.
*   **Connection Pooling:** HikariCP for resilient database connection management.
*   **AI Integration Framework:** LangChain4j (v0.35.0) for declarative AI service definitions, model binding, and RAG capabilities.
*   **LLM Provider:** Google Gemini API (`gemini-2.0-flash`), interfaced natively through LangChain4j.

### 🎨 2.2 Frontend Client (React & Vite)
The frontend provides a state-of-the-art UI focused on usability and aesthetics. It features a "glassmorphism" dark theme with layered gradients, micro-animations, and dynamic data rendering.
*   **Build Tool & Framework:** Vite, React 18
*   **Styling Engine:** Tailwind CSS 3.4.17
*   **Iconography & Animation:** `lucide-react` for crisp SVG icons, `framer-motion` for complex entry and exit UI animations.
*   **Routing & HTTP:** `react-router-dom` for Single Page Application (SPA) functionality, `axios` for HTTP multipart form submission and REST interaction.

---

## ⚙️ 3. Core System Components & Flow

### 📂 3.1 Project Intake & Advanced Parsing Engine
When a team submits a project via the Frontend (`/upload`), the submission is intercepted by the Backend's `EvaluationController`.

**Payload Processing:**
The user uploads a `multipart/form-data` request containing:
1.  **JSON Payload:** `teamName`, `projectTitle`, `useCaseDescription`, `architectureDetails`, `promptStrategy`.
2.  **PDF File:** A system architecture or design document.
3.  **ZIP File:** The actual source code of the project.

**Deep Extraction (`SubmissionService.java`):**
Instead of just saving the text to a database, the system cracks open the submitted files:
*   **PDF Processing:** Leverages `ApachePdfBoxDocumentParser` from LangChain4j to extract raw, unstructured text content from the submitted PDF diagrams or whitepapers.
*   **ZIP Processing:** Utilizes standard Java `ZipInputStream` to uncompress the folder in memory. It specifically filters out non-code assets and extracts the raw string content of `.java`, `.py`, `.js`, `.ts`, `.md`, and `.yml` files.

### 🧠 3.2 Retrieval-Augmented Generation (RAG) System
Once all the text is extracted from the user's manual inputs, the PDFs, and the Code ZIP, it is passed into the `VectorDbService`.

*   **Chunking:** The massive concatenated string of code and text is chopped into intelligent, overlapping segments using LangChain4j's `DocumentSplitters.recursive(600, 100)` (600 max tokens/characters, 100 token overlap).
*   **Embedding:** These chunks are converted into dense mathematical vectors (embeddings) using the Gemini Embedding Model.
*   **Storage:** The vectors are stored persistently in an `InMemoryEmbeddingStore`. They are tagged with a specific `submissionId` metadata tag to ensure that when the AI searches for context, it doesn't accidentally pull code from a completely different team's hackathon project.

### 🤖 3.3 The Multi-Agent Orchestration System
The beating heart of the application is the `MultiAgentOrchestratorService`. When an evaluation is triggered, this service orchestrates five entirely independent AI personas.

*For every single agent invocation, the orchestrator queries the internal Vector DB to retrieve the top 10 most relevant chunks of context (Code + PDFs + Text) dynamically.*

1.  👷 **Software Engineering Agent (`SoftwareEngineeringAgent.java`)**
    *   **Role:** Analyzes the architecture, database design, design patterns, and systemic scalability.
    *   **Max Score:** 50 Points.
    *   **Output Form:** Strict JSON containing `scoreGiven`, `detailedFeedback`, and an array-based `rubricBreakdown`.

2.  🧠 **GenAI & Prompting Agent (`GenAIAgent.java`)**
    *   **Role:** Grades how efficiently the team utilized LLMs, the quality of their RAG pipelines, context windows, and chunking strategies.
    *   **Max Score:** 50 Points.
    *   **Output Form:** Strict JSON similar to the SE Agent.

3.  📝 **Prompt Quality Agent (`PromptQualityAgent.java`)**
    *   **Role:** Acts as a specialized auditor. It looks purely at the raw System Prompts and User Prompts submitted by the team. It checks for common vulnerabilities (prompt injection, jailbreaking risks) and clarity.
    *   **Max Score:** Observational (0 Points assigned directly).

4.  🔍 **Code Analysis Agent (`CodeAnalysisAgent.java`)**
    *   **Role:** Sifts through the extracted `.java`/`.py`/`.js` files from the ZIP. It looks for dirty code, tight coupling, hardcoded secrets, and missing error-handling loops.
    *   **Max Score:** Observational (0 Points assigned directly).

5.  ⚖️ **Final Scoring & Head Judge Agent (`FinalScoringAgent.java`)**
    *   **Role:** The Head Judge does not look at the project itself. Instead, it reads the JSON feedback generated by the first four Agents. It consolidates their findings, resolves any conflicting opinions (e.g., Code Agent says bad error handling, GenAI Agent says great prompt handling), and generates a conclusive, final Markdown verdict for the team.

### 🛡️ 3.4 Resilience & Throttling (API Quota Management)
Since the system fires 5 very complex, context-heavy LLM queries sequentially, it easily triggers rate limits on free-tier API accounts (e.g., `429 RESOURCE_EXHAUSTED`). 

To combat this, the Orchestrator employs:
1.  **Static Pacing:** A mandatory 4000ms (4-second) thread sleep between each of the 5 agent invocations to ensure sustained Requests Per Minute (RPM) limits are respected.
2.  **Exponential Backoff Retries (`invokeWithRetry`):** If a `429` error is caught during an active generation, the thread silently swallows the exception gracefully, sleeps for 15 seconds, and attempts the API call again. If it fails a second time, it waits 30 seconds.

---

## 🗄️ 4. Data Models & Entity Relationships

The relational database strictly defines the output and input states of the orchestrations:

1.  **`Submission` Entity:** Maps the core input. Contains fields for `teamName`, `projectTitle`, `promptStrategy`, and `architectureDetails`.
2.  **`EvaluationResult` Entity:** A `@OneToOne` mapping relative to the Submission. This holds the final generated numbers: `overallScore`, `softwareEngineeringScore`, `genAiScore`, and the Head Judge's `finalRecommendation` text block.
3.  **`AgentFeedback` Entity:** A `@OneToMany` collection attached to the `EvaluationResult`. Each record represents the direct, unedited JSON/Markdown output produced by one of the 4 sub-agents (`SoftwareEngineeringAgent`, `GenAIAgent`, etc.).

---

## 🎨 5. Frontend User Interface breakdown

*   **`index.css` & `tailwind.config.js`:** Establishes the core variables for the UI, defining the deep slate background (`#0A0A0B`), frosted card bases (`bg-white/5` with `backdrop-blur-xl`), and vibrant primary accents (`emerald-400` to `teal-500`).
*   **`App.jsx`:** The router shell. Navigates between the Dashboard, the Upload Form, and the Evaluation Report View.
*   **`Dashboard.jsx`:** Fetches all submissions from `/api/submissions`. Renders them in grid cards. Maps the `EvaluationResult.overallScore` to dynamic colors (Green for >80, Amber for >60, Red for <60).
*   **`SubmissionUpload.jsx`:** The intake form. It collects string states and attaches the `pdfFile` and `zipFile` variables. It bypasses classic JSON payload conventions and mounts everything onto an HTML5 `FormData` object explicitly typed to `multipart/form-data`.
*   **`EvaluationReport.jsx`:** The final viewing phase. Uses Framer Motion to stagger the entrance of the scores. Displays the Head Judge's verdict at the top, followed by interactive breakout cards for each individual Agent's specialized feedback markdown.

---

## 🚀 6. Execution & Deployment Summary

1.  **Backend Startup:** Executed via Maven `mvnw.cmd spring-boot:run`. Needs a configured JDK 17 explicitly inside the IDE/Terminal `JAVA_HOME`.
2.  **Database Bootstrapping:** Flyway/Hibernate automatically generates the schema inside the `genai_evaluator` MySQL database. 
3.  **Frontend Startup:** Executed via Vite `npm run dev` on port `5175`. 
4.  **Integration Point:** The frontend `api.js` points to `localhost:8080/api/submissions`. The backend resolves CORS automatically and accepts the multipart objects, converting them via an explicitly scoped Jackson `ObjectMapper` to bypass HTTP 415 serialization bugs.
