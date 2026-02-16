package factory.booking;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.booking.BookingCommand;
import dao.AbstractDao;
import dao.booking.BookingDao;
public class BookingFactory extends AbstractFactory {
    public AbstractCommand createCommand() {
        return new BookingCommand();
    }
    public AbstractDao createDao() {
        return new BookingDao();
    }
}