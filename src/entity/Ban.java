package entity;

import javax.management.RuntimeErrorException;

public class Ban {
    private String maBan;
    private int sucChua;

    public Ban(String maBan) {
        super();
        this.maBan = maBan;
    }

    public Ban(String maBan, int sucChua) {
        this.maBan = maBan;
        this.sucChua = sucChua;
    }

    // Getters
    public String getMaBan() { return maBan; }
    public int getSucChua() { return sucChua; }

    // Setters
    public void setMaBan(String maBan) {
        if(maBan == null || maBan.trim().length() == 0) throw new RuntimeException("Mã bàn không được rỗng");
        this.maBan = maBan;
    }
    public void setSucChua(int sucChua) {
        if(sucChua <= 0) throw new RuntimeException("Sức chứa phải lớn hơn 0");
        this.sucChua = sucChua;
    }

    // Equals và HashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maBan == null) ? 0 : maBan.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ban other = (Ban) obj;
        if (maBan == null) {
            if (other.maBan != null) return false;
        } else if (!maBan.equals(other.maBan)) return false;
        return true;
    }
}