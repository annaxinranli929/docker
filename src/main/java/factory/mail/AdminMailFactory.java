package factory.mail;

import command.AbstractCommand;
import command.mail.AdminMailCommand;
import dao.AbstractDao;
import dao.mail.ChangePasswordDao;
import factory.AbstractFactory;
import bean.account.AccountBean;

public class AdminMailFactory extends AbstractFactory<AccountBean> {
    @Override
    public AbstractCommand<AccountBean> createCommand() {
        return new AdminMailCommand();
    }

    @Override
    public AbstractDao<AccountBean> createDao() {
        return new ChangePasswordDao();
    }
}
