package logic;

import jakarta.servlet.http.HttpServletResponse;

public class WebResponseContext implements ResponseContext {
    private Object result;
    private String target;
    private HttpServletResponse res;
    private boolean redirect;

    public void setTarget(String transferInfo) {
        target = "WEB-INF/jsp/" + transferInfo + ".jsp";
    }
    public void setTargetURI(String uri) {
        target = uri;
    }
    public String getTarget() {
        return target;
    }
    public void setResult(Object result) {
        this.result = result;
    }
    public Object getResult() {
        return result;
    }
    public Object getResponse() {
        return res;
    }
    public void setResponse(Object response) {
        res = (HttpServletResponse) response;
    }
    public boolean isRedirect() {
        return redirect;
    }
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }
} 