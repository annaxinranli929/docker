package command.movie;

import java.util.List;

import logic.ResponseContext;
import command.AbstractCommand;
import dao.AbstractDao;
import dao.ConnectionManager;
import bean.movie.CinemaBean;

public class ShowScheduleCommand extends AbstractCommand<CinemaBean> {
    public ResponseContext execute(ResponseContext resc) {
        AbstractDao<CinemaBean> dao = getDao();

        dao.setConnection(ConnectionManager.getInstance().getConnection());
        List<CinemaBean> schedules = dao.selectAll();
        resc.setResult(schedules);

        resc.setTarget("schedule");
        return resc;
    }
}