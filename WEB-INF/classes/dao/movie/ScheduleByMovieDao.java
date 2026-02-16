package dao.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.AbstractDao;
import bean.movie.CinemaBean;
import bean.movie.MovieBean;
import bean.movie.ScheduleBean;
import bean.movie.ShowDateBean;

public class ScheduleByMovieDao extends AbstractDao<CinemaBean> {
    public List<CinemaBean> selectByMovieId(int id) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = """
            SELECT 
                c.cinema_id, c.cinema_name, sc.screen_id, sc.screen_name, 
                DATE(s.start_time) AS show_date, 
                s.schedule_id, s.start_time, s.end_time, 
                CASE WHEN s.start_time > NOW() 
                    AND s.start_time < DATE_ADD(CAST(DATE(NOW()) AS DATETIME), INTERVAL 15 DAY) 
                    THEN TRUE ELSE FALSE END AS is_reservable, 
                m.movie_id, m.movie_name, m.poster_url, m.evaluation, m.summary, m.api_id, 
                GROUP_CONCAT(g.genre_name) AS genres 
            FROM schedules s 
            JOIN movies m ON s.movie_id = m.movie_id 
            JOIN screens sc ON s.screen_id = sc.screen_id 
            JOIN cinemas c ON sc.cinema_id = c.cinema_id 
            LEFT JOIN movie_genres mg ON m.movie_id = mg.movie_id 
            LEFT JOIN genres g ON mg.genre_id = g.genre_id 
            WHERE m.movie_id = ? 
                AND s.deleted_at IS NULL 
                AND DATE(s.start_time) <= DATE_ADD(NOW(), INTERVAL 2 WEEK) 
                AND DATE(s.start_time) >= DATE(NOW()) 
            GROUP BY s.schedule_id 
            ORDER BY s.start_time
        """;

        try {
            pst = getConnection().prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            ArrayList<CinemaBean> cinemaList = new ArrayList<>();
            Map<String, CinemaBean> cinemaMap = new HashMap<>(); // cinemaId → CinemaBean

            while (rs.next()) {
                String cinemaId = rs.getString("cinema_id");
                String cinemaName = rs.getString("cinema_name");
                Date showDateValue = rs.getDate("show_date");

                String scheduleId = rs.getString("schedule_id");
                String screenId = rs.getString("screen_id");
                String screenName = rs.getString("screen_name");
                Date startTime = rs.getTimestamp("start_time");
                Date endTime = rs.getTimestamp("end_time");
                boolean reservable = rs.getBoolean("is_reservable");

                String movieId = rs.getString("movie_id");
                String movieName = rs.getString("movie_name");
                String posterUrl = rs.getString("poster_url");
                double evaluation = rs.getDouble("evaluation");
                String summary = rs.getString("summary");
                String apiId = rs.getString("api_id");
                String genresStr = rs.getString("genres"); // "Action,Comedy,Drama" みたいな文字列

                String[] genres = genresStr != null ? genresStr.split(",") : new String[0];

                // --- CinemaBean 作成 or 取得 ---
                CinemaBean cinema = cinemaMap.get(cinemaId);
                if (cinema == null) {
                    cinema = new CinemaBean();
                    cinema.setCinemaId(cinemaId);
                    cinema.setCinemaName(cinemaName);
                    cinema.setDates(new ArrayList<>());
                    cinemaMap.put(cinemaId, cinema);
                    cinemaList.add(cinema);
                }

                // --- ShowDateBean 作成 or 取得 ---
                ShowDateBean showDateBean = null;
                for (ShowDateBean sd : cinema.getDates()) {
                    if (sd.getDate().equals(showDateValue)) {
                        showDateBean = sd;
                        break;
                    }
                }
                if (showDateBean == null) {
                    showDateBean = new ShowDateBean();
                    showDateBean.setDate(showDateValue);
                    showDateBean.setSchedules(new ArrayList<>());
                    cinema.getDates().add(showDateBean);
                }

                // --- MovieBean ---
                MovieBean movie = new MovieBean();
                movie.setMovieId(movieId);
                movie.setMovieName(movieName);
                movie.setPosterUrl(posterUrl);
                movie.setEvaluation(evaluation);
                movie.setSummary(summary);
                movie.setApiId(apiId);
                movie.setGenres(genres);

                // --- ScheduleBean ---
                ScheduleBean schedule = new ScheduleBean();
                schedule.setScheduleId(scheduleId);
                schedule.setScreenId(screenId);
                schedule.setScreenName(screenName);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setReservable(reservable);
                schedule.setMovie(movie);

                // --- ShowDateBean に追加 ---
                showDateBean.getSchedules().add(schedule);
            }

        return cinemaList;
        
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch(SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }
}