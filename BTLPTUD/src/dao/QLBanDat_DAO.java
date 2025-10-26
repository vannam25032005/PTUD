package dao;

import java.sql.*;
import java.util.*;
import connectDB.ConnectDB;
import entity.*;

public class QLBanDat_DAO {

    // 🔹 LẤY DANH SÁCH TẤT CẢ CÁC BÀN (kèm trạng thái nếu đã đặt)
    public List<BanDat> getDanhSachBan() {
        List<BanDat> ds = new ArrayList<>();

        String sql = """
            SELECT 
                b.maBan, 
                b.sucChua, 
                bd.maDatBan, 
                bd.trangThai, 
                bd.soLuongKhach, 
                bd.tienCoc, 
                kh.maKH, 
                kh.hoTen, 
                kh.soDienThoai
            FROM BAN b
            LEFT JOIN BANDAT bd ON b.maBan = bd.maBan 
                AND bd.trangThai IN (N'Đã đặt', N'Đang dùng')
            LEFT JOIN KHACHHANG kh ON bd.maKH = kh.maKH
            ORDER BY b.maBan
        """;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maBan = rs.getString("maBan");
                int sucChua = rs.getInt("sucChua");

                Ban ban = new Ban(maBan, sucChua);
                String maDatBan = rs.getString("maDatBan");
                String trangThai = rs.getString("trangThai");
                int soLuongKhach = rs.getInt("soLuongKhach");
                double tienCoc = rs.getDouble("tienCoc");

                String maKH = rs.getString("maKH");
                String hoTen = rs.getString("hoTen");
                String sdt = rs.getString("soDienThoai");
                KhachHang kh = maKH != null ? new KhachHang(maKH, hoTen, sdt) : null;

                BanDat bd = new BanDat(maDatBan, kh, ban, soLuongKhach, tienCoc, trangThai);
                ds.add(bd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // 🔹 TẠO MÃ ĐẶT BÀN TỰ ĐỘNG (VD: DB01, DB02,...)
    private String taoMaDatBanMoi() {
        String sql = "SELECT MAX(maDatBan) FROM BANDAT";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String max = rs.getString(1);
                if (max != null) {
                    int so = Integer.parseInt(max.substring(2)) + 1;
                    return String.format("DB%02d", so);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "DB01";
    }

    // 🔹 THÊM MỚI KHÁCH HÀNG NẾU CHƯA CÓ
    public String themHoacLayMaKhachHang(String hoTen, String soDienThoai) {
        String maKH = null;
        String sqlTim = "SELECT maKH FROM KHACHHANG WHERE soDienThoai = ?";
        String sqlThem = "INSERT INTO KHACHHANG(maKH, hoTen, soDienThoai) VALUES (?, ?, ?)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement psTim = con.prepareStatement(sqlTim)) {

            psTim.setString(1, soDienThoai);
            ResultSet rs = psTim.executeQuery();
            if (rs.next()) {
                maKH = rs.getString("maKH");
            } else {
                // Tạo mã KH mới
                maKH = "KH" + System.currentTimeMillis() % 100000; // ví dụ KH12345
                try (PreparedStatement psThem = con.prepareStatement(sqlThem)) {
                    psThem.setString(1, maKH);
                    psThem.setString(2, hoTen);
                    psThem.setString(3, soDienThoai);
                    psThem.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maKH;
    }

    // 🔹 ĐẶT BÀN MỚI
    public boolean datBan(String maBan, String hoTen, String soDienThoai, int soLuong, double tienCoc, String thoiGian) {
        String maKH = themHoacLayMaKhachHang(hoTen, soDienThoai);
        String maDatBan = taoMaDatBanMoi();
        String sql = """
            INSERT INTO BANDAT (maDatBan, maBan, maKH, soLuongKhach, tienCoc, thoiGianDat, trangThai)
            VALUES (?, ?, ?, ?, ?, ?, N'Đã đặt')
        """;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maDatBan);
            ps.setString(2, maBan);
            ps.setString(3, maKH);
            ps.setInt(4, soLuong);
            ps.setDouble(5, tienCoc);
            ps.setString(6, thoiGian);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 🔹 CẬP NHẬT THÔNG TIN ĐẶT BÀN (VD: đổi giờ, cập nhật số lượng, trạng thái...)
    public boolean capNhatBanDat(String maDatBan, int soLuong, double tienCoc, String trangThai) {
        String sql = "UPDATE BANDAT SET soLuongKhach = ?, tienCoc = ?, trangThai = ? WHERE maDatBan = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, soLuong);
            ps.setDouble(2, tienCoc);
            ps.setString(3, trangThai);
            ps.setString(4, maDatBan);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 🔹 HỦY BÀN (chuyển trạng thái sang "Đã hủy")
    public boolean huyBan(String maDatBan) {
        String sql = "UPDATE BANDAT SET trangThai = N'Đã hủy' WHERE maDatBan = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
