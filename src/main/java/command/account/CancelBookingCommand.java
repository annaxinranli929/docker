package command.account;

import command.AbstractCommand;
import bean.account.BookingHistoryBean;
import logic.ResponseContext;
import logic.RequestContext;
import dao.AbstractDao;
import dao.ConnectionManager;

public class CancelBookingCommand extends AbstractCommand<BookingHistoryBean> {

    public ResponseContext execute(ResponseContext resc) {

        RequestContext reqc = getRequestContext();

        String bookingId = reqc.getParameter("bookingId")[0];
        String[] seatId = reqc.getParameter("seatId");

        BookingHistoryBean bean = new BookingHistoryBean();
        bean.setBookingId(bookingId);
        bean.setSeatsId(seatId);

        AbstractDao<BookingHistoryBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        ConnectionManager.getInstance().beginTransaction();

        boolean result = dao.update(bean);

        if (result) {
            ConnectionManager.getInstance().commit();

            // ★ これだけ追加
            reqc.setSessionAttribute("message", "予約を取り消しました。");
        }

        resc.setRedirect(true);
        resc.setTargetURI("myPage");
        resc.setResult(result);

        return resc;
    }
}
