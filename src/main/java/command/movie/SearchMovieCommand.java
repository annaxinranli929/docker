package command.movie;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import logic.ResponseContext;
import logic.RequestContext;
import bean.SearchDataBean;
import bean.movie.MovieBean;
import command.AbstractCommand;
import dao.AbstractDao;
import dao.movie.MovieDao;
import dao.ConnectionManager;

public class SearchMovieCommand extends AbstractCommand<MovieBean> {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();

        String category = reqc.getParameter("category")[0];
        String value = reqc.getParameter("param")[0];

        System.out.println("category = " + category);
        System.out.println("value = " + value);

        SearchDataBean searchData = new SearchDataBean();
        if (category == null) {
            throw new RuntimeException("categoryがnull");
        } else if (category.equals("genre")) {
            searchData.setGenre(value);
            System.out.println("searchDataにgenreを追加");
        } else if (category.equals("movieName")) {
            searchData.setMovieName(value);
            System.out.println("searchDataにmovieNameを追加");
        } else if (category.equals("summary")) {
            searchData.setSummary(value);
            System.out.println("searchDataにsummaryを追加");
        }

        AbstractDao<MovieBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        List<MovieBean> movies = dao.selectSearch(searchData);

        Map<String, Object> results = new HashMap<>();
        results.put("isSearchResult", true);
        results.put("data", movies);
        resc.setResult(results);

        resc.setTarget("movie_data");
        return resc;
    }
}