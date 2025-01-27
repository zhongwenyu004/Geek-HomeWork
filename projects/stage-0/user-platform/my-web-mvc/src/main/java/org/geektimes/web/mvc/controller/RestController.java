package org.geektimes.web.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * REST 控制器（标记接口）
 *
 * @since 1.0
 */
public interface RestController extends Controller {
    boolean login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException;
}
