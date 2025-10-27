package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import connectDB.ConnectDB;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

    public boolean dangNhap(String tenDangNhap, String matKhau) {
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ? AND matKhau = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String trangThai = rs.getString("trangThai");
                if (trangThai.equalsIgnoreCase("Khóa")) {
                    JOptionPane.showMessageDialog(null, "Tài khoản bị khóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                JOptionPane.showMessageDialog(null, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, matKhauMoi);
            stmt.setString(2, tenDangNhap);

            int n = stmt.executeUpdate();
            return n > 0; 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
