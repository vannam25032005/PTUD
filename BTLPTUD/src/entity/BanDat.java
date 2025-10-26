package entity;

import javax.management.RuntimeErrorException;

public class BanDat {
    private String maDatBan;
    private KhachHang khachHang; // maKH
    private Ban ban;             // maBan
    private int soLuongKhach;
    private double tienCoc;
    private String trangThai;

    public BanDat(String maDatBan) {
        super();
        this.maDatBan = maDatBan;
    }

    public BanDat(String maDatBan, KhachHang khachHang, Ban ban, int soLuongKhach, double tienCoc, String trangThai) {
        this.maDatBan = maDatBan;
        this.khachHang = khachHang;
        this.ban = ban;
        this.soLuongKhach = soLuongKhach;
        this.tienCoc = tienCoc;
        this.trangThai = trangThai;
    }

    // Getters
    public String getMaDatBan() { return maDatBan; }
    public KhachHang getKhachHang() { return khachHang; }
    public Ban getBan() { return ban; }
    public int getSoLuongKhach() { return soLuongKhach; }
    public double getTienCoc() { return tienCoc; }
    public String getTrangThai() { return trangThai; }

    // Setters
    public void setMaDatBan(String maDatBan) {
        if(maDatBan == null || maDatBan.trim().length() == 0) throw new RuntimeException("Mã đặt bàn không được rỗng");
        this.maDatBan = maDatBan;
    }
    public void setKhachHang(KhachHang khachHang) {
        if(khachHang == null) throw new RuntimeException("Phải có Khách hàng đặt bàn");
        this.khachHang = khachHang;
    }
    public void setBan(Ban ban) {
        if(ban == null) throw new RuntimeException("Phải có Bàn được đặt");
        this.ban = ban;
    }
    public void setSoLuongKhach(int soLuongKhach) {
        if(soLuongKhach <= 0) throw new RuntimeException("Số lượng khách phải lớn hơn 0");
        this.soLuongKhach = soLuongKhach;
    }
    public void setTienCoc(double tienCoc) {
        if(tienCoc < 0) throw new RuntimeException("Tiền cọc không được âm");
        this.tienCoc = tienCoc;
    }
    public void setTrangThai(String trangThai) {
        if(trangThai == null || trangThai.trim().length() == 0) throw new RuntimeException("Trạng thái không được rỗng");
        this.trangThai = trangThai;
    }

    // Equals và HashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maDatBan == null) ? 0 : maDatBan.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BanDat other = (BanDat) obj;
        if (maDatBan == null) {
            if (other.maDatBan != null) return false;
        } else if (!maDatBan.equals(other.maDatBan)) return false;
        return true;
    }
}