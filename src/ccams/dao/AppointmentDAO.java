package ccams.dao;

import ccams.database.DatabaseConnection;
import ccams.models.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDAO {
    
    // Book a new appointment
    public boolean bookAppointment(Appointment appointment) {
        String query = "INSERT INTO appointments (student_user_id, staff_id, appointment_date, status_id) " +
                      "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, appointment.getStudentUserId());
            pstmt.setInt(2, appointment.getStaffId());
            pstmt.setTimestamp(3, appointment.getAppointmentDate());
            pstmt.setInt(4, appointment.getStatusId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    appointment.setAppointmentId(rs.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get appointment by ID
    public Appointment getAppointmentById(int appointmentId) {
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            WHERE a.appointment_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get appointments by student (using student_user_id)
    public List<Appointment> getAppointmentsByStudent(int studentUserId) {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            WHERE a.student_user_id = ?
            ORDER BY a.appointment_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentUserId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Get appointments by staff ID
    public List<Appointment> getAppointmentsByStaff(int staffId) {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            WHERE a.staff_id = ?
            ORDER BY a.appointment_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Get appointments by date
    public List<Appointment> getAppointmentsByDate(Date date) {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            WHERE DATE(a.appointment_date) = ?
            ORDER BY a.appointment_date
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Get appointments by status
    public List<Appointment> getAppointmentsByStatus(int statusId) {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            WHERE a.status_id = ?
            ORDER BY a.appointment_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, statusId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Get all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            ORDER BY a.appointment_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // ==========================================================
    // FIX: ADD THIS METHOD - Get appointments by date range
    // ==========================================================
    public List<Appointment> getAppointmentsByDateRange(Date startDate, Date endDate) {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            WHERE DATE(a.appointment_date) BETWEEN ? AND ?
            ORDER BY a.appointment_date
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // ==========================================================
    // FIX: ADD THIS METHOD - Get appointments by date range using Timestamp
    // ==========================================================
    public List<Appointment> getAppointmentsByDateRange(Timestamp startDate, Timestamp endDate) {
        List<Appointment> appointments = new ArrayList<>();
        String query = """
            SELECT a.*, 
                   s.student_id, s.student_number, s.first_name as student_first, s.last_name as student_last,
                   st.first_name as staff_first, st.last_name as staff_last, st.profession,
                   aps.status_name
            FROM appointments a
            LEFT JOIN students s ON a.student_user_id = s.user_id
            LEFT JOIN staff st ON a.staff_id = st.staff_id
            LEFT JOIN appointment_status aps ON a.status_id = aps.status_id
            WHERE a.appointment_date BETWEEN ? AND ?
            ORDER BY a.appointment_date
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    // Update full appointment (Student, Staff, Date, Status)
    public boolean updateAppointment(Appointment appointment) {
        String query = "UPDATE appointments SET student_user_id = ?, staff_id = ?, appointment_date = ?, status_id = ? " +
                      "WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, appointment.getStudentUserId());
            pstmt.setInt(2, appointment.getStaffId());
            pstmt.setTimestamp(3, appointment.getAppointmentDate());
            pstmt.setInt(4, appointment.getStatusId());
            pstmt.setInt(5, appointment.getAppointmentId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update only appointment status
    public boolean updateAppointmentStatus(int appointmentId, int statusId) {
        String query = "UPDATE appointments SET status_id = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, statusId);
            pstmt.setInt(2, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update appointment status with notes
    public boolean updateAppointmentStatus(int appointmentId, int statusId, String notes) {
        String query = "UPDATE appointments SET status_id = ?, notes = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, statusId);
            pstmt.setString(2, notes);
            pstmt.setInt(3, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cancel appointment with reason
    public boolean cancelAppointment(int appointmentId, String reason) {
        String query = "UPDATE appointments SET status_id = 4, cancellation_reason = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, reason);
            pstmt.setInt(2, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cancel appointment (simple)
    public boolean cancelAppointment(int appointmentId) {
        String query = "UPDATE appointments SET status_id = 4 WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Reschedule appointment
    public boolean rescheduleAppointment(int appointmentId, Timestamp newDate) {
        String query = "UPDATE appointments SET appointment_date = ?, status_id = 5 WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, newDate);
            pstmt.setInt(2, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Reschedule appointment with reason
    public boolean rescheduleAppointment(int appointmentId, Timestamp newDate, String reason) {
        String query = "UPDATE appointments SET appointment_date = ?, status_id = 5, reschedule_reason = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, newDate);
            pstmt.setString(2, reason);
            pstmt.setInt(3, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Complete appointment with notes
    public boolean completeAppointment(int appointmentId, String notes) {
        String query = "UPDATE appointments SET status_id = 3, notes = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, notes);
            pstmt.setInt(2, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if time slot is available
    public boolean isTimeSlotAvailable(int staffId, Timestamp date) {
        String query = "SELECT COUNT(*) FROM appointments WHERE staff_id = ? " +
                      "AND appointment_date = ? AND status_id NOT IN (4)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, staffId);
            pstmt.setTimestamp(2, date);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // DASHBOARD METHODS
    public int getTotalAppointments() {
        String sql = "SELECT COUNT(*) as total FROM appointments";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getAppointmentCountByStatus(int statusId) {
        String query = "SELECT COUNT(*) FROM appointments WHERE status_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, statusId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public Map<String, Integer> getWeeklyTrends() {
        Map<String, Integer> trends = new HashMap<>();
        String sql = """
            SELECT 
                DAYNAME(appointment_date) as day_name, 
                COUNT(*) as total
            FROM appointments
            GROUP BY day_name
            ORDER BY 
                CASE day_name
                    WHEN 'Monday' THEN 1
                    WHEN 'Tuesday' THEN 2
                    WHEN 'Wednesday' THEN 3
                    WHEN 'Thursday' THEN 4
                    WHEN 'Friday' THEN 5
                    WHEN 'Saturday' THEN 6
                    WHEN 'Sunday' THEN 7
                END
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trends.put(rs.getString("day_name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trends;
    }
    
    // Helper method to map ResultSet to Appointment
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setStudentUserId(rs.getInt("student_user_id"));
        appointment.setStaffId(rs.getInt("staff_id"));
        appointment.setAppointmentDate(rs.getTimestamp("appointment_date"));
        appointment.setStatusId(rs.getInt("status_id"));
        appointment.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Try to get notes
        try { 
            appointment.setNotes(rs.getString("notes")); 
        } catch (Exception e) { appointment.setNotes(""); }
        
        try { 
            appointment.setCancellationReason(rs.getString("cancellation_reason")); 
        } catch (Exception e) { appointment.setCancellationReason(""); }
        
        try { 
            appointment.setRescheduleReason(rs.getString("reschedule_reason")); 
        } catch (Exception e) { appointment.setRescheduleReason(""); }
        
        // SAFELY retrieve student name
        String sFirst = rs.getString("student_first");
        String sLast = rs.getString("student_last");
        if (sFirst != null && sLast != null) {
            appointment.setStudentName(sFirst + " " + sLast);
        } else {
            appointment.setStudentName("Unknown Student");
        }
        
        // SAFELY retrieve student number
        try { 
            appointment.setStudentNumber(rs.getString("student_number")); 
        } catch (Exception e) { appointment.setStudentNumber("N/A"); }
        
        // SAFELY retrieve staff name
        String stFirst = rs.getString("staff_first");
        String stLast = rs.getString("staff_last");
        if (stFirst != null && stLast != null) {
            appointment.setStaffName(stFirst + " " + stLast);
        } else {
            appointment.setStaffName("Unknown Staff");
        }
        
        // SAFELY retrieve profession
        try { 
            appointment.setStaffProfession(rs.getString("profession")); 
        } catch (Exception e) { appointment.setStaffProfession("Unknown"); }
        
        // SAFELY retrieve status
        try { 
            appointment.setStatusName(rs.getString("status_name")); 
        } catch (Exception e) { appointment.setStatusName("Unknown"); }
        
        return appointment;
    }
}