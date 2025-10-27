	package dao;
	
	import java.sql.Connection;
	// SỬA: Dùng java.sql.Date và java.util.Date
	import java.sql.Date; 
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.sql.Statement;
	import java.util.ArrayList;
	import java.util.List;
	
	
	import connectDB.ConnectDB; 
	import entity.NhanVien;
	import entity.QuanLy; // Import QuanLy
	
	public class NhanVien_DAO {
	
	    /**
	     * Trích xuất thông tin một Nhân Viên từ ResultSet
	     * @param rs ResultSet chứa dữ liệu nhân viên
	     * @return một đối tượng NhanVien
	     * @throws SQLException
	     */
	    private NhanVien extractNhanVienFromResultSet(ResultSet rs) throws SQLException {
	        String maNV = rs.getString("maNV");
	        String hoTen = rs.getString("hoTen");
	        String cccd = rs.getString("CCCD");
	        String sdt = rs.getString("soDienThoai");
	        String email = rs.getString("email");
	        
	        // SỬA: Chuyển java.sql.Date sang java.util.Date
	        java.util.Date ngaySinh = null;
	        Date sqlNgaySinh = rs.getDate("ngaySinh");
	        if (sqlNgaySinh != null) {
	            ngaySinh = new java.util.Date(sqlNgaySinh.getTime()); // Chuyển đổi trực tiếp
	        }
	        
	        // SỬA: Đọc kiểu BIT thành Boolean (nullable nếu cột cho phép NULL)
	        Boolean gioiTinh = rs.getBoolean("gioiTinh"); 
	        if (rs.wasNull()) {
	             gioiTinh = null; // Hoặc xử lý khác nếu cột là NOT NULL
	        }
	
	        String trangThai = rs.getString("trangThai");
	        
	        // SỬA: Xử lý QuanLy (khóa ngoại) bằng cách đọc maQL
	        String maQL = rs.getString("maQL"); 
	        QuanLy quanLy = null;
	        if (maQL != null && !maQL.trim().isEmpty()) {
	            // Chỉ cần mã QL để tạo đối tượng QuanLy đơn giản
	            quanLy = new QuanLy(maQL); // Giả định có constructor QuanLy(String maQL)
	        }
	
	        // SỬA: Gọi constructor của NhanVien phù hợp (dùng java.util.Date và QuanLy)
	        // Lưu ý: Cần đảm bảo thứ tự tham số khớp với constructor trong NhanVien.java
	        // Constructor cũ: NhanVien(String maNV, String hoTen, String cCCD, String soDienThoai, String email, Boolean gioiTinh, Date ngaySinh, String trangThai, String maQL)
	        // Cần sửa constructor Entity hoặc DAO để khớp. Giả định Entity đã sửa:
	        // NhanVien(String maNV, String hoTen, String cCCD, String soDienThoai, String email, Boolean gioiTinh, Date ngaySinh, String trangThai, QuanLy quanLy)
	        return new NhanVien(maNV, hoTen, cccd, sdt, email, gioiTinh, ngaySinh, trangThai, quanLy); 
	    }
	
	    /**
	     * Lấy danh sách tất cả nhân viên từ CSDL
	     * @return List<NhanVien>
	     */
	    public List<NhanVien> getAllNhanVien() {
	        List<NhanVien> dsNV = new ArrayList<>();
	        // SỬA: Bỏ cột chucVu khỏi SELECT
	        String sql = "SELECT maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL FROM NHANVIEN";
	        
	        try (Connection con = ConnectDB.getInstance().getConnection();
	             Statement stmt = con.createStatement();
	             ResultSet rs = stmt.executeQuery(sql)) {
	
	            while (rs.next()) {
	                NhanVien nv = extractNhanVienFromResultSet(rs);
	                dsNV.add(nv);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace(); 
	        }
	        return dsNV;
	    }
	
	    /**
	     * Thêm nhân viên mới vào CSDL. Ném SQLException để lớp gọi xử lý.
	     * @param nv đối tượng NhanVien cần thêm
	     * @return true nếu thêm thành công
	     * @throws SQLException nếu có lỗi CSDL
	     */
	    public boolean addNhanVien(NhanVien nv) throws SQLException { 
	        // SỬA: Bỏ cột chucVu khỏi INSERT
	        String sql = "INSERT INTO NHANVIEN (maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL) " +
	                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; // Chỉ còn 9 tham số
	        
	        try (Connection con = ConnectDB.getInstance().getConnection();
	             PreparedStatement stmt = con.prepareStatement(sql)) {
	             
	            stmt.setString(1, nv.getMaNV());
	            stmt.setString(2, nv.getHoTen());
	            stmt.setString(3, nv.getCCCD());
	            stmt.setString(4, nv.getSoDienThoai());
	            stmt.setString(5, nv.getEmail());
	            
	            // SỬA: Chuyển java.util.Date sang java.sql.Date
	            if (nv.getNgaySinh() != null) {
	                stmt.setDate(6, new java.sql.Date(nv.getNgaySinh().getTime())); 
	            } else {
	                stmt.setNull(6, java.sql.Types.DATE);
	            }
	            
	            // SỬA: Xử lý Boolean (nullable nếu cần)
	            if (nv.getGioiTinh() != null) {
	                 stmt.setBoolean(7, nv.getGioiTinh());
	            } else {
	                 stmt.setNull(7, java.sql.Types.BIT); // Hoặc ném lỗi nếu giới tính là bắt buộc
	            }
	            
	            stmt.setString(8, nv.getTrangThai()); // Tham số thứ 8
	            
	            // Xử lý maQL (tham số thứ 9)
	            if (nv.getQuanLy() != null && nv.getQuanLy().getMaQL() != null) {
	                stmt.setString(9, nv.getQuanLy().getMaQL());
	            } else {
	                stmt.setNull(9, java.sql.Types.NVARCHAR); 
	            }
	
	            int n = stmt.executeUpdate();
	            return n > 0; 
	        }
	    }
	
	    /**
	     * Cập nhật thông tin nhân viên. Ném SQLException để lớp gọi xử lý.
	     * @param nv đối tượng NhanVien cần cập nhật
	     * @return true nếu cập nhật thành công
	     * @throws SQLException nếu có lỗi CSDL
	     */
	    public boolean updateNhanVien(NhanVien nv) throws SQLException {
	        // SỬA: Bỏ cột chucVu khỏi UPDATE
	        String sql = "UPDATE NHANVIEN SET hoTen = ?, CCCD = ?, soDienThoai = ?, " +
	                       "email = ?, ngaySinh = ?, gioiTinh = ?, trangThai = ?, maQL = ? " + // Chỉ còn 8 cột SET
	                       "WHERE maNV = ?";
	        
	        try (Connection con = ConnectDB.getInstance().getConnection();
	             PreparedStatement stmt = con.prepareStatement(sql)) {
	             
	            stmt.setString(1, nv.getHoTen());
	            stmt.setString(2, nv.getCCCD());
	            stmt.setString(3, nv.getSoDienThoai());
	            stmt.setString(4, nv.getEmail());
	            
	            // SỬA: Chuyển java.util.Date sang java.sql.Date
	            if (nv.getNgaySinh() != null) {
	                stmt.setDate(5, new java.sql.Date(nv.getNgaySinh().getTime()));
	            } else {
	                stmt.setNull(5, java.sql.Types.DATE);
	            }
	            
	            // SỬA: Xử lý Boolean
	             if (nv.getGioiTinh() != null) {
	                 stmt.setBoolean(6, nv.getGioiTinh());
	            } else {
	                 stmt.setNull(6, java.sql.Types.BIT);
	            }
	            
	            stmt.setString(7, nv.getTrangThai()); // Tham số thứ 7
	            
	            // Xử lý maQL (tham số thứ 8)
	            if (nv.getQuanLy() != null && nv.getQuanLy().getMaQL() != null) {
	                stmt.setString(8, nv.getQuanLy().getMaQL());
	            } else {
	                stmt.setNull(8, java.sql.Types.NVARCHAR); 
	            }
	            
	            stmt.setString(9, nv.getMaNV()); // WHERE clause (tham số thứ 9)
	
	            int n = stmt.executeUpdate();
	            return n > 0; 
	        }
	    }
	
	    /**
	     * Tìm kiếm nhân viên theo Mã NV hoặc Tên NV (tìm kiếm gần đúng)
	     * @param keyword từ khóa tìm kiếm
	     * @return List<NhanVien>
	     */
	    public List<NhanVien> findNhanVien(String keyword) {
	        List<NhanVien> dsNV = new ArrayList<>();
	        // SỬA: Bỏ cột chucVu khỏi SELECT
	        String sql = "SELECT maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL " + 
	                     "FROM NHANVIEN WHERE maNV LIKE ? OR hoTen LIKE ?";
	        
	        try (Connection con = ConnectDB.getInstance().getConnection();
	             PreparedStatement stmt = con.prepareStatement(sql)) {
	             
	            String likeKeyword = "%" + keyword + "%";
	            stmt.setString(1, likeKeyword);
	            stmt.setString(2, likeKeyword);
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    NhanVien nv = extractNhanVienFromResultSet(rs);
	                    dsNV.add(nv);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return dsNV;
	    }
	    
	    /**
	     * Tìm một nhân viên theo mã (tìm chính xác)
	     * @param maNV Mã nhân viên
	     * @return NhanVien nếu tìm thấy, null nếu không
	     */
	    public NhanVien findNhanVienById(String maNV) {
	         // SỬA: Bỏ cột chucVu khỏi SELECT
	        String sql = "SELECT maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL " + 
	                     "FROM NHANVIEN WHERE maNV = ?";
	        
	        try (Connection con = ConnectDB.getInstance().getConnection();
	             PreparedStatement stmt = con.prepareStatement(sql)) {
	             
	            stmt.setString(1, maNV);
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return extractNhanVienFromResultSet(rs);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null; // Không tìm thấy
	    }
	    
	    // Bạn có thể thêm các hàm khác nếu cần
	}