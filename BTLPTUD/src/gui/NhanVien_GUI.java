package gui;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
// Import SQLException
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.QuanLy;

import java.util.List;
import java.text.ParseException;

public class NhanVien_GUI extends JPanel {
    private JTextField txtEmployeeId, txtName, txtIdNumber, txtPhone, txtEmail;
    private JDateChooser dateChooserBirthDate;
    private JRadioButton rbMale, rbFemale;
    private JComboBox<String> cbStatus;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    
    // 💡 KHỞI TẠO DAO TẠI ĐÂY
    private NhanVien_DAO nhanVien_DAO = new NhanVien_DAO(); // Đổi tên để tránh xung đột

    // Constructor đã sửa
    public NhanVien_GUI() {
        // Khởi tạo các thành phần GUI
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 235, 205));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(139, 69, 19));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // 1. TẠO FORM VÀ BẢNG TRƯỚC
        JPanel formPanel = createFormPanel();
        
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(new Color(255, 235, 205));
        JPanel searchPanel = createSearchPanel();
        JScrollPane tableScrollPane = createTable();
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 2. TẠO TOP PANEL ĐỂ GỘP TITLE VÀ FORM
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(255, 235, 205)); // Đồng bộ màu nền
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // 3. THÊM TOP PANEL VÀO NORTH
        add(topPanel, BorderLayout.NORTH);

        // 4. THÊM BOTTOM PANEL (CHỨA BẢNG) VÀO CENTER
        add(bottomPanel, BorderLayout.CENTER);

        // 5. GỌI LOAD DỮ LIỆU
        loadDataToTable();
        clearForm(); // Xóa form và thiết lập trạng thái ban đầu
    }

    //HÀM THIẾT LẬP BAN ĐẦU CHO FORM
    private void clearForm() {
        txtEmployeeId.setText("");
        txtName.setText("");
        dateChooserBirthDate.setDate(null);
        txtIdNumber.setText("");
        txtPhone.setText("");
        txtEmail.setText("");

        // Cần phải chọn mặc định cho Giới tính nếu cột này là NOT NULL
        rbMale.setSelected(true); // Thiết lập mặc định là Nam

        cbStatus.setSelectedIndex(0);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 222, 179));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 180, 140), 2),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Mã nhân viên, Họ tên
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(createLabel("Mã nhân viên"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtEmployeeId = createStyledTextField();
        panel.add(txtEmployeeId, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Họ tên"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtName = createStyledTextField();
        panel.add(txtName, gbc);

        // Row 2: Ngày sinh, Số CCCD
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(createLabel("Ngày sinh"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        dateChooserBirthDate = new JDateChooser();
        dateChooserBirthDate.setDateFormatString("dd/MM/yyyy");
        // Sửa lỗi: Lấy thành phần UI của DateEditor một cách an toàn
        Component dateEditorComponent = dateChooserBirthDate.getDateEditor().getUiComponent();
        if (dateEditorComponent instanceof JTextField) {
            JTextField dateEditorTextField = (JTextField) dateEditorComponent;
            styleTextField(dateEditorTextField);
            dateEditorTextField.setEditable(false);
        }
        JPanel dateWrapperPanel = new JPanel(new GridLayout(1, 1));
        dateWrapperPanel.add(dateChooserBirthDate);
        gbc.ipady = 10;
        panel.add(dateWrapperPanel, gbc);
        gbc.ipady = 0;

        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Số CCCD"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtIdNumber = createStyledTextField();
        panel.add(txtIdNumber, gbc);

        // Row 3: Giới tính, Số điện thoại
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Giới tính"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(new Color(245, 222, 179));
        rbMale = new JRadioButton("Nam");
        rbFemale = new JRadioButton("Nữ");
        rbMale.setBackground(new Color(245, 222, 179));
        rbFemale.setBackground(new Color(245, 222, 179));
        rbMale.setFont(new Font("Arial", Font.PLAIN, 14));
        rbFemale.setFont(new Font("Arial", Font.PLAIN, 14));
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        rbMale.setSelected(true); // Chọn mặc định Nam
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        panel.add(genderPanel, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(createLabel("Số điện thoại"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtPhone = createStyledTextField();
        panel.add(txtPhone, gbc);

        // Row 4: Email, Trạng thái làm việc
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(createLabel("Email"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtEmail = createStyledTextField();
        panel.add(txtEmail, gbc);

        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(createLabel("Trạng thái làm việc"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        cbStatus = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc", "Tạm nghỉ"});
        cbStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        cbStatus.setBackground(Color.WHITE);
        panel.add(cbStatus, gbc);

        // Row 5: Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(new Color(245, 222, 179));
        
        JButton btnAdd = createFormButton("Thêm nhân viên", new Color(76, 175, 80), new Color(56, 142, 60), 160);
        btnAdd.addActionListener(e -> addEmployee());

        JButton btnEdit = createFormButton("Chỉnh sửa thông tin", new Color(234, 196, 28), new Color(245, 166, 35), 180);
        btnEdit.addActionListener(e -> editEmployee());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JButton createFormButton(String text, Color bgColor, Color borderColor, int width) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(width, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(62, 39, 35));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        styleTextField(textField);
        return textField;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(255, 235, 205));

        JLabel lblSearch = new JLabel("Tìm theo Mã Nhân Viên:");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 14));
        lblSearch.setForeground(new Color(62, 39, 35));

        txtSearch = new JTextField(35);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFocusPainted(false);
        btnSearch.setFont(new Font("Arial", Font.BOLD, 13));
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setPreferredSize(new Dimension(130, 35));
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        btnSearch.addActionListener(e -> searchEmployee());

        panel.add(lblSearch);
        panel.add(txtSearch);
        panel.add(btnSearch);

        return panel;
    }

    private JScrollPane createTable() {
        String[] columns = {"Mã", "Tên nhân viên", "Ngày sinh", "Số CCCD", "Số điện thoại", "Giới tính", "Email", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(135, 206, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    loadSelectedRow();
                }
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 178, 102));
        header.setForeground(new Color(62, 39, 35));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140), 2));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140), 2));

        return scrollPane;
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);

        List<NhanVien> dsNV = nhanVien_DAO.getAllNhanVien();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (NhanVien nv : dsNV) {
            
            // 💡 FIX: SỬA LỖI LOGIC TẠI ĐÂY:
            // true là "Nữ", false là "Nam" (để khớp với hàm addEmployee)
            String gioiTinhStr = (nv.getGioiTinh() != null && nv.getGioiTinh()) ? "Nữ" : "Nam";
            
            // Xử lý ngày sinh an toàn
            String ngaySinhStr = (nv.getNgaySinh() != null) ? sdf.format(nv.getNgaySinh()) : "";

            tableModel.addRow(new Object[]{
                nv.getMaNV(),
                nv.getHoTen(),
                ngaySinhStr,
                nv.getCCCD(),
                nv.getSoDienThoai(),
                gioiTinhStr,
                nv.getEmail(),
                nv.getTrangThai()
            });
        }
    }

    private void addEmployee() {
        // 1. Get data from form and validate input
        String maNV = txtEmployeeId.getText().trim();
        String hoTen = txtName.getText().trim();
        java.util.Date ngaySinh = dateChooserBirthDate.getDate();
        String cccd = txtIdNumber.getText().trim();
        String sdt = txtPhone.getText().trim();

        // 💡 Xử lý giới tính: Giới tính là NOT NULL trong CSDL (BIT NOT NULL)
        Boolean gioiTinh = null; 
        if (rbMale.isSelected()) gioiTinh = false; // 0: Nam (BIT false)
        else if (rbFemale.isSelected()) gioiTinh = true; // 1: Nữ (BIT true)
        
        // Kiểm tra Giới tính: Nếu không chọn và CSDL là NOT NULL
        if (gioiTinh == null) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn giới tính.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
             return;
        }

        String email = txtEmail.getText().trim();
        String trangThai = cbStatus.getSelectedItem().toString();
        String maQL = "QL001"; // Hardcoded manager ID (Cần đảm bảo QL001 tồn tại)

        try {
            // 2. Validation (Check if fields are empty, if not handled by individual setters in Entity)
            if (maNV.isEmpty() || hoTen.isEmpty() || cccd.isEmpty() || sdt.isEmpty() || trangThai.isEmpty()) {
                 throw new IllegalArgumentException("Các trường Mã NV, Họ tên, CCCD, SĐT, Trạng thái không được để trống.");
            }
            
            

            NhanVien nv = new NhanVien(maNV, hoTen, cccd, sdt, email, gioiTinh, ngaySinh, trangThai, new QuanLy(maQL));

            // 4. Call DAO (throws SQLException)
            nhanVien_DAO.addNhanVien(nv);

            // 5. Success
            loadDataToTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Đã thêm nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e_val) {
            JOptionPane.showMessageDialog(this, e_val.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e_sql) {
            handleSQLException(e_sql, maNV, cccd, maQL, "Thêm");
        } catch (Exception e_all) {
            e_all.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn. Chi tiết: " + e_all.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Get data from form and validate
        String maNV = txtEmployeeId.getText().trim();
        String hoTen = txtName.getText().trim();
        java.util.Date ngaySinh = dateChooserBirthDate.getDate();
        String cccd = txtIdNumber.getText().trim();
        String sdt = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String trangThai = cbStatus.getSelectedItem().toString();

        Boolean gioiTinh = null;
        if (rbMale.isSelected()) gioiTinh = false; 
        else if (rbFemale.isSelected()) gioiTinh = true;

        if (maNV.isEmpty() || hoTen.isEmpty() || cccd.isEmpty() || sdt.isEmpty() || trangThai.isEmpty() || gioiTinh == null) {
             JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
             return;
        }

        try {
            // 2. GET OLD MANAGER ID (maQL) and CHUCVU
            NhanVien nvHienTai = nhanVien_DAO.findNhanVienById(maNV);
            if (nvHienTai == null) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy nhân viên với mã này để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String maQL_cu = nvHienTai.getQuanLy().getMaQL();
            

            // 3. Create new object (throws IllegalArgumentException)
            NhanVien nvMoi = new NhanVien(maNV, hoTen, cccd, sdt, email, gioiTinh, ngaySinh, trangThai, new QuanLy(maQL_cu));

            // 4. Call DAO (throws SQLException)
            nhanVien_DAO.updateNhanVien(nvMoi);

            // 5. Success
            loadDataToTable();
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin nhân viên!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e_val) {
            JOptionPane.showMessageDialog(this, e_val.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e_sql) {
            handleSQLException(e_sql, maNV, cccd, null, "Sửa");
        } catch (Exception e_all) {
            e_all.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn. Chi tiết: " + e_all.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles specific SQL exceptions related to constraints.
     */
    private void handleSQLException(SQLException e_sql, String maNV, String cccd, String maQL, String actionType) {
        String errorMessage = e_sql.getMessage();

        // Check for Primary Key (maNV) or Unique Key (CCCD, SĐT, Email) violation
        if (errorMessage.contains("PRIMARY KEY constraint") || errorMessage.contains("UNIQUE KEY constraint")) {
            // Check for specific constraint violation
            if (errorMessage.contains("(" + maNV + ")")) { 
                JOptionPane.showMessageDialog(this, actionType + " thất bại: Mã NV '" + maNV + "' đã tồn tại!", "Lỗi trùng Mã NV", JOptionPane.ERROR_MESSAGE);
            } else if (errorMessage.contains("(" + cccd + ")")) { 
                JOptionPane.showMessageDialog(this, actionType + " thất bại: Số CCCD '" + cccd + "' đã tồn tại!", "Lỗi trùng CCCD", JOptionPane.ERROR_MESSAGE);
            } else {
                // Other potential UNIQUE/PK constraint violation (e.g., duplicate phone number, email)
                JOptionPane.showMessageDialog(this, actionType + " thất bại: Dữ liệu nhập vào bị trùng lặp (CCCD, SĐT, hoặc Email).", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Check for Foreign Key (maQL) violation
        else if (errorMessage.contains("FOREIGN KEY constraint") && actionType.equals("Thêm") && maQL != null) {
             JOptionPane.showMessageDialog(this, actionType + " thất bại: Mã Quản Lý '" + maQL + "' không tồn tại!", "Lỗi Mã Quản Lý", JOptionPane.ERROR_MESSAGE);
        }
        // Handle other SQL errors (print stack trace for debugging)
        else {
            e_sql.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Lỗi CSDL không xác định khi " + actionType.toLowerCase() + ". Chi tiết: " + errorMessage, "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtEmployeeId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());

            try {
                String dateString = tableModel.getValueAt(selectedRow, 2).toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date birthDate = sdf.parse(dateString);
                dateChooserBirthDate.setDate(birthDate);
            } catch (ParseException e) {
                dateChooserBirthDate.setDate(null);
            }

            txtIdNumber.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtPhone.setText(tableModel.getValueAt(selectedRow, 4).toString());
            
            // Fix: Check for exact string match for gender
            String gender = tableModel.getValueAt(selectedRow, 5).toString();
            if (gender.equals("Nam")) rbMale.setSelected(true);
            else if (gender.equals("Nữ")) rbFemale.setSelected(true);
            
            txtEmail.setText(tableModel.getValueAt(selectedRow, 6).toString());

            String status = tableModel.getValueAt(selectedRow, 7).toString();
            cbStatus.setSelectedItem(status);
        }
    }

    private void searchEmployee() {
        String maTimKiem = txtSearch.getText().trim();

        if (maTimKiem.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập Mã Nhân Viên hoặc Tên cần tìm.",
                "Chưa nhập mã",
                JOptionPane.WARNING_MESSAGE);
            loadDataToTable();
            return;
        }

        // Dùng findNhanVien để tìm kiếm gần đúng (MaNV OR HoTen)
        List<NhanVien> dsKetQua = nhanVien_DAO.findNhanVien(maTimKiem); 

        tableModel.setRowCount(0);

        if (dsKetQua.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy nhân viên nào có mã/tên: '" + maTimKiem + "'.",
                "Không tìm thấy",
                JOptionPane.INFORMATION_MESSAGE);

            loadDataToTable(); 
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            for (NhanVien nv : dsKetQua) {
                // 💡 FIX: (Giống loadDataToTable) false=Nam, true=Nữ
                String gioiTinhStr = (nv.getGioiTinh() != null && nv.getGioiTinh()) ? "Nữ" : "Nam";
                String ngaySinhStr = (nv.getNgaySinh() != null) ? sdf.format(nv.getNgaySinh()) : "";

                tableModel.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getHoTen(),
                    ngaySinhStr,
                    nv.getCCCD(),
                    nv.getSoDienThoai(),
                    gioiTinhStr,
                    nv.getEmail(),
                    nv.getTrangThai()
                });
            }
            
            JOptionPane.showMessageDialog(this,
                "Đã tìm thấy " + dsKetQua.size() + " kết quả.",
                "Tìm thấy",
                JOptionPane.INFORMATION_MESSAGE);

            table.setRowSelectionInterval(0, 0);
            loadSelectedRow();
        }
    }
    
    // ... (clearForm and main methods) ...
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý nhân viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            NhanVien_GUI panel = new NhanVien_GUI();
            frame.add(panel);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}