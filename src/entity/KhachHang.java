package entity;

import java.util.Objects;

public class KhachHang {
    private String maKH;
    private String hoTenKH;
    private String soDienThoai;
    private String email;
    private boolean gioiTinh; 
    
    public KhachHang() {
    }

    public KhachHang(String maKH) {
        this.maKH = maKH;
    }

    
    public KhachHang(String maKH, String hoTenKH, String soDienThoai, String email, boolean gioiTinh) {
        this.maKH = maKH;
        this.hoTenKH = hoTenKH;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.gioiTinh = gioiTinh;
    }

    // Getters
    public String getMaKH() { return maKH; }
    public String getHoTenKH() { return hoTenKH; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public boolean isGioiTinh() { return gioiTinh; }

    // Setters với Validation
    public void setMaKH(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
         
            throw new IllegalArgumentException("Mã khách hàng không được để trống.");
        }
     
        if (!maKH.matches("^KH[0-9]{3}$")) {
            throw new IllegalArgumentException("Mã khách hàng phải theo định dạng KHxxx (ví dụ: KH001).");
        }
        this.maKH = maKH;
    }

    public void setHoTenKH(String hoTenKH) {
        if (hoTenKH == null || hoTenKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên khách hàng không được để trống.");
        }
        this.hoTenKH = hoTenKH;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
    
        if (!soDienThoai.matches("^(09|03)[0-9]{8}$")) {
            throw new IllegalArgumentException("Số điện thoại phải có 10 chữ số và bắt đầu bằng 09 hoặc 03.");
        }
        this.soDienThoai = soDienThoai;
    }

    public void setEmail(String email) {
   

        this.email = email; 
    }

    public void setGioiTinh(boolean gioiTinh) {
      
        this.gioiTinh = gioiTinh; 
    }


    // Equals và HashCode
    @Override
    public int hashCode() {
        return Objects.hash(maKH);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        KhachHang other = (KhachHang) obj;
        return Objects.equals(maKH, other.maKH);
    }
    
    @Override
    public String toString() {
        return "KhachHang [maKH=" + maKH + ", hoTenKH=" + hoTenKH + ", soDienThoai=" + soDienThoai
                + ", email=" + email + ", gioiTinh=" + gioiTinh + "]";
    }
}