package tests;

import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingDates;
import core.models.CreateBookingResponse;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTest {

    private static final Log log = LogFactory.getLog(GetBookingTest.class);
    private APIClient apiClient;
    public int bookingId1;
    public Booking requestBooking1;

    // Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        }
    @Test
    @DisplayName("GET /booking возвращает непустой список бронирований с валидными id")
    public void getBookingsReturnsNotEmptyList() {
        Response response = apiClient.getBookings();
        assertThat(response.getStatusCode()).isEqualTo(200);
        List<Booking> bookings = response.jsonPath().getList("", Booking.class);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings).allSatisfy(booking -> assertThat(booking.getBookingid()).isPositive());

    }


    @Test
    @DisplayName("POST /booking создаёт бронирование и возвращает статус 200")
    public void createBookingReturns200() {
        // Подготовка данных
        Booking requestBooking = new Booking();
        BookingDates dates = new BookingDates("2025-12-01", "2025-12-10");
        requestBooking.setFirstname("Alice");
        requestBooking.setLastname("Tester");
        requestBooking.setTotalprice(500);
        requestBooking.setDepositpaid(true);
        requestBooking.setBookingdates(dates);
        requestBooking.setAdditionalneeds("Early check-in");

        requestBooking1=requestBooking;

        // Отправка запроса
        Response response = apiClient.createBooking(requestBooking);

        // Проверки
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        //проверяем что bookingid не пустой
        assertThat(createBookingResponse.getBookingid()).isNotNull();

        bookingId1=createBookingResponse.getBookingid();
       // System.out.println("bookingId1="+bookingId1);


        //assertThat(createBookingResponse.getBooking()).isEqualTo(requestBooking);
        // попросить AssertJ сравнить поля рекурсивно:
        //assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        assertThat(createBookingResponse.getBooking()).usingRecursiveComparison().isEqualTo(requestBooking);

    }

    @Test
    @DisplayName("GET /booking/{id} возвращает заполненное бронирование")
    public void getBookingByIdReturnsFilledBooking() {

        // id не хардкодим: берём реально существующий из списка
        //int existingId = apiClient.getBookings().jsonPath().getList("bookingid", Integer.class).get(0);

        createBookingReturns200();
        int existingId=bookingId1;
        //System.out.println("bookingId1="+bookingId1);

        Response response = apiClient.getBookingById(existingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        //System.out.println("requestBooking1="+requestBooking1);


        Booking fromServer = response.as(Booking.class);
        assertThat(fromServer.getFirstname()).isEqualTo(requestBooking1.getFirstname());
        assertThat(fromServer.getLastname()).isEqualTo(requestBooking1.getLastname());
        assertThat(fromServer.getTotalprice()).isEqualTo(requestBooking1.getTotalprice());
        assertThat(fromServer.getDepositpaid()).isEqualTo(requestBooking1.getDepositpaid());
        assertThat(fromServer.getBookingdates().getCheckin()).isEqualTo(requestBooking1.getBookingdates().getCheckin());
        assertThat(fromServer.getBookingdates().getCheckout()).isEqualTo(requestBooking1.getBookingdates().getCheckout());
        assertThat(fromServer.getAdditionalneeds()).isEqualTo(requestBooking1.getAdditionalneeds());


    }

    @Test
    @DisplayName("GET /booking/{id} с несуществующим id возвращает 404")
    public void getBookingByUnknownIdReturns404() {
        Response response = apiClient.getBookingById(999_999_999);

        assertThat(response.getStatusCode()).isEqualTo(404);

    }
}
