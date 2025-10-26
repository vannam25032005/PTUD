package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.MonAn;
import entity.QuanLy;

public class MonAn_DAO {

    //Đọc toàn bộ món ăn
    public List<MonAn> docTuBang() {
        List<MonAn> dsMon = new ArrayList<>();
        String sql = "SELECT * FROM MONAN";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                QuanLy ql = new QuanLy(rs.getString("maQL"));
                MonAn mon = new MonAn(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getString("loaiMon"),
                        rs.getDouble("giaMon"),
                        rs.getString("hinhAnh"),
                        ql
                );
                dsMon.add(mon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }

    //Thêm món ăn
    public boolean themMonAn(MonAn mon) {
        String sql = "INSERT INTO MONAN(maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mon.getMaMonAn());
            ps.setString(2, mon.getTenMonAn());
            ps.setString(3, mon.getLoaiMonAn());
            ps.setDouble(4, mon.getGiaMonAn());
            ps.setString(5, mon.getHinhAnh());
            if (mon.getQuanLy() != null && mon.getQuanLy().getMaQL() != null)
                ps.setString(6, mon.getQuanLy().getMaQL());
            else
                ps.setNull(6, Types.NVARCHAR);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Cập nhật món ăn
    public boolean chinhSuaMonAn(MonAn mon) {
        String sql = "UPDATE MONAN SET tenMon=?, loaiMon=?, giaMon=?, hinhAnh=?, maQL=? WHERE maMon=?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mon.getTenMonAn());
            ps.setString(2, mon.getLoaiMonAn());
            ps.setDouble(3, mon.getGiaMonAn());
            ps.setString(4, mon.getHinhAnh());
            if (mon.getQuanLy() != null && mon.getQuanLy().getMaQL() != null)
                ps.setString(5, mon.getQuanLy().getMaQL());
            else
                ps.setNull(5, Types.NVARCHAR);
            ps.setString(6, mon.getMaMonAn());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm theo tên món
    public List<MonAn> timTheoTen(String tenMon) {
        List<MonAn> ds = new ArrayList<>();
        String sql = "SELECT * FROM MONAN WHERE tenMon LIKE ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + tenMon + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QuanLy ql = new QuanLy(rs.getString("maQL"));
                MonAn mon = new MonAn(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getString("loaiMon"),
                        rs.getDouble("giaMon"),
                        rs.getString("hinhAnh"),
                        ql
                );
                ds.add(mon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Lọc theo loại món
    public List<MonAn> locTheoLoai(String loaiMon) {
        List<MonAn> dsMon = new ArrayList<>();
        String sql = "SELECT * FROM MONAN WHERE loaiMon = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loaiMon);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QuanLy ql = new QuanLy(rs.getString("maQL"));
                MonAn mon = new MonAn(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getString("loaiMon"),
                        rs.getDouble("giaMon"),
                        rs.getString("hinhAnh"),
                        ql
                );
                dsMon.add(mon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }

    // Lọc + sắp xếp theo giá
    public List<MonAn> locTheoGia(String thuTu) {
        List<MonAn> dsMon = new ArrayList<>();
        String sql = "SELECT * FROM MONAN ORDER BY giaMon " + (thuTu.equalsIgnoreCase("DESC") ? "DESC" : "ASC");
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QuanLy ql = new QuanLy(rs.getString("maQL"));
                MonAn mon = new MonAn(
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getString("loaiMon"),
                        rs.getDouble("giaMon"),
                        rs.getString("hinhAnh"),
                        ql
                );
                dsMon.add(mon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }
    public List<MonAn> locMonAn(String loaiMonAn, String thuTu) {
        List<MonAn> dsMon = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();

            String sql = "SELECT * FROM MONAN";
            if (loaiMonAn != null && !loaiMonAn.equals("Tất cả")) {
                sql += " WHERE loaiMon = ?";
            }
            sql += " ORDER BY giaMon " + (thuTu.equalsIgnoreCase("DESC") ? "DESC" : "ASC");

            PreparedStatement ps = con.prepareStatement(sql);
            if (loaiMonAn != null && !loaiMonAn.equals("Tất cả")) {
                ps.setString(1, loaiMonAn);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MonAn mon = new MonAn(
                    rs.getString("maMon"),
                    rs.getString("tenMon"), 
                    rs.getString("loaiMon"),
                    rs.getDouble("giaMon"),           
                    rs.getString("hinhAnh"),
                    new QuanLy(rs.getString("maQL"))
                );
                dsMon.add(mon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }
 //Hàm đóng kết nối
    private void close(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
