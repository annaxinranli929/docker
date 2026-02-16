package factory.account;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.account.RegisterCardCommand;
import dao.AbstractDao;
import dao.card.CardDao;

public class RegisterCardFactory extends AbstractFactory {

    @Override
    public AbstractCommand createCommand() {
        return new RegisterCardCommand();
    }

    @Override
    public AbstractDao createDao() {
        return new CardDao();
    }
}
