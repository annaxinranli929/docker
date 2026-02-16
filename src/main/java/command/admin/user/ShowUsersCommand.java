package command.admin.user;

import command.AbstractCommand;
import logic.ResponseContext;
import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ShowUsersCommand extends AbstractCommand<AccountBean> {

    @Override
    public ResponseContext execute(ResponseContext resc) {

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        List<AccountBean> users = dao.selectAll();

        Map<String, Object> results = new HashMap<>();
        results.put("userList", users);

        resc.setResult(results);
        resc.setTarget("admin_show_users");
        return resc;
    }
}