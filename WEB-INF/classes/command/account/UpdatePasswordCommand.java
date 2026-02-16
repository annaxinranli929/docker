package command.account;

import java.util.List;
import java.util.Map;
import java.util.HashMap; 

import command.AbstractCommand;
import logic.RequestContext;
import logic.ResponseContext;
import dao.AbstractDao;
import bean.account.AccountBean;
import dao.ConnectionManager;

public class UpdatePasswordCommand extends AbstractCommand<AccountBean> {

    public ResponseContext execute(ResponseContext resc) {

        RequestContext reqc = getRequestContext();

        String token = reqc.getParameter("token")[0];
        String newPass = reqc.getParameter("newPassword")[0];

        AccountBean user = new AccountBean();
        user.setPassword(newPass);
        user.setMess(token);

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        ConnectionManager.getInstance().beginTransaction();
        boolean result = dao.update(user);
        ConnectionManager.getInstance().commit();

        Map<String, Object> results = new HashMap<>();
        if (result) {
            results.put("message", "パスワード変更完了");
        } else {
            results.put("message", "パスワード変更失敗");
        }
        
        resc.setResult(results);

        resc.setTarget("login");
        return resc;
    }
}
