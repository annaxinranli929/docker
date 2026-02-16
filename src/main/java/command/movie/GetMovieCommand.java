package command.movie;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import logic.ResponseContext;
import logic.RequestContext;
import bean.SearchDataBean;
import bean.movie.MovieBean;
import bean.movie.CinemaBean;
import command.AbstractCommand;
import dao.AbstractDao;
import dao.movie.MovieDao;
import dao.movie.ScheduleByMovieDao;
import dao.ConnectionManager;

public class GetMovieCommand extends AbstractCommand<MovieBean> {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();

        int movieId = Integer.parseInt(reqc.getParameter("id")[0]);

        AbstractDao<MovieBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        MovieBean movie = dao.selectById(movieId);

        ScheduleByMovieDao sbmdao = new ScheduleByMovieDao();
        sbmdao.setConnection(ConnectionManager.getInstance().getConnection());
        List<CinemaBean> schedules = sbmdao.selectByMovieId(movieId);

        Map<String, Object> results = new HashMap<>();
        results.put("movieDetail", movie);
        results.put("schedules", schedules);

        resc.setResult(results);

        resc.setTarget("movie_detail");
        return resc;
    }
}