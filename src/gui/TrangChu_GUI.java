package gui;



import javax.swing.*;
import java.awt.*;

public class TrangChu_GUI extends JPanel {
    public TrangChu_GUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Panel chứa hình nền
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon background = new ImageIcon("src/image/nhahang.png");
                    g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
                    // Thêm lớp phủ mờ để chữ dễ đọc
                    g.setColor(new Color(255, 255, 255, 20)); // Trắng, độ mờ 150
                    g.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    System.out.println("Home background error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

 
        JLabel infoLabel = new JLabel("Chào ngày mới");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(infoLabel, BorderLayout.NORTH);

        add(backgroundPanel, BorderLayout.CENTER);
    }
}