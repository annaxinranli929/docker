package factory.price;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.price.ShowPriceCommand;
import dao.AbstractDao;
import dao.price.PriceDao;

public class ShowPriceFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new ShowPriceCommand();
    }
    public AbstractDao createDao() {
        return new PriceDao();
    }
}