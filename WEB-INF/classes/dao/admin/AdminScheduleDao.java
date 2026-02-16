package dao.admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.AbstractDao;
import bean.admin.AdminScheduleBean;

public class AdminScheduleDao extends AbstractDao<AdminScheduleBean> {

    /* =========================
     *  Schedule list (admin)
     * ========================= */
    @Override
    public List<AdminScheduleBean> selectAll() {
        Connection cn = getConnection();
        String sql =
            "SELECT s.schedule_id, s.start_time, s.end_time, " +
            "       s.movie_id, s.screen_id, " +
            "       IF(s.deleted_at IS NULL, true, false) AS is_active, " +
            "       m.movie_name, " +
            "       sc.screen_name, " +
            "       c.cinema_name " +
            "FROM schedules s " +
            "JOIN movies m ON s.movie_id = m.movie_id " +
            "JOIN screens sc ON s.screen_id = sc.screen_id " +
            "JOIN cinemas c ON sc.cinema_id = c.cinema_id " +
            "ORDER BY s.start_time ASC";

        try (PreparedStatement st = cn.prepareStatement(sql);
            ResultSet rs = st.executeQuery()) {
            List<AdminScheduleBean> list = new ArrayList<>();
            while (rs.next()) {
                AdminScheduleBean b = new AdminScheduleBean();
                b.setScheduleId(String.valueOf(rs.getInt("schedule_id")));
                b.setMovieId(String.valueOf(rs.getInt("movie_id")));
                b.setMovieName(rs.getString("movie_name"));
                b.setScreenId(String.valueOf(rs.getInt("screen_id")));
                b.setScreenName(rs.getString("screen_name"));
                b.setCinemaName(rs.getString("cinema_name"));
                b.setStartTime(rs.getTimestamp("start_time").toString());
                b.setEndTime(rs.getTimestamp("end_time").toString());
                b.setActive(rs.getBoolean("is_active"));
                list.add(b);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AdminScheduleBean> selectByRange(boolean past) {
        Connection cn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String sql =
                "SELECT s.schedule_id, s.start_time, s.end_time, " +
                "       s.movie_id, s.screen_id, " +
                "       IF(s.deleted_at IS NULL, true, false) AS is_active, " +
                "       m.movie_name, " +
                "       sc.screen_name, " +
                "       c.cinema_name " +
                "FROM schedules s " +
                "JOIN movies m ON s.movie_id = m.movie_id " +
                "JOIN screens sc ON s.screen_id = sc.screen_id " +
                "JOIN cinemas c ON sc.cinema_id = c.cinema_id " +
                (past
                ? "WHERE s.start_time < CURRENT_DATE() "
                : "WHERE s.start_time >= CURRENT_DATE() ") +
                (past
                ? "ORDER BY s.start_time DESC"
                : "ORDER BY s.start_time ASC");

            st = cn.prepareStatement(sql);
            rs = st.executeQuery();

            List<AdminScheduleBean> list = new ArrayList<>();
            while (rs.next()) {
                AdminScheduleBean b = new AdminScheduleBean();
                b.setScheduleId(String.valueOf(rs.getInt("schedule_id")));
                b.setMovieId(String.valueOf(rs.getInt("movie_id")));
                b.setMovieName(rs.getString("movie_name"));
                b.setScreenId(String.valueOf(rs.getInt("screen_id")));
                b.setScreenName(rs.getString("screen_name"));
                b.setCinemaName(rs.getString("cinema_name"));
                b.setStartTime(rs.getTimestamp("start_time").toString());
                b.setEndTime(rs.getTimestamp("end_time").toString());
                b.setActive(rs.getBoolean("is_active"));
                list.add(b);
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            close(rs);
            close(st);
        }
    }


    /* =========================
     *  Schedule insert
     * ========================= */
    public boolean insertSchedule(AdminScheduleBean bean) {
        Connection cn = getConnection();
        PreparedStatement st = null;

        try {
            String sql =
                "INSERT INTO schedules (start_time, end_time, movie_id, screen_id) " +
                "VALUES (?, ?, ?, ?)";

            st = cn.prepareStatement(sql);
            st.setTimestamp(1, Timestamp.valueOf(bean.getStartTime()));
            st.setTimestamp(2, Timestamp.valueOf(bean.getEndTime()));
            st.setInt(3, Integer.parseInt(bean.getMovieId()));
            st.setInt(4, Integer.parseInt(bean.getScreenId()));

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            close(st);
        }
    }

    /* =========================
     *  Schedule update
     * ========================= */
    public boolean updateSchedule(AdminScheduleBean bean) {
        Connection cn = getConnection();

        String sql =
            "UPDATE schedules " +
            "SET start_time=?, end_time=?, movie_id=?, screen_id=? " +
            "WHERE schedule_id=?";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setTimestamp(1, Timestamp.valueOf(bean.getStartTime()));
            st.setTimestamp(2, Timestamp.valueOf(bean.getEndTime()));
            st.setInt(3, Integer.parseInt(bean.getMovieId()));
            st.setInt(4, Integer.parseInt(bean.getScreenId()));
            st.setInt(5, Integer.parseInt(bean.getScheduleId()));

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* =========================
     *  Toggle
     * ========================= */
    public boolean toggleDeletedAt(int scheduleId) {
        Connection cn = getConnection();
        PreparedStatement st = null;

        try {
            String sql =
                "UPDATE schedules " +
                "SET deleted_at = CASE WHEN deleted_at IS NULL THEN NOW() ELSE NULL END " +
                "WHERE schedule_id = ?";

            st = cn.prepareStatement(sql);
            st.setInt(1, scheduleId);
            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            close(st);
        }
    }

    public List<MovieOption> selectMovieOptions() {
        Connection cn = getConnection();

        String sql =
            "SELECT movie_id, movie_name, runtime_minutes " +
            "FROM movies " +
            "ORDER BY movie_name";

        List<MovieOption> list = new ArrayList<>();

        try (PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Integer runtimeMinutes = (Integer) rs.getObject("runtime_minutes"); // NULL-safe

                list.add(new MovieOption(
                    rs.getInt("movie_id"),
                    rs.getString("movie_name"),
                    runtimeMinutes
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer findRuntimeMinutesByMovieId(int movieId) {
        Connection cn = getConnection();
        String sql = "SELECT runtime_minutes FROM movies WHERE movie_id=?";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, movieId);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                int v = rs.getInt("runtime_minutes");
                return rs.wasNull() ? null : v;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* =========================
     *  Movie helpers (IMPORTANT)
     * ========================= */

    public boolean hasOverlapForInsert(AdminScheduleBean bean) {
        Connection cn = getConnection();

        String sql =
            "SELECT COUNT(*) " +
            "FROM schedules " +
            "WHERE screen_id = ? " +
            "AND start_time < ? " +
            "AND end_time > ?";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, Integer.parseInt(bean.getScreenId()));
            st.setTimestamp(2, Timestamp.valueOf(bean.getEndTime()));
            st.setTimestamp(3, Timestamp.valueOf(bean.getStartTime()));

            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasOverlapForUpdate(AdminScheduleBean bean) {
        Connection cn = getConnection();

        String sql =
            "SELECT COUNT(*) " +
            "FROM schedules " +
            "WHERE screen_id = ? " +
            "AND schedule_id <> ? " +
            "AND start_time < ? " +
            "AND end_time > ?";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, Integer.parseInt(bean.getScreenId()));
            st.setInt(2, Integer.parseInt(bean.getScheduleId()));
            st.setTimestamp(3, Timestamp.valueOf(bean.getEndTime()));
            st.setTimestamp(4, Timestamp.valueOf(bean.getStartTime()));

            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AdminScheduleBean> findConflictsForInsert(AdminScheduleBean bean) {
        Connection cn = getConnection();

        String sql =
            "SELECT s.schedule_id, s.start_time, s.end_time, s.movie_id, m.movie_name, s.screen_id " +
            "FROM schedules s " +
            "JOIN movies m ON s.movie_id = m.movie_id " +
            "WHERE s.screen_id = ? " +
            "AND s.start_time < ? " +
            "AND s.end_time > ? " +
            "ORDER BY s.start_time " +
            "LIMIT 5";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, Integer.parseInt(bean.getScreenId()));
            st.setTimestamp(2, Timestamp.valueOf(bean.getEndTime()));
            st.setTimestamp(3, Timestamp.valueOf(bean.getStartTime()));

            try (ResultSet rs = st.executeQuery()) {
                List<AdminScheduleBean> list = new ArrayList<>();
                while (rs.next()) {
                    AdminScheduleBean b = new AdminScheduleBean();
                    b.setScheduleId(String.valueOf(rs.getInt("schedule_id")));
                    b.setMovieId(String.valueOf(rs.getInt("movie_id")));
                    b.setMovieName(rs.getString("movie_name")); // make sure bean has movieName
                    b.setScreenId(String.valueOf(rs.getInt("screen_id")));
                    b.setStartTime(rs.getTimestamp("start_time").toString());
                    b.setEndTime(rs.getTimestamp("end_time").toString());
                    list.add(b);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AdminScheduleBean> findConflictsForUpdate(AdminScheduleBean bean) {
        Connection cn = getConnection();

        String sql =
            "SELECT s.schedule_id, s.start_time, s.end_time, s.movie_id, m.movie_name, s.screen_id " +
            "FROM schedules s " +
            "JOIN movies m ON s.movie_id = m.movie_id " +
            "WHERE s.screen_id = ? " +
            "AND s.schedule_id <> ? " +
            "AND s.start_time < ? " +
            "AND s.end_time > ? " +
            "ORDER BY s.start_time " +
            "LIMIT 5";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, Integer.parseInt(bean.getScreenId()));
            st.setInt(2, Integer.parseInt(bean.getScheduleId()));
            st.setTimestamp(3, Timestamp.valueOf(bean.getEndTime()));
            st.setTimestamp(4, Timestamp.valueOf(bean.getStartTime()));

            try (ResultSet rs = st.executeQuery()) {
                List<AdminScheduleBean> list = new ArrayList<>();
                while (rs.next()) {
                    AdminScheduleBean b = new AdminScheduleBean();
                    b.setScheduleId(String.valueOf(rs.getInt("schedule_id")));
                    b.setMovieId(String.valueOf(rs.getInt("movie_id")));
                    b.setMovieName(rs.getString("movie_name"));
                    b.setScreenId(String.valueOf(rs.getInt("screen_id")));
                    b.setStartTime(rs.getTimestamp("start_time").toString());
                    b.setEndTime(rs.getTimestamp("end_time").toString());
                    list.add(b);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MovieOption {
        private int movieId;
        private String movieName;
        private Integer runtimeMinutes;

        public MovieOption(int movieId, String movieName, Integer runtimeMinutes) {
            this.movieId = movieId;
            this.movieName = movieName;
            this.runtimeMinutes = runtimeMinutes;
        }

        public int getMovieId() { return movieId; }
        public String getMovieName() { return movieName; }
        public Integer getRuntimeMinutes() { return runtimeMinutes; }
    }

    public List<ScreenOption> selectScreenOptions() {
        Connection cn = getConnection();
        String sql =
            "SELECT sc.screen_id, sc.screen_name, c.cinema_name " +
            "FROM screens sc " +
            "JOIN cinemas c ON sc.cinema_id = c.cinema_id " +
            "ORDER BY c.cinema_name, sc.screen_name";

        try (PreparedStatement st = cn.prepareStatement(sql);
            ResultSet rs = st.executeQuery()) {

            List<ScreenOption> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new ScreenOption(
                    rs.getInt("screen_id"),
                    rs.getString("screen_name"),
                    rs.getString("cinema_name")
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ScreenOption {
        private int screenId;
        private String screenName;
        private String cinemaName;

        public ScreenOption(int screenId, String screenName, String cinemaName) {
            this.screenId = screenId;
            this.screenName = screenName;
            this.cinemaName = cinemaName;
        }
        public int getScreenId() { return screenId; }
        public String getScreenName() { return screenName; }
        public String getCinemaName() { return cinemaName; }
    }

    public String selectGenreTextByIds(int[] genreIds) {
        if (genreIds == null || genreIds.length == 0) return "";

        Connection cn = getConnection();

        StringBuilder in = new StringBuilder();
        for (int i = 0; i < genreIds.length; i++) {
            if (i > 0) in.append(",");
            in.append("?");
        }

        String sql = "SELECT genre_name FROM genres WHERE genre_id IN (" + in + ") ORDER BY genre_name";
        List<String> names = new ArrayList<>();

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < genreIds.length; i++) {
                ps.setInt(i + 1, genreIds[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) names.add(rs.getString(1));
            }
            return String.join("、", names);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* =========================
     *  Close helpers
     * ========================= */
    private void close(AutoCloseable ac) {
        try {
            if (ac != null) ac.close();
        } catch (Exception ignore) {}
    }
}
