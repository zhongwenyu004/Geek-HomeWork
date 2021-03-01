package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


@Path("/login")
public class LoginController implements RestController {


    @GET
    @POST
    @Path("/usercheck")
   public String login(HttpServletRequest request, HttpServletResponse response){
        String email =request.getQueryString().split("&")[0].split("=")[1];
        String password =request.getQueryString().split("&")[1].split("=")[1];
        System.out.println("验证email:"+email+";验证密码："+password);
        return "ok";
    }
}
