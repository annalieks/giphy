package com.bsa.bsa_giphy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "date", "query", "gif" })
public class History {
    @JsonProperty("date")
    private String date;
    @JsonProperty("query")
    private String query;
    @JsonProperty("gif")
    private Path gif;
}
