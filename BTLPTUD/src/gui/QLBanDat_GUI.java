package gui11;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import dao.QLBanDat_DAO;
import entity.Ban;
import entity.BanDat;
import entity.KhachHang;

public class QLBanDat_GUI extends JPanel implements ActionListener {
    private JPanel pnlDanhSachBan;
    private JLabel lblBanDangChon;
    private JButton btnDatBan, btnHuyBan, btnLamMoi;
    private JTextField txtHoTen, txtSoDienThoai, txtSoLuong, txtTienCoc, txtThoiGian;
    private JTextArea txtYeuCau;
    private JComboBox<String> cboTrangThai;

    private QLBanDat_DAO banDatDAO;
    private List<BanDat> danhSachBan;
    private BanDat banDangChon;

    public QLBanDat_GUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 230, 200));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ====== Tiêu đề ======
        JLabel lblTieuDe = new JLabel("QUẢN LÝ ĐẶT BÀN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // ====== DANH SÁCH BÀN ======
        pnlDanhSachBan = new JPanel(new GridLayout(3, 3, 20, 20));
        pnlDanhSachBan.setOpaque(false);
        add(pnlDanhSachBan, BorderLayout.CENTER);

        // ====== FORM BÊN PHẢI ======
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new GridLayout(8, 2, 10, 10));
        pnlRight.setBorder(new TitledBorder("Thông tin đặt bàn"));
        pnlRight.setOpaque(false);

        pnlRight.add(new JLabel("Tên khách hàng:"));
        txtHoTen = new JTextField();
        pnlRight.add(txtHoTen);

        pnlRight.add(new JLabel("Số điện thoại:"));
        txtSoDienThoai = new JTextField();
        pnlRight.add(txtSoDienThoai);

        pnlRight.add(new JLabel("Số lượng khách:"));
        txtSoLuong = new JTextField();
        pnlRight.add(txtSoLuong);

        pnlRight.add(new JLabel("Tiền cọc:"));
        txtTienCoc = new JTextField();
        pnlRight.add(txtTienCoc);

        pnlRight.add(new JLabel("Thời gian:"));
        txtThoiGian = new JTextField();
        pnlRight.add(txtThoiGian);

        pnlRight.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Trống", "Đã đặt", "Đang dùng"});
        pnlRight.add(cboTrangThai);

        pnlRight.add(new JLabel("Yêu cầu thêm:"));
        txtYeuCau = new JTextArea(2, 15);
        pnlRight.add(new JScrollPane(txtYeuCau));

        JPanel pnlButton = new JPanel(new GridLayout(1, 3, 10, 0));
        btnDatBan = new JButton("Đặt bàn");
        btnHuyBan = new JButton("Hủy bàn");
        btnLamMoi = new JButton("Làm mới");
        pnlButton.add(btnDatBan);
        pnlButton.add(btnHuyBan);
        pnlButton.add(btnLamMoi);

        JPanel pnlEast = new JPanel(new BorderLayout(10, 10));
        lblBanDangChon = new JLabel("Chưa chọn bàn nào", SwingConstants.CENTER);
        lblBanDangChon.setFont(new Font("Arial", Font.BOLD, 16));
        pnlEast.add(lblBanDangChon, BorderLayout.NORTH);
        pnlEast.add(pnlRight, BorderLayout.CENTER);
        pnlEast.add(pnlButton, BorderLayout.SOUTH);
        pnlEast.setOpaque(false);

        add(pnlEast, BorderLayout.EAST);

        // ===== DAO + DỮ LIỆU =====
        banDatDAO = new QLBanDat_DAO();
        loadDanhSachBan();

        // ===== SỰ KIỆN =====
        btnDatBan.addActionListener(this);
        btnHuyBan.addActionListener(this);
        btnLamMoi.addActionListener(this);
    }

    // ===========================================
    // LOAD DANH SÁCH BÀN TỪ DATABASE
    private void loadDanhSachBan() {
        pnlDanhSachBan.removeAll();
        danhSachBan = banDatDAO.getDanhSachBan();

        for (BanDat bd : danhSachBan) {
            JButton btnBan = new JButton(bd.getBan().getMaBan());
            btnBan.setFont(new Font("Arial", Font.BOLD, 14));
            btnBan.setOpaque(true);
            btnBan.setPreferredSize(new Dimension(100, 60));

            // Gắn màu theo trạng thái
            switch (bd.getTrangThai()) {
                case "Đã đặt" -> btnBan.setBackground(new Color(255, 153, 153));
                case "Đang dùng" -> btnBan.setBackground(new Color(255, 204, 102));
                default -> btnBan.setBackground(new Color(144, 238, 144));
            }

            // Gắn event chọn bàn
            btnBan.addActionListener(e -> {
                banDangChon = bd;
                lblBanDangChon.setText("Đang chọn: " + bd.getBan().getMaBan());
                fillFormFromBan(bd);
            });

            pnlDanhSachBan.add(btnBan);
        }

        pnlDanhSachBan.revalidate();
        pnlDanhSachBan.repaint();
    }

    // ===========================================
    // ĐỔ DỮ LIỆU BÀN LÊN FORM
    private void fillFormFromBan(BanDat bd) {
        if (bd.getKhachHang() != null) {
            txtHoTen.setText(bd.getKhachHang().getHoTenKH());
            txtSoDienThoai.setText(bd.getKhachHang().getSoDienThoai());
        } else {
            txtHoTen.setText("");
            txtSoDienThoai.setText("");
        }
        txtSoLuong.setText(String.valueOf(bd.getSoLuongKhach()));
        txtTienCoc.setText(String.valueOf(bd.getTienCoc()));
        cboTrangThai.setSelectedItem(bd.getTrangThai());
    }

    // ===========================================
    // LÀM MỚI FORM
    private void clearForm() {
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
        txtSoLuong.setText("");
        txtTienCoc.setText("");
        txtThoiGian.setText("");
        txtYeuCau.setText("");
        cboTrangThai.setSelectedIndex(0);
        lblBanDangChon.setText("Chưa chọn bàn nào");
        banDangChon = null;
    }

    // ===========================================
    // XỬ LÝ SỰ KIỆN (GIỐNG MonAn_GUI)
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // --- ĐẶT BÀN ---
        if (o.equals(btnDatBan)) {
            if (banDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn!");
                return;
            }

            try {
                String hoTen = txtHoTen.getText().trim();
                String sdt = txtSoDienThoai.getText().trim();
                int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
                double tienCoc = Double.parseDouble(txtTienCoc.getText().trim());
                String thoiGian = txtThoiGian.getText().trim();

                KhachHang kh = new KhachHang(null, hoTen, sdt);
                BanDat bd = new BanDat(null, kh, banDangChon.getBan(), soLuong, tienCoc, "Đã đặt");

                if (banDatDAO.datBan(bd, thoiGian)) {
                    JOptionPane.showMessageDialog(this, "Đặt bàn thành công!");
                    loadDanhSachBan();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Đặt bàn thất bại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }

        // --- HỦY BÀN ---
        if (o.equals(btnHuyBan)) {
            if (banDangChon == null || banDangChon.getMaDatBan() == null) {
                JOptionPane.showMessageDialog(this, "Bàn này chưa được đặt!");
                return;
            }

            if (banDatDAO.huyBan(banDangChon.getMaDatBan())) {
                JOptionPane.showMessageDialog(this, "Hủy bàn thành công!");
                loadDanhSachBan();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể hủy bàn!");
            }
        }

        // --- LÀM MỚI ---
        if (o.equals(btnLamMoi)) {
            clearForm();
            loadDanhSachBan();
        }
    }
}
