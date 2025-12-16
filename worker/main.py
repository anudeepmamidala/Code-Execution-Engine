from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import logging
import os
from dotenv import load_dotenv
from executor import CodeExecutor, TestcaseData, ExecutionResult

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Create FastAPI app
app = FastAPI(
    title="Code Execution Worker",
    description="Executes user code in isolated sandbox",
    version="1.0.0"
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize code executor
executor = CodeExecutor(
    time_limit=int(os.getenv("TIME_LIMIT", "2000")),
    memory_limit=int(os.getenv("MEMORY_LIMIT", "256"))
)

# ============================================
# REQUEST/RESPONSE MODELS
# ============================================

class TestcaseRequest(BaseModel):
    testcase_id: int
    input: str
    expected_output: str

class ExecutionRequest(BaseModel):
    submission_id: int
    problem_id: int
    code: str
    language: str = "python"
    testcases: List[TestcaseRequest]
    time_limit: Optional[int] = 2000
    memory_limit: Optional[int] = 256

class TestcaseResultResponse(BaseModel):
    testcase_id: int
    passed: bool
    output: str
    error: str
    execution_time: int

class ExecutionResponse(BaseModel):
    submission_id: int
    status: str  # COMPLETED, FAILED
    results: List[TestcaseResultResponse]
    error: Optional[str] = None

# ============================================
# ENDPOINTS
# ============================================

@app.post("/execute", response_model=ExecutionResponse)
async def execute_code(request: ExecutionRequest):
    """
    Execute code against testcases
    
    POST /execute
    {
        "submission_id": 1,
        "problem_id": 1,
        "code": "print(input())",
        "testcases": [
            {
                "testcase_id": 1,
                "input": "hello",
                "expected_output": "hello"
            }
        ]
    }
    """
    logger.info(f"Received execution request for submission {request.submission_id}")
    
    try:
        # Validate language
        if request.language != "python":
            raise HTTPException(
                status_code=400,
                detail=f"Unsupported language: {request.language}. Only 'python' is supported."
            )
        
        # Validate code
        if not request.code or len(request.code) == 0:
            raise HTTPException(
                status_code=400,
                detail="Code cannot be empty"
            )
        
        if len(request.code) > 1000000:  # 1MB limit
            raise HTTPException(
                status_code=400,
                detail="Code is too large (max 1MB)"
            )
        
        # Validate testcases
        if not request.testcases or len(request.testcases) == 0:
            raise HTTPException(
                status_code=400,
                detail="At least one testcase is required"
            )
        
        logger.info(f"Executing code with {len(request.testcases)} testcases")
        
        # Convert to executor format
        testcases = [
            TestcaseData(
                testcase_id=tc.testcase_id,
                input=tc.input,
                expected_output=tc.expected_output
            )
            for tc in request.testcases
        ]
        
        # Execute code
        results = executor.execute_code(request.code, testcases)
        
        logger.info(f"Execution completed for submission {request.submission_id}")
        
        # Convert results to response format
        result_responses = [
            TestcaseResultResponse(
                testcase_id=r.testcase_id,
                passed=r.passed,
                output=r.output,
                error=r.error,
                execution_time=r.execution_time
            )
            for r in results
        ]
        
        return ExecutionResponse(
            submission_id=request.submission_id,
            status="COMPLETED",
            results=result_responses
        )
        
    except HTTPException as e:
        logger.error(f"HTTP Error: {e.detail}")
        raise
    except Exception as e:
        logger.error(f"Unexpected error: {str(e)}")
        return ExecutionResponse(
            submission_id=request.submission_id,
            status="FAILED",
            results=[],
            error=str(e)
        )

@app.get("/health")
async def health_check():
    """Health check endpoint"""
    logger.info("Health check requested")
    return {
        "status": "ok",
        "service": "code-execution-worker",
        "version": "1.0.0"
    }

@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "name": "Code Execution Worker",
        "version": "1.0.0",
        "endpoints": {
            "execute": "POST /execute - Execute code against testcases",
            "health": "GET /health - Health check"
        }
    }

# ============================================
# STARTUP/SHUTDOWN
# ============================================

@app.on_event("startup")
async def startup_event():
    logger.info("Worker service starting up...")
    logger.info(f"Time limit: {executor.time_limit}s")
    logger.info(f"Memory limit: {executor.memory_limit}MB")

@app.on_event("shutdown")
async def shutdown_event():
    logger.info("Worker service shutting down...")

# ============================================
# ERROR HANDLERS
# ============================================

@app.exception_handler(Exception)
async def general_exception_handler(request, exc):
    logger.error(f"Unhandled exception: {str(exc)}")
    return {
        "status": "error",
        "message": str(exc)
    }

if __name__ == "__main__":
    import uvicorn
    
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "8000"))
    
    logger.info(f"Starting worker on {host}:{port}")
    
    uvicorn.run(app, host=host, port=port)