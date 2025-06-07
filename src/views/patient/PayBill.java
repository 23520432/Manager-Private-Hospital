/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.sql.Statement;

import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class PayBill extends JFrame {
    private JTextField txtSearch, txtMaHD, txtTongTien;
    private JComboBox<String> cbTrangThai;
    private JTable tableThuoc, tableHDKham, tableDieuTri;
    private String patientId;
    private JPanel payBillPanel; 


    public PayBill(String patientId) throws SQLException, ClassNotFoundException {
        this.patientId = patientId;
        
        setTitle("THANH TOÁN HÓA ĐƠN");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        //Co theo kích thước màn hình (đề xuất)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height); // full màn hình
        
        payBillPanel = initComponents();
        add(payBillPanel); 
    }
    
    private void filterTable(JTable table, String keyword, String maHD, double tongTien, String trangThai) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!keyword.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword)));
        }
        if (!maHD.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(maHD)));
        }
        if (tongTien >= 0) {
            filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, tongTien));
        }
        if (!trangThai.equals("Tất cả")) {
            filters.add(RowFilter.regexFilter("(?i)" + trangThai));
        }

        sorter.setRowFilter(RowFilter.andFilter(filters));
    }

    
    private void filterData() throws SQLException, ClassNotFoundException {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String maHD = txtMaHD.getText().trim().toLowerCase();
        String tongTienStr = txtTongTien.getText().trim();
        String trangThai = cbTrangThai.getSelectedItem().toString();

        double tongTien = -1;
        try {
            if (!tongTienStr.isEmpty()) {
                tongTien = Double.parseDouble(tongTienStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tổng tiền phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        filterTable(tableThuoc, keyword, maHD, tongTien, trangThai);
        filterTable(tableHDKham, keyword, maHD, tongTien, trangThai);
        filterTable(tableDieuTri, keyword, maHD, tongTien, trangThai);
    }

    
    public JPanel initComponents() throws SQLException, ClassNotFoundException {
        JPanel panel = new JPanel(new BorderLayout());
        
        // ===== TOP PANEL (Tiêu đề + tìm kiếm) =====
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // Tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("THANH TOÁN HÓA ĐƠN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 40));
        lblTitle.setForeground(new Color(0x588EA7));
        lblTitle.setBackground(new Color(0xd6eaed));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        lblTitle.setOpaque(true);
        titlePanel.add(lblTitle, BorderLayout.CENTER);
        
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(new Color(0xCDE8E5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        txtSearch = new JTextField(12);
        txtMaHD = new JTextField(10);
        txtTongTien = new JTextField(10);
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Chưa thanh toán"});
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(0x78a2a7));
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSearch.setBackground(new Color(0xff9800));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSearch.setBackground(new Color(0x78a2a7));
            }
        });
        
        btnSearch.addActionListener(e -> {
            try {
                filterData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; 
        JLabel lbWord = new JLabel("Từ khóa:");
        lbWord.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lbWord, gbc);
        gbc.gridx = 1; 
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        searchPanel.add(txtSearch, gbc);

        gbc.gridx = 2; 
        JLabel lbMaHD = new JLabel("Mã hóa đơn:");
        lbMaHD.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lbMaHD, gbc);
        txtMaHD.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        gbc.gridx = 3; searchPanel.add(txtMaHD, gbc);

        gbc.gridx = 4; 
        JLabel lbMoney = new JLabel("Tổng tiền:");
        lbMoney.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lbMoney, gbc);
        txtTongTien.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        gbc.gridx = 5; searchPanel.add(txtTongTien, gbc);

        gbc.gridx = 6; 
        JLabel lbState = new JLabel("Trạng thái:");
        lbState.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lbState, gbc);
        gbc.gridx = 7; 
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTrangThai.setBackground(Color.WHITE);
        searchPanel.add(cbTrangThai, gbc);

        gbc.gridx = 8;
        gbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(btnSearch, gbc);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH); 


        // ===== BOTTOM PANEL (3 bảng) =====
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(0xd6eaed));
        panel.add(mainPanel, BorderLayout.CENTER);

        // Các bảng
        mainPanel.add(createTableSection("Hóa đơn thuốc", tableThuoc = new JTable()));
        mainPanel.add(createTableSection("Hóa đơn khám bệnh", tableHDKham = new JTable()));
        mainPanel.add(createTableSection("Hóa đơn điều trị", tableDieuTri = new JTable()));
        
        loadThuocTableData();    
        loadHoaDonKBTableData();       
        loadDieuTriTableData(); 
        
        return panel;
        
    }

    private JPanel createTableSection(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(0xd6eaed)); // Giữ đồng nhất với mainPanel nếu cần

        // Tiêu đề (JLabel thay cho TitledBorder)
        JLabel lblSectionTitle = new JLabel(title);
        lblSectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblSectionTitle.setForeground(Color.BLACK);
        lblSectionTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10)); // khoảng cách
        panel.add(lblSectionTitle, BorderLayout.NORTH);
        
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(new Color(0xCDE8E5));
        
        //Xem chi tiết bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        showBillDetails(table, selectedRow);
                }
                }
            }
        });

        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        return panel;
    }

    public void loadThuocTableData() throws SQLException, ClassNotFoundException {
        DefaultTableModel model = (DefaultTableModel) tableThuoc.getModel();
        model.setRowCount(0); // Clear table

        model.setColumnIdentifiers(new Object[] {
            "Mã đơn thuốc", "Mã dược sĩ", "Mã bác sĩ", "Mã bệnh nhân", "Giới tính bệnh nhân", "Ngày sinh bệnh nhân", "Lịch sử bệnh lý bệnh nhân",
            "Dị ứng bệnh nhân", "File Đơn Thuốc", "Ghi chú", "Ngày bán", "Thành tiền", "Trạng thái thanh toán"
        });

        tableThuoc.getColumnModel().getColumn(12).setCellRenderer(new StatusCellRenderer());

        String sql = "SELECT MADT, MADS, MABS, MABN, GIOITINHBN, NGAYSINHBN, LICHSU_BENHLY_BN, DIUNGBN, "
                   + "FILEDONTHUOC, GHICHU, NGAYBAN, THANHTIEN, TRANGTHAITT "
                   + "FROM DONTHUOC_DONTHUOCYC WHERE MABN = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MADT"),
                        rs.getString("MADS"),
                        rs.getString("MABS"),
                        rs.getString("MABN"),
                        rs.getString("GIOITINHBN"),
                        rs.getString("NGAYSINHBN"),
                        rs.getString("LICHSU_BENHLY_BN"),
                        rs.getString("DIUNGBN"),
                        rs.getBlob("FILEDONTHUOC"),
                        rs.getString("GHICHU"),
                        rs.getString("NGAYBAN"),
                        rs.getString("THANHTIEN"),
                        rs.getString("TRANGTHAITT")
                    };
                    model.addRow(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu đơn thuốc: " + e.getMessage());
        }
    }

    
    public void loadHoaDonKBTableData() throws SQLException, ClassNotFoundException {
        DefaultTableModel model = (DefaultTableModel) tableHDKham.getModel();
        model.setRowCount(0); // Clear table

        // Set column headers
        model.setColumnIdentifiers(new Object[] {
            "Mã hóa đơn khám bệnh", "Mã khám", "Ngày lập", "Tổng tiền",
            "Phương thức thanh toán", "Ghi chú", "Trạng thái thanh toán"
        });
        
        tableHDKham.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        String sql = "SELECT * FROM HOADON_KHAMBENH H JOIN KHAM K ON H.MAKHAM = K.MAKHAM WHERE K.MABN = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MAHDKB"),
                        rs.getString("MAKHAM"),
                        rs.getDate("NGAYLAP"),
                        rs.getDouble("TONGTIEN"),
                        rs.getString("PHUONGTHUCTT"),
                        rs.getString("GHICHU"),
                        rs.getString("TRANGTHAITT")
                    };
                    model.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    
    public void loadDieuTriTableData() throws SQLException, ClassNotFoundException {
        DefaultTableModel model = (DefaultTableModel) tableDieuTri.getModel();
        model.setRowCount(0); // Clear table

        // Set column headers
        model.setColumnIdentifiers(new Object[] {
            "Mã điều trị", "Mã khám", "Mã bác sĩ", "Phòng điều trị",
            "Phương pháp điều trị", "Ngày tiếp nhận", "Ngày kết thúc dự kiến",
            "Tổng hóa đơn điều trị", "Kết quả điều trị", "Lưu ý", "Trạng thái thanh toán"
        });
        
        tableDieuTri.getColumnModel().getColumn(10).setCellRenderer(new StatusCellRenderer());

        String sql = "SELECT * FROM DIEUTRI D JOIN KHAM K ON D.MAKHAM = K.MAKHAM WHERE K.MABN = ?";

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {       
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MADTR"),
                        rs.getString("MAKHAM"),
                        rs.getString("MABS"),
                        rs.getString("PHONGDIEUTRI"),
                        rs.getString("PHUONGPHAPDT"),
                        rs.getDate("NGAYTIEPNHAN"),
                        rs.getDate("NGAYKTDK"),
                        rs.getDouble("TONGHDDT"),
                        rs.getString("KETQUADT"),
                        rs.getString("LUUY"),
                        rs.getString("TRANGTHAITT")
                    };
                    model.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void showBillDetails(JTable table, int row) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(new Color(0xd9eef2));

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textPane.setBackground(Color.WHITE);
        textPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        StyledDocument doc = textPane.getStyledDocument();

        SimpleAttributeSet boldAttr = new SimpleAttributeSet();
        StyleConstants.setBold(boldAttr, true);
        SimpleAttributeSet normalAttr = new SimpleAttributeSet();

        // Hiển thị thông tin cơ bản
        for (int i = 0; i < table.getColumnCount(); i++) {
            String colName = table.getColumnName(i);
            Object valueObj = table.getValueAt(row, i);
            String value = (valueObj != null) ? valueObj.toString() : "Không có dữ liệu";
            try {
                doc.insertString(doc.getLength(), colName + ": ", boldAttr);
                doc.insertString(doc.getLength(), value + "\n", normalAttr);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

        // Thêm dữ liệu chi tiết theo bảng
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            if (table == tableThuoc) {
                String maDT = table.getValueAt(row, 0).toString(); // MADT
                ResultSet rs = stmt.executeQuery("SELECT * FROM CTDT WHERE MADT = '" + maDT + "'");
                doc.insertString(doc.getLength(), "\n--- Chi tiết đơn thuốc ---\n", boldAttr);
                while (rs.next()) {
                    doc.insertString(doc.getLength(), "Sản phẩm: " + rs.getString("MASP") + ", SL: " + rs.getInt("SOLUONG") + ", Đơn giá: " + rs.getDouble("DONGIA") + ", Thành tiền: " + rs.getDouble("THANHTIEN") + "\n", normalAttr);
                }
            } else if (table == tableHDKham) {
                String maHD = table.getValueAt(row, 0).toString(); // MAHDKB
                ResultSet rs = stmt.executeQuery("SELECT * FROM CTHD_KHAMBENH WHERE MAHDKB = '" + maHD + "'");
                doc.insertString(doc.getLength(), "\n--- Chi tiết khám bệnh ---\n", boldAttr);
                while (rs.next()) {
                    doc.insertString(doc.getLength(), "Dịch vụ: " + rs.getString("MADV") + ", SL: " + rs.getInt("SOLUONGDV") + ", Trị giá: " + rs.getDouble("TRIGIA") + "\n", normalAttr);
                }
            } else if (table == tableDieuTri) {
                String maDTri = table.getValueAt(row, 0).toString(); // MADTR
                ResultSet rs = stmt.executeQuery("SELECT * FROM CTDIEUTRI WHERE MADTR = '" + maDTri + "'");
                doc.insertString(doc.getLength(), "\n--- Chi tiết điều trị ---\n", boldAttr);
                while (rs.next()) {
                    doc.insertString(doc.getLength(),
                        "DV: " + rs.getString("MADV") +
                        ", SL DV: " + rs.getInt("SOLUONGDV") +
                        ", Đơn thuốc: " + rs.getString("MADT") +
                        ", SL ĐT: " + rs.getInt("SOLUONGDT") + "\n", normalAttr
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        textPane.setCaretPosition(0); // Đặt con trỏ về đầu => không cuộn xuống cuối
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(0xd9eef2));
        
        String status = table.getValueAt(row, table.getColumnCount() - 1).toString().trim().toLowerCase();

        if (status.equals("chưa thanh toán")) {
            JButton payButton = new JButton("Thanh toán");
            payButton.setBackground(new Color(0x007b00));
            payButton.setForeground(Color.WHITE);
            payButton.setFocusPainted(false);
            payButton.addActionListener(e -> {
                showPaymentDialog(table, row, dialog);
            });
            buttonPanel.add(payButton);
        }

        
        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(0x2B4A59));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        contentPanel.add(new JScrollPane(textPane), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(contentPanel);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showPaymentDialog(JTable table, int row, JDialog parentDialog) {
        String maHD = table.getValueAt(row, 0).toString();

        // Xác định cột tổng tiền tương ứng với từng bảng
        String tongTien = "";
        if (table == tableThuoc) {
            tongTien = table.getValueAt(row, 11).toString(); 
        } else if (table == tableHDKham) {
            tongTien = table.getValueAt(row, 3).toString(); 
        } else if (table == tableDieuTri) {
            tongTien = table.getValueAt(row, 7).toString(); 
        }

        JDialog payDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận thanh toán", true);
        payDialog.setSize(400, 250);
        payDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(0xd9eef2));

        panel.add(new JLabel("Mã hóa đơn:"));
        JTextField txtMaHD = new JTextField(maHD);
        txtMaHD.setEditable(false);
        panel.add(txtMaHD);

        panel.add(new JLabel("Tổng tiền:"));
        JTextField txtTongTien = new JTextField(tongTien);
        txtTongTien.setEditable(false);
        panel.add(txtTongTien);

        panel.add(new JLabel("Phương thức thanh toán:"));
        String[] methods = {"Tiền mặt", "Chuyển khoản", "Thẻ ngân hàng", "Ví điện tử"};
        JComboBox<String> cbPhuongThuc = new JComboBox<>(methods);
        cbPhuongThuc.setBackground(Color.WHITE);
        panel.add(cbPhuongThuc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(new Color(0x2B4A59));
        btnXacNhan.setForeground(Color.WHITE);
        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(0xff9800));

        btnXacNhan.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                String ma = table.getValueAt(row, 0).toString();

                String updateQuery = "";
                if (table == tableThuoc) {
                    updateQuery = "UPDATE DONTHUOC_DONTHUOCYC SET TRANGTHAITT = 'Đã thanh toán' WHERE MADT = ?";
                } else if (table == tableHDKham) {
                    updateQuery = "UPDATE HOADON_KHAMBENH SET TRANGTHAITT = 'Đã thanh toán' WHERE MAHDKB = ?";
                } else if (table == tableDieuTri) {
                    updateQuery = "UPDATE DIEUTRI SET TRANGTHAITT = 'Đã thanh toán' WHERE MADTR = ?";
                }

                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setString(1, ma);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    payDialog.dispose();
                    parentDialog.dispose();
                    reloadPayBillPanel(); 
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> payDialog.dispose());

        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnHuy);

        payDialog.setLayout(new BorderLayout());
        payDialog.add(panel, BorderLayout.CENTER);
        payDialog.add(buttonPanel, BorderLayout.SOUTH);
        payDialog.setVisible(true);
    }

    
    private void reloadPayBillPanel() {
        try {
            Container parent = payBillPanel.getParent(); // Lấy panel cha
            int index = -1;

            if (parent != null) {
                Component[] components = parent.getComponents();
                for (int i = 0; i < components.length; i++) {
                    if (components[i] == payBillPanel) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    parent.remove(payBillPanel); // Xóa panel cũ
                    payBillPanel = initComponents(); // Tạo lại panel mới
                    parent.add(payBillPanel, index); // Thêm panel mới vào đúng vị trí
                    parent.revalidate(); // Cập nhật giao diện
                    parent.repaint();    // Vẽ lại
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new PayBill("U002").setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(PayBill.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayBill.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}


// Renderer tùy chỉnh để đổi màu văn bản trong cột "Trạng thái thanh toán"
class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String status = value != null ? value.toString().trim().toLowerCase() : "";
        switch (status) {
            case "chưa thanh toán" -> {
                c.setFont(new Font("Arial", Font.BOLD, 13));
                c.setForeground(Color.RED);
            }
            case "đã thanh toán" -> c.setForeground(new Color(0x2E7D32));
            default -> c.setForeground(Color.BLACK);
        }
        return c;
    }
}