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
import java.util.stream.Collectors;

// Import các lớp cần thiết để làm việc với CSDL
import connectDB.ConnectDB; 
import dao.MonAn_DAO;
import dao.Ban_DAO;
import dao.CT_BanDat_DAO; 
import entity.MonAn;
import gui.GoiMon_GUI.NutEditor;
import gui.GoiMon_GUI.NutRenderer;
import entity.CTBanDat;
import entity.Ban;
import entity.BanDat; // Cần cho CTBanDat

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
    private Map<String, Integer> bangGia; 
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

    // --- BIẾN CSDL ---
    private MonAn_DAO monAn_DAO;
    private CT_BanDat_DAO ctBanDat_DAO; 
    private List<MonAn> danhSachMonAn; 
    private List<MonAn> danhSachMonAnHienThi;
    
    // ✅ THAY ĐỔI: Mã bàn không còn final, có thể thay đổi
    private String maBanHienTai;
    private Ban ban;
    
    // Khai báo cột
    private static final String[] COT_DANG_GOI = {"STT", "Tên món", "SL", "Giá", "Thao tác"};
    private static final String[] COT_DA_GOI = {"STT", "Tên món", "SL", "Giá"};


    // ✅ THAY ĐỔI: Constructor nhận mã bàn
    public GoiMon_GUI(Ban ban) throws SQLException {
        this.ban = ban; // Gán mã bàn từ tham số
        maBanHienTai = ban.getMaBan();
        dinhDangTien = NumberFormat.getInstance(new Locale("vi", "VN"));

        gioHang = new LinkedHashMap<>();
        bangGia = new HashMap<>();
        gioHangXacNhan = new LinkedHashMap<>();

        // 💡 1. KHỞI TẠO MODEL VÀ LABEL (Sửa lỗi NullPointer)
        modelMonDangGoi = new DefaultTableModel(COT_DANG_GOI, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        modelMonDaGoi = new DefaultTableModel(COT_DA_GOI, 0) {
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        
        lblTongTienDangGoi = new JLabel(dinhDangTien.format(0) + " VND");
        lblTieuDeDangGoi = new JLabel("Món đang gọi (0)");
        lblTongTien = new JLabel(dinhDangTien.format(0) + " VND");
        lblTieuDeDaGoi = new JLabel("Món đã gọi (0)"); 


        // 2. KHỞI TẠO DAO VÀ KẾT NỐI
        monAn_DAO = new MonAn_DAO();
        ctBanDat_DAO = new CT_BanDat_DAO(); 
        ConnectDB.getConnection(); 
        
        // 3. TẢI DỮ LIỆU
        taiDuLieuMonAn();
        khoiTaoGia(); 
        taiDuLieuDaGoi(); // Gọi hàm tải dữ liệu sau khi các model/label đã được khởi tạo

        // 4. THIẾT LẬP GUI
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
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(GoiMon_GUI.this);
                if (parent != null) {
                    ConnectDB.disconnect(); 
                    parent.dispose();
                }
            }
        });
        
        tieuDe.add(lblBack, BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Gọi món - Bàn: " + maBanHienTai, SwingConstants.CENTER);
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

// -------------------------------------------------------------------------
// --- PHƯƠNG THỨC MỚI: CẬP NHẬT MÃ BÀN ------------------------------------
// -------------------------------------------------------------------------

    /**
     * ✅ THÊM MỚI: Cập nhật mã bàn hiện tại và tải lại dữ liệu món đã gọi
     */
    public void setMaBanHienTai(String maBan) {
        this.maBanHienTai = maBan;
        
        // Xóa dữ liệu cũ
        gioHangXacNhan.clear();
        gioHang.clear();
        
        // Tải lại dữ liệu món đã gọi cho bàn mới
        taiDuLieuDaGoi();
        
        // Cập nhật giao diện
        capNhatBangGio();
        txtGhiChu.setText("");
        
        // Cập nhật tiêu đề
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComps = panel.getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getText().startsWith("Gọi món - Bàn:")) {
                            label.setText("Gọi món - Bàn: " + maBanHienTai);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * ✅ THÊM MỚI: Lấy mã bàn hiện tại
     */
    public String getMaBanHienTai() {
        return maBanHienTai;
    }

// -------------------------------------------------------------------------
// --- LOGIC TẢI DỮ LIỆU CSDL VÀ HIỂN THỊ ----------------------------------
// -------------------------------------------------------------------------

    private void taiDuLieuMonAn() {
        try {
            danhSachMonAn = monAn_DAO.layTatCaMonAn();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách món ăn từ CSDL: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            danhSachMonAn = new ArrayList<>(); 
        }
        danhSachMonAnHienThi = new ArrayList<>(danhSachMonAn);
    }

    private void khoiTaoGia() {
        bangGia.clear();
        if (danhSachMonAn != null) {
            for (MonAn mon : danhSachMonAn) {
                bangGia.put(mon.getTenMonAn(), (int)mon.getGiaMonAn());
            }
        }
    }
    
    /**
     * TẢI MÓN ĐÃ GỌI TỪ CSDL VÀ CẬP NHẬT BẢNG ĐÃ GỌI
     * ✅ SỬ DỤNG: maBanHienTai thay vì MA_DAT_BAN_HIEN_TAI
     */
    private void taiDuLieuDaGoi() {
        // Lấy Map<Tên món, Số lượng> từ CSDL
        Map<String, Integer> data = ctBanDat_DAO.layCTBan(maBanHienTai);
        
        gioHangXacNhan.clear();
        if (data != null) {
            gioHangXacNhan.putAll(data);
        }
        
        // Cập nhật bảng Món đã gọi
        capNhatBangDaGoi();
    }

// -------------------------------------------------------------------------
// --- LOGIC LỌC VÀ TÌM KIẾM ----------------------------------------------
// -------------------------------------------------------------------------

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

// -------------------------------------------------------------------------
// --- LOGIC GIỎ HÀNG (TRONG BỘ NHỚ) ---------------------------------------
// -------------------------------------------------------------------------

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

    /**
     * LƯU DỮ LIỆU TỪ GIỎ HÀNG VÀO CSDL (CT_BANDAT)
     * ✅ SỬ DỤNG: maBanHienTai thay vì MA_DAT_BAN_HIEN_TAI
     */
    private void xacNhanDon() {
        if (gioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng, không có gì để xác nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // LẤY MAP MÓN ĂN GỐC TỪ CSDL ĐỂ TÌM MÃ MONAN
            Map<String, MonAn> mapTenToMon = new HashMap<>();
            for(MonAn mon : danhSachMonAn) {
                mapTenToMon.put(mon.getTenMonAn(), mon);
            }
            
            // 1. CỘNG DỒN DỮ LIỆU MỚI VÀO gioHangXacNhan TRONG BỘ NHỚ
            for (Map.Entry<String, Integer> entry : gioHang.entrySet()) {
                gioHangXacNhan.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }

            // 2. LƯU TOÀN BỘ gioHangXacNhan VÀO CSDL (Xóa cũ, chèn mới)
            ctBanDat_DAO.xoaTatCaCTBan(maBanHienTai); // ✅ Sử dụng maBanHienTai
            
            // Lấy thực thể BanDat (chỉ cần mã)
            Ban banDatGoc = new Ban(maBanHienTai); // ✅ Sử dụng maBanHienTai
            
            for (Map.Entry<String, Integer> entry : gioHangXacNhan.entrySet()) {
                MonAn mon = mapTenToMon.get(entry.getKey());
                if (mon != null) {
                    // Tạo CTBanDat với các Entity đầy đủ
                    CTBanDat ct = new CTBanDat(banDatGoc, mon, entry.getValue());
                    ctBanDat_DAO.themCTBan(ct);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu chi tiết đơn hàng vào CSDL: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        // 3. LÀM SẠCH VÀ CẬP NHẬT GUI
        gioHang.clear();
        capNhatBangGio(); // Cập nhật giỏ hàng đang gọi (rỗng)
        
        capNhatBangDaGoi(); // Cập nhật bảng món đã gọi (dùng gioHangXacNhan)
        txtGhiChu.setText("");

        JOptionPane.showMessageDialog(this, "Đã xác nhận đơn hàng! Các món đã được lưu vào CSDL.");
    }
    
// -------------------------------------------------------------------------
// --- PHƯƠNG THỨC XỬ LÝ SAU THANH TOÁN ------------------------------------
// -------------------------------------------------------------------------

    /**
     * Làm mới trạng thái GUI sau khi hóa đơn đã được thanh toán xong (bao gồm xóa CSDL)
     * ✅ SỬ DỤNG: maBanHienTai thay vì MA_DAT_BAN_HIEN_TAI
     */
 // Trong GoiMontest_GUI.java

    public void lamMoiSauThanhToan() {
        try {
            // 1. XÓA DỮ LIỆU GỌI MÓN (CT_BanDat) CỦA BÀN NÀY KHỎI CSDL
            ctBanDat_DAO.xoaTatCaCTBan(maBanHienTai); // Sử dụng mã bàn hiện tại

            // 2. CẬP NHẬT TRẠNG THÁI BÀN THÀNH "Trống" TRONG CSDL
            Ban_DAO banDAO_temp = new Ban_DAO(); // Khởi tạo DAO Bàn (hoặc inject nếu có)
            if (banDAO_temp.updateTrangThaiBan(maBanHienTai, "Trống")) {
//                System.out.println(">>> [GoiMon_GUI] Đã cập nhật bàn '" + maBanHienTai + "' thành Trống sau thanh toán.");
            } else {
//                System.err.println(">>> [GoiMon_GUI] Lỗi: Không cập nhật được trạng thái bàn '" + maBanHienTai + "' thành Trống.");
                // Cân nhắc hiển thị lỗi cho người dùng nếu cần thiết
                 JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái bàn về Trống.", "Lỗi CSDL", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            // Bắt lỗi chung cho cả xóa CT_BanDat và cập nhật BAN
            JOptionPane.showMessageDialog(this, "Lỗi CSDL khi làm mới sau thanh toán: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // In lỗi ra console để debug
            // Dù lỗi CSDL, vẫn tiếp tục làm mới GUI
        }

        // 3. LÀM MỚI CÁC THÀNH PHẦN GUI
        gioHangXacNhan.clear(); // Xóa danh sách món đã xác nhận trong bộ nhớ
        tongTienHoaDon = 0;
        modelMonDaGoi.setRowCount(0); // Xóa bảng món đã gọi trên GUI
        lblTongTien.setText(dinhDangTien.format(tongTienHoaDon) + " VND"); // Cập nhật tổng tiền hóa đơn
        lblTieuDeDaGoi.setText("Món đã gọi (0)"); // Cập nhật tiêu đề bảng đã gọi

        gioHang.clear(); // Xóa giỏ hàng tạm thời
        capNhatBangGio(); // Cập nhật bảng món đang gọi (thành rỗng)
        txtGhiChu.setText(""); // Xóa ghi chú

        // 4. THÔNG BÁO CHO NGƯỜI DÙNG
        JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán và bàn [" + maBanHienTai + "] đã được dọn!", "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Mở giao diện ThanhToan_Gui và truyền dữ liệu hóa đơn.
     */
    private void thanhToan() {
    	if (gioHangXacNhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có món nào được xác nhận để thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        if (parentFrame == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha để mở màn hình thanh toán.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        parentFrame.setVisible(false);
        GoiMon_GUI currentGui = this;

        JFrame thanhToanFrame = new JFrame("Thanh toán");
        thanhToanFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        thanhToanFrame.setSize(1000, 750);
        thanhToanFrame.setLocationRelativeTo(null);
        
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
            // Giả định ThanhToan_Gui có constructor này
            ThanhToan_Gui thanhToanPanel = new ThanhToan_Gui(gioHangXacNhan, bangGia, tongTienHoaDon,ban);
            thanhToanFrame.setContentPane(thanhToanPanel);
            
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Lỗi khi khởi tạo màn hình Thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             parentFrame.setVisible(true); 
             return;
        }

        thanhToanFrame.setVisible(true);
    }
    
// -------------------------------------------------------------------------
// --- KHU VỰC THIẾT KẾ GUI VÀ HIỂN THỊ ------------------------------------
// -------------------------------------------------------------------------
    
    /**
     * Cập nhật hiển thị bảng Món đã gọi và Tổng tiền hóa đơn.
     */
    private void capNhatBangDaGoi() {
        modelMonDaGoi.setRowCount(0);
        int stt = 1;
        tongTienHoaDon = 0;
        
        for (Map.Entry<String, Integer> e : gioHangXacNhan.entrySet()) {
            String ten = e.getKey();
            int sl = e.getValue();
            if (bangGia.containsKey(ten)) {
                int gia = bangGia.get(ten);
                long thanhTien = (long)gia * sl;
                
                modelMonDaGoi.addRow(new Object[]{stt++, ten, sl, dinhDangTien.format(thanhTien) + " VND"});
                tongTienHoaDon += thanhTien;
            }
        }
        lblTieuDeDaGoi.setText("Món đã gọi (" + gioHangXacNhan.size() + ")");
        lblTongTien.setText(dinhDangTien.format(tongTienHoaDon) + " VND");
    }
    
    private JPanel taoKhuVucMenu() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JPanel pnlTop = new JPanel(new BorderLayout(8, 0));
        pnlTop.setBackground(Color.WHITE);

        JPanel pnlLoai = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlLoai.setBackground(Color.WHITE);
        JLabel lblLoai = new JLabel("Loại món");
        lblLoai.setFont(new Font("Arial", Font.BOLD, 13));
        
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
        pnlDanhSachMon.removeAll(); 
        pnlDanhSachMon.setLayout(new GridLayout(0, 3, 10, 12)); 
        
        if (ds != null && !ds.isEmpty()) {
            for (MonAn mon : ds) {
                themMon(mon.getTenMonAn(), (int)mon.getGiaMonAn(), mon.getHinhAnh());
            }
        } else {
            pnlDanhSachMon.setLayout(new BorderLayout());
            pnlDanhSachMon.add(new JLabel("Không tìm thấy món ăn nào.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        pnlDanhSachMon.revalidate();
        pnlDanhSachMon.repaint();
    }
    
    
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
        
        String internalImagePath = "/image/" + imagePath;
        
        try {
            URL imageUrl = getClass().getResource(internalImagePath);
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image originalImage = originalIcon.getImage();
                Image scaledImage = originalImage.getScaledInstance(90, 80, Image.SCALE_SMOOTH); 
                lblAnh.setIcon(new ImageIcon(scaledImage));
                lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
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
        panel.add(lblTieuDeDangGoi);
        panel.add(Box.createVerticalStrut(8));

        // SỬ DỤNG MODEL ĐÃ KHỞI TẠO
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
        pnlSubTotal.add(lblTxtSubTotal, BorderLayout.WEST);
        pnlSubTotal.add(lblTongTienDangGoi, BorderLayout.EAST);
        
        lblTongTienDangGoi.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTienDangGoi.setForeground(new Color(255, 140, 0));
        
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
        panel.add(lblTieuDeDaGoi);
        panel.add(Box.createVerticalStrut(8));

        // 💡 SỬ DỤNG MODEL ĐÃ KHỞI TẠO
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
        
        pnlTong.add(lblTxtTong, BorderLayout.WEST);
        pnlTong.add(lblTongTien, BorderLayout.EAST);
        
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(220,53,69));
        
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
    
// -------------------------------------------------------------------------
// --- CÁC LỚP RENDERER VÀ MAIN --------------------------------------------
// -------------------------------------------------------------------------

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

//    public static void main(String[] args) throws SQLException {
//        // ⚠️ Mở kết nối trước khi khởi tạo GUI
//        ConnectDB.getConnection(); 
//
//        JFrame f = new JFrame("Gọi món");
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.setSize(1200, 720);
//        f.setLocationRelativeTo(null);
//        f.setContentPane(new GoiMontest_GUI("B001"));
//        f.setVisible(true);
//        
//        // ⚠️ Đóng kết nối khi ứng dụng thoát
//        f.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                ConnectDB.disconnect();
//            }
//        });
//    }
}