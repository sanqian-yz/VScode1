IF DB_ID(N'SheGo') IS NULL
BEGIN
    CREATE DATABASE SheGo;
END
GO

USE SheGo;
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id INT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(50) NOT NULL UNIQUE,
        password_hash CHAR(64) NOT NULL,
        salt CHAR(32) NOT NULL,
        phone VARCHAR(20) NULL,
        role VARCHAR(20) NOT NULL DEFAULT 'USER',
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

IF OBJECT_ID(N'dbo.products', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.products (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(500) NULL,
        price DECIMAL(10,2) NOT NULL,
        stock INT NOT NULL DEFAULT 0,
        image_url VARCHAR(500) NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

IF OBJECT_ID(N'dbo.cart_items', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.cart_items (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        product_id INT NOT NULL,
        quantity INT NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT uq_cart_user_product UNIQUE (user_id, product_id),
        CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES dbo.users(id),
        CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES dbo.products(id)
    );
END
GO

IF OBJECT_ID(N'dbo.orders', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.orders (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        total_amount DECIMAL(10,2) NOT NULL,
        status VARCHAR(20) NOT NULL DEFAULT 'PAID',
        delivery_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
        delivery_staff_id INT NULL,
        delivery_address NVARCHAR(255) NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES dbo.users(id),
        CONSTRAINT fk_order_delivery_staff FOREIGN KEY (delivery_staff_id) REFERENCES dbo.users(id)
    );
END
GO

IF OBJECT_ID(N'dbo.order_items', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_items (
        id INT IDENTITY(1,1) PRIMARY KEY,
        order_id INT NOT NULL,
        product_id INT NOT NULL,
        quantity INT NOT NULL,
        unit_price DECIMAL(10,2) NOT NULL,
        CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES dbo.orders(id),
        CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES dbo.products(id)
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_products_name' AND object_id = OBJECT_ID('dbo.products'))
BEGIN
    CREATE INDEX idx_products_name ON dbo.products(name);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_cart_user' AND object_id = OBJECT_ID('dbo.cart_items'))
BEGIN
    CREATE INDEX idx_cart_user ON dbo.cart_items(user_id);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_orders_user' AND object_id = OBJECT_ID('dbo.orders'))
BEGIN
    CREATE INDEX idx_orders_user ON dbo.orders(user_id);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_orders_delivery_status' AND object_id = OBJECT_ID('dbo.orders'))
BEGIN
    CREATE INDEX idx_orders_delivery_status ON dbo.orders(delivery_status);
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.products)
BEGIN
    INSERT INTO dbo.products(name, description, price, stock, image_url) VALUES
    (N'可乐', N'冰镇可乐 330ml', 3.00, 100, NULL),
    (N'泡面', N'宿舍速食红烧牛肉面', 5.50, 120, NULL),
    (N'抽纸', N'基础日用品 3 包装', 12.90, 80, NULL),
    (N'矿泉水', N'550ml 瓶装水', 2.00, 200, NULL);
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'admin')
BEGIN
    INSERT INTO dbo.users(username, password_hash, salt, phone, role)
    VALUES (N'admin', '0e14da9d8aadc6fc27decd1b05097cd4312903005cbece01043ee2d4316b2398', '00112233445566778899aabbccddeeff', NULL, 'ADMIN');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'delivery1')
BEGIN
    INSERT INTO dbo.users(username, password_hash, salt, phone, role)
    VALUES (N'delivery1', '5c485f5af389a40092e31d72df91a637309d0b53d07e3a3c62f17fbe96134ab9', '11112222333344445555666677778888', NULL, 'DELIVERY');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE username = N'user1')
BEGIN
    INSERT INTO dbo.users(username, password_hash, salt, phone, role)
    VALUES (N'user1', 'fb39645cbd85d400205ebf378403bae75640e6bf5325eb88a373739b4e07b564', 'abcdef0123456789abcdef0123456789', '13800000000', 'USER');
END
GO

-- 演示账号说明：
-- admin / admin123 (管理员)
-- delivery1 / delivery123 (配送员)
-- user1 / user123456 (普通用户)
-- 仅用于本地开发演示，请在生产环境通过注册或后台安全创建账号。
