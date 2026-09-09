package tests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
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

import static org.assertj.core.api.Assertions.assertThat;


public class patchReservationUpdate {
    private static final Log log = LogFactory.getLog(GetBookingTest.class);
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreateBookingResponse createBookingResponse; // храним созданное бронирование
    private NewBooking newBooking;  // новый объект для создания бронирования
    private NewBooking newBooking2;
    private int bookingId1;
    private NewBooking requestBooking2;

    @BeforeEach
    @DisplayName("Частичное обновление бронирования ( PATCH/booking/{id} )")
    public void setup() throws JsonProcessingException {

        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");

        // Подготовка данных Создаёшь объект Booking с данными - это твой эталон
        newBooking = new NewBooking();
        newBooking.setFirstname("Alice");
        newBooking.setLastname("Tester");
        newBooking.setTotalprice(500);
        newBooking.setDepositpaid(true);
        BookingDates dates = new BookingDates("2025-12-01", "2025-12-10");
        newBooking.setBookingdates(dates);
        newBooking.setAdditionalneeds("Early check-in");

        requestBooking2 = newBooking;

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
    @DisplayName("Частичное обновление бронирования (PATCH /booking/{id})")
    public void putReservationUpdate() throws JsonProcessingException {

        // Подготовка данных Создаёшь объект NewBooking с данными - обновленные данные
        newBooking2 = new NewBooking();
        newBooking2.setFirstname("Alice2");

        requestBooking2 = newBooking2;
        String requestBody = objectMapper.writeValueAsString(newBooking2);
        // Отправка запроса
        int existingId=bookingId1; //присваеваем значение  bookingId1 из теста на создание

        Response response = apiClient.patchBooking(existingId,requestBody);

        assertThat(response.getStatusCode()).isEqualTo(200);

        // сверяем ответы из response  и request  patch  запроса
        NewBooking fromServer = response.as( NewBooking.class);
        assertThat(fromServer.getFirstname()).isEqualTo(requestBooking2.getFirstname());
        assertThat(fromServer.getLastname()).isEqualTo(newBooking.getLastname());
        assertThat(fromServer.getTotalprice()).isEqualTo(newBooking.getTotalprice());
        assertThat(fromServer.getDepositpaid()).isEqualTo(newBooking.getDepositpaid());
        assertThat(fromServer.getBookingdates().getCheckin()).isEqualTo(newBooking.getBookingdates().getCheckin());
        assertThat(fromServer.getBookingdates().getCheckout()).isEqualTo(newBooking.getBookingdates().getCheckout());
        assertThat(fromServer.getAdditionalneeds()).isEqualTo(newBooking.getAdditionalneeds());
    }
    @AfterEach
    public void tearDown(){
        // удалаяем созданное бронирование
        apiClient.createToken("admin","password123");
        apiClient.deleteBooking(bookingId1);

        Response response2 = apiClient.getBookingById(bookingId1);
        assertThat(response2.getStatusCode()).isEqualTo(404);
        assertThat(response2.asString()).contains("Not Found");

    }
}
