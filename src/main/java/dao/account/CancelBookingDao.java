package dao.account;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import bean.account.BookingHistoryBean;
import dao.AbstractDao;
import dao.ConnectionManager;

public class CancelBookingDao extends AbstractDao<BookingHistoryBean> {
    public boolean update(BookingHistoryBean bean) {
        PreparedStatement pst = null;
        boolean result = false;
        String sql = """
                UPDATE booking_seats 
                SET deleted_at = NOW() 
                WHERE seat_id = ? 
                    AND booking_id = ? 
                """;

        try {
            pst = getConnection().prepareStatement(sql);
            pst.setString(1, bean.getSeatsId()[0]);
            pst.setString(2, bean.getBookingId());
            result = pst.executeUpdate() == 1;
        } catch(SQLException e) {
            ConnectionManager.getInstance().rollback();
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        if(!result) {
            return false;
        }

        PreparedStatement pst2 = null;
        String sql2 = """
                UPDATE bookings 
                SET deleted_at = NOW() 
                WHERE booking_id = ?  
                AND NOT EXISTS (
                    SELECT 1 
                    FROM booking_seats 
                    WHERE booking_id = ?  
                    AND deleted_at IS NULL 
                );
                """;
        try {
            pst2 = getConnection().prepareStatement(sql2);
            pst2.setString(1, bean.getBookingId());
            pst2.setString(2, bean.getBookingId());
            boolean result2 = pst2.executeUpdate() == 1;
        } catch(SQLException e) {
            ConnectionManager.getInstance().rollback();
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(pst2 != null) {
                    pst2.close();
                }
            } catch (SQLException e) {
                ConnectionManager.getInstance().rollback();
                throw new RuntimeException(e.getMessage(), e);       
            }
        } 
        return true;
    }
}
