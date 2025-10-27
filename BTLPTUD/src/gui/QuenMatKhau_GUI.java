//package gui;
//
//import dao.QuenMatKhau_DAO;
//import dao.TaiKhoan_DAO;
//import util.OTPManager;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//
//public class QuenMatKhau_GUI extends JFrame implements ActionListener {
//    private static final long serialVersionUID = 1L;
//
//    // Các control
//    private JTextField txtEmail;
//    private JButton btnGuiOTP;
//    private JTextField txtOTP;
//    private JButton btnXacNhanOTP;
//
//    private JTextField txtTenDNVisible; // chỉ để hiển thị (nếu muốn)
//    private JPasswordField txtMatKhauMoi, txtXacNhanMK;
//    private JButton btnCapNhat, btnThoat;
//
//    // DAO
//    private TaiKhoan_DAO taiKhoanDAO = new TaiKhoan_DAO();
//    private QuenMatKhau_DAO mailDAO = new QuenMatKhau_DAO();
//
//    // Lưu tenDangNhap sau khi tìm theo email
//    private String tenDangNhapFound = null;
//    private String emailFound = null;
//
//    public QuenMatKhau_GUI() {
//        setTitle("Quên mật khẩu");
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//
//        // Layout chính
//        JPanel root = new JPanel(new BorderLayout());
//        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
//        setContentPane(root);
//
//        Font font = new Font("Arial", Font.BOLD, 14);
//
//        // Title
//        JLabel lblTitle = new JLabel("Khôi phục mật khẩu");
//        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
//        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
//        root.add(lblTitle, BorderLayout.NORTH);
//
//        // Body
//        Box body = Box.createVerticalBox();
//
//        // Email row
//        Box bEmail = Box.createHorizontalBox();
//        JLabel lEmail = new JLabel("Email: ");
//        lEmail.setFont(font);
//        txtEmail = new JTextField();
//        bEmail.add(lEmail);
//        bEmail.add(Box.createHorizontalStrut(10));
//        bEmail.add(txtEmail);
//        bEmail.add(Box.createHorizontalStrut(10));
//        btnGuiOTP = new JButton("Gửi OTP");
//        bEmail.add(btnGuiOTP);
//        body.add(bEmail);
//
//        body.add(Box.createVerticalStrut(12));
//
//        // OTP row (ẩn mặc định)
//        Box bOTP = Box.createHorizontalBox();
//        JLabel lOTP = new JLabel("OTP: ");
//        lOTP.setFont(font);
//        txtOTP = new JTextField();
//        txtOTP.setPreferredSize(new Dimension(120, 26));
//        btnXacNhanOTP = new JButton("Xác nhận OTP");
//        bOTP.add(lOTP);
//        bOTP.add(Box.createHorizontalStrut(10));
//        bOTP.add(txtOTP);
//        bOTP.add(Box.createHorizontalStrut(10));
//        bOTP.add(btnXacNhanOTP);
//        body.add(bOTP);
//
//        body.add(Box.createVerticalStrut(12));
//
//        // Hiển thị tên đăng nhập tìm được (không cho sửa)
//        Box bTenDN = Box.createHorizontalBox();
//        JLabel lTenDN = new JLabel("Tên đăng nhập: ");
//        lTenDN.setFont(font);
//        txtTenDNVisible = new JTextField();
//        txtTenDNVisible.setEditable(false);
//        txtTenDNVisible.setPreferredSize(new Dimension(200,26));
//        bTenDN.add(lTenDN);
//        bTenDN.add(Box.createHorizontalStrut(10));
//        bTenDN.add(txtTenDNVisible);
//        body.add(bTenDN);
//
//        body.add(Box.createVerticalStrut(12));
//
//        // Mat khau moi (ẩn mặc định)
//        Box bMK1 = Box.createHorizontalBox();
//        JLabel lMK1 = new JLabel("Mật khẩu mới: ");
//        lMK1.setFont(font);
//        txtMatKhauMoi = new JPasswordField();
//        txtMatKhauMoi.setPreferredSize(new Dimension(200,26));
//        bMK1.add(lMK1);
//        bMK1.add(Box.createHorizontalStrut(10));
//        bMK1.add(txtMatKhauMoi);
//        body.add(bMK1);
//
//        body.add(Box.createVerticalStrut(10));
//
//        Box bMK2 = Box.createHorizontalBox();
//        JLabel lMK2 = new JLabel("Xác nhận MK: ");
//        lMK2.setFont(font);
//        txtXacNhanMK = new JPasswordField();
//        txtXacNhanMK.setPreferredSize(new Dimension(200,26));
//        bMK2.add(lMK2);
//        bMK2.add(Box.createHorizontalStrut(10));
//        bMK2.add(txtXacNhanMK);
//        body.add(bMK2);
//
//        body.add(Box.createVerticalStrut(16));
//
//        // Buttons
//        Box bButtons = Box.createHorizontalBox();
//        btnCapNhat = new JButton("Cập nhật");
//        btnThoat = new JButton("Thoát");
//        bButtons.add(btnCapNhat);
//        bButtons.add(Box.createHorizontalStrut(12));
//        bButtons.add(btnThoat);
//        body.add(bButtons);
//
//        // Add body
//        root.add(body, BorderLayout.CENTER);
//
//        // Style: ẩn các phần chưa đến lượt
//        txtOTP.setEnabled(false);
//        btnXacNhanOTP.setEnabled(false);
//        txtTenDNVisible.setText("");
//        txtMatKhauMoi.setEnabled(false);
//        txtXacNhanMK.setEnabled(false);
//        btnCapNhat.setEnabled(false);
//
//        // Events
//        btnGuiOTP.addActionListener(this);
//        btnXacNhanOTP.addActionListener(this);
//        btnCapNhat.addActionListener(this);
//        btnThoat.addActionListener(this);
//
//        setSize(520, 320);
//        setResizable(false);
//        setLocationRelativeTo(null);
//        setVisible(true);
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        Object o = e.getSource();
//
//        if (o == btnGuiOTP) {
//            String email = txtEmail.getText().trim();
//            if (email.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập email!");
//                return;
//            }
//
//            // Tìm tenDangNhap theo email
//            String tenDN = taiKhoanDAO.findTenDangNhapByEmail(email);
//            if (tenDN == null) {
//                JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản tương ứng với email này!");
//                return;
//            }
//
//            // Lưu tạm thông tin
//            this.tenDangNhapFound = tenDN;
//            this.emailFound = email;
//
//            // Tạo OTP và gửi mail
//            String otp = OTPManager.generateOTP(email);
//            String subject = "Mã xác thực OTP - Khôi phục mật khẩu";
//            String body = "Mã xác thực của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút.";
//
//            // Gọi DAO gửi mail (lớp bạn có sẵn)
//            mailDAO.sendEmail(email, subject, body);
//
//            JOptionPane.showMessageDialog(this, "OTP đã được gửi tới email. Vui lòng kiểm tra hộp thư.");
//            // Mở phần nhập OTP
//            txtOTP.setEnabled(true);
//            btnXacNhanOTP.setEnabled(true);
//            // disable nút gửi để tránh spam (nếu muốn cho phép gửi lại thì enable sau)
//            btnGuiOTP.setEnabled(false);
//        }
//
//        else if (o == btnXacNhanOTP) {
//            String otpInput = txtOTP.getText().trim();
//            if (otpInput.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập OTP!");
//                return;
//            }
//            if (emailFound == null) {
//                JOptionPane.showMessageDialog(this, "Vui lòng gửi OTP trước!");
//                return;
//            }
//
//            boolean ok = OTPManager.validateOTP(emailFound, otpInput);
//            if (!ok) {
//                JOptionPane.showMessageDialog(this, "OTP không hợp lệ hoặc đã hết hạn!");
//                return;
//            }
//
//            // OTP hợp lệ → hiển thị phần đổi mật khẩu
//            txtTenDNVisible.setText(tenDangNhapFound);
//            txtMatKhauMoi.setEnabled(true);
//            txtXacNhanMK.setEnabled(true);
//            btnCapNhat.setEnabled(true);
//
//            // Disable OTP controls (không cần nữa)
//            txtOTP.setEnabled(false);
//            btnXacNhanOTP.setEnabled(false);
//        }
//
//        else if (o == btnCapNhat) {
//            String mkMoi = new String(txtMatKhauMoi.getPassword()).trim();
//            String mkXacNhan = new String(txtXacNhanMK.getPassword()).trim();
//
//            if (mkMoi.isEmpty() || mkXacNhan.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới và xác nhận!");
//                return;
//            }
//            if (!mkMoi.equals(mkXacNhan)) {
//                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
//                return;
//            }
//
//            if (tenDangNhapFound == null) {
//                JOptionPane.showMessageDialog(this, "Lỗi: không tìm thấy tên đăng nhập để cập nhật!");
//                return;
//            }
//
//            boolean updated = taiKhoanDAO.capNhatMatKhauByTenDN(tenDangNhapFound, mkMoi);
//            if (updated) {
//                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
//                // dọn dẹp
//                OTPManager.removeOTP(emailFound);
//                dispose(); // hoặc clear form tuỳ ý
//            } else {
//                JOptionPane.showMessageDialog(this, "Cập nhật mật khẩu thất bại. Vui lòng thử lại.");
//            }
//        }
//
//        else if (o == btnThoat) {
//            this.dispose();
//        }
//    }