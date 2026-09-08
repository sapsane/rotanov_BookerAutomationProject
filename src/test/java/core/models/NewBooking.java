package core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


    /**
     * Модель бронирования.
     * Пустой конструктор + сеттеры - всё, что нужно Jackson: он создаёт объект и заполняет поля.
     * Никаких @JsonCreator: двух конкурирующих конструкторов в одном классе быть не должно.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class NewBooking {

        private String firstname;
        private String lastname;
        private Integer totalprice;
        private Boolean depositpaid;
        private BookingDates bookingdates;
        private String additionalneeds;

        public NewBooking() {
        }



        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public Integer getTotalprice() {
            return totalprice;
        }

        public void setTotalprice(Integer totalprice) {
            this.totalprice = totalprice;
        }

        public Boolean getDepositpaid() {
            return depositpaid;
        }

        public void setDepositpaid(Boolean depositpaid) {
            this.depositpaid = depositpaid;
        }

        public BookingDates getBookingdates() {
            return bookingdates;
        }

        public void setBookingdates(BookingDates bookingdates) {
            this.bookingdates = bookingdates;
        }

        public String getAdditionalneeds() {
            return additionalneeds;
        }

        public void setAdditionalneeds(String additionalneeds) {
            this.additionalneeds = additionalneeds;
        }
    }

