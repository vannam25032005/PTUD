package entity;

public class TaiKhoan {
	private String tenDangNhap;
	private String matKhau;
	private String vaiTro;
	private NhanVien nhanVien;
	private QuanLy quanLy;
	private String trangThai;
	public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, NhanVien nhanVien, QuanLy quanLy,
			String trangThai) {
		super();
		this.tenDangNhap = tenDangNhap;
		this.matKhau = matKhau;
		this.vaiTro = vaiTro;
		this.nhanVien = nhanVien;
		this.quanLy = quanLy;
		this.trangThai = trangThai;
	}
	public String getTenDangNhap() {
		return tenDangNhap;
	}
	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}
	public String getMatKhau() {
		return matKhau;
	}
	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}
	public String getVaiTro() {
		return vaiTro;
	}
	public void setVaiTro(String vaiTro) {
		this.vaiTro = vaiTro;
	}
	public NhanVien getNhanVien() {
		return nhanVien;
	}
	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}
	public QuanLy getQuanLy() {
		return quanLy;
	}
	public void setQuanLy(QuanLy quanLy) {
		this.quanLy = quanLy;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	
}
