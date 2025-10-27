package entity;

import java.util.Objects;

public class TheThanhVien {
    private String maThe;
    private KhachHang khachHang; 
    private int diemTichLuy;
    private String loaiHang; // Sẽ được tự động tính toán

    /**
     * Hàm logic nghiệp vụ: Quyết định loại hạng dựa trên điểm
     */
    public static String tinhLoaiHang(int diem) {
        if (diem >= 250) {
            return "Kim cương";
        } else if (diem >= 100) {
            return "Vàng";
        } else {
            return "Bạc"; // 0 - 99 điểm
        }
    }

    /**
     * Constructor mới: Dùng khi TẠO thẻ mới. Tự động tính loaiHang.
     */
    public TheThanhVien(String maThe, KhachHang khachHang, int diemTichLuy) {
        this.maThe = maThe;
        this.khachHang = khachHang;
        // Gọi setter để áp dụng logic
        this.setDiemTichLuy(diemTichLuy); 
    }

    /**
     * Constructor cũ: Dùng khi DAO TẢI thẻ từ CSDL lên.
     * Chúng ta vẫn dùng setter để đảm bảo nếu CSDL có sai sót
     * (ví dụ: 500 điểm nhưng lưu là 'Bạc') thì code sẽ tự sửa lại.
     */
    public TheThanhVien(String maThe, KhachHang khachHang, int diemTichLuy, String loaiHangTuCSDL) {
        this.maThe = maThe;
        this.khachHang = khachHang;
        // Gọi setter để tự động tính toán lại loaiHang dựa trên điểm
        // Bỏ qua loaiHangTuCSDL, vì điểm là "nguồn chân lý" (source of truth).
        this.setDiemTichLuy(diemTichLuy);
    }
    
    // Constructor tối giản
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
    
    /**
     * SỬA ĐỔI QUAN TRỌNG:
     * Khi set điểm, đồng thời set luôn loại hạng
     */
    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) diemTichLuy = 0;
        this.diemTichLuy = diemTichLuy;
        this.loaiHang = tinhLoaiHang(diemTichLuy); // Tự động cập nhật
    }
    
    /**
     * XÓA: Không cho phép set loaiHang trực tiếp
     * public void setLoaiHang(String loaiHang) { this.loaiHang = loaiHang; }
     */

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