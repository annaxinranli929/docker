package logic;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class FrontServlet extends HttpServlet {
    private static final String[] forwardURL = {"/img", "/css", "/js"};

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        doPost(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        req.setCharacterEncoding("utf-8");

        // 画像ファイルなどサーブレットを通さないリクエストを流す
        String servletPath = req.getServletPath();
        for (String url : forwardURL) {
            if (servletPath.startsWith(url)) {
                req.getServletContext().getNamedDispatcher("default").forward(req, res);
                return;
            }
        }

        ApplicationController app = new WebApplicationController();
        RequestContext reqc = app.getRequestContext(req);
        ResponseContext resc = app.handleRequest(reqc);
        resc.setResponse(res);
        app.handleResponse(reqc, resc);
    }
}