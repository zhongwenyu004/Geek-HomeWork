package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.service.DerbyConnectionService;
import org.geektimes.web.mvc.controller.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

@Path("/login")
public class LoginController implements RestController {
    @Path("/usercheck")
    public boolean login(HttpServletRequest request, HttpServletResponse response){
        String email = request.getQueryString().split("&")[0].split("=")[1];
        String password = request.getQueryString().split("&")[1].split("=")[1];
        System.out.println("email:"+email+";密码："+password);
        DerbyConnectionService derbyConnectionService = new DerbyConnectionService();
        boolean result = derbyConnectionService.checkuser(email,password);
        return true;
    }
}
