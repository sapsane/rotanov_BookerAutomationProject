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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateBookingTest {
    private static final Log log = LogFactory.getLog(GetBookingTest.class);
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreateBookingResponse createBookingResponse; // храним созданное бронирование
    private NewBooking newBooking;  // новый объект для создания бронирования
    private int bookingId1;



    // Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper=new ObjectMapper();
        apiClient.createToken("admin","password123");

        // Подготовка данных Создаёшь объект Booking с данными - это твой эталон
        newBooking = new NewBooking();
        newBooking.setFirstname("Alice");
        newBooking.setLastname("Tester");
        newBooking.setTotalprice(500);
        newBooking.setDepositpaid(true);
        BookingDates dates = new BookingDates("2025-12-01", "2025-12-10");
        newBooking.setBookingdates(dates);
        newBooking.setAdditionalneeds("Early check-in");



    }
    @Test
    @DisplayName("POST /booking создаёт бронирование")
    public void createBooking() throws JsonProcessingException {

        //выполняем запрос к эндпоинту /booking через ApiClient
        String requestBody = objectMapper.writeValueAsString(newBooking);
        // Отправка запроса
        Response response = apiClient.createBooking2(requestBody);

        // Проверки что статус код=200
        assertThat(response.getStatusCode()).isEqualTo(200);

        // десериализуем тело ответа в объект Booking
        String responseBody = response.asString();
        createBookingResponse= objectMapper.readValue(responseBody,CreateBookingResponse.class);

        //

        //проверяем что booking не пустой
        assertThat(createBookingResponse).isNotNull();
        //сохраняем в переменную bookingId
        bookingId1=createBookingResponse.getBookingid();
        //System.out.println("bookingId1="+bookingId1);


        //проверяем, что тело ответа содержит объект нового бронирования
        assertEquals(createBookingResponse.getBooking().getFirstname(),newBooking.getFirstname());
        assertEquals(createBookingResponse.getBooking().getLastname(),newBooking.getLastname());
        assertEquals(createBookingResponse.getBooking().getTotalprice(),newBooking.getTotalprice());
        assertEquals(createBookingResponse.getBooking().getDepositpaid(),newBooking.getDepositpaid());
        assertEquals(createBookingResponse.getBooking().getDepositpaid(),newBooking.getDepositpaid());
        assertEquals(createBookingResponse.getBooking().getBookingdates().getCheckin(),newBooking.getBookingdates().getCheckin());
        assertEquals(createBookingResponse.getBooking().getBookingdates().getCheckout(),newBooking.getBookingdates().getCheckout());
        assertEquals(createBookingResponse.getBooking().getAdditionalneeds(),newBooking.getAdditionalneeds());
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
