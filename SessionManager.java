package Library_System;

import java.sql.Timestamp;

public class SessionManager {

    // Private static variables to store admin session data
    private static long currentAdminId;
    private static String currentAdminName;
    private static String currentAdminDepartment;
    private static String currentAdminContactNumber;
    private static String currentAdminEmail;
    private static Timestamp adminCreatedDate;

    // Private static variables to store user session data
    private static long currentUserId;
    private static String currentUsername;
    private static String currentUserType;
    private static String currentCourse;
    private static String currentUserDepartment;
    private static String currentUserContactNumber;
    private static String currentUserEmail;
    private static Timestamp userCreatedDate;

    // Session type tracker
    private static String currentSessionType; // "ADMIN" or "USER"

    // ADMIN SESSION METHODS
    // Call this when admin successfully logs in
    public static void loginAdmin(long adminId, String adminName, String department,
            String contactNumber, String email, Timestamp createdDate) {
        // Clear any existing sessions
        clearAllSessions();

        currentAdminId = adminId;
        currentAdminName = adminName;
        currentAdminDepartment = department;
        currentAdminContactNumber = contactNumber;
        currentAdminEmail = email;
        adminCreatedDate = createdDate;
        currentSessionType = "ADMIN";
    }

    // USER SESSION METHODS
    // Call this when user successfully logs in
    public static void loginUser(long userId, String username, String userType, String course,
            String department, String contactNumber, String email, Timestamp createdDate) {
        // Clear any existing sessions
        clearAllSessions();

        currentUserId = userId;
        currentUsername = username;
        currentUserType = userType;
        currentCourse = course;
        currentUserDepartment = department;
        currentUserContactNumber = contactNumber;
        currentUserEmail = email;
        userCreatedDate = createdDate;
        currentSessionType = "USER";
    }

    // LOGOUT AND CLEAR METHODS
    // Clear all session data when logging out
    public static void logout() {
        clearAllSessions();
    }

    // Private method to clear all session data
    private static void clearAllSessions() {
        // Clear admin data
        currentAdminId = 0;
        currentAdminName = null;
        currentAdminDepartment = null;
        currentAdminContactNumber = null;
        currentAdminEmail = null;
        adminCreatedDate = null;

        // Clear user data
        currentUserId = 0;
        currentUsername = null;
        currentUserType = null;
        currentCourse = null;
        currentUserDepartment = null;
        currentUserContactNumber = null;
        currentUserEmail = null;
        userCreatedDate = null;

        // Clear session type
        currentSessionType = null;
    }

    // ADMIN GETTERS
    public static long getCurrentAdminId() {
        return currentAdminId;
    }

    public static String getCurrentAdminName() {
        return currentAdminName;
    }

    public static String getCurrentAdminDepartment() {
        return currentAdminDepartment;
    }

    public static String getCurrentAdminContactNumber() {
        return currentAdminContactNumber;
    }

    public static String getCurrentAdminEmail() {
        return currentAdminEmail;
    }

    public static Timestamp getAdminCreatedDate() {
        return adminCreatedDate;
    }

    // USER GETTERS
    public static long getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentUserType() {
        return currentUserType;
    }

    public static String getCurrentCourse() {
        return currentCourse;
    }

    public static String getCurrentUserDepartment() {
        return currentUserDepartment;
    }

    public static String getCurrentUserContactNumber() {
        return currentUserContactNumber;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static Timestamp getUserCreatedDate() {
        return userCreatedDate;
    }

    // SESSION STATUS METHODS
    // Check if admin is logged in
    public static boolean isAdminLoggedIn() {
        return currentAdminId != 0 && "ADMIN".equals(currentSessionType);
    }

    // Check if user is logged in
    public static boolean isUserLoggedIn() {
        return currentUserId != 0 && "USER".equals(currentSessionType);
    }

    // Check if anyone is logged in
    public static boolean isAnyoneLoggedIn() {
        return isAdminLoggedIn() || isUserLoggedIn();
    }

    // Get current session type
    public static String getCurrentSessionType() {
        return currentSessionType;
    }

    // Check if current user is a student
    public static boolean isCurrentUserStudent() {
        return isUserLoggedIn() && "Student".equals(currentUserType);
    }

    // Check if current user is faculty
    public static boolean isCurrentUserFaculty() {
        return isUserLoggedIn() && "Faculty".equals(currentUserType);
    }

    // VALIDATION METHODS
    // Validate AdminID format (11 digits)
    public static boolean isValidAdminId(long adminId) {
        return adminId >= 10000000000L && adminId <= 99999999999L;
    }

    // Validate UserID format (11 digits)
    public static boolean isValidUserId(long userId) {
        return userId >= 10000000000L && userId <= 99999999999L;
    }

    // Validate UserType
    public static boolean isValidUserType(String userType) {
        return "Student".equals(userType) || "Faculty".equals(userType);
    }

    // CONVENIENCE METHODS
    // Get current user's display name (username for users, admin name for admins)
    public static String getCurrentDisplayName() {
        if (isAdminLoggedIn()) {
            return currentAdminName;
        } else if (isUserLoggedIn()) {
            return currentUsername;
        }
        return "Not logged in";
    }

    // Get current user's ID (works for both admin and user)
    public static long getCurrentActiveId() {
        if (isAdminLoggedIn()) {
            return currentAdminId;
        } else if (isUserLoggedIn()) {
            return currentUserId;
        }
        return 0;
    }

    // Get current user's department (works for both admin and user)
    public static String getCurrentActiveDepartment() {
        if (isAdminLoggedIn()) {
            return currentAdminDepartment;
        } else if (isUserLoggedIn()) {
            return currentUserDepartment;
        }
        return null;
    }

    // Get current user's email (works for both admin and user)
    public static String getCurrentActiveEmail() {
        if (isAdminLoggedIn()) {
            return currentAdminEmail;
        } else if (isUserLoggedIn()) {
            return currentUserEmail;
        }
        return null;
    }
}
