package command.seat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.SearchDataBean;
import bean.seat.SeatBean;
import bean.schedule.ScheduleInfoBean;
import command.AbstractCommand;
import dao.AbstractDao;
import dao.ConnectionManager;
import dao.seat.SeatDao;
import dao.schedule.ScheduleInfoDao;
import logic.ResponseContext;

public class SeatCommand extends AbstractCommand<SeatBean> {

    @Override
    public ResponseContext execute(ResponseContext resc) {
// scheduleId から座席リストを取得
        String[] scheduleIds = getRequestContext().getParameter("scheduleId");
        String scheduleId = scheduleIds[0];
        Map<String, Object> result = new HashMap<>();

        java.sql.Connection cn = ConnectionManager.getInstance().getConnection();

        ScheduleInfoDao infoDao = new ScheduleInfoDao();
        infoDao.setConnection(cn);

        ScheduleInfoBean info = infoDao.selectByScheduleId(scheduleId);
        if (info != null) {
            result.put("movieName", info.getMovieName());
            result.put("posterUrl", info.getPosterUrl());
            result.put("screenName", info.getScreenName());
            result.put("startTime", info.getStartTime());
            result.put("endTime", info.getEndTime());
        }


        SearchDataBean searchData = new SearchDataBean();
        searchData.setScheduleId(scheduleId);
        
        searchData.setScheduleId(scheduleId);

        AbstractDao<SeatBean> dao = getDao();
        dao.setConnection(cn);

        List<SeatBean> seats = dao.selectSearch(searchData);
        System.out.println("座席数 = " + seats.size());
   


        result.put("scheduleId", scheduleId);
        result.put("seats", seats);
    

        resc.setResult(result);
        resc.setTarget("seat");
        return resc;

        
    }
}

