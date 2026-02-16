package factory.movie;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.movie.GetMovieCommand;
import dao.AbstractDao;
import dao.movie.MovieDao;

public class GetMovieFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new GetMovieCommand();
    }
    public AbstractDao createDao() {
        return new MovieDao();
    }
}