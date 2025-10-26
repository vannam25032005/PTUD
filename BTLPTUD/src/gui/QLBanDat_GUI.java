package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class QLBanDat_GUI extends JPanel {

    // Khai báo biến giao diện
    private JPanel pnlDanhSachBan, pnlFormThongTin;
    private JLabel lblTieuDe;
    private JTextField txtSoDienThoai, txtHoTen, txtThoiGian, txtSoLuongNguoi, txtTienCoc;
    private JTextArea txtYeuCauThem;
    private JButton btnDatBan, btnChinhSua, btnGoiMon, btnHuyBan, btnXacNhan, btnXoaTrang;
    private JPanel pnlChinh;
	private JLabel lblBanDangChon;

    public QLBanDat_GUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 226, 197));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ===== TIÊU ĐỀ =====
        lblTieuDe = new JLabel("Quản lý bàn đặt");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        lblTieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTieuDe, BorderLayout.NORTH);

        // ===== PANEL CHÍNH =====
        pnlChinh = new JPanel(new BorderLayout());
        pnlChinh.setOpaque(false);

        // Tạo panel bên trái cho danh sách bàn và nút chức năng
        JPanel leftPanel = new JPanel(new BorderLayout());
        lblBanDangChon = new JLabel("Chưa chọn bàn nào");
        lblBanDangChon.setFont(new Font("Arial", Font.BOLD, 16));
        lblBanDangChon.setHorizontalAlignment(SwingConstants.LEFT);
        lblBanDangChon.setBorder(new EmptyBorder(10, 0, 10, 0)); // Padding trên-dưới cho label
        leftPanel.add(lblBanDangChon, BorderLayout.NORTH);
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(40, 50, 40, 0)); // Thêm padding cho bên trái

        pnlDanhSachBan = new JPanel(new GridLayout(3, 3, 30, 20));
        pnlDanhSachBan.setOpaque(false);

        for (int i = 1; i <= 9; i++) {
            JButton btnBan = new JButton("B" + String.format("%02d", i));
            btnBan.setPreferredSize(new Dimension(150, 0)); // Giảm kích thước bàn
            btnBan.setFont(new Font("Arial", Font.BOLD, 12)); // Giảm font size cho phù hợp
            btnBan.setBackground(i % 2 == 0 ? new Color(255, 142, 142) : new Color(142, 255, 163));
            btnBan.setBorder(new LineBorder(Color.BLACK, 2, true));
            pnlDanhSachBan.add(btnBan);
        }
        leftPanel.add(pnlDanhSachBan, BorderLayout.CENTER);

        // ===== 4 NÚT CHỨC NĂNG 2x2 Ở DƯỚI, CĂN GIỮA TRONG BÊN TRÁI =====
        JPanel pnlChucNang = new JPanel(new GridLayout(2, 2, 10, 5));
        pnlChucNang.setOpaque(false);
        pnlChucNang.setBorder(new EmptyBorder(10, 20, 10, 20)); // Căn giữa gọn hơn trong leftPanel

        btnDatBan = new JButton("Đặt bàn");
        btnChinhSua = new JButton("Chỉnh sửa");
        btnGoiMon = new JButton("Gọi món");
        btnHuyBan = new JButton("Hủy bàn");
        btnHuyBan.setForeground(Color.RED);

        pnlChucNang.add(btnDatBan);
        pnlChucNang.add(btnChinhSua);
        pnlChucNang.add(btnGoiMon);
        pnlChucNang.add(btnHuyBan);

        leftPanel.add(pnlChucNang, BorderLayout.SOUTH);

        // Thêm leftPanel vào pnlChinh ở WEST (bên trái)
        pnlChinh.add(leftPanel, BorderLayout.WEST);

        // ===== FORM THÔNG TIN (BÊN PHẢI) =====
        pnlFormThongTin = new JPanel();
        pnlFormThongTin.setOpaque(false);
        pnlFormThongTin.setLayout(new BoxLayout(pnlFormThongTin, BoxLayout.Y_AXIS));
        pnlFormThongTin.setBorder(new EmptyBorder(60, 30, 10, 0));

        Font lblFont = new Font("Arial", Font.BOLD, 14);
        Dimension sizeLabel = new Dimension(120, 30);
        Dimension sizeTextField = new Dimension(220, 30);

        // ===== DÒNG SỐ ĐIỆN THOẠI =====
        JPanel pnlSDT = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlSDT.setOpaque(false);
        JLabel lblSDT = new JLabel("Số điện thoại:");
        lblSDT.setFont(lblFont);
        lblSDT.setPreferredSize(sizeLabel);
        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setPreferredSize(sizeTextField);
        pnlSDT.add(lblSDT);
        pnlSDT.add(txtSoDienThoai);
        pnlFormThongTin.add(pnlSDT);

        // ===== DÒNG HỌ TÊN =====
        JPanel pnlHoTen = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlHoTen.setOpaque(false);
        JLabel lblHoTen = new JLabel("Họ và tên:");
        lblHoTen.setFont(lblFont);
        lblHoTen.setPreferredSize(sizeLabel);
        txtHoTen = new JTextField();
        txtHoTen.setPreferredSize(sizeTextField);
        pnlHoTen.add(lblHoTen);
        pnlHoTen.add(txtHoTen);
        pnlFormThongTin.add(pnlHoTen);

        // ===== DÒNG THỜI GIAN =====
        JPanel pnlTG = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlTG.setOpaque(false);
        JLabel lblThoiGian = new JLabel("Thời gian:");
        lblThoiGian.setFont(lblFont);
        lblThoiGian.setPreferredSize(sizeLabel);
        txtThoiGian = new JTextField();
        txtThoiGian.setPreferredSize(new Dimension(220, 30));
        JCheckBox chkDatLien = new JCheckBox("Đặt liền");
        chkDatLien.setOpaque(false);
        pnlTG.add(lblThoiGian);
        pnlTG.add(txtThoiGian);
        pnlTG.add(chkDatLien);
        pnlFormThongTin.add(pnlTG);

        // ===== DÒNG SỐ LƯỢNG NGƯỜI =====
        JPanel pnlSL = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlSL.setOpaque(false);
        JLabel lblSL = new JLabel("Số lượng người:");
        lblSL.setFont(lblFont);
        lblSL.setPreferredSize(sizeLabel);
        txtSoLuongNguoi = new JTextField();
        txtSoLuongNguoi.setPreferredSize(sizeTextField);
        pnlSL.add(lblSL);
        pnlSL.add(txtSoLuongNguoi);
        pnlFormThongTin.add(pnlSL);

        // ===== TRẠNG THÁI =====
        JPanel pnlTrangThai = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlTrangThai.setOpaque(false);
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(lblFont);
        lblTrangThai.setPreferredSize(sizeLabel);
        String[] dsTrangThai = {"Chưa đến", "Đang dùng", "Đã hủy"};
        JComboBox<String> cboTrangThai = new JComboBox<>(dsTrangThai);
        cboTrangThai.setPreferredSize(sizeTextField);
        pnlTrangThai.add(lblTrangThai);
        pnlTrangThai.add(cboTrangThai);
        pnlFormThongTin.add(pnlTrangThai);

        // ===== TIỀN CỌC =====
        JPanel pnlTienCoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlTienCoc.setOpaque(false);
        JLabel lblTienCoc = new JLabel("Tiền cọc:");
        lblTienCoc.setFont(lblFont);
        lblTienCoc.setPreferredSize(sizeLabel);
        txtTienCoc = new JTextField();
        txtTienCoc.setPreferredSize(sizeTextField);
        pnlTienCoc.add(lblTienCoc);
        pnlTienCoc.add(txtTienCoc);
        pnlFormThongTin.add(pnlTienCoc);

        // ===== YÊU CẦU THÊM =====
        JPanel pnlYCLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlYCLabel.setOpaque(false);
        JLabel lblYC = new JLabel("Yêu cầu thêm của khách (nếu có):");
        lblYC.setFont(lblFont);
        JPanel spacerYCLabel = new JPanel();
        spacerYCLabel.setPreferredSize(sizeTextField);
        spacerYCLabel.setOpaque(false);
        pnlYCLabel.add(lblYC);
        pnlYCLabel.add(spacerYCLabel);
        pnlFormThongTin.add(pnlYCLabel);

        JPanel pnlYCTextArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlYCTextArea.setOpaque(false);
        JPanel spacerLabelYCText = new JPanel(); 
        spacerLabelYCText.setOpaque(false);
        txtYeuCauThem = new JTextArea(4, 32);
        txtYeuCauThem.setBorder(new LineBorder(Color.GRAY, 1, true));
        pnlYCTextArea.add(spacerLabelYCText);
        pnlYCTextArea.add(txtYeuCauThem);
        pnlFormThongTin.add(pnlYCTextArea);

        pnlFormThongTin.add(Box.createVerticalStrut(10));

        // ===== Nút xác nhận =====
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlNut.setOpaque(false);
        btnXacNhan = new JButton("Xác nhận");
        btnXoaTrang = new JButton("Xóa trắng");
        pnlNut.add(btnXacNhan);
        pnlNut.add(btnXoaTrang);

        pnlFormThongTin.add(pnlNut);

        // Thêm form vào pnlChinh ở EAST (bên phải)
        pnlChinh.add(pnlFormThongTin, BorderLayout.EAST);

        // Thêm pnlChinh vào panel chính
        add(pnlChinh, BorderLayout.CENTER);
    }
}