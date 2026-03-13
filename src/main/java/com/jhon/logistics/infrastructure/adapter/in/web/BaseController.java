package com.jhon.logistics.infrastructure.adapter.in.web;

import com.jhon.logistics.application.dto.response.global.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    protected <T> ResponseEntity<ApiResponse<List<T>>> okList(List<T> data, String message) {
        return ResponseEntity.ok(ApiResponse.successList(data, message));
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(data, message));
    }

    protected <T> ResponseEntity<ApiResponse<T>> noContent(String message) {
        return ResponseEntity.ok(ApiResponse.noContent(message));
    }
}