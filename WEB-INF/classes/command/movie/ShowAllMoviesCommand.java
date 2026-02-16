package command.movie;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import logic.ResponseContext;
import command.AbstractCommand;
import bean.movie.MovieBean;
import dao.ConnectionManager;
import dao.AbstractDao;

public class ShowAllMoviesCommand extends AbstractCommand<MovieBean> {
    public ResponseContext execute(ResponseContext resc) {
        AbstractDao<MovieBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        List<MovieBean> allMovie = dao.selectAll();

        Map<String, Object> results = new HashMap<>();
        results.put("isSearchResult", false);
        results.put("data", allMovie);
        resc.setResult(results);

        resc.setTarget("movie_data");
        return resc;
    }
}