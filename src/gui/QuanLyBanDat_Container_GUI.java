package gui;

import javax.swing.*;
import java.awt.*;

public class QuanLyBanDat_Container_GUI extends JPanel {
    private BanDat_GUI banDatPanel;
    private DanhSachBanDat_GUI danhSachPanel;
    
    public QuanLyBanDat_Container_GUI() {
        setLayout(new BorderLayout());
        
        // Tạo JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Khởi tạo 2 panel
        banDatPanel = new BanDat_GUI();
        danhSachPanel = new DanhSachBanDat_GUI();
        
        // Thiết lập listener 2 chiều
        banDatPanel.setDataRefreshListener(new DataRefreshListener() {
            @Override
            public void onDataChanged() {
                // Khi BanDat_GUI thêm/sửa/xóa -> reload DanhSachBanDat_GUI
                danhSachPanel.refreshData();
            }
        });
        
        danhSachPanel.setDataRefreshListener(new DataRefreshListener() {
            @Override
            public void onDataChanged() {
                // Khi DanhSachBanDat_GUI xác nhận/hủy -> reload BanDat_GUI
                banDatPanel.refreshData();
            }
        });
        
        // Thêm vào tabs
        tabbedPane.addTab("📝 Đặt Bàn", banDatPanel);
        tabbedPane.addTab("📋 Danh Sách Bàn Đặt", danhSachPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}