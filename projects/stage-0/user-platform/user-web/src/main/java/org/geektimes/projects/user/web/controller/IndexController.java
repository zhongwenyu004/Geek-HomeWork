package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


@Path("/index")
public class IndexController implements PageController {

    @GET
    @POST
    @Path("/login") // /login -> IndexController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "login-form.jsp";
    }


}
