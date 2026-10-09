package com.shego.dao;

import com.shego.model.CartItem;
import com.shego.model.Product;
import com.shego.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
    public List<CartItem> listByUser(int userId) throws SQLException {
        String sql = "SELECT c.id,c.user_id,c.product_id,c.quantity,p.name,p.description,p.price,p.stock,p.image_url "
                + "FROM cart_items c JOIN products p ON c.product_id=p.id WHERE c.user_id=? ORDER BY c.id DESC";
        List<CartItem> items = new ArrayList<CartItem>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CartItem item = new CartItem();
                    item.setId(resultSet.getInt("id"));
                    item.setUserId(resultSet.getInt("user_id"));
                    item.setProductId(resultSet.getInt("product_id"));
                    item.setQuantity(resultSet.getInt("quantity"));

                    Product product = new Product();
                    product.setId(resultSet.getInt("product_id"));
                    product.setName(resultSet.getString("name"));
                    product.setDescription(resultSet.getString("description"));
                    BigDecimal price = resultSet.getBigDecimal("price");
                    product.setPrice(price == null ? BigDecimal.ZERO : price);
                    product.setStock(resultSet.getInt("stock"));
                    product.setImageUrl(resultSet.getString("image_url"));
                    item.setProduct(product);

                    items.add(item);
                }
            }
        }
        return items;
    }

    public void addOrIncrease(int userId, int productId, int quantity) throws SQLException {
        String updateSql = "UPDATE cart_items SET quantity=quantity+? WHERE user_id=? AND product_id=?";
        String insertSql = "INSERT INTO cart_items(user_id,product_id,quantity) VALUES(?,?,?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement update = connection.prepareStatement(updateSql);
             PreparedStatement insert = connection.prepareStatement(insertSql)) {
            update.setInt(1, quantity);
            update.setInt(2, userId);
            update.setInt(3, productId);
            int affected = update.executeUpdate();
            if (affected == 0) {
                insert.setInt(1, userId);
                insert.setInt(2, productId);
                insert.setInt(3, quantity);
                insert.executeUpdate();
            }
        }
    }

    public boolean updateQuantity(int userId, int productId, int quantity) throws SQLException {
        String sql = "UPDATE cart_items SET quantity=? WHERE user_id=? AND product_id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteItem(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id=? AND product_id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            return statement.executeUpdate() > 0;
        }
    }

    public void clearByUser(int userId, Connection connection) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}
