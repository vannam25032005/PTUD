package entity;

import java.time.LocalDate;
// Đã loại bỏ import java.sql.Date; (chỉ dùng LocalDate)

public class KhuyenMai {
	private String maKM;
	private String tenKM;
	private String moTa;
	private Double phanTramGiam;
	
	// Sửa kiểu dữ liệu từ java.sql.Date sang java.time.LocalDate
	private LocalDate ngayBatDau; 
	private LocalDate ngayKetThuc;
	
	// Constructor đầy đủ tham số đã sửa kiểu Date
	public KhuyenMai(String maKM, String tenKM, String moTa, Double phanTramGiam, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
		super();
		this.maKM = maKM;
		this.tenKM = tenKM;
		this.moTa = moTa;
		this.phanTramGiam = phanTramGiam;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
	}
	
	// Constructor mặc định (cần thiết cho một số framework)
	public KhuyenMai(String maKM) {
		super();
        this.maKM = maKM;
	}
	
	// ======================== Getters and Setters ========================

	public String getMaKM() {
		return maKM;
	}
	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}
	
	public String getTenKM() {
		return tenKM;
	}
	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}
	
	public String getMoTa() {
		return moTa;
	}
	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}
	
	public Double getPhanTramGiam() {
		return phanTramGiam;
	}
	public void setPhanTramGiam(Double phanTramGiam) {
		this.phanTramGiam = phanTramGiam;
	}
	
	// Đã sửa kiểu dữ liệu trả về cho Getter/Setter ngày tháng
	public LocalDate getNgayBatDau() {
		return ngayBatDau;
	}
	public void setNgayBatDau(LocalDate ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}
	
	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}
	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}
	
	// Thêm phương thức toString() để tiện cho việc Debug
    @Override
    public String toString() {
        return "KhuyenMai [maKM=" + maKM + ", tenKM=" + tenKM + ", phanTramGiam=" + phanTramGiam 
                + ", ngayBatDau=" + ngayBatDau + ", ngayKetThuc=" + ngayKetThuc + "]";
    }
}