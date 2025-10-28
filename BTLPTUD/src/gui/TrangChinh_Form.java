package gui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class TrangChinh_Form extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private CardLayout cardLayout;
	private JPanel pnlContent;

	private JButton btndangxuat;
	
	public TrangChinh_Form() {
		setTitle("Nhà hàng TripleND");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1200, 800);
		setExtendedState(MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(247, 247, 247));
        
     
        // Menu trái
        JPanel pTrai = new JPanel();
        
        pTrai.setBackground(new Color(160, 134, 121));
        pTrai.setPreferredSize(new Dimension(220, getHeight()));
        pTrai.setLayout(new BoxLayout(pTrai, BoxLayout.Y_AXIS));
        pTrai.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        
        // Logo
        ImageIcon logoIcon = new ImageIcon("src/image/logo.png");
        JLabel lblLogo = new JLabel(logoIcon);
        
        Image scaledImage = logoIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        lblLogo.setIcon(scaledIcon);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
        pTrai.add(lblLogo);
        
     // Nút điều hướng (menu bên trái)
        String[] btnChucnang = {
            "Dashboard",
            "Quản lý bàn đặt",
            "Quản lý món ăn",
            "Quản lý KH",
            "Quản lý nhân viên",
            "Quản lý hóa đơn",
            "Báo cáo",
            "Khuyến mãi",
            
        };

        String[] imgPaths = {
            "src/image/dashboard.png",
            "src/image/ban.png",
            "src/image/monan.png",
            "src/image/khachhang.png",
            "src/image/nhanvien.png",
            "src/image/invoice.png",
            "src/image/baocao.png",
            "src/image/khuyenmai.png",
        
        };

        // Màu nền chính
        Color colorNen = new Color(255, 216, 164);
        Color colorNhat = new Color(255, 231, 188);
        Color colorDam = new Color(255, 178, 44);
        Font fontMenu = new Font("Segoe UI", Font.BOLD, 14);

        pTrai.setBackground(colorNen);

        for (int i = 0; i < btnChucnang.length; i++) {
            String label = btnChucnang[i];
            JButton btn = new JButton(label);

            // Thêm icon
            ImageIcon icon = new ImageIcon(imgPaths[i]);
            Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));

            // Tinh chỉnh giao diện nút
            btn.setFont(fontMenu);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(15);
            btn.setMaximumSize(new Dimension(250, 50));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

           
                btn.setBackground(colorNhat);
                btn.setForeground(Color.BLACK);
           

        

            // Chuyển trang khi bấm
            btn.addActionListener(e -> cardLayout.show(pnlContent, label));

            pTrai.add(btn);
            pTrai.add(Box.createRigidArea(new Dimension(0, 15))); // khoảng cách giữa các nút
        }

        
        btndangxuat = new JButton("Đăng xuất");
        ImageIcon icon = new ImageIcon("src/image/dangxuat.png");
        Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        btndangxuat.setIcon(new ImageIcon(img));
        btndangxuat.setHorizontalAlignment(SwingConstants.LEFT);
        btndangxuat.setMaximumSize(new Dimension(250, 50));
        btndangxuat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btndangxuat.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btndangxuat.setForeground(Color.BLACK);
        btndangxuat.setBackground(colorNhat);
        pTrai.add(btndangxuat);
        // Content phải
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        
        // Thêm từng trang nhỏ vào pnlContent
        pnlContent.add(new TrangChu_GUI(), "Dashboard");
        pnlContent.add(new BanDat_GUI(), "Quản lý bàn đặt");
        pnlContent.add(new MonAn_GUI(), "Quản lý món ăn");
        pnlContent.add(new QuanLyKhachHang_GUI(), "Quản lý KH");
        pnlContent.add(new NhanVien_GUI(), "Quản lý nhân viên");
        pnlContent.add(new BaoCao_GUI(), "Báo cáo");
        pnlContent.add(new KhuyenMai_GUI(), "Khuyến mãi");
        pnlContent.add(new HoaDon_GUI(), "Quản lý hóa đơn");
        

        
      
     
        
      
        add(pTrai, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);
		setVisible(true);
		btndangxuat.addActionListener(this);
		
	}

	
public static void main(String[] args) {
	new TrangChinh_Form().setVisible(true);;
}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o  = e.getSource();
		if(o.equals(btndangxuat)) {
			 int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		        if (confirm == JOptionPane.YES_OPTION) {
		        	dispose();
		        	new DangNhap_GUI().setVisible(true);
		        }
		}
		
	}
	
}
