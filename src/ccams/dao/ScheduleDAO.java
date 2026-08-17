package ccams.dao;

import ccams.database.DatabaseConnection;
import ccams.models.Schedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    
    // Add schedule - FIXED: Using correct column names
    public boolean addSchedule(Schedule schedule) {
        String query = "INSERT INTO schedules (staff_id, day_of_week, start_time, end_time, " +
                      "is_recurring, specific_date, is_available) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, schedule.getStaffId());
            pstmt.setString(2, schedule.getDayOfWeek());
            pstmt.setTime(3, schedule.getStartTime());
            pstmt.setTime(4, schedule.getEndTime());
            pstmt.setBoolean(5, schedule.isRecurring());
            pstmt.setDate(6, schedule.getSpecificDate());
            pstmt.setBoolean(7, schedule.isAvailable());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    schedule.setScheduleId(rs.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get schedule by ID
    public Schedule getScheduleById(int scheduleId) {
        String query = "SELECT s.*, st.first_name, st.last_name, st.profession FROM schedules s " +
                      "JOIN staff st ON s.staff_id = st.staff_id " +
                      "WHERE s.schedule_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, scheduleId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSchedule(rs);
            }
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get schedules by staff ID
    public List<Schedule> getSchedulesByStaff(int staffId) {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT s.*, st.first_name, st.last_name, st.profession FROM schedules s " +
                      "JOIN staff st ON s.staff_id = st.staff_id " +
                      "WHERE s.staff_id = ? ORDER BY s.day_of_week, s.start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    // Get all schedules
    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT s.*, st.first_name, st.last_name, st.profession FROM schedules s " +
                      "JOIN staff st ON s.staff_id = st.staff_id " +
                      "ORDER BY st.last_name, s.day_of_week, s.start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    // Get available schedules for a specific day
    public List<Schedule> getAvailableSchedulesForDay(String dayOfWeek) {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT s.*, st.first_name, st.last_name, st.profession FROM schedules s " +
                      "JOIN staff st ON s.staff_id = st.staff_id " +
                      "WHERE s.day_of_week = ? AND s.is_available = 1 " +
                      "ORDER BY st.last_name, s.start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, dayOfWeek);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    // Get schedules by date (specific date schedules)
    public List<Schedule> getSchedulesByDate(Date date) {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT s.*, st.first_name, st.last_name, st.profession FROM schedules s " +
                      "JOIN staff st ON s.staff_id = st.staff_id " +
                      "WHERE s.specific_date = ? AND s.is_available = 1 " +
                      "ORDER BY st.last_name, s.start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    // Update schedule
    public boolean updateSchedule(Schedule schedule) {
        String query = "UPDATE schedules SET day_of_week = ?, start_time = ?, end_time = ?, " +
                      "is_recurring = ?, specific_date = ?, is_available = ? " +
                      "WHERE schedule_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, schedule.getDayOfWeek());
            pstmt.setTime(2, schedule.getStartTime());
            pstmt.setTime(3, schedule.getEndTime());
            pstmt.setBoolean(4, schedule.isRecurring());
            pstmt.setDate(5, schedule.getSpecificDate());
            pstmt.setBoolean(6, schedule.isAvailable());
            pstmt.setInt(7, schedule.getScheduleId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete schedule
    public boolean deleteSchedule(int scheduleId) {
        String query = "DELETE FROM schedules WHERE schedule_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, scheduleId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Toggle availability
    public boolean toggleAvailability(int scheduleId, boolean isAvailable) {
        String query = "UPDATE schedules SET is_available = ? WHERE schedule_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, scheduleId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper method to map ResultSet to Schedule
    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        schedule.setStaffId(rs.getInt("staff_id"));
        schedule.setDayOfWeek(rs.getString("day_of_week"));
        schedule.setStartTime(rs.getTime("start_time"));
        schedule.setEndTime(rs.getTime("end_time"));
        schedule.setRecurring(rs.getBoolean("is_recurring"));
        schedule.setSpecificDate(rs.getDate("specific_date"));
        schedule.setAvailable(rs.getBoolean("is_available"));
        schedule.setCreatedAt(rs.getTimestamp("created_at"));
        schedule.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Display fields from join
        schedule.setStaffName(rs.getString("first_name") + " " + rs.getString("last_name"));
        schedule.setStaffProfession(rs.getString("profession"));
        
        return schedule;
    }
}