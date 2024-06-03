package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ProductPriceDatabase {
    private static final String DB_URL = "jdbc:mysql://localhost:3306";
    private static final String DB_USER = "name";
    private static final String DB_PASSWORD = "password";

    private JFrame frame;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField searchField;
    private JLabel resultLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProductPriceDatabase();
            }
        });
    }

    public ProductPriceDatabase() {
        frame = new JFrame("商品价格库");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        JPanel addPanel = new JPanel(new GridLayout(3, 2));
        nameField = new JTextField(20);
        priceField = new JTextField(20);
        JButton addButton = new JButton("添加商品");
        JButton importButton = new JButton("从 Excel 中添加");

        addPanel.add(new JLabel("商品名:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("价格:"));
        addPanel.add(priceField);
        addPanel.add(addButton);
        addPanel.add(importButton);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(60); // 增大3倍
        JButton searchButton = new JButton("查找");
        searchButton.setPreferredSize(new Dimension(100, 30)); // 固定高度
        resultLabel = new JLabel();
        resultLabel.setVerticalAlignment(SwingConstants.TOP); // 顶部对齐，方便查看结果

        JPanel searchTopPanel = new JPanel(new BorderLayout());
        searchTopPanel.add(searchField, BorderLayout.CENTER);
        searchTopPanel.add(searchButton, BorderLayout.EAST);

        JScrollPane resultScrollPane = new JScrollPane(resultLabel);
        resultScrollPane.setPreferredSize(new Dimension(600, 300)); // 增大3倍

        searchPanel.add(searchTopPanel, BorderLayout.NORTH);
        searchPanel.add(resultScrollPane, BorderLayout.CENTER);

        frame.add(addPanel, BorderLayout.NORTH);
        frame.add(searchPanel, BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProduct();
            }
        });

        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importFromExcel();
            }
        });

        frame.setVisible(true);
    }

    private void addProduct() {
        String name = nameField.getText();
        String price = priceField.getText();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO products (name, price) VALUES (?, ?)")) {
            statement.setString(1, name);
            statement.setBigDecimal(2, new BigDecimal(price));
            statement.executeUpdate();
            JOptionPane.showMessageDialog(frame, "商品添加成功!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "商品添加失败: " + e.getMessage());
        }
    }

    private void searchProduct() {
        String name = searchField.getText();

        // 添加通配符以支持模糊搜索
        String searchQuery = "%" + name + "%";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT name, price FROM products WHERE name LIKE ?")) {
            statement.setString(1, searchQuery);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder resultBuilder = new StringBuilder();
            boolean found = false;

            while (resultSet.next()) {
                found = true;
                String productName = resultSet.getString("name");
                BigDecimal productPrice = resultSet.getBigDecimal("price");
                resultBuilder.append("商品名: ").append(productName)
                        .append("<br>价格: ").append(productPrice)
                        .append("<br><br>");
            }

            if (found) {
                resultLabel.setText("<html>" + resultBuilder.toString() + "</html>");
            } else {
                resultLabel.setText("商品没有找到.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "商品查找失败: " + e.getMessage());
        }
    }


    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xls", "xlsx"));
        int returnValue = fileChooser.showOpenDialog(frame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(selectedFile);
                 Workbook workbook = WorkbookFactory.create(fis)) {

                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    importSheet(sheet);
                }

                JOptionPane.showMessageDialog(frame, "所有商品添加成功!");
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "添加失败: " + e.getMessage());
            }
        }
    }

    private void importSheet(Sheet sheet) throws SQLException {
        for (Row row : sheet) {
            Cell nameCell = row.getCell(0);
            Cell priceCell = row.getCell(1);

            if (nameCell != null && priceCell != null) {
                String name = nameCell.getStringCellValue();
                BigDecimal price = new BigDecimal(priceCell.getNumericCellValue());

                addProductToDatabase(name, price);
            }
        }
    }

    private void addProductToDatabase(String name, BigDecimal price) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO products (name, price) VALUES (?, ?)")) {
            statement.setString(1, name);
            statement.setBigDecimal(2, price);
            statement.executeUpdate();
        }
    }

}
