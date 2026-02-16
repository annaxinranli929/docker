package factory.account;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.account.SendUpdatePasswordCommand;
import dao.AbstractDao;

public class SendUpdatePasswordFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new SendUpdatePasswordCommand();
    }
    public AbstractDao createDao() {
        return null;
    }
}