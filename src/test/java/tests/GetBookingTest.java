package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;

    // Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void Setup() {
        apiClient = new APIClient();
        objectMapper=new ObjectMapper();
    }
    @Test
    public void testGetBooking() throws Exception {
        // Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBooking();
        // проверяем что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

//        //Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {
        }); // TypeReference<List<Booking>>() {}=анонимный класс

        //Проверяем что тело ответа содержит объекты Booking
        assertThat(bookings).isNotEmpty(); //проверяем  что список не пуст

        // Проверяем что каждый объект Booking содержит валидное значение Bookingid
        for (Booking booking : bookings) {
            assertThat(booking.getBookingid()).isGreaterThan(0); // booking должен быть больше 0
        }
    }

    @ParameterizedTest(name="провека GET(получения) бронирования по id")
    @CsvSource({
            //https://restful-booker.herokuapp.com/booking/2
             "Jim, Ericsson, 669, false, 2019-01-02,Breakfast"

    })
    public void testGetBookingById(String firstname,
                                   String  lastname,
                                   int totalprice,
                                   boolean depositpaid,
                                   Object bookingdates,
                                   String additionalneeds
                                   ) throws Exception {
        // Выполняем запрос к эндпоинту /booking через APIClient
        Response response1 = apiClient.getBookingById("1");
        // проверяем что статус код ответа равен 200
        assertThat(response1.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в список объектов Booking
        String responseBody = response1.getBody().asString();


        response1.setFirstname(firstname);
        response1.setLastname(lastname);
        response1.setTotalprice(totalprice);
        response1.setDepositpaid(depositpaid);
        response1.setBookingdates(new Booking.Bookingdates("2019-01-02","2019-01-02"));
        response1.setAdditionalneeds(additionalneeds);



             //Десериализуем тело ответа в список объектов Booking
            String responseBody = response.getBody().asString();
             List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {
              }); // TypeReference<List<Booking>>() {}=анонимный класс
    }
}
