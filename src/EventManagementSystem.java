import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class EventManagementSystem {
    // Database connection method
    private static Connection connect() {
        String url = "jdbc:mysql://localhost:3306/EventManagement"; // Ensure this matches your DB setup
        String user = "root"; // Replace with your MySQL username
        String password = "********"; // Replace with your MySQL password

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Event Management System");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel lblName = new JLabel("Event Name:");
        lblName.setBounds(20, 20, 100, 30);
        JTextField txtName = new JTextField();
        txtName.setBounds(150, 20, 200, 30);

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        lblDate.setBounds(20, 60, 150, 30);
        JTextField txtDate = new JTextField();
        txtDate.setBounds(150, 60, 200, 30);

        JLabel lblLocation = new JLabel("Location:");
        lblLocation.setBounds(20, 100, 100, 30);
        JTextField txtLocation = new JTextField();
        txtLocation.setBounds(150, 100, 200, 30);

        JLabel lblAttendees = new JLabel("Attendees:");
        lblAttendees.setBounds(20, 140, 100, 30);
        JTextField txtAttendees = new JTextField();
        txtAttendees.setBounds(150, 140, 200, 30);

        JButton btnAdd = new JButton("Add Event");
        btnAdd.setBounds(50, 200, 120, 30);

        JButton btnView = new JButton("View Events");
        btnView.setBounds(200, 200, 120, 30);

        JButton btnDelete = new JButton("Delete Event");
        btnDelete.setBounds(350, 200, 120, 30);

        JTextArea textArea = new JTextArea();
        textArea.setBounds(20, 250, 450, 200);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 250, 450, 200);

        frame.add(lblName);
        frame.add(txtName);
        frame.add(lblDate);
        frame.add(txtDate);
        frame.add(btnDelete);
        frame.add(lblLocation);
        frame.add(txtLocation);
        frame.add(lblAttendees);
        frame.add(txtAttendees);
        frame.add(btnAdd);
        frame.add(btnView);
        frame.add(scrollPane);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = txtName.getText();
                String date = txtDate.getText();
                String location = txtLocation.getText();
                int attendees;
                try {
                    attendees = Integer.parseInt(txtAttendees.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for attendees.");
                    return;
                }

                try (Connection conn = connect();
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO Events (name, date, location, attendees) VALUES (?, ?, ?, ?)")) {
                    stmt.setString(1, name);
                    stmt.setString(2, date);
                    stmt.setString(3, location);
                    stmt.setInt(4, attendees);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Event added successfully!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error adding event: " + ex.getMessage());
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String eventId = JOptionPane.showInputDialog(frame, "Enter Event ID to delete:");

                if (eventId != null && !eventId.trim().isEmpty()) {
                    try (Connection conn = connect();
                         PreparedStatement stmt = conn.prepareStatement("DELETE FROM Events WHERE id = ?")) {

                        stmt.setInt(1, Integer.parseInt(eventId));
                        int rowsAffected = stmt.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, "Event deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "No event found with the given ID.");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Error deleting event: " + ex.getMessage());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid numeric Event ID.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Event ID cannot be empty.");
                }
            }
        });


        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                try (Connection conn = connect();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM Events")) {
                    while (rs.next()) {
                        textArea.append("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                                ", Date: " + rs.getString("date") + ", Location: " + rs.getString("location") +
                                ", Attendees: " + rs.getInt("attendees") + "\n");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error retrieving events: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }

}

