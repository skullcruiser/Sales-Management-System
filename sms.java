import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;

public class SalesManagementSystem8{
    private static final String URL = "jdbc:mysql://localhost:3306/ProjectSMS";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    private Connection conn;
    private JFrame mainFrame;

    public SalesManagementSystem8() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found in classpath", e);
        }
        conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(false);
        createTables();
        createGUI();
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");
    }

    private void createTables() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Customer (" +
                    "customer_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE, " +
                    "phone VARCHAR(20), " +
                    "address VARCHAR(200))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Product (" +
                    "product_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL UNIQUE, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "stock_quantity INT NOT NULL CHECK (stock_quantity >= 0))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Employee (" +
                    "employee_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "position VARCHAR(50), " +
                    "salary DECIMAL(10,2))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Order_Status (" +
                    "order_status_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "status_name VARCHAR(50) NOT NULL UNIQUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Orders (" +
                    "order_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "customer_id INT, " +
                    "employee_id INT, " +
                    "order_date DATE, " +
                    "order_status_id INT DEFAULT 1, " +
                    "FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE SET NULL, " +
                    "FOREIGN KEY (employee_id) REFERENCES Employee(employee_id) ON DELETE SET NULL, " +
                    "FOREIGN KEY (order_status_id) REFERENCES Order_Status(order_status_id) ON DELETE SET NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Order_details (" +
                    "order_id INT, " +
                    "product_id INT, " +
                    "quantity INT NOT NULL CHECK (quantity > 0), " +
                    "PRIMARY KEY (order_id, product_id), " +
                    "FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Sales_report (" +
                    "report_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "employee_id INT, " +
                    "report_date DATE, " +
                    "FOREIGN KEY (employee_id) REFERENCES Employee(employee_id) ON DELETE SET NULL)");

            stmt.execute("INSERT IGNORE INTO Order_Status (status_name) VALUES ('Pending'), ('Shipped'), ('Delivered')");

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    private void createGUI() {
        mainFrame = new JFrame("Sales Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 600);
        mainFrame.setLayout(new java.awt.GridLayout(10, 2, 10, 10));
        mainFrame.setLocationRelativeTo(null);

        JButton addCustomerBtn = new JButton("Add Customer");
        JButton addProductBtn = new JButton("Add Product");
        JButton placeOrderBtn = new JButton("Place Order");
        JButton generateReportBtn = new JButton("Generate Sales Report");
        JButton addEmployeeBtn = new JButton("Add Employee");
        JButton displayCustomersBtn = new JButton("Display Customers");
        JButton displayOrdersBtn = new JButton("Display Orders");
        JButton displayEmployeesBtn = new JButton("Display Employees");
        JButton displayProductsBtn = new JButton("Display Products");
        JButton displayOrderDetailsBtn = new JButton("Display Order Details");
        JButton displaySalesReportsBtn = new JButton("Display Sales Reports");
        JButton updateCustomerBtn = new JButton("Update Customer");
        JButton updateProductBtn = new JButton("Update Product");
        JButton updateEmployeeBtn = new JButton("Update Employee");
        JButton updateOrderStatusBtn = new JButton("Update Order Status");
        JButton deleteCustomerBtn = new JButton("Delete Customer");
        JButton deleteEmployeeBtn = new JButton("Delete Employee");
        JButton deleteOrderBtn = new JButton("Delete Order");
        JButton exitBtn = new JButton("Exit");

        addCustomerBtn.addActionListener(e -> addCustomerGUI());
        addProductBtn.addActionListener(e -> addProductGUI());
        placeOrderBtn.addActionListener(e -> placeOrderGUI());
        generateReportBtn.addActionListener(e -> generateSalesReportGUI());
        addEmployeeBtn.addActionListener(e -> addEmployeeGUI());
        displayCustomersBtn.addActionListener(e -> displayCustomersGUI());
        displayOrdersBtn.addActionListener(e -> displayOrdersGUI());
        displayEmployeesBtn.addActionListener(e -> displayEmployeesGUI());
        displayProductsBtn.addActionListener(e -> displayProductsGUI());
        displayOrderDetailsBtn.addActionListener(e -> displayOrderDetailsGUI());
        displaySalesReportsBtn.addActionListener(e -> displaySalesReportsGUI());
        updateCustomerBtn.addActionListener(e -> updateCustomerGUI());
        updateProductBtn.addActionListener(e -> updateProductGUI());
        updateEmployeeBtn.addActionListener(e -> updateEmployeeGUI());
        updateOrderStatusBtn.addActionListener(e -> updateOrderStatusGUI());
        deleteCustomerBtn.addActionListener(e -> deleteCustomerGUI());
        deleteEmployeeBtn.addActionListener(e -> deleteEmployeeGUI());
        deleteOrderBtn.addActionListener(e -> deleteOrderGUI());
        exitBtn.addActionListener(e -> {
            try {
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error closing connection: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        });

        mainFrame.add(addCustomerBtn);
        mainFrame.add(addProductBtn);
        mainFrame.add(placeOrderBtn);
        mainFrame.add(generateReportBtn);
        mainFrame.add(addEmployeeBtn);
        mainFrame.add(displayCustomersBtn);
        mainFrame.add(displayOrdersBtn);
        mainFrame.add(displayEmployeesBtn);
        mainFrame.add(displayProductsBtn);
        mainFrame.add(displayOrderDetailsBtn);
        mainFrame.add(displaySalesReportsBtn);
        mainFrame.add(updateCustomerBtn);
        mainFrame.add(updateProductBtn);
        mainFrame.add(updateEmployeeBtn);
        mainFrame.add(updateOrderStatusBtn);
        mainFrame.add(deleteCustomerBtn);
        mainFrame.add(deleteEmployeeBtn);
        mainFrame.add(deleteOrderBtn);
        mainFrame.add(exitBtn);

        mainFrame.setVisible(true);
    }

    // CRUD Operations
    public void addCustomer(String name, String email, String phone, String address) throws SQLException {
        String sql = "INSERT INTO Customer (name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                JOptionPane.showMessageDialog(mainFrame, "Customer added successfully! Customer ID: " + rs.getInt(1), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void addProduct(String name, double price, int stockQuantity) throws SQLException {
        try {
            String sql = "INSERT INTO Product (name, price, stock_quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, stockQuantity);
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(mainFrame, "Product added successfully! Product ID: " + rs.getInt(1), "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                conn.commit();
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void addEmployee(String name, String position, double salary) throws SQLException {
        String sql = "INSERT INTO Employee (name, position, salary) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setDouble(3, salary);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                JOptionPane.showMessageDialog(mainFrame, "Employee added successfully! Employee ID: " + rs.getInt(1), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void updateCustomer(int customerId, String name, String email, String phone, String address) throws SQLException {
        try {
            String sql = "UPDATE Customer SET name = ?, email = ?, phone = ?, address = ? WHERE customer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setInt(5, customerId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new SQLException("Customer ID not found");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void updateProduct(int productId, String name, double price, int stockQuantity) throws SQLException {
        try {
            String sql = "UPDATE Product SET name = ?, price = ?, stock_quantity = ? WHERE product_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stockQuantity);
            pstmt.setInt(4, productId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new SQLException("Product ID not found");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void updateEmployee(int employeeId, String name, String position, double salary) throws SQLException {
        try {
            String sql = "UPDATE Employee SET name = ?, position = ?, salary = ? WHERE employee_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setDouble(3, salary);
            pstmt.setInt(4, employeeId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new SQLException("Employee ID not found");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void updateOrderStatus(int orderId, int newStatusId) throws SQLException {
        try {
            String sql = "UPDATE Orders SET order_status_id = ? WHERE order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newStatusId);
            pstmt.setInt(2, orderId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Order status updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new SQLException("Order ID not found");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        try {
            String sql = "DELETE FROM Customer WHERE customer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Customer deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new SQLException("Customer ID not found");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        try {
            String sql = "DELETE FROM Employee WHERE employee_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Employee deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new SQLException("Employee ID not found");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void deleteOrder(int orderId) throws SQLException {
        try {
            String sql = "DELETE FROM Orders WHERE order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Order deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new SQLException("Order ID not found");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void placeOrder(int customerId, int employeeId, List<OrderItem> items) throws SQLException {
        try {
            String checkCustomerSql = "SELECT COUNT(*) FROM Customer WHERE customer_id = ?";
            PreparedStatement checkCustomerPstmt = conn.prepareStatement(checkCustomerSql);
            checkCustomerPstmt.setInt(1, customerId);
            ResultSet rsCheckCustomer = checkCustomerPstmt.executeQuery();
            rsCheckCustomer.next();
            if (rsCheckCustomer.getInt(1) == 0) {
                throw new SQLException("Customer ID " + customerId + " does not exist.");
            }

            String checkEmployeeSql = "SELECT COUNT(*) FROM Employee WHERE employee_id = ?";
            PreparedStatement checkEmployeePstmt = conn.prepareStatement(checkEmployeeSql);
            checkEmployeePstmt.setInt(1, employeeId);
            ResultSet rsCheckEmployee = checkEmployeePstmt.executeQuery();
            rsCheckEmployee.next();
            if (rsCheckEmployee.getInt(1) == 0) {
                throw new SQLException("Employee ID " + employeeId + " does not exist.");
            }

            String orderSql = "INSERT INTO Orders (customer_id, employee_id, order_date, order_status_id) VALUES (?, ?, ?, 1)";
            PreparedStatement orderPstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPstmt.setInt(1, customerId);
            orderPstmt.setInt(2, employeeId); // Ensure employee_id is set
            orderPstmt.setDate(3, Date.valueOf(LocalDate.now()));
            orderPstmt.executeUpdate();

            ResultSet rs = orderPstmt.getGeneratedKeys();
            rs.next();
            int orderId = rs.getInt(1);

            String detailSql = "INSERT INTO Order_details (order_id, product_id, quantity) VALUES (?, ?, ?)";
            String updateStockSql = "UPDATE Product SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";
            for (OrderItem item : items) {
                PreparedStatement checkStock = conn.prepareStatement("SELECT stock_quantity FROM Product WHERE product_id = ?");
                checkStock.setInt(1, item.productId);
                ResultSet stockRs = checkStock.executeQuery();
                if (!stockRs.next() || stockRs.getInt("stock_quantity") < item.quantity) {
                    throw new SQLException("Insufficient stock for product ID: " + item.productId);
                }

                PreparedStatement detailPstmt = conn.prepareStatement(detailSql);
                detailPstmt.setInt(1, orderId);
                detailPstmt.setInt(2, item.productId);
                detailPstmt.setInt(3, item.quantity);
                detailPstmt.executeUpdate();

                PreparedStatement stockPstmt = conn.prepareStatement(updateStockSql);
                stockPstmt.setInt(1, item.quantity);
                stockPstmt.setInt(2, item.productId);
                stockPstmt.setInt(3, item.quantity);
                stockPstmt.executeUpdate();
            }
            conn.commit();
            JOptionPane.showMessageDialog(mainFrame, "Order placed successfully! Order ID: " + orderId + " by Employee ID: " + employeeId, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public void generateSalesReport(int employeeId) throws SQLException {
        try {
            String checkEmployeeSql = "SELECT COUNT(*) FROM Employee WHERE employee_id = ?";
            PreparedStatement checkEmployeePstmt = conn.prepareStatement(checkEmployeeSql);
            checkEmployeePstmt.setInt(1, employeeId);
            ResultSet rsCheck = checkEmployeePstmt.executeQuery();
            rsCheck.next();
            if (rsCheck.getInt(1) == 0) {
                throw new SQLException("Employee ID " + employeeId + " does not exist.");
            }

            String sql = "SELECT SUM(od.quantity * p.price) as total_sales " +
                    "FROM Order_details od " +
                    "JOIN Product p ON od.product_id = p.product_id " +
                    "JOIN Orders o ON od.order_id = o.order_id " +
                    "WHERE o.order_date = ? AND o.employee_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setInt(2, employeeId);
            ResultSet rs = pstmt.executeQuery();
            double salesAmount = rs.next() ? rs.getDouble("total_sales") : 0.0;

            String reportSql = "INSERT INTO Sales_report (employee_id, report_date) VALUES (?, ?)";
            PreparedStatement reportPstmt = conn.prepareStatement(reportSql);
            reportPstmt.setInt(1, employeeId);
            reportPstmt.setDate(2, Date.valueOf(LocalDate.now()));
            reportPstmt.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(mainFrame, "Sales report generated! Employee's sales today: $" + salesAmount,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    // GUI Methods
    private void addCustomerGUI() {
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(10);
        JTextField addressField = new JTextField(30);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone (10 digits):"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();

            // Validate phone and email
            if (!isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(mainFrame, "Phone number must be 10 digits.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid email format.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Proceed to insert into DB
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Customer (name, email, phone, address) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.setString(4, address);
                stmt.executeUpdate();
                conn.commit();
                JOptionPane.showMessageDialog(mainFrame, "Customer added successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainFrame, "Error adding customer: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void addProductGUI() {
        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField stockField = new JTextField(20);

        JPanel panel = new JPanel(new java.awt.GridLayout(3, 2));
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                addProduct(nameField.getText(), price, stock);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid number format for price or stock.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainFrame, "Error adding product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addEmployeeGUI() {
        JTextField nameField = new JTextField(20);
        JTextField positionField = new JTextField(20);
        JTextField salaryField = new JTextField(20);

        JPanel panel = new JPanel(new java.awt.GridLayout(3, 2));
        panel.add(new JLabel("Employee Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double salary = Double.parseDouble(salaryField.getText());
                addEmployee(nameField.getText(), positionField.getText(), salary);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid number format for salary.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainFrame, "Error adding employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateCustomerGUI() {
        try {
            displayCustomersGUI();
            String customerIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Customer ID to update:");
            if (customerIdStr == null) return;
            int customerId = Integer.parseInt(customerIdStr);

            String sql = "SELECT * FROM Customer WHERE customer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(mainFrame, "Customer ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField nameField = new JTextField(rs.getString("name"), 20);
            JTextField emailField = new JTextField(rs.getString("email"), 20);
            JTextField phoneField = new JTextField(rs.getString("phone"), 20);
            JTextField addressField = new JTextField(rs.getString("address"), 20);

            JPanel panel = new JPanel(new java.awt.GridLayout(4, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);

            int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Update Customer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                updateCustomer(customerId, nameField.getText(), emailField.getText(),
                        phoneField.getText(), addressField.getText());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Customer ID format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProductGUI() {
        try {
            displayProductsGUI();
            String productIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Product ID to update:");
            if (productIdStr == null) return;
            int productId = Integer.parseInt(productIdStr);

            String sql = "SELECT * FROM Product WHERE product_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(mainFrame, "Product ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField nameField = new JTextField(rs.getString("name"), 20);
            JTextField priceField = new JTextField(String.valueOf(rs.getDouble("price")), 20);
            JTextField stockField = new JTextField(String.valueOf(rs.getInt("stock_quantity")), 20);

            JPanel panel = new JPanel(new java.awt.GridLayout(3, 2));
            panel.add(new JLabel("Product Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Stock Quantity:"));
            panel.add(stockField);

            int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Update Product", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double price = Double.parseDouble(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    updateProduct(productId, nameField.getText(), price, stock);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(mainFrame, "Invalid number format for price or stock", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Product ID format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployeeGUI() {
        try {
            displayEmployeesGUI();
            String employeeIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Employee ID to update:");
            if (employeeIdStr == null) return;
            int employeeId = Integer.parseInt(employeeIdStr);

            String sql = "SELECT * FROM Employee WHERE employee_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(mainFrame, "Employee ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField nameField = new JTextField(rs.getString("name"), 20);
            JTextField positionField = new JTextField(rs.getString("position"), 20);
            JTextField salaryField = new JTextField(String.valueOf(rs.getDouble("salary")), 20);

            JPanel panel = new JPanel(new java.awt.GridLayout(3, 2));
            panel.add(new JLabel("Employee Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Position:"));
            panel.add(positionField);
            panel.add(new JLabel("Salary:"));
            panel.add(salaryField);

            int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double salary = Double.parseDouble(salaryField.getText());
                    updateEmployee(employeeId, nameField.getText(), positionField.getText(), salary);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(mainFrame, "Invalid number format for salary", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Employee ID format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrderStatusGUI() {
        try {
            displayOrdersGUI();
            String orderIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Order ID to update status:");
            if (orderIdStr == null) return;
            int orderId = Integer.parseInt(orderIdStr);

            String[] statuses = {"Pending", "Shipped", "Delivered"};
            String newStatus = (String) JOptionPane.showInputDialog(mainFrame,
                    "Select new status:", "Update Order Status",
                    JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);

            if (newStatus != null) {
                int statusId = switch (newStatus) {
                    case "Pending" -> 1;
                    case "Shipped" -> 2;
                    case "Delivered" -> 3;
                    default -> 1;
                };
                updateOrderStatus(orderId, statusId);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Order ID format",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error updating order status: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomerGUI() {
        try {
            displayCustomersGUI();
            String customerIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Customer ID to delete:");
            if (customerIdStr == null) return;

            int customerId = Integer.parseInt(customerIdStr);
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to delete customer ID " + customerId + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                deleteCustomer(customerId);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Customer ID format",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error deleting customer: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployeeGUI() {
        try {
            displayEmployeesGUI();
            String employeeIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Employee ID to delete:");
            if (employeeIdStr == null) return;

            int employeeId = Integer.parseInt(employeeIdStr);
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to delete employee ID " + employeeId + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                deleteEmployee(employeeId);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Employee ID format",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error deleting employee: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOrderGUI() {
        try {
            displayOrdersGUI();
            String orderIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Order ID to delete:");
            if (orderIdStr == null) return;

            int orderId = Integer.parseInt(orderIdStr);
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to delete order ID " + orderId + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                deleteOrder(orderId);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Order ID format",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error deleting order: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void placeOrderGUI() {
        try {
            displayCustomersGUI();
            JTextField customerIdField = new JTextField(10);
            displayEmployeesGUI();
            JTextField employeeIdField = new JTextField(10);
            displayProductsGUI();

            JPanel initialPanel = new JPanel(new java.awt.GridLayout(2, 2));
            initialPanel.add(new JLabel("Customer ID:"));
            initialPanel.add(customerIdField);
            initialPanel.add(new JLabel("Employee ID:"));
            initialPanel.add(employeeIdField);

            int result = JOptionPane.showConfirmDialog(mainFrame, initialPanel, "Place Order", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            int customerId = Integer.parseInt(customerIdField.getText());
            int employeeId = Integer.parseInt(employeeIdField.getText());

            List<OrderItem> items = new ArrayList<>();
            while (true) {
                JTextField productIdField = new JTextField(10);
                JTextField quantityField = new JTextField(10);

                JPanel itemPanel = new JPanel(new java.awt.GridLayout(2, 2));
                itemPanel.add(new JLabel("Product ID (0 to finish):"));
                itemPanel.add(productIdField);
                itemPanel.add(new JLabel("Quantity:"));
                itemPanel.add(quantityField);

                int itemResult = JOptionPane.showConfirmDialog(mainFrame, itemPanel, "Add Order Item", JOptionPane.OK_CANCEL_OPTION);
                if (itemResult != JOptionPane.OK_OPTION) break;

                int productId = Integer.parseInt(productIdField.getText());
                if (productId == 0) break;
                int quantity = Integer.parseInt(quantityField.getText());
                items.add(new OrderItem(productId, quantity));
            }

            if (!items.isEmpty()) {
                placeOrder(customerId, employeeId, items);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "No items added to the order.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid number format for IDs or quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error placing order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateSalesReportGUI() {
        try {
            displayEmployeesGUI();
            String employeeIdStr = JOptionPane.showInputDialog(mainFrame, "Enter Employee ID:");
            if (employeeIdStr == null) return;
            int employeeId = Integer.parseInt(employeeIdStr);
            generateSalesReport(employeeId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid number format for Employee ID.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error generating sales report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayCustomersGUI() {
        try {
            String sql = "SELECT * FROM Customer";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                Vector<String> columnNames = new Vector<>();
                columnNames.add("Customer ID");
                columnNames.add("Name");
                columnNames.add("Email");
                columnNames.add("Phone");
                columnNames.add("Address");

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("customer_id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("email"));
                    row.add(rs.getString("phone"));
                    row.add(rs.getString("address"));
                    data.add(row);
                }

                DefaultTableModel model = new DefaultTableModel(data, columnNames);
                JTable table = new JTable(model);
                JOptionPane.showMessageDialog(mainFrame, new JScrollPane(table), "List of Customers", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error displaying customers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayOrdersGUI() {
        try {
            String sql = "SELECT o.order_id, o.customer_id, o.employee_id, o.order_date, os.status_name " +
                    "FROM Orders o " +
                    "LEFT JOIN Order_Status os ON o.order_status_id = os.order_status_id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                Vector<String> columnNames = new Vector<>();
                columnNames.add("Order ID");
                columnNames.add("Customer ID");
                columnNames.add("Employee ID");
                columnNames.add("Order Date");
                columnNames.add("Status");

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("order_id"));
                    row.add(rs.getInt("customer_id"));
                    row.add(rs.getInt("employee_id"));
                    row.add(rs.getString("order_date"));
                    row.add(rs.getString("status_name"));
                    data.add(row);
                }

                DefaultTableModel model = new DefaultTableModel(data, columnNames);
                JTable table = new JTable(model);
                JOptionPane.showMessageDialog(mainFrame, new JScrollPane(table), "List of Orders", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error displaying orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayEmployeesGUI() {
        try {
            String sql = "SELECT * FROM Employee";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                Vector<String> columnNames = new Vector<>();
                columnNames.add("Employee ID");
                columnNames.add("Name");
                columnNames.add("Position");
                columnNames.add("Salary");

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("employee_id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("position"));
                    row.add(rs.getDouble("salary"));
                    data.add(row);
                }

                DefaultTableModel model = new DefaultTableModel(data, columnNames);
                JTable table = new JTable(model);
                JOptionPane.showMessageDialog(mainFrame, new JScrollPane(table), "List of Employees", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error displaying employees: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProductsGUI() {
        try {
            String sql = "SELECT * FROM Product";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                Vector<String> columnNames = new Vector<>();
                columnNames.add("Product ID");
                columnNames.add("Name");
                columnNames.add("Price");
                columnNames.add("Stock Quantity");

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("product_id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getDouble("price"));
                    row.add(rs.getInt("stock_quantity"));
                    data.add(row);
                }

                DefaultTableModel model = new DefaultTableModel(data, columnNames);
                JTable table = new JTable(model);
                JOptionPane.showMessageDialog(mainFrame, new JScrollPane(table), "List of Products", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error displaying products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayOrderDetailsGUI() {
        try {
            String sql = "SELECT od.order_id, od.product_id, od.quantity, p.name as product_name, " +
                    "(od.quantity * p.price) as subtotal " +
                    "FROM Order_details od " +
                    "JOIN Product p ON od.product_id = p.product_id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                Vector<String> columnNames = new Vector<>();
                columnNames.add("Order ID");
                columnNames.add("Product ID");
                columnNames.add("Product Name");
                columnNames.add("Quantity");
                columnNames.add("Subtotal");

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("order_id"));
                    row.add(rs.getInt("product_id"));
                    row.add(rs.getString("product_name"));
                    row.add(rs.getInt("quantity"));
                    row.add(rs.getDouble("subtotal"));
                    data.add(row);
                }

                DefaultTableModel model = new DefaultTableModel(data, columnNames);
                JTable table = new JTable(model);
                JOptionPane.showMessageDialog(mainFrame, new JScrollPane(table), "List of Order Details", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error displaying order details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displaySalesReportsGUI() {
        try {
            String sql = "SELECT sr.report_id, sr.employee_id, sr.report_date, e.name as employee_name, " +
                    "COALESCE((SELECT SUM(od.quantity * p.price) " +
                    "FROM Order_details od " +
                    "JOIN Product p ON od.product_id = p.product_id " +
                    "JOIN Orders o ON od.order_id = o.order_id " +
                    "WHERE o.order_date = sr.report_date AND o.employee_id = sr.employee_id), 0) as sales_amount " +
                    "FROM Sales_report sr " +
                    "JOIN Employee e ON sr.employee_id = e.employee_id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                Vector<String> columnNames = new Vector<>();
                columnNames.add("Report ID");
                columnNames.add("Employee ID");
                columnNames.add("Employee Name");
                columnNames.add("Report Date");
                columnNames.add("Sales Amount");

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    int reportId = rs.getInt("report_id");
                    int empId = rs.getInt("employee_id");
                    String empName = rs.getString("employee_name");
                    String reportDate = rs.getString("report_date");
                    double salesAmount = rs.getDouble("sales_amount");

                    // Debugging: Log the calculation
                    System.out.println("Report ID: " + reportId + ", Employee ID: " + empId +
                            ", Date: " + reportDate + ", Calculated Sales: $" + salesAmount);

                    Vector<Object> row = new Vector<>();
                    row.add(reportId);
                    row.add(empId);
                    row.add(empName);
                    row.add(reportDate);
                    row.add(salesAmount);
                    data.add(row);
                }

                DefaultTableModel model = new DefaultTableModel(data, columnNames);
                JTable table = new JTable(model);
                JOptionPane.showMessageDialog(mainFrame, new JScrollPane(table), "List of Sales Reports", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error displaying sales reports: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class OrderItem {
        int productId;
        int quantity;

        OrderItem(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new SalesManagementSystem8();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}