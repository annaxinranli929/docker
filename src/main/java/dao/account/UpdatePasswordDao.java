package dao.account;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;

import java.util.List;
import java.util.ArrayList;

public class UpdatePasswordDao extends AbstractDao<AccountBean> {
    @Override
    public boolean update(AccountBean bean) {
        
        PreparedStatement pst = null;

        String sql = """
            UPDATE users 
            SET password = ? 
            WHERE password = ? 
                AND deleted_at IS NULL
        """;

        try {
            pst = getConnection().prepareStatement(sql);
            pst.setString(1, bean.getPassword());
            pst.setString(2, bean.getMess());

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
