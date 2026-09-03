package tests;

import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingDates;
import core.models.CreateBookingResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTest {

    private APIClient apiClient;

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

        // Отправка запроса
        Response response = apiClient.createBooking(requestBooking);

        // Проверки
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        //проверяем что bookingid не пустой
        assertThat(createBookingResponse.getBookingid()).isNotNull();
        //сравниваем два объекта
        assertThat(createBookingResponse.getBooking().equals(requestBooking));


    }

    @Test
    @DisplayName("GET /booking/{id} возвращает заполненное бронирование")
    public void getBookingByIdReturnsFilledBooking() {
        // id не хардкодим: берём реально существующий из списка
        int existingId = apiClient.getBookings().jsonPath().getList("bookingid", Integer.class).get(0);

        Response response = apiClient.getBookingById(existingId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        Booking booking = response.as(Booking.class);
        assertThat(booking.getFirstname()).isNotBlank();
        assertThat(booking.getLastname()).isNotBlank();
        assertThat(booking.getTotalprice()).isNotNull();
        assertThat(booking.getDepositpaid()).isNotNull();
        assertThat(booking.getBookingdates()).isNotNull();
        assertThat(booking.getBookingdates().getCheckin()).isNotBlank();
        assertThat(booking.getBookingdates().getCheckout()).isNotBlank();
    }

    @Test
    @DisplayName("GET /booking/{id} с несуществующим id возвращает 404")
    public void getBookingByUnknownIdReturns404() {
        Response response = apiClient.getBookingById(999_999_999);

        assertThat(response.getStatusCode()).isEqualTo(404);

    }
}
