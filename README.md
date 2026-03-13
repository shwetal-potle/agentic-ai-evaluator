# Agentic AI Evaluation Tool

An AI-powered evaluation platform built to automatically judge and score GenAI hackathon submissions. This tool uses a Multi-Agent AI System powered by LangChain4j and Gemini 2.0 to independently evaluate Software Engineering principles, Prompt Quality, Architecture, and GenAI concepts.

## Tech Stack

*   **Backend:** Java 17, Spring Boot 3, Spring Data JPA, MySQL
*   **AI Layer:** LangChain4j, Gemini 2.0, AllMiniLmL6V2 Quantized Embeddings (Local), LangChain4j In-Memory Vector Store
*   **Frontend:** React, Vite, TailwindCSS, Framer Motion, Recharts

---

## 🚀 Getting Started

Follow these steps to run the application locally.

### Prerequisites

*   **Java 17+** installed
*   **Node.js 18+** installed
*   **MySQL Server** running locally on port 3306
*   A valid **Google Gemini 2.0 API Key**

### 1. Database Setup

Ensure your local MySQL server is running. The application will automatically attempt to create the `genai_evaluator` database on startup if it doesn't exist.
*   **Default Username:** `root`
*   **Default Password:** `root`
*(You can change these in `backend/src/main/resources/application.properties`)*

### 2. Configure the Gemini API Key

You must provide a Gemini API key for the AI evaluation agents to work. Open `backend/src/main/resources/application.properties` and replace `provide_your_api_key_here` with your actual key:
```properties
gemini.api.key=YOUR_ACTUAL_API_KEY
```

---

### 3. Running the Backend (Spring Boot)

Navigate to the `backend` directory and run the Spring Boot application using the embedded Maven wrapper:

```bash
cd backend
mvnw.cmd spring-boot:run
```
*(On Mac/Linux, use `./mvnw spring-boot:run`)*

The backend API will start on **http://localhost:8080**.

---

### 4. Running the Frontend (React + Vite)

Open a new terminal window, navigate to the `frontend` directory, install the dependencies, and start the development server:

```bash
cd frontend
npm install
npm run dev
```

The React frontend will be available at **http://localhost:5173**.

---

## 🎯 Usage

1.  Open the frontend dashboard in your browser (`http://localhost:5173`).
2.  Click **"Create First Submission"** (or use the top navigation) to upload a new hackathon project.
3.  Fill out the team name, project title, usecase, architecture, and prompt strategies.
4.  Submit the form. The data will be saved to the database and embedded into the local Vector Store.
5.  On the Evaluation Report page, click **"Trigger Multi-Agent Evaluator"**.
6.  The 5 orchestrated LangChain4j AI Agents will analyze the chunks, score the submission out of 100, and generate a detailed report with radar charts and qualitative feedback!
