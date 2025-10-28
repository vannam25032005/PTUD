package entity;

import java.util.Objects;

/**
 * Lớp Entity đại diện cho chi tiết món ăn được gọi tại một Bàn (CT_Ban).
 * Lưu ý: Tên lớp vẫn giữ là CTBanDat theo yêu cầu, dù logic liên kết với Bàn.
 */
public class CTBanDat { // Bạn có thể cân nhắc đổi tên thành CTBanAn cho rõ ràng hơn

    private Ban ban;       // Khóa ngoại tham chiếu đến Bàn
    private MonAn monAn;   // Khóa ngoại tham chiếu đến Món Ăn
    private int soLuong;

    // --- Constructor ---

    /**
     * Constructor đầy đủ tham số.
     * @param ban Đối tượng Bàn mà món ăn được gọi.
     * @param monAn Đối tượng Món Ăn được gọi.
     * @param soLuong Số lượng món ăn được gọi.
     */
    public CTBanDat(Ban ban, MonAn monAn, int soLuong) {
        setBan(ban);         // Sử dụng setter để áp dụng validation
        setMonAn(monAn);     // Sử dụng setter
        setSoLuong(soLuong); // Sử dụng setter
    }

    // --- Getters ---

    public Ban getBan() {
        return ban;
    }

    public MonAn getMonAn() {
        return monAn;
    }

    public int getSoLuong() {
        return soLuong;
    }

    // --- Setters (với Validation cơ bản) ---

    public void setBan(Ban ban) {
        if (ban == null || ban.getMaBan() == null || ban.getMaBan().trim().isEmpty()) {
            throw new IllegalArgumentException("Đối tượng Bàn hoặc Mã Bàn không hợp lệ.");
        }
        this.ban = ban;
    }

    public void setMonAn(MonAn monAn) {
        if (monAn == null || monAn.getMaMonAn() == null || monAn.getMaMonAn().trim().isEmpty()) { // Giả định có getMaMonAn()
            throw new IllegalArgumentException("Đối tượng Món Ăn hoặc Mã Món Ăn không hợp lệ.");
        }
        this.monAn = monAn;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        this.soLuong = soLuong;
    }

    // --- hashCode và equals (Dựa trên khóa chính kép: Ban và MonAn) ---

    @Override
    public int hashCode() {
        // Sử dụng mã của Ban và MonAn để tạo hashCode
        return Objects.hash(ban != null ? ban.getMaBan() : null,
                            monAn != null ? monAn.getMaMonAn() : null); // Giả định có getMaMonAn()
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CTBanDat other = (CTBanDat) obj;
        // So sánh dựa trên mã của Ban và MonAn
        return Objects.equals(ban != null ? ban.getMaBan() : null, other.ban != null ? other.ban.getMaBan() : null) &&
               Objects.equals(monAn != null ? monAn.getMaMonAn() : null, other.monAn != null ? other.monAn.getMaMonAn() : null); // Giả định có getMaMonAn()
    }

    // --- toString ---

    @Override
    public String toString() {
        return "CTBanDat [ban=" + (ban != null ? ban.getMaBan() : "null")
                + ", monAn=" + (monAn != null ? monAn.getMaMonAn() : "null") // Giả định có getMaMonAn()
                + ", soLuong=" + soLuong + "]";
    }
}