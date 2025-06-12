package views.pharmacist;

import utils.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class HisMedSell extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnUpdate;
    private JTextField txtSearch;
    private String mads; // Mã dược sĩ đăng nhập

    public HisMedSell(String mads) {
        this.mads = mads;
        setTitle("Lịch sử bán thuốc");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(43, 74, 160));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel title = new JLabel("LỊCH SỬ BÁN THUỐC", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        searchPanel.setBackground(new Color(43, 74, 160));
        
        JLabel lblSearch = new JLabel("Tìm kiếm mã bệnh nhân:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(Color.WHITE);
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(200, 35));
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setBackground(new Color(255, 255, 255));
        btnSearch.setForeground(new Color(43, 74, 160));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(255, 255, 255));
        btnRefresh.setForeground(new Color(43, 74, 160));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {
            "Mã đơn thuốc", "Mã dược sĩ", "Mã bác sĩ khám", "Mã bệnh nhân", "Giới tính bệnh nhân", "Ngày sinh bệnh nhân",
            "Lịch sử bệnh lý", "Dị ứng", "File đơn thuốc", "Ghi chú", "Ngày bán", "Thành tiền", "Trạng thái"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setGridColor(new Color(234, 234, 234));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Customize table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(43, 74, 160));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Status column renderer
        table.getColumnModel().getColumn(12).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if ("Đã thanh toán".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(0, 128, 0));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.RED);
                }
                return c;
            }
        });

        // Double click listener
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showRowDetails(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        btnUpdate = new JButton("Thêm thông tin");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnUpdate.setBackground(new Color(43, 74, 160));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(e -> openUpdateDialog());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        bottomPanel.add(btnUpdate);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add search functionality
        btnSearch.addActionListener(e -> searchPatient());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void searchPatient() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM DONTHUOC_DONTHUOCYC WHERE LOWER(MABN) LIKE ? ORDER BY MADT DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + searchText + "%");
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        Object val = rs.getObject(i);
                        if (val instanceof java.sql.Date) {
                            row.add(val != null ? sdf.format(val) : "");
                        } else if (val instanceof java.sql.Blob) {
                            row.add(val != null ? "[File]" : "");
                        } else {
                            row.add(val);
                        }
                    }
                    tableModel.addRow(row);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + ex.getMessage());
        }
    }

    private void loadData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM DONTHUOC_DONTHUOCYC ORDER BY MADT DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        Object val = rs.getObject(i);
                        if (val instanceof java.sql.Date) {
                            row.add(val != null ? sdf.format(val) : "");
                        } else if (val instanceof java.sql.Blob) {
                            row.add(val != null ? "[File]" : "");
                        } else {
                            row.add(val);
                        }
                    }
                    tableModel.addRow(row);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void showRowDetails(int row) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            String columnName = tableModel.getColumnName(i);
            Object value = tableModel.getValueAt(row, i);

            JLabel lbl = new JLabel(columnName + ":");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(Color.DARK_GRAY);

            JTextField tf = new JTextField(value != null ? value.toString() : "");
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tf.setEditable(false);
            tf.setBackground(Color.WHITE);

            panel.add(lbl);
            panel.add(tf);
        }

        JDialog detailDialog = new JDialog(this, "Chi tiết đơn thuốc", true);
        detailDialog.getContentPane().setBackground(Color.WHITE);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.add(panel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Đóng");
        closeBtn.setBackground(new Color(43, 74, 160));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.addActionListener(e -> detailDialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        detailDialog.add(btnPanel, BorderLayout.SOUTH);

        detailDialog.setSize(700, 600);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }

    private void openUpdateDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn thuốc để cập nhật!");
            return;
        }

        String madt = String.valueOf(tableModel.getValueAt(row, 0));
        String mabs = String.valueOf(tableModel.getValueAt(row, 2));
        String ghichu = String.valueOf(tableModel.getValueAt(row, 9));
        String ngayban = String.valueOf(tableModel.getValueAt(row, 10));
        String thanhtien = String.valueOf(tableModel.getValueAt(row, 11));
        String trangthai = String.valueOf(tableModel.getValueAt(row, 12));
        boolean isPaid = "Đã thanh toán".equalsIgnoreCase(trangthai);

        JTextField tfMABS = new JTextField(mabs.equals("null") ? "" : mabs, 15);
        JTextField tfGhiChu = new JTextField(ghichu.equals("null") ? "" : ghichu, 15);
        JTextField tfNgayBan = new JTextField(ngayban.equals("null") ? "" : ngayban, 15);
        JTextField tfThanhTien = new JTextField(thanhtien.equals("null") ? "" : thanhtien, 15);
        tfThanhTien.setEditable(false);

        String[] trangThaiArr = {"Chưa thanh toán", "Đã thanh toán"};
        JComboBox<String> cbTrangThai = new JComboBox<>(trangThaiArr);
        cbTrangThai.setSelectedItem(trangthai);
        cbTrangThai.setPreferredSize(new Dimension(200, 30));

        cbTrangThai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ("Đã thanh toán".equals(value)) {
                    c.setBackground(new Color(144, 238, 144)); // xanh lá
                    if (isSelected) c.setBackground(new Color(100, 200, 100));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel[] labels = {
            new JLabel("Mã bác sĩ:"), new JLabel("Ghi chú:"),
            new JLabel("Ngày bán (yyyy-MM-dd):"), new JLabel("Thành tiền:"),
            new JLabel("Trạng thái:")
        };
        for (JLabel lbl : labels) lbl.setFont(labelFont);

        JTextField[] fields = {tfMABS, tfGhiChu, tfNgayBan, tfThanhTien};
        for (JTextField f : fields) {
            f.setFont(fieldFont);
            f.setPreferredSize(new Dimension(250, 30));
        }

        Component[] components = {tfMABS, tfGhiChu, tfNgayBan, tfThanhTien, cbTrangThai};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            form.add(labels[i], gbc);
            gbc.gridx = 1;
            form.add(components[i], gbc);
        }

        JButton btnCapNhat = new JButton("Thêm thông tin");
        btnCapNhat.setBackground(new Color(43, 74, 160));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFont(labelFont);

        JButton btnXemChiTiet = new JButton("Xem chi tiết");
        btnXemChiTiet.setBackground(new Color(0, 123, 255));
        btnXemChiTiet.setForeground(Color.WHITE);
        btnXemChiTiet.setFont(labelFont);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnXemChiTiet);

        // Khóa chỉnh sửa nếu đã thanh toán
        if (isPaid) {
            tfMABS.setEditable(false);
            tfGhiChu.setEditable(false);
            tfNgayBan.setEditable(false);
            cbTrangThai.setEnabled(false);
            btnCapNhat.setEnabled(false);
            btnCapNhat.setToolTipText("Đơn thuốc đã thanh toán không thể sửa.");
        }

        JDialog dialog = new JDialog(this, "Cập nhật đơn thuốc", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Hiển thị ảnh đơn thuốc nếu có
        ImageIcon imageIcon = getPrescriptionImage(madt);
        if (imageIcon != null) {
            JLabel lblImage = new JLabel(imageIcon);
            lblImage.setBorder(BorderFactory.createTitledBorder("Ảnh đơn thuốc"));
            dialog.add(lblImage, BorderLayout.EAST);
        }

        // Cập nhật thông tin
        btnCapNhat.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE DONTHUOC_DONTHUOCYC SET MADS=?, MABS=?, GHICHU=?, NGAYBAN=?, THANHTIEN=?, TRANGTHAITT=? WHERE MADT=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, mads);
                    ps.setString(2, tfMABS.getText().trim());
                    ps.setString(3, tfGhiChu.getText().trim());

                    if (tfNgayBan.getText().trim().isEmpty()) {
                        ps.setNull(4, Types.DATE);
                    } else {
                        ps.setDate(4, java.sql.Date.valueOf(tfNgayBan.getText().trim().substring(0, 10)));
                    }

                    if (tfThanhTien.getText().trim().isEmpty()) {
                        ps.setNull(5, Types.NUMERIC);
                    } else {
                        ps.setDouble(5, Double.parseDouble(tfThanhTien.getText().trim()));
                    }

                    ps.setString(6, cbTrangThai.getSelectedItem().toString());
                    ps.setString(7, madt);

                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                    dialog.dispose();
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật: " + ex.getMessage());
            }
        });

        // Mở chi tiết đơn thuốc
        btnXemChiTiet.addActionListener(e -> {
            dialog.dispose();
            CTDT ctdt = new CTDT(madt, () -> {
                loadData(); // callback sau khi xem CTDT
            });
            ctdt.setVisible(true);
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private ImageIcon getPrescriptionImage(String madt) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT FILEDONTHUOC FROM DONTHUOC_DONTHUOCYC WHERE MADT=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, madt);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Blob blob = rs.getBlob("FILEDONTHUOC");
                    if (blob != null) {
                        byte[] bytes = blob.getBytes(1, (int) blob.length());
                        ImageIcon icon = new ImageIcon(bytes);
                        Image scaledImage = icon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải ảnh: " + ex.getMessage());
        }
        return null;
    }
} 