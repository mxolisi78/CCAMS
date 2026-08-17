package ccams.models;

/**
 * Settings model for the Campus Clinic Appointment Management System (CCAMS)
 */
public class Settings {

    // =========================
    // General Settings
    // =========================
    private String clinicName;
    private String clinicAddress;
    private String clinicPhone;
    private String clinicEmail;
    private String clinicHours;

    // =========================
    // Appointment Settings
    // =========================
    private int appointmentDuration;
    private int maxAppointmentsPerDay;
    private int cancellationWindow;
    private int reminderHours;

    // =========================
    // Notification Settings
    // =========================
    private boolean enableEmailNotifications;
    private boolean enableSMSNotifications;
    private boolean enableAppNotifications;

    // =========================
    // Security Settings
    // =========================
    private boolean requirePasswordChange;
    private int passwordExpiryDays;
    private int maxLoginAttempts;
    private boolean enableTwoFactorAuth;

    /**
     * Default Constructor
     */
    public Settings() {

        // General
        clinicName = "Sol Plaatje University Campus Clinic";
        clinicAddress = "Sol Plaatje University, Kimberley, South Africa";
        clinicPhone = "+27 53 123 4567";
        clinicEmail = "clinic@spu.ac.za";
        clinicHours = "Monday - Friday (08:00 - 17:00)";

        // Appointment
        appointmentDuration = 30;
        maxAppointmentsPerDay = 20;
        cancellationWindow = 24;
        reminderHours = 24;

        // Notifications
        enableEmailNotifications = true;
        enableSMSNotifications = false;
        enableAppNotifications = true;

        // Security
        requirePasswordChange = false;
        passwordExpiryDays = 90;
        maxLoginAttempts = 5;
        enableTwoFactorAuth = false;
    }

    // =====================================================
    // General Settings
    // =====================================================

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getClinicPhone() {
        return clinicPhone;
    }

    public void setClinicPhone(String clinicPhone) {
        this.clinicPhone = clinicPhone;
    }

    public String getClinicEmail() {
        return clinicEmail;
    }

    public void setClinicEmail(String clinicEmail) {
        this.clinicEmail = clinicEmail;
    }

    public String getClinicHours() {
        return clinicHours;
    }

    public void setClinicHours(String clinicHours) {
        this.clinicHours = clinicHours;
    }

    // =====================================================
    // Appointment Settings
    // =====================================================

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public void setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
    }

    public int getMaxAppointmentsPerDay() {
        return maxAppointmentsPerDay;
    }

    public void setMaxAppointmentsPerDay(int maxAppointmentsPerDay) {
        this.maxAppointmentsPerDay = maxAppointmentsPerDay;
    }

    public int getCancellationWindow() {
        return cancellationWindow;
    }

    public void setCancellationWindow(int cancellationWindow) {
        this.cancellationWindow = cancellationWindow;
    }

    public int getReminderHours() {
        return reminderHours;
    }

    public void setReminderHours(int reminderHours) {
        this.reminderHours = reminderHours;
    }

    // =====================================================
    // Notification Settings
    // =====================================================

    public boolean isEnableEmailNotifications() {
        return enableEmailNotifications;
    }

    public void setEnableEmailNotifications(boolean enableEmailNotifications) {
        this.enableEmailNotifications = enableEmailNotifications;
    }

    public boolean isEnableSMSNotifications() {
        return enableSMSNotifications;
    }

    public void setEnableSMSNotifications(boolean enableSMSNotifications) {
        this.enableSMSNotifications = enableSMSNotifications;
    }

    public boolean isEnableAppNotifications() {
        return enableAppNotifications;
    }

    public void setEnableAppNotifications(boolean enableAppNotifications) {
        this.enableAppNotifications = enableAppNotifications;
    }

    // =====================================================
    // Security Settings
    // =====================================================

    public boolean isRequirePasswordChange() {
        return requirePasswordChange;
    }

    public void setRequirePasswordChange(boolean requirePasswordChange) {
        this.requirePasswordChange = requirePasswordChange;
    }

    public int getPasswordExpiryDays() {
        return passwordExpiryDays;
    }

    public void setPasswordExpiryDays(int passwordExpiryDays) {
        this.passwordExpiryDays = passwordExpiryDays;
    }

    public int getMaxLoginAttempts() {
        return maxLoginAttempts;
    }

    public void setMaxLoginAttempts(int maxLoginAttempts) {
        this.maxLoginAttempts = maxLoginAttempts;
    }

    public boolean isEnableTwoFactorAuth() {
        return enableTwoFactorAuth;
    }

    public void setEnableTwoFactorAuth(boolean enableTwoFactorAuth) {
        this.enableTwoFactorAuth = enableTwoFactorAuth;
    }

    // =====================================================
    // Utility
    // =====================================================

    @Override
    public String toString() {
        return "Settings{" +
                "clinicName='" + clinicName + '\'' +
                ", clinicEmail='" + clinicEmail + '\'' +
                ", appointmentDuration=" + appointmentDuration +
                ", emailNotifications=" + enableEmailNotifications +
                ", smsNotifications=" + enableSMSNotifications +
                ", twoFactorAuth=" + enableTwoFactorAuth +
                '}';
    }
}