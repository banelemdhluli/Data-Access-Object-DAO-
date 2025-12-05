** Data-Access-Object-DAO **

🏥 Hospital Management System – DAO with Java Swing + MySQL

This project is a simple Hospital Management System built using Java Swing (GUI) and MySQL it practises DAO
It allows users to manage patients and doctors through a desktop interface with full CRUD functionality.


 👤 Author: Banele Mdhluli
💻 Language: Java
🛠️ IDE: NetBeans / IntelliJ / Any Java IDE
🧑‍💻 MySQL

---
## 🧠 Concepts Used

📚 Java Swing
🧾 GUI
🌀 CRUD
❗ RDBMS 
🔀 JDBC


---
## 📌 Features


✅ Patient Management

Add a new patient

View all patient records

Update any patient record by ID

Delete a patient by ID

Save a patient

Built-in Patient model class

Uses Java Swing components (JLabels, JTextFields, Buttons, JTextArea)

✅ Doctor Management

Add a new doctor

Display all saved doctors

Update doctor details

Delete a doctor by ID

Save a doctor

Built-in Doctor model class

Uses Java Swing components (JLabels, JTextFields, Buttons, JTextArea)

✅ Appointment Management

Add a new appointment

Display all saved appointment

Update appointment details

Delete a appointment by ID

Search a appointment by ID

Save an appointment

Built-in Doctor model class

Uses Java Swing components (JLabels, JTextFields, Buttons, JTextArea)


---
## SQL QUERY

CREATE TABLE patients (
    PatientID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    DOB DATE,
    Gender VARCHAR(10),
    Phone VARCHAR(20),
    Address VARCHAR(100),
    AdmissionDate DATE
),

CREATE TABLE doctors (
    DoctorID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Specialty VARCHAR(50),
    Phone VARCHAR(15),
    Email VARCHAR(50)
),

CREATE TABLE appointments (
    AppointmentID INT AUTO_INCREMENT PRIMARY KEY,
    PatientID int,
    DoctorID int,
    AppointmentDate DATE,
    Description VARCHAR(100),
    AppointmentStatus VARCHAR(50)
);

---
## ▶️ HOW TO RUN

Make sure MySQL Server is running.

📥 Clone or download the project files.
🧑‍💻 Open the project in your preferred Java IDE.
📂 Ensure all .java files are in the same package or directory.
🚀 Run the DAO classes.


---
## 📝 LICENSE

This project is for educational use and free to modify.







