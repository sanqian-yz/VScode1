package com.shego.dao;

import com.shego.model.CartItem;
import com.shego.model.Order;
import com.shego.model.OrderItem;
import com.shego.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDao {
    private final CartDao cartDao = new CartDao();

    public int createOrderFromCart(int userId) throws SQLException {
        String cartSql = "SELECT c.product_id,c.quantity,p.price,p.stock,p.name FROM cart_items c "
                + "JOIN products p ON c.product_id=p.id WHERE c.user_id=?";
        String orderSql = "INSERT INTO orders(user_id,total_amount,status,delivery_status) VALUES(?,?,?,?)";
        String orderItemSql = "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES(?,?,?,?)";
        String stockSql = "UPDATE products SET stock=stock-? WHERE id=? AND stock>=?";

        try (Connection connection = DBUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<CartItem> cartItems = new ArrayList<CartItem>();
                BigDecimal total = BigDecimal.ZERO;

                try (PreparedStatement cartStatement = connection.prepareStatement(cartSql)) {
                    cartStatement.setInt(1, userId);
                    try (ResultSet resultSet = cartStatement.executeQuery()) {
                        while (resultSet.next()) {
                            CartItem item = new CartItem();
                            item.setProductId(resultSet.getInt("product_id"));
                            item.setQuantity(resultSet.getInt("quantity"));
                            BigDecimal price = resultSet.getBigDecimal("price");
                            if (price == null) {
                                price = BigDecimal.ZERO;
                            }
                            com.shego.model.Product product = new com.shego.model.Product();
                            product.setPrice(price);
                            product.setStock(resultSet.getInt("stock"));
                            product.setName(resultSet.getString("name"));
                            item.setProduct(product);
                            cartItems.add(item);
                            total = total.add(item.getSubtotal());
                        }
                    }
                }

                if (cartItems.isEmpty()) {
                    connection.rollback();
                    return -1;
                }

                int orderId;
                try (PreparedStatement orderStatement = connection.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    orderStatement.setInt(1, userId);
                    orderStatement.setBigDecimal(2, total);
                    orderStatement.setString(3, "PAID");
                    orderStatement.setString(4, "PENDING");
                    orderStatement.executeUpdate();
                    try (ResultSet keys = orderStatement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("创建订单失败，未返回主键");
                        }
                        orderId = keys.getInt(1);
                    }
                }

                try (PreparedStatement itemStatement = connection.prepareStatement(orderItemSql);
                     PreparedStatement stockStatement = connection.prepareStatement(stockSql)) {
                    for (CartItem item : cartItems) {
                        itemStatement.setInt(1, orderId);
                        itemStatement.setInt(2, item.getProductId());
                        itemStatement.setInt(3, item.getQuantity());
                        itemStatement.setBigDecimal(4, item.getProduct().getPrice());
                        itemStatement.addBatch();

                        stockStatement.setInt(1, item.getQuantity());
                        stockStatement.setInt(2, item.getProductId());
                        stockStatement.setInt(3, item.getQuantity());
                        stockStatement.addBatch();
                    }
                    itemStatement.executeBatch();
                    int[] stocks = stockStatement.executeBatch();
                    for (int affected : stocks) {
                        if (affected == 0) {
                            throw new SQLException("库存不足，无法下单");
                        }
                    }
                }

                cartDao.clearByUser(userId, connection);
                connection.commit();
                return orderId;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Order> listByUser(int userId) throws SQLException {
        String sql = "SELECT o.id,o.user_id,o.total_amount,o.status,o.delivery_status,o.delivery_staff_id,o.created_at,u.username,du.username AS delivery_name "
                + "FROM orders o JOIN users u ON o.user_id=u.id LEFT JOIN users du ON o.delivery_staff_id=du.id "
                + "WHERE o.user_id=? ORDER BY o.id DESC";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapOrders(resultSet);
            }
        }
    }

    public List<Order> listAll() throws SQLException {
        String sql = "SELECT o.id,o.user_id,o.total_amount,o.status,o.delivery_status,o.delivery_staff_id,o.created_at,u.username,du.username AS delivery_name "
                + "FROM orders o JOIN users u ON o.user_id=u.id LEFT JOIN users du ON o.delivery_staff_id=du.id ORDER BY o.id DESC";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapOrders(resultSet);
        }
    }

    public Order findDetail(int orderId, Integer userIdLimit) throws SQLException {
        String orderSql = "SELECT o.id,o.user_id,o.total_amount,o.status,o.delivery_status,o.delivery_staff_id,o.created_at,u.username,du.username AS delivery_name "
                + "FROM orders o JOIN users u ON o.user_id=u.id LEFT JOIN users du ON o.delivery_staff_id=du.id WHERE o.id=?";
        String itemSql = "SELECT oi.id,oi.order_id,oi.product_id,oi.quantity,oi.unit_price,p.name FROM order_items oi "
                + "JOIN products p ON oi.product_id=p.id WHERE oi.order_id=? ORDER BY oi.id";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement orderStatement = connection.prepareStatement(orderSql)) {
            orderStatement.setInt(1, orderId);
            try (ResultSet orderRs = orderStatement.executeQuery()) {
                if (!orderRs.next()) {
                    return null;
                }
                Order order = mapOrder(orderRs);
                if (userIdLimit != null && order.getUserId() != userIdLimit) {
                    return null;
                }
                try (PreparedStatement itemStatement = connection.prepareStatement(itemSql)) {
                    itemStatement.setInt(1, orderId);
                    try (ResultSet itemRs = itemStatement.executeQuery()) {
                        List<OrderItem> items = new ArrayList<OrderItem>();
                        while (itemRs.next()) {
                            OrderItem item = new OrderItem();
                            item.setId(itemRs.getInt("id"));
                            item.setOrderId(itemRs.getInt("order_id"));
                            item.setProductId(itemRs.getInt("product_id"));
                            item.setQuantity(itemRs.getInt("quantity"));
                            item.setUnitPrice(itemRs.getBigDecimal("unit_price"));
                            item.setProductName(itemRs.getString("name"));
                            items.add(item);
                        }
                        order.setItems(items);
                    }
                }
                return order;
            }
        }
    }

    public List<Order> listAvailableForDelivery() throws SQLException {
        String sql = "SELECT o.id,o.user_id,o.total_amount,o.status,o.delivery_status,o.delivery_staff_id,o.created_at,u.username,du.username AS delivery_name "
                + "FROM orders o JOIN users u ON o.user_id=u.id LEFT JOIN users du ON o.delivery_staff_id=du.id "
                + "WHERE o.delivery_status IN ('PENDING','DELIVERING') ORDER BY o.id DESC";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapOrders(rs);
        }
    }

    public List<Order> listByDeliveryStaff(int deliveryStaffId) throws SQLException {
        String sql = "SELECT o.id,o.user_id,o.total_amount,o.status,o.delivery_status,o.delivery_staff_id,o.created_at,u.username,du.username AS delivery_name "
                + "FROM orders o JOIN users u ON o.user_id=u.id LEFT JOIN users du ON o.delivery_staff_id=du.id "
                + "WHERE o.delivery_staff_id=? ORDER BY o.id DESC";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, deliveryStaffId);
            try (ResultSet rs = statement.executeQuery()) {
                return mapOrders(rs);
            }
        }
    }

    public boolean claimOrder(int orderId, int deliveryStaffId) throws SQLException {
        String sql = "UPDATE orders SET delivery_staff_id=?,delivery_status='DELIVERING' WHERE id=? AND delivery_status='PENDING'";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, deliveryStaffId);
            statement.setInt(2, orderId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateDeliveryStatus(int orderId, int deliveryStaffId, String status) throws SQLException {
        String sql = "UPDATE orders SET delivery_status=? WHERE id=? AND delivery_staff_id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, orderId);
            statement.setInt(3, deliveryStaffId);
            return statement.executeUpdate() > 0;
        }
    }

    private List<Order> mapOrders(ResultSet resultSet) throws SQLException {
        Map<Integer, Order> map = new HashMap<Integer, Order>();
        while (resultSet.next()) {
            Order order = mapOrder(resultSet);
            map.put(order.getId(), order);
        }
        return new ArrayList<Order>(map.values());
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setUsername(rs.getString("username"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setDeliveryStatus(rs.getString("delivery_status"));
        int deliveryStaffId = rs.getInt("delivery_staff_id");
        if (!rs.wasNull()) {
            order.setDeliveryStaffId(deliveryStaffId);
        }
        order.setDeliveryStaffName(rs.getString("delivery_name"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        order.setCreatedAt(createdAt);
        return order;
    }
}
