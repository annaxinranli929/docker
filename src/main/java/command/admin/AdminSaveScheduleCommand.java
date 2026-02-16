package command.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import bean.admin.AdminScheduleBean;
import command.AbstractCommand;
import dao.ConnectionManager;
import dao.admin.AdminScheduleDao;
import logic.RequestContext;
import logic.ResponseContext;

public class AdminSaveScheduleCommand extends AbstractCommand<AdminScheduleBean> {

    private static final int BUFFER_MINUTES = 10;

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

        String action = first(reqc, "action"); // insert / update / delete
        Map<String, Object> result = new HashMap<>();
        boolean ok = false;

        try {
            if ("delete".equals(action)) {
                int sid = Integer.parseInt(first(reqc, "scheduleId"));
                ok = dao.toggleDeletedAt(sid);

                if (!ok) {
                    result.put("message", "切替に失敗しました");
                }

            } else {
                AdminScheduleBean b = new AdminScheduleBean();
                b.setScheduleId(first(reqc, "scheduleId"));
                b.setMovieId(first(reqc, "movieId"));
                b.setScreenId(first(reqc, "screenId"));

                boolean active = "true".equalsIgnoreCase(first(reqc, "isActive"));
                b.setActive(active);

                String startLocal = first(reqc, "startTime");
                b.setStartTime(toTimestampString(startLocal));

                if ("insert".equals(action)) {
                    int movieIdInt = Integer.parseInt(b.getMovieId());
                    Integer rt = dao.findRuntimeMinutesByMovieId(movieIdInt);

                    if (rt == null || rt <= 0) {
                        result.put("ok", false);
                        result.put("message", "上映時間が未登録です。映画を先にDB登録してください。");
                        repopulateForJsp(result, dao, req);
                        resc.setResult(result);
                        resc.setTarget("admin_schedule_manage");
                        return resc;
                    }

                    java.time.LocalDateTime baseStart = java.time.LocalDateTime.parse(startLocal);
                    java.time.LocalDateTime baseEnd = baseStart.plusMinutes(rt + BUFFER_MINUTES);

                    int repeatDays = intParam(reqc, "repeatDays", 1);
                    int repeatEvery = intParam(reqc, "repeatEvery", 1);
                    if (repeatDays < 1) repeatDays = 1;
                    if (repeatEvery < 1) repeatEvery = 1;

                    java.util.List<AdminScheduleBean> toInsert = new java.util.ArrayList<>();
                    for (int i = 0; i < repeatDays; i++) {
                        int offset = i * repeatEvery;

                        java.time.LocalDateTime st = baseStart.plusDays(offset);
                        java.time.LocalDateTime ed = baseEnd.plusDays(offset);

                        AdminScheduleBean one = new AdminScheduleBean();
                        one.setMovieId(b.getMovieId());
                        one.setScreenId(b.getScreenId());
                        one.setActive(active);
                        one.setStartTime(toTimestampStringFromLdt(st));
                        one.setEndTime(toTimestampStringFromLdt(ed));
                        toInsert.add(one);
                    }

                    java.util.List<AdminScheduleBean> allConflicts = new java.util.ArrayList<>();
                    for (AdminScheduleBean one : toInsert) {
                        List<AdminScheduleBean> conflicts = dao.findConflictsForInsert(one);
                        if (!conflicts.isEmpty()) {
                            allConflicts.addAll(conflicts);
                        }
                    }

                    if (!allConflicts.isEmpty()) {
                        result.put("ok", false);
                        result.put("message", "繰り返し登録の中に重複があります（登録しませんでした）");
                        result.put("conflicts", allConflicts);
                        repopulateForJsp(result, dao, req);
                        resc.setResult(result);
                        resc.setTarget("admin_schedule_manage");
                        return resc;
                    }

                    boolean allOk = true;
                    for (AdminScheduleBean one : toInsert) {
                        if (!dao.insertSchedule(one)) { allOk = false; break; }
                    }
                    ok = allOk;

                    result.put("ok", ok);
                    result.put("message", ok ? "繰り返しで追加しました" : "追加に失敗しました");

                    // stop here (don’t run old single-insert code)
                    if (ok) {
                        String url = req.getContextPath() + "/adminSchedules?saved=1";
                        req.setAttribute("redirectUrl", url);
                        resc.setTarget("redirect");
                        resc.setResult(result);
                        return resc;
                    } else {
                        repopulateForJsp(result, dao, req);
                        resc.setResult(result);
                        resc.setTarget("admin_schedule_manage");
                        return resc;
                    }
                }
            }

            result.put("ok", ok);
            if (!result.containsKey("message")) {
                result.put("message", ok ? "保存しました" : "保存に失敗しました");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("ok", false);
            result.put("message", "入力形式を確認してください（日時やID）");
        }

        // PRG redirect on success
        if (ok) {
            String range = req.getParameter("range");
            String url = req.getContextPath() + "/adminSchedules?saved=1"
                    + ("past".equals(range) ? "&range=past" : "");
            req.setAttribute("redirectUrl", url);
            resc.setTarget("redirect");
            resc.setResult(result);
            return resc;
        }

        // forward on failure
        repopulateForJsp(result, dao, req);
        resc.setResult(result);
        resc.setTarget("admin_schedule_manage");
        return resc;
    }

    private void repopulateForJsp(Map<String, Object> result, AdminScheduleDao dao, HttpServletRequest req) {
        boolean past = "past".equals(req.getParameter("range"));
        result.put("data", dao.selectByRange(past));   
        result.put("movies", dao.selectMovieOptions());
        result.put("screens", dao.selectScreenOptions());
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String first(RequestContext reqc, String key) {
        String[] arr = reqc.getParameter(key);
        if (arr == null || arr.length == 0) return null;
        return arr[0];
    }

    private String toTimestampString(String datetimeLocal) {
        if (datetimeLocal == null || datetimeLocal.isBlank()) return null;
        String v = datetimeLocal.trim().replace('T', ' ');
        if (v.length() == 16) v = v + ":00";
        return v;
    }

    private int intParam(RequestContext reqc, String key, int def) {
        String[] arr = reqc.getParameter(key);
        if (arr == null || arr.length == 0 || arr[0] == null || arr[0].isBlank()) return def;
        try { return Integer.parseInt(arr[0]); } catch (Exception e) { return def; }
    }

    private String toTimestampStringFromLdt(java.time.LocalDateTime dt) {
        return dt.toString().replace('T', ' ') + ":00";
    }
}