package dao.account;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;

public class UpdateAccountDao extends AbstractDao<AccountBean>{ 
    @Override
    public boolean update(AccountBean account){
        PreparedStatement pst = null;
        String sql = """
                UPDATE users 
                SET first_name = ?,  
                    last_name = ?, 
                    password = ?, 
                    email = ?, 
                    phone = ?,  
                    credit_card_number = ?  
                WHERE user_id = ? 
                """;

        try { 
            pst = getConnection().prepareStatement(sql);
            pst.setString(1, account.getFirstName());
            pst.setString(2, account.getLastName());
            pst.setString(3, account.getPassword());
            pst.setString(4, account.getEmail());
            pst.setString(5, account.getPhone());
            pst.setString(6, account.getCreditCardNumber());
            pst.setInt(7, account.getUserId());

            return pst.executeUpdate() == 1;
        } catch(SQLException e){
            e.printStackTrace();
            ConnectionManager.getInstance().rollback();
            return false;
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch(SQLException e){
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
