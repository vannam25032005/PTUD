package dao;

import connectDB.ConnectDB;
import entity.Ban;
import java.sql.*;
import java.util.ArrayList;

public class Ban_DAO {

    // Phương thức chuyển đổi ResultSet thành đối tượng Ban
    private Ban createBanFromResultSet(ResultSet rs) throws SQLException {
        String maBan = rs.getString("maBan");
        String loaiBan = rs.getString("loaiBan");
        int soGhe = rs.getInt("soGhe");
        String khuVuc = rs.getString("khuVuc");
        String trangThai = rs.getString("trangThai");
        return new Ban(maBan, loaiBan, soGhe, khuVuc, trangThai);
    }
    
    /**
     * Lấy toàn bộ danh sách bàn từ CSDL.
     * @return ArrayList<Ban> danh sách tất cả các bàn.
     */
    public ArrayList<Ban> getAllBan() {
        ArrayList<Ban> dsBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection(); // Lấy kết nối Singleton
        String sql = "SELECT maBan, loaiBan, soGhe, khuVuc, trangThai FROM BAN";
        
        try (PreparedStatement ps = con.prepareStatement(sql); // Statement và ResultSet tự đóng
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                dsBan.add(createBanFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsBan;
    }
    
    /**
     * Tìm bàn theo mã bàn.
     * @param maBan Mã bàn cần tìm.
     * @return Ban đối tượng bàn nếu tìm thấy, ngược lại trả về null.
     */
    public Ban getBanById(String maBan) {
        Ban ban = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maBan, loaiBan, soGhe, khuVuc, trangThai FROM BAN WHERE maBan = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ban = createBanFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm bàn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return ban;
    }

    /**
     * Cập nhật trạng thái của một bàn trong CSDL.
     * @param maBan Mã bàn cần cập nhật.
     * @param trangThaiMoi Trạng thái mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateTrangThaiBan(String maBan, String trangThaiMoi) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE BAN SET trangThai = ? WHERE maBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maBan);
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Thêm một bàn mới vào CSDL.
     * @param ban Đối tượng Ban cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addBan(Ban ban) {
        Connection con = ConnectDB.getConnection();
        String sql = "INSERT INTO BAN (maBan, loaiBan, soGhe, khuVuc, trangThai) VALUES (?, ?, ?, ?, ?)";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ban.getMaBan());
            ps.setString(2, ban.getLoaiBan());
            ps.setInt(3, ban.getSoGhe());
            ps.setString(4, ban.getKhuVuc());
            ps.setString(5, ban.getTrangThai());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Cập nhật thông tin một bàn trong CSDL.
     * @param ban Đối tượng Ban với thông tin mới (dựa trên maBan).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateBan(Ban ban) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE BAN SET loaiBan = ?, soGhe = ?, khuVuc = ?, trangThai = ? WHERE maBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ban.getLoaiBan());
            ps.setInt(2, ban.getSoGhe());
            ps.setString(3, ban.getKhuVuc());
            ps.setString(4, ban.getTrangThai());
            ps.setString(5, ban.getMaBan());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Xóa một bàn khỏi CSDL.
     * @param maBan Mã bàn cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteBan(String maBan) {
        Connection con = ConnectDB.getConnection();
        String sql = "DELETE FROM BAN WHERE maBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }
    
    /**
     * Lấy danh sách bàn theo trạng thái hoặc loại bàn.
     * @param filterType "trangThai" hoặc "loaiBan"
     * @param filterValue Giá trị để lọc
     * @return ArrayList<Ban> danh sách bàn đã lọc.
     */
    public ArrayList<Ban> getFilteredBan(String filterType, String filterValue) {
        ArrayList<Ban> dsBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        if (!filterType.equals("trangThai") && !filterType.equals("loaiBan")) {
            System.err.println("Lọc sai tham số cột!");
            return dsBan;
        }

        String sql = "SELECT maBan, loaiBan, soGhe, khuVuc, trangThai FROM BAN WHERE " + filterType + " = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, filterValue); // ✅ Thiếu dòng này gây lỗi
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsBan.add(createBanFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsBan;
    }
}