package dao.account;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sound.midi.SysexMessage;

import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;

public class UserRemovalDao extends AbstractDao<AccountBean> {
    public boolean update(AccountBean bean) {
        PreparedStatement pst = null;
        boolean result = false;
        String sql = """
                UPDATE users 
                SET deleted_at = NOW() 
                WHERE user_id = ? 
                """;
        
        try {
            pst = getConnection().prepareStatement(sql);
            pst.setInt(1, bean.getUserId());
            result = pst.executeUpdate() == 1;
        } catch(SQLException e) {
            ConnectionManager.getInstance().rollback();
            e.printStackTrace();
            return false;
        } finally { 
            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }   
        }
        if(!result) {
            return false;
        }

        PreparedStatement pst2 = null;
        boolean result2 = false;
        String sql2 = """
                UPDATE bookings 
                SET deleted_at = NOW() 
                WHERE user_id = ?  
                """;
        try {
            pst2 = getConnection().prepareStatement(sql2);
            pst2.setInt(1, bean.getUserId());
            result2 = pst2.executeUpdate() > 0 ;
        } catch(SQLException e) {
            ConnectionManager.getInstance().rollback();
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(pst2 != null) {
                    pst2.close();
                }
            } catch (SQLException e) {
                ConnectionManager.getInstance().rollback();
                throw new RuntimeException(e.getMessage(), e);       
            }
        } 
        
        if(!result) {
            return false;
        }

        PreparedStatement pst3 = null;
        String sql3 = """
                UPDATE booking_seats
                SET deleted_at = NOW()
                WHERE booking_id IN (
                    SELECT booking_id 
                    FROM bookings 
                    WHERE user_id = ? 
                );
                """;
        try {
            pst3 = getConnection().prepareStatement(sql3);
            pst3.setInt(1, bean.getUserId());
            return pst3.executeUpdate() > 0;
        } catch(SQLException e) {
            ConnectionManager.getInstance().rollback();
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(pst3 != null) {
                    pst3.close();
                }
            } catch (SQLException e) {
                ConnectionManager.getInstance().rollback();
                throw new RuntimeException(e.getMessage(), e);       
            }
        } 
    }
}
