package dao.card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.card.CardBean;
import dao.AbstractDao;

public class CardDao extends AbstractDao<CardBean> {

    public List<CardBean> findByUserId(int userId) {
        String sql = "SELECT card_id, user_id, brand, last4, exp_month, exp_year, is_default " +
                     "FROM saved_cards WHERE user_id=? AND deleted_at IS NULL " +
                     "ORDER BY is_default DESC, card_id DESC";

        List<CardBean> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CardBean c = new CardBean();
                    c.setCardId(rs.getInt("card_id"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setBrand(rs.getString("brand"));
                    c.setLast4(rs.getString("last4"));
                    c.setExpMonth(rs.getInt("exp_month"));
                    c.setExpYear(rs.getInt("exp_year"));
                    c.setDefault(rs.getBoolean("is_default"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public boolean insert(CardBean c) {
        String sql = "INSERT INTO saved_cards " +
                     "(user_id, brand, last4, exp_month, exp_year, is_default) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getBrand());
            ps.setString(3, c.getLast4());
            ps.setInt(4, c.getExpMonth());
            ps.setInt(5, c.getExpYear());
            ps.setBoolean(6, c.isDefault());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ★ 追加 ★
    public boolean updateByUserId(CardBean c) {

        String sql = "UPDATE saved_cards " +
                     "SET brand=?, last4=?, exp_month=?, exp_year=?, is_default=? " +
                     "WHERE user_id=? AND deleted_at IS NULL";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, c.getBrand());
            ps.setString(2, c.getLast4());
            ps.setInt(3, c.getExpMonth());
            ps.setInt(4, c.getExpYear());
            ps.setBoolean(5, c.isDefault());
            ps.setInt(6, c.getUserId());

            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
