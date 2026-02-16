package dao.price;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.AbstractDao;
import bean.price.PriceBean;

public class PriceDao extends AbstractDao<PriceBean> {
    public List<PriceBean> selectAll() {
        Connection cn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        String sql = """
            SELECT price_id, category, price
            FROM prices 
            ORDER BY CASE
                WHEN category = '一般' THEN 1
                WHEN category = '大学生' THEN 2
                WHEN category = '高校生' THEN 3
                WHEN category = '小・中学生' THEN 4
                WHEN category = '子ども(5歳以下)' THEN 5
                WHEN category = 'シニア(65歳以上)' THEN 6
            END asc
        """;
        List<PriceBean> results = new ArrayList<>();
        

        try {
            st = cn.prepareStatement(sql);
            rs = st.executeQuery();

            
            while (rs.next()) {
                PriceBean price = new PriceBean();
                price.setPriceId(rs.getInt("price_id"));
                price.setCategory(rs.getString("category"));
                price.setPrice(rs.getInt("price"));

                results.add(price);
            }
            return results;

        } catch(SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
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
public int findPriceByCategory(String category) {
    Connection cn = getConnection();
    String sql = "SELECT price FROM prices WHERE category = ?";

    try (PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setString(1, category);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("price");
            }
            throw new SQLException("該当する料金がありません: " + category);
        }
    } catch (SQLException e) {
        throw new RuntimeException(e.getMessage(), e);
    }
}
 public int findPriceById(int priceId) {
        Connection cn = getConnection();
        String sql = "SELECT price FROM prices WHERE price_id = ?";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, priceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("price");
                throw new SQLException("該当する料金がありません: price_id=" + priceId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}