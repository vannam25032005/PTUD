package entity;

public class Ban {
    private String maBan;
    private String loaiBan; 
    private int soGhe;
    private String khuVuc; 
    private String trangThai; 

    // Constructors
    public Ban(String maban) {
    	this.maBan = maban;
    }

    public Ban(String maBan, String loaiBan, int soGhe, String khuVuc, String trangThai) {
        this.maBan = maBan;
        this.loaiBan = loaiBan;
        this.soGhe = soGhe;
        this.khuVuc = khuVuc;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(String loaiBan) {
        this.loaiBan = loaiBan;
    }

    public int getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(int soGhe) {
        this.soGhe = soGhe;
    }

    public String getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(String khuVuc) {
        this.khuVuc = khuVuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Ban [maBan=" + maBan + ", loaiBan=" + loaiBan + ", soGhe=" + soGhe + ", khuVuc=" + khuVuc
                + ", trangThai=" + trangThai + "]";
    }
}