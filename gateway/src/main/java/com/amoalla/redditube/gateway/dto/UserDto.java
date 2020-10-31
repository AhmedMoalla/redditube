package com.amoalla.redditube.gateway.dto;

import com.amoalla.redditube.gateway.validation.FieldMatch;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldMatch(first = "password", second = "confirmPassword", message = "must match with Password")
public class UserDto extends RegisteredUserDto {
    @NotEmpty
    @Size(min = 8, message = "must at least be 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "must contain at least one uppercase letter, one lowercase letter and one number")
    private String password;
    @NotEmpty
    private String confirmPassword;
}
