package io.testomat.junit5.dto;

public record TestRunRequest(

        String title,
        String group,
        String env

) {
}
