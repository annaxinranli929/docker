package factory.account;

import bean.account.AccountBean;
import command.AbstractCommand;
import command.account.UserRemovalCommand;
import dao.AbstractDao;
import factory.AbstractFactory;
import dao.account.UserRemovalDao;

public class UserRemovalFactory extends AbstractFactory<AccountBean> {
    public AbstractCommand<AccountBean> createCommand() {
        return new UserRemovalCommand();
    }
    public AbstractDao<AccountBean> createDao() {
        return new UserRemovalDao();
    }
}
