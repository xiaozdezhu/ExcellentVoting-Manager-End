package controllers;

import org.codehaus.jackson.JsonNode;
import play.Logger;
import play.Play;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import utils.JSONUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felix
 * Date :  16/4/4.
 * Desc :
 */
public class Secure extends Controller {

    private static final int TIME_USER_COOKIE = Integer.parseInt(Play.configuration.getProperty("user.cookie.maxAge", "3600"));


    private static final String ADMIN_COOKIE = "vote_admin";

    @Before(unless = {"login", "toLogin", "logout"})
    static void checkAuthenticated() {
        // 验证用户是否登录
        Logger.info("Secure: 验证用户是否登录");
        Logger.info("Session time: " + Play.configuration.getProperty("application.session.maxAge"));
        if (null == session.get("username")) {
            redirect("/login");
        }
    }

    /**
     * 登录页
     * 取用户cookie信息登录
     */
    public static void login() {
        Logger.info("进入登录页面");
        // 取用户cookie信息
        Http.Cookie adminCookie = request.cookies.get(ADMIN_COOKIE);
        if (null != adminCookie) { // cookie 不为空 取用户信息登录
            JsonNode userInfo = JSONUtils.toJsonNode(adminCookie.value);
            String username = userInfo.get("username").asText();
            String password = userInfo.get("password").asText();
            toLogin(username, password, false);
        } else {
            render();
        }
    }

    /**
     * 登录
     * @param username 用户名
     * @param password    密码
     * @param isKeepWeek    是否保持登录状态
     */
    public static void toLogin(final String username,
                               final String password,
                               boolean isKeepWeek) {
        Logger.info("登录");
        if (!verifyUser(validation, username, password)) {
            Validation.keep();
            login();
        } else {
            // 用户信息存入 session
            session.put("username", username);
            if (isKeepWeek) {
                // 保持登录一周
                Map<String, String> adminUser = new HashMap<String, String>(){{
                    put("username", username);
                    put("password", password);
                }};
                String adminInfo = JSONUtils.toJson(adminUser);
                response.setCookie(ADMIN_COOKIE, adminInfo, null, "/", TIME_USER_COOKIE, false);
            }
            // 登录后进入工程案例列表页面
            redirect("/department/0");
        }

    }

    /**
     * 退出登录
     */
    public static void logout() {
        Logger.info("退出登录");
        session.clear();
        response.removeCookie(ADMIN_COOKIE);
        redirect("/login");
    }


    /**
     * 验证用户
     * @param username 用户名
     * @param password   密码
     *  验证用户名密码是否正确
     */
    private static boolean verifyUser(Validation validation, String username, String password) {
        validation.required(username).message("用户名");
        validation.required(password).message("密码");

        String adminName = Play.configuration.getProperty("administrator.username", "error");
        String adminPasswd =  Play.configuration.getProperty("administrator.password", "error");

        validation.equals(username, adminName).message("用户名");
        validation.equals(password, adminPasswd).message("密码");
        if (Validation.hasErrors()) {
            return false;
        } else {
            return true;
        }
    }

}
