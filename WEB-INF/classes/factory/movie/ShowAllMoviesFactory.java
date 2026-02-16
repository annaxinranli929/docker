package factory.movie;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.movie.ShowAllMoviesCommand;
import dao.AbstractDao;
import dao.movie.MovieDao;

public class ShowAllMoviesFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new ShowAllMoviesCommand();
    }
    public AbstractDao createDao() {
        return new MovieDao();
    }
}