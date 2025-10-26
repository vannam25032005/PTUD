package dao;

import connectDB.ConnectDB;
import entity.CT_HoaDon;
import entity.HoaDon;
import entity.MonAn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CTHoaDon_DAO {

    /**
     * Thêm một Chi tiết hóa đơn mới vào cơ sở dữ liệu.
     * Sử dụng Connection (con) được truyền vào để đảm bảo chạy trong cùng một Transaction.
     * @param ct Chi tiết hóa đơn cần thêm.
     * @param con Connection dùng chung cho Transaction của HoaDon.
     * @return true nếu thêm thành công.
     */
    public boolean themCTHoaDon(CT_HoaDon ct, Connection con) throws SQLException {
        // Cần đảm bảo maHD và maMon không NULL
        if (ct.getHoaDon() == null || ct.getMonAn() == null) {
            throw new IllegalArgumentException("Hóa đơn và Món ăn không được rỗng trong chi tiết hóa đơn.");
        }
        
        String sql = "INSERT INTO CT_HOADON (maHD, maMon, soLuong) VALUES (?, ?, ?)";
        // KHÔNG đóng PreparedStatement hoặc Connection ở đây
        try (PreparedStatement ps = con.prepareStatement(sql)) { 
            ps.setString(1, ct.getHoaDon().getMaHoaDon());
            ps.setString(2, ct.getMonAn().getMaMonAn());
            ps.setInt(3, ct.getSoLuong());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lấy danh sách chi tiết hóa đơn (CT_HoaDon) dựa trên mã hóa đơn.
     * Cần JOIN với MONAN để lấy giaMonAn cho việc tính toán thành tiền/tổng tiền.
     * @param maHD Mã hóa đơn cần truy vấn.
     * @return List<CT_HoaDon>
     */
    public ArrayList<CT_HoaDon> layDSCTHoaDonTheoMaHD(String maHD) {
        ArrayList<CT_HoaDon> danhSachCT = new ArrayList<>();
        String sql = "SELECT CTHD.maHD, CTHD.maMon, CTHD.soLuong, MA.tenMon, MA.giaMon " +
                     "FROM CT_HOADON CTHD JOIN MONAN MA ON CTHD.maMon = MA.maMon " +
                     "WHERE CTHD.maHD = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maHD);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 1. Tạo Entity MonAn ảo (chỉ cần MaMon, TenMon và GiaMonAn)
                    MonAn monAn = new MonAn(rs.getString("maMon"));
                    monAn.setTenMonAn(rs.getString("tenMon"));
                    monAn.setGiaMonAn(rs.getDouble("giaMon"));
                    
                    // 2. Tạo Entity HoaDon ảo (chỉ cần MaHD)
                    HoaDon hoaDon = new HoaDon(rs.getString("maHD"));
                    
                    // 3. Tạo CT_HoaDon
                    CT_HoaDon ct = new CT_HoaDon(hoaDon, monAn, rs.getInt("soLuong"));
                    
                    danhSachCT.add(ct);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachCT;
    }
}