package ccams.dao;

import ccams.database.DatabaseConnection;
import ccams.models.Student;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    // ============================================================
    // Helper method to hash passwords using SHA-256
    // ============================================================
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Register new student (creates user account + student profile)
    public boolean registerStudent(Student student, String password) {
        Connection conn = null;
        PreparedStatement userPstmt = null;
        PreparedStatement studentPstmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // ============================================================
            // HASH THE PASSWORD BEFORE STORING
            // ============================================================
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                System.err.println("❌ Password hashing failed!");
                conn.rollback();
                return false;
            }
            
            System.out.println("📝 Registering student: " + student.getStudentNumber());
            System.out.println("   Password (original): " + password);
            System.out.println("   Password (hashed): " + hashedPassword.substring(0, 20) + "...");
            
            // 1. Create user account - STORE HASHED PASSWORD
            String userQuery = "INSERT INTO users (username, password, role_id, is_active) VALUES (?, ?, 2, 1)";
            userPstmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            userPstmt.setString(1, student.getStudentNumber()); // Use student number as username
            userPstmt.setString(2, hashedPassword); // Store hashed password
            
            int affectedRows = userPstmt.executeUpdate();
            
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }
            
            generatedKeys = userPstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                student.setUserId(userId);
            } else {
                conn.rollback();
                return false;
            }
            
            // 2. Create student profile
            String studentQuery = "INSERT INTO students (user_id, student_number, first_name, last_name, " +
                                  "email, phone, date_of_birth, address, emergency_contact, emergency_phone) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            studentPstmt = conn.prepareStatement(studentQuery, Statement.RETURN_GENERATED_KEYS);
            studentPstmt.setInt(1, student.getUserId());
            studentPstmt.setString(2, student.getStudentNumber());
            studentPstmt.setString(3, student.getFirstName());
            studentPstmt.setString(4, student.getLastName());
            studentPstmt.setString(5, student.getEmail());
            studentPstmt.setString(6, student.getPhone());
            studentPstmt.setDate(7, student.getDateOfBirth());
            studentPstmt.setString(8, student.getAddress());
            studentPstmt.setString(9, student.getEmergencyContact());
            studentPstmt.setString(10, student.getEmergencyPhone());
            
            affectedRows = studentPstmt.executeUpdate();
            
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }
            
            ResultSet studentRs = studentPstmt.getGeneratedKeys();
            if (studentRs.next()) {
                student.setStudentId(studentRs.getInt(1));
            }
            
            conn.commit();
            System.out.println("✅ Student registered successfully with hashed password!");
            System.out.println("   Username: " + student.getStudentNumber());
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (userPstmt != null) userPstmt.close();
                if (studentPstmt != null) studentPstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get student by ID
    public Student getStudentById(int studentId) {
        String query = "SELECT s.*, u.username, r.role_name, u.is_active FROM students s " +
                      "JOIN users u ON s.user_id = u.user_id " +
                      "LEFT JOIN roles r ON u.role_id = r.role_id " +
                      "WHERE s.student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get student by student number
    public Student getStudentByStudentNumber(String studentNumber) {
        String query = "SELECT s.*, u.username, r.role_name, u.is_active FROM students s " +
                      "JOIN users u ON s.user_id = u.user_id " +
                      "LEFT JOIN roles r ON u.role_id = r.role_id " +
                      "WHERE s.student_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, u.username, r.role_name FROM students s " +
                      "JOIN users u ON s.user_id = u.user_id " +
                      "LEFT JOIN roles r ON u.role_id = r.role_id " +
                      "ORDER BY s.last_name, s.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                try {
                    students.add(mapResultSetToStudent(rs));
                } catch (SQLException e) {
                    System.err.println("Error mapping student row: " + e.getMessage());
                }
            }
            
            System.out.println("Loaded " + students.size() + " students from database.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    
    // Search students
    public List<Student> searchStudents(String searchTerm) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, u.username, r.role_name FROM students s " +
                      "JOIN users u ON s.user_id = u.user_id " +
                      "LEFT JOIN roles r ON u.role_id = r.role_id " +
                      "WHERE s.student_number LIKE ? OR s.first_name LIKE ? OR " +
                      "s.last_name LIKE ? OR s.email LIKE ? " +
                      "ORDER BY s.last_name, s.first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                try {
                    students.add(mapResultSetToStudent(rs));
                } catch (SQLException e) {
                    System.err.println("Error mapping student row: " + e.getMessage());
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    
    // Update student
    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET first_name = ?, last_name = ?, email = ?, " +
                      "phone = ?, date_of_birth = ?, address = ?, emergency_contact = ?, " +
                      "emergency_phone = ? WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setDate(5, student.getDateOfBirth());
            pstmt.setString(6, student.getAddress());
            pstmt.setString(7, student.getEmergencyContact());
            pstmt.setString(8, student.getEmergencyPhone());
            pstmt.setInt(9, student.getStudentId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete student (soft delete - deactivate user)
    public boolean deleteStudent(int studentId) {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String getUserIdQuery = "SELECT user_id FROM students WHERE student_id = ?";
            pstmt1 = conn.prepareStatement(getUserIdQuery);
            pstmt1.setInt(1, studentId);
            ResultSet rs = pstmt1.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String deactivateQuery = "UPDATE users SET is_active = 0 WHERE user_id = ?";
                pstmt2 = conn.prepareStatement(deactivateQuery);
                pstmt2.setInt(1, userId);
                pstmt2.executeUpdate();
                System.out.println("🔴 Deactivated student (user_id: " + userId + ")");
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Count students
    public int countStudents() {
        String query = "SELECT COUNT(*) FROM students";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // Check if student number exists
    public boolean studentNumberExists(String studentNumber) {
        String query = "SELECT COUNT(*) FROM students WHERE student_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper method to map ResultSet to Student
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setUserId(rs.getInt("user_id"));
        student.setStudentNumber(rs.getString("student_number"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setAddress(rs.getString("address"));
        student.setEmergencyContact(rs.getString("emergency_contact"));
        student.setEmergencyPhone(rs.getString("emergency_phone"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        try {
            student.setActive(rs.getBoolean("is_active"));
        } catch (SQLException e) {
            student.setActive(true);
        }
        
        try {
            student.setUsername(rs.getString("username"));
            student.setRoleName(rs.getString("role_name"));
        } catch (SQLException e) {
            // These columns might not exist in all queries
        }
        
        return student;
    }
}