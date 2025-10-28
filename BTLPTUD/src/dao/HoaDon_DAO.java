package dao;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.CT_HoaDon;
import entity.NhanVien;
import entity.Ban;
import entity.KhuyenMai;
import entity.BanDat;
import entity.TheThanhVien;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    private final CTHoaDon_DAO ctHoaDonDAO = new CTHoaDon_DAO();
    private final KhuyenMai_DAO kmDAO = new KhuyenMai_DAO();
    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHD = new ArrayList<>();

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            String sql = "SELECT maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien FROM HOADON";
            PreparedStatement stmt = con.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String maHD = rs.getString("maHD");
                String maThe = rs.getString("maThe");
                String maNV = rs.getString("maNV");
                String maBan = rs.getString("maBan");
                String maDatBan = rs.getString("maDatBan");
                String maKM = rs.getString("maKM");

                Timestamp ts = rs.getTimestamp("ngayLap");
                LocalDateTime ngayLap = null;
                if (ts != null) {
                    ngayLap = ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
                
                double tongTien = rs.getDouble("tongTien");

                HoaDon hd = new HoaDon(maHD);
                hd.setNgayLap(ngayLap);

                // Gán khóa ngoại (chỉ gán mã — không cần lấy cả đối tượng)
                hd.setNhanVien(new NhanVien(maNV));
                hd.setTheThanhVien(maThe != null ? new TheThanhVien(maThe) : null);
                hd.setBan(new Ban(maBan));
                hd.setBanDat(maDatBan != null ? new BanDat(maDatBan) : null);
                hd.setKhuyenMai(maKM != null ? new KhuyenMai(maKM) : null);

                dsHD.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsHD;
    }


    private HoaDon taoHoaDonTuResult(ResultSet rs, Connection con) throws Exception {
        String maHD = rs.getString("maHD");

        HoaDon hd = new HoaDon(maHD);
        hd.setNgayLap(rs.getTimestamp("ngayLap").toLocalDateTime());
        hd.setNhanVien(new NhanVien(rs.getString("maNV")));
        hd.setBan(new Ban(rs.getString("maBan")));

        String maThe = rs.getString("maThe");
        if (maThe != null)
            hd.setTheThanhVien(new TheThanhVien(maThe));

        String maDatBan = rs.getString("maDatBan");
        if (maDatBan != null)
            hd.setBanDat(new BanDat(maDatBan));

        String maKM = rs.getString("maKM");
        if (maKM != null)
            hd.setKhuyenMai(new KhuyenMai(maKM));

        // ✅ Lấy CT hóa đơn bằng đúng Connection truyền xuống
        ArrayList<CT_HoaDon> dsCT = ctHoaDonDAO.layDSCTHoaDonTheoMaHD(con, hd.getMaHoaDon());
        hd.setDanhSachChiTietHoaDon(dsCT);

        return hd;
    }

    //------------------ THÊM HÓA ĐƠN GỒM CT ---------------------
    public boolean themHoaDon(HoaDon hd) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con.setAutoCommit(false); // ✅ Bắt đầu giao dịch

            // ✅ Tạo mã hóa đơn
            String sqlMa = "SELECT 'HD' + RIGHT('000' + CAST((COUNT(*) + 1) AS VARCHAR), 3) FROM HOADON";
            Statement st = con.createStatement();
            rs = st.executeQuery(sqlMa);
            if (rs.next()) {
                hd.setMaHoaDon(rs.getString(1));
            }

            // ✅ Insert hóa đơn ban đầu tổng tiền = 0
            String sql = "INSERT INTO HOADON(maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan)"
                       + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = con.prepareStatement(sql);
            ps.setString(1, hd.getMaHoaDon());
            ps.setString(2, hd.getTheThanhVien() != null ? hd.getTheThanhVien().getMaThe() : null);
            ps.setString(3, hd.getNhanVien().getMaNV());
            ps.setString(4, hd.getBan().getMaBan());
            ps.setString(5, hd.getBanDat() != null ? hd.getBanDat().getMaDatBan() : null);
            ps.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
            ps.setTimestamp(7, Timestamp.valueOf(hd.getNgayLap()));
            ps.setDouble(8, hd.tinhTongTien());
            ps.setString(9, "Chưa thanh toán");
            ps.executeUpdate();

            // ✅ Insert danh sách CT_HOADON
            CTHoaDon_DAO ctDao = new CTHoaDon_DAO();
            double tongTien = 0;

            for (CT_HoaDon ct : hd.getDanhSachChiTietHoaDon()) {
                ct.getHoaDon().setMaHoaDon(hd.getMaHoaDon()); // Gán mã HD vừa tạo
                ctDao.themCTHoaDon(ct, con);
                tongTien += ct.tinhThanhTien();
            }

            // ✅ Update tổng tiền
            String sqlUpdate = "UPDATE HOADON SET tongTien = ? WHERE maHD = ?";
            try (PreparedStatement psUp = con.prepareStatement(sqlUpdate)) {
                psUp.setDouble(1, tongTien);
                psUp.setString(2, hd.getMaHoaDon());
                psUp.executeUpdate();
            }

            con.commit(); // ✅ Giao dịch thành công
            return true;

        } catch (Exception e) {
            try { con.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) {}
        }
        return false;
    }




    //------------------ LẤY HÓA ĐƠN CHƯA THANH TOÁN ----------------
    public HoaDon layHoaDonChuaThanhToanTheoBan(String maBan, String maNV) {
        String sql = "SELECT TOP 1 maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap "
                   + "FROM HOADON WHERE maBan=? AND trangThaiThanhToan='Chờ thanh toán' "
                   + "ORDER BY ngayLap DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return taoHoaDonTuResult(rs, con);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //------------------ SINH MÃ HÓA ĐƠN ---------------------
    public String layMaHDTiepTheo() {
        String sql = "SELECT TOP 1 maHD FROM HoaDon ORDER BY maHD DESC";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String maHD = rs.getString("maHD"); // VD: HD0002
                int so = Integer.parseInt(maHD.substring(2));
                so++;
                return String.format("HD%04d", so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "HD0001"; // Nếu chưa có hóa đơn nào
    }
    
}
