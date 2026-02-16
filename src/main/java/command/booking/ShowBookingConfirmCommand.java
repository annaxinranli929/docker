package command.booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.account.AccountBean;
import bean.card.CardBean;
import bean.price.PriceBean;
import bean.schedule.ScheduleInfoBean;
import command.AbstractCommand;
import dao.ConnectionManager;
import dao.card.CardDao;
import dao.price.PriceDao;
import dao.schedule.ScheduleInfoDao;
import logic.RequestContext;
import logic.ResponseContext;

public class ShowBookingConfirmCommand extends AbstractCommand<PriceBean> {
    
    public ResponseContext execute(ResponseContext resc) {

        RequestContext reqc = getRequestContext();

        String[] scheduleIdArr = reqc.getParameter("scheduleId");
        String[] seatIds = reqc.getParameter("seatIds");
        String[] seatNames = reqc.getParameter("seatNames");

        if (scheduleIdArr == null || scheduleIdArr.length == 0 || seatIds == null || seatIds.length == 0) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "座席情報がありません");
            resc.setResult(err);
            resc.setTarget("booking_result");
            return resc;
        }

        String scheduleId = scheduleIdArr[0];


// 上映情報
        ScheduleInfoDao infoDao = new ScheduleInfoDao();
        infoDao.setConnection(ConnectionManager.getInstance().getConnection());

        ScheduleInfoBean info = infoDao.selectByScheduleId(scheduleId);

        String posterUrl = null;
        String movieName = null;
        String screenName = null;
        java.sql.Timestamp startTime = null;
        java.sql.Timestamp endTime = null;

        if (info != null) {
            posterUrl = info.getPosterUrl();
            movieName = info.getMovieName();
            screenName = info.getScreenName();
            startTime = info.getStartTime();
            endTime = info.getEndTime();
        }



        if (scheduleIdArr == null || scheduleIdArr.length == 0 || seatIds == null || seatIds.length == 0) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "座席情報がありません");
            resc.setResult(err);
            resc.setTarget("booking_result");
            return resc;
        }

        // prices
        PriceDao priceDao = new PriceDao();
        priceDao.setConnection(ConnectionManager.getInstance().getConnection());
        List<PriceBean> prices = priceDao.selectAll();

        // cards
        CardDao cardDao = new CardDao();
        cardDao.setConnection(ConnectionManager.getInstance().getConnection());

        List<CardBean> cards = new ArrayList<>();
        AccountBean account = (AccountBean) reqc.getSessionAttribute("account");

        if (account != null) {
            int userId = account.getUserId();
            cards = cardDao.findByUserId(userId);
            System.out.println("cards size=" + cards.size() + " userId=" + userId);
        } else {
            System.out.println("account is null (not logged in)");
        }

        // result
        Map<String, Object> result = new HashMap<>();
        result.put("scheduleId", scheduleId);
        result.put("seatIds", seatIds);
        result.put("seatNames", seatNames);
        result.put("prices", prices);
        result.put("posterUrl", posterUrl);
        result.put("movieName", movieName);
        result.put("screenName", screenName);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("cards", cards);

        resc.setResult(result);
        resc.setTarget("booking_confirm");
        return resc;
    }
}
    
