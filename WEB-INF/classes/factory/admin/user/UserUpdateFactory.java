package factory.admin.user;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.admin.user.UserUpdateCommand;
import dao.AbstractDao;
import dao.admin.user.UserDao;

public class UserUpdateFactory extends AbstractFactory {
    @Override
    public AbstractCommand createCommand() {
        return new UserUpdateCommand();
    }
    @Override
    public AbstractDao createDao() {
        return new UserDao();
    }
}
