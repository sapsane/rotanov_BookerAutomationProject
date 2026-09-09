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

public class PutReservationUpdateTest {
    private static final Log log = LogFactory.getLog(GetBookingTest.class);
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreateBookingResponse createBookingResponse; // храним созданное бронирование
    private NewBooking newBooking;  // новый объект для создания бронирования
    private NewBooking newBooking2;
    private int bookingId1;



    @BeforeEach
    @DisplayName("предусловия создание бронирования Create )")
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
        //сохраняем в переменную bookingId = getBookingid()
        bookingId1 = createBookingResponse.getBookingid();

    }
    @Test
    @DisplayName("Обновление бронирования ( PUT /booking/{id} )")
    public void putReservationUpdate() throws JsonProcessingException {
        // Подготовка данных Создаёшь объект Booking с данными - обновленные данные
        newBooking2 = new NewBooking();
        newBooking2.setFirstname("Alice2");
        newBooking2.setLastname("Tester2");
        newBooking2.setTotalprice(700);
        newBooking2.setDepositpaid(true);
        BookingDates dates = new BookingDates("2025-12-01", "2025-12-10");
        newBooking2.setBookingdates(dates);
        newBooking2.setAdditionalneeds("Early check-in");


        String requestBody = objectMapper.writeValueAsString(newBooking2);
        // Отправка запроса
        int existingId=bookingId1; //присваеваем значение  bookingId1 из теста на создание

        Response response = apiClient.putBooking(existingId,requestBody); //присваеваем значение и создаем put запрос

        assertThat(response.getStatusCode()).isEqualTo(200);


        // сверяем ответы из response  и request  put  запроса
        NewBooking fromServer = response.as(NewBooking.class);
        assertThat(fromServer.getFirstname()).isEqualTo(newBooking2.getFirstname());
        assertThat(fromServer.getLastname()).isEqualTo(newBooking2.getLastname());
        assertThat(fromServer.getTotalprice()).isEqualTo(newBooking2.getTotalprice());
        assertThat(fromServer.getDepositpaid()).isEqualTo(newBooking2.getDepositpaid());
        assertThat(fromServer.getBookingdates().getCheckin()).isEqualTo(newBooking2.getBookingdates().getCheckin());
        assertThat(fromServer.getBookingdates().getCheckout()).isEqualTo(newBooking2.getBookingdates().getCheckout());
        assertThat(fromServer.getAdditionalneeds()).isEqualTo(newBooking2.getAdditionalneeds());
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
