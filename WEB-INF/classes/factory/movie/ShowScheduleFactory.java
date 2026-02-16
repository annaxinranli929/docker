package factory.movie;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.movie.ShowScheduleCommand;
import dao.AbstractDao;
import dao.movie.ShowScheduleDao;
import bean.movie.CinemaBean;

public class ShowScheduleFactory extends AbstractFactory<CinemaBean> {
    public AbstractCommand<CinemaBean> createCommand() {
        return new ShowScheduleCommand();
    }
    public AbstractDao<CinemaBean> createDao() {
        return new ShowScheduleDao();
    }
}