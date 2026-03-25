package ttLogistics.controllers;

import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class DriverScreen extends JFrame {

    DefaultTableModel model = new DefaultTableModel();
    JTable table;

    JTextField fName, fAddr, fPhone, fDob, fEmpNo, fSalary, fHire, fConNo, fRate;
    JComboBox<String> driverType;

    JTextField aPid, aDid, aHours;
    JComboBox<String> aRole;

    public DriverScreen() {
        setTitle("Driver Management");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG);
        setLayout(new BorderLayout());
        add(UIHelper.hdr("DM", "Driver Management"), BorderLayout.NORTH);
        table = UIHelper.styledTable(model);

        JPanel sideInner = new JPanel();
        sideInner.setLayout(new BoxLayout(sideInner, BoxLayout.Y_AXIS));
        sideInner.setBackground(UIHelper.BG);

        JPanel addCard = UIHelper.sideCard("Add New Driver", UIHelper.ACCENT);
        fName = UIHelper.field(18, "e.g., John Smith"); fAddr = UIHelper.field(18, "e.g., 123 Oak Ave");
        fPhone = UIHelper.field(18, "e.g., 670-555-0123"); fDob = UIHelper.field(18, "YYYY-MM-DD");
        UIHelper.row(addCard, "Full Name",     fName);
        UIHelper.row(addCard, "Address",       fAddr);
        UIHelper.row(addCard, "Phone",         fPhone);
        UIHelper.row(addCard, "Date of Birth", fDob);
        driverType = UIHelper.combo("Full-Time", "Contract");
        UIHelper.row(addCard, "Driver Type",   driverType);
        fEmpNo = UIHelper.field(18, "e.g., E001"); fSalary = UIHelper.field(18, "e.g., 25000"); fHire = UIHelper.field(18, "YYYY-MM-DD");
        UIHelper.row(addCard, "Employee No.", fEmpNo);
        UIHelper.row(addCard, "Salary (M)",   fSalary);
        UIHelper.row(addCard, "Hire Date",    fHire);
        fConNo = UIHelper.field(18, "e.g., CON-001"); fRate = UIHelper.field(18, "e.g., 150");
        UIHelper.row(addCard, "Contract No.", fConNo);
        UIHelper.row(addCard, "Hourly Rate",  fRate);
        JButton btnAdd = UIHelper.btn("Add Driver + Create Login", UIHelper.ACCENT);
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        addCard.add(Box.createVerticalStrut(12));
        addCard.add(btnAdd);

        JPanel editCard = UIHelper.sideCard("Edit Selected Driver", UIHelper.GOLD);
        JTextField eName = UIHelper.field(18, "Full name"), eAddr = UIHelper.field(18, "Address"), ePhone = UIHelper.field(18, "Phone");
        JTextField eEmpNo = UIHelper.field(18, "Emp #"), eSalary = UIHelper.field(18, "Monthly salary"), eHire = UIHelper.field(18, "YYYY-MM-DD");
        JTextField eConNo = UIHelper.field(18, "Contract #"), eRate = UIHelper.field(18, "Hourly rate");
        UIHelper.row(editCard, "Full Name",    eName);
        UIHelper.row(editCard, "Address",      eAddr);
        UIHelper.row(editCard, "Phone",        ePhone);
        UIHelper.row(editCard, "Employee No.", eEmpNo);
        UIHelper.row(editCard, "Salary (M)",   eSalary);
        UIHelper.row(editCard, "Hire Date",    eHire);
        UIHelper.row(editCard, "Contract No.", eConNo);
        UIHelper.row(editCard, "Hourly Rate",  eRate);
        JButton btnLoad4Edit = UIHelper.btn("Load Selected", new Color(80, 120, 200));
        JButton btnSaveEdit  = UIHelper.btn("Save Changes", UIHelper.GOLD);
        JPanel editBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        editBtns.setBackground(UIHelper.CARD);
        editBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        editBtns.add(btnLoad4Edit);
        editBtns.add(btnSaveEdit);
        editCard.add(Box.createVerticalStrut(10));
        editCard.add(editBtns);

        JPanel assignCard = UIHelper.sideCard("Assign to Delivery", new Color(160, 90, 220));
        aPid = UIHelper.field(18, "Person ID"); aDid = UIHelper.field(18, "Delivery ID");
        aHours = UIHelper.field(18, "Hours worked"); aRole = UIHelper.combo("main driver", "assistant");
        UIHelper.row(assignCard, "Person ID",    aPid);
        UIHelper.row(assignCard, "Delivery ID",  aDid);
        UIHelper.row(assignCard, "Role",         aRole);
        UIHelper.row(assignCard, "Hours Worked", aHours);
        JButton btnAssign = UIHelper.btn("Assign Driver", new Color(160, 90, 220));
        btnAssign.setAlignmentX(Component.LEFT_ALIGNMENT);
        assignCard.add(Box.createVerticalStrut(10));
        assignCard.add(btnAssign);

        sideInner.add(addCard);
        sideInner.add(Box.createVerticalStrut(10));
        sideInner.add(editCard);
        sideInner.add(Box.createVerticalStrut(10));
        sideInner.add(assignCard);
        sideInner.add(Box.createVerticalStrut(10));

        JScrollPane sideScroll = new JScrollPane(sideInner);
        sideScroll.getViewport().setBackground(UIHelper.BG);
        sideScroll.setBorder(null);
        sideScroll.setPreferredSize(new Dimension(320, 0));
        sideScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, UIHelper.scrollFor(table), sideScroll);
        split.setDividerLocation(750);
        split.setBackground(UIHelper.BG);
        add(split, BorderLayout.CENTER);

        JButton btnFT     = UIHelper.btn("Full-Time Drivers", UIHelper.ACCENT);
        JButton btnCon    = UIHelper.btn("Contract Drivers", UIHelper.BLUE);
        JButton btnAss    = UIHelper.btn("Assignments", new Color(160, 90, 220));
        JButton btnAll    = UIHelper.btn("All Persons", UIHelper.DIM);
        JButton btnRefresh= UIHelper.btn("Refresh", new Color(0, 160, 160));
        JButton btnDelete = UIHelper.btn("Delete Driver", UIHelper.RED);
        JButton btnBack   = UIHelper.btn("← Back to Menu", new Color(120, 120, 120));
        btnBack.addActionListener(e -> { dispose(); new MainMenu(); });
        add(UIHelper.bottomBar(this, btnBack, btnFT, btnCon, btnAss, btnAll, btnRefresh, btnDelete), BorderLayout.SOUTH);

        final String[] lastQuery = {null};

        btnFT.addActionListener(e -> { lastQuery[0] = "FT"; DBConnection.loadTable(table,
            "SELECT p.person_id,p.full_name,p.phone,p.date_of_birth," +
            "ft.employee_number,ft.salary,ft.hire_date " +
            "FROM Person p JOIN Full_Time_Driver ft ON p.person_id=ft.person_id"); });

        btnCon.addActionListener(e -> { lastQuery[0] = "CON"; DBConnection.loadTable(table,
            "SELECT p.person_id,p.full_name,p.phone,p.date_of_birth," +
            "cd.contract_number,cd.hourly_rate " +
            "FROM Person p JOIN Contract_Driver cd ON p.person_id=cd.person_id"); });

        btnAss.addActionListener(e -> { lastQuery[0] = "ASS"; DBConnection.loadTable(table,
            "SELECT da.person_id,p.full_name AS driver,da.delivery_id," +
            "da.role,da.hours_worked FROM Driver_Assignment da " +
            "JOIN Person p ON da.person_id=p.person_id ORDER BY da.delivery_id"); });

        btnAll.addActionListener(e -> { lastQuery[0] = "ALL"; DBConnection.loadTable(table,
            "SELECT p.person_id,p.full_name,p.phone,p.date_of_birth, " +
            "CASE WHEN ft.person_id IS NOT NULL THEN 'Full-Time' " +
            "WHEN cd.person_id IS NOT NULL THEN 'Contract' " +
            "WHEN fm.person_id IS NOT NULL THEN 'Fleet Manager' ELSE 'Person' END AS role_type " +
            "FROM Person p LEFT JOIN Full_Time_Driver ft ON p.person_id=ft.person_id " +
            "LEFT JOIN Contract_Driver cd ON p.person_id=cd.person_id " +
            "LEFT JOIN Fleet_Manager fm ON p.person_id=fm.person_id"); });

        btnRefresh.addActionListener(e -> {
            if (lastQuery[0] == null) { btnFT.doClick(); return; }
            switch (lastQuery[0]) {
                case "FT"  -> btnFT.doClick();
                case "CON" -> btnCon.doClick();
                case "ASS" -> btnAss.doClick();
                case "ALL" -> btnAll.doClick();
            }
        });

        btnAdd.addActionListener(e -> {
            if (fName.getText().trim().isEmpty()) { UIHelper.err("Full name is required."); return; }

            JPanel loginPrompt = new JPanel(new GridLayout(4, 1, 4, 6));
            loginPrompt.setBackground(UIHelper.BG);
            JLabel lu2 = new JLabel("Set login credentials for this driver:");
            lu2.setFont(UIHelper.LBL); lu2.setForeground(UIHelper.TEXT); loginPrompt.add(lu2);
            JTextField newUser = new JTextField(15); loginPrompt.add(newUser);
            JLabel lp2 = new JLabel("Password:");
            lp2.setFont(UIHelper.LBL); lp2.setForeground(UIHelper.TEXT); loginPrompt.add(lp2);
            JPasswordField newPass = new JPasswordField(15); loginPrompt.add(newPass);

            int result = JOptionPane.showConfirmDialog(this, loginPrompt,
                "Create Driver Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            String username = newUser.getText().trim();
            String password = new String(newPass.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) { UIHelper.err("Username and password are required."); return; }

            try {
                DBConnection.conn.setAutoCommit(false);

                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "INSERT INTO Person(full_name,address,phone,date_of_birth) VALUES(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, fName.getText().trim()); ps.setString(2, fAddr.getText().trim());
                ps.setString(3, fPhone.getText().trim()); ps.setString(4, fDob.getText().trim());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                int pid = keys.next() ? keys.getInt(1) : -1;
                keys.close(); ps.close();

                if ("Full-Time".equals(driverType.getSelectedItem())) {
                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "INSERT INTO Full_Time_Driver(person_id,employee_number,salary,hire_date) VALUES(?,?,?,?)");
                    ps2.setInt(1, pid); ps2.setString(2, fEmpNo.getText().trim());
                    ps2.setDouble(3, Double.parseDouble(fSalary.getText().trim()));
                    ps2.setString(4, fHire.getText().trim());
                    ps2.executeUpdate(); ps2.close();
                } else {
                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "INSERT INTO Contract_Driver(person_id,contract_number,hourly_rate) VALUES(?,?,?)");
                    ps2.setInt(1, pid); ps2.setString(2, fConNo.getText().trim());
                    ps2.setDouble(3, Double.parseDouble(fRate.getText().trim()));
                    ps2.executeUpdate(); ps2.close();
                }

                PreparedStatement ps3 = DBConnection.conn.prepareStatement(
                    "INSERT INTO App_Users(username,password_hash,role,person_id) VALUES(?,SHA2(?,256),'driver',?)");
                ps3.setString(1, username); ps3.setString(2, password); ps3.setInt(3, pid);
                ps3.executeUpdate(); ps3.close();

                Statement st = DBConnection.conn.createStatement();
                st.execute("CREATE USER IF NOT EXISTS '" + username + "'@'localhost' IDENTIFIED BY '" + password + "'");
                st.execute("GRANT SELECT ON ttlogistics.* TO '" + username + "'@'localhost'");
                st.execute("GRANT UPDATE ON ttlogistics.Person TO '" + username + "'@'localhost'");
                st.execute("FLUSH PRIVILEGES");
                st.close();

                DBConnection.conn.commit(); DBConnection.conn.setAutoCommit(true);
                UIHelper.info("Driver added!\nPerson ID: " + pid + "\nLogin: " + username + " / " + password);
                for (JTextField f2 : new JTextField[]{fName, fAddr, fPhone, fDob, fEmpNo, fSalary, fHire, fConNo, fRate})
                    f2.setText("");
                btnFT.doClick();
            } catch (Exception ex) {
                try { DBConnection.conn.rollback(); DBConnection.conn.setAutoCommit(true); } catch (Exception ignored) {}
                UIHelper.err("Add driver error:\n" + ex.getMessage());
            }
        });

        btnLoad4Edit.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r < 0) { UIHelper.err("Select a driver row first."); return; }
            int pid = (int) model.getValueAt(r, 0);
            try {
                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "SELECT p.full_name,p.address,p.phone, " +
                    "ft.employee_number,ft.salary,ft.hire_date, " +
                    "cd.contract_number,cd.hourly_rate " +
                    "FROM Person p " +
                    "LEFT JOIN Full_Time_Driver ft ON p.person_id=ft.person_id " +
                    "LEFT JOIN Contract_Driver  cd ON p.person_id=cd.person_id " +
                    "WHERE p.person_id=?");
                ps.setInt(1, pid); ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    eName.setText(rs.getString("full_name") != null ? rs.getString("full_name") : "");
                    eAddr.setText(rs.getString("address")   != null ? rs.getString("address") : "");
                    ePhone.setText(rs.getString("phone")    != null ? rs.getString("phone") : "");
                    eEmpNo.setText(rs.getString("employee_number") != null ? rs.getString("employee_number") : "");
                    eSalary.setText(rs.getString("salary")  != null ? rs.getString("salary") : "");
                    eHire.setText(rs.getString("hire_date") != null ? rs.getString("hire_date") : "");
                    eConNo.setText(rs.getString("contract_number") != null ? rs.getString("contract_number") : "");
                    eRate.setText(rs.getString("hourly_rate") != null ? rs.getString("hourly_rate") : "");
                    editCard.putClientProperty("editing_pid", pid);
                }
                rs.close(); ps.close();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnSaveEdit.addActionListener(e -> {
            Object pidObj = editCard.getClientProperty("editing_pid");
            if (pidObj == null) { UIHelper.err("Load a driver first."); return; }
            int pid = (int) pidObj;
            try {
                DBConnection.conn.setAutoCommit(false);
                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "UPDATE Person SET full_name=?,address=?,phone=? WHERE person_id=?");
                ps.setString(1, eName.getText().trim()); ps.setString(2, eAddr.getText().trim());
                ps.setString(3, ePhone.getText().trim()); ps.setInt(4, pid);
                ps.executeUpdate(); ps.close();
                if (!eEmpNo.getText().trim().isEmpty() || !eSalary.getText().trim().isEmpty()) {
                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "UPDATE Full_Time_Driver SET employee_number=?,salary=?,hire_date=? WHERE person_id=?");
                    ps2.setString(1, eEmpNo.getText().trim());
                    ps2.setDouble(2, eSalary.getText().trim().isEmpty() ? 0 : Double.parseDouble(eSalary.getText().trim()));
                    ps2.setString(3, eHire.getText().trim()); ps2.setInt(4, pid);
                    ps2.executeUpdate(); ps2.close();
                }
                if (!eConNo.getText().trim().isEmpty() || !eRate.getText().trim().isEmpty()) {
                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "UPDATE Contract_Driver SET contract_number=?,hourly_rate=? WHERE person_id=?");
                    ps2.setString(1, eConNo.getText().trim());
                    ps2.setDouble(2, eRate.getText().trim().isEmpty() ? 0 : Double.parseDouble(eRate.getText().trim()));
                    ps2.setInt(3, pid);
                    ps2.executeUpdate(); ps2.close();
                }
                DBConnection.conn.commit(); DBConnection.conn.setAutoCommit(true);
                UIHelper.info("Driver #" + pid + " updated!");
                btnAll.doClick();
            } catch (Exception ex) {
                try { DBConnection.conn.rollback(); DBConnection.conn.setAutoCommit(true); } catch (Exception ignored) {}
                UIHelper.err("Save error:\n" + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r < 0) { UIHelper.err("Select a driver to delete."); return; }
            int pid = (int) model.getValueAt(r, 0);
            String name = model.getValueAt(r, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete driver: " + name + " (ID: " + pid + ")?\n\n" +
                "This will also delete:\n • All their driver records\n" +
                " • All their delivery assignments\n • Their login account\n\nThis CANNOT be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    DBConnection.conn.setAutoCommit(false);
                    PreparedStatement ps0 = DBConnection.conn.prepareStatement(
                        "SELECT username FROM App_Users WHERE person_id=? AND role='driver'");
                    ps0.setInt(1, pid); ResultSet rs0 = ps0.executeQuery();
                    String driverUsername = rs0.next() ? rs0.getString("username") : null;
                    rs0.close(); ps0.close();

                    PreparedStatement ps1 = DBConnection.conn.prepareStatement(
                        "DELETE FROM App_Users WHERE person_id=?");
                    ps1.setInt(1, pid); ps1.executeUpdate(); ps1.close();

                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "DELETE FROM Person WHERE person_id=?");
                    ps2.setInt(1, pid); ps2.executeUpdate(); ps2.close();

                    if (driverUsername != null) {
                        Statement st = DBConnection.conn.createStatement();
                        st.execute("DROP USER IF EXISTS '" + driverUsername + "'@'localhost'");
                        st.execute("FLUSH PRIVILEGES");
                        st.close();
                    }
                    DBConnection.conn.commit(); DBConnection.conn.setAutoCommit(true);
                    UIHelper.info("Driver " + name + " deleted successfully.");
                    btnAll.doClick();
                } catch (Exception ex) {
                    try { DBConnection.conn.rollback(); DBConnection.conn.setAutoCommit(true); } catch (Exception ignored) {}
                    UIHelper.err("Delete error:\n" + ex.getMessage());
                }
            }
        });

        btnAssign.addActionListener(e -> {
            try {
                DBConnection.callProc("{CALL assign_driver_to_delivery(?,?,?,?)}",
                    Integer.parseInt(aPid.getText().trim()),
                    Integer.parseInt(aDid.getText().trim()),
                    (String) aRole.getSelectedItem(),
                    Double.parseDouble(aHours.getText().trim()));
                btnAss.doClick();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnFT.doClick();
        setVisible(true);
    }
}
