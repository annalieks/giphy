package com.bsa.bsa_giphy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserIdDto {
    @Pattern(regexp = "^[\\w\\-. ]+$")
    @Size(min = 1, max = 260)
    private final String userId;
}
