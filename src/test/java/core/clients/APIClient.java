package core.clients;

import core.models.Booking;
import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
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
    private  String token;

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
                .header("Accept", "application/json")
                .filter(addAuthTokenFilter());
    }
    public void createToken(String username,String password){
        //Тело запроса для получения токена
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\" }",username,password);

        Response response = getRequestSpec()
                .body(requestBody)
                .when()
                .post(ApiEndpoints.AUTH.getPath()) // используем ENUM для эндпойнта /auth
                .then()
                .statusCode(200) //ожидаемый статус код 200 ок
                .extract()
                .response();
                // извлекаем токен из ответа
        token = response.jsonPath().getString("token");
    }

    private Filter addAuthTokenFilter(){
        return (FilterableRequestSpecification requestSpec,
                FilterableResponseSpecification responceSpec,
                FilterContext ctx) -> {
            if (token != null) {
                requestSpec.header("Cookie", "token=" + token);
            }
            return ctx.next(requestSpec, responceSpec);
        };
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
                .log().all()
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
                .log().all()
                .when()
                .post(ApiEndpoints.BOOKING.getPath())
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response deleteBooking(int bookingId){
        return getRequestSpec()
                .pathParam("id",bookingId) // указываем path parametr для ID
                .when()
                .delete(ApiEndpoints.BOOKING.getPath() +"/{id}")  //используем параметр пути в запросе
                .then()
                .log().all()
                .statusCode(201)  // предполагаемый код ответа
                .extract()
                .response();
    }
    public Response createBooking2(String newBooking) {
        return getRequestSpec()
                .body(newBooking)
                .log().all()
                .when()
                .post(ApiEndpoints.BOOKING.getPath())
                .then()
                .log().all()
                .extract()
                .response();
    }
    public Response putBooking(int bookingId,String newBooking) {
        return getRequestSpec()
                .body(newBooking)
                .pathParam("id", bookingId) // указываем path parametr для ID
                .when()
                .put(ApiEndpoints.BOOKING.getPath() + "/{id}")  //используем параметр пути в запросе
                .then()
                .log().all()
                .extract()
                .response();
    }
    public Response patchBooking (int bookingId,String newBooking){
        return getRequestSpec()
                .body(newBooking)
                .pathParam("id", bookingId) // указываем path parametr для ID
                .when()
                .patch(ApiEndpoints.BOOKING.getPath() + "/{id}")  //используем параметр пути в запросе
                .then()
                .log().all()
                .extract()
                .response();

    }
}


