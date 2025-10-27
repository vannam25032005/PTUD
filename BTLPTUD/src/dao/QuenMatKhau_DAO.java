package dao;

import java.util.*;
import java.time.Instant;
import javax.mail.*;
import javax.mail.internet.*;

public class QuenMatKhau_DAO {

    private static class OTPItem {
        String maOTP;
        Instant hetHan;
        OTPItem(String maOTP, Instant hetHan) {
            this.maOTP = maOTP;
            this.hetHan = hetHan;
        }
    }

    private static final Map<String, OTPItem> otpStore = new HashMap<>();
    private static final long HSD = 300; // 5 phút

    private final String from = "25.nguyenvannam.11a1@gmail.com";
    private final String appPassword = "pfwh ohnr vrse prgk";

    // Tạo OTP + gửi email
    public boolean guiOTP(String email) {
        try {
            String otp = taoOTP(email);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, 
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, appPassword);
                    }
                }
            );

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, email);
            msg.setSubject("Mã xác thực OTP", "UTF-8");
            msg.setText("Mã OTP của bạn là: " + otp + "\nCó hiệu lực trong 5 phút", "UTF-8");

            Transport.send(msg);
            System.out.println("Gửi OTP đến email: " + email);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tạo OTP
    public String taoOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        synchronized (otpStore) {
            otpStore.put(email, new OTPItem(otp, Instant.now().plusSeconds(HSD)));
        }
        return otp;
    }

    // Kiểm tra OTP hợp lệ
    public boolean kiemTraOTP(String email, String otp) {
        synchronized (otpStore) {
            OTPItem item = otpStore.get(email);
            if (item == null) return false;
            if (Instant.now().isAfter(item.hetHan)) {
                otpStore.remove(email);
                return false;
            }
            boolean ok = item.maOTP.equals(otp);
            if (ok) otpStore.remove(email); // xoá dùng 1 lần
            return ok;
        }
    }
}
