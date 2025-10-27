package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhuyenMai;

public class KhuyenMai_DAO {

	/**
	 * Chuyển đổi từ ResultSet thành đối tượng KhuyenMai.
	 */
	private KhuyenMai createKhuyenMaiFromResultSet(ResultSet rs) throws Exception {
		// Dùng tên cột DB chính xác:
		String maKM = rs.getString("maKM");
		String tenKM = rs.getString("tenKM");
		String moTa = rs.getString("moTa");
		double phanTramGiam = rs.getDouble("phanTramGiam");

		// Lấy ngày bắt đầu và kết thúc:
		LocalDate ngayBD = rs.getDate("ngayBatDau").toLocalDate();
		LocalDate ngayKT = rs.getDate("ngayKetThuc").toLocalDate();

		// SỬ DỤNG CONSTRUCTOR MỚI (6 tham số)
		return new KhuyenMai(maKM, tenKM, moTa, phanTramGiam, ngayBD, ngayKT);
	}

	// 1. Lấy tất cả khuyến mãi - ĐÃ SỬA LỖI QUẢN LÝ CONNECTION
	public ArrayList<KhuyenMai> getAllKhuyenMai() {
		ArrayList<KhuyenMai> dsKhuyenMai = new ArrayList<>();
		String sql = "SELECT * FROM KHUYENMAI";

		// Lấy Connection ra khỏi Try-with-resources để giữ kết nối Singleton mở
		Connection con = ConnectDB.getConnection(); 

		// CHỈ SỬ DỤNG TRY-WITH-RESOURCES CHO PreparedStatement và ResultSet
		try (PreparedStatement ps = con.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) { 

			while (rs.next()) {
				KhuyenMai km = createKhuyenMaiFromResultSet(rs);
				dsKhuyenMai.add(km);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dsKhuyenMai;
	}

	// 2. Thêm khuyến mãi mới - ĐÃ SỬA LỖI QUẢN LÝ CONNECTION
	public boolean themKhuyenMai(KhuyenMai km) {
		int n = 0;
		// Câu lệnh SQL: KHUYENMAI (maKM, tenKM, moTa, phanTramGiam, ngayBatDau, ngayKetThuc)
		String sql = "INSERT INTO KHUYENMAI (maKM, tenKM, moTa, phanTramGiam, ngayBatDau, ngayKetThuc) VALUES (?, ?, ?, ?, ?, ?)";

		// Lấy Connection ra khỏi Try-with-resources
		Connection con = ConnectDB.getConnection(); 
        
		try (PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, km.getMaKM());
			ps.setString(2, km.getTenKM());
			ps.setString(3, km.getMoTa());
			ps.setDouble(4, km.getPhanTramGiam());

			// Chuyển đổi từ java.time.LocalDate sang java.sql.Date
			ps.setDate(5, Date.valueOf(km.getNgayBatDau()));
			ps.setDate(6, Date.valueOf(km.getNgayKetThuc()));

			n = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n > 0;
	}

	// 3. Cập nhật (Sửa) khuyến mãi - ĐÃ SỬA LỖI QUẢN LÝ CONNECTION
	public boolean suaKhuyenMai(KhuyenMai km) {
		int n = 0;
		// Thêm moTa vào SET
		String sql = "UPDATE KHUYENMAI SET tenKM = ?, moTa = ?, phanTramGiam = ?, ngayBatDau = ?, ngayKetThuc = ? WHERE maKM = ?";

		// Lấy Connection ra khỏi Try-with-resources
		Connection con = ConnectDB.getConnection(); 

		try (PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, km.getTenKM());
			ps.setString(2, km.getMoTa()); // moTa (MỚI)
			ps.setDouble(3, km.getPhanTramGiam());

			ps.setDate(4, Date.valueOf(km.getNgayBatDau()));
			ps.setDate(5, Date.valueOf(km.getNgayKetThuc()));

			ps.setString(6, km.getMaKM()); // WHERE maKM

			n = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n > 0;
	}

	// 4. Xóa khuyến mãi theo mã - ĐÃ SỬA LỖI QUẢN LÝ CONNECTION
	public boolean xoaKhuyenMai(String maKhuyenMai) {
		int n = 0;
		String sql = "DELETE FROM KHUYENMAI WHERE maKM = ?";

		// Lấy Connection ra khỏi Try-with-resources
		Connection con = ConnectDB.getConnection(); 

		try (PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maKhuyenMai);

			n = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n > 0;
	}

	// 5. Tìm khuyến mãi theo mã - ĐÃ SỬA LỖI QUẢN LÝ CONNECTION
	public KhuyenMai timKhuyenMaiTheoMa(String id) {
		KhuyenMai km = null;
		String sql = "SELECT * FROM KHUYENMAI WHERE maKM = ?";

		// Lấy Connection ra khỏi Try-with-resources
		Connection con = ConnectDB.getConnection(); 
		
		// Chỉ PreparedStatement được đóng tự động
		try (PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, id);

			// ResultSet được đóng trong khối try riêng
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					km = createKhuyenMaiFromResultSet(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return km;
	}

	// 6. Tìm danh sách khuyến mãi theo tên gần đúng - ĐÃ SỬA LỖI QUẢN LÝ CONNECTION
	public ArrayList<KhuyenMai> timKhuyenMaiTheoTen(String ten) {
		ArrayList<KhuyenMai> dsKhuyenMai = new ArrayList<>();
		String sql = "SELECT * FROM KHUYENMAI WHERE tenKM LIKE ?";

		// Lấy Connection ra khỏi Try-with-resources
		Connection con = ConnectDB.getConnection(); 

		try (PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, "%" + ten + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					KhuyenMai km = createKhuyenMaiFromResultSet(rs);
					dsKhuyenMai.add(km);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dsKhuyenMai;
	}
}