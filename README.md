# 商品价格库系统

这是一个使用Java Swing开发的商品价格库系统，允许用户添加商品、从Excel导入商品、以及通过商品名称进行搜索。

## 功能描述

- **添加商品**：手动添加商品名称和价格到数据库。
- **从Excel导入**：从Excel文件中批量导入商品信息。
- **搜索商品**：通过商品名称进行模糊搜索并显示结果。

## 先决条件

在运行本项目之前，请确保您的环境中已经安装了以下软件：

- Java Development Kit (JDK) 8 或更高版本
- MySQL 数据库
- Maven (可选)

## 数据库设置

1. 创建数据库：

   ```sql
   CREATE DATABASE IF NOT EXISTS product_price_db;
   USE product_price_db;
   
   CREATE TABLE IF NOT EXISTS products (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       price DECIMAL(10, 2) NOT NULL
   );
   
2. 配置数据库连接：

在代码中的 DB_URL、DB_USER 和 DB_PASSWORD 常量中，修改为你自己的数据库URL、用户名和密码。

## 编译和运行
使用IDE
1. 克隆或下载本仓库。
2. 打开IDE并导入项目。
3. 确保所有依赖库都已添加到项目中（如Apache POI库）。
4. 运行 ProductPriceDatabase 类中的 main 方法。
   
使用命令行

1. 克隆或下载本仓库。
2. 打开终端并导航到项目目录。
3. 使用以下命令编译和运行项目：
   
   ```sh
   javac -cp ".:lib/*" -d bin src/org/example/ProductPriceDatabase.java
   java -cp ".:bin:lib/*" org.example.ProductPriceDatabase
## 使用指南
1. 添加商品：
在“商品名”字段中输入商品名称。
在“价格”字段中输入商品价格。
点击“添加商品”按钮。
2. 从Excel导入：
点击“从 Excel 中添加”按钮。
选择包含商品信息的Excel文件（文件扩展名为.xls）。
确保Excel文件的第一列是商品名称，第二列是价格。
3. 搜索商品
在搜索栏中输入要搜索的商品名称或关键字。
点击“查找”按钮。
结果将显示在下方的结果区域。
## 依赖库
本项目使用以下依赖库：
1. MySQL Connector
2. Apache POI (处理Excel文件)
