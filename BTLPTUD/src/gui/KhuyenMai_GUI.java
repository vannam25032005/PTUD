package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId; 
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.toedter.calendar.JDateChooser; 

import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import connectDB.ConnectDB; 

public class KhuyenMai_GUI extends JPanel {
    
    // ==================================================================================
    // 1. KHAI BÁO CÁC THÀNH PHẦN
    // ==================================================================================
    private static final long serialVersionUID = 1L;
    
    // Fields
    private JTextField txtMaKM, txtTenKM, txtMoTa, txtPhanTramGiam;
    private JDateChooser dcNgayBD, dcNgayKT; 
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    
    // Buttons và ComboBox (Đã sửa lỗi khai báo cho btnXoaBoLoc)
    private JButton btnThem, btnSua, btnXoa, btnTim, btnXoaBoLoc; 
    private JComboBox<String> cboTrangThai; 
    
    // DAO và Hằng số
    private KhuyenMai_DAO khuyenMai_DAO;
    private static final DateTimeFormatter DATE_FORMATTER_GUI = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int TEXTFIELD_HEIGHT = 40; 

    // ==================================================================================
    // 2. KHỞI TẠO VÀ CẤU TRÚC GIAO DIỆN
    // ==================================================================================

    public KhuyenMai_GUI() {
        // Khởi tạo kết nối CSDL và DAO
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        khuyenMai_DAO = new KhuyenMai_DAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 239, 213)); 
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. TIÊU ĐỀ
        JLabel lblTitle = new JLabel("Quản Lý Khuyến Mãi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Panel chứa Header, Search và Form
        JPanel pnlTopContainer = new JPanel();
        pnlTopContainer.setLayout(new BoxLayout(pnlTopContainer, BoxLayout.Y_AXIS));
        pnlTopContainer.setBackground(getBackground());
        
        pnlTopContainer.add(lblTitle);
        pnlTopContainer.add(Box.createVerticalStrut(10)); 

        // 2. THANH TÌM KIẾM VÀ BỘ LỌC
        JPanel pnlSearchAndFilter = createSearchAndFilterPanel();
        pnlTopContainer.add(pnlSearchAndFilter);
        pnlTopContainer.add(Box.createVerticalStrut(15));
        
        // 3. FORM NHẬP DỮ LIỆU VÀ NÚT
        JPanel pnlFormAndButtons = createInputFormPanel();
        pnlTopContainer.add(pnlFormAndButtons);

        add(pnlTopContainer, BorderLayout.NORTH);

        // 4. BẢNG DANH SÁCH
        String[] cols = {"Mã", "Tên khuyến mãi", "Mô tả", "Giá trị", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); 
        
        add(scroll, BorderLayout.CENTER);
        
        // Load dữ liệu và thêm sự kiện
        loadDataToTable();
        addTableClickListener();
        addActionListener(); 
        
        // Thiết lập trạng thái ban đầu cho nút Sửa/Xóa
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
    }
    
    // ==================================================================================
    // PHƯƠNG THỨC TẠO GIAO DIỆN CON
    // ==================================================================================
    
    private JPanel createSearchAndFilterPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        pnl.setBackground(getBackground()); 
        pnl.setMaximumSize(new Dimension(1000, 50));
        
        // Text field tìm kiếm
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(250, 35)); 
        styleTextField(txtTimKiem);
        
        // Nút tìm kiếm 
        btnTim = new JButton("Tìm");
        btnTim.setPreferredSize(new Dimension(100, 35));
        btnTim.setBackground(Color.WHITE);
        
        // ComboBox Trạng thái
        cboTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc"});
        cboTrangThai.setPreferredSize(new Dimension(150, 35));
        
        // Nút Xóa bộ lọc
        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        btnXoaBoLoc.setPreferredSize(new Dimension(100, 35));
        btnXoaBoLoc.setBackground(Color.WHITE);

        pnl.add(txtTimKiem);
        pnl.add(btnTim);
        pnl.add(cboTrangThai);
        pnl.add(btnXoaBoLoc);
        
        return pnl;
    }
    
    private JPanel createInputFormPanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE); 
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        pnl.setMaximumSize(new Dimension(1000, 450));
        
        // Panel chứa các trường nhập liệu (GridBagLayout)
        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5; // Chia đều 2 cột

        // Khởi tạo các fields
        txtMaKM = createSizedTextField(TEXTFIELD_HEIGHT);
        txtTenKM = createSizedTextField(TEXTFIELD_HEIGHT);
        txtMoTa = createSizedTextField(TEXTFIELD_HEIGHT); 
        txtPhanTramGiam = createSizedTextField(TEXTFIELD_HEIGHT);
        dcNgayBD = createDateChooser(TEXTFIELD_HEIGHT);
        dcNgayKT = createDateChooser(TEXTFIELD_HEIGHT);

        // Hàng 1: Mã KM & Tên KM
        addField(pnlFields, gbc, "Mã khuyến mãi", txtMaKM, 0, 0);
        addField(pnlFields, gbc, "Tên khuyến mãi", txtTenKM, 1, 0);
        
        // Hàng 2: Mô tả & Phần trăm giảm
        addField(pnlFields, gbc, "Mô tả", txtMoTa, 0, 1);
        addField(pnlFields, gbc, "Phần trăm giảm", txtPhanTramGiam, 1, 1);

        // Hàng 3: Ngày bắt đầu (Trái) & Ngày kết thúc (Phải)
        addField(pnlFields, gbc, "Ngày bắt đầu", dcNgayBD, 0, 2);
        addField(pnlFields, gbc, "Ngày kết thúc", dcNgayKT, 1, 2);

        pnl.add(pnlFields, BorderLayout.CENTER);
        
        // Panel Nút chức năng 
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        pnlButtons.setBackground(Color.WHITE);

        btnThem = new JButton("Thêm khuyến mãi"); 
        btnXoa = new JButton("Xóa"); 
        btnSua = new JButton("Sửa"); 

        styleButton(btnThem, new Color(39, 174, 96), 180); 
        styleButton(btnXoa, new Color(231, 76, 60), 100); 
        styleButton(btnSua, new Color(243, 156, 18), 100); 

        pnlButtons.add(btnThem);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnSua);

        pnl.add(pnlButtons, BorderLayout.SOUTH);
        
        return pnl;
    }
    
    // ==================================================================================
    // PHƯƠNG THỨC CHUNG & HÀM HỖ TRỢ
    // ==================================================================================
    
    private JTextField createSizedTextField(int height) {
        JTextField textField = new JTextField(15);
        textField.setPreferredSize(new Dimension(200, height));
        return textField;
    }
    
    private JDateChooser createDateChooser(int height) {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy"); 
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.setPreferredSize(new Dimension(200, height));
        return dateChooser;
    }

    private void styleTextField(JTextField tf) {
        tf.setBorder(BorderFactory.createCompoundBorder(
            tf.getBorder(), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5))); 
    }
    
    private void addField(JPanel parent, GridBagConstraints gbc, String labelText, JComponent component, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        
        JPanel pnlField = new JPanel(new BorderLayout());
        pnlField.setBackground(parent.getBackground());
        pnlField.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(), 
            labelText, 
            TitledBorder.LEADING, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.PLAIN, 12), 
            Color.BLACK)); 
            
        pnlField.add(component, BorderLayout.CENTER);
        parent.add(pnlField, gbc);
    }

    private void styleButton(JButton btn, Color color, int width) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(width, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 
    }
    
    // ==================================================================================
    // 3. LOGIC DỮ LIỆU VÀ SỰ KIỆN (LOAD/LỌC/CLICK)
    // ==================================================================================
    
    /**
     * Tải dữ liệu vào bảng. Nếu danh sách truyền vào là null, tải toàn bộ từ DB.
     * @param danhSachKM Danh sách khuyến mãi cần hiển thị
     */
    public void loadDataToTable(ArrayList<KhuyenMai> danhSachKM) {
        model.setRowCount(0); 
        
        if (danhSachKM == null) {
            danhSachKM = khuyenMai_DAO.getAllKhuyenMai();
        }
        
        for (KhuyenMai km : danhSachKM) {
            String trangThai = getTrangThai(km.getNgayBatDau(), km.getNgayKetThuc());
            
            String ngayBDStr = km.getNgayBatDau().format(DATE_FORMATTER_GUI);
            String ngayKTStr = km.getNgayKetThuc().format(DATE_FORMATTER_GUI);
            
            Object[] rowData = {
                km.getMaKM(),
                km.getTenKM(),
                (km.getMoTa() != null) ? km.getMoTa() : "---", 
                String.format("%.0f%%", km.getPhanTramGiam() * 100), 
                ngayBDStr,
                ngayKTStr,
                trangThai
            };
            model.addRow(rowData);
        }
    }
    
    // Phương thức gọi tải toàn bộ dữ liệu
    public void loadDataToTable() {
        loadDataToTable(null);
    }
    
    /**
     * Xác định trạng thái của khuyến mãi dựa trên ngày.
     */
    private String getTrangThai(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        LocalDate homNay = LocalDate.now();
        
        if (homNay.isBefore(ngayBatDau)) {
            return "Sắp diễn ra";
        } else if (homNay.isAfter(ngayKetThuc)) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }
    
    private void addTableClickListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                hienThiChiTietKhuyenMaiLenForm();
            }
        });
    }

    private void hienThiChiTietKhuyenMaiLenForm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtMaKM.setText(model.getValueAt(selectedRow, 0).toString());
            txtTenKM.setText(model.getValueAt(selectedRow, 1).toString());
            txtMoTa.setText(model.getValueAt(selectedRow, 2).toString()); 
            
            String giaTriStr = model.getValueAt(selectedRow, 3).toString().replace("%", "");
            double phanTram = Double.parseDouble(giaTriStr) / 100.0;
            txtPhanTramGiam.setText(String.valueOf(phanTram)); 
            
            // XỬ LÝ NGÀY CHO JDateChooser
            String ngayBDStr = model.getValueAt(selectedRow, 4).toString();
            String ngayKTStr = model.getValueAt(selectedRow, 5).toString();

            LocalDate ngayBD = LocalDate.parse(ngayBDStr, DATE_FORMATTER_GUI);
            LocalDate ngayKT = LocalDate.parse(ngayKTStr, DATE_FORMATTER_GUI);
            
            dcNgayBD.setDate(Date.from(ngayBD.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            dcNgayKT.setDate(Date.from(ngayKT.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            
            txtMaKM.setEditable(false);
            btnThem.setEnabled(false);
            btnSua.setEnabled(true);
            btnXoa.setEnabled(true);
        }
    }
    
    private void addActionListener() {
        btnThem.addActionListener(e -> themKhuyenMai());
        btnSua.addActionListener(e -> suaKhuyenMai());
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        btnTim.addActionListener(e -> timKhuyenMai());
        
        // SỬA LỖI: Gán action listener trực tiếp cho btnXoaBoLoc
        if (btnXoaBoLoc != null) {
            btnXoaBoLoc.addActionListener(e -> lamMoiFormVaBang());
        }
        
        // THÊM CHỨC NĂNG: Lắng nghe sự kiện thay đổi của ComboBox trạng thái
        if (cboTrangThai != null) {
            cboTrangThai.addActionListener(e -> locKhuyenMaiTheoTrangThai());
        }
    }
    
    /**
     * Chức năng: Xóa trắng toàn bộ form nhập liệu, trường tìm kiếm, 
     * reset bộ lọc và tải lại toàn bộ dữ liệu ra bảng.
     */
    private void lamMoiFormVaBang() {
        xoaTrangForm();
        
        // Xóa trường tìm kiếm và reset bộ lọc
        txtTimKiem.setText(""); 
        if (cboTrangThai != null) {
            cboTrangThai.setSelectedIndex(0); // Reset ComboBox về "Tất cả trạng thái"
        }
        
        loadDataToTable(); // Tải lại toàn bộ dữ liệu
    }

    /**
     * Hàm hỗ trợ xóa trắng form nhập liệu và reset trạng thái nút
     */
    private void xoaTrangForm() {
        txtMaKM.setText("");
        txtTenKM.setText("");
        txtMoTa.setText("");
        txtPhanTramGiam.setText("");
        
        dcNgayBD.setDate(null);
        dcNgayKT.setDate(null);
        
        txtMaKM.setEditable(true);
        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);

        table.clearSelection();
    }
    
    /**
     * Chức năng: Lọc dữ liệu trong bảng dựa trên trạng thái được chọn trong ComboBox.
     */
    private void locKhuyenMaiTheoTrangThai() {
        String selectedTrangThai = (String) cboTrangThai.getSelectedItem();
        
        if (selectedTrangThai == null || selectedTrangThai.equals("Tất cả trạng thái")) {
            loadDataToTable(); // Tải lại toàn bộ nếu chọn "Tất cả"
            return;
        }

        // 1. Lấy toàn bộ danh sách KM
        ArrayList<KhuyenMai> allKM = khuyenMai_DAO.getAllKhuyenMai();
        ArrayList<KhuyenMai> filteredList = new ArrayList<>();
        
        // 2. Lọc danh sách
        for (KhuyenMai km : allKM) {
            String currentTrangThai = getTrangThai(km.getNgayBatDau(), km.getNgayKetThuc());
            if (currentTrangThai.equals(selectedTrangThai)) {
                filteredList.add(km);
            }
        }
        
        // 3. Hiển thị danh sách đã lọc
        loadDataToTable(filteredList);
        
        // 4. Xóa trắng form nhập liệu để chuẩn bị cho thao tác mới
        xoaTrangForm(); 
    }

    // ==================================================================================
    // 4. LOGIC CHỨC NĂNG (CRUD)
    // ==================================================================================
    
    private KhuyenMai layDuLieuTuForm() throws Exception {
        String ma = txtMaKM.getText().trim();
        String ten = txtTenKM.getText().trim();
        String moTa = txtMoTa.getText().trim();
        double giaTri;
        LocalDate ngayBD, ngayKT;

        if (ma.isEmpty() || ten.isEmpty() || txtPhanTramGiam.getText().trim().isEmpty()) {
             throw new Exception("Vui lòng điền đầy đủ các trường bắt buộc (Mã, Tên, Giá trị).");
        }
        
        // LẤY VÀ KIỂM TRA NGÀY TỪ JDateChooser
        Date dateBD = dcNgayBD.getDate();
        Date dateKT = dcNgayKT.getDate();
        
        if (dateBD == null || dateKT == null) {
            throw new Exception("Vui lòng chọn Ngày bắt đầu và Ngày kết thúc.");
        }
        
        ngayBD = dateBD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        ngayKT = dateKT.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // KIỂM TRA GIÁ TRỊ GIẢM
        try {
             giaTri = Double.parseDouble(txtPhanTramGiam.getText().trim());
             if (giaTri < 0 || giaTri > 1) {
                 throw new Exception("Giá trị giảm phải nằm trong khoảng từ 0 đến 1 (tương đương 0% đến 100%).");
             }
        } catch (NumberFormatException e) {
             throw new Exception("Giá trị giảm không hợp lệ. Vui lòng nhập số thập phân (ví dụ: 0.1 cho 10%).");
        }
        
        // KIỂM TRA TÍNH HỢP LỆ CỦA KHOẢNG THỜI GIAN
        if (ngayBD.isAfter(ngayKT)) {
            throw new Exception("Ngày bắt đầu không thể sau Ngày kết thúc.");
        }
        
        // Dùng mã quản lý cứng QL001 (tùy thuộc vào thiết kế DB/Entity của bạn).
        String maQL = "QL001"; 

        return new KhuyenMai(ma, ten, moTa, giaTri, ngayBD, ngayKT);
    }

    private void themKhuyenMai() {
        try {
            KhuyenMai km = layDuLieuTuForm();
            if (khuyenMai_DAO.timKhuyenMaiTheoMa(km.getMaKM()) != null) {
                JOptionPane.showMessageDialog(this, "Mã khuyến mãi đã tồn tại!", "Lỗi Trùng Mã", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (khuyenMai_DAO.themKhuyenMai(km)) {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!");
                lamMoiFormVaBang();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại! Vui lòng kiểm tra kết nối DB.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void suaKhuyenMai() {
        try {
            KhuyenMai km = layDuLieuTuForm();
            if (khuyenMai_DAO.suaKhuyenMai(km)) {
                JOptionPane.showMessageDialog(this, "Sửa khuyến mãi thành công!");
                lamMoiFormVaBang();
            } else {
                JOptionPane.showMessageDialog(this, "Sửa khuyến mãi thất bại! Vui lòng kiểm tra mã.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaKhuyenMai() {
        String maKM = txtMaKM.getText().trim();
        if (maKM.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khuyến mãi: " + maKM + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (khuyenMai_DAO.xoaKhuyenMai(maKM)) {
                    JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!");
                    lamMoiFormVaBang();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thất bại! Có thể do ràng buộc dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi CSDL khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void timKhuyenMai() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadDataToTable();
            return;
        }

        ArrayList<KhuyenMai> danhSachKM = khuyenMai_DAO.timKhuyenMaiTheoTen(keyword);
        
        if (danhSachKM.isEmpty()) {
            KhuyenMai km = khuyenMai_DAO.timKhuyenMaiTheoMa(keyword);
            if (km != null) {
                danhSachKM.add(km);
            }
        }
        
        loadDataToTable(danhSachKM);
        
        if (danhSachKM.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Reset ComboBox trạng thái sau khi tìm kiếm
        cboTrangThai.setSelectedIndex(0); 
    }


    // ==================================================================================
    // 5. PHƯƠNG THỨC MAIN (TEST)
    // ==================================================================================
    public static void main(String[] args) {
        JFrame frame = new JFrame("Quản Lý Khuyến Mãi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.add(new KhuyenMai_GUI());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}