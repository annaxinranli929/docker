package command.account;

import bean.account.AccountBean;
import command.AbstractCommand;
import dao.AbstractDao;
import dao.ConnectionManager;
import logic.RequestContext;
import logic.ResponseContext;

public class UpdateAccountCommand extends AbstractCommand<AccountBean> {

    public ResponseContext execute(ResponseContext resc) {

        RequestContext reqc = getRequestContext();

        String firstName = reqc.getParameter("firstName")[0];
        String lastName  = reqc.getParameter("lastName")[0];
        String password  = reqc.getParameter("password")[0];
        String email     = reqc.getParameter("email")[0];
        String phone     = reqc.getParameter("phone")[0];
        int userId       = Integer.parseInt(reqc.getParameter("userId")[0]);

        AccountBean account = (AccountBean) reqc.getSessionAttribute("account");

        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPassword(password);
        account.setEmail(email);
        account.setPhone(phone);
        // クレカ番号はここで変更しない
        account.setUserId(userId);

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        ConnectionManager.getInstance().beginTransaction();

        boolean result = dao.update(account);

        if (result) {
            ConnectionManager.getInstance().commit();
            reqc.setSessionAttribute("account", account);
            reqc.setSessionAttribute("message", "更新しました。");
        } else {
            ConnectionManager.getInstance().rollback();
            reqc.setSessionAttribute("message", "更新に失敗しました。");
        }

        resc.setTargetURI("myPage");
        resc.setRedirect(true);
        return resc;
    }
}