package hospitalmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

/**

 * AppointmentDAO is a Java Swing GUI application that allows users to:
 * Enter appointment details
 * Save appointments to MySQL database
 * Display all saved appointments
 * Delete appointments by AppointmentID
 * Update appointments
 */

public class AppointmentDAO extends JFrame {


    // Swing components
    private JTextField patientIDField, doctorIDField, appointmentDateField, descriptionField, appointmentStatusField;
    private JTextField appointmentIDField;  // For update/delete/search
    private JTextField searchPatientField, searchDoctorField;

    private JTextArea displayArea;

    private JButton saveButton, displayButton, deleteButton, updateButton, searchButton;

    // Constructor that sets up the GUI layout and event handling
    public AppointmentDAO() {

        setTitle("Appointment Management");
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(12, 2, 5, 5));

        // ======================= INPUT FIELDS =======================

        add(new JLabel("Appointment ID (for Update/Delete):"));
        appointmentIDField = new JTextField();
        add(appointmentIDField);

        add(new JLabel("Patient ID:"));
        patientIDField = new JTextField();
        add(patientIDField);

        add(new JLabel("Doctor ID:"));
        doctorIDField = new JTextField();
        add(doctorIDField);

        add(new JLabel("Appointment Date (YYYY-MM-DD):"));
        appointmentDateField = new JTextField();
        add(appointmentDateField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField();
        add(descriptionField);

        add(new JLabel("Appointment Status:"));
        appointmentStatusField = new JTextField();
        add(appointmentStatusField);

        // ======================= SEARCH FIELDS =======================

        add(new JLabel("Search by Patient ID:"));
        searchPatientField = new JTextField();
        add(searchPatientField);

        add(new JLabel("Search by Doctor ID:"));
        searchDoctorField = new JTextField();
        add(searchDoctorField);

        // ======================= BUTTONS =======================

        saveButton = new JButton("Save Appointment");
        displayButton = new JButton("Display All");
        updateButton = new JButton("Update Appointment");
        deleteButton = new JButton("Delete Appointment");
        searchButton = new JButton("Search");

        add(saveButton);
        add(displayButton);
        add(updateButton);
        add(deleteButton);
        add(searchButton);
        add(new JLabel()); // filler

        // ======================= DISPLAY AREA =======================

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        add(scrollPane);
        add(new JLabel());

        // ======================= BUTTON LISTENERS =======================

        saveButton.addActionListener(e -> saveAppointment());
        displayButton.addActionListener(e -> displayAppointments());
        deleteButton.addActionListener(e -> deleteAppointment());
        updateButton.addActionListener(e -> updateAppointment());
        searchButton.addActionListener(e -> searchAppointments());

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
            JOptionPane.showMessageDialog(null, "Database connection failed!");
            return null;
        }
    }

    // ======================= ADD APPOINTMENTS =======================

    // Inserts a new appointment record into the Appointments table.

    public void addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (PatientID, DoctorID, AppointmentDate, Description, AppointmentStatus) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointment.getPatientID());
            stmt.setInt(2, appointment.getDoctorID());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setString(4, appointment.getDescription());
            stmt.setString(5, appointment.getAppointmentStatus());

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Appointment saved successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving appointment: " + e.getMessage());
        }
    }

    // ======================= DISPLAY APPOINTMENTS =======================

    // Queries and displays all appointment records from the database in the text area.

    public void displayAppointments() {
        String sql = "SELECT * FROM appointments";

        displayArea.setText("AppointmentID | PatientID | DoctorID | Date | Description | Status\n");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                displayArea.append(
                        rs.getInt("AppointmentID") + " | " +
                                rs.getInt("PatientID") + " | " +
                                rs.getInt("DoctorID") + " | " +
                                rs.getDate("AppointmentDate") + " | " +
                                rs.getString("Description") + " | " +
                                rs.getString("AppointmentStatus") + "\n"
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    // ======================= SAVE  APPOINTMENTS =======================

    // Reads input field values, creates a Appointment  object, and saves it to the database.
    // Handles conversion of string to LocalDate.


    private void saveAppointment() {
        try {
            int patientID = Integer.parseInt(patientIDField.getText());
            int doctorID = Integer.parseInt(doctorIDField.getText());
            LocalDate appointmentDate = LocalDate.parse(appointmentDateField.getText());
            String description = descriptionField.getText();
            String appointmentStatus = appointmentStatusField.getText();

            Appointment appointment = new Appointment(patientID, doctorID, appointmentDate, description, appointmentStatus);
            addAppointment(appointment);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
        }
    }

    // ======================= DELETE APPOINTMENTS =======================

    // Deletes appointment record into the APPOINTMENTS table.

    private void deleteAppointment() {
        try {
            int id = Integer.parseInt(appointmentIDField.getText());

            String sql = "DELETE FROM appointments WHERE AppointmentID = ?";

            try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment deleted successfully!");
                    displayAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, "No appointment found with ID: " + id);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Appointment ID!");
        }
    }

    // ======================= UPDATE APPOINTMENT =======================

   // Update appointment record on APPOINTMENTS table

    private void updateAppointment() {
        try {
            int appointmentID = Integer.parseInt(appointmentIDField.getText());
            int patientID = Integer.parseInt(patientIDField.getText());
            int doctorID = Integer.parseInt(doctorIDField.getText());
            LocalDate date = LocalDate.parse(appointmentDateField.getText());
            String description = descriptionField.getText();
            String status = appointmentStatusField.getText();

            String sql = "UPDATE appointments SET PatientID = ?, DoctorID = ?, AppointmentDate = ?, Description = ?, AppointmentStatus = ? WHERE AppointmentID = ?";

            try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, patientID);
                stmt.setInt(2, doctorID);
                stmt.setDate(3, Date.valueOf(date));
                stmt.setString(4, description);
                stmt.setString(5, status);
                stmt.setInt(6, appointmentID);

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment updated successfully!");
                    displayAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, "Appointment ID not found!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage());
        }
    }

    // ======================= SEARCH APPOINTMENTS =======================

    // Search appointment record  APPOINTMENT table

    private void searchAppointments() {
        String sql = "SELECT * FROM appointments WHERE 1=1 ";

        try {
            String patient = searchPatientField.getText().trim();
            String doctor = searchDoctorField.getText().trim();

            if (!patient.isEmpty()) sql += " AND PatientID = " + Integer.parseInt(patient);
            if (!doctor.isEmpty()) sql += " AND DoctorID = " + Integer.parseInt(doctor);

            displayArea.setText("AppointmentID | PatientID | DoctorID | Date | Description | Status\n");

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    displayArea.append(
                            rs.getInt("AppointmentID") + " | " +
                                    rs.getInt("PatientID") + " | " +
                                    rs.getInt("DoctorID") + " | " +
                                    rs.getDate("AppointmentDate") + " | " +
                                    rs.getString("Description") + " | " +
                                    rs.getString("AppointmentStatus") + "\n"
                    );
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid search input!");
        }
    }

    // ======================= MAIN =======================

    // To run the application

    public static void main(String[] args) {
        new AppointmentDAO();
    }

    // ======================= APPOINTMENT MODEL CLASS =======================

    // Inner class representing a Appointment entity with fields
    // matching the columns in the APPOINTMENTS database table.

    public static class Appointment {
        private int appointmentID;
        private int patientID;
        private int doctorID;
        private LocalDate appointmentDate;
        private String description;
        private String appointmentStatus;

        // Constructor
        public Appointment(int patientID, int doctorID, LocalDate appointmentDate, String description, String appointmentStatus) {
            this.patientID = patientID;
            this.doctorID = doctorID;
            this.appointmentDate = appointmentDate;
            this.description = description;
            this.appointmentStatus = appointmentStatus;
        }

        // Getter methods
        public int getAppointmentID() { return appointmentID; }
        public int getPatientID() { return patientID; }
        public int getDoctorID() { return doctorID; }
        public LocalDate getAppointmentDate() { return appointmentDate; }
        public String getDescription() { return description; }
        public String getAppointmentStatus() { return appointmentStatus; }
    }
}
