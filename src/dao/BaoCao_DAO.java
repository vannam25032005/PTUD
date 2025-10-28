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
    
   
    public Map<MonAn, Integer> getTopMonAnBanChay(LocalDate tuNgay, LocalDate denNgay, int limit) {
        Map<MonAn, Integer> topMon = new LinkedHashMap<>();

        String sql = "SELECT m.maMon, m.tenMon, SUM(ct.soLuong) AS TotalQuantity " +
                     "FROM CT_HOADON ct " +
                     "JOIN HOADON hd ON ct.maHD = hd.maHD " +
                     "JOIN MONAN m ON ct.maMon = m.maMon " +
                     "WHERE hd.ngayLap >= ? AND hd.ngayLap < ? " + 
                     "GROUP BY m.maMon, m.tenMon " +
                     "ORDER BY TotalQuantity DESC " +
                     "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY"; 

        // SỬA: Loại bỏ kiểm tra 'if (con == null)' dư thừa vì try-with-resources
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            // Chuyển đổi LocalDate sang java.sql.Date
            java.sql.Date sqlTuNgay = java.sql.Date.valueOf(tuNgay);
            // Ngày kết thúc: bao gồm cả ngày denNgay (đến 00:00:00 của ngày tiếp theo)
            java.sql.Date sqlDenNgayTiepTheo = java.sql.Date.valueOf(denNgay.plusDays(1)); 

            stmt.setDate(1, sqlTuNgay);
            stmt.setDate(2, sqlDenNgayTiepTheo);
            stmt.setInt(3, limit);

            // Xử lý ResultSet an toàn
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Đảm bảo MonAn có constructor MonAn(String maMon, String tenMon)
                    MonAn mon = new MonAn(rs.getString("maMon"), rs.getString("tenMon")); 
                    int soLuongBan = rs.getInt("TotalQuantity");
                    topMon.put(mon, soLuongBan);
                }
            }
        } catch (SQLException e) {
            // Nên sử dụng Logger hoặc ít nhất là print ra stderr rõ ràng
            System.err.println("Lỗi SQL khi lấy Top Món Ăn Bán Chạy.");
            e.printStackTrace();
        }
        return topMon;
    }

    public Map<String, Double> getDoanhThuTheoNhom(LocalDate tuNgay, LocalDate denNgay, String groupType) {
        Map<String, Double> doanhThu = new LinkedHashMap<>();
        String selectCol;

        // 1. Xác định format SQL Server cho nhóm
        if (groupType.equals("Ngày")) {
            // Hiển thị dạng YYYY-MM-DD
            selectCol = "FORMAT(hd.ngayLap, 'yyyy-MM-dd')"; 
        } else if (groupType.equals("Tháng")) {
            // Hiển thị dạng YYYY-MM
            selectCol = "FORMAT(hd.ngayLap, 'yyyy-MM')"; 
        } else { // Năm
            // Hiển thị dạng YYYY
            selectCol = "FORMAT(hd.ngayLap, 'yyyy')"; 
        }

        String sql = "SELECT " + selectCol + " AS GroupKey, " +
                     "SUM([tongTien]) AS TotalRevenue " +
                     "FROM HOADON hd " +
                     "WHERE hd.ngayLap >= ? AND hd.ngayLap < ? " +
                     "GROUP BY " + selectCol +
                     " ORDER BY GroupKey";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            if (con == null) return doanhThu;

            // Lọc đến hết ngày cuối cùng của khoảng
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay.plusDays(1))); 

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String groupKey = rs.getString("GroupKey");
                    double revenue = rs.getDouble("TotalRevenue");
                    doanhThu.put(groupKey, revenue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy Doanh Thu theo nhóm: " + e.getMessage());
            e.printStackTrace();
        }
        return doanhThu;
    }
}