package com.shego.web;

import com.shego.dao.OrderDao;
import com.shego.model.User;
import com.shego.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/delivery/orders")
public class DeliveryOrderServlet extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(WebUtil.SESSION_USER);
        try {
            request.setAttribute("availableOrders", orderDao.listAvailableForDelivery());
            request.setAttribute("myOrders", orderDao.listByDeliveryStaff(user.getId()));
            request.setAttribute("csrfToken", WebUtil.ensureCsrfToken(request));
            request.getRequestDispatcher("/WEB-INF/views/delivery/orders.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("配送订单加载失败", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.validateCsrf(request)) {
            request.getSession().setAttribute("flash", "请求已失效，请重试");
            response.sendRedirect(request.getContextPath() + "/delivery/orders");
            return;
        }

        User user = (User) request.getSession().getAttribute(WebUtil.SESSION_USER);
        String action = request.getParameter("action");
        int orderId = WebUtil.parsePositiveInt(request.getParameter("orderId"), -1);

        if (orderId <= 0) {
            request.getSession().setAttribute("flash", "订单参数不正确");
            response.sendRedirect(request.getContextPath() + "/delivery/orders");
            return;
        }

        try {
            boolean ok;
            if ("claim".equals(action)) {
                ok = orderDao.claimOrder(orderId, user.getId());
            } else if ("finish".equals(action)) {
                ok = orderDao.updateDeliveryStatus(orderId, user.getId(), "DONE");
            } else {
                ok = orderDao.updateDeliveryStatus(orderId, user.getId(), "DELIVERING");
            }
            request.getSession().setAttribute("flash", ok ? "状态更新成功" : "状态更新失败，请检查权限或状态");
            response.sendRedirect(request.getContextPath() + "/delivery/orders");
        } catch (SQLException e) {
            throw new ServletException("配送状态更新失败", e);
        }
    }
}
