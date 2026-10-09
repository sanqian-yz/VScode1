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

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.validateCsrf(request)) {
            request.getSession().setAttribute("flash", "请求已失效，请重试");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        User user = (User) request.getSession().getAttribute(WebUtil.SESSION_USER);
        try {
            int orderId = orderDao.createOrderFromCart(user.getId());
            if (orderId <= 0) {
                request.getSession().setAttribute("flash", "购物车为空，无法下单");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            request.getSession().setAttribute("flash", "支付成功，订单已创建：#" + orderId);
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (SQLException e) {
            request.getSession().setAttribute("flash", "下单失败：" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
}
