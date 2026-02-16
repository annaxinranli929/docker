package dao.booking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bean.booking.BookingBean;
import dao.AbstractDao;
import dao.ConnectionManager;

public class BookingDao extends AbstractDao<BookingBean> {

    public int insertAndGetId(BookingBean b) {
        String sql = "INSERT INTO bookings(schedule_id, user_id, total_price) VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                 getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, b.getScheduleId());
            ps.setInt(2, b.getUserId());
            ps.setInt(3, b.getTotalPrice());

            int updated = ps.executeUpdate();
            if (updated != 1) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1); // booking_id
            }
            return -1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insert(BookingBean b) {
        String selectForUpdate = """
            SELECT bs.seat_id
            FROM booking_seats bs 
                JOIN bookings b ON bs.booking_id = b.booking_id 
            WHERE bs.deleted_at IS NULL 
                AND b.deleted_at IS NULL
                AND b.schedule_id = ? 
                AND bs.seat_id IN (        
        """;
        
        int seatCount = b.getSeatIds().length;
        for (int i = 0; i < seatCount; i++) {
            if (i > 0) {
                selectForUpdate += ", ";
            }
            selectForUpdate += "?";
        }
        selectForUpdate += ") FOR UPDATE";

        PreparedStatement pst = null;
        ResultSet selectResult = null;
        try {
            pst = getConnection().prepareStatement(selectForUpdate);

            pst.setInt(1, b.getScheduleId());

            for (int i = 0; i < seatCount; i++) {
                pst.setString(i + 2, b.getSeatIds()[i]);
            }

            selectResult = pst.executeQuery();

            if (selectResult.next()) {
                throw new SQLException("その席は予約済みです");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (selectResult != null) {
                    selectResult.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                try{
                    if (pst != null) {
                        pst.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }


        PreparedStatement st2 = null;
        PreparedStatement st3 = null;
        ResultSet generatedKey = null;
        String sql2 = ""
            + "INSERT INTO bookings(schedule_id, user_id, total_price) "
            + "    VALUES(?, ?, ?)";
        String sql3 = ""
            + "INSERT INTO booking_seats(booking_id, schedule_id, seat_id, price_id) "
            + "    VALUES(?, ?, ?, ?)";
        try {
            st2 = getConnection().prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            st2.setInt(1, b.getScheduleId());
            st2.setInt(2, b.getUserId());
            st2.setInt(3, b.getTotalPrice());
            st2.executeUpdate();

            generatedKey = st2.getGeneratedKeys();
            if (generatedKey.next()) {
                int bookingId = generatedKey.getInt(1);
                st3 = getConnection().prepareStatement(sql3);
                for (String seatId : b.getSeatIds()) {
                         Integer priceId = b.getSeatPriceIdMap().get(seatId);
                    if (priceId == null) {
                     throw new SQLException("料金区分が未設定の座席があります seat_id=" + seatId );
                    }
                    st3.setInt(1, bookingId);
                    st3.setInt(2, b.getScheduleId());
                    st3.setInt(3, Integer.parseInt(seatId));

                    st3.setInt(4, priceId);
                    st3.executeUpdate();
                }
            } else {
                System.out.println("booking_idとれず");
            }
            return true;
        } catch (SQLException e) {
            ConnectionManager.getInstance().rollback();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (generatedKey != null)
                    generatedKey.close();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            try { 
                if (st2 != null)
                    st2.close();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            try { 
                if (st3 != null)
                    st3.close();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}