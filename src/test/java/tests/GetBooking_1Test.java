package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingDates;
import core.models.CreateBookingResponse;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBooking_1Test {
    private static final Log log = LogFactory.getLog(GetBookingTest.class);
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreateBookingResponse createBookingResponse; // храним созданное бронирование
    private NewBooking newBooking;  // новый объект для создания бронирования
    private int bookingId1;


    // Инициализация API клиента перед каждым тестом
    @BeforeEach
    @DisplayName("предусловия создание бронирования Create )")
    public void setup() throws JsonProcessingException {

        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");

        // Подготовка данных Создаёшь объект Booking с данными
        newBooking = new NewBooking();
        newBooking.setFirstname("Alice");
        newBooking.setLastname("Tester");
        newBooking.setTotalprice(500);
        newBooking.setDepositpaid(true);
        BookingDates dates = new BookingDates("2025-12-01", "2025-12-10");
        newBooking.setBookingdates(dates);
        newBooking.setAdditionalneeds("Early check-in");



        String requestBody = objectMapper.writeValueAsString(newBooking);
        // Отправка запроса
        Response response = apiClient.createBooking2(requestBody);

        // Проверки что статус код=200
        assertThat(response.getStatusCode()).isEqualTo(200);


        // десериализуем тело ответа в объект Booking
        String responseBody = response.asString();
        createBookingResponse = objectMapper.readValue(responseBody, CreateBookingResponse.class);

        //проверяем что booking не пустой
        assertThat(createBookingResponse).isNotNull();
        //сохраняем в переменную bookingId
        bookingId1 = createBookingResponse.getBookingid();
        //System.out.println("bookingId1="+bookingId1);
    }

    @Test
    @DisplayName("Получение списка всех бронирований ( GET /booking )")
    public void getBookingAll() throws JsonProcessingException {

        Response response = apiClient.getBookings();
        assertThat(response.getStatusCode()).isEqualTo(200);
        List<Booking> bookings = response.jsonPath().getList("", Booking.class);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allSatisfy(booking -> assertThat(booking.getBookingid()).isPositive());

    }

    @AfterEach
    @DisplayName("пост условия. Удалить созданное бронирование, отправив DELETE-запрос на\n" +
            "/booking/{bookingid} ")
    public void tearDown() {
        // удалаяем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(bookingId1);


        Response response2 = apiClient.getBookingById(bookingId1);
        assertThat(response2.getStatusCode()).isEqualTo(404);
        assertThat(response2.asString()).contains("Not Found");

    }
}