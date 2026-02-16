package factory.news;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.news.NewsCommand;
import dao.AbstractDao;

public class NewsFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new NewsCommand();
    }
    public AbstractDao createDao() {
        return null;
    }
}