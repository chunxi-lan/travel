package web.travel.web.servlet;

import web.travel.domain.Result;
import web.travel.domain.User;
import web.travel.service.UserService;
import web.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

    private UserService service = new UserServiceImpl();
    /**
     * 注册功能
     */
    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证码校验
        String check = request.getParameter("check");
        //从sesion中获取验证码
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        //保证验证码只能使用一次
        session.removeAttribute("CHECKCODE_SERVER");
        //比较验证码
        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)) {
            //验证码错误，注册失败
            Result info = new Result();
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            //将info对象序列化为json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            return;
        }
        //通过验证码校验
        //1.获取数据
        Map<String, String[]> map = request.getParameterMap();

        //2.封装对象
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.调用service
        boolean flag = service.regist(user);
        Result info = new Result();
        //4.响应结果
        if (flag) {
            //注册成功
            info.setFlag(true);
        } else {
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败！！！");
        }
        writeValue(info,response);
    }

    /**
     * 登陆功能
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证码校验
        String check = request.getParameter("check");
        //从sesion中获取验证码
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        //保证验证码只能使用一次
        session.removeAttribute("CHECKCODE_SERVER");
        //比较验证码
        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)) {
            //验证码错误，登录失败
            Result info = new Result();
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            writeValue(info,response);
            return;
        }
        //通过验证码校验

        //1.获取用户名和密码数据
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User loginUser = service.login(user);

        Result info = new Result();

        if (loginUser == null) {
            info.setFlag(false);
            info.setErrorMsg("用户名或密码错误！");
        }
        if (loginUser != null && !"Y".equals(loginUser.getStatus())) {
            info.setFlag(false);
            info.setErrorMsg("您尚未激活，请激活");
        }
        if (loginUser != null && "Y".equals(loginUser.getStatus())) {
            request.getSession().setAttribute("user", loginUser);
            info.setFlag(true);
        }
        writeValue(info,response);
    }

    /**
     * 查找一个
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object user = request.getSession().getAttribute("user");
        writeValue(user,response);
    }

    /**
     * 退出功能
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();

        //跳转登录页面
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    /**
     * 激活功能
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code != null){
            boolean flag = service.active(code);

            String msg = null;
            if (flag){
                msg = "激活成功，请<a href='login.html'>登陆</a>";
            }else {
                msg = "激活失败，请联系管理员";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }
}
