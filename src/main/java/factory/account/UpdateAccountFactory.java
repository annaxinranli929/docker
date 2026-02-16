package factory.account;

import bean.account.AccountBean;
import command.AbstractCommand;
import command.account.UpdateAccountCommand;
import dao.AbstractDao;
import dao.account.UpdateAccountDao;
import factory.AbstractFactory;

public class UpdateAccountFactory extends AbstractFactory<AccountBean>{
    public AbstractCommand<AccountBean> createCommand() {
        return new UpdateAccountCommand();
    }
    public AbstractDao<AccountBean> createDao() {
        return new UpdateAccountDao();
    }

}
