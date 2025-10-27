package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL; 
import java.sql.SQLException; 
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

// Import các lớp cần thiết để làm việc với CSDL
import connectDB.ConnectDB; 
import dao.MonAn_DAO;
import entity.MonAn;

// LƯU Ý: Phải đảm bảo lớp ThanhToan_Gui (với constructor Map, Map, double) tồn tại 
// trong package gui hoặc import nó.

public class GoiMon_GUI extends JPanel {
    private NumberFormat dinhDangTien;

    private JComboBox<String> cboLoaiMon;
    private JPanel pnlDanhSachMon;
    private JTextField txtTimKiem;

    // --- Biến cho Món đang gọi ---
    private JTable tblMonDangGoi;
    private DefaultTableModel modelMonDangGoi;
    private JLabel lblTongTienDangGoi; 
    private Map<String, Integer> gioHang;   
    private Map<String, Integer> bangGia; // Tên món -> Giá (int)
    private double tongTien = 0;              

    // --- Biến cho Tổng tiền Hóa đơn và Món đã gọi ---
    private JLabel lblTongTien;             
    private JTable tblMonDaGoi;
    private DefaultTableModel modelMonDaGoi;
    private Map<String, Integer> gioHangXacNhan; 
    private double tongTienHoaDon = 0;       

    private JTextArea txtGhiChu;
    private JLabel lblTieuDeDangGoi;
    private JLabel lblTieuDeDaGoi;

    // --- BIẾN MỚI TỪ CSDL ---
    private MonAn_DAO monAn_DAO;
    private List<MonAn> danhSachMonAn; // Danh sách món ăn gốc từ CSDL
    private List<MonAn> danhSachMonAnHienThi; // Danh sách hiển thị sau khi lọc/tìm kiếm

    public GoiMon_GUI() throws SQLException {
        dinhDangTien = NumberFormat.getInstance(new Locale("vi", "VN"));

        gioHang = new LinkedHashMap<>();
        bangGia = new HashMap<>();
        gioHangXacNhan = new LinkedHashMap<>();

        // 1. KHỞI TẠO DAO VÀ KẾT NỐI
        monAn_DAO = new MonAn_DAO();
        ConnectDB.getConnection(); // Mở kết nối
        
        // 2. TẢI DỮ LIỆU
        taiDuLieuMonAn();
        khoiTaoGia(); // Đổ dữ liệu giá vào Map

        setLayout(new BorderLayout());
        setBackground(new Color(255, 235, 205));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(255, 218, 185));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lblBack = new JLabel("←");
        lblBack.setFont(new Font("Arial", Font.BOLD, 20));
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Đóng kết nối và đóng cửa sổ
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(GoiMon_GUI.this);
                if (parent != null) {
                    ConnectDB.disconnect(); // ⚠️ Đóng kết nối
                    parent.dispose();
                }
            }
        });
        
        tieuDe.add(lblBack, BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Gọi món", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        tieuDe.add(lblTitle, BorderLayout.CENTER);
        add(tieuDe, BorderLayout.NORTH);

        JPanel pnlChinh = new JPanel(new BorderLayout(12, 12));
        pnlChinh.setBackground(new Color(245, 245, 245));
        pnlChinh.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlTrai = taoKhuVucMenu();
        JPanel pnlPhai = taoKhuVucGioHang();

        pnlChinh.add(pnlTrai, BorderLayout.CENTER);
        pnlChinh.add(pnlPhai, BorderLayout.EAST);

        add(pnlChinh, BorderLayout.CENTER);
    }

    // --- PHƯƠNG THỨC MỚI: Tải và Khởi tạo dữ liệu ---
    private void taiDuLieuMonAn() {
        try {
            danhSachMonAn = monAn_DAO.layTatCaMonAn();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách món ăn từ CSDL: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            danhSachMonAn = new ArrayList<>(); // Tạo danh sách rỗng nếu lỗi
        }
        danhSachMonAnHienThi = new ArrayList<>(danhSachMonAn); 
    }

    private void khoiTaoGia() {
        bangGia.clear(); 
        if (danhSachMonAn != null) {
            for (MonAn mon : danhSachMonAn) {
                // Đổ tên món và giá vào Map bangGia (ép kiểu double -> int)
                bangGia.put(mon.getTenMonAn(), (int)mon.getGiaMonAn()); 
            }
        }
    }
    // ----------------------------------------------------


    private JPanel taoKhuVucMenu() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JPanel pnlTop = new JPanel(new BorderLayout(8, 0));
        pnlTop.setBackground(Color.WHITE);

        JPanel pnlLoai = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlLoai.setBackground(Color.WHITE);
        JLabel lblLoai = new JLabel("Loại món");
        lblLoai.setFont(new Font("Arial", Font.BOLD, 13));
        
        // ⚠️ LẤY DANH SÁCH LOẠI MÓN TỪ DAO
        List<String> loaiMonCSDL = new ArrayList<>();
        try {
            loaiMonCSDL = monAn_DAO.layDanhSachLoaiMon();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String[] dsLoaiMoi = new String[loaiMonCSDL.size() + 1];
        dsLoaiMoi[0] = "Tất cả";
        for(int i = 0; i < loaiMonCSDL.size(); i++) {
            dsLoaiMoi[i+1] = loaiMonCSDL.get(i);
        }
        
        cboLoaiMon = new JComboBox<>(dsLoaiMoi);
        cboLoaiMon.setPreferredSize(new Dimension(140, 32));
        cboLoaiMon.addActionListener(e -> locMon());
        pnlLoai.add(lblLoai);
        pnlLoai.add(cboLoaiMon);
        
        // ... (phần code tạo ô tìm kiếm) ...
        JPanel pnlTim = new JPanel(new BorderLayout(4, 0));
        pnlTim.setBackground(Color.WHITE);
        pnlTim.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        pnlTim.setPreferredSize(new Dimension(300, 34));
        txtTimKiem = new JTextField();
        txtTimKiem.setBorder(BorderFactory.createEmptyBorder(4,8,4,4));
        JButton btnTim = new JButton("🔍");
        btnTim.setBorder(BorderFactory.createEmptyBorder());
        btnTim.setFocusPainted(false);
        btnTim.setBackground(Color.WHITE);
        btnTim.addActionListener(e -> timMon());
        pnlTim.add(txtTimKiem, BorderLayout.CENTER);
        pnlTim.add(btnTim, BorderLayout.EAST);

        pnlTop.add(pnlLoai, BorderLayout.WEST);
        pnlTop.add(pnlTim, BorderLayout.EAST);

        pnlDanhSachMon = new JPanel(new GridLayout(0, 3, 10, 12));
        pnlDanhSachMon.setBackground(Color.WHITE);
        pnlDanhSachMon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // ⚠️ HIỂN THỊ MÓN ĂN THỰC TẾ LẦN ĐẦU
        hienThiMonAn(danhSachMonAnHienThi); 

        JScrollPane cuon = new JScrollPane(pnlDanhSachMon);
        cuon.setBorder(null);
        cuon.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(pnlTop, BorderLayout.NORTH);
        panel.add(cuon, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Phương thức hiển thị các item món ăn lên pnlDanhSachMon
     */
    private void hienThiMonAn(List<MonAn> ds) {
        pnlDanhSachMon.removeAll(); // Xóa các thành phần cũ
        pnlDanhSachMon.setLayout(new GridLayout(0, 3, 10, 12)); // Đảm bảo layout không bị mất
        
        if (ds != null && !ds.isEmpty()) {
            for (MonAn mon : ds) {
                // Sử dụng thuộc tính của MonAn entity
                themMon(mon.getTenMonAn(), (int)mon.getGiaMonAn(), mon.getHinhAnh());
            }
        } else {
            // Thêm thông báo nếu danh sách rỗng
            pnlDanhSachMon.setLayout(new BorderLayout());
            pnlDanhSachMon.add(new JLabel("Không tìm thấy món ăn nào.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        pnlDanhSachMon.revalidate();
        pnlDanhSachMon.repaint();
    }
    
    // --- LỌC VÀ TÌM KIẾM DỮ LIỆU THỰC TẾ ---
    private void locMon() {
        String loaiChon = (String) cboLoaiMon.getSelectedItem();
        danhSachMonAnHienThi.clear();
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        
        for (MonAn mon : danhSachMonAn) {
            boolean thoaManLoai = "Tất cả".equals(loaiChon) || mon.getLoaiMonAn().equals(loaiChon);
            boolean thoaManTimKiem = mon.getTenMonAn().toLowerCase().contains(tuKhoa);
            
            if (thoaManLoai && thoaManTimKiem) {
                danhSachMonAnHienThi.add(mon);
            }
        }
        hienThiMonAn(danhSachMonAnHienThi);
    }

    private void timMon() {
        locMon(); 
    }
    
    // ... (Các phương thức khác giữ nguyên) ...
    
    private void themMon(String ten, int gia, String imagePath) { 
        // Code tạo item món ăn
        JPanel item = new JPanel(new BorderLayout(6,6));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(8,8,8,8)
        ));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- KHU VỰC TẢI VÀ HIỂN THỊ HÌNH ẢNH ---
        JLabel lblAnh = new JLabel(); 
        lblAnh.setPreferredSize(new Dimension(110, 90));
        lblAnh.setOpaque(true);
        lblAnh.setBackground(new Color(250,250,250));
        lblAnh.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        
        // LOGIC ĐƯỜNG DẪN ĐÃ SỬA ĐỔI:
        // 1. Giữ nguyên imagePath (VD: "monan.png")
        // 2. Nối với tiền tố cố định "/src/image/"
        // internalImagePath sẽ là: /src/image/monan.png
        String internalImagePath = "/image/" + imagePath;
        
        try {
            // Tải ảnh từ Classpath
            URL imageUrl = getClass().getResource(internalImagePath);
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image originalImage = originalIcon.getImage();
                Image scaledImage = originalImage.getScaledInstance(90, 80, Image.SCALE_SMOOTH); 
                lblAnh.setIcon(new ImageIcon(scaledImage));
                lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                // Hiển thị đường dẫn bị lỗi để tiện Debug
                lblAnh.setText("Ảnh (Lỗi tìm: " + internalImagePath + ")"); 
                lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            lblAnh.setText("Lỗi Ảnh");
            lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        }
        // --- KẾT THÚC KHU VỰC TẢI ẢNH ---

        JLabel lblTen = new JLabel(ten, SwingConstants.CENTER);
        lblTen.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel lblGia = new JLabel(dinhDangTien.format(gia) + " VND", SwingConstants.CENTER);
        lblGia.setFont(new Font("Arial", Font.PLAIN, 12));
        lblGia.setForeground(new Color(220, 53, 69));

        JPanel pnlDuoi = new JPanel(new BorderLayout(6,6));
        pnlDuoi.setBackground(Color.WHITE);

        SpinnerNumberModel spModel = new SpinnerNumberModel(1, 1, 99, 1);
        JSpinner spSoLuong = new JSpinner(spModel);
        spSoLuong.setPreferredSize(new Dimension(60, 30));
        ((JSpinner.DefaultEditor) spSoLuong.getEditor()).getTextField().setFont(new Font("Arial", Font.BOLD, 12));

        JButton btnChon = new JButton("Chọn");
        btnChon.setBackground(new Color(40, 167, 69));
        btnChon.setForeground(Color.WHITE);
        btnChon.setFocusPainted(false);
        btnChon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChon.addActionListener(e -> {
            int sl = (Integer) spSoLuong.getValue();
            themVaoGio(ten, gia, sl);
        });

        pnlDuoi.add(spSoLuong, BorderLayout.WEST);
        pnlDuoi.add(btnChon, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.add(lblTen);
        info.add(Box.createVerticalStrut(6));
        info.add(lblGia);
        info.add(Box.createVerticalStrut(8));
        info.add(pnlDuoi);

        item.add(lblAnh, BorderLayout.NORTH);
        item.add(info, BorderLayout.CENTER);

        pnlDanhSachMon.add(item);
    }
    
    private JPanel taoKhuVucGioHang() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(420, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));

        // --- KHU VỰC MÓN ĐANG GỌI (GIỎ HÀNG) ---
        lblTieuDeDangGoi = new JLabel("Món đang gọi (0)");
        lblTieuDeDangGoi.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDeDangGoi.setForeground(new Color(255, 140, 0));

        panel.add(lblTieuDeDangGoi);
        panel.add(Box.createVerticalStrut(8));

        String[] cot = {"STT", "Tên món", "SL", "Giá", "Thao tác"};
        modelMonDangGoi = new DefaultTableModel(cot, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        tblMonDangGoi = new JTable(modelMonDangGoi);
        tblMonDangGoi.setRowHeight(42);
        tblMonDangGoi.setFont(new Font("Arial", Font.PLAIN, 12));
        tblMonDangGoi.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 4; i++)
            tblMonDangGoi.getColumnModel().getColumn(i).setCellRenderer(canGiua);

        tblMonDangGoi.getColumnModel().getColumn(4).setCellRenderer(new NutRenderer("Xóa", new Color(220,53,69)));
        tblMonDangGoi.getColumnModel().getColumn(4).setCellEditor(new NutEditor(new JCheckBox(), "Xóa", new Color(220,53,69), (row) -> {
            int modelRow = tblMonDangGoi.convertRowIndexToModel(row);
            if (modelRow >= 0 && modelRow < modelMonDangGoi.getRowCount()) {
                String ten = (String) modelMonDangGoi.getValueAt(modelRow, 1);
                xoaKhoiGio(ten);
            }
        }));

        JScrollPane cuon = new JScrollPane(tblMonDangGoi);
        cuon.setPreferredSize(new Dimension(0, 190));
        panel.add(cuon);
        panel.add(Box.createVerticalStrut(8));

        // --- HIỂN THỊ TỔNG TIỀN CHO MÓN ĐANG GỌI (SUBTOTAL) ---
        JPanel pnlSubTotal = new JPanel(new BorderLayout());
        pnlSubTotal.setBackground(Color.WHITE);
        JLabel lblTxtSubTotal = new JLabel("Tổng tiền tạm tính");
        lblTxtSubTotal.setFont(new Font("Arial", Font.ITALIC, 13));
        lblTongTienDangGoi = new JLabel(dinhDangTien.format(0) + " VND", SwingConstants.RIGHT); 
        lblTongTienDangGoi.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTienDangGoi.setForeground(new Color(255, 140, 0));
        pnlSubTotal.add(lblTxtSubTotal, BorderLayout.WEST);
        pnlSubTotal.add(lblTongTienDangGoi, BorderLayout.EAST);
        panel.add(pnlSubTotal);
        panel.add(Box.createVerticalStrut(10));
        // --- KẾT THÚC SUBTOTAL ---

        JLabel lblGhiChu = new JLabel("Ghi chú");
        lblGhiChu.setFont(new Font("Arial", Font.BOLD, 13));
        txtGhiChu = new JTextArea(4, 20);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        JScrollPane cuonGhiChu = new JScrollPane(txtGhiChu);
        cuonGhiChu.setPreferredSize(new Dimension(0, 70));

        panel.add(lblGhiChu);
        panel.add(Box.createVerticalStrut(6));
        panel.add(cuonGhiChu);
        panel.add(Box.createVerticalStrut(10));

        JPanel pnlNut = new JPanel(new BorderLayout(8,8));
        pnlNut.setBackground(Color.WHITE);
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(new Color(40,167,69));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Arial", Font.BOLD, 14));
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXacNhan.addActionListener(e -> xacNhanDon());

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(220,53,69));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFont(new Font("Arial", Font.BOLD, 14));
        btnHuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> huyDon());

        pnlNut.add(btnXacNhan, BorderLayout.CENTER);
        pnlNut.add(btnHuy, BorderLayout.EAST);

        panel.add(pnlNut);
        panel.add(Box.createVerticalStrut(12));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(8));

        // --- KHU VỰC MÓN ĐÃ GỌI VÀ TỔNG TIỀN CỘNG DỒN ---
        lblTieuDeDaGoi = new JLabel("Món đã gọi (0)");
        lblTieuDeDaGoi.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDeDaGoi.setForeground(new Color(255, 140, 0));
        panel.add(lblTieuDeDaGoi);
        panel.add(Box.createVerticalStrut(8));

        String[] cot2 = {"STT", "Tên món", "SL", "Giá"};
        modelMonDaGoi = new DefaultTableModel(cot2, 0) {
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        tblMonDaGoi = new JTable(modelMonDaGoi);
        tblMonDaGoi.setRowHeight(36);
        tblMonDaGoi.setFont(new Font("Arial", Font.PLAIN, 12));
        tblMonDaGoi.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        DefaultTableCellRenderer canGiua2 = new DefaultTableCellRenderer();
        canGiua2.setHorizontalAlignment(JLabel.CENTER);
        tblMonDaGoi.getColumnModel().getColumn(0).setCellRenderer(canGiua2);
        tblMonDaGoi.getColumnModel().getColumn(1).setCellRenderer(canGiua2);
        tblMonDaGoi.getColumnModel().getColumn(2).setCellRenderer(canGiua2);

        DefaultTableCellRenderer canPhai2 = new DefaultTableCellRenderer();
        canPhai2.setHorizontalAlignment(JLabel.RIGHT);
        tblMonDaGoi.getColumnModel().getColumn(3).setCellRenderer(canPhai2);

        JScrollPane cuon2 = new JScrollPane(tblMonDaGoi);
        cuon2.setPreferredSize(new Dimension(0, 140));
        panel.add(cuon2);
        panel.add(Box.createVerticalStrut(8));

        // TỔNG TIỀN CUỐI CÙNG (CỘNG DỒN)
        JPanel pnlTong = new JPanel(new BorderLayout());
        pnlTong.setBackground(Color.WHITE);
        JLabel lblTxtTong = new JLabel("Tổng tiền hóa đơn");
        lblTxtTong.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTien = new JLabel(dinhDangTien.format(tongTienHoaDon) + " VND", SwingConstants.RIGHT); 
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(220,53,69));

        pnlTong.add(lblTxtTong, BorderLayout.WEST);
        pnlTong.add(lblTongTien, BorderLayout.EAST);
        panel.add(pnlTong);
        panel.add(Box.createVerticalStrut(10));

        JButton btnThanhToan = new JButton("Thanh toán");
        btnThanhToan.setBackground(new Color(0, 123, 255));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
        btnThanhToan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThanhToan.addActionListener(e -> thanhToan()); 
        panel.add(btnThanhToan);

        return panel;
    }

    private void themVaoGio(String ten, int gia, int sl) {
        if (gioHang.containsKey(ten))
            gioHang.put(ten, gioHang.get(ten) + sl);
        else
            gioHang.put(ten, sl);
        capNhatBangGio();
        JOptionPane.showMessageDialog(this, "Đã thêm " + sl + " " + ten + " vào giỏ hàng");
    }

    private void capNhatBangGio() {
        modelMonDangGoi.setRowCount(0);
        int stt = 1;
        tongTien = 0; 
        for (Map.Entry<String, Integer> e : gioHang.entrySet()) {
            String ten = e.getKey();
            int sl = e.getValue();
            // Lấy giá từ Map bangGia đã được load từ CSDL
            int gia = bangGia.getOrDefault(ten, 0); 
            modelMonDangGoi.addRow(new Object[]{stt++, ten, sl, dinhDangTien.format((long)gia * sl) + " VND", "Xóa"});
            tongTien += (long)gia * sl;
        }
        
        lblTongTienDangGoi.setText(dinhDangTien.format(tongTien) + " VND"); 
        
        lblTieuDeDangGoi.setText("Món đang gọi (" + gioHang.size() + ")");
    }

    private void xoaKhoiGio(String ten) {
        gioHang.remove(ten);
        capNhatBangGio();
    }
    
    private void huyDon() {
        if (gioHang.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng, không có gì để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
             return;
        }

        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy đơn đang gọi?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gioHang.clear();
            capNhatBangGio();
            JOptionPane.showMessageDialog(this, "Đơn hàng đang gọi đã được hủy.");
        }
    }

    private void xacNhanDon() {
        if (gioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng, không có gì để xác nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. CỘNG DỒN DỮ LIỆU
        for (Map.Entry<String, Integer> entry : gioHang.entrySet()) {
            String ten = entry.getKey();
            int slThem = entry.getValue();
            
            if (gioHangXacNhan.containsKey(ten)) {
                gioHangXacNhan.put(ten, gioHangXacNhan.get(ten) + slThem);
            } else {
                gioHangXacNhan.put(ten, slThem);
            }
        }
        
        // 2. TÍNH TỔNG TIỀN CỘNG DỒN VÀ CẬP NHẬT BẢNG MÓN ĐÃ GỌI
        modelMonDaGoi.setRowCount(0);
        int stt = 1;
        tongTienHoaDon = 0; 
        
        for (Map.Entry<String, Integer> e : gioHangXacNhan.entrySet()) {
            String ten = e.getKey();
            int sl = e.getValue();
            if (bangGia.containsKey(ten)) {
                 int gia = bangGia.get(ten);
                 long thanhTien = (long)gia * sl;
                 
                 // Thêm món vào bảng Món đã gọi (chỉ còn 4 cột)
                 modelMonDaGoi.addRow(new Object[]{stt++, ten, sl, dinhDangTien.format(thanhTien) + " VND"});
                 tongTienHoaDon += thanhTien; 
            }
        }
        lblTieuDeDaGoi.setText("Món đã gọi (" + gioHangXacNhan.size() + ")");


        // 3. LÀM SẠCH GIỎ HÀNG ĐANG GỌI VÀ CẬP NHẬT TỔNG TIỀN CỘNG DỒN
        gioHang.clear();
        capNhatBangGio(); 
        
        lblTongTien.setText(dinhDangTien.format(tongTienHoaDon) + " VND");

        // LÀM SẠCH Ô GHI CHÚ SAU KHI XÁC NHẬN
        txtGhiChu.setText(""); 

        JOptionPane.showMessageDialog(this, "Đã xác nhận đơn hàng! Các món đang gọi đã được gửi xuống bếp.");
    }
    
    // =========================================================================
    // === PHƯƠNG THỨC LÀM MỚI (MỚI THÊM) =====================================
    // =========================================================================
    /**
     * Làm mới trạng thái GUI sau khi hóa đơn đã được thanh toán xong
     */
    public void lamMoiSauThanhToan() {
        gioHangXacNhan.clear();
        tongTienHoaDon = 0;
        modelMonDaGoi.setRowCount(0);
        lblTongTien.setText(dinhDangTien.format(tongTienHoaDon) + " VND");
        lblTieuDeDaGoi.setText("Món đã gọi (0)");
        
        // Đảm bảo giỏ hàng đang gọi cũng rỗng (nếu chưa xác nhận)
        gioHang.clear();
        capNhatBangGio(); 
        txtGhiChu.setText("");
        
        // Cần thông báo này để biết quá trình hiện lại trang đã thành công
        // (Nếu không muốn thông báo, có thể xóa dòng này)
        JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán và đơn hàng đã làm mới!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // === PHƯƠNG THỨC THANH TOÁN (ĐÃ SỬA ĐỔI) ================================
    // =========================================================================
    /**
     * Mở giao diện ThanhToan_Gui và truyền dữ liệu hóa đơn.
     */
    private void thanhToan() {
        if (gioHangXacNhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có món nào được xác nhận để thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Lấy cửa sổ chính (JFrame) đang chứa JPanel này
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        if (parentFrame == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha để mở màn hình thanh toán.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. ẨN CỬA SỔ GỌI MÓN
        parentFrame.setVisible(false);

        // 3. TẠO VÀ CẤU HÌNH CỬA SỔ THANH TOÁN
        JFrame thanhToanFrame = new JFrame("Thanh toán");
        thanhToanFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        thanhToanFrame.setSize(1000, 750);
        thanhToanFrame.setLocationRelativeTo(null);
        
        // Tham chiếu đến GUI hiện tại để gọi phương thức làm mới
        GoiMon_GUI currentGui = this;

        // 4. ĐÍNH KÈM WindowListener ĐỂ HIỆN LẠI CỬA SỔ GỌI MÓN
        thanhToanFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                // HIỆN LẠI CỬA SỔ GỌI MÓN KHI CỬA SỔ THANH TOÁN ĐÓNG
                parentFrame.setVisible(true);
                parentFrame.toFront();
                
                // Xóa dữ liệu cũ sau khi thanh toán thành công
                currentGui.lamMoiSauThanhToan(); 
            }
        });

        try {
            // KHỞI TẠO ThanhToan_Gui THỰC TẾ
            // LƯU Ý: Phải đảm bảo ThanhToan_Gui đã được biên dịch và có constructor này
            ThanhToan_GUI thanhToanPanel = new ThanhToan_GUI(gioHangXacNhan, bangGia, tongTienHoaDon);
            thanhToanFrame.setContentPane(thanhToanPanel);
            
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Lỗi khi khởi tạo màn hình Thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             parentFrame.setVisible(true); // Nếu lỗi, hiện lại cửa sổ cha ngay lập tức
             return;
        }

        thanhToanFrame.setVisible(true);
    }
    // =========================================================================
    
    class NutRenderer extends JButton implements TableCellRenderer {
        public NutRenderer(String text, Color color) {
            setText(text);
            setOpaque(true);
            setBackground(color);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class NutEditor extends DefaultCellEditor {
        private JButton btn;
        private ActionListener listener;

        public NutEditor(JCheckBox checkBox, String text, Color color, java.util.function.Consumer<Integer> onClick) {
            super(checkBox);
            btn = new JButton(text);
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            listener = e -> onClick.accept(tblMonDangGoi.getSelectedRow());
            btn.addActionListener(listener);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return btn;
        }

        public Object getCellEditorValue() {
            return null;
        }
    }

    public static void main(String[] args) throws SQLException {
        // ⚠️ Mở kết nối trước khi khởi tạo GUI
        ConnectDB.getConnection(); 

        JFrame f = new JFrame("Gọi món");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1200, 720);
        f.setLocationRelativeTo(null);
        f.setContentPane(new GoiMon_GUI());
        f.setVisible(true);
        
        // ⚠️ Đóng kết nối khi ứng dụng thoát
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConnectDB.disconnect();
            }
        });
    }
}