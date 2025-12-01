package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import connectDB.ConnectDB;

public class Dashboard_DAO {

    // ---------------------------------------------------------
    // 1. Doanh thu trong ngày
    // ---------------------------------------------------------
    public double getDoanhThuHomNay() {
        String sql =
            "SELECT SUM(tongTien) AS doanhThu " +
            "FROM HOADON " +
            "WHERE CAST(ngayLap AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getDouble("doanhThu");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ---------------------------------------------------------
    // 2. Số món bán trong ngày
    // ---------------------------------------------------------
    public int getSoMonBanHomNay() {
        String sql =
            "SELECT SUM(c.soLuong) AS tongSL " +
            "FROM CT_HOADON c JOIN HOADON h ON c.maHD = h.maHD " +
            "WHERE CAST(h.ngayLap AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getInt("tongSL");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ---------------------------------------------------------
    // 3. Doanh thu trung bình tháng này
    // ---------------------------------------------------------
    public double getDoanhThuThangNay() {
        String sql =
            "SELECT SUM(tongTien) AS doanhThu " +
            "FROM HOADON " +
            "WHERE MONTH(ngayLap) = MONTH(GETDATE()) " +
            "AND YEAR(ngayLap) = YEAR(GETDATE())";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getDouble("doanhThu");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ---------------------------------------------------------
    // 4. Doanh thu trung bình quý này
    // ---------------------------------------------------------
    public double getDoanhThuQuyNay() {
        String sql =
            "SELECT SUM(tongTien) AS doanhThu " +
            "FROM HOADON " +
            "WHERE DATEPART(QUARTER, ngayLap) = DATEPART(QUARTER, GETDATE()) " +
            "AND YEAR(ngayLap) = YEAR(GETDATE())";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getDouble("doanhThu");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ---------------------------------------------------------
    // 5. Thống kê theo khung giờ (Sáng – Trưa – Tối)
    // ---------------------------------------------------------
    public Map<String, Integer> getTiLeTheoKhungGio() {

        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Sáng", 0);
        map.put("Trưa", 0);
        map.put("Tối", 0);

        String sql =
            "SELECT DATEPART(HOUR, ngayLap) AS gio, COUNT(*) AS soLuong " +
            "FROM HOADON " +
            "WHERE CAST(ngayLap AS DATE) = CAST(GETDATE() AS DATE) " +
            "GROUP BY DATEPART(HOUR, ngayLap)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int gio = rs.getInt("gio");
                int sl = rs.getInt("soLuong");

                if (gio >= 5 && gio < 11)
                    map.put("Sáng", map.get("Sáng") + sl);
                else if (gio >= 11 && gio < 17)
                    map.put("Trưa", map.get("Trưa") + sl);
                else if (gio >= 17 && gio <= 23)
                    map.put("Tối", map.get("Tối") + sl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    // ---------------------------------------------------------
    // 6. Thống kê đặt bàn theo thứ trong tuần
    // ---------------------------------------------------------
    public Map<String, Integer> getThongKeDatBanTuanNay() {

        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("T2", 0);
        map.put("T3", 0);
        map.put("T4", 0);
        map.put("T5", 0);
        map.put("T6", 0);
        map.put("T7", 0);
        map.put("CN", 0);

        String sql =
            "SELECT DATEPART(WEEKDAY, ngayDat) AS thu, COUNT(*) AS soLuong " +
            "FROM BANDAT " +
            "WHERE DATEPART(WEEK, ngayDat) = DATEPART(WEEK, GETDATE()) " +
            "AND YEAR(ngayDat) = YEAR(GETDATE()) " +
            "GROUP BY DATEPART(WEEKDAY, ngayDat)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int thu = rs.getInt("thu");
                int sl = rs.getInt("soLuong");

                switch (thu) {
                    case 2: map.put("T2", sl); break;
                    case 3: map.put("T3", sl); break;
                    case 4: map.put("T4", sl); break;
                    case 5: map.put("T5", sl); break;
                    case 6: map.put("T6", sl); break;
                    case 7: map.put("T7", sl); break;
                    case 1: map.put("CN", sl); break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    // ---------------------------------------------------------
    // 7. Top 3 món ăn bán chạy
    // ---------------------------------------------------------
    public List<Map<String, Object>> getTop3MonBanChay() {

        List<Map<String, Object>> list = new ArrayList<>();

        String sql =
            "SELECT TOP 3 m.tenMon, SUM(c.soLuong) AS tongSL " +
            "FROM CT_HOADON c JOIN MONAN m ON c.maMon = m.maMon " +
            "GROUP BY m.tenMon " +
            "ORDER BY tongSL DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("tenMon", rs.getString("tenMon"));
                row.put("soLuong", rs.getInt("tongSL"));
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
