package command.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import bean.admin.AdminScheduleBean;
import command.AbstractCommand;
import dao.ConnectionManager;
import dao.admin.AdminScheduleDao;
import logic.RequestContext;
import logic.ResponseContext;

public class AdminSchedulesCommand extends AbstractCommand<AdminScheduleBean> {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        HttpServletRequest req = (HttpServletRequest) reqc.getRequest();
        HttpSession session = req.getSession(false);

        boolean isAdmin = (session != null && Boolean.TRUE.equals(session.getAttribute("isAdmin")));
        if (!isAdmin) {
            Map<String, Object> result = new HashMap<>();
            result.put("ok", false);
            result.put("message", "管理人ログインが必要です");
            resc.setResult(result);
            resc.setTarget("admin_login");
            return resc;
        }

        AdminScheduleDao dao = (AdminScheduleDao) getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());

        Map<String, Object> result = new HashMap<>();
        try {
            // showPast support (optional but your JSP uses it)
            // boolean showPast = "1".equals(req.getParameter("showPast"));
            String range = req.getParameter("range"); // "past" or null
            boolean past = "past".equals(range);

            List<AdminScheduleBean> list = dao.selectByRange(past);

            // ① schedule list
            // List<AdminScheduleBean> list = dao.selectAll(); // if you don't have, use dao.selectAll()
            result.put("data", list);

            // ② movie buttons
            result.put("movies", dao.selectMovieOptions());

            // ③ screens dropdown
            result.put("screens", dao.selectScreenOptions());

            result.put("ok", true);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("ok", false);
            result.put("message", "一覧の取得に失敗しました");
        }

        resc.setResult(result);
        resc.setTarget("admin_schedule_manage");
        return resc;
    }
}