package command.account;

import command.AbstractCommand;
import logic.RequestContext;
import logic.ResponseContext;
import dao.AbstractDao;
import dao.ConnectionManager;
import bean.account.AccountBean;
import bean.SearchDataBean;

import java.util.Map;
import java.util.HashMap;

public class LoginCommand extends AbstractCommand<AccountBean> {
    public ResponseContext execute(ResponseContext resc) {

        RequestContext reqc = getRequestContext();
        
        resc.setTarget("login");
        
        Map<String, Object> results = new HashMap<>();

        String password = reqc.getParameter("password")[0];
        String email = reqc.getParameter("email")[0];

        AccountBean ac = new AccountBean();
        ac.setPassword(password);
        ac.setEmail(email);

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        ConnectionManager.getInstance().beginTransaction();

        if (dao.update(ac)) {
            ConnectionManager.getInstance().commit();
            results.put("ok", true);
        } else {
            ConnectionManager.getInstance().rollback();
            results.put("message", "ログインに失敗しました。");
            results.put("detail", "メールアドレスまたはパスワードが正しくありません。");

            resc.setResult(results);
            return resc;
        }

        SearchDataBean searchData = new SearchDataBean();
        searchData.setEmail(email);
        AccountBean accountAllInfo = dao.selectSearch(searchData).get(0);

        reqc.startSession();
        reqc.setSessionAttribute("account", accountAllInfo);
        reqc.setSessionAttribute("message", "ログイン完了！"); // ← セッションにメッセージ

        results.put("message", "ログイン完了！");
        resc.setResult(results);
        resc.setTargetURI(".");
        resc.setRedirect(true);

        return resc;
    }
}
