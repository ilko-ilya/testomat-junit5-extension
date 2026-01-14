package io.testomat.junit5;

import io.testomat.junit5.annotations.TestId;
import io.testomat.junit5.annotations.Title;
import io.testomat.junit5.client.TestomatClient;
import io.testomat.junit5.dto.TestItemRequest;
import io.testomat.junit5.dto.TestRunRequest;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class TestomatExtension implements
        BeforeAllCallback,
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        AfterAllCallback {

    private final TestomatClient client = new TestomatClient();
    private String runId;

    // Ключі для збереження даних у внутрішньому сховищі JUnit
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(TestomatExtension.class);
    private static final String START_TIME = "start_time";

    @Override
    public void beforeAll(ExtensionContext context) {
        System.out.println(">>> ТЕСТОМАТ РОЗШИРЕННЯ ЗАПУЩЕНО! <<<");

        TestRunRequest runRequest = new TestRunRequest(
                "JUnit 5 Run: " + context.getDisplayName(),
                "Java",
                System.getProperty("os.name")
        );
        runId = client.createRun(runRequest);

        if (runId == null || runId.isEmpty()) {
            System.err.println("❌ ПОМИЛКА: Run не був створений! Перевір API-ключ або мережу.");
        } else {
            System.out.println("✅ Run успішно створено! ID: " + runId);
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        // Фіксуємо час початку тесту для розрахунку run_time
        context.getStore(NAMESPACE).put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (runId == null) return;

        // 2. Збираємо дані про результат тесту
        var method = context.getRequiredTestMethod();

        // Зчитуємо наші кастомні анотації @TestId та @Title
        String testId = Optional.ofNullable(method.getAnnotation(TestId.class))
                .map(TestId::value)
                .orElse("");

        String title = Optional.ofNullable(method.getAnnotation(Title.class))
                .map(Title::value)
                .orElse(context.getDisplayName());

        // Визначаємо статус
        String status = context.getExecutionException().isPresent() ? "failed" : "passed";

        // Обробка помилок (якщо є)
        String message = "";
        String stack = "";
        if (context.getExecutionException().isPresent()) {
            Throwable ex = context.getExecutionException().get();
            message = ex.getMessage();
            stack = getStackTrace(ex);
        }

        // Розрахунок часу виконання
        long startTime = context.getStore(NAMESPACE).get(START_TIME, long.class);
        long duration = System.currentTimeMillis() - startTime;

        // Формуємо запит
        TestItemRequest itemRequest = new TestItemRequest(
                status,
                title,
                testId,
                message,
                stack,
                duration
        );

        // Відправляємо результат конкретного тесту в API
        client.submitTestResult(runId, itemRequest);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // 3. Завершуємо Run після всіх тестів
        if (runId != null) {
            client.finishRun(runId);
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
