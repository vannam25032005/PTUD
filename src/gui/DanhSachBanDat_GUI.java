	package gui;
	
	import javax.swing.*;
	import javax.swing.border.*;
	import javax.swing.table.DefaultTableModel;
	import java.awt.*;
	import java.awt.event.*;
	import java.sql.SQLException;
	import java.time.format.DateTimeFormatter;
	import java.util.ArrayList;
	
	import com.toedter.calendar.JDateChooser;
	import entity.Ban;
	import entity.BanDat;
	import entity.KhachHang;
	import dao.Ban_DAO; 
	import dao.BanDat_DAO;
	import connectDB.ConnectDB;
	
	import java.text.DecimalFormat;
	import java.text.SimpleDateFormat;
	import java.util.Date;
	import java.time.LocalDate;
	import java.time.LocalTime;
	import java.time.ZoneId;
	import java.util.stream.Collectors;
	
	public class DanhSachBanDat_GUI extends JPanel {
	    
	    private JTextField txtMaDatBan, txtTenKhachHang, txtSoDienThoai, txtSoNguoi, txtTienCoc, txtMaBan; 
	    private JDateChooser dcNgayDat;
	    private JTextField txtGioDat; 
	    private JComboBox<String> cboTrangThaiLoc;
	    private JTextArea txtGhiChu;
	    
	    private JTable tableDatBan; 
	    private DefaultTableModel modelDatBan; 

	    private JTextField txtTimKiem;
	    private JButton btnTimKiem, btnXoaBoLoc;
	    
	    private JButton  btnHuyDat, btnCheckIn; 
	    
	    private Ban_DAO banDAO = new Ban_DAO();
	    private BanDat_DAO banDatDAO = new BanDat_DAO();
	
	    private DecimalFormat currencyFormat = new DecimalFormat("#,###");
	    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    
	    private ArrayList<BanDat> danhSachTatCaDatBan = new ArrayList<>();
	
	    public DanhSachBanDat_GUI() {
	      
	        setLayout(new BorderLayout(15, 15));
	        setBackground(new Color(255, 239, 213));
	        setBorder(new EmptyBorder(20, 20, 20, 20));
	        
	        // Title
	        JLabel lblTitle = new JLabel("Quản Lý Danh Sách Bàn Đã Đặt", SwingConstants.CENTER);
	        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
	        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
	        add(lblTitle, BorderLayout.NORTH);
	        
	        // Main content
	        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
	        mainPanel.setOpaque(false);
	        
	        // Left panel - Danh sách đặt bàn
	        JPanel leftPanel = createDatBanTablePanel();
	        
	        // Right panel - Form chi tiết và hành động
	        JPanel rightPanel = createDetailActionPanel();
	        
	        // Split pane
	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
	        splitPane.setDividerLocation(800);
	        splitPane.setOpaque(false);
	        
	        mainPanel.add(splitPane, BorderLayout.CENTER);
	        add(mainPanel, BorderLayout.CENTER);
	        
	        
	        loadAllDataAndFilter(); 
	        lamMoiForm();
	
	        addEventListeners();
	    }
	    
	    // ================== PHẦN THIẾT KẾ GUI ==================
	
	    private JPanel createDatBanTablePanel() {
	        // ... (Giữ nguyên phần thiết kế Bảng, Search, Filter)
	        JPanel panel = new JPanel(new BorderLayout(10, 10));
	        panel.setOpaque(false);
	        panel.setBorder(new TitledBorder(
	            BorderFactory.createLineBorder(new Color(200, 200, 200)),
	            "Danh Sách Đặt Bàn",
	            TitledBorder.LEFT,
	            TitledBorder.TOP,
	            new Font("Segoe UI", Font.BOLD, 14)
	        ));
	        
	        // Search and Filter Panel
	        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
	        searchPanel.setOpaque(false);
	        
	        txtTimKiem = createTextField();
	        txtTimKiem.setPreferredSize(new Dimension(200, 30));
	        txtTimKiem.setToolTipText("Tìm theo Mã đặt, SĐT, hoặc Tên KH");
	        btnTimKiem = createButton("🔍 Tìm", new Color(33, 150, 243), 80);
	        JLabel lblTrangThai = new JLabel("Trạng thái:");
	        cboTrangThaiLoc = new JComboBox<>(new String[]{"Hoạt động (Chờ/Đã XN)", "Chờ xác nhận", "Đã xác nhận", "Tất cả (Bao gồm Hủy/Hoàn thành)"}); 
	        cboTrangThaiLoc.setPreferredSize(new Dimension(220, 30));
	        
	        btnXoaBoLoc = createButton("🗑️ Xóa lọc", new Color(244, 67, 54), 100);
	        
	        searchPanel.add(new JLabel("Tìm kiếm:"));
	        searchPanel.add(txtTimKiem);
	        searchPanel.add(btnTimKiem);
	        searchPanel.add(lblTrangThai);
	        searchPanel.add(cboTrangThaiLoc);
	        searchPanel.add(btnXoaBoLoc);
	        
	        panel.add(searchPanel, BorderLayout.NORTH);
	        
	        // Bảng danh sách đặt bàn
	        String[] columnsDatBan = {"Mã đặt", "Tên KH", "SĐT", "Bàn", "Ngày đặt", "Giờ đặt", "Tiền cọc", "Trạng thái"};
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
	        tableDatBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        
	        // Màu sắc cho trạng thái trong bảng (Giữ nguyên)
	        tableDatBan.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
	        });
	        
	        JScrollPane scrollDatBan = new JScrollPane(tableDatBan);
	        panel.add(scrollDatBan, BorderLayout.CENTER);
	        
	        return panel;
	    }
	    
	    private JPanel createDetailActionPanel() {
	        JPanel panel = new JPanel();
	        panel.setLayout(new GridBagLayout());
	        panel.setBackground(Color.WHITE);
	        panel.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createTitledBorder(
	                BorderFactory.createLineBorder(new Color(200, 200, 200)),
	                "Chi Tiết & Hành Động",
	                TitledBorder.LEFT,
	                TitledBorder.TOP,
	                new Font("Segoe UI", Font.BOLD, 14)
	            ),
	            new EmptyBorder(10, 20, 20, 20)
	        ));
	        
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.insets = new Insets(8, 10, 8, 10);
	        
	        // Form fields (Giữ nguyên)
	        txtMaDatBan = createTextField(); txtMaDatBan.setEditable(false); txtMaDatBan.setBackground(new Color(240, 240, 240));
	txtMaBan = createTextField(); txtMaBan.setEditable(false); txtMaBan.setBackground(new Color(240, 240, 240));
	        txtTenKhachHang = createTextField(); txtTenKhachHang.setEditable(false);
	        txtSoDienThoai = createTextField(); txtSoDienThoai.setEditable(false);
	        txtSoNguoi = createTextField(); txtSoNguoi.setEditable(false);
	        txtTienCoc = createTextField(); txtTienCoc.setEditable(false);
	        txtGioDat = createTextField(); txtGioDat.setEditable(false);
	        
	        dcNgayDat = new JDateChooser(); dcNgayDat.setDateFormatString("dd/MM/yyyy"); dcNgayDat.setEnabled(false);
	        
	        txtGhiChu = new JTextArea(3, 20);
	        txtGhiChu.setEditable(false);
	        txtGhiChu.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
	            BorderFactory.createEmptyBorder(5, 5, 5, 5)
	        ));
	        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
	        txtGhiChu.setLineWrap(true);
	        txtGhiChu.setWrapStyleWord(true);
	        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
	        scrollGhiChu.setPreferredSize(new Dimension(250, 70));

	        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
	        panel.add(createFieldPanel("Mã đặt bàn", txtMaDatBan), gbc);
	        gbc.gridy = 1;
	        panel.add(createFieldPanel("Mã Bàn", txtMaBan), gbc);
	        gbc.gridy = 2;
	        panel.add(createFieldPanel("Tên khách hàng", txtTenKhachHang), gbc);
	        gbc.gridy = 3;
	        panel.add(createFieldPanel("Số điện thoại", txtSoDienThoai), gbc);
	        gbc.gridy = 4;
	        panel.add(createFieldPanel("Số người", txtSoNguoi), gbc);
	        gbc.gridy = 5;
	        panel.add(createFieldPanel("Ngày đặt", dcNgayDat), gbc);
	        gbc.gridy = 6;
	        panel.add(createFieldPanel("Giờ đặt", txtGioDat), gbc);
	        gbc.gridy = 7;
	        panel.add(createFieldPanel("Tiền cọc (VNĐ)", txtTienCoc), gbc);
	        gbc.gridy = 8;
	        panel.add(createFieldPanel("Ghi chú", scrollGhiChu), gbc);
	        
	        gbc.gridy = 9; gbc.insets = new Insets(20, 10, 8, 10);
	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
	        buttonPanel.setOpaque(false);
	        
	        btnCheckIn = createButton("Check-in", new Color(255, 152, 0), 120);
	        btnHuyDat = createButton("Hủy đặt", new Color(244, 67, 54), 100);
	        
	        buttonPanel.add(btnCheckIn);
	        buttonPanel.add(btnHuyDat);
	        
	        panel.add(buttonPanel, gbc);
	return panel;
	    }
	    
	    // Phương thức tạo FieldPanel, TextField, Button 
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
	
	    private void loadAllDataAndFilter() {
	        danhSachTatCaDatBan = banDatDAO.getAllBanDat();
	        timKiemVaLoc(); 
	    }
	    
	    /**
	     * Tải dữ liệu đã lọc lên bảng JTable
	     */
	    private void loadDatBanTable(ArrayList<BanDat> dsDatBan) {
	        modelDatBan.setRowCount(0);
	        for (BanDat bd : dsDatBan) {
	            modelDatBan.addRow(new Object[]{
	                bd.getMaDatBan(),
	                bd.getKhachHang().getHoTenKH(),
	                bd.getKhachHang().getSoDienThoai(),
	                bd.getBan().getMaBan(),
	                bd.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
	                bd.getGioDat().format(timeFormatter),
	                currencyFormat.format(bd.getTienCoc()),
	bd.getTrangThai()
	            });
	        }
	    }
	    
	    /**
	     * Phương thức cập nhật trạng thái bàn vật lý
	     */
	    private void capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
	        if (!banDAO.updateTrangThaiBan(maBan, trangThaiMoi)) {
	            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái bàn vật lý trong CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	
	    // ================== PHẦN XỬ LÝ SỰ KIỆN LOGIC CSDL ==================
	    private void addEventListeners() {
	        // Click chọn đặt bàn -> Hiển thị chi tiết
	        tableDatBan.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                int row = tableDatBan.getSelectedRow();
	                if (row >= 0) {
	                    hienThiThongTinDatBan(row);
	                }
	            }
	        });
	
	        // Nút Check-in
	        btnCheckIn.addActionListener(e -> checkInDatBan());
	        // Nút Hủy đặt
	        btnHuyDat.addActionListener(e -> huyDatBan());
	        
	        
	        // Tìm kiếm và Lọc
	        btnTimKiem.addActionListener(e -> timKiemVaLoc());
	        txtTimKiem.addActionListener(e -> timKiemVaLoc());
	        cboTrangThaiLoc.addActionListener(e -> timKiemVaLoc());
	        btnXoaBoLoc.addActionListener(e -> {
	            txtTimKiem.setText("");
	            cboTrangThaiLoc.setSelectedIndex(0);
	            timKiemVaLoc();
	        });
	    }
	
	    // ================== PHẦN LOGIC XỬ LÝ ==================
	
	    private void hienThiThongTinDatBan(int row) {
	        if (row < 0) return;
	        
	        try {
	            String maDatBan = (String) modelDatBan.getValueAt(row, 0);
	            BanDat bd = banDatDAO.getBanDatById(maDatBan); 
	
	            if (bd != null) {
	                txtMaDatBan.setText(bd.getMaDatBan());
	                txtMaBan.setText(bd.getBan().getMaBan());
	                txtTenKhachHang.setText(bd.getKhachHang().getHoTenKH());
	                txtSoDienThoai.setText(bd.getKhachHang().getSoDienThoai());
	                txtSoNguoi.setText(String.valueOf(bd.getSoLuongKhach())); 
	                txtTienCoc.setText(currencyFormat.format(bd.getTienCoc()));
	                
	                Date ngayDatDate = Date.from(bd.getNgayDat().atStartOfDay(ZoneId.systemDefault()).toInstant());
	                dcNgayDat.setDate(ngayDatDate);
	txtGioDat.setText(bd.getGioDat().format(timeFormatter));
	                
	                txtGhiChu.setText(bd.getGhiChu());
	                
	                // Cập nhật trạng thái nút theo trạng thái đặt bàn
	                String trangThai = bd.getTrangThai();
	           
	                // Chỉ cho phép Hủy nếu không phải đã "Hoàn thành" hoặc "Đã hủy"
	                btnHuyDat.setEnabled(!"Đã hủy".equals(trangThai));
	                
	                btnCheckIn.setEnabled( "Đã đặt".equals(trangThai)); 
	
	            } else {
	                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (Exception e) {
	            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết đặt bàn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    
	    /**
	     * Cập nhật trạng thái đơn đặt bàn và trạng thái bàn vật lý.
	     * Sử dụng cho checkin và Hủy.
	     */
	    private void capNhatTrangThaiDatBan(String trangThaiMoi) {
	        String maDatBan = txtMaDatBan.getText().trim();
	        if (maDatBan.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn một mục đặt bàn để cập nhật trạng thái!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	
	        BanDat banDat = banDatDAO.getBanDatById(maDatBan);
	        if (banDat == null) return;
	        
	        String trangThaiHienTai = banDat.getTrangThai();
	        if (trangThaiHienTai.equals(trangThaiMoi)) {
	            JOptionPane.showMessageDialog(this, "Đặt bàn này đã ở trạng thái \"" + trangThaiMoi + "\" rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
	            return;
	        }
	
	        String maBan = banDat.getBan().getMaBan();
	        banDat.setTrangThai(trangThaiMoi);
	
	        if (banDatDAO.updateBanDat(banDat)) {
	            if (trangThaiMoi.equals("Đã xác nhận")) {
	                capNhatTrangThaiBan(maBan, "Đã đặt"); 
	            } else if (trangThaiMoi.equals("Đã hủy")) {
	                capNhatTrangThaiBan(maBan, "Trống"); 
	            }
	            
	            JOptionPane.showMessageDialog(this,
	                "Đã chuyển trạng thái ĐẶT BÀN sang: " + trangThaiMoi, 
	                "Thành công", 
	                JOptionPane.INFORMATION_MESSAGE);
	            
	            loadAllDataAndFilter();
	            lamMoiForm();
	            
	            // ✨ THÊM DÒNG NÀY - Thông báo cho BanDat_GUI refresh
	            if (refreshListener != null) {
	                refreshListener.onDataChanged();
	            }
	            
	        } else {
	            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái đặt bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    
	    private void huyDatBan() {
	        String maDatBan = txtMaDatBan.getText().trim();
	         if (maDatBan.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn một mục đặt bàn để hủy!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        
	        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn HỦY đặt bàn này? (Bàn sẽ được giải phóng)", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
	        
	        if (confirm == JOptionPane.YES_OPTION) {
	            capNhatTrangThaiDatBan("Đã hủy");
	        }
	    }
	    
	    /**
	     * Xử lý nghiệp vụ Check-in: Cập nhật trạng thái đặt bàn và chuyển sang Gọi Món.
	     */
	    private void checkInDatBan() {
	        String maDatBan = txtMaDatBan.getText().trim();
	        if (maDatBan.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn một mục đặt bàn để Check-in!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        
	        BanDat banDat = banDatDAO.getBanDatById(maDatBan);
	        if (banDat == null) return;
	        String maBan = banDat.getBan().getMaBan();
	        String trangThaiHienTai = banDat.getTrangThai();
	
	        int confirm = JOptionPane.showConfirmDialog(this, 
	            "Xác nhận Check-in cho đơn: " + maDatBan + " (Bàn " + maBan + " sẽ chuyển sang 'Đang sử dụng')", 
	            "Xác nhận Check-in", 
	            JOptionPane.YES_NO_OPTION);
	
	        if (confirm != JOptionPane.YES_OPTION) {
	            return;
	        }
	        LocalTime gioVao = LocalTime.now();
	        banDat.setGioCheckIn(gioVao);
	        banDatDAO.updateGioCheckIn(maDatBan, gioVao);
	        banDat.setTrangThai("Hoàn thành");
	        if (!banDatDAO.updateBanDat(banDat)) {
	            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái đặt bàn sang 'Hoàn thành'.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
	            return;
	        }
	
	        capNhatTrangThaiBan(maBan, "Đang sử dụng");
	        loadAllDataAndFilter();
	        lamMoiForm();
	       
	        if (refreshListener != null) {
	            refreshListener.onDataChanged();
	        }
	        
	        JOptionPane.showMessageDialog(this, "Check-in thành công. Bàn " + maBan + " đang sử dụng.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
	    }
	
	    private void timKiemVaLoc() {
	        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
	        String trangThaiLoc = (String) cboTrangThaiLoc.getSelectedItem();
	        
	        // Lấy danh sách đang hoạt động (Chờ/Đã XN) nếu chọn mặc định
	        ArrayList<BanDat> dsLoc = new ArrayList<>(danhSachTatCaDatBan);
	        
	        // Lọc theo trạng thái
	        if (trangThaiLoc.equals("Hoạt động (Chờ/Đã XN)")) {
	            dsLoc = (ArrayList<BanDat>) dsLoc.stream()
	                .filter(bd -> bd.getTrangThai().equals("Đã đặt") || bd.getTrangThai().equals("Đã xác nhận"))
	                .collect(Collectors.toList());
	        } else if (!trangThaiLoc.equals("Tất cả (Bao gồm Hủy/Hoàn thành)")) {
	            // Lọc theo trạng thái cụ thể (Chờ XN hoặc Đã XN)
	            dsLoc = (ArrayList<BanDat>) dsLoc.stream()
	                .filter(bd -> bd.getTrangThai().equals(trangThaiLoc))
	                .collect(Collectors.toList());
	        } 
	        // Lọc theo từ khóa (Mã đặt, SĐT, Tên KH)
	        if (!tuKhoa.isEmpty()) {
	            dsLoc = (ArrayList<BanDat>) dsLoc.stream()
	                .filter(bd -> bd.getMaDatBan().toLowerCase().contains(tuKhoa) ||
	                             bd.getKhachHang().getSoDienThoai().toLowerCase().contains(tuKhoa) || 
	                             bd.getKhachHang().getHoTenKH().toLowerCase().contains(tuKhoa))
	                .collect(Collectors.toList());
	        }
	        
	        loadDatBanTable(dsLoc);
	        lamMoiForm();
	    }
	    
	    private void lamMoiForm() {
	        txtMaDatBan.setText("");
	        txtMaBan.setText("");
	        txtTenKhachHang.setText("");
	        txtSoDienThoai.setText("");
	        txtSoNguoi.setText("");
	        txtTienCoc.setText("");
	        dcNgayDat.setDate(null);
	        txtGioDat.setText("");
	        txtGhiChu.setText("");
	        tableDatBan.clearSelection();
	        
	   ;
	        btnHuyDat.setEnabled(false);
	        btnCheckIn.setEnabled(false);
	    }
	    private DataRefreshListener refreshListener;
	    
	    public void setDataRefreshListener(DataRefreshListener listener) {
	        this.refreshListener = listener;
	    }
	    
	    public void refreshData() {
	        loadAllDataAndFilter();
	    }
	    
	    // ================== MAIN (Ví dụ) ==================
//	     public static void main(String[] args) throws SQLException {
//	          ConnectDB.getInstance().connect();
//	         SwingUtilities.invokeLater(() -> {
//	             JFrame frame = new JFrame("Quản Lý Danh Sách Đặt Bàn");
//	             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	             frame.setSize(1200, 800);
//	             frame.add(new DanhSachBanDat_GUI());
//	             frame.setLocationRelativeTo(null);
//	             frame.setVisible(true);
//	         });
//	     }
	}
