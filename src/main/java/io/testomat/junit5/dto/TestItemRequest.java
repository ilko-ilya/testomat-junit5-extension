package io.testomat.junit5.dto;

public record TestItemRequest(

        String status,      // "passed", "failed", "skipped"
        String title,
        String test_id,     // з нашої анотації @TestId
        String message,
        String stack,
        Long run_time

) {
}
