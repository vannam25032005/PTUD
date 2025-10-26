package entity;

import javax.management.RuntimeErrorException;

public class CT_HoaDon {
    private int soLuong;

    // Khóa chính kép và liên kết
    private HoaDon hoaDon; 
    private MonAn monAn;   

    // Constructor chỉ chứa khóa chính kép
    public CT_HoaDon(HoaDon hoaDon, MonAn monAn) {
        this.hoaDon = hoaDon;
        this.monAn = monAn;
    }

    // Constructor đầy đủ (Không có thanhTien)
    public CT_HoaDon(HoaDon hoaDon, MonAn monAn, int soLuong) {
        this.hoaDon = hoaDon;
        this.monAn = monAn;
        this.soLuong = soLuong;
    }

    // Getters
    public int getSoLuong() { return soLuong; }
    public HoaDon getHoaDon() { return hoaDon; }
    public MonAn getMonAn() { return monAn; }

    // PHƯƠNG THỨC TÍNH TOÁN: THÀNH TIỀN
    public double tinhThanhTien() {
        if (monAn == null) {
            throw new RuntimeException("Không có thông tin món ăn để tính thành tiền.");
        }
        // Tính thành tiền dựa trên giá món ăn (giaMonAn) và số lượng
        return soLuong * monAn.getGiaMonAn();
    }

    // Setters
    public void setSoLuong(int soLuong) {
        if(soLuong <= 0) throw new RuntimeException("Số lượng phải lớn hơn 0");
        this.soLuong = soLuong;
    }
    public void setHoaDon(HoaDon hoaDon) {
        if(hoaDon == null) throw new RuntimeException("Chi tiết phải có Hóa đơn");
        this.hoaDon = hoaDon;
    }
    public void setMonAn(MonAn monAn) {
        if(monAn == null) throw new RuntimeException("Chi tiết phải có Món ăn");
        this.monAn = monAn;
    }

    // Equals và HashCode (Khóa kép)
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hoaDon == null) ? 0 : hoaDon.hashCode());
        result = prime * result + ((monAn == null) ? 0 : monAn.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CT_HoaDon other = (CT_HoaDon) obj;
        if (hoaDon == null) {
            if (other.hoaDon != null) return false;
        } else if (!hoaDon.equals(other.hoaDon)) return false;
        if (monAn == null) {
            if (other.monAn != null) return false;
        } else if (!monAn.equals(other.monAn)) return false;
        return true;
    }
}