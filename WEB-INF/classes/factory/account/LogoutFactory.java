package factory.account;

import command.AbstractCommand;
import command.account.LogoutCommand;
import dao.AbstractDao;
import factory.AbstractFactory;

public class LogoutFactory extends AbstractFactory{
    public AbstractCommand createCommand() {
        return new LogoutCommand();
    }
    public AbstractDao createDao() {
        return null;
    }
}
