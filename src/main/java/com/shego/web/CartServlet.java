package com.shego.web;

import com.shego.dao.CartDao;
import com.shego.model.User;
import com.shego.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private final CartDao cartDao = new CartDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(WebUtil.SESSION_USER);
        try {
            request.setAttribute("items", cartDao.listByUser(user.getId()));
            request.setAttribute("csrfToken", WebUtil.ensureCsrfToken(request));
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("购物车加载失败", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.validateCsrf(request)) {
            request.getSession().setAttribute("flash", "请求已失效，请重试");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        User user = (User) request.getSession().getAttribute(WebUtil.SESSION_USER);
        String action = request.getParameter("action");
        int productId = WebUtil.parsePositiveInt(request.getParameter("productId"), -1);
        int quantity = WebUtil.parsePositiveInt(request.getParameter("quantity"), 1);

        if (productId <= 0) {
            request.getSession().setAttribute("flash", "商品参数不正确");
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        try {
            if ("delete".equals(action)) {
                cartDao.deleteItem(user.getId(), productId);
                request.getSession().setAttribute("flash", "已从购物车移除商品");
            } else if ("update".equals(action)) {
                cartDao.updateQuantity(user.getId(), productId, quantity);
                request.getSession().setAttribute("flash", "购物车数量已更新");
            } else {
                cartDao.addOrIncrease(user.getId(), productId, quantity);
                request.getSession().setAttribute("flash", "已加入购物车");
            }
            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (SQLException e) {
            throw new ServletException("购物车操作失败", e);
        }
    }
}
