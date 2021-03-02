package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.service.DerbyConnectionService;
import org.geektimes.web.mvc.controller.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


@Path("/login")
public class LoginController implements RestController {
    @POST
    @Path("/usercheck")
   public boolean login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String type = request.getMethod();
        String name="";
        String password="";
        if(!"POST".equals(type)){
            name =request.getQueryString().split("&")[0].split("=")[1];
            password =request.getQueryString().split("&")[1].split("=")[1];
        }else{
            name =request.getParameter("name");
            password = request.getParameter("password");
        }
        name= URLDecoder.decode(name,"utf-8");
        DerbyConnectionService derbyConnectionService = new DerbyConnectionService();
        System.out.println("验证email:"+name+";验证密码："+password);
        return derbyConnectionService.checkuser(name,password);
    }
}
