package ttLogistics.db;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class UIHelper {

    public static final Color BG     = new Color(13, 18, 28);
    public static final Color PANEL  = new Color(22, 29, 42);
    public static final Color CARD   = new Color(30, 40, 58);
    public static final Color ACCENT = new Color(0, 190, 130);
    public static final Color GOLD   = new Color(255, 185, 50);
    public static final Color RED    = new Color(215, 60, 60);
    public static final Color BLUE   = new Color(60, 130, 220);
    public static final Color TEXT   = new Color(220, 228, 242);
    public static final Color DIM    = new Color(100, 120, 148);
    public static final Color ROW2   = new Color(38, 50, 68);
    public static final Font  HEAD   = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font  LBL    = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font  BODY   = new Font("Segoe UI", Font.PLAIN, 12);

    public static JTextField field(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(new Color(40, 52, 72));
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 70, 95)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return f;
    }

    public static JTextField field(int cols, String placeholder) {
        JTextField f = field(cols);
        f.setText(placeholder);
        f.setForeground(new Color(130, 150, 180)); // Dimmer color for placeholder
        f.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(new Color(130, 150, 180));
                }
            }
        });
        return f;
    }

    public static JPasswordField passField() {
        JPasswordField f = new JPasswordField(20);
        f.setBackground(new Color(40, 52, 72));
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 70, 95)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return f;
    }

    public static JComboBox<String> combo(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(new Color(40, 52, 72));
        c.setForeground(TEXT);
        c.setFont(BODY);
        return c;
    }

    public static JComboBox<String> dropdownFromMap(java.util.Map<Integer, String> dataMap) {
        JComboBox<String> c = new JComboBox<>(dataMap.values().toArray(new String[0]));
        c.setBackground(new Color(40, 52, 72));
        c.setForeground(TEXT);
        c.setFont(BODY);
        // Store ID values as map for retrieval
        java.util.List<Integer> ids = new java.util.ArrayList<>(dataMap.keySet());
        c.putClientProperty("ids", ids);
        return c;
    }

    public static Integer getSelectedId(JComboBox<String> dropdown) {
        @SuppressWarnings("unchecked")
        java.util.List<Integer> ids = (java.util.List<Integer>) dropdown.getClientProperty("ids");
        int selectedIndex = dropdown.getSelectedIndex();
        if (selectedIndex >= 0 && ids != null && selectedIndex < ids.size()) {
            return ids.get(selectedIndex);
        }
        return -1;
    }

    public static JButton btn(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(c.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(c); }
        });
        return b;
    }

    public static JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(DIM);
        l.setFont(LBL);
        return l;
    }

    public static JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        t.setBackground(CARD);
        t.setForeground(TEXT);
        t.setFont(BODY);
        t.setRowHeight(26);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0, 130, 90));
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setBackground(PANEL);
        t.getTableHeader().setForeground(ACCENT);
        t.getTableHeader().setFont(LBL);
        t.getTableHeader().setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean f, int row, int col) {
                super.getTableCellRendererComponent(tb, v, sel, f, row, col);
                setBackground(sel ? new Color(0, 130, 90) : row % 2 == 0 ? CARD : ROW2);
                setForeground(sel ? Color.WHITE : TEXT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
        return t;
    }

    public static JScrollPane scrollFor(JTable t) {
        JScrollPane s = new JScrollPane(t);
        s.getViewport().setBackground(CARD);
        s.setBorder(BorderFactory.createLineBorder(new Color(45, 58, 78)));
        return s;
    }

    public static JPanel sideCard(String title, Color accent) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        if (!title.isEmpty()) {
            JLabel h = new JLabel(title);
            h.setFont(HEAD);
            h.setForeground(accent);
            h.setAlignmentX(Component.LEFT_ALIGNMENT);
            h.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            p.add(h);
        }
        return p;
    }

    public static void row(JPanel p, String labelText, JComponent f) {
        JLabel l = lbl(labelText);
        l.setBorder(BorderFactory.createEmptyBorder(6, 0, 2, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(f);
    }

    public static JPanel hdr(String icon, String title) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(PANEL);
        h.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        JLabel l = new JLabel(icon + "  " + title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 17));
        l.setForeground(ACCENT);
        h.add(l, BorderLayout.WEST);
        return h;
    }

    public static JPanel bar(JComponent... comps) {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        b.setBackground(PANEL);
        for (JComponent c : comps) b.add(c);
        return b;
    }

    public static JPanel bottomBar(JFrame owner, JComponent... leftComps) {
        JPanel b = new JPanel(new BorderLayout());
        b.setBackground(PANEL);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        left.setBackground(PANEL);
        for (JComponent c : leftComps) left.add(c);
        JButton btnLogout = btn("Logout", RED);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnLogout.addActionListener(e -> doLogout(owner));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setBackground(PANEL);
        right.add(btnLogout);
        b.add(left, BorderLayout.WEST);
        b.add(right, BorderLayout.EAST);
        return b;
    }

    public static void doLogout(JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Close all open frames
            java.awt.Frame[] allFrames = java.awt.Frame.getFrames();
            for (java.awt.Frame f : allFrames) {
                f.dispose();
            }
            // Close database connection
            DBConnection.closeConnection();
            // Open login screen
            new ttLogistics.controllers.LoginScreen();
        }
    }

    public static void info(String m) {
        JOptionPane.showMessageDialog(null, m, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void err(String m) {
        JOptionPane.showMessageDialog(null, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
