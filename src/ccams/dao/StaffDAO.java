package ccams.dao;

import ccams.database.DatabaseConnection;
import ccams.models.Staff;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    // ============================================================
    // 1. HASH PASSWORD USING SHA-256
    // ============================================================
    private String hashPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(
                    password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }


    // ============================================================
    // 2. ADD NEW STAFF MEMBER
    // Creates USER account + STAFF profile
    // ============================================================
    public boolean addStaff(Staff staff, String password) {

        Connection conn = null;
        PreparedStatement userPstmt = null;
        PreparedStatement staffPstmt = null;
        ResultSet generatedKeys = null;

        try {

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // ----------------------------------------------------
            // Determine role
            // ----------------------------------------------------
            int roleId;

            if (staff.getProfession() != null &&
                    staff.getProfession().equalsIgnoreCase("NURSE")) {

                roleId = 3;

            } else {

                roleId = 4;
            }


            // ----------------------------------------------------
            // Username = email
            // ----------------------------------------------------
            String username = staff.getEmail();


            // ----------------------------------------------------
            // Check if username already exists
            // ----------------------------------------------------
            if (usernameExists(username)) {

                System.out.println(
                        "Username already exists: " + username
                );

                conn.rollback();
                return false;
            }


            // ----------------------------------------------------
            // HASH PASSWORD
            // ----------------------------------------------------
            String hashedPassword = hashPassword(password);

            System.out.println(
                    "Creating user with username: " + username
            );

            System.out.println(
                    "Password hashed using SHA-256"
            );


            // ----------------------------------------------------
            // CREATE USER
            // ----------------------------------------------------
            String userQuery =
                    "INSERT INTO users " +
                    "(username, password, role_id, is_active) " +
                    "VALUES (?, ?, ?, 1)";

            userPstmt = conn.prepareStatement(
                    userQuery,
                    Statement.RETURN_GENERATED_KEYS
            );

            userPstmt.setString(1, username);

            // IMPORTANT:
            // Store HASHED password, NOT plain text
            userPstmt.setString(2, hashedPassword);

            userPstmt.setInt(3, roleId);


            int affectedRows = userPstmt.executeUpdate();

            if (affectedRows == 0) {

                conn.rollback();
                return false;
            }


            // ----------------------------------------------------
            // GET GENERATED USER ID
            // ----------------------------------------------------
            generatedKeys = userPstmt.getGeneratedKeys();

            if (generatedKeys.next()) {

                int userId = generatedKeys.getInt(1);

                staff.setUserId(userId);

            } else {

                conn.rollback();
                return false;
            }


            // ----------------------------------------------------
            // CREATE STAFF PROFILE
            // ----------------------------------------------------
            String staffQuery =
                    "INSERT INTO staff " +
                    "(user_id, first_name, last_name, profession, " +
                    "email, phone, active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            staffPstmt = conn.prepareStatement(
                    staffQuery,
                    Statement.RETURN_GENERATED_KEYS
            );

            staffPstmt.setInt(
                    1,
                    staff.getUserId()
            );

            staffPstmt.setString(
                    2,
                    staff.getFirstName()
            );

            staffPstmt.setString(
                    3,
                    staff.getLastName()
            );

            staffPstmt.setString(
                    4,
                    staff.getProfession()
            );

            staffPstmt.setString(
                    5,
                    staff.getEmail()
            );

            staffPstmt.setString(
                    6,
                    staff.getPhone()
            );

            staffPstmt.setBoolean(
                    7,
                    staff.isActive()
            );


            affectedRows = staffPstmt.executeUpdate();

            if (affectedRows == 0) {

                conn.rollback();
                return false;
            }


            // ----------------------------------------------------
            // GET GENERATED STAFF ID
            // ----------------------------------------------------
            try (ResultSet staffRs =
                         staffPstmt.getGeneratedKeys()) {

                if (staffRs.next()) {

                    staff.setStaffId(
                            staffRs.getInt(1)
                    );
                }
            }


            // ----------------------------------------------------
            // COMMIT
            // ----------------------------------------------------
            conn.commit();

            System.out.println(
                    "Staff added successfully!"
            );

            System.out.println(
                    "Username: " + username
            );

            System.out.println(
                    "Staff ID: " + staff.getStaffId()
            );

            // NEVER print the password

            return true;


        } catch (SQLException e) {

            try {

                if (conn != null) {
                    conn.rollback();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

            return false;


        } finally {

            try {

                if (generatedKeys != null) {
                    generatedKeys.close();
                }

                if (userPstmt != null) {
                    userPstmt.close();
                }

                if (staffPstmt != null) {
                    staffPstmt.close();
                }

                if (conn != null) {
                    conn.setAutoCommit(true);
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }


    // ============================================================
    // 3. CHECK IF USERNAME EXISTS
    // ============================================================
    private boolean usernameExists(String username) {

        String query =
                "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }


    // ============================================================
    // 4. GET STAFF BY ID
    // ============================================================
    public Staff getStaffById(int staffId) {

        String query =
                "SELECT s.*, u.username, r.role_name, u.is_active " +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN roles r ON u.role_id = r.role_id " +
                "WHERE s.staff_id = ?";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setInt(1, staffId);

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                if (rs.next()) {

                    return mapResultSetToStaff(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // ============================================================
    // 5. GET STAFF BY EMAIL
    // ============================================================
    public Staff getStaffByEmail(String email) {

        String query =
                "SELECT s.*, u.username, r.role_name, u.is_active " +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN roles r ON u.role_id = r.role_id " +
                "WHERE s.email = ?";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                if (rs.next()) {

                    return mapResultSetToStaff(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // ============================================================
    // 6. GET ALL STAFF
    // ============================================================
    public List<Staff> getAllStaff() {

        List<Staff> staffList =
                new ArrayList<>();

        String query =
                "SELECT s.*, u.username, r.role_name, u.is_active " +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN roles r ON u.role_id = r.role_id " +
                "ORDER BY s.profession, s.last_name, s.first_name";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             Statement stmt =
                     conn.createStatement();

             ResultSet rs =
                     stmt.executeQuery(query)) {

            while (rs.next()) {

                staffList.add(
                        mapResultSetToStaff(rs)
                );
            }

            System.out.println(
                    "StaffDAO.getAllStaff() returned: "
                    + staffList.size()
                    + " records"
            );

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return staffList;
    }


    // ============================================================
    // 7. GET STAFF BY PROFESSION
    // ============================================================
    public List<Staff> getStaffByProfession(
            String profession) {

        List<Staff> staffList =
                new ArrayList<>();

        String query =
                "SELECT s.*, u.username, r.role_name, u.is_active " +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN roles r ON u.role_id = r.role_id " +
                "WHERE s.profession = ? " +
                "AND u.is_active = 1 " +
                "ORDER BY s.last_name, s.first_name";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(
                    1,
                    profession.toUpperCase()
            );

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                while (rs.next()) {

                    staffList.add(
                            mapResultSetToStaff(rs)
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return staffList;
    }


    // ============================================================
    // 8. SEARCH STAFF
    // ============================================================
    public List<Staff> searchStaff(
            String searchTerm) {

        List<Staff> staffList =
                new ArrayList<>();

        String query =
                "SELECT s.*, u.username, r.role_name, u.is_active " +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN roles r ON u.role_id = r.role_id " +
                "WHERE s.first_name LIKE ? " +
                "OR s.last_name LIKE ? " +
                "OR s.email LIKE ? " +
                "OR s.profession LIKE ? " +
                "ORDER BY s.profession, s.last_name, s.first_name";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            String pattern =
                    "%" + searchTerm + "%";

            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                while (rs.next()) {

                    staffList.add(
                            mapResultSetToStaff(rs)
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return staffList;
    }


    // ============================================================
    // 9. UPDATE STAFF
    // ============================================================
    public boolean updateStaff(Staff staff) {

        String query =
                "UPDATE staff SET " +
                "first_name = ?, " +
                "last_name = ?, " +
                "profession = ?, " +
                "email = ?, " +
                "phone = ?, " +
                "active = ? " +
                "WHERE staff_id = ?";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(
                    1,
                    staff.getFirstName()
            );

            pstmt.setString(
                    2,
                    staff.getLastName()
            );

            pstmt.setString(
                    3,
                    staff.getProfession()
            );

            pstmt.setString(
                    4,
                    staff.getEmail()
            );

            pstmt.setString(
                    5,
                    staff.getPhone()
            );

            pstmt.setBoolean(
                    6,
                    staff.isActive()
            );

            pstmt.setInt(
                    7,
                    staff.getStaffId()
            );

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // ============================================================
    // 10. UPDATE AVAILABILITY
    // ============================================================
    public boolean updateAvailability(
            int staffId,
            boolean isAvailable) {

        return true;
    }


    // ============================================================
    // 11. DELETE STAFF
    // Soft delete - deactivate user account
    // ============================================================
    public boolean deleteStaff(int staffId) {

        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        try {

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);


            String getUserIdQuery =
                    "SELECT user_id FROM staff " +
                    "WHERE staff_id = ?";

            pstmt1 =
                    conn.prepareStatement(
                            getUserIdQuery
                    );

            pstmt1.setInt(1, staffId);

            ResultSet rs =
                    pstmt1.executeQuery();

            if (rs.next()) {

                int userId =
                        rs.getInt("user_id");

                String deactivateQuery =
                        "UPDATE users SET is_active = 0 " +
                        "WHERE user_id = ?";

                pstmt2 =
                        conn.prepareStatement(
                                deactivateQuery
                        );

                pstmt2.setInt(1, userId);

                pstmt2.executeUpdate();

                System.out.println(
                        "Deactivated staff (user_id: "
                        + userId + ")"
                );
            }

            conn.commit();

            return true;

        } catch (SQLException e) {

            try {

                if (conn != null) {
                    conn.rollback();
                }

            } catch (SQLException ex) {

                ex.printStackTrace();
            }

            e.printStackTrace();

            return false;

        } finally {

            try {

                if (pstmt1 != null) {
                    pstmt1.close();
                }

                if (pstmt2 != null) {
                    pstmt2.close();
                }

                if (conn != null) {
                    conn.setAutoCommit(true);
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }


    // ============================================================
    // 12. CHECK EMAIL
    // ============================================================
    public boolean emailExists(String email) {

        String query =
                "SELECT COUNT(*) FROM staff WHERE email = ?";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }


    // ============================================================
    // 13. COUNT STAFF BY PROFESSION
    // ============================================================
    public int countStaffByProfession(
            String profession) {

        String query =
                "SELECT COUNT(*) FROM staff s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "WHERE s.profession = ? " +
                "AND u.is_active = 1";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             PreparedStatement pstmt =
                     conn.prepareStatement(query)) {

            pstmt.setString(
                    1,
                    profession.toUpperCase()
            );

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }


    // ============================================================
    // 14. GET AVAILABLE STAFF
    // ============================================================
    public List<Staff> getAvailableStaff() {

        List<Staff> staffList =
                new ArrayList<>();

        String query =
                "SELECT s.*, u.username, r.role_name, u.is_active " +
                "FROM staff s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN roles r ON u.role_id = r.role_id " +
                "WHERE u.is_active = 1 " +
                "ORDER BY s.profession, s.last_name, s.first_name";

        try (Connection conn =
                     DatabaseConnection.getConnection();

             Statement stmt =
                     conn.createStatement();

             ResultSet rs =
                     stmt.executeQuery(query)) {

            while (rs.next()) {

                staffList.add(
                        mapResultSetToStaff(rs)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return staffList;
    }


    // ============================================================
    // 15. MAP RESULT SET TO STAFF
    // ============================================================
    private Staff mapResultSetToStaff(
            ResultSet rs) throws SQLException {

        Staff staff = new Staff();

        staff.setStaffId(
                rs.getInt("staff_id")
        );

        staff.setUserId(
                rs.getInt("user_id")
        );

        staff.setFirstName(
                rs.getString("first_name")
        );

        staff.setLastName(
                rs.getString("last_name")
        );

        staff.setProfession(
                rs.getString("profession")
        );


        // Fields not currently used
        staff.setSpecialization(null);
        staff.setLicenseNumber(null);
        staff.setYearsExperience(0);
        staff.setAvailable(true);
        staff.setCreatedAt(null);
        staff.setUpdatedAt(null);


        staff.setEmail(
                rs.getString("email")
        );


        try {

            staff.setPhone(
                    rs.getString("phone")
            );

        } catch (SQLException e) {

            staff.setPhone(null);
        }


        try {

            staff.setActive(
                    rs.getBoolean("active")
            );

        } catch (SQLException e) {

            staff.setActive(true);
        }


        try {

            staff.setActive(
                    rs.getBoolean("is_active")
            );

        } catch (SQLException e) {

            // Keep staff.active value
        }


        try {

            staff.setUsername(
                    rs.getString("username")
            );

        } catch (SQLException e) {

            staff.setUsername(null);
        }


        try {

            staff.setRoleName(
                    rs.getString("role_name")
            );

        } catch (SQLException e) {

            staff.setRoleName(null);
        }


        return staff;
    }
}