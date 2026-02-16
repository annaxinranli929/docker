package factory.account;

import bean.account.BookingHistoryBean;
import command.AbstractCommand;
import command.account.CancelBookingCommand;
import dao.AbstractDao;
import factory.AbstractFactory;
import dao.account.CancelBookingDao;

public class CancelBookingFactory extends AbstractFactory<BookingHistoryBean> {
    public AbstractCommand<BookingHistoryBean> createCommand() {
        return new CancelBookingCommand();
    }
    public AbstractDao<BookingHistoryBean> createDao() {
        return new CancelBookingDao();
    }
}
