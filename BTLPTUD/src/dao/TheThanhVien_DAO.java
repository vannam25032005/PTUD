package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.TheThanhVien;

public class TheThanhVien_DAO {

    //  Lấy thẻ theo mã khách hàng
    public TheThanhVien getTheTheoMaKH(String maKH) {
        TheThanhVien the = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            String sql = "SELECT maThe, maKH, diemTichLuy, loaiHang FROM THETHANHVIEN WHERE maKH = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maKH);
            rs = stmt.executeQuery();

            if (rs.next()) {
                the = new TheThanhVien();
                the.setMaThe(rs.getString("maThe"));
                the.setDiemTichLuy(rs.getInt("diemTichLuy"));
                the.setLoaiHang(rs.getString("loaiHang"));
                KhachHang kh = new KhachHang(rs.getString("maKH"));
                the.setKhachHang(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return the;
    }

    // Cập nhật điểm & hạng sau khi chi tiêu
    public boolean capNhatDiemVaHang(String maThe, double tongTienChiTieu) {
        Connection con = null;
        PreparedStatement stmt = null;
        boolean result = false;

        try {
            con = ConnectDB.getConnection();

            // 1 điểm = 100.000 VNĐ
            int diemMoi = (int) (tongTienChiTieu / 100_000);

            // Lấy điểm hiện tại
            int diemHienTai = 0;
            String sqlSelect = "SELECT diemTichLuy FROM THETHANHVIEN WHERE maThe = ?";
            stmt = con.prepareStatement(sqlSelect);
            stmt.setString(1, maThe);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                diemHienTai = rs.getInt("diemTichLuy");
            }
            rs.close();
            stmt.close();

            int tongDiem = diemHienTai + diemMoi;
            String loaiHang = xacDinhLoaiHang(tongDiem);

            // Cập nhật vào CSDL
            String sqlUpdate = "UPDATE THETHANHVIEN SET diemTichLuy = ?, loaiHang = ? WHERE maThe = ?";
            stmt = con.prepareStatement(sqlUpdate);
            stmt.setInt(1, tongDiem);
            stmt.setString(2, loaiHang);
            stmt.setString(3, maThe);

            int n = stmt.executeUpdate();
            result = n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return result;
    }

    //  Xác định loại hạng dựa vào điểm
    public String xacDinhLoaiHang(int diem) {
        if (diem >= 250) {
            return "Kim cương";
        } else if (diem >= 100) {
            return "Vàng";
        } else {
            return "Bạc";
        }
    }

    //  Lấy phần trăm ưu đãi theo loại hạng
    public double tinhUuDai(String loaiHang) {
        switch (loaiHang) {
            case "Kim cương": return 0.15;
            case "Vàng": return 0.12;
            case "Bạc": return 0.10;
            default: return 0.0;
        }
    }
    public static void main(String[] args) {
        TheThanhVien_DAO dao = new TheThanhVien_DAO();

        // ✅ Nhập mã khách hàng có trong database của bạn
        String maKH = "KH001";

        TheThanhVien the = dao.getTheTheoMaKH(maKH);

        if (the != null) {
            System.out.println("===== KẾT QUẢ TRUY VẤN =====");
            System.out.println("Mã thẻ: " + the.getMaThe());
            System.out.println("Điểm tích lũy: " + the.getDiemTichLuy());
            System.out.println("Loại hạng: " + the.getLoaiHang());

            if (the.getKhachHang() != null)
                System.out.println("✅ Liên kết khách hàng OK: " + the.getKhachHang().getMaKH());
            else
                System.out.println("❌ Lỗi: Khách hàng đang null!");
        } else {
            System.out.println("❌ Không tìm thấy thẻ cho mã khách hàng: " + maKH);
        }
    }
}