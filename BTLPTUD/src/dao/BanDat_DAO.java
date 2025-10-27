package dao;

import connectDB.ConnectDB;
import entity.BanDat;
import entity.KhachHang; // Import KhachHang entity
import entity.Ban;       // Import Ban entity
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class BanDat_DAO {

    // Khởi tạo các DAO phụ thuộc để truy vấn các Entity liên quan
    private KhachHang_DAO khachHang_DAO = new KhachHang_DAO();
    private Ban_DAO ban_DAO = new Ban_DAO();

    /**
     * Phương thức chuyển đổi ResultSet thành đối tượng BanDat.
     * Sử dụng các DAO phụ thuộc để lấy đối tượng KhachHang và Ban.
     */
    private BanDat createBanDatFromResultSet(ResultSet rs) throws SQLException {
        // Lấy các mã khóa ngoại từ ResultSet
        String maKhachHang = rs.getString("maKH");
        String maBan = rs.getString("maBan");

        // Sử dụng DAO để truy vấn Entity
        KhachHang khachHang = khachHang_DAO.getKhachHangById(maKhachHang); 
        Ban ban = ban_DAO.getBanById(maBan);

        // Lấy các trường thông thường
        String maDatBan = rs.getString("maDatBan");
        
        // Chuyển đổi java.sql.Date và java.sql.Time sang java.time.LocalDate và LocalTime
        LocalDate ngayDat = rs.getDate("ngayDat").toLocalDate();
        LocalTime gioDat = rs.getTime("gioDat").toLocalTime();
        
        int soLuongKhach = rs.getInt("soLuongKhach");
        double tienCoc = rs.getDouble("tienCoc");
        String trangThai = rs.getString("trangThai");
        String ghiChu = rs.getString("ghiChu");

        // Tạo đối tượng BanDat mới
        return new BanDat(maDatBan, khachHang, ban, ngayDat, gioDat, 
                          soLuongKhach, tienCoc, trangThai, ghiChu);
    }

    /**
     * Lấy tất cả các đặt bàn từ CSDL.
     * @return ArrayList<BanDat> danh sách tất cả các đặt bàn.
     */
    public ArrayList<BanDat> getAllBanDat() {
        ArrayList<BanDat> dsDatBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        // Cần SELECT cả mã KH và mã Bàn để query đối tượng
        String sql = "SELECT maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu FROM BANDAT";
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                dsDatBan.add(createBanDatFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsDatBan;
    }
    
    /**
     * Lấy thông tin đặt bàn theo mã.
     * @param maDatBan Mã đặt bàn cần tìm.
     * @return BanDat đối tượng đặt bàn nếu tìm thấy, ngược lại trả về null.
     */
    public BanDat getBanDatById(String maDatBan) {
        BanDat banDat = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu FROM BANDAT WHERE maDatBan = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    banDat = createBanDatFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm đặt bàn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return banDat;
    }

    /**
     * Thêm một đặt bàn mới vào CSDL.
     * @param banDat Đối tượng BanDat cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addBanDat(BanDat banDat) {
        Connection con = ConnectDB.getConnection();
        // Cần INSERT mã KH và mã Bàn
        String sql = "INSERT INTO BANDAT (maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, banDat.getMaDatBan());
            // Lấy mã từ đối tượng Entity lồng nhau
            ps.setString(2, banDat.getKhachHang().getMaKH()); 
            ps.setString(3, banDat.getBan().getMaBan()); 
            
            // Chuyển đổi LocalDate và LocalTime sang java.sql.Date và Time
            ps.setDate(4, Date.valueOf(banDat.getNgayDat()));
            ps.setTime(5, Time.valueOf(banDat.getGioDat()));
            
            ps.setInt(6, banDat.getSoLuongKhach());
            ps.setDouble(7, banDat.getTienCoc());
            ps.setString(8, banDat.getTrangThai());
            ps.setString(9, banDat.getGhiChu());
            
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Cập nhật thông tin một đặt bàn trong CSDL.
     * @param banDat Đối tượng BanDat với thông tin mới (dựa trên maDatBan).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateBanDat(BanDat banDat) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE BANDAT SET maKH = ?, maBan = ?, ngayDat = ?, gioDat = ?, soLuongKhach = ?, tienCoc = ?, trangThai = ?, ghiChu = ? WHERE maDatBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, banDat.getKhachHang().getMaKH());
            ps.setString(2, banDat.getBan().getMaBan());
            
            ps.setDate(3, Date.valueOf(banDat.getNgayDat()));
            ps.setTime(4, Time.valueOf(banDat.getGioDat()));
            
            ps.setInt(5, banDat.getSoLuongKhach());
            ps.setDouble(6, banDat.getTienCoc());
            ps.setString(7, banDat.getTrangThai());
            ps.setString(8, banDat.getGhiChu());
            ps.setString(9, banDat.getMaDatBan()); // WHERE clause
            
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Xóa một đặt bàn khỏi CSDL.
     * @param maDatBan Mã đặt bàn cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteBanDat(String maDatBan) {
        Connection con = ConnectDB.getConnection();
        String sql = "DELETE FROM BANDAT WHERE maDatBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Tìm kiếm đặt bàn theo nhiều tiêu chí (tên KH, SĐT, mã bàn, trạng thái).
     * Do tên KH và SĐT nằm trong bảng KhachHang, cần JOIN.
     * @param keyword Từ khóa tìm kiếm.
     * @param trangThaiLoc Trạng thái để lọc (hoặc "Tất cả").
     * @return ArrayList<BanDat> danh sách đặt bàn phù hợp.
     */
    public ArrayList<BanDat> searchBanDat(String keyword, String trangThaiLoc) {
        ArrayList<BanDat> dsDatBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        
        // Cần JOIN với bảng KhachHang để tìm kiếm theo Tên KH hoặc SĐT
        StringBuilder sql = new StringBuilder("SELECT BD.* FROM BANDAT BD JOIN KHACHHANG KH ON BD.maKH = KH.maKH WHERE 1=1");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (BD.maDatBan LIKE ? OR KH.tenKH LIKE ? OR KH.sdt LIKE ? OR BD.maBan LIKE ?)");
        }
        if (trangThaiLoc != null && !trangThaiLoc.equals("Tất cả")) {
            sql.append(" AND BD.trangThai = ?");
        }
        
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                ps.setString(paramIndex++, likeKeyword); // BD.maDatBan
                ps.setString(paramIndex++, likeKeyword); // KH.tenKH
                ps.setString(paramIndex++, likeKeyword); // KH.sdt
                ps.setString(paramIndex++, likeKeyword); // BD.maBan
            }
            if (trangThaiLoc != null && !trangThaiLoc.equals("Tất cả")) {
                ps.setString(paramIndex++, trangThaiLoc);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsDatBan.add(createBanDatFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsDatBan;
    }
    
    /**
     * Lấy mã đặt bàn tự động tăng lớn nhất.
     * @return String mã đặt bàn tiếp theo.
     */
    public String generateNewMaDatBan() {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT MAX(maDatBan) FROM BANDAT WHERE maDatBan LIKE 'DB%'";
        String lastId = null;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lastId = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lastId == null) {
            return "DB001";
        } else {
            // Giả định mã có dạng DBxxx
            try {
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("DB%03d", num);
            } catch (NumberFormatException e) {
                // Xử lý trường hợp mã không đúng format
                return "DB" + System.currentTimeMillis(); 
            }
        }
    }
}