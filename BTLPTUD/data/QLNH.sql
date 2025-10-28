CREATE DATABASE QLNH;
GO

USE QLNH;
GO

-- Xóa các bảng nếu đã tồn tại (Đảm bảo thứ tự xóa để tránh lỗi khóa ngoại)
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

------------------------------------
-- 1. QUANLY
------------------------------------
CREATE TABLE QUANLY (
    maQL NVARCHAR(10) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    soDienThoai NVARCHAR(15) UNIQUE NOT NULL,
    email NVARCHAR(50),
    ngaySinh DATE,
    CCCD NVARCHAR(20) UNIQUE,
    gioiTinh BIT NOT NULL, -- 0: Nam, 1: Nữ
    trangThai NVARCHAR(20) NOT NULL 
);

------------------------------------
-- 2. NHANVIEN (ĐÃ BỎ CỘT chucVu)
------------------------------------
CREATE TABLE NHANVIEN (
    maNV NVARCHAR(10) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    CCCD NVARCHAR(20) UNIQUE,
    soDienThoai NVARCHAR(15) UNIQUE NOT NULL,
    email NVARCHAR(50),
    ngaySinh DATE,
    gioiTinh BIT NOT NULL, -- 0: Nam, 1: Nữ
    trangThai NVARCHAR(20) NOT NULL,
    
    maQL NVARCHAR(10), -- Quản lý phụ trách trực tiếp
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL) 
);

------------------------------------
-- 3. TAIKHOAN
------------------------------------
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

------------------------------------
-- 4. KHACHHANG
------------------------------------
CREATE TABLE KHACHHANG (
    maKH NVARCHAR(10) PRIMARY KEY,
    hoTenKH NVARCHAR(50) NOT NULL,
    soDienThoai NVARCHAR(15) UNIQUE NOT NULL,
    email NVARCHAR(50), 
    gioiTinh BIT NOT NULL -- 0: Nam, 1: Nữ
);

------------------------------------
-- 5. THETHANHVIEN
------------------------------------
CREATE TABLE THETHANHVIEN (
    maThe NVARCHAR(10) PRIMARY KEY,
    maKH NVARCHAR(10) UNIQUE NOT NULL,
    diemTichLuy INT NOT NULL CHECK (diemTichLuy >= 0),
    loaiHang NVARCHAR(50) NOT NULL DEFAULT N'Bạc', -- Giữ lại cột này
    FOREIGN KEY (maKH) REFERENCES KHACHHANG(maKH)
);

------------------------------------
-- 6. BAN
------------------------------------
CREATE TABLE BAN (
    maBan NVARCHAR(10) PRIMARY KEY,
    loaiBan NVARCHAR(50) NOT NULL,    
    soGhe INT NOT NULL CHECK (soGhe > 0), 
    khuVuc NVARCHAR(50) NOT NULL,     
    trangThai NVARCHAR(20) NOT NULL   
);

------------------------------------
-- 7. BANDAT
------------------------------------
CREATE TABLE BANDAT (
    maDatBan NVARCHAR(10) PRIMARY KEY,
    maKH NVARCHAR(10) NOT NULL,
    maBan NVARCHAR(10) NOT NULL,
    
    ngayDat DATE NOT NULL,
    gioDat TIME NOT NULL, 
    ghiChu NVARCHAR(255),
    
    soLuongKhach INT NOT NULL CHECK (soLuongKhach > 0),
    tienCoc FLOAT NOT NULL CHECK (tienCoc >= 0),
    trangThai NVARCHAR(20) NOT NULL, 
    
    FOREIGN KEY (maKH) REFERENCES KHACHHANG(maKH),
    FOREIGN KEY (maBan) REFERENCES BAN(maBan)
);

------------------------------------
-- 8. KHUYENMAI
------------------------------------
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

------------------------------------
-- 9. MONAN
------------------------------------
CREATE TABLE MONAN (
    maMon NVARCHAR(10) PRIMARY KEY,
    tenMon NVARCHAR(50) NOT NULL,
    loaiMon NVARCHAR(50) NOT NULL,
    giaMon FLOAT NOT NULL CHECK (giaMon > 0),
    hinhAnh NVARCHAR(255),
    
    maQL NVARCHAR(10) NOT NULL, 
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL) 
);
CREATE TABLE CT_BANDAT (
    maBan NVARCHAR(10) NOT NULL,
    maMon NVARCHAR(10) NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    PRIMARY KEY (maBan, maMon),
    FOREIGN KEY (maBan) REFERENCES BAN(maBan),
    FOREIGN KEY (maMon) REFERENCES MONAN(maMon)
);
GO
------------------------------------
-- 10. HOADON
------------------------------------
CREATE TABLE HOADON (
    maHD NVARCHAR(10) PRIMARY KEY,
    maThe NVARCHAR(10),	
    maNV NVARCHAR(10) NOT NULL, 
    maBan NVARCHAR(10) NOT NULL,
    maDatBan NVARCHAR(10) UNIQUE, 
    maKM NVARCHAR(10),
    ngayLap DATETIME NOT NULL,
    tongTien FLOAT NOT NULL CHECK (tongTien >= 0),
    trangThaiThanhToan NVARCHAR(20) NOT NULL, 
    
    FOREIGN KEY (maThe) REFERENCES THETHANHVIEN(maThe),
    FOREIGN KEY (maNV) REFERENCES NHANVIEN(maNV),
    FOREIGN KEY (maBan) REFERENCES BAN(maBan),
    FOREIGN KEY (maDatBan) REFERENCES BANDAT(maDatBan),
    FOREIGN KEY (maKM) REFERENCES KHUYENMAI(maKM)
);

------------------------------------
-- 11. CT_HOADON
------------------------------------
CREATE TABLE CT_HOADON (
    maHD NVARCHAR(10),
    maMon NVARCHAR(10),
    soLuong INT NOT NULL CHECK (soLuong > 0),
    thanhTien FLOAT NOT NULL CHECK (thanhTien >= 0),
    
    PRIMARY KEY (maHD, maMon),
    FOREIGN KEY (maHD) REFERENCES HOADON(maHD),
    FOREIGN KEY (maMon) REFERENCES MONAN(maMon)
);

------------------------------------
-- 12. BAOCAO
------------------------------------
CREATE TABLE BAOCAO (
    maBC NVARCHAR(10) PRIMARY KEY,
    ngayLap DATE NOT NULL,
    thoiGianTu DATE NOT NULL,
    thoiGianDen DATE NOT NULL,
    doanhThu FLOAT NOT NULL,
    
    maQL NVARCHAR(10) NOT NULL, 
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL)
);
GO

---
-- Dữ liệu mẫu (Cập nhật tên cột)

-- 1. QUANLY
INSERT INTO QUANLY (maQL, hoTen, soDienThoai, email, ngaySinh, CCCD, gioiTinh, trangThai) VALUES
('QL001', N'Trần Đại Hiệp', '0901234567', 'hiep.tran@qlnh.vn', '1985-01-01', '001122334455', 0, N'Đang làm'),
('QL002', N'Lê Thị Mai', '0919876543', 'mai.le@qlnh.vn', '1990-05-10', '001122334466', 1, N'Đang làm');

-- 2. NHANVIEN (ĐÃ BỎ CỘT chucVu)
-- LƯU Ý: Số lượng cột trong VALUES phải khớp với số lượng cột trong INSERT
INSERT INTO NHANVIEN (maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL) VALUES
('NV001', N'Nguyễn Văn An', '001122334477', '0922345678', 'an.nguyen@qlnh.vn', '1995-05-20', 0, N'Đang làm', 'QL001'),
('NV002', N'Phạm Thu Hà', '001122334488', '0933456789', 'ha.pham@qlnh.vn', '1998-08-15', 1, N'Đang làm', 'QL002');

-- 3. TAIKHOAN
INSERT INTO TAIKHOAN (tenDangNhap, matKhau, vaiTro, trangThai, maNV, maQL) VALUES
('tranhiep', '123456', N'Quản Lý', N'Hoạt động', NULL, 'QL001'),
('nvan', '123456', N'Nhân Viên', N'Hoạt động', 'NV001', NULL);

-- 4. KHACHHANG
INSERT INTO KHACHHANG (maKH, hoTenKH, soDienThoai, email, gioiTinh) VALUES
('KH001', N'Phan Đình Quang', '0812345678', 'quang.phan@gmail.com', 0),
('KH002', N'Ngô Tố Uyên', '0898765432', 'uyen.ngo@gmail.com', 1);

-- 5. THETHANHVIEN (Đã thêm loaiHang)
INSERT INTO THETHANHVIEN (maThe, maKH, diemTichLuy, loaiHang) VALUES
('TV001', 'KH001', 500, N'Vàng'); -- SỬA: Thêm giá trị cho loaiHang

-- 6. BAN
INSERT INTO BAN (maBan, loaiBan, soGhe, khuVuc, trangThai) VALUES
('B001', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),
('B002', N'Bàn vừa', 4, N'Tầng 1', N'Đang sử dụng'),
('B003', N'Phòng VIP', 10, N'Tầng 2', N'Đã đặt');

-- 7. BANDAT
INSERT INTO BANDAT (maDatBan, maKH, maBan, ngayDat, gioDat, ghiChu, soLuongKhach, tienCoc, trangThai) VALUES
('DB001', 'KH001', 'B003', '2025-10-27', '18:30:00', N'Kỷ niệm ngày cưới', 5, 200000, N'Đã nhận');

-- 8. KHUYENMAI
INSERT INTO KHUYENMAI (maKM, tenKM, moTa, phanTramGiam, ngayBatDau, ngayKetThuc, maQL) VALUES 
('KM001', N'Giảm giá mùa hè', NULL, 0.15, '2025-06-01', '2025-08-30', 'QL001');

-- 9. MONAN
INSERT INTO MONAN (maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL) VALUES 
('MA001', N'Lẩu Hải Sản', N'Món Chính', 299000, N'lau_haisan.jpg', 'QL001'),
('MA003', N'Trà Đào', N'Thức Uống', 40000, N'tra_dao.jpg', 'QL002');

-- 10. HOADON
INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) VALUES
('HD001', 'TV001', 'NV001', 'B002', 'DB001', 'KM001', GETDATE(), 288150, N'Đã thanh toán');

-- 11. CT_HOADON
INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES
('HD001', 'MA001', 1, 299000), 
('HD001', 'MA003', 1, 40000); 

-- 12. BAOCAO
INSERT INTO BAOCAO (maBC, ngayLap, thoiGianTu, thoiGianDen, doanhThu, maQL) VALUES 
('BC001', GETDATE(), '2025-10-01', '2025-10-31', 288150, 'QL001'); 
GO
-- Bắt đầu chèn dữ liệu
INSERT INTO dbo.BAN (maBan, loaiBan, soGhe, khuVuc, trangThai) VALUES
-- 10 Bàn nhỏ (2-4 chỗ) ở Tầng 1
('B004', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B005', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B006', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B007', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B008', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B009', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B010', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B011', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),
('B012', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),
('B013', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),

-- 6 Bàn vừa (4-6 chỗ) ở Tầng 2
('B014', N'Bàn vừa', 6, N'Tầng 2', N'Trống'),
('B015', N'Bàn vừa', 6, N'Tầng 2', N'Trống'),
('B016', N'Bàn vừa', 6, N'Tầng 2', N'Trống'),
('B017', N'Bàn vừa', 4, N'Tầng 2', N'Trống'),
('B018', N'Bàn vừa', 4, N'Tầng 2', N'Trống'),
('B019', N'Bàn vừa', 4, N'Tầng 2', N'Trống'),

-- 2 Bàn lớn (8-10 chỗ) ở Tầng 3
('B020', N'Bàn lớn', 8, N'Tầng 3', N'Trống'),
('B021', N'Bàn lớn', 8, N'Tầng 3', N'Trống'),

-- 2 Phòng VIP (12-15 chỗ)
('VIP2', N'Phòng VIP', 15, N'Tầng 3', N'Trống'),
('VIP3', N'Phòng VIP', 15, N'Tầng 3', N'Trống');