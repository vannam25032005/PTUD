package entity;

import javax.management.RuntimeErrorException;

public class MonAn {
    private String maMonAn;
    private String tenMonAn;
    private String loaiMonAn;
    private double giaMonAn;
    private String hinhAnh; // Thuộc tính mới: Đường dẫn hình ảnh
    private QuanLy quanLy; 

    public MonAn(String maMonAn) {
        super();
        this.maMonAn = maMonAn;
    }
    public MonAn(String maMonAn, String tenMon) {
        super();
        this.maMonAn = maMonAn;
        this.tenMonAn = tenMon;
    }

    public MonAn(String maMonAn, String tenMonAn, String loaiMonAn, double giaMonAn, String hinhAnh, QuanLy quanLy) {
        this.maMonAn = maMonAn;
        this.tenMonAn = tenMonAn;
        this.loaiMonAn = loaiMonAn;
        this.giaMonAn = giaMonAn;
        this.hinhAnh = hinhAnh;
        this.quanLy = quanLy;
    }

    // Getters
    public String getMaMonAn() { return maMonAn; }
    public String getTenMonAn() { return tenMonAn; }
    public String getLoaiMonAn() { return loaiMonAn; }
    public double getGiaMonAn() { return giaMonAn; }
    public String getHinhAnh() { return hinhAnh; } 
    public QuanLy getQuanLy() { return quanLy; }

    // Setters
    public void setMaMonAn(String maMonAn) {
        if(maMonAn == null || maMonAn.trim().length() == 0) throw new RuntimeException("Mã món ăn không được rỗng");
        this.maMonAn = maMonAn;
    }
    public void setTenMonAn(String tenMonAn) {
        if(tenMonAn == null || tenMonAn.trim().length() == 0) throw new RuntimeException("Tên món ăn không được rỗng");
        this.tenMonAn = tenMonAn;
    }
    public void setLoaiMonAn(String loaiMonAn) {
        if(loaiMonAn == null || loaiMonAn.trim().length() == 0) throw new RuntimeException("Loại món ăn không được rỗng");
        this.loaiMonAn = loaiMonAn;
    }
    public void setGiaMonAn(double giaMonAn) {
        if(giaMonAn <= 0) throw new RuntimeException("Giá món ăn phải lớn hơn 0");
        this.giaMonAn = giaMonAn;
    }
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh; // Có thể chấp nhận NULL
    }
    public void setQuanLy(QuanLy quanLy) {
        if(quanLy == null) throw new RuntimeException("Món ăn phải do Quản lý quản lý");
        this.quanLy = quanLy;
    }

    // Equals và HashCode
    @Override
    public int hashCode() {
        return maMonAn.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MonAn other = (MonAn) obj;
        return maMonAn != null && maMonAn.equals(other.maMonAn);
    }
}