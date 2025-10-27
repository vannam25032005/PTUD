package gui;

import connectDB.ConnectDB;
import dao.KhachHang_DAO;
import dao.TheThanhVien_DAO;
import entity.KhachHang;
import entity.NhanVien;
import entity.TheThanhVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuanLyKhachHang_GUI extends JPanel {
    private JTextField txtCustomerId, txtName, txtPhone, txtEmail, txtSearch;
    private JRadioButton rbMale, rbFemale;
    private ButtonGroup genderGroup;

    private JTextField txtDiemTichLuy;

    private JTable table;
    private DefaultTableModel tableModel;

    private KhachHang_DAO kh_dao;
    private TheThanhVien_DAO ttv_dao;

    private NhanVien nhanVienDangNhap;

    public QuanLyKhachHang_GUI() {
        kh_dao = new KhachHang_DAO();
        ttv_dao = new TheThanhVien_DAO();
        // Giả lập nhân viên đăng nhập - bạn cần thay thế bằng logic thực tế
        nhanVienDangNhap = new NhanVien("NV001"); 

        // --- Layout Setup ---
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 218, 170));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Quản lý khách hàng", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        // 💡 FIX: KHÔNG add titleLabel vào đây vội

        // Center Panel (Form + Buttons)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(255, 218, 170));
        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = createButtonPanel();
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        // 💡 FIX: KHÔNG add centerPanel vào đây vội

        // Bottom Panel (Search + Table)
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(new Color(255, 218, 170));
        JPanel searchPanel = createSearchPanel();
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        JScrollPane tableScrollPane = createTable();
        bottomPanel.add(tableScrollPane, BorderLayout.CENTER);
        // 💡 FIX: KHÔNG add bottomPanel vào đây vội
        
        // --- 💡 FIX: Sắp xếp lại Layout ---
        
        // 1. Tạo một panel bọc cho toàn bộ phần trên
        JPanel topWrapperPanel = new JPanel(new BorderLayout());
        topWrapperPanel.setBackground(new Color(255, 218, 170));
        topWrapperPanel.add(titleLabel, BorderLayout.NORTH);  // Title ở trên cùng
        topWrapperPanel.add(centerPanel, BorderLayout.CENTER); // Form và các nút ở dưới Title
        
        // 2. Thêm panel bọc ở trên vào NORTH
        add(topWrapperPanel, BorderLayout.NORTH);
        
        // 3. Thêm panel chứa bảng (bottomPanel) vào CENTER để nó chiếm không gian
        add(bottomPanel, BorderLayout.CENTER);
        
        // --- End Layout Setup ---

        // Load initial data
        loadTableData();
        
        // 💡 FIX: Thêm clearForm() để set trạng thái ban đầu
        clearForm();
    }

    /**
     * Loads customer data into the JTable from the database.
     */
    private void loadTableData() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            ArrayList<Object[]> dsKH = kh_dao.getAllKhachHangForTable();
            for (Object[] row : dsKH) {
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khách hàng!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates the form panel with input fields.
     * @return The form JPanel.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 210, 210));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: MaKH, HoTen
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2; gbc.anchor = GridBagConstraints.WEST;
        panel.add(createLabel("Mã khách hàng"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtCustomerId = createStyledTextField();
        panel.add(txtCustomerId, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Họ tên"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtName = createStyledTextField();
        panel.add(txtName, gbc);

        // Row 2: SDT, Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(createLabel("Số điện thoại"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtPhone = createStyledTextField();
        panel.add(txtPhone, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Email"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtEmail = createStyledTextField();
        panel.add(txtEmail, gbc);

        // Row 3: GioiTinh, DiemTichLuy
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Giới tính"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        genderPanel.setBackground(new Color(220, 210, 210));
        rbMale = new JRadioButton("Nam");
        rbFemale = new JRadioButton("Nữ");
        rbMale.setBackground(new Color(220, 210, 210));
        rbFemale.setBackground(new Color(220, 210, 210));
        rbMale.setFont(new Font("Arial", Font.PLAIN, 14));
        rbFemale.setFont(new Font("Arial", Font.PLAIN, 14));
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        panel.add(genderPanel, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Điểm tích lũy"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtDiemTichLuy = createStyledTextField();
        txtDiemTichLuy.setText("0"); // Default value
        panel.add(txtDiemTichLuy, gbc);

        return panel;
    }

    /**
     * Creates the panel containing action buttons (Add, Delete, Edit, Clear).
     * @return The button JPanel.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(new Color(255, 218, 170));

        JButton btnAdd = new JButton("Thêm khách hàng");
        styleButton(btnAdd, new Color(76, 175, 80), Color.WHITE);
        btnAdd.addActionListener(e -> addCustomer());

        JButton btnDelete = new JButton("Xóa khách hàng");
        styleButton(btnDelete, new Color(244, 67, 54), Color.WHITE);
        btnDelete.addActionListener(e -> deleteCustomer());

        JButton btnEdit = new JButton("Chỉnh sửa thông tin");
        styleButton(btnEdit, new Color(234, 196, 28), Color.WHITE);
        btnEdit.addActionListener(e -> editCustomer());

        JButton btnClear = new JButton("Làm mới");
        styleButton(btnClear, new Color(158, 158, 158), Color.WHITE);
        btnClear.addActionListener(e -> clearForm());

        panel.add(btnAdd);
        panel.add(btnDelete);
        panel.add(btnEdit);
        panel.add(btnClear);

        return panel;
    }

    /**
     * Applies consistent styling to a JButton.
     * @param button The button to style.
     * @param bgColor Background color.
     * @param fgColor Foreground (text) color.
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(230, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
    }

    /**
     * Creates a styled JLabel.
     * @param text The label text.
     * @return The styled JLabel.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        return label;
    }

    /**
     * Creates a styled JTextField.
     * @return The styled JTextField.
     */
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(Color.WHITE);
        textField.setPreferredSize(new Dimension(0, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    /**
     * Creates the search panel containing the search field and button.
     * @return The search JPanel.
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(255, 218, 170));

        txtSearch = new JTextField(35);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JButton btnSearch = new JButton("Tìm Kiếm");
        btnSearch.setFocusPainted(false);
        btnSearch.setFont(new Font("Arial", Font.BOLD, 16));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setPreferredSize(new Dimension(150, 35));
        btnSearch.addActionListener(e -> searchCustomer());
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);

        JLabel lblSearch = new JLabel("Tìm theo mã khách hàng");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 13));
        lblSearch.setForeground(new Color(100, 100, 100));

        panel.add(lblSearch);
        panel.add(txtSearch);
        panel.add(btnSearch);

        return panel;
    }

    /**
     * Creates the JScrollPane containing the customer data JTable.
     * @return The JScrollPane with the table.
     */
    private JScrollPane createTable() {
        String[] columns = {"Mã", "Tên khách hàng", "Thẻ thành viên", "Số điện thoại", "Giới tính", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(255, 200, 150));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        // Add listener to load data into form when a row is clicked
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    loadSelectedRow();
                }
            }
        });

        // Style the table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 178, 102));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 100), 2));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 100), 2));

        return scrollPane;
    }

    /**
     * Handles adding a new customer. Validates input, calls DAO, and updates the table.
     */
    private void addCustomer() {
        if (!validateInput()) {
            return;
        }

        try {
            String maKH = txtCustomerId.getText().trim();
            String hoTen = txtName.getText().trim();
            String sdt = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            boolean gioiTinh = rbMale.isSelected(); // true = Nam, false = Nữ

            KhachHang kh = new KhachHang(maKH, hoTen, sdt, email, gioiTinh);

            if (kh_dao.addKhachHang(kh)) {
                int diem = parseDiem();
                String maTheMoi = ttv_dao.generateNewMaThe();
                TheThanhVien ttv = new TheThanhVien(maTheMoi, kh, diem);
                ttv_dao.addTheThanhVien(ttv);

                loadTableData(); // Reload table data
                clearForm();     // Clear input fields
                JOptionPane.showMessageDialog(this, "Đã thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // This case might not be reached if the DAO throws SQLException for duplicates
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
             // Handle specific duplicate key error
             if (e.getMessage().contains("Violation of PRIMARY KEY")) {
                 JOptionPane.showMessageDialog(this,
                    "Mã khách hàng '" + txtCustomerId.getText().trim() + "' đã bị trùng. Vui lòng nhập mã khác.",
                    "Lỗi Trùng Mã Khách Hàng",
                    JOptionPane.ERROR_MESSAGE);
             } else {
                 // Handle other SQL errors
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Lỗi CSDL khi thêm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             }
        } catch (IllegalArgumentException e) {
             // Catch validation errors from Entity or parseDiem
             JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi dữ liệu nhập", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Handles deleting the selected customer. Confirms deletion, calls DAOs, and updates the table.
     */
    private void deleteCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maKH = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa khách hàng " + maKH + "?\n(Thao tác này cũng sẽ xóa thẻ thành viên liên kết)",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Must delete the membership card first due to foreign key constraint
                ttv_dao.deleteTheThanhVienByMaKH(maKH);
                if (kh_dao.deleteKhachHang(maKH)) {
                    loadTableData();
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Đã xóa khách hàng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles editing the selected customer. Validates input, calls appropriate DAO update method,
     * handles membership card updates, and refreshes the table.
     */
    private void editCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateInput()) { // validateInput also checks Entity constraints
            try {
                String maKHCU = tableModel.getValueAt(selectedRow, 0).toString();
                String maKHMoi = txtCustomerId.getText().trim();
                String hoTen = txtName.getText().trim();
                String sdt = txtPhone.getText().trim();
                String email = txtEmail.getText().trim();
                boolean gioiTinh = rbMale.isSelected();
                int diemMoi = parseDiem();

                KhachHang khMoi = new KhachHang(maKHMoi, hoTen, sdt, email, gioiTinh);
                boolean updateSuccess = false;

                // Check if the primary key (maKH) was changed
                if (maKHCU.equals(maKHMoi)) {
                    // Case 1: MaKH unchanged, use standard update
                    updateSuccess = kh_dao.updateKhachHang(khMoi);
                } else {
                    // Case 2: MaKH changed, requires special update (and confirmation)
                    int confirmChangePK = JOptionPane.showConfirmDialog(this,
                        "Bạn đang thay đổi Mã Khách Hàng. Hành động này sẽ cập nhật CSDL.\nBạn có chắc chắn?",
                        "Xác nhận đổi Mã KH", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if(confirmChangePK == JOptionPane.YES_OPTION) {
                        // Call the DAO method designed to update the primary key
                        updateSuccess = kh_dao.updateKhachHangPK(maKHCU, khMoi);
                    } else {
                        // User canceled, revert the MaKH field and exit
                        txtCustomerId.setText(maKHCU);
                        return;
                    }
                }

                // If either update case was successful
                if (updateSuccess) {
                    // Update membership card points/status
                    TheThanhVien ttv = ttv_dao.getTheByMaKH(maKHMoi); // Get card using NEW maKH

                    if (ttv == null) {
                        // If customer didn't have a card, create one now
                        String maTheMoi = ttv_dao.generateNewMaThe();
                        TheThanhVien ttvMoi = new TheThanhVien(maTheMoi, khMoi, diemMoi);
                        ttv_dao.addTheThanhVien(ttvMoi);
                    } else {
                        // If customer already had a card, update points (Entity recalculates rank)
                        ttv.setDiemTichLuy(diemMoi);
                        ttv_dao.updateTheThanhVien(ttv);
                    }

                    loadTableData(); // Refresh table
                    JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin khách hàng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại! (Có thể do trùng Mã KH mới)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException | IllegalArgumentException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Loads the data of the currently selected table row into the input form.
     */
    private void loadSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            try {
                String maKH = tableModel.getValueAt(selectedRow, 0).toString();
                // Get full customer object from DAO to ensure data consistency
                KhachHang kh = kh_dao.getKhachHangByMa(maKH);

                if (kh == null) {
                    // Should not happen if table data is correct, but handle defensively
                    JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng trong CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Populate form fields
                txtCustomerId.setText(kh.getMaKH());
                txtName.setText(kh.getHoTenKH());
                txtPhone.setText(kh.getSoDienThoai());
                txtEmail.setText(kh.getEmail());
                
                // true = Nam, false = Nữ
                if (kh.isGioiTinh()) { 
                    rbMale.setSelected(true);
                } else {
                    rbFemale.setSelected(true);
                }

                // Get membership card info
                TheThanhVien ttv = ttv_dao.getTheByMaKH(maKH);
                if (ttv != null) {
                    txtDiemTichLuy.setText(String.valueOf(ttv.getDiemTichLuy()));
                } else {
                    txtDiemTichLuy.setText("0"); // Default if no card exists
                }

                // Keep MaKH editable even after loading
                // txtCustomerId.setEditable(false); // Removed this line

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết khách hàng!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the search functionality based on exact MaKH.
     * Shows message if empty, found, or not found, and reloads data accordingly.
     */
    private void searchCustomer() {
        String keyword = txtSearch.getText().trim();

        // Handle empty search field
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ô tìm kiếm không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            loadTableData(); // Reload full list
            return;
        }

        try {
            // Call DAO to search for exact MaKH match
            Object[] row = kh_dao.getKhachHangRowByMa(keyword);

            tableModel.setRowCount(0); // Clear table before displaying results

            if (row != null) {
                // Case 1: Exact match found
                tableModel.addRow(row);
                table.setRowSelectionInterval(0, 0); // Select the found row
                loadSelectedRow();                   // Load data into the form
                JOptionPane.showMessageDialog(this, "Đã tìm thấy khách hàng: " + keyword, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Case 2: No match found
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với mã: " + keyword, "Không tìm thấy", JOptionPane.WARNING_MESSAGE);

                // Reload the full list after user clicks OK on the message
                loadTableData();
                clearForm(); // Also clear the form
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates the input fields using Entity constraints and additional GUI checks.
     * Displays error messages if validation fails.
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInput() {
        try {
            // Use constructor to leverage Entity validation
            new KhachHang(
                txtCustomerId.getText().trim(),
                txtName.getText().trim(),
                txtPhone.getText().trim(),
                txtEmail.getText().trim(),
                rbMale.isSelected() // Pass boolean directly
                
            );

            // Additional GUI check: Gender must be selected
            if (!rbMale.isSelected() && !rbFemale.isSelected()) {
                throw new IllegalArgumentException("Vui lòng chọn giới tính!");
            }

            // Check if DiemTichLuy is a valid non-negative number
            parseDiem();

            return true;

        } catch (IllegalArgumentException e) {
            // Catch validation errors from Entity constructor or parseDiem
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi dữ liệu nhập", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Helper method to parse the DiemTichLuy text field.
     * @return The integer value of the points.
     * @throws IllegalArgumentException if the text is not a valid non-negative integer.
     */
    private int parseDiem() throws IllegalArgumentException {
        try {
            int diem = Integer.parseInt(txtDiemTichLuy.getText().trim());
            if (diem < 0) {
                 throw new IllegalArgumentException("Điểm tích lũy không được là số âm.");
            }
            return diem;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Điểm tích lũy phải là một con số.");
        }
    }


    /**
     * Clears all input fields in the form and deselects any table row.
     */
    private void clearForm() {
        txtCustomerId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        genderGroup.clearSelection();
        txtDiemTichLuy.setText("0");

        txtCustomerId.setEditable(true); // Ensure MaKH is editable after clearing
        table.clearSelection();          // Deselect table row
    }


    /**
     * Main method to launch the QuanLyKhachHang panel in a JFrame.
     * Establishes DB connection first.
     */
    public static void main(String[] args) {
        // Establish database connection before creating GUI
        ConnectDB.connect();

        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý khách hàng - Final Version");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            QuanLyKhachHang_GUI panel = new QuanLyKhachHang_GUI();

            frame.add(panel);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
            frame.setVisible(true);
        });
    }
}