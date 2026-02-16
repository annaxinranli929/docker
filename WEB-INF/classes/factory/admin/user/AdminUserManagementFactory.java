package factory.admin.user;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.admin.AdminImportScheduleCommand;
import dao.AbstractDao;
import bean.admin.TmdbMovieBean;

public class AdminImportScheduleFactory extends AbstractFactory<AccountBean> {
    @Override
    public AbstractCommand<AccountBean> createCommand() {
        return new AdminImportScheduleCommand();
    }
    @Override
    public AbstractDao<TmdbMovieBean> createDao() {
        return null; // Step 1: no DB yet
    }
}
