package factory.account;

import command.AbstractCommand;
import command.account.SendMyPageCommand;
import dao.AbstractDao;
import factory.AbstractFactory;
import bean.account.BookingHistoryBean;
import dao.account.BookingHistoryDao;

public class SendMyPageFactory extends AbstractFactory<BookingHistoryBean>{
    public AbstractCommand<BookingHistoryBean> createCommand() {
        return new SendMyPageCommand();
    }
    public AbstractDao<BookingHistoryBean> createDao() {
        return new BookingHistoryDao();
    }

}
