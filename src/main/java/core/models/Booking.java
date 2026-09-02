package core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;


public class Booking {
    private int bookingid;

//----------

    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private Bookingdates bookingdates;
    private String additionalneeds;



    public static class Bookingdates {
        private String checkin;

        private String checkout;
        //---конструктор-----------

        public Bookingdates(String checkin, String checkout) {
            this.checkin = checkin;
            this.checkout = checkout;
        }
        //Геттер и Сеттер
        public String getCheckin() {
            return checkin;
        }

        public void setCheckin(String checkin) {
            this.checkin = checkin;
        }

        public String getCheckout() {
            return checkout;
        }

        public void setCheckout(String checkout) {
            this.checkout = checkout;
        }

    }
    //--------------------------------------
    // Конструктор
    @JsonCreator
    public Booking(@JsonProperty("bookingid") int bookingid) {
        this.bookingid=bookingid;
    }

    //-конструктор-----------------------------------------
    @JsonCreator
    public Booking(    @JsonProperty("firstname") String firstname,
                       @JsonProperty("lastname")  String lastname,
                       @JsonProperty("totalprice") Integer totalprice,
                       @JsonProperty("depositpaid") Boolean depositpaid,
                       @JsonProperty("bookingdates") Bookingdates bookingdates,
                       @JsonProperty("additionalneeds") String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
        this.additionalneeds = additionalneeds;
    }

    //---------------------------------------
    // Геттер и сеттер
    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }
    //-------Геттер и сеттер-----------------------

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

    public Bookingdates getBookingdates() {
        return bookingdates;
    }

    public void setBookingdates(Bookingdates bookingdates) {
        this.bookingdates = bookingdates;
    }

    public String getAdditionalneeds() {
        return additionalneeds;
    }

    public void setAdditionalneeds(String additionalneeds) {
        this.additionalneeds = additionalneeds;
    }


    //----------------------------

}

