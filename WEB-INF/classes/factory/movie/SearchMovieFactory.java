package factory.movie;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.movie.SearchMovieCommand;
import dao.AbstractDao;
import dao.movie.MovieDao;

public class SearchMovieFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new SearchMovieCommand();
    }
    public AbstractDao createDao() {
        return new MovieDao();
    }
}