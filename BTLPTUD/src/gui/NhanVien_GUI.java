package gui;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NhanVien_GUI extends JPanel {

    public NhanVien_GUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 200, 150)); // màu nền tổng thể

        // ====== TIÊU ĐỀ ======
        JLabel lblTitle = new JLabel("Quản lý nhân viên", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ====== FORM NHẬP LIỆU ======
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(245, 210, 180));
        formPanel.setBorder(new CompoundBorder(
                new EmptyBorder(15, 20, 15, 20),
                new LineBorder(new Color(230, 180, 130), 2, true)
        ));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Các trường nhập
        JTextField txtMaNV = new JTextField(15);
        JTextField txtHoTen = new JTextField(15);
        JTextField txtNgaySinh = new JTextField(15);
        JTextField txtCCCD = new JTextField(15);
        JTextField txtSDT = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc"});

        // Giới tính
        JRadioButton rbNam = new JRadioButton("Nam");
        JRadioButton rbNu = new JRadioButton("Nữ");
        ButtonGroup bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(rbNam);
        bgGioiTinh.add(rbNu);
        rbNam.setBackground(formPanel.getBackground());
        rbNu.setBackground(formPanel.getBackground());

        // ====== HÀNG 1: Mã NV + Họ tên ======
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã nhân viên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMaNV, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtHoTen, gbc);

        // ====== HÀNG 2: Ngày sinh + CCCD ======
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNgaySinh, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Số CCCD:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtCCCD, gbc);

        // ====== HÀNG 3: Giới tính + SDT ======
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        JPanel pnGioiTinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnGioiTinh.setBackground(formPanel.getBackground());
        pnGioiTinh.add(rbNam);
        pnGioiTinh.add(rbNu);
        gbc.gridx = 1;
        formPanel.add(pnGioiTinh, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtSDT, gbc);

        // ====== HÀNG 4: Email + Trạng thái ======
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Trạng thái làm việc:"), gbc);
        gbc.gridx = 3;
        formPanel.add(cbTrangThai, gbc);

        // ====== HÀNG 5: Nút chức năng ======
        JButton btnThem = new JButton("Thêm nhân viên");
        JButton btnSua = new JButton("Chỉnh sửa thông tin");
        btnThem.setBackground(new Color(60, 179, 113)); // xanh lá
        btnThem.setForeground(Color.WHITE);
        btnSua.setBackground(new Color(255, 215, 0));   // vàng
        btnSua.setForeground(Color.BLACK);
        btnThem.setFocusPainted(false);
        btnSua.setFocusPainted(false);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSua.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setBackground(formPanel.getBackground());
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ====== THANH TÌM KIẾM + BẢNG DỮ LIỆU ======
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(getBackground());
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Ô tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(bottomPanel.getBackground());
        JTextField txtSearch = new JTextField("Tìm theo mã hoặc tên", 25);
        JButton btnSearch = new JButton("🔍");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        bottomPanel.add(searchPanel, BorderLayout.NORTH);

        // Bảng
        String[] columnNames = {"Mã", "Tên nhân viên", "Ngày sinh", "Số CCCD", "Số điện thoại", "Giới tính", "Email"};
        Object[][] data = {
                {"NV001", "Nguyễn Văn A", "03/06/2006", "094205001522", "0367123456", "Nam", "Anguyen123@gmail.com"},
                {"NV002", "Trần Thị B", "17/10/2004", "094205114356", "0367654321", "Nữ", "satthutraitim25@gmail.com"},
                {"NV003", "Đặng Thành A", "25/08/2005", "094205113261", "09426789012", "Nam", "contimtrayxuoc4@gmail.com"},
                {"NV004", "Trần Minh H", "06/04/2005", "094205112345", "09361234124", "Nam", "quetoirauma36@gmail.com"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setBorder(new LineBorder(Color.BLACK, 1));
        JScrollPane scrollPane = new JScrollPane(table);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    
}
