package com.srms.desktop.ui;

import com.srms.desktop.dao.StudentDAO;
import com.srms.desktop.db.DBConnection;
import com.srms.desktop.model.Department;
import com.srms.desktop.model.Student;
import com.srms.desktop.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Main window: JTable of students + form for Add / Update / Delete / Search (JDBC). */
public class MainFrame extends JFrame {

    private final StudentDAO dao = new StudentDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Enrollment No", "First Name", "Last Name", "Email", "Phone", "Gender", "DOB", "Year", "CGPA", "City", "Dept"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    final JTextField txtEnr = field("enrollmentNo"), txtFirst = field("firstName"), txtLast = field("lastName"),
            txtEmail = field("email"), txtPhone = field("phone"), txtDob = field("dob"), txtYear = field("year"),
            txtCgpa = field("cgpa"), txtCity = field("city"), txtSearch = field("search");
    final JComboBox<String> cmbGender = new JComboBox<>(new String[]{"", "Male", "Female", "Other"});
    final JComboBox<Department> cmbDept = new JComboBox<>();
    final JButton btnAdd = new JButton("Add"), btnUpdate = new JButton("Update"), btnDelete = new JButton("Delete"),
            btnClear = new JButton("Clear"), btnSearch = new JButton("Search"), btnRefresh = new JButton("Show All");
    private final JLabel lblStatus = new JLabel(" ");
    private Integer selectedId = null;

    public MainFrame(String username, String fullName) {
        super("SRMS Desktop - Student Records (Swing + JDBC)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cmbGender.setName("gender");
        cmbDept.setName("department");
        btnAdd.setName("add"); btnUpdate.setName("update"); btnDelete.setName("delete");
        btnClear.setName("clear"); btnSearch.setName("searchButton"); btnRefresh.setName("refresh");
        table.setName("table");

        // ---- top bar ----
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(31, 78, 156));
        top.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel title = new JLabel("Student Record Management System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        title.setForeground(Color.WHITE);
        JLabel who = new JLabel("Logged in: " + fullName + " (" + username + ")  since "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        who.setForeground(new Color(220, 230, 255));
        JButton btnLogout = new JButton("Logout");
        btnLogout.setName("logout");
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(who);
        right.add(btnLogout);
        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        // ---- form ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student details"));
        Object[][] rows = {
                {"Enrollment No *", txtEnr}, {"First Name *", txtFirst}, {"Last Name *", txtLast},
                {"Email *", txtEmail}, {"Mobile *", txtPhone}, {"Gender *", cmbGender},
                {"DOB (yyyy-mm-dd) *", txtDob}, {"Year (1-4) *", txtYear}, {"CGPA (0-10)", txtCgpa},
                {"City", txtCity}, {"Department *", cmbDept}};
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < rows.length; i++) {
            c.gridx = 0; c.gridy = i; c.weightx = 0;
            form.add(new JLabel((String) rows[i][0]), c);
            c.gridx = 1; c.weightx = 1;
            form.add((Component) rows[i][1], c);
        }
        JPanel btns = new JPanel(new GridLayout(2, 2, 6, 6));
        btns.add(btnAdd); btns.add(btnUpdate); btns.add(btnDelete); btns.add(btnClear);
        c.gridx = 0; c.gridy = rows.length; c.gridwidth = 2; c.insets = new Insets(12, 6, 4, 6);
        form.add(btns, c);
        form.setPreferredSize(new Dimension(360, 520));

        // ---- table + search ----
        JPanel center = new JPanel(new BorderLayout(6, 6));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch.setColumns(26);
        search.add(new JLabel("Search (name / enrollment / email / city / dept):"));
        search.add(txtSearch);
        search.add(btnSearch);
        search.add(btnRefresh);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] widths = {35, 105, 80, 75, 250, 90, 60, 90, 40, 50, 80, 45};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        center.add(search, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 8));

        lblStatus.setName("status");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        add(top, BorderLayout.NORTH);
        add(form, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        // ---- events ----
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> load(txtSearch.getText()));
        txtSearch.addActionListener(e -> load(txtSearch.getText()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); load(null); });
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) fillFromTable(); });

        loadDepartments();
        load(null);
        setSize(1380, 660);
        setLocationRelativeTo(null);
    }

    private static JTextField field(String name) {
        JTextField f = new JTextField(16);
        f.setName(name);
        return f;
    }

    private void loadDepartments() {
        try {
            cmbDept.removeAllItems();
            for (Department d : dao.departments()) cmbDept.addItem(d);
            cmbDept.setSelectedIndex(-1);
        } catch (SQLException ex) {
            error(ex);
        }
    }

    /** READ: fill the JTable from the database. */
    void load(String keyword) {
        try {
            List<Student> list = dao.search(keyword);
            table.clearSelection();
            model.setRowCount(0);
            for (Student s : list) {
                model.addRow(new Object[]{s.getId(), s.getEnrollmentNo(), s.getFirstName(), s.getLastName(), s.getEmail(),
                        s.getPhone(), s.getGender(), s.getDob(), s.getYearOfStudy(),
                        s.getCgpa() == null ? "" : s.getCgpa(), s.getCity() == null ? "" : s.getCity(), s.getDeptCode()});
            }
            status(list.size() + " student(s) loaded from " + DBConnection.url().replaceAll("\\?.*", ""));
        } catch (SQLException ex) {
            error(ex);
        }
    }

    private Student readForm() {
        List<String> errors = Validator.validateStudent(txtEnr.getText().trim(), txtFirst.getText().trim(),
                txtLast.getText().trim(), txtEmail.getText().trim(), txtPhone.getText().trim(),
                (String) cmbGender.getSelectedItem(), txtDob.getText().trim(), txtYear.getText().trim(),
                txtCgpa.getText().trim(), cmbDept.getSelectedItem());
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fix the following:\n\n- " + String.join("\n- ", errors),
                    "Validation error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Student s = new Student();
        s.setEnrollmentNo(txtEnr.getText().trim());
        s.setFirstName(txtFirst.getText().trim());
        s.setLastName(txtLast.getText().trim());
        s.setEmail(txtEmail.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        s.setGender((String) cmbGender.getSelectedItem());
        s.setDob(LocalDate.parse(txtDob.getText().trim()));
        s.setYearOfStudy(Integer.parseInt(txtYear.getText().trim()));
        s.setCgpa(txtCgpa.getText().isBlank() ? null : new BigDecimal(txtCgpa.getText().trim()));
        s.setCity(txtCity.getText().trim());
        s.setDeptId(((Department) cmbDept.getSelectedItem()).getId());
        return s;
    }

    /** CREATE */
    private void addStudent() {
        Student s = readForm();
        if (s == null) return;
        try {
            dao.insert(s);
            load(null);
            clearForm();
            status("Student added: " + s.getFirstName() + " " + s.getLastName() + " (id " + s.getId() + ")");
            JOptionPane.showMessageDialog(this, "Student added successfully (id " + s.getId() + ").", "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            error(ex);
        }
    }

    /** UPDATE */
    private void updateStudent() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a student in the table first.", "Update", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student s = readForm();
        if (s == null) return;
        s.setId(selectedId);
        try {
            int n = dao.update(s);
            load(txtSearch.getText());
            status(n + " row updated: " + s.getEnrollmentNo());
            JOptionPane.showMessageDialog(this, "Student updated successfully.", "Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            error(ex);
        }
    }

    /** DELETE */
    private void deleteStudent() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Select a student in the table first.", "Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Delete student " + txtEnr.getText() + " - " + txtFirst.getText()
                + " " + txtLast.getText() + "?\nTheir course enrollments will also be removed.", "Confirm delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            int n = dao.delete(selectedId);
            clearForm();
            load(txtSearch.getText());
            status(n + " student deleted.");
        } catch (SQLException ex) {
            error(ex);
        }
    }

    private void fillFromTable() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= table.getRowCount()) return;
        int m = table.convertRowIndexToModel(row);
        selectedId = (Integer) model.getValueAt(m, 0);
        txtEnr.setText(str(model.getValueAt(m, 1)));
        txtFirst.setText(str(model.getValueAt(m, 2)));
        txtLast.setText(str(model.getValueAt(m, 3)));
        txtEmail.setText(str(model.getValueAt(m, 4)));
        txtPhone.setText(str(model.getValueAt(m, 5)));
        cmbGender.setSelectedItem(str(model.getValueAt(m, 6)));
        txtDob.setText(str(model.getValueAt(m, 7)));
        txtYear.setText(str(model.getValueAt(m, 8)));
        txtCgpa.setText(str(model.getValueAt(m, 9)));
        txtCity.setText(str(model.getValueAt(m, 10)));
        String code = str(model.getValueAt(m, 11));
        for (int i = 0; i < cmbDept.getItemCount(); i++) {
            if (cmbDept.getItemAt(i).getCode().equals(code)) cmbDept.setSelectedIndex(i);
        }
        status("Selected student id " + selectedId);
    }

    void clearForm() {
        selectedId = null;
        for (JTextField f : new JTextField[]{txtEnr, txtFirst, txtLast, txtEmail, txtPhone, txtDob, txtYear, txtCgpa, txtCity})
            f.setText("");
        cmbGender.setSelectedIndex(0);
        cmbDept.setSelectedIndex(-1);
        table.clearSelection();
    }

    JTable table() { return table; }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private void status(String msg) {
        lblStatus.setForeground(new Color(46, 125, 50));
        lblStatus.setText(msg);
    }

    private void error(SQLException ex) {
        lblStatus.setForeground(new Color(198, 40, 40));
        lblStatus.setText("Error: " + ex.getMessage());
        JOptionPane.showMessageDialog(this, Validator.friendly(ex), "Database error", JOptionPane.ERROR_MESSAGE);
    }
}
