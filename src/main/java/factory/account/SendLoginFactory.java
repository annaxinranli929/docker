package factory.account;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.account.SendLoginCommand;
import dao.AbstractDao;

public class SendLoginFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new SendLoginCommand();
    }
    public AbstractDao createDao() {
        return null;
    }
}