package com.shego.web;

import com.shego.dao.UserDao;
import com.shego.model.User;
import com.shego.util.PasswordUtil;
import com.shego.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("csrfToken", WebUtil.ensureCsrfToken(request));
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.validateCsrf(request)) {
            request.setAttribute("error", "请求已失效，请刷新后重试");
            doGet(request, response);
            return;
        }

        String username = trim(request.getParameter("username"));
        String phone = trim(request.getParameter("phone"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (username.length() < 3 || username.length() > 30) {
            request.setAttribute("error", "用户名长度需在 3-30 之间");
            doGet(request, response);
            return;
        }
        if (password == null || password.length() < 6) {
            request.setAttribute("error", "密码长度至少 6 位");
            doGet(request, response);
            return;
        }
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "两次密码输入不一致");
            doGet(request, response);
            return;
        }
        if (!phone.isEmpty() && !phone.matches("^1\\d{10}$")) {
            request.setAttribute("error", "手机号格式不正确");
            doGet(request, response);
            return;
        }

        try {
            if (userDao.findByUsername(username) != null) {
                request.setAttribute("error", "用户名已存在");
                doGet(request, response);
                return;
            }

            String salt = PasswordUtil.generateSaltHex();
            User user = new User();
            user.setUsername(username);
            user.setPhone(phone);
            user.setSalt(salt);
            user.setPasswordHash(PasswordUtil.hashPassword(password, salt));
            user.setRole("USER");
            userDao.create(user);

            response.sendRedirect(request.getContextPath() + "/login?registered=1");
        } catch (SQLException e) {
            throw new ServletException("注册失败", e);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
