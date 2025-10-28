package dao;

import connectDB.ConnectDB;
import entity.CTBanDat; // Hoặc CTBanAn nếu bạn đổi tên Entity
import entity.Ban;       // SỬA: Import Ban
import entity.MonAn;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;


public class CT_BanDat_DAO { 

   
    private Ban_DAO banDAO = new Ban_DAO(); 
    private MonAn_DAO monAnDAO = new MonAn_DAO();
    
   
    
    private CTBanDat createCTBanFromResultSet(ResultSet rs) throws SQLException { 
    
        String maBan = rs.getString("maBan"); 
        String maMon = rs.getString("maMon");
        int soLuong = rs.getInt("soLuong");
        
        // 2. Tải các Entity Khóa ngoại đầy đủ
        Ban ban = banDAO.getBanById(maBan); // SỬA: Tải Ban
        MonAn monAn = monAnDAO.getMonAnById(maMon);
        
        // 3. Trả về Entity CTBanDat (hoặc CTBanAn)
        return new CTBanDat(ban, monAn, soLuong); 
    }

  
    
    public Map<String, Integer> layCTBan(String maBan) { 
        Map<String, Integer> chiTiet = new LinkedHashMap<>();
        
  
        String sql = "SELECT CT.soLuong, MA.tenMon " +
                     "FROM CT_BANDAT CT JOIN MONAN MA ON CT.maMon = MA.maMon WHERE CT.maBan = ?"; 
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maBan); 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chiTiet.put(rs.getString("tenMon"), rs.getInt("soLuong"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải chi tiết bàn ăn: " + e.getMessage()); 
            e.printStackTrace();
        }
        return chiTiet;
    }

 
    public boolean themCTBan(CTBanDat ct) throws SQLException { 
        
        String maBan = ct.getBan().getMaBan(); 
        String maMon = ct.getMonAn().getMaMonAn();
        int soLuong = ct.getSoLuong();
        
        
        String sql = "INSERT INTO CT_BANDAT (maBan, maMon, soLuong) VALUES (?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maBan); 
            ps.setString(2, maMon);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
             System.err.println("Lỗi khi thêm chi tiết bàn ăn: " + e.getMessage()); 
             throw e; 
        }
    }

   
    public boolean xoaTatCaCTBan(String maBan) throws SQLException {
    
        String sql = "DELETE FROM CT_BANDAT WHERE maBan = ?"; 
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maBan); 
            return ps.executeUpdate() >= 0; 
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa chi tiết bàn ăn: " + e.getMessage()); 
            throw e;
        }
    }
}