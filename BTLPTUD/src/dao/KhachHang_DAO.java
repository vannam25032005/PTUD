package dao;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.NhanVien;

import java.sql.*;
import java.util.ArrayList;

public class KhachHang_DAO {

    /**
     * Phương thức nội bộ chuyển đổi ResultSet thành đối tượng KhachHang.
     */
    private KhachHang createKhachHangFromResultSet(ResultSet rs) throws SQLException {
        String maKH = rs.getString("maKH");
        String hoTenKH = rs.getString("hoTenKH");
        String soDienThoai = rs.getString("soDienThoai");
        String email = rs.getString("email");
        boolean gioiTinh = rs.getBoolean("gioiTinh"); // Đọc giá trị boolean (true/false)
        
        return new KhachHang(maKH, hoTenKH, soDienThoai, email, gioiTinh);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Lấy toàn bộ danh sách Khách hàng từ CSDL.
     * @return ArrayList<KhachHang> danh sách Khách hàng.
     */
    public ArrayList<KhachHang> getAllKhachHang() {
        ArrayList<KhachHang> dsKhachHang = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG";
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                dsKhachHang.add(createKhachHangFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return dsKhachHang;
    }
    
    /**
     * Tìm Khách hàng theo Mã Khách hàng.
     * @param maKH Mã Khách hàng cần tìm.
     * @return KhachHang đối tượng Khách hàng nếu tìm thấy, ngược lại trả về null.
     */
    public KhachHang getKhachHangById(String maKH) {
        KhachHang khachHang = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG WHERE maKH = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    khachHang = createKhachHangFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Khách hàng theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return khachHang;
    }

    /**
     * Tìm Khách hàng theo Số điện thoại.
     * @param soDienThoai Số điện thoại cần tìm.
     * @return KhachHang đối tượng Khách hàng nếu tìm thấy, ngược lại trả về null.
     */
    public KhachHang getKhachHangBySdt(String soDienThoai) {
        KhachHang khachHang = null;
Connection con = ConnectDB.getConnection();
        String sql = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG WHERE soDienThoai = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soDienThoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    khachHang = createKhachHangFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Khách hàng theo SĐT: " + e.getMessage());
            e.printStackTrace();
        }
        return khachHang;
    }
    
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Thêm một Khách hàng mới vào CSDL.
     * @param khachHang Đối tượng KhachHang cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addKhachHang(KhachHang khachHang) {
        Connection con = ConnectDB.getConnection();
        String sql = "INSERT INTO KHACHHANG (maKH, hoTenKH, soDienThoai, email, gioiTinh) VALUES (?, ?, ?, ?, ?)";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, khachHang.getMaKH());
            ps.setString(2, khachHang.getHoTenKH());
            ps.setString(3, khachHang.getSoDienThoai());
            ps.setString(4, khachHang.getEmail());
            ps.setBoolean(5, khachHang.isGioiTinh());
            
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Cập nhật thông tin một Khách hàng trong CSDL.
     * @param khachHang Đối tượng KhachHang với thông tin mới (dựa trên maKH).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateKhachHang(KhachHang khachHang) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE KHACHHANG SET hoTenKH = ?, soDienThoai = ?, email = ?, gioiTinh = ? WHERE maKH = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, khachHang.getHoTenKH());
            ps.setString(2, khachHang.getSoDienThoai());
            ps.setString(3, khachHang.getEmail());
            ps.setBoolean(4, khachHang.isGioiTinh());
            ps.setString(5, khachHang.getMaKH());
            
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Xóa một Khách hàng khỏi CSDL.
* @param maKH Mã Khách hàng cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteKhachHang(String maKH) {
        Connection con = ConnectDB.getConnection();
        String sql = "DELETE FROM KHACHHANG WHERE maKH = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Tìm kiếm Khách hàng theo từ khóa (Mã KH, Tên, SĐT).
     * @param keyword Từ khóa tìm kiếm.
     * @return ArrayList<KhachHang> danh sách Khách hàng phù hợp.
     */
    public ArrayList<KhachHang> searchKhachHang(String keyword) {
        ArrayList<KhachHang> dsKhachHang = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG WHERE maKH LIKE ? OR hoTenKH LIKE ? OR soDienThoai LIKE ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsKhachHang.add(createKhachHangFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return dsKhachHang;
    }

    /**
     * Lấy mã Khách hàng tự động tăng lớn nhất.
     * @return String mã Khách hàng tiếp theo.
     */
    
    public String generateNewMaKH() {
        String newId = "KH001"; // Mặc định là KH001 nếu chưa có KH nào
        String sql = "SELECT TOP 1 maKH FROM KHACHHANG ORDER BY maKH DESC";
        
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String lastId = rs.getString("maKH");
                
                
                String numPart = lastId.substring(2); 
                int number = Integer.parseInt(numPart);
                number++; // Tăng số lên 1 (ví dụ: 2 -> 3)
                
              
                newId = String.format("KH%03d", number); 
            }
            
        } catch (SQLException e) {
e.printStackTrace();
           
        }
        return newId;
    }
 // Trong dao.KhachHang_DAO.java

    /**
     * Thêm Khách hàng mới vào CSDL nếu chưa tồn tại (dựa trên SĐT), 
     * hoặc trả về Khách hàng đã có.
     * @param khachHang Đối tượng KhachHang TẠM THỜI (có hoTen, soDienThoai, gioiTinh, maKH=null).
     * @return KhachHang đã có maKH HỢP LỆ từ CSDL (dù là mới hay cũ).
     * @throws Exception Nếu không thể thêm Khách hàng mới vào CSDL.
     */
    public KhachHang addOrGetKhachHang(KhachHang khachHang) throws Exception {
        // 1. Kiểm tra Khách hàng đã tồn tại chưa
        KhachHang khachHangCSDL = getKhachHangBySdt(khachHang.getSoDienThoai());

        if (khachHangCSDL == null) {
            // 2. KHÁCH HÀNG MỚI: Phát sinh mã
            String maKHNew = generateNewMaKH();

            // Cập nhật maKH vào đối tượng tạm thời để gọi hàm addKhachHang(KhachHang)
            khachHang.setMaKH(maKHNew);
            
            // Cần đảm bảo rằng email KHÔNG phải NOT NULL hoặc đã được set giá trị.
            if (khachHang.getEmail() == null || khachHang.getEmail().isEmpty()) {
                khachHang.setEmail(""); // Đảm bảo email không null nếu CSDL cho phép
            }

            // 3. Thêm vào CSDL
            if (addKhachHang(khachHang)) {
                // Trả về đối tượng vừa được thêm thành công (đã có maKH mới)
                return khachHang; 
            } else {
                // Lỗi xảy ra trong quá trình INSERT
                throw new Exception("Thêm Khách hàng mới vào CSDL thất bại.");
            }
        } else {
            // 4. KHÁCH HÀNG CŨ: Cập nhật tên nếu cần
            if (!khachHangCSDL.getHoTenKH().equals(khachHang.getHoTenKH())) {
                khachHangCSDL.setHoTenKH(khachHang.getHoTenKH());
                // Gọi hàm cập nhật tên (cần thêm nếu chưa có, hoặc dùng updateKhachHang)
                // Giả định bạn có hàm updateHoTen(String maKH, String hoTen)
                // Vì chưa có, ta dùng hàm updateKhachHang() sẵn có
                updateKhachHang(khachHangCSDL); 
            }
            return khachHangCSDL;
        }
    }
    public Object[] getKhachHangRowByMa(String maKH) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT kh.maKH, kh.hoTenKH, ISNULL(ttv.loaiHang, N'Chưa có') AS loaiHang, " +
                     "kh.soDienThoai, kh.gioiTinh, kh.email " +
                     "FROM KHACHHANG kh " +
                     "LEFT JOIN THETHANHVIEN ttv ON kh.maKH = ttv.maKH " +
                     "WHERE kh.maKH = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object[] row = new Object[6];
                    row[0] = rs.getString("maKH");
                    row[1] = rs.getString("hoTenKH");
                    row[2] = rs.getString("loaiHang");
                    row[3] = rs.getString("soDienThoai");
                    row[4] = rs.getBoolean("gioiTinh") ? "Nam" : "Nữ";
                    row[5] = rs.getString("email");
                    return row;
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace(); // <-- XÓA DÒNG NÀY
            throw e; // Chỉ ném lỗi lên
        }
        return null; // Không tìm thấy
    }
    public KhachHang getKhachHangByMa(String maKH) throws SQLException {
        KhachHang kh = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT * FROM KHACHHANG WHERE maKH = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hoTen = rs.getString("hoTenKH");
                    String sdt = rs.getString("soDienThoai");
                    String email = rs.getString("email");
                    boolean gioiTinh = rs.getBoolean("gioiTinh");

                

                    kh = new KhachHang(maKH, hoTen, sdt, email, gioiTinh);
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace(); // <-- XÓA DÒNG NÀY
            throw e; // Chỉ ném lỗi lên
        }
        return kh;
    }

    public ArrayList<Object[]> getAllKhachHangForTable() throws SQLException {
        ArrayList<Object[]> list = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT kh.maKH, kh.hoTenKH, ISNULL(ttv.loaiHang, N'Chưa có') AS loaiHang, " +
                     "kh.soDienThoai, kh.gioiTinh, kh.email " +
                     "FROM KHACHHANG kh " +
                     "LEFT JOIN THETHANHVIEN ttv ON kh.maKH = ttv.maKH";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getString("maKH");
                row[1] = rs.getString("hoTenKH");
                row[2] = rs.getString("loaiHang");
                row[3] = rs.getString("soDienThoai");
                row[4] = rs.getBoolean("gioiTinh") ? "Nam" : "Nữ";
                row[5] = rs.getString("email");
                list.add(row);
            }
        } catch (SQLException e) {
            // e.printStackTrace(); // <-- XÓA DÒNG NÀY
            throw e; // Chỉ ném lỗi lên
        }
        return list;
    }
    public boolean updateKhachHangPK(String maKHCU, KhachHang khMoi) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sqlKH = "UPDATE KHACHHANG SET maKH = ?, hoTenKH = ?, soDienThoai = ?, email = ?, gioiTinh = ?, maNV = ? " +
                       "WHERE maKH = ?";
        String sqlTTV = "UPDATE THETHANHVIEN SET maKH = ? WHERE maKH = ?";

        try {
            con.setAutoCommit(false); // Bắt đầu Transaction

            try (PreparedStatement psTTV = con.prepareStatement(sqlTTV)) {
                psTTV.setString(1, khMoi.getMaKH());
                psTTV.setString(2, maKHCU);
                psTTV.executeUpdate();
            }

            try (PreparedStatement psKH = con.prepareStatement(sqlKH)) {
                psKH.setString(1, khMoi.getMaKH());
                psKH.setString(2, khMoi.getHoTenKH());
                psKH.setString(3, khMoi.getSoDienThoai());
                psKH.setString(4, khMoi.getEmail());
                psKH.setBoolean(5, khMoi.isGioiTinh());
              
                psKH.setString(7, maKHCU);

                int rowsAffected = psKH.executeUpdate();
                if (rowsAffected > 0) {
                    con.commit();
                    return true;
                } else {
                    con.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            con.rollback();
            // e.printStackTrace(); // <-- XÓA DÒNG NÀY
            throw e; // Chỉ ném lỗi lên
        } finally {
            con.setAutoCommit(true);
        }
    }
}
