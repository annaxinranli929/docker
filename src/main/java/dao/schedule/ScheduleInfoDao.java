package dao.schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.schedule.ScheduleInfoBean;
import dao.AbstractDao;

public class ScheduleInfoDao extends AbstractDao<ScheduleInfoBean> {

    public ScheduleInfoBean selectByScheduleId(String scheduleId) {
        String sql =
            "SELECT sch.start_time, sch.end_time, " +
            "       m.movie_name, m.poster_url, sc.screen_name " +
            "FROM schedules sch " +
            "JOIN movies  m  ON m.movie_id  = sch.movie_id " +
            "JOIN screens sc ON sc.screen_id = sch.screen_id " +
            "WHERE sch.schedule_id = ? AND sch.deleted_at IS NULL";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, scheduleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new ScheduleInfoBean(
                    rs.getString("movie_name"),
                    rs.getString("poster_url"),
                    rs.getString("screen_name"),
                    rs.getTimestamp("start_time"),
                    rs.getTimestamp("end_time")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
