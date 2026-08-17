package ccams.services;

import ccams.models.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class SettingsService {

    private static final String CONFIG_DIRECTORY = "config";
    private static final String SETTINGS_FILE = CONFIG_DIRECTORY + "/settings.properties";

    private Settings settings;

    public SettingsService() {
        createConfigDirectory();
        loadSettings();
    }

    private void createConfigDirectory() {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIRECTORY));
        } catch (IOException e) {
            System.err.println("Failed to create config directory.");
            e.printStackTrace();
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public void loadSettings() {

        settings = new Settings();

        File file = new File(SETTINGS_FILE);

        if (!file.exists()) {
            System.out.println("Settings file not found. Creating default configuration...");
            saveSettings();
            return;
        }

        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(file)) {

            props.load(fis);

            // =========================
            // General
            // =========================

            settings.setClinicName(
                    props.getProperty("clinic.name", settings.getClinicName()));

            settings.setClinicAddress(
                    props.getProperty("clinic.address", settings.getClinicAddress()));

            settings.setClinicPhone(
                    props.getProperty("clinic.phone", settings.getClinicPhone()));

            settings.setClinicEmail(
                    props.getProperty("clinic.email", settings.getClinicEmail()));

            settings.setClinicHours(
                    props.getProperty("clinic.hours", settings.getClinicHours()));

            // =========================
            // Appointment
            // =========================

            settings.setAppointmentDuration(
                    getInt(props, "appointment.duration",
                            settings.getAppointmentDuration()));

            settings.setMaxAppointmentsPerDay(
                    getInt(props, "appointment.maxPerDay",
                            settings.getMaxAppointmentsPerDay()));

            settings.setCancellationWindow(
                    getInt(props, "appointment.cancellationWindow",
                            settings.getCancellationWindow()));

            settings.setReminderHours(
                    getInt(props, "appointment.reminderHours",
                            settings.getReminderHours()));

            // =========================
            // Notifications
            // =========================

            settings.setEnableEmailNotifications(
                    getBoolean(props, "notifications.email",
                            settings.isEnableEmailNotifications()));

            settings.setEnableSMSNotifications(
                    getBoolean(props, "notifications.sms",
                            settings.isEnableSMSNotifications()));

            settings.setEnableAppNotifications(
                    getBoolean(props, "notifications.app",
                            settings.isEnableAppNotifications()));

            // =========================
            // Security
            // =========================

            settings.setRequirePasswordChange(
                    getBoolean(props, "security.requirePasswordChange",
                            settings.isRequirePasswordChange()));

            settings.setPasswordExpiryDays(
                    getInt(props, "security.passwordExpiryDays",
                            settings.getPasswordExpiryDays()));

            settings.setMaxLoginAttempts(
                    getInt(props, "security.maxLoginAttempts",
                            settings.getMaxLoginAttempts()));

            settings.setEnableTwoFactorAuth(
                    getBoolean(props, "security.twoFactorAuth",
                            settings.isEnableTwoFactorAuth()));

            System.out.println("Settings loaded successfully.");

        } catch (IOException e) {

            System.err.println("Error loading settings.");
            e.printStackTrace();
        }
    }

    public boolean saveSettings() {

        Properties props = new Properties();

        // General
        props.setProperty("clinic.name", settings.getClinicName());
        props.setProperty("clinic.address", settings.getClinicAddress());
        props.setProperty("clinic.phone", settings.getClinicPhone());
        props.setProperty("clinic.email", settings.getClinicEmail());
        props.setProperty("clinic.hours", settings.getClinicHours());

        // Appointment
        props.setProperty("appointment.duration",
                String.valueOf(settings.getAppointmentDuration()));

        props.setProperty("appointment.maxPerDay",
                String.valueOf(settings.getMaxAppointmentsPerDay()));

        props.setProperty("appointment.cancellationWindow",
                String.valueOf(settings.getCancellationWindow()));

        props.setProperty("appointment.reminderHours",
                String.valueOf(settings.getReminderHours()));

        // Notifications
        props.setProperty("notifications.email",
                String.valueOf(settings.isEnableEmailNotifications()));

        props.setProperty("notifications.sms",
                String.valueOf(settings.isEnableSMSNotifications()));

        props.setProperty("notifications.app",
                String.valueOf(settings.isEnableAppNotifications()));

        // Security
        props.setProperty("security.requirePasswordChange",
                String.valueOf(settings.isRequirePasswordChange()));

        props.setProperty("security.passwordExpiryDays",
                String.valueOf(settings.getPasswordExpiryDays()));

        props.setProperty("security.maxLoginAttempts",
                String.valueOf(settings.getMaxLoginAttempts()));

        props.setProperty("security.twoFactorAuth",
                String.valueOf(settings.isEnableTwoFactorAuth()));

        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {

            props.store(fos, "CCAMS Application Settings");

            System.out.println("Settings saved successfully.");

            return true;

        } catch (IOException e) {

            System.err.println("Failed to save settings.");
            e.printStackTrace();

            return false;
        }
    }

    public void updateSettings(Settings newSettings) {

        this.settings = newSettings;
        saveSettings();
    }

    public void resetToDefaults() {

        settings = new Settings();
        saveSettings();

        System.out.println("Settings reset to defaults.");
    }

    /**
     * Safely read an integer.
     */
    private int getInt(Properties props, String key, int defaultValue) {

        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely read a boolean.
     */
    private boolean getBoolean(Properties props, String key, boolean defaultValue) {

        String value = props.getProperty(key);

        if (value == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

}