package factory;

import java.util.Properties;
import java.io.FileInputStream;

import command.AbstractCommand;
import dao.AbstractDao;
import bean.Bean;

public abstract class AbstractFactory<T extends Bean> {
    public static AbstractFactory getFactory(String key) {
        Properties prop = new Properties();
        AbstractFactory factory = null;
        try {
            prop.load(new FileInputStream("C:/webapps/NEOcinema/factory.properties"));
            String className = prop.getProperty(key);
            Class<?> c = Class.forName(className);
            factory = (AbstractFactory) c.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return factory;
    }
    public abstract AbstractCommand<T> createCommand();
    public abstract AbstractDao<T> createDao();
}