package views.pharmacist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class PharmacistDashboard extends JFrame {
    private String pharmacistId;
    private String pharmacistName;
    private JPanel mainPanel;
    
    // Custom colors
    private static final Color PRIMARY_COLOR = new Color(43, 74, 89);
    private static final Color HOVER_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_HOVER_COLOR = new Color(222, 246, 186);
    private static final Color MENU_BG_COLOR = new Color(33, 54, 69);
    
    public PharmacistDashboard(String pharmacistId, String pharmacistName) {
        this.pharmacistId = pharmacistId;
        this.pharmacistName = pharmacistName;

        setTitle("Dashboard Dược sĩ");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // MENU bên trái
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(MENU_BG_COLOR);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));
        menuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Logo/Title section
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(MENU_BG_COLOR);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        menuLabel.setForeground(Color.WHITE);
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        menuPanel.add(menuLabel);
        menuPanel.add(Box.createVerticalStrut(20));

        String[] menuItems = {
            "Thông báo",
            "Điều khoản sử dụng",
            "An toàn bảo mật",
            "Đăng xuất"
        };

        for (String item : menuItems) {
            JButton btn = createMenuButton(item);
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(5));
        }

        add(menuPanel, BorderLayout.WEST);

        // PHẦN CHÍNH GIỮA
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);

        // Welcome section with gradient background
        JPanel welcomePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = PRIMARY_COLOR;
                Color color2 = HOVER_COLOR;
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        welcomePanel.setPreferredSize(new Dimension(getWidth(), 100));

        JLabel welcomeLabel = new JLabel("Chào mừng dược sĩ: " + pharmacistName, JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        mainContentPanel.add(welcomePanel, BorderLayout.NORTH);

        // Grid panel with shadow effect
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 40, 40));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(40, 120, 40, 120));

        addFeatureButton(gridPanel, "Thông tin cá nhân", "profile.png", this::openPharmacistView);
        addFeatureButton(gridPanel, "Quản lý thuốc", "pill.png", this::openProductView);
        addFeatureButton(gridPanel, "Tra cứu dịch vụ khám", "DichVuKham.png", this::openServiceForm);
        addFeatureButton(gridPanel, "Hồ sơ bệnh nhân", "HoSoBenhNhan.png", this::openProfilePatient);
        addFeatureButton(gridPanel, "Báo cáo", "BaoCao.png", this::openPharmacistReport);
        addFeatureButton(gridPanel, "Lịch sử bán thuốc", "LichSuBanThuoc.png", this::openHisMedSell);

        mainContentPanel.add(gridPanel, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(230, 50));
        btn.setMinimumSize(new Dimension(230, 50));
        btn.setPreferredSize(new Dimension(230, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setBackground(MENU_BG_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(MENU_BG_COLOR);
            }
        });

        btn.addActionListener(e -> {
            switch (text) {
                case "Thông báo" -> openPharmacistNotification();
                case "Điều khoản sử dụng" -> openTermOfUse();
                case "An toàn bảo mật" -> openSecurityPolicy();
                case "Đăng xuất" -> dispose();
            }
        });

        return btn;
    }

    private void addFeatureButton(JPanel panel, String text, String iconFile, Runnable action) {
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(PRIMARY_COLOR, 2, 20),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        btnPanel.setPreferredSize(new Dimension(250, 250));

        // Shadow effect
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            btnPanel.getBorder()
        ));

        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/views/pharmacist/image/" + iconFile));
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            iconLabel.setText("?");
        }

        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        textLabel.setForeground(PRIMARY_COLOR);
        textLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnPanel.add(iconLabel, BorderLayout.CENTER);
        btnPanel.add(textLabel, BorderLayout.SOUTH);

        btnPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.run(); }
            public void mouseEntered(MouseEvent e) { 
                btnPanel.setBackground(BUTTON_HOVER_COLOR);
                btnPanel.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(HOVER_COLOR, 2, 20),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            public void mouseExited(MouseEvent e) { 
                btnPanel.setBackground(Color.WHITE);
                btnPanel.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(PRIMARY_COLOR, 2, 20),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        panel.add(btnPanel);
    }

    // Custom border bo góc
    static class RoundedBorder extends LineBorder {
        private int radius;
        public RoundedBorder(Color color, int thickness, int radius) {
            super(color, thickness, true);
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(lineColor);
            g2d.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    private void openPharmacistNotification() {
        try {
            new PharmacistNotification().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở Thông báo!");
        }
    }

    private void openHisMedSell() {
        new HisMedSell(pharmacistId).setVisible(true);
    }

    private void openPharmacistReport() {
        try {
            new PharmacistReport(pharmacistId).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở Báo cáo!");
        }
    }

    private void setMainContent(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void openPharmacistView() {
        try {
            JFrame frame = new JFrame("Thông tin Dược sĩ");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.add(new PharmacistView(pharmacistId, null));
            frame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở Thông tin cá nhân!");
        }
    }

    private void openProductView() {
        try {
            new ProductView().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở Quản lý thuốc!");
        }
    }

    private void openServiceForm() {
        try {
            new ServiceForm(pharmacistId).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở Tra cứu dịch vụ khám!");
        }
    }

    private void openProfilePatient() {
        try {
            new ProfilePatient(pharmacistId).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở Hồ sơ bệnh nhân!");
        }
    }

    private void openTermOfUse() {
        try {
            new TermOfUseModel().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở Điều khoản sử dụng!");
        }
    }

    private void openSecurityPolicy() {
        try {
            new SecurityPolicyModel().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở An toàn bảo mật!");
        }
    }

    // Main test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PharmacistDashboard("DS001", "Nguyễn Văn Dược Sĩ").setVisible(true);
        });
    }
} 