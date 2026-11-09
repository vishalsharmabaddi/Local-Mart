package com.localmart.backend.common;

import java.util.Map;

public record ValidationErrorResponse(int status, String message, Map<String, String> errors) {
}
