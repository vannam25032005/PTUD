package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;

public class TaiKhoan_DAO {

    // Kiểm tra đăng nhập
    public boolean dangNhap(String tenDangNhap, String matKhau) {
        String sql = "SELECT trangThai FROM TAIKHOAN WHERE tenDangNhap = ? AND matKhau = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return !"Khóa".equalsIgnoreCase(rs.getString("trangThai"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

 // Kiểm tra thông tin tài khoản + email + CCCD
    public boolean kiemTraThongTin(String tenDangNhap, String email, String cccd) {
        try {Connection con = ConnectDB.getConnection();
            String sql = "SELECT t.tenDangNhap " +
                         "FROM TAIKHOAN t " +
                         "JOIN NHANVIEN n ON t.maNV = n.maNV " +
                         "WHERE t.tenDangNhap = ? AND n.email = ? AND n.CCCD = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenDangNhap);
            ps.setString(2, email);
            ps.setString(3, cccd);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật mật khẩu
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        try {Connection con = ConnectDB.getConnection();
            String sql = "UPDATE TAIKHOAN SET matKhau = ? WHERE tenDangNhap = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, matKhauMoi);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
