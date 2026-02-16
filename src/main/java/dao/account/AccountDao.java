package dao.account;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;


public class AccountDao extends AbstractDao<AccountBean>{
    public boolean insert(AccountBean bean){
        Connection cn = getConnection();
        PreparedStatement st = null;
        
        String sql = """
                INSERT INTO users(first_name, last_name, password, phone, email, credit_card_number)  
                VALUES(?, ?, ?, ?, ?, ?) 
                """;
                
        boolean result = false;

        try{
            st = cn.prepareStatement(sql);
            st.setString(1, bean.getFirstName());
            st.setString(2, bean.getLastName());
            st.setString(3, bean.getPassword());
            st.setString(4, bean.getPhone());
            st.setString(5, bean.getEmail());
            st.setString(6, bean.getCreditCardNumber());

            
            int update = st.executeUpdate();

            if(update >= 1){
                return true;
            }else{
                return false;
            }
        }catch(SQLException e){
            ConnectionManager.getInstance().rollback();
            return false;
        }finally{
            try{
                if(st != null){
                    st.close();
                }
            }catch(SQLException e){
                throw new RuntimeException(e.getMessage(),e);
            }
        }
    }
}
