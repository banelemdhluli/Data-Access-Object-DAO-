package hospitalmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * DoctorDAO is a Java Swing GUI application that allows users to:
 * Enter doctor details
 * Save doctors to MySQL database
 * Display all saved doctors
 * Delete doctors by DoctorID
 * Update doctors
 */

public class DoctorDAO extends JFrame {

    // Swing components
    private JTextField firstNameField, lastNameField, specialtyField, phoneField, emailField;
    private JTextField deleteIdField; // field for deleting a doctor
    private JTextArea displayArea;

    private JButton saveButton, displayButton, deleteButton, updateButton;

    // Constructor that sets up the GUI layout and event handling
    public DoctorDAO() {
        setTitle("Doctor Management");
        setSize(650, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 2)); // 12 rows, 2 columns

        // ======================= INPUT FIELDS =======================
        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        add(lastNameField);

        add(new JLabel("Specialty:"));
        specialtyField = new JTextField();
        add(specialtyField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        // ======================= BUTTONS =======================
        saveButton = new JButton("Save Doctor");
        displayButton = new JButton("Display All Doctors");
        deleteButton = new JButton("Delete Doctor");
        updateButton = new JButton("Update Doctor"); // FIXED (previously missing)

        add(saveButton);
        add(displayButton);
        add(updateButton);

        // ======================= DELETE SECTION =======================
        add(new JLabel("Doctor ID to Delete:"));
        deleteIdField = new JTextField();
        add(deleteIdField);
        add(deleteButton);

        // ======================= DISPLAY AREA =======================
        displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane);

        // filler
        add(new JLabel());

        // ======================= BUTTON ACTIONS =======================
        saveButton.addActionListener(e -> saveDoctor());
        displayButton.addActionListener(e -> displayDoctors());
        updateButton.addActionListener(e -> updateDoctor());
        deleteButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(deleteIdField.getText());
                deleteDoctor(id);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid Doctor ID!");
            }
        });

        setVisible(true);
    }

    // ======================= DATABASE CONNECTION =======================

    // Establishes connection to the MySQL database
    // @return Connection object or null if connection fails

    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/hospital_management_system";
        String user = "root";
        String password = ""; // Your SQL password

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed:\n" + e.getMessage());
            return null;
        }
    }

    // ======================= ADD DOCTOR =======================

    // Inserts a new doctor record into the doctor table.

    public void addDoctor(Doctor doctor) {
        String sql = "INSERT INTO DOCTORS (FirstName, LastName, Specialty, Phone, Email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialty());
            stmt.setString(4, doctor.getPhone());
            stmt.setString(5, doctor.getEmail());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Doctor saved successfully.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving doctor: " + e.getMessage());
        }
    }

    // ======================= DISPLAY DOCTOR =======================

    // Queries and displays all doctor records from the database in the text area.

    public void displayDoctors() {
        String sql = "SELECT * FROM DOCTORS";
        displayArea.setText("DoctorID | FirstName | LastName | Specialty | Phone | Email\n");

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                displayArea.append(
                        rs.getInt("DoctorID") + " | " +
                                rs.getString("FirstName") + " | " +
                                rs.getString("LastName") + " | " +
                                rs.getString("Specialty") + " | " +
                                rs.getString("Phone") + " | " +
                                rs.getString("Email") + "\n"
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving doctors: " + e.getMessage());
        }
    }

    // ======================= DELETE DOCTOR =======================

    // Deletes doctor record into the DOCTORS table.


    public void deleteDoctor(int doctorID) {
        String sql = "DELETE FROM DOCTORS WHERE DoctorID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorID);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!");
                displayDoctors();
            } else {
                JOptionPane.showMessageDialog(this, "No doctor found with ID: " + doctorID);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting doctor: " + e.getMessage());
        }
    }

    // ======================= SAVE DOCTOR =======================

    // Reads input field values, creates a Doctor  object, and saves it to the database.
    // Handles conversion of string to LocalDate.

    private void saveDoctor() {
        Doctor doctor = new Doctor(
                firstNameField.getText(),
                lastNameField.getText(),
                specialtyField.getText(),
                phoneField.getText(),
                emailField.getText()
        );

        addDoctor(doctor);
    }

    // ======================= UPDATE DOCTOR =======================

    // Update doctor record on DOCTORS table

    private void updateDoctor() {
        try {

            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String specialty = specialtyField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();

            String sql = "UPDATE doctors SET FirstName = ?, LastName = ?, Specialty = ?, Phone = ?, Email = ? WHERE DoctorID = ?";

            try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, specialty);
                stmt.setString(4, phone);
                stmt.setString(5, email);
                stmt.setInt(6, Integer.parseInt(deleteIdField.getText())); // FIXED missing WHERE ID

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Doctor updated successfully!");
                    displayDoctors();
                } else {
                    JOptionPane.showMessageDialog(this, "Doctor ID not found!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage());
        }
    }

    // ======================= MAIN METHOD =======================

    // To run the application

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoctorDAO::new);
    }

    // ======================= DOCTOR MODEL CLASS =======================

    // Inner class representing a Doctor entity with fields
    // matching the columns in the DOCTORS database table.

    public static class Doctor {
        private int doctorID;
        private String firstName;
        private String lastName;
        private String specialty;
        private String phone;
        private String email;


        // Constructor
        public Doctor(String firstName, String lastName, String specialty, String phone, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.specialty = specialty;
            this.phone = phone;
            this.email = email;
        }

        // Getter methods
        public int getDoctorID() { return doctorID; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getSpecialty() { return specialty; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
    }
}
