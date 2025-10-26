package entity;

import java.time.LocalDate;
import javax.management.RuntimeErrorException;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String CCCD;
    private String soDienThoai;
    private String email;
    private LocalDate ngaySinh;
    private String trangThai;
    private QuanLy quanLy; // maQL

    public NhanVien(String maNV) {
        super();
        this.maNV = maNV;
    }

    public NhanVien(String maNV, String hoTen, String CCCD, String soDienThoai, String email, LocalDate ngaySinh, String trangThai, QuanLy quanLy) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.CCCD = CCCD;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.ngaySinh = ngaySinh;
        this.trangThai = trangThai;
        this.quanLy = quanLy;
    }

    // Getters
    public String getMaNV() { return maNV; }
    public String getHoTen() { return hoTen; }
    public String getCCCD() { return CCCD; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getTrangThai() { return trangThai; }
    public QuanLy getQuanLy() { return quanLy; }

    // Setters
    public void setMaNV(String maNV) {
        if(maNV == null || maNV.trim().length() == 0) throw new RuntimeException("Mã Nhân viên không được rỗng");
        this.maNV = maNV;
    }
    public void setHoTen(String hoTen) {
        if(hoTen == null || hoTen.trim().length() == 0) throw new RuntimeException("Họ tên không được rỗng");
        this.hoTen = hoTen;
    }
    public void setCCCD(String CCCD) {
        if(CCCD == null || CCCD.trim().length() != 12) throw new RuntimeException("CCCD phải có 12 ký tự");
        this.CCCD = CCCD;
    }
    public void setSoDienThoai(String soDienThoai) {
        if(soDienThoai == null || soDienThoai.trim().length() == 0) throw new RuntimeException("Số điện thoại không được rỗng");
        this.soDienThoai = soDienThoai;
    }
    public void setEmail(String email) {
        if(email == null || email.trim().length() == 0) throw new RuntimeException("Email không được rỗng");
        this.email = email;
    }
    public void setNgaySinh(LocalDate ngaySinh) {
        if(ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) throw new RuntimeException("Ngày sinh không hợp lệ");
        this.ngaySinh = ngaySinh;
    }
    public void setTrangThai(String trangThai) {
        if(trangThai == null || trangThai.trim().length() == 0) throw new RuntimeException("Trạng thái không được rỗng");
        this.trangThai = trangThai;
    }
    public void setQuanLy(QuanLy quanLy) {
        if(quanLy == null) throw new RuntimeException("Nhân viên phải có Quản lý");
        this.quanLy = quanLy;
    }

    // Equals và HashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maNV == null) ? 0 : maNV.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NhanVien other = (NhanVien) obj;
        if (maNV == null) {
            if (other.maNV != null) return false;
        } else if (!maNV.equals(other.maNV)) return false;
        return true;
    }
}