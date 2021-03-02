package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

@Path("/success")
public class SuccessController implements PageController {
@Path("/loginok")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable{
        return "loginok.jsp";
    }
}
