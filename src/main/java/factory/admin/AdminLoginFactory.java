package factory.admin;

import bean.admin.AdminLoginBean;
import command.AbstractCommand;
import command.admin.AdminLoginCommand;
import dao.AbstractDao;
import factory.AbstractFactory;

public class AdminLoginFactory extends AbstractFactory<AdminLoginBean> {

    @Override
    public AbstractCommand<AdminLoginBean> createCommand() {
        return new AdminLoginCommand();
    }

    @Override
    public AbstractDao<AdminLoginBean> createDao() {
        // Login does not use DB
        return null;
    }
}
