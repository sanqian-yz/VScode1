package com.shego.web;

import com.shego.dao.ProductDao;
import com.shego.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private final ProductDao productDao = new ProductDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        try {
            req.setAttribute("products", productDao.find(keyword));
            req.setAttribute("csrfToken", WebUtil.ensureCsrfToken(req));
            req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("商品查询失败", e);
        }
    }
}
