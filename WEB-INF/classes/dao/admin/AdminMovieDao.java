package dao.admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import bean.admin.AdminMovieBean;
import dao.AbstractDao;

public class AdminMovieDao extends AbstractDao<AdminMovieBean> {

    @Override
    public List<AdminMovieBean> selectAll() {
        Connection cn = getConnection();

        String sql =
            "SELECT movie_id, movie_name, poster_url, evaluation, summary, runtime_minutes, api_id " +
            "FROM movies ORDER BY movie_id";

        try (PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            List<AdminMovieBean> list = new ArrayList<>();
            while (rs.next()) {
                int movieId = rs.getInt("movie_id");

                AdminMovieBean b = new AdminMovieBean();
                b.setMovieId(String.valueOf(movieId));
                b.setMovieName(rs.getString("movie_name"));
                b.setPosterUrl(rs.getString("poster_url"));
                b.setEvaluation(rs.getDouble("evaluation"));
                b.setSummary(rs.getString("summary"));
                b.setRuntimeMinutes((Integer) rs.getObject("runtime_minutes"));
                b.setApiId(String.valueOf(rs.getInt("api_id")));
                b.setGenreNames(selectGenreNamesByMovieId(cn, movieId));

                list.add(b);
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean insert(AdminMovieBean bean) {
        Connection cn = getConnection();

        String sql =
            "INSERT INTO movies (movie_name, poster_url, evaluation, summary, runtime_minutes, api_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            cn.setAutoCommit(false); //  IMPORTANT

            int movieId;
            try (PreparedStatement st = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, bean.getMovieName());
                st.setString(2, bean.getPosterUrl());
                st.setDouble(3, bean.getEvaluation());
                st.setString(4, bean.getSummary());

                if (bean.getRuntimeMinutes() == null) {
                    st.setNull(5, Types.INTEGER);
                } else {
                    st.setInt(5, bean.getRuntimeMinutes());
                }

                st.setInt(6, Integer.parseInt(bean.getApiId()));

                if (st.executeUpdate() != 1) {
                    cn.rollback();
                    return false;
                }

                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (!keys.next()) {
                        cn.rollback();
                        return false;
                    }
                    movieId = keys.getInt(1);
                }
            }

            insertMovieGenresByIds(cn, movieId, bean.getGenreIds());

            cn.commit(); // OK because autocommit=false
            return true;

        } catch (Exception e) {
            try { cn.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException(e.getMessage(), e);

        } finally {
            // try { cn.rollback(); } catch (SQLException ignore) {}
            try { cn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    private void insertMovieGenres(Connection cn, int movieId, int[] genreIds) throws SQLException {
        if (genreIds == null || genreIds.length == 0) return;
    }
    
    //  moved here (as you requested earlier)
    public Integer findMovieIdByApiId(int apiId) {
        Connection cn = getConnection();
        String sql = "SELECT movie_id FROM movies WHERE api_id=?";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, apiId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getInt("movie_id") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String[] selectGenreNamesByMovieId(Connection cn, int movieId) throws SQLException {
        String sql =
            "SELECT g.genre_name " +
            "FROM genres g " +
            "JOIN movie_genres mg ON g.genre_id = mg.genre_id " +
            "WHERE mg.movie_id=? " +
            "ORDER BY g.genre_name";

        List<String> generNames = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, movieId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    generNames.add(rs.getString("genre_name"));
                }
            }
        }
        return generNames.toArray(new String[0]);
    }

    /**
     * Insert mapping only if genre_id exists in genres.
     * Unknown genre_id -> 0 rows inserted (safe).
     */
    private void insertMovieGenresByIds(Connection cn, int movieId, String[] genreIds) throws SQLException {
        if (genreIds == null || genreIds.length == 0) return;

        String sql =
            "INSERT INTO movie_genres (movie_id, genre_id) " +
            "SELECT ?, g.genre_id FROM genres g WHERE g.genre_id=?";

        try (PreparedStatement st = cn.prepareStatement(sql)) {
            for (String gidStr : genreIds) {
                Integer gid = tryParseInt(gidStr);
                if (gid == null) continue;

                st.setInt(1, movieId);
                st.setInt(2, gid);
                st.addBatch();
            }
            st.executeBatch();
        }
    }

    private Integer tryParseInt(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}