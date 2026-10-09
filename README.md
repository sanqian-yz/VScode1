# 舍购校园即时电商平台（JSP + Servlet + JavaBean + DAO）

本项目基于 **JDK 1.8 + Tomcat 9 + SQL Server + javax.servlet 4.0.1**，采用传统 MVC 架构（JSP 视图 + Servlet 控制器 + JavaBean + DAO）。

## 功能清单

### 公共/认证
- 用户注册（随机盐 + SHA-256 摘要）
- 用户登录/退出
- UTF-8 过滤、登录过滤、角色过滤（普通用户/管理员/配送员）

### 普通用户
- 商品搜索与展示
- 加入购物车、修改数量、删除商品
- 模拟支付并生成订单
- 历史订单与订单详情查看

### 管理员
- 后台商品管理（新增/编辑/删除/列表）
- 查看全部订单

### 配送员
- 查看待配送/配送中订单
- 领取订单（待配送 -> 配送中）
- 更新订单为配送中/已完成
- 查看自己的配送记录

## 项目结构

```text
src/main/java/com/shego
├── dao
│   ├── UserDao.java
│   ├── ProductDao.java
│   ├── CartDao.java
│   └── OrderDao.java
├── filter
│   ├── EncodingFilter.java
│   ├── AuthFilter.java
│   └── RoleFilter.java
├── model
│   ├── User.java
│   ├── Product.java
│   ├── CartItem.java
│   ├── Order.java
│   └── OrderItem.java
├── util
│   ├── DBUtil.java
│   ├── PasswordUtil.java
│   └── WebUtil.java
└── web
    ├── LoginServlet.java
    ├── RegisterServlet.java
    ├── LogoutServlet.java
    ├── ProductServlet.java
    ├── CartServlet.java
    ├── CheckoutServlet.java
    ├── OrderServlet.java
    ├── AdminProductServlet.java
    ├── AdminOrderServlet.java
    └── DeliveryOrderServlet.java

src/main/webapp
├── assets/style.css
└── WEB-INF
    ├── web.xml
    └── views
        ├── common/header.jspf
        ├── login.jsp
        ├── register.jsp
        ├── products.jsp
        ├── cart.jsp
        ├── orders.jsp
        ├── order-detail.jsp
        ├── admin/products.jsp
        ├── admin/orders.jsp
        ├── delivery/orders.jsp
        └── error/403.jsp
```

## 数据库初始化（SQL Server）

1. 执行 `src/main/resources/schema.sql`
2. 脚本支持重复执行（含 `IF NOT EXISTS` / `IF OBJECT_ID ...`）
3. 默认演示账号：
   - 管理员：`admin / admin123`
   - 配送员：`delivery1 / delivery123`
   - 用户：`user1 / user123456`

> 上述仅为本地演示账号，不应视为生产环境默认口令策略。

## 数据库连接配置

通过 JVM 参数或环境变量配置（优先 JVM 参数）：

- `shego.db.url` / `SHEGO_DB_URL`
- `shego.db.user` / `SHEGO_DB_USER`
- `shego.db.password` / `SHEGO_DB_PASSWORD`

示例：

```bash
-Dshego.db.password="<your-db-password>"\
-Dshego.db.password="<your-db-password>"\
-Dshego.db.password="<your-db-password>"
```

## 构建与运行

1. `mvn clean package`
2. 将 `target/shego.war` 部署到 Tomcat 9 `webapps`
3. 访问：`/shego/products`

## 已知限制

- 当前支付为“模拟支付”，未接入第三方支付网关。
- 订单状态流转为教学简化版，未实现超时取消、售后、物流轨迹等高级能力。
- 未实现手机号短信验证，仅做基础格式校验。
