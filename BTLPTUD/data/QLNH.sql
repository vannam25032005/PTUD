CREATE DATABASE QLNH;
GO

USE QLNH;
GO

-- Xóa các bảng nếu đã tồn tại
IF OBJECT_ID('dbo.BAOCAO', 'U') IS NOT NULL DROP TABLE dbo.BAOCAO;
IF OBJECT_ID('dbo.CT_HOADON', 'U') IS NOT NULL DROP TABLE dbo.CT_HOADON;
IF OBJECT_ID('dbo.HOADON', 'U') IS NOT NULL DROP TABLE dbo.HOADON;
IF OBJECT_ID('dbo.BANDAT', 'U') IS NOT NULL DROP TABLE dbo.BANDAT;
IF OBJECT_ID('dbo.KHUYENMAI', 'U') IS NOT NULL DROP TABLE dbo.KHUYENMAI;
IF OBJECT_ID('dbo.MONAN', 'U') IS NOT NULL DROP TABLE dbo.MONAN;
IF OBJECT_ID('dbo.THETHANHVIEN', 'U') IS NOT NULL DROP TABLE dbo.THETHANHVIEN;
IF OBJECT_ID('dbo.KHACHHANG', 'U') IS NOT NULL DROP TABLE dbo.KHACHHANG;
IF OBJECT_ID('dbo.BAN', 'U') IS NOT NULL DROP TABLE dbo.BAN;
IF OBJECT_ID('dbo.TAIKHOAN', 'U') IS NOT NULL DROP TABLE dbo.TAIKHOAN;
IF OBJECT_ID('dbo.NHANVIEN', 'U') IS NOT NULL DROP TABLE dbo.NHANVIEN;
IF OBJECT_ID('dbo.QUANLY', 'U') IS NOT NULL DROP TABLE dbo.QUANLY;
GO

-- 1. QUANLY
CREATE TABLE QUANLY (
    maQL NVARCHAR(10) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    email NVARCHAR(50) NOT NULL,
    soDienThoai NVARCHAR(15) NOT NULL
);

-- 2. KHUYENMAI
CREATE TABLE KHUYENMAI (
    maKM NVARCHAR(10) PRIMARY KEY,
    tenKM NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(100),
    phanTramGiam FLOAT NOT NULL CHECK (phanTramGiam >= 0 AND phanTramGiam <= 1),
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    maQL NVARCHAR(10) NOT NULL,
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL),
    CHECK (ngayKetThuc >= ngayBatDau)
);

-- 3. NHANVIEN
CREATE TABLE NHANVIEN (
    maNV NVARCHAR(10) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    CCCD NVARCHAR(20) UNIQUE NOT NULL,
    soDienThoai NVARCHAR(15) NOT NULL,
    email NVARCHAR(50) NOT NULL,
    ngaySinh DATE NOT NULL,
    trangThai NVARCHAR(20) NOT NULL,
    maQL NVARCHAR(10) NOT NULL,
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL)
);

-- 4. TAIKHOAN 
CREATE TABLE TAIKHOAN (
    tenDangNhap NVARCHAR(50) PRIMARY KEY,
    matKhau NVARCHAR(50) NOT NULL,
    vaiTro NVARCHAR(20) NOT NULL,
    trangThai NVARCHAR(20) NOT NULL,
    maNV NVARCHAR(10) UNIQUE,
    maQL NVARCHAR(10) UNIQUE,
    FOREIGN KEY (maNV) REFERENCES NHANVIEN(maNV),
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL)
);

-- 5. KHACHHANG
CREATE TABLE KHACHHANG (
    maKH NVARCHAR(10) PRIMARY KEY,
    hoTenKH NVARCHAR(50) NOT NULL,
    soDienThoai NVARCHAR(15) NOT NULL,
    email NVARCHAR(50),
    gioiTinh BIT NOT NULL,
    maNV NVARCHAR(10) NOT NULL,
    FOREIGN KEY (maNV) REFERENCES NHANVIEN(maNV)
);

-- 6. THETHANHVIEN
CREATE TABLE THETHANHVIEN (
    maThe NVARCHAR(10) PRIMARY KEY,
    maKH NVARCHAR(10) UNIQUE NOT NULL,
    diemTichLuy INT NOT NULL CHECK (diemTichLuy >= 0),
    FOREIGN KEY (maKH) REFERENCES KHACHHANG(maKH)
);

-- 7. BAN
CREATE TABLE BAN (
    maBan NVARCHAR(10) PRIMARY KEY,
    sucChua INT NOT NULL CHECK (sucChua > 0)
);

-- 8. BANDAT
CREATE TABLE BANDAT (
    maDatBan NVARCHAR(10) PRIMARY KEY,
    maKH NVARCHAR(10) NOT NULL,
    maBan NVARCHAR(10) NOT NULL,
    soLuongKhach INT NOT NULL CHECK (soLuongKhach > 0),
    tienCoc FLOAT NOT NULL CHECK (tienCoc >= 0),
    trangThai NVARCHAR(20) NOT NULL,
    FOREIGN KEY (maKH) REFERENCES KHACHHANG(maKH),
    FOREIGN KEY (maBan) REFERENCES BAN(maBan)
);

-- 9. MONAN
CREATE TABLE MONAN (
    maMon NVARCHAR(10) PRIMARY KEY,
    tenMon NVARCHAR(50) NOT NULL,
    loaiMon NVARCHAR(50) NOT NULL,
    giaMon FLOAT NOT NULL CHECK (giaMon > 0),
    hinhAnh NVARCHAR(255),
    maQL NVARCHAR(10) NOT NULL,
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL)
);

-- 10. HOADON (ĐÃ THÊM cột TONGTIEN)
CREATE TABLE HOADON (
    maHD NVARCHAR(10) PRIMARY KEY,
    maThe NVARCHAR(10), 
    maNV NVARCHAR(10) NOT NULL,
    maBan NVARCHAR(10) NOT NULL,
    maDatBan NVARCHAR(10),
    maKM NVARCHAR(10),
    ngayLap DATETIME NOT NULL,
    tongTien FLOAT NOT NULL CHECK (tongTien >= 0), -- Cột TONGTIEN đã được thêm
    
    FOREIGN KEY (maThe) REFERENCES THETHANHVIEN(maThe),
    FOREIGN KEY (maNV) REFERENCES NHANVIEN(maNV),
    FOREIGN KEY (maBan) REFERENCES BAN(maBan),
    FOREIGN KEY (maDatBan) REFERENCES BANDAT(maDatBan),
    FOREIGN KEY (maKM) REFERENCES KHUYENMAI(maKM)
);

-- 11. CT_HOADON (ĐÃ THÊM cột THANHTIEN)
CREATE TABLE CT_HOADON (
    maHD NVARCHAR(10),
    maMon NVARCHAR(10),
    soLuong INT NOT NULL CHECK (soLuong > 0),
    thanhTien FLOAT NOT NULL CHECK (thanhTien >= 0), -- Cột THANHTIEN đã được thêm
    
    PRIMARY KEY (maHD, maMon),
    FOREIGN KEY (maHD) REFERENCES HOADON(maHD),
    FOREIGN KEY (maMon) REFERENCES MONAN(maMon)
);

-- 12. BAOCAO
CREATE TABLE BAOCAO (
    maBC NVARCHAR(10) PRIMARY KEY,
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    doanhThu FLOAT NOT NULL,
    maNV NVARCHAR(10), 
    maQL NVARCHAR(10) NOT NULL,
    maHD NVARCHAR(10), 
    FOREIGN KEY (maNV) REFERENCES NHANVIEN(maNV),
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL),
    FOREIGN KEY (maHD) REFERENCES HOADON(maHD)
);
GO

-- ===================================
-- DỮ LIỆU MẪU ĐÃ CẬP NHẬT
-- ===================================

INSERT INTO QUANLY (maQL, hoTen, email, soDienThoai) VALUES 
('QL001', N'Trần Đại Hiệp', 'hiep.tran@qlnh.vn', '0901234567'), 
('QL002', N'Lê Thị Mai', 'mai.le@qlnh.vn', '0919876543');

INSERT INTO BAN (maBan, sucChua) VALUES 
('B001', 4), ('B002', 6), ('B003', 2), ('B004', 8);

INSERT INTO NHANVIEN (maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, trangThai, maQL) VALUES 
('NV001', N'Nguyễn Văn An', '001122334455', '0922345678', 'an.nguyen@qlnh.vn', '1995-05-20', N'Đang làm', 'QL001'),
('NV002', N'Phạm Thu Hà', '001122334466', '0933456789', 'ha.pham@qlnh.vn', '1998-08-15', N'Đang làm', 'QL001');

INSERT INTO TAIKHOAN (tenDangNhap, matKhau, vaiTro, trangThai, maNV, maQL) VALUES
('tranhiep', '123456', N'Quản Lý', N'Hoạt động', NULL, 'QL001'),
('nvan', '123456', N'Nhân Viên', N'Hoạt động', 'NV001', NULL);

INSERT INTO KHUYENMAI (maKM, tenKM, moTa, phanTramGiam, ngayBatDau, ngayKetThuc, maQL) VALUES 
('KM001', N'Giảm giá mùa hè', NULL, 0.15, '2025-06-01', '2025-08-30', 'QL001'), -- Giảm 15%
('KM002', N'Khách hàng mới', N'Giảm 10%', 0.10, '2024-01-01', '2030-01-01', 'QL002');

INSERT INTO MONAN (maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL) VALUES 
('MA001', N'Lẩu Hải Sản', N'Món Chính', 299000, N'images/lau_haisan.jpg', 'QL001'), 
('MA002', N'Bò Né', N'Ăn Sáng', 85000, N'images/bo_ne.jpg', 'QL001'),
('MA003', N'Trà Đào', N'Thức Uống', 40000, N'images/tra_dao.jpg', 'QL002');

INSERT INTO KHACHHANG (maKH, hoTenKH, soDienThoai, email, gioiTinh, maNV) VALUES 
('KH001', N'Phan Đình Quang', '0812345678', 'quang.phan@gmail.com', 0, 'NV001'),
('KH002', N'Ngô Tố Uyên', '0898765432', 'uyen.ngo@gmail.com', 1, 'NV002');

INSERT INTO THETHANHVIEN (maThe, maKH, diemTichLuy) VALUES 
('TV001', 'KH001', 500), 
('TV002', 'KH002', 1500);

INSERT INTO BANDAT (maDatBan, maKH, maBan, soLuongKhach, tienCoc, trangThai) VALUES 
('DB001', 'KH001', 'B002', 5, 200000, N'Đã đặt'), 
('DB002', 'KH002', 'B004', 7, 300000, N'Đã nhận');

-- TÍNH TAY GIÁ TRỊ TỔNG TIỀN VÀ THÀNH TIỀN:
-- HD001: 1*MA001 (299000) + 1*MA003 (40000) = 339000. Giảm 15% (KM001) => 339000 * 0.85 = 288150.
-- HD002: 1*MA002 (85000) + 1*MA003 (40000) = 125000. Không KM => 125000.

INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien) VALUES 
('HD001', 'TV001', 'NV001', 'B002', 'DB001', 'KM001', '2025-05-15 10:30:00', 288150), 
('HD002', NULL, 'NV002', 'B001', NULL, NULL, '2025-05-15 11:45:00', 125000);

INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES 
('HD001', 'MA001', 1, 299000), 
('HD001', 'MA003', 1, 40000),  
('HD002', 'MA002', 1, 85000),   
('HD002', 'MA003', 1, 40000);  
GO