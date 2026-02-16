package command.admin;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import bean.admin.AdminLoginBean;
import command.AbstractCommand;
import logic.RequestContext;
import logic.ResponseContext;

public class AdminLoginCommand extends AbstractCommand<AdminLoginBean> {

    // hardcoded for now (change later if you add AdminDao)
    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PW = "admin";

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        HttpServletRequest req = (HttpServletRequest) reqc.getRequest();
        HttpSession session = req.getSession();

        String adminId = param(reqc, "adminId");
        String password = param(reqc, "password");

        // First access (no params) -> show login JSP
        if (adminId == null || password == null) {
            resc.setTarget("admin_login");
            return resc;
        }

        Map<String, Object> result = new HashMap<>();

        // Auth check
        if (ADMIN_ID.equals(adminId) && ADMIN_PW.equals(password)) {
            session.setAttribute("isAdmin", Boolean.TRUE);

            result.put("ok", true);
            result.put("message", "ログイン成功");
            resc.setResult(result);

            // ✅ redirect to schedules (GET)
            String url = req.getContextPath() + "/adminSchedules";
            req.setAttribute("redirectUrl", url);

            resc.setTarget("redirect"); // -> /WEB-INF/jsp/redirect.jsp
            return resc;
        }

        // Failed login
        result.put("ok", false);
        result.put("message", "ID または パスワードが違います");
        resc.setResult(result);
        resc.setTarget("admin_login");
        return resc;
    }

    private String param(RequestContext reqc, String key) {
        String[] arr = reqc.getParameter(key);
        return (arr == null || arr.length == 0) ? null : arr[0];
    }
}
