package com.shego.dao;

import com.shego.model.Product;
import com.shego.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    public List<Product> find(String keyword) throws SQLException {
        String key = keyword == null ? "" : keyword.trim();
        String sql = "SELECT id,name,description,price,stock,image_url FROM products "
                + "WHERE name LIKE ? OR description LIKE ? ORDER BY id DESC";
        List<Product> list = new ArrayList<Product>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String fuzzy = "%" + key + "%";
            statement.setString(1, fuzzy);
            statement.setString(2, fuzzy);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(map(resultSet));
                }
            }
        }
        return list;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT id,name,description,price,stock,image_url FROM products WHERE id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return map(resultSet);
                }
            }
        }
        return null;
    }

    public int create(Product product) throws SQLException {
        String sql = "INSERT INTO products(name,description,price,stock,image_url) VALUES(?,?,?,?,?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getStock());
            statement.setString(5, product.getImageUrl());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE products SET name=?,description=?,price=?,stock=?,image_url=? WHERE id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getStock());
            statement.setString(5, product.getImageUrl());
            statement.setInt(6, product.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Product map(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        BigDecimal price = resultSet.getBigDecimal("price");
        product.setPrice(price == null ? BigDecimal.ZERO : price);
        product.setStock(resultSet.getInt("stock"));
        product.setImageUrl(resultSet.getString("image_url"));
        return product;
    }
}
