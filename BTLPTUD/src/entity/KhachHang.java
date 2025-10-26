package entity;

import javax.management.RuntimeErrorException;

public class KhachHang {
    private String maKH;
    private String hoTenKH;
    private String soDienThoai;
    private String email; // Chấp nhận NULL trong CSDL nhưng không rỗng
    private boolean gioiTinh; 
    private NhanVien nhanVien; // maNV

    public KhachHang(String maKH) {
        super();
        this.maKH = maKH;
    }

    public KhachHang(String maKH, String hoTenKH, String soDienThoai, String email, boolean gioiTinh, NhanVien nhanVien) {
        this.maKH = maKH;
        this.hoTenKH = hoTenKH;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.nhanVien = nhanVien;
    }

    // Getters
    public String getMaKH() { return maKH; }
    public String getHoTenKH() { return hoTenKH; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public boolean isGioiTinh() { return gioiTinh; }
    public NhanVien getNhanVien() { return nhanVien; }

    // Setters
    public void setMaKH(String maKH) {
        if(maKH == null || maKH.trim().length() == 0) throw new RuntimeException("Mã Khách hàng không được rỗng");
        this.maKH = maKH;
    }
    public void setHoTenKH(String hoTenKH) {
        if(hoTenKH == null || hoTenKH.trim().length() == 0) throw new RuntimeException("Họ tên Khách hàng không được rỗng");
        this.hoTenKH = hoTenKH;
    }
    public void setSoDienThoai(String soDienThoai) {
        if(soDienThoai == null || soDienThoai.trim().length() == 0) throw new RuntimeException("Số điện thoại không được rỗng");
        this.soDienThoai = soDienThoai;
    }
    public void setEmail(String email) {
        // Chấp nhận email có thể NULL trong CSDL, chỉ kiểm tra không rỗng nếu khác NULL
        if(email != null && email.trim().length() == 0) throw new RuntimeException("Email không được rỗng nếu có giá trị");
        this.email = email; 
    }
    public void setGioiTinh(boolean gioiTinh) { this.gioiTinh = gioiTinh; }
    public void setNhanVien(NhanVien nhanVien) {
        if(nhanVien == null) throw new RuntimeException("Phải có Nhân viên lập hồ sơ");
        this.nhanVien = nhanVien;
    }

    // Equals và HashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maKH == null) ? 0 : maKH.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KhachHang other = (KhachHang) obj;
        if (maKH == null) {
            if (other.maKH != null) return false;
        } else if (!maKH.equals(other.maKH)) return false;
        return true;
    }
}