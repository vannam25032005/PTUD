package dao;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.TheThanhVien;

import java.sql.*;
import java.util.ArrayList;

public class TheThanhVien_DAO {

  
    public TheThanhVien layTheTheoMaKH(String maKH) throws SQLException {
        TheThanhVien theTV = null;
        Connection ketNoi = ConnectDB.getConnection(); 
        String cauSQL = "SELECT maThe, diemTichLuy, loaiHang FROM THETHANHVIEN WHERE maKH = ?"; 

        try (PreparedStatement stmt = ketNoi.prepareStatement(cauSQL)) {
            stmt.setString(1, maKH); // Gán tham số maKH
            try (ResultSet kq = stmt.executeQuery()) {
                if (kq.next()) { // Nếu tìm thấy kết quả
                    String maThe = kq.getString("maThe");
                    int diem = kq.getInt("diemTichLuy");
                    String loaiHang = kq.getString("loaiHang");

                    // Tạo đối tượng KhachHang chỉ với mã (vì chỉ cần mã KH cho constructor TheThanhVien)
                    KhachHang kh = new KhachHang(maKH);

                    // Sử dụng constructor của TheThanhVien (Entity sẽ tự tính toán lại loại hạng nếu cần)
                    theTV = new TheThanhVien(maThe, kh, diem, loaiHang);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thẻ thành viên theo mã KH '" + maKH + "': " + e.getMessage());
           
            throw e;
        }
      
        return theTV; 
    }

   
    public boolean themTheThanhVien(TheThanhVien theTV) throws SQLException {
        Connection ketNoi = ConnectDB.getConnection();
        String cauSQL = "INSERT INTO THETHANHVIEN (maThe, maKH, diemTichLuy, loaiHang) VALUES (?, ?, ?, ?)";
        int soLuongAnhHuong = 0;

        try (PreparedStatement stmt = ketNoi.prepareStatement(cauSQL)) {
            stmt.setString(1, theTV.getMaThe());
            stmt.setString(2, theTV.getKhachHang().getMaKH()); // Lấy mã KH từ đối tượng KhachHang bên trong
            stmt.setInt(3, theTV.getDiemTichLuy());
            stmt.setString(4, theTV.getLoaiHang()); // Loại hạng đã được Entity tính
            soLuongAnhHuong = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thẻ thành viên mới (Mã thẻ: " + theTV.getMaThe() + "): " + e.getMessage());
            // Ném lỗi lên để GUI xử lý (ví dụ: thông báo lỗi trùng khóa)
            throw e;
        }
        return soLuongAnhHuong > 0;
    }

   
    public boolean capNhatTheThanhVien(TheThanhVien theTV) throws SQLException {
        Connection ketNoi = ConnectDB.getConnection();
        String cauSQL = "UPDATE THETHANHVIEN SET loaiHang = ?, diemTichLuy = ? WHERE maThe = ?";
        int soLuongAnhHuong = 0;

        try (PreparedStatement stmt = ketNoi.prepareStatement(cauSQL)) {
            stmt.setString(1, theTV.getLoaiHang());
            stmt.setInt(2, theTV.getDiemTichLuy()); 
            stmt.setString(3, theTV.getMaThe());     
            soLuongAnhHuong = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thẻ thành viên '" + theTV.getMaThe() + "': " + e.getMessage());
            throw e; // Ném lỗi lên
        }
        return soLuongAnhHuong > 0;
    }

    
    public boolean xoaTheThanhVienTheoMaKH(String maKH) throws SQLException {
        Connection ketNoi = ConnectDB.getConnection();
        String cauSQL = "DELETE FROM THETHANHVIEN WHERE maKH = ?";
        try (PreparedStatement stmt = ketNoi.prepareStatement(cauSQL)) {
            stmt.setString(1, maKH);
            stmt.executeUpdate(); 
          
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa thẻ thành viên của KH '" + maKH + "': " + e.getMessage());
            throw e; 
        }
    }

  
    public String phatSinhMaThe() throws SQLException {
        Connection ketNoi = ConnectDB.getConnection();
        // Chỉ lấy MAX của các mã bắt đầu bằng 'TTV' để tránh lỗi định dạng
        String cauSQL = "SELECT MAX(maThe) FROM THETHANHVIEN WHERE maThe LIKE 'TTV%'";
        String maTheMoi = "TTV001"; // Mã mặc định nếu chưa có mã 'TTV%' nào

        try (PreparedStatement stmt = ketNoi.prepareStatement(cauSQL);
             ResultSet kq = stmt.executeQuery()) {

            if (kq.next()) {
                String maTheLonNhat = kq.getString(1); 
                if (maTheLonNhat != null) { 
                    try {
                        String phanSo = maTheLonNhat.substring(3);
                        int so = Integer.parseInt(phanSo);                  
                        so++;
                        maTheMoi = String.format("TTV%03d", so);
                    } catch (NumberFormatException | StringIndexOutOfBoundsException eLoiDinhDang) {
                        System.err.println("Lỗi định dạng mã thẻ thành viên tối đa trong CSDL: " + maTheLonNhat);
                      
                        maTheMoi = "TTV001"; 
                    }
                }
              
            }
        } catch (SQLException eLoiSQL) {
            System.err.println("Lỗi SQL khi phát sinh mã thẻ thành viên mới: " + eLoiSQL.getMessage());
            throw eLoiSQL; 
        }
       

        return maTheMoi; 
    }
}