package factory.admin;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.admin.AdminImportScheduleCommand;
import dao.AbstractDao;
import bean.admin.TmdbMovieBean;

public class AdminImportScheduleFactory extends AbstractFactory<TmdbMovieBean> {
    @Override
    public AbstractCommand<TmdbMovieBean> createCommand() {
        return new AdminImportScheduleCommand();
    }
    @Override
    public AbstractDao<TmdbMovieBean> createDao() {
        return null; // Step 1: no DB yet
    }
}
