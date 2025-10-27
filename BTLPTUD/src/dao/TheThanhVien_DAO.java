package dao;

import connectDB.ConnectDB; 
import entity.KhachHang;
import entity.TheThanhVien;

import java.sql.*;
import java.util.ArrayList;

public class TheThanhVien_DAO {

    /**
     * XÓA: Hàm getAllLoaiHang() đã bị xóa vì không còn ComboBox.
     */
    // public ArrayList<String> getAllLoaiHang() throws SQLException { ... }

    /**
     * Lấy thẻ thành viên bằng mã khách hàng
     */
    public TheThanhVien getTheByMaKH(String maKH) throws SQLException {
        TheThanhVien ttv = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT * FROM THETHANHVIEN WHERE maKH = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maThe = rs.getString("maThe");
                    int diem = rs.getInt("diemTichLuy");
                    String loaiHang = rs.getString("loaiHang");
                    
                    KhachHang kh = new KhachHang(maKH);
                    // Dùng constructor, logic trong Entity sẽ tự xử lý
                    ttv = new TheThanhVien(maThe, kh, diem, loaiHang); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return ttv;
    }

    /**
     * Thêm thẻ thành viên mới (Không đổi)
     * Đối tượng ttv truyền vào đã được Entity tính toán loaiHang sẵn
     */
    public boolean addTheThanhVien(TheThanhVien ttv) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "INSERT INTO THETHANHVIEN (maThe, maKH, diemTichLuy, loaiHang) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ttv.getMaThe());
            ps.setString(2, ttv.getKhachHang().getMaKH());
            ps.setInt(3, ttv.getDiemTichLuy());
            ps.setString(4, ttv.getLoaiHang()); // loaiHang này đã được entity tính
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Cập nhật loại hạng thẻ (Không đổi)
     * Đối tượng ttv truyền vào đã được Entity tính toán loaiHang sẵn
     */
    public boolean updateTheThanhVien(TheThanhVien ttv) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE THETHANHVIEN SET loaiHang = ?, diemTichLuy = ? WHERE maThe = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ttv.getLoaiHang()); // loaiHang này đã được entity tính
            ps.setInt(2, ttv.getDiemTichLuy());
            ps.setString(3, ttv.getMaThe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    // (Các hàm delete... và generateNewMaThe() giữ nguyên)
    public boolean deleteTheThanhVienByMaKH(String maKH) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "DELETE FROM THETHANHVIEN WHERE maKH = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ps.executeUpdate(); 
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String generateNewMaThe() throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT MAX(maThe) FROM THETHANHVIEN";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxMaThe = rs.getString(1);
                if (maxMaThe != null) {
                    int num = Integer.parseInt(maxMaThe.substring(3)) + 1;
                    return String.format("TTV%03d", num);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof SQLException) throw (SQLException)e;
        }
        return "TTV001";
    }
}