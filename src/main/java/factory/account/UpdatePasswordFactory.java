package factory.account;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.account.UpdatePasswordCommand;
import dao.AbstractDao;
import dao.account.UpdatePasswordDao;
import bean.account.AccountBean;

public class UpdatePasswordFactory extends AbstractFactory<AccountBean> {
    public AbstractCommand<AccountBean> createCommand() {
        return new UpdatePasswordCommand();
    }
    public AbstractDao<AccountBean> createDao() {
        return new UpdatePasswordDao();
    }
}