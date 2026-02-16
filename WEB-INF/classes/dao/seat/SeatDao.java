package dao.seat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.SearchDataBean;
import bean.seat.SeatBean;
import dao.AbstractDao;

public class SeatDao extends AbstractDao<SeatBean> {

    @Override
    public List<SeatBean> selectSearch(SearchDataBean searchData) {
        String sql =
            "SELECT s.seat_id, s.seat_name, " +
            "       CASE WHEN r.seat_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_reserved " +
            "FROM seats s " +
            "JOIN schedules sch ON sch.screen_id = s.screen_id " +
            "LEFT JOIN ( " +
            "    SELECT bs.seat_id " +
            "    FROM booking_seats bs " +
            "    JOIN bookings b ON b.booking_id = bs.booking_id " +
            "    WHERE b.schedule_id = ? " +
            "      AND b.deleted_at IS NULL " +
            "      AND bs.deleted_at IS NULL " +
            ") r ON r.seat_id = s.seat_id " +
            "WHERE sch.schedule_id = ? " +
            "ORDER BY s.seat_name";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, searchData.getScheduleId());
            ps.setString(2, searchData.getScheduleId());

            try (ResultSet rs = ps.executeQuery()) {
                List<SeatBean> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new SeatBean(
                        rs.getInt("seat_id"),
                        rs.getString("seat_name"),
                        rs.getBoolean("is_reserved")
                    ));
                }
                
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}