package dao.booking;


import java.sql.SQLException;


import java.sql.Connection;      
import java.sql.PreparedStatement;
import java.sql.SQLException;   

import bean.booking.BookingBean;
import dao.AbstractDao;

public class BookingSeatDao {

    private Connection con;

    public void setConnection(Connection con) {
        this.con = con;
    }


    public void insert(int bookingId, int scheduleId, int seatId, int priceId) {
        String sql =
            "INSERT INTO booking_seats(booking_id, schedule_id, seat_id, price_id) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, scheduleId);
            ps.setInt(3, seatId);
            ps.setInt(4, priceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}