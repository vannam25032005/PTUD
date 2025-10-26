package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import dao.MonAn_DAO;
import entity.MonAn;
import entity.QuanLy;

public class QLMonAn_GUI extends JPanel implements ActionListener, MouseListener {

    private JTextField txtMaMon, txtTenMon, txtGia, txtTimKiem;
    private JComboBox<String> cboLoaiMon, cboLoaiTimKiem, cboSapXep;
    private JButton btnThemMon, btnChinhSua, btnTimKiem, btnChonFile;
    private JLabel lblKhongCoFile;
    private DefaultTableModel model;
    private JTable table;

    private MonAn_DAO monAnDAO;

    public QLMonAn_GUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 225, 190));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ===== TIÊU ĐỀ =====
        JLabel lblTieuDe = new JLabel("Quản lý món ăn");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        lblTieuDe.setForeground(Color.DARK_GRAY);
        add(lblTieuDe, BorderLayout.NORTH);

        // ===== FORM VÀ THANH TÌM =====
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(new Color(255, 225, 190));
        pnlTop.setBorder(new EmptyBorder(60, 0, 10, 0));

        JPanel pnlFormMain = new JPanel(new BorderLayout());
        pnlFormMain.setBackground(new Color(255, 225, 190));
        pnlFormMain.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Form bên trái
        JPanel pnlFormFields = new JPanel();
        pnlFormFields.setLayout(new BoxLayout(pnlFormFields, BoxLayout.Y_AXIS));
        pnlFormFields.setPreferredSize(new Dimension(320, 200));
        pnlFormFields.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Mã món
        JPanel pnlMaMon = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlMaMon.add(new JLabel("Mã món:"));
        txtMaMon = new JTextField(15);
        pnlMaMon.add(txtMaMon);
        pnlFormFields.add(pnlMaMon);

        // Tên món
        JPanel pnlTenMon = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlTenMon.add(new JLabel("Tên món:"));
        txtTenMon = new JTextField(15);
        pnlTenMon.add(txtTenMon);
        pnlFormFields.add(pnlTenMon);

        // Loại món
        JPanel pnlLoaiMon = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlLoaiMon.add(new JLabel("Loại món:"));
        String[] loaiMonOptions = {"Đồ ăn", "Đồ uống", "Ăn vặt"};
        cboLoaiMon = new JComboBox<>(loaiMonOptions);
        pnlLoaiMon.add(cboLoaiMon);
        pnlFormFields.add(pnlLoaiMon);

        // Giá
        JPanel pnlGia = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlGia.add(new JLabel("Giá:"));
        txtGia = new JTextField(15);
        pnlGia.add(txtGia);
        pnlFormFields.add(pnlGia);

        // Ảnh
        JPanel pnlAnh = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlAnh.add(new JLabel("Ảnh:"));
        btnChonFile = new JButton("Chọn file");
        lblKhongCoFile = new JLabel("No file chosen");
        lblKhongCoFile.setForeground(Color.GRAY);
        pnlAnh.add(btnChonFile);
        pnlAnh.add(lblKhongCoFile);
        pnlFormFields.add(pnlAnh);

        pnlFormMain.add(pnlFormFields, BorderLayout.CENTER);

        // Nút thêm / sửa
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnThemMon = new JButton("Thêm món");
        btnChinhSua = new JButton("Chỉnh sửa");
        pnlButtons.add(btnThemMon);
        pnlButtons.add(btnChinhSua);
        pnlFormMain.add(pnlButtons, BorderLayout.SOUTH);

        pnlTop.add(pnlFormMain, BorderLayout.NORTH);

        // Thanh tìm kiếm
        JPanel pnlSearch = new JPanel(new BorderLayout());
        pnlSearch.setBackground(new Color(255, 225, 190));

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTimKiem = new JTextField(15);
        btnTimKiem = new JButton("Tìm");
        pnlLeft.add(new JLabel("Tìm kiếm:"));
        pnlLeft.add(txtTimKiem);
        pnlLeft.add(btnTimKiem);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cboLoaiTimKiem = new JComboBox<>(new String[]{"Tất cả", "Đồ ăn", "Đồ uống", "Ăn vặt"});
        cboSapXep = new JComboBox<>(new String[]{"Giá tăng dần", "Giá giảm dần"});
        pnlRight.add(cboLoaiTimKiem);
        pnlRight.add(cboSapXep);

        pnlSearch.add(pnlLeft, BorderLayout.WEST);
        pnlSearch.add(pnlRight, BorderLayout.EAST);

        pnlTop.add(pnlSearch, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // ===== BẢNG =====
        String[] columns = {"Mã món", "Tên món", "Loại món", "Giá", "Hình ảnh"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ===== SỰ KIỆN =====
        btnThemMon.addActionListener(this);
        btnChinhSua.addActionListener(this);
        btnTimKiem.addActionListener(this);
        btnChonFile.addActionListener(this);
        cboLoaiTimKiem.addActionListener(this);
        cboSapXep.addActionListener(this);
        table.addMouseListener(this);

        // ===== DAO =====
        monAnDAO = new MonAn_DAO();
        loadDataToTable();
    }

    // Load dữ liệu
    private void loadDataToTable() {
        model.setRowCount(0);
        List<MonAn> ds = monAnDAO.docTuBang();
        for (MonAn m : ds) {
            model.addRow(new Object[]{
                    m.getMaMonAn(),
                    m.getTenMonAn(),
                    m.getLoaiMonAn(),
                    String.format("%.0f VNĐ", m.getGiaMonAn()),
                    m.getHinhAnh()
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // --- chọn file ảnh ---
        if (o.equals(btnChonFile)) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn ảnh món ăn");
            chooser.setFileFilter(new FileNameExtensionFilter("Ảnh (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                lblKhongCoFile.setText(file.getAbsolutePath());
                lblKhongCoFile.setForeground(Color.BLACK);
            }
            return;
        }

        // --- thêm món ---
        if (o.equals(btnThemMon)) {
            try {
                String ma = txtMaMon.getText().trim();
                String ten = txtTenMon.getText().trim();
                String loai = cboLoaiMon.getSelectedItem().toString();
                double gia = Double.parseDouble(txtGia.getText().trim());
                String hinhAnh = lblKhongCoFile.getText();

                MonAn mon = new MonAn(ma, ten, loai, gia, hinhAnh, null);

                if (monAnDAO.themMonAn(mon)) {
                    JOptionPane.showMessageDialog(this, "Thêm món thành công!");
                    loadDataToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thêm món (trùng mã?)");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
            return;
        }

        // --- chỉnh sửa ---
        if (o.equals(btnChinhSua)) {
            try {
                String ma = txtMaMon.getText().trim();
                String ten = txtTenMon.getText().trim();
                String loai = cboLoaiMon.getSelectedItem().toString();
                double gia = Double.parseDouble(txtGia.getText().trim());
                String hinhAnh = lblKhongCoFile.getText();

                MonAn mon = new MonAn(ma, ten, loai, gia, hinhAnh, null);

                if (monAnDAO.chinhSuaMonAn(mon)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadDataToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
            return;
        }

        // --- tìm kiếm ---
        if (o.equals(btnTimKiem)) {
            String keyword = txtTimKiem.getText().trim();
            List<MonAn> ds = monAnDAO.timTheoTen(keyword);
            model.setRowCount(0);
            for (MonAn m : ds) {
                model.addRow(new Object[]{
                        m.getMaMonAn(),
                        m.getTenMonAn(),
                        m.getLoaiMonAn(),
                        String.format("%.0f VNĐ", m.getGiaMonAn()),
                        m.getHinhAnh()
                });
            }
            return;
        }

        // --- lọc + sắp xếp ---
        if (o.equals(cboLoaiTimKiem) || o.equals(cboSapXep)) {
            String loai = cboLoaiTimKiem.getSelectedItem().toString();
            String thuTu = cboSapXep.getSelectedItem().toString().contains("tăng") ? "ASC" : "DESC";
            List<MonAn> ds = monAnDAO.locMonAn(loai, thuTu);

            model.setRowCount(0);
            for (MonAn m : ds) {
                model.addRow(new Object[]{
                        m.getMaMonAn(),
                        m.getTenMonAn(),
                        m.getLoaiMonAn(),
                        String.format("%.0f VNĐ", m.getGiaMonAn()),
                        m.getHinhAnh()
                });
            }
        }
    }

    // Click chuột vào bảng -> hiển thị dữ liệu lên form
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaMon.setText(model.getValueAt(row, 0).toString());
            txtTenMon.setText(model.getValueAt(row, 1).toString());
            cboLoaiMon.setSelectedItem(model.getValueAt(row, 2).toString());

            String giaStr = model.getValueAt(row, 3).toString().replace("VNĐ", "").trim();
            txtGia.setText(giaStr.replace(",", ""));

            String path = model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "";
            if (!path.isEmpty()) {
                lblKhongCoFile.setText(path);
                lblKhongCoFile.setForeground(Color.BLACK);
            } else {
                lblKhongCoFile.setText("No file chosen");
                lblKhongCoFile.setForeground(Color.GRAY);
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
