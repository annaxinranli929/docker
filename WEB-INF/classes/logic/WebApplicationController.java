package logic;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import factory.AbstractFactory;
import command.AbstractCommand;

public class WebApplicationController implements ApplicationController {
    public RequestContext getRequestContext(Object request) {
        RequestContext reqc = new WebRequestContext();
        reqc.setRequest(request);
        return reqc;
    }

    public ResponseContext handleRequest(RequestContext reqc) {
        AbstractFactory factory = AbstractFactory.getFactory(reqc.getFactoryPath());
        AbstractCommand command = factory.createCommand();
        command.setDao(factory.createDao());
        command.init(reqc);
        ResponseContext resc = command.execute(new WebResponseContext());
        return resc;
    }
    
    public void handleResponse(RequestContext reqc, ResponseContext resc) {
        HttpServletRequest req = (HttpServletRequest) reqc.getRequest();
        HttpServletResponse res = (HttpServletResponse) resc.getResponse();
        try {
            if (resc.isRedirect()) {
                res.sendRedirect(resc.getTarget());
            } else {
                req.setAttribute("result", resc.getResult());
                req.getRequestDispatcher(resc.getTarget()).forward(req, res);
            }
        } catch(ServletException e){
            throw new RuntimeException(e);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}