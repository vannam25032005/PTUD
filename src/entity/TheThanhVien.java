package entity;

import java.util.Objects;

public class TheThanhVien {
    private String maThe;
    private KhachHang khachHang; 
    private int diemTichLuy;
    private String loaiHang; 

    
    public static String tinhLoaiHang(int diem) {
        if (diem >= 250) {
            return "Kim cương";
        } else if (diem >= 100) {
            return "Vàng";
        } else {
            return "Bạc"; // 0 - 99 điểm
        }
    }

    
    public TheThanhVien(String maThe, KhachHang khachHang, int diemTichLuy) {
        this.maThe = maThe;
        this.khachHang = khachHang;

        this.setDiemTichLuy(diemTichLuy); 
    }

   
    public TheThanhVien(String maThe, KhachHang khachHang, int diemTichLuy, String loaiHangTuCSDL) {
        this.maThe = maThe;
        this.khachHang = khachHang;
   
        this.setDiemTichLuy(diemTichLuy);
    }
    

    public TheThanhVien(String maThe) {
        this.maThe = maThe;
    }


    // Getters
    public String getMaThe() { return maThe; }
    public KhachHang getKhachHang() { return khachHang; }
    public int getDiemTichLuy() { return diemTichLuy; }
    public String getLoaiHang() { return loaiHang; }

    // Setters
    public void setMaThe(String maThe) { this.maThe = maThe; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    
   
    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) diemTichLuy = 0;
        this.diemTichLuy = diemTichLuy;
        this.loaiHang = tinhLoaiHang(diemTichLuy); // Tự động cập nhật
    }
   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TheThanhVien that = (TheThanhVien) o;
        return Objects.equals(maThe, that.maThe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maThe);
    }
}