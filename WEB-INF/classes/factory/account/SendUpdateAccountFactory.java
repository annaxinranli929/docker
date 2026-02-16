package factory.account;

import command.AbstractCommand;
import command.account.SendUpdateAccountCommand;
import dao.AbstractDao;
import factory.AbstractFactory;

public class SendUpdateAccountFactory extends AbstractFactory{
    public AbstractCommand createCommand() {
        return new SendUpdateAccountCommand();
    }
    public AbstractDao createDao() {
        return  null;
    }
}
