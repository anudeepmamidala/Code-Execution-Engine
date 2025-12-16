package com.example.codeforge.client;

import com.example.codeforge.dto.ExecutionJobRequest;
import com.example.codeforge.dto.ExecutionJobResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@Slf4j
public class WorkerClient {
    
    @Value("${worker.service.url:http://localhost:8000}")
    private String workerServiceUrl;
    
    @Value("${worker.service.timeout:30000}")
    private long workerTimeout; // milliseconds
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public WorkerClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Send execution job to worker and wait for results
     */
    public ExecutionJobResponse executeCode(ExecutionJobRequest jobRequest) {
        log.info("Sending execution job to worker for submission: {}", jobRequest.getSubmissionId());
        
        try {
            // Convert request to JSON
            String requestBody = objectMapper.writeValueAsString(jobRequest);
            
            log.debug("Job request: {}", requestBody);
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(workerServiceUrl + "/execute"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofMillis(workerTimeout))
                    .build();
            
            // Send request and get response
            log.info("Calling worker at: {}/execute", workerServiceUrl);
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            log.debug("Worker response status: {}", response.statusCode());
            
            // Check response status
            if (response.statusCode() != 200) {
                log.error("Worker returned error status: {}", response.statusCode());
                log.error("Response: {}", response.body());
                throw new RuntimeException("Worker execution failed with status: " + response.statusCode());
            }
            
            // Parse response
            ExecutionJobResponse jobResponse = objectMapper.readValue(response.body(), 
                    ExecutionJobResponse.class);
            
            log.info("Execution completed for submission: {} with status: {}", 
                    jobRequest.getSubmissionId(), jobResponse.getStatus());
            
            return jobResponse;
            
        } catch (java.net.http.HttpTimeoutException e) {
            log.error("Worker execution timed out after {} ms", workerTimeout);
            throw new RuntimeException("Code execution timed out. Limit: " + workerTimeout + "ms");
        } catch (InterruptedException e) {
            log.error("Worker request interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("Code execution was interrupted");
        } catch (Exception e) {
            log.error("Error communicating with worker: {}", e.getMessage(), e);
            throw new RuntimeException("Error executing code: " + e.getMessage());
        }
    }
    
    /**
     * Health check for worker service
     */
    public boolean isWorkerAvailable() {
        try {
            log.debug("Checking worker availability at: {}/health", workerServiceUrl);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(workerServiceUrl + "/health"))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            boolean available = response.statusCode() == 200;
            log.info("Worker health check: {}", available ? "OK" : "DOWN");
            return available;
            
        } catch (Exception e) {
            log.warn("Worker health check failed: {}", e.getMessage());
            return false;
        }
    }
}