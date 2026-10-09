package com.shego.web;

import com.shego.dao.OrderDao;
import com.shego.model.Order;
import com.shego.model.User;
import com.shego.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(WebUtil.SESSION_USER);
        String orderIdStr = request.getParameter("id");

        try {
            if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
                int orderId = WebUtil.parsePositiveInt(orderIdStr, -1);
                Integer limitUserId = "ADMIN".equalsIgnoreCase(user.getRole()) ? null : user.getId();
                Order order = orderDao.findDetail(orderId, limitUserId);
                if (order == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "订单不存在");
                    return;
                }
                request.setAttribute("order", order);
                request.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(request, response);
            } else {
                request.setAttribute("orders", orderDao.listByUser(user.getId()));
                request.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("订单查询失败", e);
        }
    }
}
