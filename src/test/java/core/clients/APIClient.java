package core.clients;

import core.models.Booking;
import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Клиент знает, КУДА и КАК сходить. Он не решает, какой ответ считается правильным -
 * это работа теста, поэтому здесь нет ни одной проверки статус-кода.
 */
public class APIClient {

    private final String baseUrl;

    public APIClient() {
        this.baseUrl = determineBaseUrl();
    }

    // Определение базового URL на основе файла конфигурации
    private String determineBaseUrl() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file: " + configFileName, e);
        }
        return properties.getProperty("baseUrl");
    }

    // Настройка базовых параметров HTTP-запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    /** GET /ping */
    public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.PING.getPath());
    }

    /** GET /booking - список всех бронирований (только id). */
    public Response getBookings() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath());
    }

    /** GET /booking/{id} - конкретное бронирование. */
    public Response getBookingById(int id) {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPathById(id));
    }

    public Response createBooking(Booking booking) {
        return getRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING.getPath());
    }
}


