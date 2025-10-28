package dao;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.CT_HoaDon;
import entity.NhanVien;
import entity.Ban;
import entity.KhuyenMai;
import entity.BanDat;
import entity.TheThanhVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    
    private final CTHoaDon_DAO ctHoaDonDAO = new CTHoaDon_DAO();

  
    
    private HoaDon createHoaDonFromResultSet(ResultSet rs) throws Exception {
    
        String maHD = rs.getString("maHD");
        String maThe = rs.getString("maThe");
        String maNV = rs.getString("maNV");
        String maBan = rs.getString("maBan");
        String maDatBan = rs.getString("maDatBan");
        String maKM = rs.getString("maKM");
       
        Timestamp ngayLapTs = rs.getTimestamp("ngayLap");
        LocalDateTime ngayLap = ngayLapTs != null ? ngayLapTs.toLocalDateTime() : LocalDateTime.now(); 
        
        // Khởi tạo HoaDon với constructor tối thiểu
        HoaDon hd = new HoaDon(maHD);
        hd.setNgayLap(ngayLap);
        
        // Gán các Entity Khóa ngoại (dùng Entity ảo, chỉ cần mã)
        hd.setTheThanhVien(maThe != null ? new TheThanhVien(maThe) : null);
        hd.setNhanVien(new NhanVien(maNV));
        hd.setBan(new Ban(maBan));
        hd.setBanDat(maDatBan != null ? new BanDat(maDatBan) : null);
        hd.setKhuyenMai(maKM != null ? new KhuyenMai(maKM) : null);
        
     
        return hd;
    }

   
    public boolean themHoaDon(HoaDon hd) {
        Connection con = null;
        boolean success = false;
        
       
        String sqlHD = "INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction
            
            // 1. THÊM HÓA ĐƠN CHÍNH
            try (PreparedStatement psHD = con.prepareStatement(sqlHD)) {
                psHD.setString(1, hd.getMaHoaDon());
                psHD.setString(2, hd.getTheThanhVien() != null ? hd.getTheThanhVien().getMaThe() : null);
                psHD.setString(3, hd.getNhanVien().getMaNV());
                psHD.setString(4, hd.getBan().getMaBan());
                psHD.setString(5, hd.getBanDat() != null ? hd.getBanDat().getMaDatBan() : null);
                psHD.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
                psHD.setTimestamp(7, Timestamp.valueOf(hd.getNgayLap()));
                psHD.setString(8, "Chờ thanh toán"); // ⚠️ BỔ SUNG TRẠNG THÁI MẶC ĐỊNH
                
                if (psHD.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }
            
            // 2. THÊM CHI TIẾT HÓA ĐƠN
            // NOTE: Logic này chỉ đúng khi bạn tạo HD mới với DSCT rỗng (vì đang gọi từ Xác nhận đơn)
            if (hd.getDanhSachChiTietHoaDon() != null) {
                for (CT_HoaDon ct : hd.getDanhSachChiTietHoaDon()) {
                   
                    if (!ctHoaDonDAO.themCTHoaDon(ct, con)) { 
                        con.rollback();
                        return false; 
                    }
                }
            }
            
            con.commit();
            success = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
                con = null; // Thiết lập lại con về null
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }
    
   
    public HoaDon layHoaDonChuaThanhToanTheoBan(String maBan, String maNV) {
        HoaDon hd = null;
        
        String sql = "SELECT TOP 1 maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap " +
                "FROM HOADON WHERE maBan = ? " +
                "ORDER BY ngayLap DESC"; 

   try (Connection con = ConnectDB.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

       ps.setString(1, maBan);
            

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hd = createHoaDonFromResultSet(rs);
                    ArrayList<CT_HoaDon> dsCT = ctHoaDonDAO.layDSCTHoaDonTheoMaHD(hd.getMaHoaDon());
                    hd.setDanhSachChiTietHoaDon(dsCT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hd;
    }

   
    public String layMaHDTiepTheo() {
        String maHD = "HD0001";
        String sql = "SELECT MAX(maHD) AS MaxMaHD FROM HOADON";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getString("MaxMaHD") != null) {
                String maxMaHD = rs.getString("MaxMaHD");
                int num = Integer.parseInt(maxMaHD.substring(2)) + 1;
                maHD = String.format("HD%04d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maHD;
    }
    
  
}