package factory.hello;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.hello.HelloCommand;
import dao.AbstractDao;
import dao.hello.HelloDao;

public class HelloFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new HelloCommand();
    }
    public AbstractDao createDao() {
        return new HelloDao();
    }
}