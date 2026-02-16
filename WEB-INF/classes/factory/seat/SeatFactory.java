package factory.seat;

import bean.seat.SeatBean;
import command.AbstractCommand;
import command.seat.SeatCommand;
import dao.AbstractDao;
import dao.seat.SeatDao;
import factory.AbstractFactory;

public class SeatFactory extends AbstractFactory<SeatBean> {
    @Override
    public AbstractCommand<SeatBean> createCommand() {
        return new SeatCommand();
    }

    @Override
    public AbstractDao<SeatBean> createDao() {
        return new SeatDao();
    }
}

