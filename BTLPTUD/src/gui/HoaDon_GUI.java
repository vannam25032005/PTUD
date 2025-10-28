package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.HoaDon_DAO;
import entity.HoaDon;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDon_GUI extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtTimMaHD;
    private JSpinner chonNgayTu, chonNgayDen;
    private HoaDon_DAO hoaDon_DAO = new HoaDon_DAO();

    private List<HoaDon> danhSachGoc = new ArrayList<>();

    public HoaDon_GUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 218, 170));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Quản lý hóa đơn", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        add(taoPanelBoLoc(), BorderLayout.SOUTH);

        JScrollPane tableScrollPane = createTable();
        add(tableScrollPane, BorderLayout.CENTER);

        loadDataToTable();
    }

    private JPanel taoPanelBoLoc() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 218, 170));

        JPanel panelTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTim.setBackground(new Color(255, 218, 170));
        panelTim.add(new JLabel("Mã HĐ:"));
        txtTimMaHD = new JTextField(10);
        panelTim.add(txtTimMaHD);
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> timTheoMaHD());
        panelTim.add(btnTim);

        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelLoc.setBackground(new Color(255, 218, 170));
        panelLoc.add(new JLabel("Từ ngày:"));
        chonNgayTu = new JSpinner(new SpinnerDateModel());
        chonNgayTu.setEditor(new JSpinner.DateEditor(chonNgayTu, "dd/MM/yyyy"));
        chonNgayTu.setPreferredSize(new Dimension(120, 30));
        panelLoc.add(chonNgayTu);

        panelLoc.add(new JLabel("Đến ngày:"));
        chonNgayDen = new JSpinner(new SpinnerDateModel());
        chonNgayDen.setEditor(new JSpinner.DateEditor(chonNgayDen, "dd/MM/yyyy"));
        chonNgayDen.setPreferredSize(new Dimension(120, 30));
        panelLoc.add(chonNgayDen);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.addActionListener(e -> locTheoKhoangNgay());
        panelLoc.add(btnLoc);

        JButton btnReset = new JButton("Làm mới");
        btnReset.addActionListener(e -> resetBang());
        panelLoc.add(btnReset);

        panel.add(panelTim, BorderLayout.WEST);
        panel.add(panelLoc, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createTable() {
        String[] columns = {"Mã hóa đơn", "Mã nhân viên", "Ngày lập", "Tổng tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(255, 200, 150));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 178, 102));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 15));

        return new JScrollPane(table);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        danhSachGoc = hoaDon_DAO.getAllHoaDon();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (HoaDon hd : danhSachGoc) {
            String ngayLapStr = (hd.getNgayLap() != null)
                    ? dtf.format(hd.getNgayLap())
                    : "";

            tableModel.addRow(new Object[]{
                    hd.getMaHoaDon(),
                    (hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : ""),
                    ngayLapStr,
                    String.format("%,.0f", hd.tinhTongTien())
            });
        }
    }

    private void timTheoMaHD() {
        String ma = txtTimMaHD.getText().trim().toLowerCase();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập mã hóa đơn cần tìm!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        lamMoiBang();
        boolean timThay = false;

        for (HoaDon hd : danhSachGoc) {
            if (hd.getMaHoaDon().toLowerCase().contains(ma)) {
                addRow(hd);
                timThay = true;
            }
        }

        if (!timThay) {
            JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy hóa đơn với mã: " + ma, 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            resetBang();
        }
    }


    private void locTheoKhoangNgay() {
        Date tuNgay = (Date) chonNgayTu.getValue();
        Date denNgay = (Date) chonNgayDen.getValue();

        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this,
                    "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!",
                    "Lỗi ngày tháng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // điều chỉnh để ĐẾN NGÀY = cuối ngày (23:59:59)
        denNgay = new Date(denNgay.getTime() + (24 * 60 * 60 * 1000) - 1);

        lamMoiBang();
        boolean found = false;

        for (HoaDon hd : danhSachGoc) {
            if (hd.getNgayLap() == null) continue;
            Date ngayLap = java.sql.Timestamp.valueOf(hd.getNgayLap());

            if (!ngayLap.before(tuNgay) && !ngayLap.after(denNgay)) {
                addRow(hd);
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy hóa đơn nào trong khoảng ngày đã chọn!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            resetBang();
        }
    }

    private void addRow(HoaDon hd) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        tableModel.addRow(new Object[]{
                hd.getMaHoaDon(),
                (hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : ""),
                (hd.getNgayLap() != null ? dtf.format(hd.getNgayLap()) : ""),
                String.format("%,.0f", hd.tinhTongTien())
        });
    }

    private void resetBang() {
        lamMoiBang();
        loadDataToTable();
    }

    private void lamMoiBang() {
        tableModel.setRowCount(0);
    }

    
}
