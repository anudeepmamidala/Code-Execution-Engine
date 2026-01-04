# CodeForge – Secure Code Execution & Interview Evaluation Platform (Backend)

CodeForge is a **backend-focused code execution and interview evaluation platform** built using Spring Boot.  
It securely executes **untrusted user code**, evaluates submissions against testcases, and supports **behavioral interview assessment using the STAR framework**.

This project emphasizes **execution safety, clean architecture, and backend correctness** rather than UI polish.

---

## What This Project Does

CodeForge provides two main systems:

### 1. Code Execution & Evaluation Engine
- Secure execution of user-submitted code
- Multi-testcase evaluation
- Strict execution limits
- Deterministic verdict generation

### 2. Behavioral Interview Evaluation
- STAR-based behavioral questions
- Automated structure-based scoring
- Feedback generation and user statistics

---

## Core Features

### Code Execution Engine
- Secure OS-level execution of **untrusted Python code**
- Isolated temporary directories per execution
- STDIN injection with proper EOF handling
- STDOUT / STDERR capture
- Execution timeout enforcement (2 seconds)
- Forced process termination on timeout
- Output normalization and judging
- Floating-point tolerant comparison
- Output size limiting
- Automatic cleanup of files and processes
- Per-testcase execution tracking
- Aggregated submission verdicts

### Submission Workflow
- User submits code for a problem
- Submission stored with status `PENDING`
- Code executed against all public testcases
- Each testcase result persisted
- Final verdict computed:
  - PASSED
  - FAILED
  - ERROR
  - TIME LIMIT EXCEEDED
- Global execution toggle via configuration

### Testcase Management
- Public and hidden testcases
- Admin-controlled creation, update, and deletion
- Public testcases visible to users
- Hidden testcases reserved for evaluation

### Behavioral Interview Module
- STAR-based behavioral questions
- Category-based question bank
- Automatic STAR scoring:
  - Situation
  - Task
  - Action
  - Result
- Structured feedback generation
- User answer history and statistics
- Average word count tracking

---

## Project Structure

com.example.codeforge
│
├── config # Security and filter configuration
├── controller # REST API endpoints
├── dto # Request / response DTOs
├── entity # JPA entities
├── mapper # Entity ↔ DTO mapping
├── repository # Spring Data JPA repositories
├── security # Authentication support
├── service # Core business logic
└── utils # Utilities (execution engine, JWT, helpers)


Each package has a **single responsibility**, ensuring maintainability and clean separation of concerns.

---

## Code Execution Design

- User code is written to an **isolated temporary directory**
- Execution handled via `ProcessBuilder`
- STDIN injected safely with newline normalization
- Execution time limited to **2 seconds**
- Output size capped to prevent memory abuse
- Processes forcibly destroyed on timeout
- Temporary files deleted after execution

This design assumes **hostile input** by default.

> Note: This is a controlled process-based execution engine, not a container-based sandbox.

---

## Execution Flow

1. User submits code for a problem
2. Submission persisted with status `PENDING`
3. Public testcases fetched
4. For each testcase:
   - Code is executed
   - Output captured
   - Verdict determined
   - Result stored
5. Final submission status computed
6. Response returned to the user

---

## Tech Stack

- **Backend:** Java, Spring Boot
- **Security:** Spring Security, JWT
- **Execution:** OS-level process execution
- **Persistence:** Spring Data JPA
- **Database:** Configurable
- **Build Tool:** Maven / Gradle
- **Frontend:** Minimal React client (API consumer)

---

## My Role & Contributions

- Designed and implemented the **entire code execution pipeline**
- Built secure OS-level execution logic
- Implemented testcase-based judging and aggregation
- Designed submission lifecycle and verdict handling
- Built behavioral interview module with STAR evaluation
- Implemented structured logging for execution tracing
- Enforced execution constraints and cleanup mechanisms

This project focuses on **backend correctness, safety, and clean architecture**.

---

## Running Locally

```bash
git clone <repo-url>
cd codeforge-backend
./mvnw spring-boot:run
