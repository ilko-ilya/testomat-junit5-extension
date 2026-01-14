package io.testomat.junit5.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.testomat.junit5.dto.TestItemRequest;
import io.testomat.junit5.dto.TestRunRequest;
import io.testomat.junit5.dto.TestRunResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TestomatClient {

    private static final String BASE_URL = "https://app.testomat.io/api/reporter";
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TestomatClient() {
        // Зчитуємо API-ключ згідно з ТЗ
        this.apiKey = System.getenv("TESTOMATIO");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Створює новий Run в Testomat.io та повертає його UID
     */
    public String createRun(TestRunRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);

            // Отримуємо ключ із системи
            String apiKey = System.getenv("TESTOMATIO");
            System.out.println(">>> DEBUG: Використовуємо ключ: " + apiKey);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://app.testomat.io/api/reporter?api_key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(">>> DEBUG: Status Code: " + response.statusCode());
            System.out.println(">>> DEBUG: Response Body: " + response.body());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                TestRunResponse res = objectMapper.readValue(response.body(), TestRunResponse.class);
                return res.uid();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Відправляє результат конкретного тесту
     */
    public void submitTestResult(String uid, TestItemRequest request) {
        if (uid == null || apiKey == null) return;

        try {
            String body = objectMapper.writeValueAsString(request);
            String url = BASE_URL + "/" + uid + "/testrun?api_key=" + apiKey;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // ДОДАНО: Логування для перевірки
            System.out.println(">>> DEBUG (Submit Test): Status " + response.statusCode());
        } catch (Exception e) {
            System.err.println("Failed to submit test result: " + e.getMessage());
        }
    }

    /**
     * Завершує Run
     */
    public void finishRun(String uid) {
        if (uid == null || apiKey == null) return;

        try {
            String body = "{\"status_event\": \"finish\"}";
            String url = BASE_URL + "/" + uid + "?api_key=" + apiKey;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(">>> DEBUG (Finish Run): Status " + response.statusCode());
        } catch (Exception e) {
            System.err.println("Failed to finish TestRun: " + e.getMessage());
        }
    }

}
