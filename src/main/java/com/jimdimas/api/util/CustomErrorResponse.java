package com.jimdimas.api.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomErrorResponse{

    private String message;

}
