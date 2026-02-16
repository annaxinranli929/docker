package dao.account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import bean.SearchDataBean;
import bean.account.BookingHistoryBean;
import dao.AbstractDao;

public class BookingHistoryDao extends AbstractDao<BookingHistoryBean>{
    public List<BookingHistoryBean> selectSearch(SearchDataBean searchData){
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<BookingHistoryBean> results = new ArrayList<>(); 
        String sql = """
                SELECT 
                    b.booking_id,
                    b.booking_at,
                    b.total_price,
                    m.movie_name,
                    m.poster_url,
                    c.cinema_name,
                    sc.screen_name,
                    s.start_time,
                    s.end_time,

                    CASE
                        WHEN s.end_time < NOW() THEN 1
                        ELSE 0
                    END AS finished,

                    -- 座席名のリスト (例: "A03, A04")
                    GROUP_CONCAT(st.seat_name ORDER BY st.seat_id SEPARATOR ', ') AS seats_name,
                    -- 座席IDのリスト (例: "101, 102") -> プログラムで分割して利用
                    GROUP_CONCAT(st.seat_id ORDER BY st.seat_id SEPARATOR ',') AS seats_id
                FROM 
                    bookings b
                JOIN 
                    schedules s ON b.schedule_id = s.schedule_id
                JOIN 
                    movies m ON s.movie_id = m.movie_id
                JOIN 
                    screens sc ON s.screen_id = sc.screen_id
                JOIN 
                    cinemas c ON sc.cinema_id = c.cinema_id
                JOIN 
                    booking_seats bs ON b.booking_id = bs.booking_id
                JOIN 
                    seats st ON bs.seat_id = st.seat_id
                WHERE 
                    b.user_id = ?              
                    AND b.deleted_at IS NULL    
                    AND s.deleted_at IS NULL    
                    AND bs.deleted_at IS NULL 
                GROUP BY 
                    b.booking_id
                ORDER BY 
                    s.start_time DESC;
                """;
        try {
            pst = getConnection().prepareStatement(sql);
            pst.setString(1, searchData.getUserId());
            rs = pst.executeQuery();
            while (rs.next()){
                BookingHistoryBean bean = new BookingHistoryBean();
                bean.setBookingId(rs.getString("booking_id"));
                bean.setBookingAt(rs.getTimestamp("booking_at"));
                bean.setTotalPrice(rs.getInt("total_price"));
                bean.setMovieName(rs.getString("movie_name"));
                bean.setPosterUrl(rs.getString("poster_url"));
                bean.setCinemaName(rs.getString("cinema_name"));
                bean.setScreenName(rs.getString("screen_name"));
                bean.setStartTime(rs.getTimestamp("start_time"));
                bean.setEndTime(rs.getTimestamp("end_time"));
                bean.setSeatsId(rs.getString("seats_id") == null ? new String[0] : rs.getString("seats_id").split(","));
                bean.setSeatsName(rs.getString("seats_name") == null ? new String[0] : rs.getString("seats_name").split(","));
                bean.setFinished(rs.getInt("finished") == 1);  
  
                results.add(bean);      
            }
            return results;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                }
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                try {
                    if(pst != null) {
                        pst.close();
                    }
                } catch(Exception e) {
                    throw new RuntimeException(e.getMessage() ,e);
                }
            }
        }
    }
}
