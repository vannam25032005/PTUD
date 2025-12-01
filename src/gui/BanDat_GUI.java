package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import com.toedter.calendar.JDateChooser;
import entity.Ban;
import entity.BanDat;
import entity.KhachHang;
import dao.Ban_DAO; 
import dao.BanDat_DAO;
import dao.KhachHang_DAO;
import connectDB.ConnectDB;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class BanDat_GUI extends JPanel {
    
    private JTextField txtMaDatBan, txtTenKhachHang, txtSoDienThoai, txtSoNguoi, txtTienCoc; 
    private JDateChooser dcNgayDat;
    private JComboBox<String> cboGioDat, cboLoaiBan; 
    private JTextArea txtGhiChu;
    
    private JButton btnDatBan, btnLamMoi, btnGoiMon; 
    
    private JTable tableBan; 
    private DefaultTableModel modelBan; 
    
    private Ban_DAO banDAO = new Ban_DAO();
    private BanDat_DAO banDatDAO = new BanDat_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat currencyFormat = new DecimalFormat("#,###");

    private ArrayList<Ban> danhSachBanHienTai = new ArrayList<>(); 
    
    public BanDat_GUI() {
      
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 239, 213));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel lblTitle = new JLabel("Quản Lý Đặt Bàn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);
        
        // Left panel - Danh sách bàn
        JPanel leftPanel = createBanPanel();
        
        // Right panel - Form đặt bàn
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setOpaque(false);
        
        // Form đặt bàn
        JPanel formPanel = createFormPanel();
        rightPanel.add(formPanel, BorderLayout.NORTH); 
        
        // Thêm khoảng trống để rightPanel không bị co lại
        JPanel emptySpace = new JPanel();
        emptySpace.setOpaque(false);
        rightPanel.add(emptySpace, BorderLayout.CENTER); 
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setOpaque(false);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
add(mainPanel, BorderLayout.CENTER);
        
        // Load dữ liệu ban đầu từ CSDL
        loadBanToTable(banDAO.getAllBan());
        lamMoiForm();

        // Add listeners
        addEventListeners();
    }
    
    // ================== PHẦN THIẾT KẾ GUI ==================

    private JPanel createBanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Danh Sách Bàn",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        // Filter panel với nút Gọi món
        JPanel filterPanel = new JPanel(new BorderLayout(10, 5));
        filterPanel.setOpaque(false);
        
        // Panel cho combobox lọc
        JPanel filterComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterComboPanel.setOpaque(false);
        
        JLabel lblFilter = new JLabel("Lọc theo:");
        cboLoaiBan = new JComboBox<>(new String[]{"Tất cả", "Bàn nhỏ", "Bàn vừa", "Bàn lớn", "Phòng VIP"});
        cboLoaiBan.setPreferredSize(new Dimension(150, 30));
        
        filterComboPanel.add(lblFilter);
        filterComboPanel.add(cboLoaiBan);
        
        // Panel cho nút Gọi món
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        
        btnGoiMon = createButton("🍽️ Gọi món", new Color(255, 152, 0), 120);
        btnGoiMon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        buttonPanel.add(btnGoiMon);
        
        filterPanel.add(filterComboPanel, BorderLayout.WEST);
        filterPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Bảng danh sách bàn
        String[] columnsBan = {"Mã bàn", "Loại bàn", "Số ghế", "Khu vực", "Trạng thái"};
        modelBan = new DefaultTableModel(columnsBan, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableBan = new JTable(modelBan);
        tableBan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableBan.setRowHeight(30);
        tableBan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableBan.getTableHeader().setBackground(new Color(240, 240, 240));
        tableBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Màu sắc cho trạng thái
        tableBan.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && column == 4) {
                    String trangThai = (String) table.getValueAt(row, 4);
                    if ("Trống".equals(trangThai)) {
                        c.setBackground(new Color(200, 255, 200));
                    } else if ("Đã đặt".equals(trangThai)) {
                        c.setBackground(new Color(255, 200, 200));
                    } else if ("Đang sử dụng".equals(trangThai)) {
                        c.setBackground(new Color(255, 255, 150));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollBan = new JScrollPane(tableBan);
        scrollBan.setPreferredSize(new Dimension(380, 500));
        panel.add(scrollBan, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Thông Tin Đặt Bàn",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
            ),
            new EmptyBorder(10, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Mã đặt bàn", txtMaDatBan = createTextField()), gbc);
        
        gbc.gridx = 1;
        panel.add(createFieldPanel("Tên khách hàng *", txtTenKhachHang = createTextField()), gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createFieldPanel("Số điện thoại *", txtSoDienThoai = createTextField()), gbc);
        
        gbc.gridx = 1;
        panel.add(createFieldPanel("Số người *", txtSoNguoi = createTextField()), gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        dcNgayDat = new JDateChooser();
        dcNgayDat.setDateFormatString("dd/MM/yyyy");
        dcNgayDat.setPreferredSize(new Dimension(200, 35));
        dcNgayDat.setMinSelectableDate(new Date());
        panel.add(createFieldPanel("Ngày đặt *", dcNgayDat), gbc);
gbc.gridx = 1;
        String[] gioList = new String[32];
        for (int i = 0; i < 32; i++) {
            int hour = i / 2 + 9;
            int minute = (i % 2) * 30;
            if (hour <= 24) {
                gioList[i] = String.format("%02d:%02d", hour == 24 ? 0 : hour, minute);
            }
        }
        cboGioDat = new JComboBox<>(gioList);
        cboGioDat.setPreferredSize(new Dimension(200, 35));
        panel.add(createFieldPanel("Giờ đặt *", cboGioDat), gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        txtTienCoc = createTextField();
        txtTienCoc.setText("0");
        panel.add(createFieldPanel("Tiền cọc (VNĐ)", txtTienCoc), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setPreferredSize(new Dimension(450, 70));
        panel.add(createFieldPanel("Ghi chú", scrollGhiChu), gbc);
        
        // Buttons
        gbc.gridy = 5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        btnDatBan = createButton("Đặt bàn", new Color(76, 175, 80), 130);
        btnLamMoi = createButton("Làm mới", new Color(158, 158, 158), 130);
        
        // Đã loại bỏ: btnCapNhat, btnXacNhan, btnHuyDat
        buttonPanel.add(btnDatBan);
        buttonPanel.add(btnLamMoi);
        
        panel.add(buttonPanel, gbc);
        
        txtMaDatBan.setEditable(false);
        txtMaDatBan.setBackground(new Color(240, 240, 240));
        
        return panel;
    }
    
    private JPanel createFieldPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 80));
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    private JButton createButton(String text, Color bgColor, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    // ================== PHẦN TẢI VÀ XỬ LÝ DỮ LIỆU CSDL ==================

    private void loadBanToTable(ArrayList<Ban> dsBan) {
        modelBan.setRowCount(0);
        danhSachBanHienTai.clear();
        for (Ban ban : dsBan) {
            modelBan.addRow(new Object[]{
                ban.getMaBan(), ban.getLoaiBan(), ban.getSoGhe(), ban.getKhuVuc(), ban.getTrangThai()
            });
            danhSachBanHienTai.add(ban);
        }
    }
    
    private void capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
        if (banDAO.updateTrangThaiBan(maBan, trangThaiMoi)) {
            for (int i = 0; i < modelBan.getRowCount(); i++) {
                if (maBan.equals(modelBan.getValueAt(i, 0))) {
                    modelBan.setValueAt(trangThaiMoi, i, 4);
                    
                    for (Ban ban : danhSachBanHienTai) {
                        if (ban.getMaBan().equals(maBan)) {
                            ban.setTrangThai(trangThaiMoi);
                            break;
                        }
                    }
                    
                    tableBan.repaint();
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái bàn trong CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== PHẦN XỬ LÝ SỰ KIỆN LOGIC CSDL ==================
    private void addEventListeners() {
        // Click chọn bàn (Giữ nguyên)
        tableBan.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableBan.getSelectedRow();
                    if (row >= 0) {
                        String maBan = (String) modelBan.getValueAt(row, 0);
                        String trangThai = (String) modelBan.getValueAt(row, 4);
if ("Trống".equals(trangThai)) {
                            JOptionPane.showMessageDialog(BanDat_GUI.this, 
                                "Đã chọn bàn: " + maBan, 
                                "Thông báo", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(BanDat_GUI.this, 
                                "Bàn này đã được đặt hoặc đang sử dụng!", 
                                "Cảnh báo", 
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });

        // Nút Gọi món (Giữ nguyên)
        btnGoiMon.addActionListener(e -> moGiaoDienGoiMon());

        // Đặt bàn (Giữ nguyên)
        btnDatBan.addActionListener(e -> datBanMoi());
        
        // Làm mới
        btnLamMoi.addActionListener(e -> lamMoiForm());
        
        // Lọc loại bàn
        cboLoaiBan.addActionListener(e -> locBanTheoLoai());
    }

    // ================== PHẦN LOGIC XỬ LÝ ==================

    /**
     * Mở giao diện gọi món khi có bàn được chọn
     */
    private void moGiaoDienGoiMon() {
        int selectedRow = tableBan.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn bàn trước khi gọi món!",
                    "Chưa chọn bàn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maBan = (String) modelBan.getValueAt(selectedRow, 0);
        String trangThaiBan = (String) modelBan.getValueAt(selectedRow, 4);

        // ============================
        //  TRƯỜNG HỢP BÀN ĐANG TRỐNG
        // ============================
        if ("Trống".equals(trangThaiBan)) {

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bàn " + maBan + " đang trống.\n"
                  + "Bạn có muốn chuyển sang trạng thái 'Đang sử dụng' và bắt đầu gọi món không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                try {
                    // 1. Tạo mã đặt bàn
                    String maDatBanMoi = banDatDAO.generateNewMaDatBan();

                    // 2. Tạo khách hàng mặc định
                    KhachHang kh = new KhachHang(null, "Khách lẻ", "0000000000", "", false);
                    kh = khachHangDAO.themHoacLayKhachHang(kh);

                    // 3. Lấy đối tượng bàn
                    Ban banObj = banDAO.getBanById(maBan);
                    LocalTime gioVao = LocalTime.now();

                    // 4. Tạo bản ghi đặt bàn KHÔNG QUA VALIDATION GIỜ
                    BanDat bdMoi = new BanDat(
                            maDatBanMoi,
                            kh,
                            banObj,
                            LocalDate.now(),
                            LocalTime.now(),  // giờ đặt = giờ khách vào
                            1,
                            0,
                            "Đang sử dụng",
                            "Khách vào trực tiếp",
                            gioVao
                    );

                    // ⛔ QUAN TRỌNG: GỌI HÀM CHUYÊN DÙNG CHO TRỰC TIẾP
                    banDatDAO.addBanDatTrucTiep(bdMoi);

                    // 5. Lưu giờ check-in
                    banDatDAO.updateGioCheckIn(maDatBanMoi, gioVao);

                    // 6. Cập nhật trạng thái bàn
                    capNhatTrangThaiBan(maBan, "Đang sử dụng");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi tạo bản ghi đặt bàn khi khách vào trực tiếp:\n" + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 7. Mở giao diện gọi món
                chuyenSangGoiMon(maBan);
            }

            return;
        }

        // ============================
        //  CÁC TRƯỜNG HỢP KHÁC
        // ============================
        if ("Đã đặt".equals(trangThaiBan) || "Đang sử dụng".equals(trangThaiBan)) {
            chuyenSangGoiMon(maBan);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể gọi món cho bàn có trạng thái: " + trangThaiBan,
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    
    /**
     * Chuyển sang giao diện gọi món
     * @param maBan Mã bàn được chọn để gọi món.
     */
private void chuyenSangGoiMon(String maBan) {
        // 1. Lấy cửa sổ cha (JFrame chứa BanDat_GUI)
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Ẩn cửa sổ hiện tại (BanDat_GUI)
        parentFrame.setVisible(false);

        // 3. Tạo cửa sổ mới cho việc Gọi Món
        JFrame goiMonFrame = new JFrame("Gọi Món cho Bàn: " + maBan); 
        goiMonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        goiMonFrame.setSize(1200, 800); 
        goiMonFrame.setLocationRelativeTo(parentFrame); 

        // 4. Thêm WindowListener để xử lý khi cửa sổ Gọi Món đóng
        goiMonFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                // Hiện lại cửa sổ BanDat_GUI
                parentFrame.setVisible(true);
                parentFrame.toFront(); 

                // Tải lại danh sách bàn để cập nhật trạng thái
                loadBanToTable(banDAO.getAllBan());
            }
        });

        try {
            // 5. Khởi tạo Panel Gọi Món và TRUYỀN MÃ BÀN VÀO 
      
            GoiMon_GUI goiMonPanel = new GoiMon_GUI(maBan); 

            // 6. Đặt Panel vào Frame và hiển thị
            goiMonFrame.setContentPane(goiMonPanel);
            goiMonFrame.setVisible(true);

        } catch (Exception e) { 
            JOptionPane.showMessageDialog(parentFrame, "Lỗi khi khởi tạo giao diện Gọi Món (CSDL): " + e.getMessage(), "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
            parentFrame.setVisible(true); 
            goiMonFrame.dispose(); 
            e.printStackTrace();
        }
    }
    
   
    private BanDat validateAndCreateBanDat(String maDatBanHienTai) throws Exception {
        
        // --- 1. Validation Cơ bản ---
        if (txtTenKhachHang.getText().trim().isEmpty() || 
            txtSoDienThoai.getText().trim().isEmpty() ||
            txtSoNguoi.getText().trim().isEmpty() ||
            dcNgayDat.getDate() == null) {
            throw new Exception("Vui lòng điền đầy đủ thông tin bắt buộc (*)");
        }
        
        // --- 2. Validation SĐT ---
        String sdt = txtSoDienThoai.getText().trim();
        if (!sdt.matches("^0\\d{9}$")) {
            throw new Exception("Số điện thoại không hợp lệ! (10 số, bắt đầu bằng 0)");
        }
        
        // --- 3. Validation Số người ---
        int soNguoi;
        try {
soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
            if (soNguoi <= 0) {
                throw new Exception("Số lượng khách phải lớn hơn 0.");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Số lượng khách không hợp lệ!");
        }
        
        // --- 4. Validation Tiền cọc ---
        double tienCoc = 0;
        try {
            String tienCocStr = txtTienCoc.getText().trim().replaceAll("[^0-9]", "");
            if (!tienCocStr.isEmpty()) {
                tienCoc = Double.parseDouble(tienCocStr);
            }
            if (tienCoc < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new Exception("Tiền cọc không hợp lệ!");
        }
        
        // --- 5. Validation Bàn được chọn ---
        int selectedBanRow = tableBan.getSelectedRow();
        if (selectedBanRow < 0) {
            throw new Exception("Vui lòng chọn bàn từ danh sách bên trái!");
        }
        
        String maBan = (String) modelBan.getValueAt(selectedBanRow, 0);
        Ban banDuocChon = banDAO.getBanById(maBan);

        // --- 6. Chuẩn bị các đối tượng Entity ---
        LocalDate ngayDat = dcNgayDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime gioDat = LocalTime.parse(cboGioDat.getSelectedItem().toString());
        // Trạng thái luôn là "Chờ xác nhận" khi đặt bàn mới
        String trangThai = "Đã đặt"; 
        String ghiChu = txtGhiChu.getText();
        
        // --- 7. Xử lý Khách hàng ---
        String tenKH = txtTenKhachHang.getText().trim();
        KhachHang khachHang = khachHangDAO.timKhachHangTheoSDT(sdt);
        
        if(khachHang == null) {
            khachHang = new KhachHang(
                null,       
                tenKH,
                sdt,
                "",        
                false       
            );  
        } else {
           
            khachHang.setHoTenKH(tenKH);
        }

        // --- 8. Tạo đối tượng BanDat ---
        BanDat banDat = new BanDat(
            maDatBanHienTai, 
            khachHang, 
            banDuocChon, 
            ngayDat, 
            gioDat, 
            soNguoi, 
            tienCoc, 
            trangThai, 
            ghiChu,
            null
        );

        return banDat;
    }
    
    private void datBanMoi() {
        try {
            String maDatBanMoi = banDatDAO.generateNewMaDatBan();
            BanDat banDat = validateAndCreateBanDat(maDatBanMoi);
            KhachHang khachHangHoanChinh = khachHangDAO.themHoacLayKhachHang(banDat.getKhachHang());
            banDat.setKhachHang(khachHangHoanChinh);
            
            if (banDatDAO.addBanDat(banDat)) {
                JOptionPane.showMessageDialog(this, 
                    "Đặt bàn thành công!\nMã đặt bàn: " + maDatBanMoi, 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadBanToTable(banDAO.getAllBan());
                lamMoiForm();
                
                // ✨ THÊM DÒNG NÀY - Thông báo cho DanhSachBanDat_GUI refresh
                if (refreshListener != null) {
                    refreshListener.onDataChanged();
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể thêm đặt bàn vào CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void locBanTheoLoai() {
        String loaiChon = (String) cboLoaiBan.getSelectedItem();
        
        if ("Tất cả".equals(loaiChon)) {
            loadBanToTable(banDAO.getAllBan());
            return;
        }
        
        String filterValue = "";
        switch (loaiChon) {
            case "Bàn nhỏ": filterValue = "Bàn nhỏ"; break;
            case "Bàn vừa": filterValue = "Bàn vừa"; break;
            case "Bàn lớn": filterValue = "Bàn lớn"; break;
            case "Phòng VIP": filterValue = "Phòng VIP"; break;
        }

        ArrayList<Ban> dsBanLoc = banDAO.getFilteredBan("loaiBan", filterValue);
        loadBanToTable(dsBanLoc);
    }

    private void lamMoiForm() {
        txtMaDatBan.setText(banDatDAO.generateNewMaDatBan());
        txtTenKhachHang.setText("");
        txtSoDienThoai.setText("");
        txtSoNguoi.setText("");
        txtTienCoc.setText("0");
        dcNgayDat.setDate(new Date());
        cboGioDat.setSelectedIndex(0);
        txtGhiChu.setText("");
        
        tableBan.clearSelection();
    }
    private DataRefreshListener refreshListener; // THÊM DÒNG NÀY
    
    // THÊM PHƯƠNG THỨC NÀY
    public void setDataRefreshListener(DataRefreshListener listener) {
        this.refreshListener = listener;
    }
    
    // THÊM PHƯƠNG THỨC NÀY
    public void refreshData() {
        loadBanToTable(banDAO.getAllBan());
    }
   
//     public static void main(String[] args) throws SQLException {
//         ConnectDB.getInstance().connect();
//         SwingUtilities.invokeLater(() -> {
//             JFrame frame = new JFrame("Hệ Thống Đặt Bàn (Đơn giản)");
//             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frame.setSize(1200, 850);
//             frame.add(new BanDat_GUI());
//             frame.setLocationRelativeTo(null);
//frame.setVisible(true);
//         });
//     }
}
