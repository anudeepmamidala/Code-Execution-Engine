package com.example.codeforge.dto.submission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitCodeRequest {
    private Long problemId;
    private String code;
}
