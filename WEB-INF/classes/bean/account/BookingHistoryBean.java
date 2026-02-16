package bean.account;

import bean.Bean;
import java.util.Date;

public class BookingHistoryBean implements Bean {
    private String bookingId;
    private Date bookingAt;
    private int totalPrice;
    private String movieName;
    private String posterUrl;
    private String cinemaName;
    private String screenName;
    private Date startTime;
    private Date endTime;
    private String[] seatsName;
    private String[] seatsId;
    private boolean finished;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Date getBookingAt() {
        return bookingAt;
    }

    public void setBookingAt(Date bookingAt) {
        this.bookingAt = bookingAt;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String[] getSeatsName() {
        return seatsName;
    }

    public void setSeatsName(String[] seatsName) {
        this.seatsName = seatsName;
    }

    public String[] getSeatsId() {
        return seatsId;
    }

    public void setSeatsId(String[] seatsId) {
        this.seatsId = seatsId;
    }
    public boolean isFinished() {
    return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
