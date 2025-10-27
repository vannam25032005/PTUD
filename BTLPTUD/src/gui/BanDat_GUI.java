package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import com.toedter.calendar.JDateChooser;

// Import các lớp Entity và DAO
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
    
    private JTextField txtMaDatBan, txtTenKhachHang, txtSoDienThoai, txtSoNguoi, txtTienCoc, txtTimKiem;
    private JDateChooser dcNgayDat;
    private JComboBox<String> cboGioDat, cboTrangThai, cboLoaiBan, cboFilterTrangThai;
    private JTextArea txtGhiChu;
    private JButton btnDatBan, btnHuyDat, btnCapNhat, btnXacNhan, btnTimKiem, btnXoaBoLoc, btnLamMoi, btnGoiMon;
    private JTable tableDatBan, tableBan;
    private DefaultTableModel modelDatBan, modelBan;
    
    // Khởi tạo các DAO
    private Ban_DAO banDAO = new Ban_DAO();
    private BanDat_DAO banDatDAO = new BanDat_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat currencyFormat = new DecimalFormat("#,###");

    // Dùng để lưu danh sách bàn hiện tại (cho mục đích lọc trong GUI)
    private ArrayList<Ban> danhSachBanHienTai = new ArrayList<>(); 
    
    public BanDat_GUI() {
     
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 239, 213));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel lblTitle = new JLabel("Quản Lý Đặt Bàn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);
        
        // Left panel - Danh sách bàn
        JPanel leftPanel = createBanPanel();
        
        // Right panel - Form đặt bàn và danh sách đặt bàn
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setOpaque(false);
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Form đặt bàn
        JPanel formPanel = createFormPanel();
        rightPanel.add(formPanel, BorderLayout.CENTER);
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setOpaque(false);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Bottom - Danh sách đặt bàn
        JPanel bottomPanel = createDatBanTablePanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Load dữ liệu ban đầu từ CSDL
        loadBanToTable(banDAO.getAllBan());
        loadDatBanTable(banDatDAO.getAllBanDat());
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
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(250, 35));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        btnTimKiem = new JButton("🔍 Tìm");
        btnTimKiem.setPreferredSize(new Dimension(80, 35));
        btnTimKiem.setBackground(Color.WHITE);
        
        cboFilterTrangThai = new JComboBox<>(new String[]{"Tất cả", "Chờ xác nhận", "Đã xác nhận", "Đã hủy", "Hoàn thành"});
        cboFilterTrangThai.setPreferredSize(new Dimension(140, 35));
        
        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        btnXoaBoLoc.setPreferredSize(new Dimension(110, 35));
        btnXoaBoLoc.setBackground(Color.WHITE);
        
        panel.add(new JLabel("Tìm kiếm:"));
        panel.add(txtTimKiem);
        panel.add(btnTimKiem);
        panel.add(cboFilterTrangThai);
        panel.add(btnXoaBoLoc);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
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
        
        gbc.gridx = 1;
        cboTrangThai = new JComboBox<>(new String[]{"Chờ xác nhận", "Đã xác nhận", "Đã hủy", "Hoàn thành"});
        cboTrangThai.setPreferredSize(new Dimension(200, 35));
        panel.add(createFieldPanel("Trạng thái", cboTrangThai), gbc);
        
        // Row 5 - Ghi chú (full width)
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
        btnCapNhat = createButton("Cập nhật", new Color(255, 193, 7), 130);
        btnXacNhan = createButton("Xác nhận", new Color(33, 150, 243), 130);
        btnHuyDat = createButton("Hủy đặt", new Color(244, 67, 54), 130);
        btnLamMoi = createButton("Làm mới", new Color(158, 158, 158), 130);
        
        buttonPanel.add(btnDatBan);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnHuyDat);
        buttonPanel.add(btnLamMoi);
        
        panel.add(buttonPanel, gbc);
        
        txtMaDatBan.setEditable(false);
        txtMaDatBan.setBackground(new Color(240, 240, 240));
        
        return panel;
    }
    
    private JPanel createDatBanTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Danh Sách Đặt Bàn",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        String[] columnsDatBan = {"Mã ĐB", "Khách hàng", "SĐT", "Số người", "Ngày đặt", "Giờ", "Tiền cọc", "Mã bàn", "Trạng thái"};
        modelDatBan = new DefaultTableModel(columnsDatBan, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableDatBan = new JTable(modelDatBan);
        tableDatBan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableDatBan.setRowHeight(30);
        tableDatBan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableDatBan.getTableHeader().setBackground(new Color(240, 240, 240));
        JScrollPane scrollDatBan = new JScrollPane(tableDatBan);
        scrollDatBan.setPreferredSize(new Dimension(1000, 200));
        panel.add(scrollDatBan, BorderLayout.CENTER);
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
  
    private void loadDatBanTable(ArrayList<BanDat> dsDatBan) {
        modelDatBan.setRowCount(0);
        if (dsDatBan != null) {
            for (BanDat banDat : dsDatBan) {
                modelDatBan.addRow(new Object[]{
                    banDat.getMaDatBan(),
                    banDat.getKhachHang().getHoTenKH(),
                    banDat.getKhachHang().getSoDienThoai(),
                    banDat.getSoLuongKhach(),
                    dateFormat.format(Date.from(banDat.getNgayDat().atStartOfDay(ZoneId.systemDefault()).toInstant())),
                    banDat.getGioDat().toString(),
                    currencyFormat.format(banDat.getTienCoc()),
                    banDat.getBan().getMaBan(),
                    banDat.getTrangThai()
                });
            }
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
        // Click chọn bàn
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
        
        // Click chọn đặt bàn để cập nhật
        tableDatBan.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableDatBan.getSelectedRow();
                if (row >= 0) {
                    hienThiThongTinDatBan(row);
                }
            }
        });  

        // Nút Gọi món
        btnGoiMon.addActionListener(e -> moGiaoDienGoiMon());

        // Đặt bàn
        btnDatBan.addActionListener(e -> datBanMoi());
        
        // Cập nhật
        btnCapNhat.addActionListener(e -> capNhatDatBan());
        
        // Xác nhận
        btnXacNhan.addActionListener(e -> capNhatTrangThaiDatBan("Đã xác nhận"));
        
        // Hủy đặt
        btnHuyDat.addActionListener(e -> huyDatBan());
        
        // Làm mới
        btnLamMoi.addActionListener(e -> lamMoiForm());
        
        // Tìm kiếm
        btnTimKiem.addActionListener(e -> timKiemDatBan());
        
        // Xóa bộ lọc
        btnXoaBoLoc.addActionListener(e -> {
            txtTimKiem.setText("");
            cboFilterTrangThai.setSelectedIndex(0);
            loadDatBanTable(banDatDAO.getAllBanDat());
        });
        
        // Lọc loại bàn
        cboLoaiBan.addActionListener(e -> locBanTheoLoai());

        // Lọc trạng thái đặt bàn
        cboFilterTrangThai.addActionListener(e -> timKiemDatBan());
    }

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
        
        // Kiểm tra trạng thái bàn
        if ("Trống".equals(trangThaiBan)) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bàn " + maBan + " đang trống. Bạn có muốn chuyển sang trạng thái 'Đang sử dụng' và gọi món không?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Cập nhật trạng thái bàn sang "Đang sử dụng"
                capNhatTrangThaiBan(maBan, "Đang sử dụng");
                chuyenSangGoiMon(maBan);
            }
        } else if ("Đã đặt".equals(trangThaiBan) || "Đang sử dụng".equals(trangThaiBan)) {
            // Cho phép gọi món với bàn đã đặt hoặc đang sử dụng
            chuyenSangGoiMon(maBan);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không thể gọi món cho bàn có trạng thái: " + trangThaiBan, 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Chuyển sang giao diện gọi món
     * Bạn cần implement class GoiMon_GUI
     */
    private void chuyenSangGoiMon(String maBan) {
    	
        // 1. Lấy cửa sổ chính (JFrame) đang chứa JPanel này
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        if (parentFrame == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. ẨN CỬA SỔ CHỌN BÀN (parentFrame)
        parentFrame.setVisible(false);

        // 3. TẠO VÀ CẤU HÌNH CỬA SỔ GỌI MÓN
        JFrame goiMonFrame = new JFrame("Gọi Món cho Bàn: " + maBan);
        goiMonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        goiMonFrame.setSize(1200, 800); // Kích thước lớn hơn cho giao diện gọi món
        goiMonFrame.setLocationRelativeTo(null);
        
        // Tham chiếu đến GUI hiện tại để sử dụng sau khi đóng
        // BanDat_GUI currentGui = this; // Giữ nguyên tham chiếu nếu cần làm mới BanDat_GUI

        // 4. ĐÍNH KÈM WindowListener ĐỂ HIỆN LẠI CỬA SỔ CHỌN BÀN
        goiMonFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                // HIỆN LẠI CỬA SỔ CHỌN BÀN khi cửa sổ Gọi Món đóng
                parentFrame.setVisible(true);
                parentFrame.toFront();
                
                // TODO: Nếu BanDat_GUI cần cập nhật trạng thái bàn,
                // bạn có thể gọi một phương thức làm mới ở đây:
                // currentGui.lamMoiTrangThaiBan(maBan); 
            }
        });

        try {
            // 5. KHỞI TẠO VÀ THÊM PANEL GỌI MÓN VÀO FRAME MỚI
            // Giả sử GoiMon_GUI có constructor không tham số ném ra SQLException
            GoiMon_GUI goiMonPanel = new GoiMon_GUI(/* TODO: Thêm tham số nếu cần */); 
            
            goiMonFrame.setContentPane(goiMonPanel);
            goiMonFrame.setVisible(true);

        } catch (SQLException e) {
            // Xử lý lỗi CSDL nếu không khởi tạo được GoiMon_GUI
            JOptionPane.showMessageDialog(parentFrame, "Lỗi khi khởi tạo giao diện Gọi Món (CSDL): " + e.getMessage(), "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
            parentFrame.setVisible(true); // Hiện lại cửa sổ cha ngay lập tức
            goiMonFrame.dispose(); // Đóng frame rỗng
            e.printStackTrace();
        }
    }
private BanDat validateAndCreateBanDat(String maDatBanHienTai) throws Exception {
        
        // --- 1. Validation Cơ bản (Giữ nguyên) ---
        if (txtTenKhachHang.getText().trim().isEmpty() || 
            txtSoDienThoai.getText().trim().isEmpty() ||
            txtSoNguoi.getText().trim().isEmpty() ||
            dcNgayDat.getDate() == null) {
            throw new Exception("Vui lòng điền đầy đủ thông tin bắt buộc (*)");
        }
        
        // --- 2. Validation SĐT (Giữ nguyên) ---
        String sdt = txtSoDienThoai.getText().trim();
        if (!sdt.matches("^0\\d{9}$")) {
            throw new Exception("Số điện thoại không hợp lệ! (10 số, bắt đầu bằng 0)");
        }
        
        // --- 3. Validation Số người (Giữ nguyên) ---
        int soNguoi;
        try {
            soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
            if (soNguoi <= 0) {
                throw new Exception("Số lượng khách phải lớn hơn 0.");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Số lượng khách không hợp lệ!");
        }
        
        // --- 4. Validation Tiền cọc (Giữ nguyên) ---
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
        
        // --- 5. Validation Bàn được chọn (Giữ nguyên) ---
        int selectedBanRow = tableBan.getSelectedRow();
        if (selectedBanRow < 0) {
            throw new Exception("Vui lòng chọn bàn từ danh sách bên trái!");
        }
        
        String maBan = (String) modelBan.getValueAt(selectedBanRow, 0);
        Ban banDuocChon = banDAO.getBanById(maBan);

        // --- 6. Chuẩn bị các đối tượng Entity (Giữ nguyên) ---
        LocalDate ngayDat = dcNgayDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime gioDat = LocalTime.parse(cboGioDat.getSelectedItem().toString());
        String trangThai = cboTrangThai.getSelectedItem().toString();
        String ghiChu = txtGhiChu.getText();
        
        // --- 7. Xử lý Khách hàng (ĐÃ SỬA CHỮA) ---
        String tenKH = txtTenKhachHang.getText().trim();
        
        // Tìm kiếm KH đã tồn tại trong CSDL
        KhachHang khachHang = khachHangDAO.getKhachHangBySdt(sdt);
        
        if(khachHang == null) {
            // KH MỚI: Tạo đối tượng TẠM THỜI với các giá trị mặc định cho Email và Giới tính.
            // Đây là điểm sửa để tránh lỗi NOT NULL.
            khachHang = new KhachHang(
null,       // maKH = null (sẽ được phát sinh trong datBanMoi)
                tenKH,
                sdt,
                "",         // Email mặc định rỗng (nếu CSDL chấp nhận)
                false       // Giới tính mặc định Nam (0)
            ); 
            
        } else {
            // KHÁCH HÀNG ĐÃ TỒN TẠI: Gán tên mới vào đối tượng đã có maKH
            khachHang.setHoTenKH(tenKH);
        }

        // --- 8. Tạo đối tượng BanDat (Giữ nguyên) ---
        BanDat banDat = new BanDat(
            maDatBanHienTai, 
            khachHang, 
            banDuocChon, 
            ngayDat, 
            gioDat, 
            soNguoi, 
            tienCoc, 
            trangThai, 
            ghiChu
        );

        return banDat;
    }
    
private void datBanMoi() {
    try {
        // 1. Phát sinh Mã Đặt Bàn mới
        String maDatBanMoi = banDatDAO.generateNewMaDatBan();
        
        // 2. Validate và tạo đối tượng BanDat TẠM THỜI (Chứa KH tạm thời)
        BanDat banDat = validateAndCreateBanDat(maDatBanMoi);
        
        // 3. XỬ LÝ KHÁCH HÀNG: Tìm kiếm, nếu chưa có thì Phát sinh mã và Thêm vào CSDL
        KhachHang khachHangHoanChinh = khachHangDAO.addOrGetKhachHang(banDat.getKhachHang());
        
        // 4. Gán Khách hàng ĐÃ CÓ MAKH CHÍNH THỨC VÀO BanDat
        banDat.setKhachHang(khachHangHoanChinh); 
        
        // 5. Validation trạng thái bàn
        if (!"Trống".equals(banDat.getBan().getTrangThai())) {
             JOptionPane.showMessageDialog(this, 
                "Bàn này đã được đặt hoặc đang sử dụng!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 6. Gọi DAO để thêm đặt bàn (Lúc này, maKH đã hợp lệ, tránh lỗi Khóa Ngoại)
        if (banDatDAO.addBanDat(banDat)) {
            // Cập nhật trạng thái bàn sang "Đã đặt"
            capNhatTrangThaiBan(banDat.getBan().getMaBan(), "Đã đặt");
            
            JOptionPane.showMessageDialog(this, 
                "Đặt bàn thành công!\nMã đặt bàn: " + maDatBanMoi, 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            
            loadDatBanTable(banDatDAO.getAllBanDat());
            lamMoiForm();
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

private void hienThiThongTinDatBan(int row) {
    String maDatBan = (String) modelDatBan.getValueAt(row, 0);
    BanDat banDat = banDatDAO.getBanDatById(maDatBan);
    
    if (banDat != null) {
        txtMaDatBan.setText(banDat.getMaDatBan());
        txtTenKhachHang.setText(banDat.getKhachHang().getHoTenKH());
        txtSoDienThoai.setText(banDat.getKhachHang().getSoDienThoai());
        txtSoNguoi.setText(String.valueOf(banDat.getSoLuongKhach()));
        
        // Set DateChooser
        Date date = Date.from(banDat.getNgayDat().atStartOfDay(ZoneId.systemDefault()).toInstant());
        dcNgayDat.setDate(date);
        
        // Set ComboBox Giờ
        cboGioDat.setSelectedItem(banDat.getGioDat().toString());
        
        // Hiển thị tiền cọc
txtTienCoc.setText(String.valueOf((int)banDat.getTienCoc()));
        
        cboTrangThai.setSelectedItem(banDat.getTrangThai());
        txtGhiChu.setText(banDat.getGhiChu());
        
        // Tìm và chọn bàn tương ứng
        String maBan = banDat.getBan().getMaBan();
        for (int i = 0; i < modelBan.getRowCount(); i++) {
            if (maBan.equals(modelBan.getValueAt(i, 0))) {
                tableBan.setRowSelectionInterval(i, i);
                tableBan.scrollRectToVisible(tableBan.getCellRect(i, 0, true));
                break;
            }
        }
    }
}

    
    private void capNhatDatBan() {
        int row = tableDatBan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn đặt bàn cần cập nhật!", 
                "Chưa chọn", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String maDatBan = txtMaDatBan.getText();
            BanDat banDatMoi = validateAndCreateBanDat(maDatBan);
            
            String maBanCu = (String) modelDatBan.getValueAt(row, 7);
            String maBanMoi = banDatMoi.getBan().getMaBan();
            if (!maBanCu.equals(maBanMoi)) {
                String trangThaiCu = (String) modelDatBan.getValueAt(row, 8);
                if (!trangThaiCu.equals("Đã hủy") && !trangThaiCu.equals("Hoàn thành")) {
                    capNhatTrangThaiBan(maBanCu, "Trống");
                    capNhatTrangThaiBan(maBanMoi, "Đã đặt");
                }
            }
            
            if (banDatDAO.updateBanDat(banDatMoi)) {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật đặt bàn thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadDatBanTable(banDatDAO.getAllBanDat());
                loadBanToTable(banDAO.getAllBan());
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể cập nhật đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatTrangThaiDatBan(String trangThaiMoi) {
        int row = tableDatBan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt bàn!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maDatBan = (String) modelDatBan.getValueAt(row, 0);
        BanDat banDat = banDatDAO.getBanDatById(maDatBan);

        if (banDat == null) return;
        
        if (banDat.getTrangThai().equals(trangThaiMoi)) {
            JOptionPane.showMessageDialog(this, "Đặt bàn này đã ở trạng thái \"" + trangThaiMoi + "\" rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        banDat.setTrangThai(trangThaiMoi);

        if (banDatDAO.updateBanDat(banDat)) {
            if (trangThaiMoi.equals("Đã xác nhận")) {
                 capNhatTrangThaiBan(banDat.getBan().getMaBan(), "Đã đặt");
            } else if (trangThaiMoi.equals("Hoàn thành")) {
                 capNhatTrangThaiBan(banDat.getBan().getMaBan(), "Trống");
            }
            
            JOptionPane.showMessageDialog(this, 
                "Đã chuyển trạng thái sang: " + trangThaiMoi, 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            
            loadDatBanTable(banDatDAO.getAllBanDat());
            loadBanToTable(banDAO.getAllBan());
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void huyDatBan() {
        int row = tableDatBan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt bàn cần hủy!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy đặt bàn này? (Bàn sẽ được giải phóng)", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String maDatBan = (String) modelDatBan.getValueAt(row, 0);
            BanDat banDat = banDatDAO.getBanDatById(maDatBan);

            if (banDat == null) return;

            if (!"Hoàn thành".equals(banDat.getTrangThai())) {
                capNhatTrangThaiBan(banDat.getBan().getMaBan(), "Trống");
            }
            
            banDat.setTrangThai("Đã hủy");

            if (banDatDAO.updateBanDat(banDat)) {
                JOptionPane.showMessageDialog(this, "Đã hủy đặt bàn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDatBanTable(banDatDAO.getAllBanDat());
                loadBanToTable(banDAO.getAllBan());
                lamMoiForm();
            } else {
                 JOptionPane.showMessageDialog(this, "Lỗi khi hủy đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void lamMoiForm() {
        txtMaDatBan.setText(banDatDAO.generateNewMaDatBan());
        txtTenKhachHang.setText("");
        txtSoDienThoai.setText("");
        txtSoNguoi.setText("");
        txtTienCoc.setText("0");
        dcNgayDat.setDate(new Date());
        cboGioDat.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        txtGhiChu.setText("");
        
        tableDatBan.clearSelection();
        tableBan.clearSelection();
    }
    
    private void timKiemDatBan() {
        String keyword = txtTimKiem.getText().trim();
        String trangThaiLoc = (String) cboFilterTrangThai.getSelectedItem();
        
        ArrayList<BanDat> dsKetQua = banDatDAO.searchBanDat(keyword, trangThaiLoc);
        loadDatBanTable(dsKetQua);
        
        if (dsKetQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy kết quả phù hợp!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
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
    
    // ================== MAIN ==================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ Thống Đặt Bàn (CSDL Integrated)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);
            frame.add(new BanDat_GUI());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}