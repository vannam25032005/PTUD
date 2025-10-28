package dao;

import connectDB.ConnectDB;
import entity.CT_HoaDon;
import entity.HoaDon;
import entity.MonAn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CTHoaDon_DAO {

    
    public boolean themCTHoaDon(CT_HoaDon ct, Connection con) throws SQLException {
        // Cần đảm bảo maHD và maMon không NULL
        if (ct.getHoaDon() == null || ct.getMonAn() == null) {
            throw new IllegalArgumentException("Hóa đơn và Món ăn không được rỗng trong chi tiết hóa đơn.");
        }
        
        String sql = "INSERT INTO CT_HOADON (maHD, maMon, soLuong) VALUES (?, ?, ?)";
        // KHÔNG đóng PreparedStatement hoặc Connection ở đây
        try (PreparedStatement ps = con.prepareStatement(sql)) { 
            ps.setString(1, ct.getHoaDon().getMaHoaDon());
            ps.setString(2, ct.getMonAn().getMaMonAn());
            ps.setInt(3, ct.getSoLuong());
            return ps.executeUpdate() > 0;
        }
    }

    public ArrayList<CT_HoaDon> layDSCTHoaDonTheoMaHD(Connection con, String maHD) throws SQLException {
        ArrayList<CT_HoaDon> danhSachCT = new ArrayList<>();
        String sql = "SELECT CTHD.maHD, CTHD.maMon, CTHD.soLuong, MA.tenMon, MA.giaMon "
                   + "FROM CT_HoaDon CTHD JOIN MONAN MA ON CTHD.maMon = MA.maMon "
                   + "WHERE CTHD.maHD = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonAn monAn = new MonAn(rs.getString("maMon"));
                    monAn.setTenMonAn(rs.getString("tenMon"));
                    monAn.setGiaMonAn(rs.getDouble("giaMon"));

                    HoaDon hoaDon = new HoaDon(rs.getString("maHD"));
                    CT_HoaDon ct = new CT_HoaDon(hoaDon, monAn, rs.getInt("soLuong"));

                    danhSachCT.add(ct);
                }
            }
        }
        return danhSachCT;
    }

 // Trong CTHoaDon_DAO.java
    public CT_HoaDon layCTHoaDon(String maHD, String maMon) throws SQLException {
        CT_HoaDon ct = null;
        String sql = "SELECT maHD, maMon, soLuong FROM CT_HOADON WHERE maHD = ? AND maMon = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maHD);
            ps.setString(2, maMon);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Chỉ cần tạo Entity ảo (MaHD, MaMon)
                    HoaDon hd = new HoaDon(rs.getString("maHD"));
                    MonAn ma = new MonAn(rs.getString("maMon"));
                    ct = new CT_HoaDon(hd, ma, rs.getInt("soLuong"));
                }
            }
        }
        return ct;
    }
 // Trong CTHoaDon_DAO.java
    public boolean capNhatSoLuongCTHoaDon(String maHD, String maMon, int soLuongMoi) throws SQLException {
        String sql = "UPDATE CT_HOADON SET soLuong = ? WHERE maHD = ? AND maMon = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, soLuongMoi);
            ps.setString(2, maHD);
            ps.setString(3, maMon);
            
            return ps.executeUpdate() > 0;
        }
    }
 // Trong CTHoaDon_DAO.java
    public boolean xoaCTHoaDon(String maHD, String maMon) throws SQLException {
        String sql = "DELETE FROM CT_HOADON WHERE maHD = ? AND maMon = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maHD);
            ps.setString(2, maMon);
            
            return ps.executeUpdate() > 0;
        }
    }
}