package factory.account;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.account.AccountCommand;
import dao.AbstractDao;
import dao.account.AccountDao;

public class RegisterAccountFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new AccountCommand();
    }
    public AbstractDao createDao() {
        return new AccountDao();
    }
}