package com.amoalla.redditube.gateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RegistrationResponseDto {
    private RegisteredUserDto user;
    private List<String> errors = new ArrayList<>();

    public RegistrationResponseDto(RegisteredUserDto user) {
        this.user = user;
    }

    public RegistrationResponseDto(String errorMessage) {
        user = null;
        errors.add(errorMessage);
    }

    public RegistrationResponseDto(List<ObjectError> errors) {
        user = null;
        this.errors.addAll(createErrorMessages(errors));
    }

    private List<String> createErrorMessages(List<ObjectError> errors) {
        return errors.stream()
                .map(FieldError.class::cast)
                .map(this::createErrorMessage)
                .collect(Collectors.toList());
    }

    private String createErrorMessage(FieldError error) {
        String fieldName = error.getField();
        fieldName = ParsingUtils.reconcatenateCamelCase(fieldName, " ");
        fieldName = StringUtils.capitalize(fieldName);
        return fieldName + " " + error.getDefaultMessage();
    }
}
