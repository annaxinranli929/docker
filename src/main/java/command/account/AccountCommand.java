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

public class AccountCommand extends AbstractCommand<AccountBean> {

    public ResponseContext execute(ResponseContext resc) {

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        RequestContext reqc = getRequestContext();

        // パラメータ取得
        String firstName = reqc.getParameter("firstName")[0];
        String lastName = reqc.getParameter("lastName")[0];
        String password = reqc.getParameter("password")[0];
        String phone = reqc.getParameter("phone")[0];
        String email = reqc.getParameter("email")[0];
        String creditCardNumber = reqc.getParameter("creditCardNumber")[0];

        // トランザクション開始
        ConnectionManager.getInstance().beginTransaction();

        AccountBean bean = new AccountBean();
        bean.setFirstName(firstName);
        bean.setLastName(lastName);
        bean.setPassword(password);
        bean.setPhone(phone);
        bean.setEmail(email);
        bean.setCreditCardNumber(creditCardNumber);

        boolean result = dao.insert(bean);

        Map<String, Object> results = new HashMap<>();

        if (result) {
            results.put("message", "新規登録が完了しました");
            results.put("status", "success");  // ★ 成功ステータス
            ConnectionManager.getInstance().commit();
            resc.setTarget("login");  // 登録後はログイン画面へ
        } else {
            results.put("message", "新規登録が失敗しました");
            results.put("status", "error");    // ★ 失敗ステータス
            ConnectionManager.getInstance().rollback(); // 失敗時はロールバック
            resc.setTarget("register_account"); // 再表示
        }

        resc.setResult(results);
        return resc;
    }
}
