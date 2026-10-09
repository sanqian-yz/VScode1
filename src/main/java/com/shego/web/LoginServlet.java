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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("csrfToken", WebUtil.ensureCsrfToken(request));
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.validateCsrf(request)) {
            request.setAttribute("error", "请求已失效，请刷新后重试");
            doGet(request, response);
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (isBlank(username) || isBlank(password)) {
            request.setAttribute("error", "用户名和密码不能为空");
            doGet(request, response);
            return;
        }

        try {
            User user = userDao.findByUsername(username.trim());
            if (user == null || !PasswordUtil.matches(password, user.getSalt(), user.getPasswordHash())) {
                request.setAttribute("error", "用户名或密码错误");
                doGet(request, response);
                return;
            }

            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            User safeUser = new User();
            safeUser.setId(user.getId());
            safeUser.setUsername(user.getUsername());
            safeUser.setPhone(user.getPhone());
            safeUser.setRole(user.getRole());
            session.setAttribute(WebUtil.SESSION_USER, safeUser);

            response.sendRedirect(request.getContextPath() + "/products");
        } catch (SQLException e) {
            throw new ServletException("登录失败", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
