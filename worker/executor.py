import subprocess
import os
import tempfile
import logging
from typing import List, Dict, Optional
from dataclasses import dataclass
import signal

logger = logging.getLogger(__name__)

@dataclass
class TestcaseData:
    testcase_id: int
    input: str
    expected_output: str

@dataclass
class ExecutionResult:
    testcase_id: int
    passed: bool
    output: str
    error: str
    execution_time: int  # milliseconds

class CodeExecutor:
    """Execute user code safely in isolated subprocess"""
    
    def __init__(self, time_limit: int = 2000, memory_limit: int = 256):
        """
        Args:
            time_limit: Execution timeout in milliseconds
            memory_limit: Memory limit in MB (not enforced on all OS)
        """
        self.time_limit = time_limit / 1000  # Convert to seconds
        self.memory_limit = memory_limit
        
    def execute_code(self, code: str, testcases: List[TestcaseData]) -> List[ExecutionResult]:
        """
        Execute code against testcases
        
        Args:
            code: Python code to execute
            testcases: List of testcases to run against
            
        Returns:
            List of execution results
        """
        results = []
        
        try:
            # Create temporary file for user code
            with tempfile.NamedTemporaryFile(mode='w', suffix='.py', delete=False) as f:
                f.write(code)
                temp_file = f.name
            
            logger.info(f"Executing code from {temp_file}")
            
            # Run against each testcase
            for testcase in testcases:
                result = self._run_testcase(temp_file, testcase)
                results.append(result)
            
            return results
            
        except Exception as e:
            logger.error(f"Error in execute_code: {str(e)}")
            # Return error for all testcases
            return [
                ExecutionResult(
                    testcase_id=tc.testcase_id,
                    passed=False,
                    output="",
                    error=f"Execution error: {str(e)}",
                    execution_time=0
                )
                for tc in testcases
            ]
        finally:
            # Clean up temp file
            if os.path.exists(temp_file):
                try:
                    os.remove(temp_file)
                except:
                    pass
    
    def _run_testcase(self, code_file: str, testcase: TestcaseData) -> ExecutionResult:
        """Run single testcase against code file"""
        
        try:
            import time
            start_time = time.time()
            
            # Run Python code with input
            process = subprocess.Popen(
                ['python', code_file],
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True
            )
            
            try:
                # Run with timeout
                stdout, stderr = process.communicate(
                    input=testcase.input,
                    timeout=self.time_limit
                )
                
                execution_time = int((time.time() - start_time) * 1000)  # milliseconds
                
                # Compare output
                expected = testcase.expected_output.strip()
                actual = stdout.strip()
                
                passed = expected == actual
                
                logger.info(f"Testcase {testcase.testcase_id}: {'PASS' if passed else 'FAIL'}")
                
                return ExecutionResult(
                    testcase_id=testcase.testcase_id,
                    passed=passed,
                    output=actual,
                    error=stderr if stderr else "",
                    execution_time=execution_time
                )
                
            except subprocess.TimeoutExpired:
                logger.warning(f"Testcase {testcase.testcase_id}: TIMEOUT")
                process.kill()
                
                return ExecutionResult(
                    testcase_id=testcase.testcase_id,
                    passed=False,
                    output="",
                    error=f"Execution timeout (limit: {self.time_limit}s)",
                    execution_time=int(self.time_limit * 1000)
                )
                
        except Exception as e:
            logger.error(f"Error running testcase {testcase.testcase_id}: {str(e)}")
            
            return ExecutionResult(
                testcase_id=testcase.testcase_id,
                passed=False,
                output="",
                error=f"Error: {str(e)}",
                execution_time=0
            )