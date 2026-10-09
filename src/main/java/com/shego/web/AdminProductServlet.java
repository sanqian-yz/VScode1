package com.shego.web;

import com.shego.dao.ProductDao;
import com.shego.model.Product;
import com.shego.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/admin/products")
public class AdminProductServlet extends HttpServlet {
    private final ProductDao productDao = new ProductDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String editId = request.getParameter("editId");
            if (editId != null && !editId.trim().isEmpty()) {
                Product editProduct = productDao.findById(WebUtil.parsePositiveInt(editId, -1));
                request.setAttribute("editProduct", editProduct);
            }
            request.setAttribute("products", productDao.find(request.getParameter("keyword")));
            request.setAttribute("csrfToken", WebUtil.ensureCsrfToken(request));
            request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("后台商品加载失败", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.validateCsrf(request)) {
            request.getSession().setAttribute("flash", "请求已失效，请重试");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        String action = request.getParameter("action");
        int id = WebUtil.parsePositiveInt(request.getParameter("id"), -1);
        String name = trim(request.getParameter("name"));
        String description = trim(request.getParameter("description"));
        String imageUrl = trim(request.getParameter("imageUrl"));
        int stock = WebUtil.parsePositiveInt(request.getParameter("stock"), 0);
        BigDecimal price = parseMoney(request.getParameter("price"));

        try {
            if ("delete".equals(action)) {
                if (id > 0) {
                    productDao.delete(id);
                    request.getSession().setAttribute("flash", "商品已删除");
                }
            } else {
                if (name.isEmpty() || price == null || stock < 0) {
                    request.getSession().setAttribute("flash", "商品参数不合法");
                    response.sendRedirect(request.getContextPath() + "/admin/products");
                    return;
                }
                Product product = new Product();
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setStock(stock);
                product.setImageUrl(imageUrl);

                if ("update".equals(action) && id > 0) {
                    product.setId(id);
                    productDao.update(product);
                    request.getSession().setAttribute("flash", "商品已更新");
                } else {
                    productDao.create(product);
                    request.getSession().setAttribute("flash", "商品已新增");
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/products");
        } catch (SQLException e) {
            throw new ServletException("商品操作失败", e);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal parseMoney(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
