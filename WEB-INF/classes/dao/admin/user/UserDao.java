package dao.admin.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;

import java.util.List;
import java.util.ArrayList;

public class UserDao extends AbstractDao<AccountBean> {
    @Override
    public List<AccountBean> selectAll() {
        List<AccountBean> users = new ArrayList<>();
        Connection cn = getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        String sql = """
                SELECT user_id, first_name, last_name, password, phone, email, credit_card_number, last_login_at, is_admin, deleted_at 
                FROM users
        """;
        
        try{
            pst = cn.prepareStatement(sql);
            
            rs = pst.executeQuery();

            while(rs.next()) {
                AccountBean user = new AccountBean();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setCreditCardNumber(rs.getString("credit_card_number"));
                user.setLastLoginAt(rs.getTimestamp("last_login_at"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setDeletedAt(rs.getTimestamp("deleted_at"));

                users.add(user);
            }
            return users;

        } catch(SQLException e) {
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

    @Override
    public boolean update(AccountBean bean) {
        
        PreparedStatement pst = null;

        int userId = bean.getUserId();
        String type = bean.getMess();
        String sql = """
            UPDATE users 
        """;

        if (type.equals("toggleAdmin")) {
            sql += "SET is_admin = NOT is_admin ";
        } else if (type.equals("delete")) {
            sql += "SET deleted_at = NOW() ";
        } else if (type.equals("restore")) {
            sql += "SET deleted_at = null ";
        } else if (type.equals("resetPass")) {
            sql += "SET deleted_at = NOW() ";
        } else {
            throw new RuntimeException("なぞの変更リクエスト：" + type);
        }

        sql += "WHERE user_id = ?";

        try {
            pst = getConnection().prepareStatement(sql);
            pst.setInt(1, userId);

            return pst.executeUpdate() == 1;

        } catch(SQLException e) {
            ConnectionManager.getInstance().rollback();
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch(SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
