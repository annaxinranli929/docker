package command.admin.user;

import command.AbstractCommand;
import logic.ResponseContext;
import logic.RequestContext;
import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class UserUpdateCommand extends AbstractCommand<AccountBean> {

    @Override
    public ResponseContext execute(ResponseContext resc) {

        RequestContext reqc = getRequestContext();
        Map<String, Object> results = new HashMap<>();
        int userId = Integer.parseInt(reqc.getParameter("id")[0]);
        String mess = reqc.getParameter("action")[0];

        AccountBean account = new AccountBean();
        account.setUserId(userId);
        account.setMess(mess);

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        ConnectionManager.getInstance().beginTransaction();

        if (dao.update(account)) {
            results.put("ok", true);
            results.put("message", "変更完了");
            ConnectionManager.getInstance().commit();
        } else {
            results.put("ok", false);
            results.put("message", "とりあえずできなかった");
        }

        resc.setRedirect(true);

        resc.setResult(results);
        resc.setTargetURI("showUsers");
        return resc;
    }
}