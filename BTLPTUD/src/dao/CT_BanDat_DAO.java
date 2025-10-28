package dao;

import connectDB.ConnectDB;
import entity.CTBanDat; // Hoặc CTBanAn nếu bạn đổi tên Entity
import entity.Ban;       // SỬA: Import Ban
import entity.MonAn;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

// LƯU Ý: Phải đảm bảo Ban_DAO và MonAn_DAO đã được khởi tạo
public class CT_BanDat_DAO { // SỬA: Đổi tên lớp

    // SỬA: Thay BanDat_DAO thành Ban_DAO
    private Ban_DAO banDAO = new Ban_DAO(); 
    private MonAn_DAO monAnDAO = new MonAn_DAO();
    
    // =========================================================================
    // === PHƯƠNG THỨC TẠO ENTITY TỪ CSDL ======================================
    // =========================================================================

    /**
     * Trích xuất thông tin một CTBanDat từ ResultSet.
     * Phương thức này cần gọi các DAO khác để tải đối tượng Entity đầy đủ.
     */
    private CTBanDat createCTBanFromResultSet(ResultSet rs) throws SQLException { // SỬA: Đổi tên hàm
        // 1. Lấy mã khóa chính kép từ ResultSet (Giả định PK là maBan, maMon)
        String maBan = rs.getString("maBan"); // SỬA: Đọc maBan
        String maMon = rs.getString("maMon");
        int soLuong = rs.getInt("soLuong");
        
        // 2. Tải các Entity Khóa ngoại đầy đủ
        Ban ban = banDAO.getBanById(maBan); // SỬA: Tải Ban
        MonAn monAn = monAnDAO.getMonAnById(maMon);
        
        // 3. Trả về Entity CTBanDat (hoặc CTBanAn)
        return new CTBanDat(ban, monAn, soLuong); // SỬA: Truyền Ban
    }

    // =========================================================================
    // === PHƯƠNG THỨC TRUY VẤN (READ) =========================================
    // =========================================================================

    /**
     * Tải chi tiết các món đã gọi từ CSDL cho một mã bàn.
     * @param maBan Mã bàn. // SỬA: Tham số là maBan
     * @return Map<String, Integer> (Tên món, Số lượng) để hiển thị lên GUI.
     */
    public Map<String, Integer> layCTBan(String maBan) { // SỬA: Đổi tên hàm và tham số
        Map<String, Integer> chiTiet = new LinkedHashMap<>();
        
        // SỬA: JOIN với MONAN và WHERE theo maBan
        String sql = "SELECT CT.soLuong, MA.tenMon " +
                     "FROM CT_BANDAT CT JOIN MONAN MA ON CT.maMon = MA.maMon WHERE CT.maBan = ?"; // SỬA: WHERE CT.maBan
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maBan); // SỬA: Gán maBan
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chiTiet.put(rs.getString("tenMon"), rs.getInt("soLuong"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải chi tiết bàn ăn: " + e.getMessage()); // SỬA: Thông báo lỗi
            e.printStackTrace();
        }
        return chiTiet;
    }

    // =========================================================================
    // === PHƯƠNG THỨC THAO TÁC (WRITE) ========================================
    // =========================================================================

    /**
     * Chèn một chi tiết bàn ăn mới vào CSDL.
     * @param ct Đối tượng CTBanDat (hoặc CTBanAn) cần thêm.
     * @return true nếu chèn thành công.
     */
    public boolean themCTBan(CTBanDat ct) throws SQLException { // SỬA: Đổi tên hàm
        // Lấy mã chuỗi từ các Entity khóa ngoại
        String maBan = ct.getBan().getMaBan(); // SỬA: Lấy maBan
        String maMon = ct.getMonAn().getMaMonAn();
        int soLuong = ct.getSoLuong();
        
        // SỬA: INSERT vào cột maBan
        String sql = "INSERT INTO CT_BANDAT (maBan, maMon, soLuong) VALUES (?, ?, ?)"; // SỬA: Cột maBan
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maBan); // SỬA: Gán maBan
            ps.setString(2, maMon);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
             System.err.println("Lỗi khi thêm chi tiết bàn ăn: " + e.getMessage()); // SỬA: Thông báo lỗi
             throw e; // Ném ngoại lệ để hàm gọi xử lý Transaction
        }
    }

    /**
     * Xóa toàn bộ chi tiết món ăn cho một mã bàn cụ thể.
     * Sử dụng khi bàn được dọn hoặc hủy đơn.
     * @param maBan Mã bàn. // SỬA: Tham số là maBan
     * @return true nếu xóa thành công.
     */
    public boolean xoaTatCaCTBan(String maBan) throws SQLException { // SỬA: Đổi tên hàm và tham số
        // SỬA: DELETE theo maBan
        String sql = "DELETE FROM CT_BANDAT WHERE maBan = ?"; // SỬA: WHERE maBan
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maBan); // SỬA: Gán maBan
            return ps.executeUpdate() >= 0; // >= 0 vì có thể không có bản ghi nào để xóa
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa chi tiết bàn ăn: " + e.getMessage()); // SỬA: Thông báo lỗi
            throw e;
        }
    }
}