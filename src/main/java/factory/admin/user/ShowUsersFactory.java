package factory.admin.user;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.admin.user.ShowUsersCommand;
import dao.AbstractDao;
import dao.admin.user.UserDao;

public class ShowUsersFactory extends AbstractFactory {
    @Override
    public AbstractCommand createCommand() {
        return new ShowUsersCommand();
    }
    @Override
    public AbstractDao createDao() {
        return new UserDao();
    }
}
