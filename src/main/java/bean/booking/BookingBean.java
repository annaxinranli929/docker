package bean.booking;

import java.util.Map;
import bean.Bean;

public class BookingBean implements Bean {
    private int bookingId;
    private int userId;
    private int scheduleId;
    private int totalPrice;
    private int movieId;
    private String bookingDate;
    private String[] seatIds;
    private String[] seatNames;
    private Map<String,Integer>seatPriceIdMap;

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String[] getSeatIds() {
      return seatIds;
    }
    public void setSeatIds(String[] seatIds) {
      this.seatIds = seatIds;
    }
    public String[] getSeatNames() {
      return seatNames;
    }
    public void setSeatNames(String[] seatNames) {
      this.seatNames = seatNames;
    }
    public Map<String,Integer> getSeatPriceIdMap() {
    return seatPriceIdMap;
    }

    public void setSeatPriceIdMap(Map<String,Integer> seatPriceIdMap) {
    this.seatPriceIdMap = seatPriceIdMap;
    }
}