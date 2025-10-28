package gui;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;

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

    private NhanVien_DAO nhanVien_DAO = new NhanVien_DAO();

    public NhanVien_GUI() {
    
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 235, 205));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        JLabel titleLabel = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(139, 69, 19));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // 1. TẠO FORM VÀ BẢNG TRƯỚC
        JPanel formPanel = taoPanelForm();

        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(new Color(255, 235, 205));
        JPanel searchPanel = taoPanelTimKiem();
        JScrollPane tableScrollPane = taoBang();
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
        taiDuLieuVaoBang();
        xoaTrangForm(); // Xóa form và thiết lập trạng thái ban đầu
    }

    
    private void xoaTrangForm() {
        txtEmployeeId.setText("");
        txtName.setText("");
        dateChooserBirthDate.setDate(null);
        txtIdNumber.setText("");
        txtPhone.setText("");
        txtEmail.setText("");

        // Giữ nguyên logic chọn mặc định Nam
        rbMale.setSelected(true);

        cbStatus.setSelectedIndex(0);


        table.clearSelection(); // Bỏ chọn dòng trên bảng
    }

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 222, 179));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 180, 140), 2),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Mã nhân viên, Họ tên (Giữ nguyên labels tiếng Việt)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(taoNhan("Mã nhân viên"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtEmployeeId = taoTextFieldDinhDang();
        panel.add(txtEmployeeId, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(taoNhan("Họ tên"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtName = taoTextFieldDinhDang();
        panel.add(txtName, gbc);

        // Row 2: Ngày sinh, Số CCCD
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(taoNhan("Ngày sinh"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        dateChooserBirthDate = new JDateChooser();
        dateChooserBirthDate.setDateFormatString("dd/MM/yyyy");
        // Sửa lỗi: Lấy thành phần UI của DateEditor một cách an toàn
        Component dateEditorComponent = dateChooserBirthDate.getDateEditor().getUiComponent();
        if (dateEditorComponent instanceof JTextField) {
            JTextField dateEditorTextField = (JTextField) dateEditorComponent;
            dinhDangTextField(dateEditorTextField);
            dateEditorTextField.setEditable(false);
        }
        JPanel dateWrapperPanel = new JPanel(new GridLayout(1, 1));
        dateWrapperPanel.add(dateChooserBirthDate);
        gbc.ipady = 10;
        panel.add(dateWrapperPanel, gbc);
        gbc.ipady = 0;

        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(taoNhan("Số CCCD"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtIdNumber = taoTextFieldDinhDang();
        panel.add(txtIdNumber, gbc);

        // Row 3: Giới tính, Số điện thoại
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(taoNhan("Giới tính"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(new Color(245, 222, 179));
        rbMale = new JRadioButton("Nam"); // Giữ nguyên text
        rbFemale = new JRadioButton("Nữ"); // Giữ nguyên text
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
        panel.add(taoNhan("Số điện thoại"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtPhone = taoTextFieldDinhDang();
        panel.add(txtPhone, gbc);

        // Row 4: Email, Trạng thái làm việc
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(taoNhan("Email"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtEmail = taoTextFieldDinhDang();
        panel.add(txtEmail, gbc);

        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(taoNhan("Trạng thái làm việc"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        cbStatus = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc", "Tạm nghỉ"}); // Giữ nguyên text
        cbStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        cbStatus.setBackground(Color.WHITE);
        panel.add(cbStatus, gbc);

        // Row 5: Buttons (Giữ nguyên text nút)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(new Color(245, 222, 179));

        JButton btnAdd = taoNutForm("Thêm nhân viên", new Color(76, 175, 80), new Color(56, 142, 60), 160);
        btnAdd.addActionListener(e -> themNhanVien());

        JButton btnEdit = taoNutForm("Chỉnh sửa thông tin", new Color(234, 196, 28), new Color(245, 166, 35), 180);
        btnEdit.addActionListener(e -> suaNhanVien());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    /**
     * Tạo nút bấm cho form.
     */
    private JButton taoNutForm(String text, Color bgColor, Color borderColor, int width) {
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

    /**
     * Tạo label.
     */
    private JLabel taoNhan(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(62, 39, 35));
        return label;
    }

    /**
     * Tạo text field đã định dạng.
     */
    private JTextField taoTextFieldDinhDang() {
        JTextField textField = new JTextField();
        dinhDangTextField(textField);
        return textField;
    }

    /**
     * Định dạng text field.
     */
    private void dinhDangTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    /**
     * Tạo panel tìm kiếm.
     */
    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(255, 235, 205));

        // Giữ nguyên labels/text tiếng Việt
        JLabel lblSearch = new JLabel("Tìm theo Mã Nhân Viên hoặc Họ Tên:");
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
        btnSearch.addActionListener(e -> timKiemNhanVien());

        panel.add(lblSearch);
        panel.add(txtSearch);
        panel.add(btnSearch);

        return panel;
    }

    /**
     * Tạo bảng dữ liệu.
     */
    private JScrollPane taoBang() {
        // Giữ nguyên tên cột
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
                    hienThiDongDaChon();
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

    /**
     * Tải dữ liệu vào bảng.
     */
    private void taiDuLieuVaoBang() {
        tableModel.setRowCount(0);

        List<NhanVien> dsNV = nhanVien_DAO.layTatCaNhanVien();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (NhanVien nv : dsNV) {

            // Giữ nguyên logic xử lý giới tính và ngày sinh
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
    }

    /**
     * Thêm nhân viên mới.
     */
    private void themNhanVien() {
        // 1. Get data from form and validate input
        String maNV = txtEmployeeId.getText().trim();
        String hoTen = txtName.getText().trim();
        java.util.Date ngaySinh = dateChooserBirthDate.getDate();
        String cccd = txtIdNumber.getText().trim();
        String sdt = txtPhone.getText().trim();

        // Giữ nguyên logic xử lý giới tính
        Boolean gioiTinh = null;
        if (rbMale.isSelected()) gioiTinh = false; // 0: Nam (BIT false)
        else if (rbFemale.isSelected()) gioiTinh = true; // 1: Nữ (BIT true)

        if (gioiTinh == null) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn giới tính.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE); // Giữ nguyên thông báo
             return;
        }

        String email = txtEmail.getText().trim();
        String trangThai = cbStatus.getSelectedItem().toString();
        String maQL = "QL001"; // Hardcoded manager ID (Cần đảm bảo QL001 tồn tại)

        try {
            // 2. Validation
            if (maNV.isEmpty() || hoTen.isEmpty() || cccd.isEmpty() || sdt.isEmpty() || trangThai.isEmpty()) {
                 throw new IllegalArgumentException("Các trường Mã NV, Họ tên, CCCD, SĐT, Trạng thái không được để trống."); // Giữ nguyên thông báo
            }

            NhanVien nv = new NhanVien(maNV, hoTen, cccd, sdt, email, gioiTinh, ngaySinh, trangThai, new QuanLy(maQL));

            // 4. Call DAO
            nhanVien_DAO.themNhanVien(nv);

            // 5. Success
            taiDuLieuVaoBang();
            xoaTrangForm();
            JOptionPane.showMessageDialog(this, "Đã thêm nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE); // Giữ nguyên thông báo

        } catch (IllegalArgumentException e_val) {
            JOptionPane.showMessageDialog(this, e_val.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE); // Giữ nguyên thông báo
        } catch (SQLException e_sql) {
            xuLyLoiSQL(e_sql, maNV, cccd, maQL, "Thêm");
        } catch (Exception e_all) {
            e_all.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn. Chi tiết: " + e_all.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); // Giữ nguyên thông báo
        }
    }

    /**
     * Sửa thông tin nhân viên.
     */
    private void suaNhanVien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); // Giữ nguyên thông báo
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
             JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE); // Giữ nguyên thông báo
             return;
        }

        try {
            // 2. GET OLD MANAGER ID
            NhanVien nvHienTai = nhanVien_DAO.timNhanVienTheoMa(maNV);
            if (nvHienTai == null) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy nhân viên với mã này để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE); // Giữ nguyên thông báo
                return;
            }
            // Giữ nguyên logic lấy mã quản lý cũ
            String maQL_cu = (nvHienTai.getQuanLy() != null) ? nvHienTai.getQuanLy().getMaQL() : null;


            // 3. Create new object
            NhanVien nvMoi = new NhanVien(maNV, hoTen, cccd, sdt, email, gioiTinh, ngaySinh, trangThai, (maQL_cu != null ? new QuanLy(maQL_cu) : null) );

            // 4. Call DAO
            nhanVien_DAO.capNhatNhanVien(nvMoi);

            // 5. Success
            taiDuLieuVaoBang();
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin nhân viên!", "Thông báo", JOptionPane.INFORMATION_MESSAGE); // Giữ nguyên thông báo

        } catch (IllegalArgumentException e_val) {
            JOptionPane.showMessageDialog(this, e_val.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE); // Giữ nguyên thông báo
        } catch (SQLException e_sql) {
            xuLyLoiSQL(e_sql, maNV, cccd, null, "Sửa");
        } catch (Exception e_all) {
            e_all.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn. Chi tiết: " + e_all.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); // Giữ nguyên thông báo
        }
    }

    /**
     * Xử lý lỗi SQL.
     */
    private void xuLyLoiSQL(SQLException e_sql, String maNV, String cccd, String maQL, String actionType) {
        String errorMessage = e_sql.getMessage();

        // Giữ nguyên logic kiểm tra lỗi và thông báo tiếng Việt
        if (errorMessage.contains("PRIMARY KEY constraint") || errorMessage.contains("UNIQUE constraint")) {
            if (errorMessage.contains("maNV") || (maNV != null && errorMessage.contains("(" + maNV + ")"))) {
                JOptionPane.showMessageDialog(this, actionType + " thất bại: Mã nhân viên '" + maNV + "' đã tồn tại!", "Lỗi trùng Mã NV", JOptionPane.ERROR_MESSAGE);
            } else if (errorMessage.contains("CCCD") || (cccd != null && errorMessage.contains("(" + cccd + ")"))) {
                JOptionPane.showMessageDialog(this, actionType + " thất bại: Số CCCD '" + cccd + "' đã tồn tại!", "Lỗi trùng CCCD", JOptionPane.ERROR_MESSAGE);
            } else if (errorMessage.contains("soDienThoai")) {
                 JOptionPane.showMessageDialog(this, actionType + " thất bại: Số điện thoại này đã được sử dụng.", "Lỗi trùng SĐT", JOptionPane.ERROR_MESSAGE);
            } else if (errorMessage.contains("email")) {
                 JOptionPane.showMessageDialog(this, actionType + " thất bại: Email này đã được sử dụng.", "Lỗi trùng Email", JOptionPane.ERROR_MESSAGE);
            }
             else {
                JOptionPane.showMessageDialog(this, actionType + " thất bại: Dữ liệu nhập vào (Mã NV, CCCD, SĐT, hoặc Email) có thể đã tồn tại.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (errorMessage.contains("FOREIGN KEY constraint") && actionType.equals("Thêm") && maQL != null) {
             JOptionPane.showMessageDialog(this, actionType + " thất bại: Mã Quản Lý '" + maQL + "' không tồn tại trong hệ thống!", "Lỗi Mã Quản Lý không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
        else {
            e_sql.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi Cơ sở dữ liệu không xác định khi " + actionType.toLowerCase() + " nhân viên.\nChi tiết: " + errorMessage, "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hiển thị dữ liệu dòng đã chọn lên form.
     */
    private void hienThiDongDaChon() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtEmployeeId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());

            try {
                String dateString = tableModel.getValueAt(selectedRow, 2).toString();
                 if (!dateString.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date birthDate = sdf.parse(dateString);
                    dateChooserBirthDate.setDate(birthDate);
                } else {
                     dateChooserBirthDate.setDate(null);
                }
            } catch (ParseException e) {
                dateChooserBirthDate.setDate(null);
                 System.err.println("Lỗi parse ngày sinh: " + e.getMessage());
            }

            txtIdNumber.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtPhone.setText(tableModel.getValueAt(selectedRow, 4).toString());

            // Giữ nguyên logic xử lý giới tính
            String gender = tableModel.getValueAt(selectedRow, 5).toString();
            if (gender.equalsIgnoreCase("Nam")) { // Dùng equalsIgnoreCase
                 rbMale.setSelected(true);
            } else if (gender.equalsIgnoreCase("Nữ")) {
                 rbFemale.setSelected(true);
            } else {
                 // Xóa lựa chọn nếu dữ liệu không hợp lệ
                 ((ButtonGroup)rbMale.getModel().getGroup()).clearSelection();
            }


            txtEmail.setText(tableModel.getValueAt(selectedRow, 6).toString());

            String status = tableModel.getValueAt(selectedRow, 7).toString();
            cbStatus.setSelectedItem(status);

            // Khóa ô Mã NV sau khi load
             txtEmployeeId.setEditable(false);
             txtEmployeeId.setBackground(new Color(230, 230, 230));
        }
    }

    /**
     * Tìm kiếm nhân viên.
     */
    private void timKiemNhanVien() {
        String maTimKiem = txtSearch.getText().trim();

        if (maTimKiem.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập Mã Nhân Viên hoặc Họ Tên cần tìm.", // Giữ nguyên thông báo
                    "Chưa nhập từ khóa",
                    JOptionPane.WARNING_MESSAGE);
            taiDuLieuVaoBang();
            return;
        }

        List<NhanVien> dsKetQua = nhanVien_DAO.timNhanVien(maTimKiem);

        tableModel.setRowCount(0); // Luôn xóa bảng trước

        if (dsKetQua.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy nhân viên nào có mã hoặc tên chính xác là: '" + maTimKiem + "'.", // Giữ nguyên thông báo
                    "Không tìm thấy",
                    JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuVaoBang();
            xoaTrangForm();
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            for (NhanVien nv : dsKetQua) {
                // Giữ nguyên logic xử lý giới tính và ngày sinh
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

            table.setRowSelectionInterval(0, 0); // Chọn dòng đầu tiên
            hienThiDongDaChon();
            JOptionPane.showMessageDialog(this,
                    "Đã tìm thấy " + " nhân viên '" + maTimKiem + "'.",
                    "Tìm thấy",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Giữ nguyên phương thức main
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Quản lý nhân viên");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            NhanVien_GUI panel = new NhanVien_GUI();
//            frame.add(panel);
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//            frame.setVisible(true);
//        });
//    }
}