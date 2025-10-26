package dao;

import connectDB.ConnectDB;
import entity.MonAn; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaoCao_DAO {

    /**
     * 1. Tính tổng doanh thu (Tổng cột TONGTIEN trong HOADON).
     */
    public double tinhTongDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        double tongDoanhThu = 0;
        
        // SỬA: Lấy SUM([tongTien]) từ bảng HOADON
        String sql = "SELECT SUM([tongTien]) AS TotalRevenue FROM HOADON WHERE ngayLap >= ? AND ngayLap < ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            if (con == null) return 0;

            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay.plusDays(1))); 
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tongDoanhThu = rs.getDouble("TotalRevenue");
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tính Tổng Doanh Thu:");
            e.printStackTrace();
        }
        return tongDoanhThu;
    }
    
    /**
     * 2. Tính tổng tiền Đặt bàn (Tổng cột TONGTIEN với maDatBan IS NOT NULL).
     */
    public double tinhTongTienDatBan(LocalDate tuNgay, LocalDate denNgay) {
        double tongTienDatBan = 0;

        // SỬA: Lấy SUM([tongTien]) với điều kiện maDatBan IS NOT NULL
        String sql = "SELECT SUM([tongTien]) AS TotalBookingRevenue FROM HOADON " +
                     "WHERE maDatBan IS NOT NULL AND ngayLap >= ? AND ngayLap < ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            if (con == null) return 0;

            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay.plusDays(1))); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tongTienDatBan = rs.getDouble("TotalBookingRevenue");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tính Tổng Tiền Đặt Bàn:");
            e.printStackTrace();
        }
        return tongTienDatBan;
    }

    /**
     * 3. Tính tổng số lượng hóa đơn (Không thay đổi).
     */
    public int tinhTongSoLuongHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        int soLuongHD = 0;
        
        String sql = "SELECT COUNT(maHD) AS CountHD FROM HOADON WHERE ngayLap >= ? AND ngayLap < ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            if (con == null) return 0;

            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay.plusDays(1)));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    soLuongHD = rs.getInt("CountHD");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tính Số Lượng Hóa Đơn:");
            e.printStackTrace();
        }
        return soLuongHD;
    }
    
    /**
     * 4. Lấy Top Món Ăn Bán Chạy nhất (Sử dụng CT_HOADON).
     */
    public Map<MonAn, Integer> getTopMonAnBanChay(LocalDate tuNgay, LocalDate denNgay, int limit) {
        Map<MonAn, Integer> topMon = new LinkedHashMap<>();

        String sql = "SELECT m.maMon, m.tenMon, SUM(ct.soLuong) AS TotalQuantity " +
                     "FROM CT_HOADON ct JOIN HOADON hd ON ct.maHD = hd.maHD " +
                     "JOIN MONAN m ON ct.maMon = m.maMon " +
                     "WHERE hd.ngayLap >= ? AND hd.ngayLap < ? " + 
                     "GROUP BY m.maMon, m.tenMon " +
                     "ORDER BY TotalQuantity DESC " +
                     "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY"; 

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            if (con == null) return topMon;

            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay.plusDays(1)));
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Cần có constructor MonAn(String maMon, String tenMon)
                    MonAn mon = new MonAn(rs.getString("maMon"), rs.getString("tenMon"));
                    int soLuongBan = rs.getInt("TotalQuantity");
                    topMon.put(mon, soLuongBan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy Top Món Ăn Bán Chạy:");
            e.printStackTrace();
        }
        return topMon;
    }
}