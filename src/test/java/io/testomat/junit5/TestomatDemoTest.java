package io.testomat.junit5;

import io.testomat.junit5.annotations.TestId;
import io.testomat.junit5.annotations.Title;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestomatDemoTest {

    @Test
    @TestId("101")
    @Title("Проверка успешного входа")
    void successTest() {
        assertTrue(true);
    }

    @Test
    @TestId("102")
    @Title("Тест с ошибкой для отчета")
    void failedTest() {
        throw new RuntimeException("Специальная ошибка для проверки стектрейса");
    }
}
