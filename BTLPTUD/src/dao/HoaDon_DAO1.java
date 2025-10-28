//package dao;
//
//import connectDB.ConnectDB;
//import entity.HoaDon;
//import entity.CT_HoaDon;
//import entity.NhanVien;
//import entity.Ban;
//import entity.KhuyenMai;
//import entity.BanDat;
//import entity.TheThanhVien;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//
//public class HoaDon_DAO {
//    
//    private final CTHoaDon_DAO ctHoaDonDAO = new CTHoaDon_DAO();
//    
//    /**
//     * Hàm chuyển đổi ResultSet thành Entity HoaDon (chưa bao gồm chi tiết)
//     */
//    private HoaDon createHoaDonFromResultSet(ResultSet rs) throws Exception {
//        String maHD = rs.getString("maHD");
//        String maThe = rs.getString("maThe");
//        String maNV = rs.getString("maNV");
//        String maBan = rs.getString("maBan");
//        String maDatBan = rs.getString("maDatBan");
//        String maKM = rs.getString("maKM");
//        LocalDateTime ngayLap = rs.getTimestamp("ngayLap").toLocalDateTime();
//        
//        HoaDon hd = new HoaDon(maHD);
//        hd.setNgayLap(ngayLap);
//        
//        // Gán các Entity Khóa ngoại (dùng Entity ảo, chỉ cần mã)
//        hd.setTheThanhVien(maThe != null ? new TheThanhVien(maThe) : null);
//        hd.setNhanVien(new NhanVien(maNV)); // NOT NULL
//        hd.setBan(new Ban(maBan));         // NOT NULL
//        hd.setBanDat(maDatBan != null ? new BanDat(maDatBan) : null);
//        hd.setKhuyenMai(maKM != null ? new KhuyenMai(maKM) : null);
//        
//        return hd;
//    }
//
//    /**
//     * Thêm một hóa đơn mới và tất cả chi tiết hóa đơn đi kèm.
//     * Sử dụng Transaction (phương pháp JDBC tốt nhất).
//     * @param hd Đối tượng HoaDon cần thêm.
//     * @return true nếu thêm thành công cả Hóa đơn và Chi tiết.
//     */
//    public boolean themHoaDon(HoaDon hd) {
//        Connection con = null;
//        boolean success = false;
//        
//        // SQL cho bảng HOADON (Không có cột tongTien, trangThaiHD)
//        String sqlHD = "INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap) VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//        try {
//            con = ConnectDB.getConnection();
//            con.setAutoCommit(false); // Bắt đầu Transaction
//            
//            // 1. THÊM HÓA ĐƠN CHÍNH
//            try (PreparedStatement psHD = con.prepareStatement(sqlHD)) {
//                psHD.setString(1, hd.getMaHoaDon());
//                
//                // Khóa ngoại có thể NULL (TheThanhVien, BanDat, KhuyenMai)
//                psHD.setString(2, hd.getTheThanhVien() != null ? hd.getTheThanhVien().getMaThe() : null);
//                
//                // Khóa ngoại BẮT BUỘC NOT NULL (NhanVien, Ban)
//                psHD.setString(3, hd.getNhanVien().getMaNV());
//                psHD.setString(4, hd.getBan().getMaBan());
//                
//                // Khóa ngoại có thể NULL
//                psHD.setString(5, hd.getBanDat() != null ? hd.getBanDat().getMaDatBan() : null);
//                psHD.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
//                
//                // Ngày lập
//                psHD.setTimestamp(7, Timestamp.valueOf(hd.getNgayLap())); 
//                
//                if (psHD.executeUpdate() <= 0) {
//                    con.rollback();
//                    return false; // Thêm hóa đơn thất bại
//                }
//            }
//            
//            // 2. THÊM CHI TIẾT HÓA ĐƠN
//            if (hd.getDanhSachChiTietHoaDon() != null) {
//                for (CT_HoaDon ct : hd.getDanhSachChiTietHoaDon()) {
//                    if (!ctHoaDonDAO.themCTHoaDon(ct, con)) {
//                        con.rollback(); // Nếu bất kỳ chi tiết nào thất bại, hủy toàn bộ
//                        return false; 
//                    }
//                }
//            }
//            
//            con.commit(); // Hoàn tất Transaction
//            success = true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            try {
//                if (con != null) con.rollback(); // Rollback nếu có lỗi
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        } catch (Exception e) {
//             e.printStackTrace();
//        } finally {
//            try {
//                if (con != null) con.close();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return success;
//    }
//    
//    /**
//     * Lấy một hóa đơn dựa trên mã hóa đơn.
//     * @param maHD Mã hóa đơn.
//     * @return HoaDon hoàn chỉnh (bao gồm danh sách chi tiết).
//     */
//    public HoaDon layHoaDonTheoMa(String maHD) {
//        HoaDon hd = null;
//        String sql = "SELECT maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap FROM HOADON WHERE maHD = ?";
//
//        try (Connection con = ConnectDB.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//            
//            ps.setString(1, maHD);
//            
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    hd = createHoaDonFromResultSet(rs);
//                    
//                    // Lấy danh sách chi tiết hóa đơn và gán vào Entity
//                    ArrayList<CT_HoaDon> dsCT = ctHoaDonDAO.layDSCTHoaDonTheoMaHD(maHD);
//                    hd.setDanhSachChiTietHoaDon(dsCT);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return hd;
//    }
//}
package dao;

