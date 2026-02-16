package factory.account;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.account.RegisterAccountCommand;
import dao.AbstractDao;

public class SendRegisterAccountFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new RegisterAccountCommand();
    }
    public AbstractDao createDao() {
        return  null;
    }
}