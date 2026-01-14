package io.testomat.junit5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestRunResponse(

        String uid

) {
}
