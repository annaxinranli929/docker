package factory.admin;

import bean.admin.AdminScheduleBean;
import command.AbstractCommand;
import command.admin.AdminSchedulesCommand;
import dao.AbstractDao;
import dao.admin.AdminScheduleDao;
import factory.AbstractFactory;

public class AdminSchedulesFactory extends AbstractFactory<AdminScheduleBean> {
    @Override
    public AbstractCommand<AdminScheduleBean> createCommand() {
        return new AdminSchedulesCommand();
    }

    @Override
    public AbstractDao<AdminScheduleBean> createDao() {
        return new AdminScheduleDao();
    }
}
