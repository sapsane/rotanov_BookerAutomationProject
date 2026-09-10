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

public class GetBookingWithFlterTest {
    private static final Log log = LogFactory.getLog(GetBookingTest.class);
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreateBookingResponse createBookingResponse1; // храним созданное бронирование
    private CreateBookingResponse createBookingResponse2;
    private NewBooking newBooking1;  // новый объект для создания бронирования
    private int bookingId1;
    private int bookingId2;
    private NewBooking newBooking2;
    private NewBooking requestBooking2;
    private NewBooking requestBooking1;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");

        // Подготовка данных Создаёшь объект Booking с данными - это твой эталон
        newBooking1 = new NewBooking();
        newBooking1.setFirstname("Alice1");
        newBooking1.setLastname("Tester1");
        newBooking1.setTotalprice(500);
        newBooking1.setDepositpaid(true);
        BookingDates dates1 = new BookingDates("2025-12-01", "2025-12-10");
        newBooking1.setBookingdates(dates1);
        newBooking1.setAdditionalneeds("Early check-in");

        requestBooking1 = newBooking1;


        String requestBody1 = objectMapper.writeValueAsString(newBooking1);
        // Отправка запроса
        Response response1 = apiClient.createBooking2(requestBody1);

        // Проверки что статус код=200
        assertThat(response1.getStatusCode()).isEqualTo(200);

        // десериализуем тело ответа в объект Booking
        String responseBody1 = response1.asString();
        createBookingResponse1 = objectMapper.readValue(responseBody1, CreateBookingResponse.class);

        //проверяем что booking не пустой
        assertThat(createBookingResponse1).isNotNull();
        //сохраняем в переменную bookingId
        bookingId1 = createBookingResponse1.getBookingid();
        //System.out.println("bookingId1="+bookingId1);
//--------------------------------------------------------
        // Подготовка данных Создаёшь объект Booking с данными
        newBooking2 = new NewBooking();
        newBooking2.setFirstname("Alice2");
        newBooking2.setLastname("Tester2");
        newBooking2.setTotalprice(500);
        newBooking2.setDepositpaid(true);
        BookingDates dates2 = new BookingDates("2025-12-01", "2025-12-10");
        newBooking2.setBookingdates(dates2);
        newBooking2.setAdditionalneeds("Early check-in");

        requestBooking2 = newBooking2;


        String requestBody2 = objectMapper.writeValueAsString(newBooking2);
        // Отправка запроса
        Response response2 = apiClient.createBooking2(requestBody2);

        // Проверки что статус код=200
        assertThat(response2.getStatusCode()).isEqualTo(200);


        // десериализуем тело ответа в объект Booking
        String responseBody2 = response2.asString();
        createBookingResponse2 = objectMapper.readValue(responseBody2, CreateBookingResponse.class);

        //проверяем что booking не пустой
        assertThat(createBookingResponse2).isNotNull();
        //сохраняем в переменную bookingId
        bookingId2 = createBookingResponse2.getBookingid();
        //System.out.println("bookingId1="+bookingId1);

    }
    @Test
    @DisplayName("Получение бронирований с фильтрацией по параметру Firstname")
    public void getBookingWithFirstname() throws JsonProcessingException {
        // Отправка запроса // фильтрация по Firstname
        Response response3 = apiClient.getBookingIdWithFirstname(requestBooking1.getFirstname());
        assertThat(response3.getStatusCode()).isEqualTo(200);

        //выбираем один bookingid из списка
        int existingId1 = apiClient.getBookingIdWithFirstname(requestBooking1.getFirstname())
                .jsonPath().getList("bookingid", Integer.class).get(0);
        //System.out.println("existingId="+existingId);

        //Тело ответа содержит только те бронирования, которые соответствуют
        //фильтрующим параметрам.
        Response response21 = apiClient.getBookingById(existingId1);
        NewBooking fromServer1 = response21.as(NewBooking.class);
        assertThat(fromServer1.getFirstname()).isEqualTo(requestBooking1.getFirstname());
    }

    @Test
    @DisplayName("Получение бронирований с фильтрацией по праметру Lastname")
    public void getBookingWithLastname() throws JsonProcessingException {
        Response response4 = apiClient.getBookingIdWithLastname(requestBooking2.getLastname());
        assertThat(response4.getStatusCode()).isEqualTo(200);

        //----//выбираем один bookingid из списка---------------------
        int existingId2 = apiClient.getBookingIdWithLastname(requestBooking2.getLastname())
                .jsonPath().getList("bookingid", Integer.class).get(0);
        //--Тело ответа содержит только те бронирования, которые соответствуют
        //фильтрующим параметрам.--------------
        Response response22 = apiClient.getBookingById(existingId2);
        NewBooking fromServer2 = response22.as(NewBooking.class);
        assertThat(fromServer2.getLastname()).isEqualTo(requestBooking2.getLastname());
    }

    @Test
    @DisplayName("Получение бронирований с фильтрацией по праметрам Firstname и Lastname")
    public void getBookingWithFirstnameAndLastname() throws JsonProcessingException {
        // фильтрация по Firstname и Lastname
        Response response8 = apiClient.getBookingIdWithFirstnameAndLastname(requestBooking1.getFirstname(),
                requestBooking1.getLastname());
        assertThat(response8.getStatusCode()).isEqualTo(200);

        //выбираем один bookingid из списка--
        int existingId3 = apiClient.getBookingIdWithFirstnameAndLastname(requestBooking1.getFirstname(),
                requestBooking1.getLastname()).jsonPath().getList("bookingid", Integer.class).get(0);
        // Тело ответа содержит только те бронирования, которые соответствуют
        //фильтрующим параметрам.--------------
        Response response23 = apiClient.getBookingById(existingId3);
        NewBooking fromServer3 = response23.as(NewBooking.class);
        assertThat(fromServer3.getFirstname()).isEqualTo(requestBooking1.getFirstname());
        assertThat(fromServer3.getLastname()).isEqualTo(requestBooking1.getLastname());
    }

    @AfterEach
    public void tearDown() {
        // удалаяем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(bookingId1);

        Response response5 = apiClient.getBookingById(bookingId1);
        assertThat(response5.getStatusCode()).isEqualTo(404);
        assertThat(response5.asString()).contains("Not Found");

        //--------удалаяем созданное бронирование-------------------------
        apiClient.deleteBooking(bookingId2);

        Response response7 = apiClient.getBookingById(bookingId2);
        assertThat(response7.getStatusCode()).isEqualTo(404);
        assertThat(response7.asString()).contains("Not Found");
    }


}
