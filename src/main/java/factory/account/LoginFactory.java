package factory.account;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.account.LoginCommand;
import dao.AbstractDao;
import dao.account.LoginDao;

public class LoginFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new LoginCommand();
    }
    public AbstractDao createDao() {
        return new LoginDao();
    }
}