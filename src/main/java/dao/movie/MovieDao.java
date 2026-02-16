package dao.movie;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.AbstractDao;
import bean.movie.MovieBean;
import bean.SearchDataBean;

public class MovieDao extends AbstractDao<MovieBean> {
    public List<MovieBean> selectAll() {
        Connection cn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        String sql = """
            SELECT 
                m.movie_id, 
                m.movie_name, 
                m.poster_url, 
                m.evaluation, 
                m.summary, 
                m.api_id, 
                GROUP_CONCAT(g.genre_name) AS genres, 
                CASE 
                    WHEN EXISTS (
                        SELECT 1 FROM schedules s 
                        WHERE s.movie_id = m.movie_id 
                        AND s.deleted_at IS NULL
                        AND s.start_time >= NOW()
                    ) THEN TRUE 
                    ELSE FALSE 
                END AS is_active 
            FROM movies m 
            LEFT JOIN movie_genres mg ON m.movie_id = mg.movie_id 
            LEFT JOIN genres g ON mg.genre_id = g.genre_id 
            GROUP BY m.movie_id, m.movie_name, m.poster_url, m.evaluation, m.summary, m.api_id 
            ORDER BY m.evaluation DESC
        """;
        try {
            st = cn.prepareStatement(sql);
            rs = st.executeQuery();
            List<MovieBean> movieList = new ArrayList<>();

            while (rs.next()) {
                String movieId = rs.getString("movie_id");
                String movieName = rs.getString("movie_name");
                String posterUrl = rs.getString("poster_url");
                double evaluation = rs.getDouble("evaluation");
                String summary = rs.getString("summary");
                String apiId = rs.getString("api_id");
                String genresStr = rs.getString("genres");
                boolean isActive = rs.getBoolean("is_active");

                String[] genres = genresStr != null ? genresStr.split(",") : new String[0];

                MovieBean movie = new MovieBean();
                movie.setMovieId(movieId);
                movie.setMovieName(movieName);
                movie.setPosterUrl(posterUrl);
                movie.setEvaluation(evaluation);
                movie.setSummary(summary);
                movie.setApiId(apiId);
                movie.setGenres(genres);
                movie.setActive(isActive);

                movieList.add(movie);
            }
            return movieList;

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
                    if (st != null) {
                        st.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    public List<MovieBean> selectSearch(SearchDataBean searchData) {
        Connection cn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        String sql = """
            SELECT m.movie_id, 
                m.movie_name, 
                m.poster_url, 
                m.evaluation, 
                m.summary, 
                m.api_id, 
                GROUP_CONCAT(g.genre_name) AS genres, 
                CASE 
                    WHEN EXISTS (
                        SELECT 1 FROM schedules s 
                        WHERE s.movie_id = m.movie_id 
                        AND s.deleted_at IS NULL
                        AND s.start_time >= NOW()
                    ) THEN TRUE 
                    ELSE FALSE 
                END AS is_active 
            FROM movies m 
                JOIN movie_genres mg ON m.movie_id = mg.movie_id 
                JOIN genres g ON mg.genre_id = g.genre_id 
            """;

        String searchMovieName = searchData.getMovieName();
        String searchGenre = searchData.getGenre();
        String searchSummary = searchData.getSummary();

        if (searchMovieName == null && searchGenre == null && searchSummary == null) {
            throw new RuntimeException("検索項目がnull");
        }

        if (searchMovieName != null) {
            sql += "WHERE m.movie_name LIKE ? ";
            sql += """
                GROUP BY m.movie_id, m.movie_name, m.poster_url, m.evaluation, m.summary, m.api_id 
                """;
        }

        if (searchGenre != null) {
            sql += """
                GROUP BY m.movie_id, m.movie_name, m.poster_url, m.evaluation, m.summary, m.api_id 
                """;
            sql += "HAVING SUM(g.genre_name = ?) > 0 ";
        }

        if (searchSummary != null) {
            sql += "WHERE m.summary LIKE ? ";
            sql += """
                GROUP BY m.movie_id, m.movie_name, m.poster_url, m.evaluation, m.summary, m.api_id 
                """;
        }

        sql += """
            ORDER BY m.evaluation desc
            """;

        System.out.println(sql);
        
        try {
            st = cn.prepareStatement(sql);

            if (searchMovieName != null) {
                st.setString(1, "%" + searchMovieName + "%");
            }
            if (searchGenre != null) {
                st.setString(1, searchGenre);
            }
            if (searchSummary != null) {
                st.setString(1, "%" + searchSummary + "%");
            }

            rs = st.executeQuery();
            List<MovieBean> movieList = new ArrayList<>();

            while (rs.next()) {
                String movieId = rs.getString("movie_id");
                String movieName = rs.getString("movie_name");
                String posterUrl = rs.getString("poster_url");
                double evaluation = rs.getDouble("evaluation");
                String summary = rs.getString("summary");
                String apiId = rs.getString("api_id");
                String genresStr = rs.getString("genres");
                boolean isActive = rs.getBoolean("is_active");

                String[] genres = genresStr != null ? genresStr.split(",") : new String[0];

                MovieBean movie = new MovieBean();
                movie.setMovieId(movieId);
                movie.setMovieName(movieName);
                movie.setPosterUrl(posterUrl);
                movie.setEvaluation(evaluation);
                movie.setSummary(summary);
                movie.setApiId(apiId);
                movie.setGenres(genres);
                movie.setActive(isActive);

                movieList.add(movie);
            }
            return movieList;

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
                    if (st != null) {
                        st.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    public MovieBean selectById(int id) {
        Connection cn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        String sql = """
            SELECT 
                m.movie_id, 
                m.movie_name, 
                m.poster_url, 
                m.evaluation, 
                m.summary, 
                m.api_id, 
                GROUP_CONCAT(g.genre_name) AS genres,
                /* 詳細ページ用：この映画の有効なスケジュールがあるか判定 */
                CASE 
                    WHEN EXISTS (
                        SELECT 1 FROM schedules s 
                        WHERE s.movie_id = m.movie_id 
                        AND s.deleted_at IS NULL
                        AND s.start_time >= NOW()
                    ) THEN TRUE 
                    ELSE FALSE 
                END AS is_active
            FROM movies m 
            LEFT JOIN movie_genres mg ON m.movie_id = mg.movie_id 
            LEFT JOIN genres g ON mg.genre_id = g.genre_id 
            WHERE m.movie_id = ?
            GROUP BY m.movie_id
        """;

        System.out.println(sql);
        
        try {
            st = cn.prepareStatement(sql);

            st.setInt(1, id);

            rs = st.executeQuery();

            MovieBean movie = new MovieBean();
            if (rs.next()) {
                String movieId = rs.getString("movie_id");
                String movieName = rs.getString("movie_name");
                String posterUrl = rs.getString("poster_url");
                double evaluation = rs.getDouble("evaluation");
                String summary = rs.getString("summary");
                String apiId = rs.getString("api_id");
                String genresStr = rs.getString("genres");
                boolean isActive = rs.getBoolean("is_active");

                String[] genres = genresStr != null ? genresStr.split(",") : new String[0];

                movie.setMovieId(movieId);
                movie.setMovieName(movieName);
                movie.setPosterUrl(posterUrl);
                movie.setEvaluation(evaluation);
                movie.setSummary(summary);
                movie.setApiId(apiId);
                movie.setGenres(genres);
                movie.setActive(isActive);
            }
            return movie;

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
                    if (st != null) {
                        st.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }
}
