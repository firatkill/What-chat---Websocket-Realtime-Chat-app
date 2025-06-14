package com.whatchat.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //null olan yerleri json'da gizler
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private String error;
    private T data;

}