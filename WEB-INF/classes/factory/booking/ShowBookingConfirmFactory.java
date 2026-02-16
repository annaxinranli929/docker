package factory.booking;

import factory.AbstractFactory;
import command.AbstractCommand;
import command.booking.ShowBookingConfirmCommand;

import dao.AbstractDao;
import dao.booking.BookingDao;
import dao.price.PriceDao;

public class ShowBookingConfirmFactory extends AbstractFactory {
        
    public AbstractCommand createCommand() {
        return new ShowBookingConfirmCommand();
    }


    public AbstractDao createDao() {
        return new PriceDao();
    }
}