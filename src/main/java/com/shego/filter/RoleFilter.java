package com.shego.filter;

import com.shego.model.User;
import com.shego.util.WebUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RoleFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute(WebUtil.SESSION_USER);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String uri = req.getRequestURI();
        boolean adminUrl = uri.startsWith(req.getContextPath() + "/admin/");
        boolean deliveryUrl = uri.startsWith(req.getContextPath() + "/delivery/");

        boolean allowed = true;
        if (adminUrl) {
            allowed = "ADMIN".equalsIgnoreCase(user.getRole());
        } else if (deliveryUrl) {
            allowed = "DELIVERY".equalsIgnoreCase(user.getRole());
        }

        if (!allowed) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("message", "无权限访问该页面");
            req.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(req, resp);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
