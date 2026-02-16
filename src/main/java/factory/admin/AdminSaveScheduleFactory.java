package factory.admin;

import bean.admin.AdminScheduleBean;
import command.AbstractCommand;
import command.admin.AdminSaveScheduleCommand;
import dao.AbstractDao;
import dao.admin.AdminScheduleDao;
import factory.AbstractFactory;

public class AdminSaveScheduleFactory extends AbstractFactory<AdminScheduleBean> {
    @Override
    public AbstractCommand<AdminScheduleBean> createCommand() {
        return new AdminSaveScheduleCommand();
    }

    @Override
    public AbstractDao<AdminScheduleBean> createDao() {
        return new AdminScheduleDao();
    }
}
