package entity;


public class TheThanhVien {
    private String maThe;
    private KhachHang khachHang; 
    private int diemTichLuy;
    private String loaiHang;
    public TheThanhVien() {}

    public TheThanhVien(String maThe) {
        super();
        this.maThe = maThe;
    }

	
    public TheThanhVien(String maThe, KhachHang khachHang, int diemTichLuy, String loaiHang) {
		super();
		this.maThe = maThe;
		this.khachHang = khachHang;
		this.diemTichLuy = diemTichLuy;
		this.loaiHang = loaiHang;
	}


	public String getLoaiHang() {
		return loaiHang;
	}


	public void setLoaiHang(String loaiHang) {
		this.loaiHang = loaiHang;
	}


	// Getters
    public String getMaThe() { return maThe; }
    public KhachHang getKhachHang() { return khachHang; }
    public int getDiemTichLuy() { return diemTichLuy; }

    // Setters
    public void setMaThe(String maThe) {
        if(maThe == null || maThe.trim().length() == 0) throw new RuntimeException("Mã thẻ không được rỗng");
        this.maThe = maThe;
    }
    public void setKhachHang(KhachHang khachHang) {
        if(khachHang == null) throw new RuntimeException("Thẻ phải liên kết với Khách hàng");
        this.khachHang = khachHang;
    }
    public void setDiemTichLuy(int diemTichLuy) {
        if(diemTichLuy < 0) throw new RuntimeException("Điểm tích lũy không được âm");
        this.diemTichLuy = diemTichLuy;
    }

    // Equals và HashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maThe == null) ? 0 : maThe.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TheThanhVien other = (TheThanhVien) obj;
        if (maThe == null) {
            if (other.maThe != null) return false;
        } else if (!maThe.equals(other.maThe)) return false;
        return true;
    }
}