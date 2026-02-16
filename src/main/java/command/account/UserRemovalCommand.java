package command.account;

import command.AbstractCommand;
import dao.AbstractDao;
import dao.ConnectionManager;
import logic.RequestContext;
import logic.ResponseContext;
import bean.account.AccountBean;

public class UserRemovalCommand extends AbstractCommand<AccountBean> {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        int userId = Integer.parseInt(reqc.getParameter("userId")[0]);
        AccountBean bean = new AccountBean();
        bean.setUserId(userId);

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        ConnectionManager.getInstance().beginTransaction();
        boolean result = dao.update(bean);
        ConnectionManager.getInstance().commit();
        reqc.invalidateSession();

        resc.setRedirect(true);
        resc.setTargetURI(".");
        resc.setResult(result);
        return resc;
    }
        
}
