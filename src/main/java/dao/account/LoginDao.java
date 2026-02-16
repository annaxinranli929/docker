package dao.account;

import dao.AbstractDao;
import bean.account.AccountBean;
import bean.SearchDataBean;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class LoginDao extends AbstractDao<AccountBean> {

    @Override
    public boolean update(AccountBean account) {
        PreparedStatement pst = null;

        String sql = """
            UPDATE users 
            SET last_login_at = NOW() 
            WHERE deleted_at IS NULL 
                AND email = ? 
                AND password = ?
        """;

        try {
            pst = getConnection().prepareStatement(sql);
            pst.setString(1, account.getEmail());
            pst.setString(2, account.getPassword());
            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    @Override
    public List<AccountBean> selectSearch(SearchDataBean bean) {

        List<AccountBean> result = new ArrayList<>();
        AccountBean account = new AccountBean();
        PreparedStatement pst = null;
        ResultSet rs = null;

        String sql = """
            SELECT user_id, first_name, last_name, password, phone, email, credit_card_number, last_login_at 
            FROM users 
            WHERE email = ?
        """;

        try {
            pst = getConnection().prepareStatement(sql);
            pst.setString(1, bean.getEmail());
            rs = pst.executeQuery();

            if (rs.next()) {
                account.setUserId(rs.getInt("user_id"));
                account.setFirstName(rs.getString("first_name"));
                account.setLastName(rs.getString("last_name"));
                account.setPassword(rs.getString("password"));
                account.setPhone(rs.getString("phone"));
                account.setEmail(rs.getString("email"));
                account.setCreditCardNumber(rs.getString("credit_card_number"));
                account.setLastLoginAt(rs.getTimestamp("last_login_at"));
            }
        } catch (SQLException e) {
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
                    if (pst != null) {
                        pst.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        result.add(account);
        return result;
    }
}