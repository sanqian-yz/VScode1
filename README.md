# 舍购校园即时电商

这是 JSP + Servlet + JavaBean + DAO 的 MVC 基础骨架，当前包含商品搜索/展示和登录入口。

## 环境
- JDK 1.8、Maven、Tomcat 9、SQL Server
- 执行 `src/main/resources/schema.sql` 初始化数据库
- 通过 JVM 参数配置数据库：`-Dshego.db.url=... -Dshego.db.user=... -Dshego.db.password=...`

## 运行
1. 修改数据库连接参数，执行初始化脚本。
2. 执行 `mvn clean package`。
3. 将 `target/shego.war` 部署到 Tomcat 9 的 `webapps` 目录。
4. 访问 `/shego/products`。

密码摘要��前采用 SHA-256；正式部署应改为带随机盐的注册/登录一致校验，并补充注册、购物车、订单、管理员和配送员模块。
