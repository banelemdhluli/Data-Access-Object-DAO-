package hospitalmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * PatientDAO is a Java Swing GUI application that allows user to:
 * Enter patient details
 * Save patients to MySQL database
 * Display all saved patients
 * Delete patients by DoctorID
 * Update patients
 */

public class PatientDAO extends JFrame {

    // Swing input fields for patient data
    private JTextField firstNameField, lastNameField, dobField, genderField, phoneField, addressField, admissionDateField;
    private JTextField deleteIdField, updateIdField;
    private JTextArea displayArea;

    //Constructor initializes the GUI layout and components.
    private JButton saveButton, displayButton, deleteButton, updateButton;

    public PatientDAO() {
        setTitle("Patient Management");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(14, 2));

        // ===== INPUT FIELDS =====

        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        add(lastNameField);

        add(new JLabel("DOB (YYYY-MM-DD):"));
        dobField = new JTextField();
        add(dobField);

        add(new JLabel("Gender:"));
        genderField = new JTextField();
        add(genderField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Address:"));
        addressField = new JTextField();
        add(addressField);

        add(new JLabel("Admission Date (YYYY-MM-DD):"));
        admissionDateField = new JTextField();
        add(admissionDateField);

        // ===== BUTTONS =====
        saveButton = new JButton("Save Patient");
        displayButton = new JButton("Display All Patients");
        updateButton = new JButton("Update Patient");
        deleteButton = new JButton("Delete Patient");

        add(saveButton);
        add(displayButton);

        // ===== UPDATE SECTION =====
        add(new JLabel("Patient ID to Update:"));
        updateIdField = new JTextField();
        add(updateIdField);
        add(updateButton);

        // ===== DELETE SECTION =====
        add(new JLabel("Patient ID to Delete:"));
        deleteIdField = new JTextField();
        add(deleteIdField);
        add(deleteButton);

        // ===== DISPLAY AREA =====
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane);

        // filler
        add(new JLabel());

        // ===== BUTTON ACTIONS =====
        saveButton.addActionListener(e -> savePatient());
        displayButton.addActionListener(e -> displayPatients());

        deleteButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(deleteIdField.getText());
                deletePatient(id);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid Patient ID!");
            }
        });

        updateButton.addActionListener(e -> updatePatient());

        setVisible(true);
    }

    // ===== DATABASE CONNECTION =====

    // Establishes connection to the MySQL database
    // @return Connection object or null if connection fails

    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/hospital_management_system";
        String user = "root";
        String password = ""; // Your SQL password

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // =========== ADD PATIENT =========

    // Inserts a new patient record into the patient table.

    public void addPatient(Patient patient) {
        String sql = "INSERT INTO PATIENTS (FirstName, LastName, DOB, Gender, Phone, Address, AdmissionDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDob()));
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getAddress());
            stmt.setDate(7, Date.valueOf(patient.getAdmissionDate()));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Patient saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== DISPLAY PATIENTS =====

    // Queries and displays all patients records from the database in the text area.

    public void displayPatients() {
        String sql = "SELECT * FROM PATIENTS";
        displayArea.setText("PatientID | FirstName | LastName | DOB | Gender | Phone | Address | AdmissionDate\n");

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                displayArea.append(
                        rs.getInt("PatientID") + " | " +
                                rs.getString("FirstName") + " | " +
                                rs.getString("LastName") + " | " +
                                rs.getDate("DOB") + " | " +
                                rs.getString("Gender") + " | " +
                                rs.getString("Phone") + " | " +
                                rs.getString("Address") + " | " +
                                rs.getDate("AdmissionDate") + "\n"
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== DELETE PATIENT =====

    // Deletes patient record into the PATIENTS table.

    public void deletePatient(int patientID) {
        String sql = "DELETE FROM PATIENTS WHERE PatientID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientID);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Patient deleted successfully!");
                displayPatients();
            } else {
                JOptionPane.showMessageDialog(this, "No patient found with ID: " + patientID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting patient: " + e.getMessage());
        }
    }

    // ===== UPDATE PATIENT =====

    // Update patients record on PATIENTS table

    private void updatePatient() {
        try {
            int id = Integer.parseInt(updateIdField.getText());

            String sql = "UPDATE PATIENTS SET FirstName=?, LastName=?, DOB=?, Gender=?, Phone=?, Address=?, AdmissionDate=? WHERE PatientID=?";

            try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, firstNameField.getText());
                stmt.setString(2, lastNameField.getText());
                stmt.setDate(3, Date.valueOf(LocalDate.parse(dobField.getText())));
                stmt.setString(4, genderField.getText());
                stmt.setString(5, phoneField.getText());
                stmt.setString(6, addressField.getText());
                stmt.setDate(7, Date.valueOf(LocalDate.parse(admissionDateField.getText())));
                stmt.setInt(8, id);

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Patient updated successfully!");
                    displayPatients();
                } else {
                    JOptionPane.showMessageDialog(this, "Patient ID not found!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage());
        }
    }

    // ===== SAVE PATIENT =====

    // Reads input field values, creates a patient  object, and saves it to the database.
    // Handles conversion of string to LocalDate.

    private void savePatient() {
        try {
            Patient patient = new Patient(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    LocalDate.parse(dobField.getText()),
                    genderField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    LocalDate.parse(admissionDateField.getText())
            );
            addPatient(patient);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ===== MAIN METHOD =====

    // To run the application

    public static void main(String[] args) {
        new PatientDAO();
    }

    //  ======================= PATIENT MODEL CLASS =======================

    // Inner class representing a Patient entity with fields
    // matching the columns in the PATIENTS database table.

    public static class Patient {
        private String firstName, lastName, gender, phone, address;
        private LocalDate dob, admissionDate;

        // Constructor
        public Patient(String firstName, String lastName, LocalDate dob, String gender,
                       String phone, String address, LocalDate admissionDate) {

            this.firstName = firstName;
            this.lastName = lastName;
            this.dob = dob;
            this.gender = gender;
            this.phone = phone;
            this.address = address;
            this.admissionDate = admissionDate;
        }
        // Getter methods

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public LocalDate getDob() { return dob; }
        public String getGender() { return gender; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public LocalDate getAdmissionDate() { return admissionDate; }
    }
}
