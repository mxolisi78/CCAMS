package ccams.services;

import ccams.models.Settings;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private Settings settings;

    public EmailService() {
        settings = new SettingsService().getSettings();
    }

    public boolean sendEmail(String to, String subject, String body) {

        // Gmail Account
        final String username = "yourgmail@gmail.com";
        final String password = "YOUR_16_CHARACTER_APP_PASSWORD";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);

            // Sender must match authenticated account
            message.setFrom(new InternetAddress(username));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully.");

            return true;

        } catch (MessagingException e) {

            System.out.println("Email sending failed.");
            e.printStackTrace();

            return false;
        }
    }

    public void sendAppointmentConfirmation(
            String studentEmail,
            String studentName,
            String date,
            String time,
            String staffName) {

        String subject = "Appointment Confirmation - CCAMS";

        String body =
                "Dear " + studentName + ",\n\n"
                + "Your appointment has been confirmed.\n\n"
                + "Date: " + date + "\n"
                + "Time: " + time + "\n"
                + "Healthcare Professional: " + staffName + "\n\n"
                + "Please arrive at least 10 minutes before your appointment.\n\n"
                + "Regards,\n"
                + settings.getClinicName();

        sendEmail(studentEmail, subject, body);
    }

    public void sendAppointmentReminder(
            String studentEmail,
            String studentName,
            String date,
            String time,
            String staffName) {

        String subject = "Appointment Reminder - CCAMS";

        String body =
                "Dear " + studentName + ",\n\n"
                + "This is a reminder about your upcoming appointment.\n\n"
                + "Date: " + date + "\n"
                + "Time: " + time + "\n"
                + "Healthcare Professional: " + staffName + "\n\n"
                + "Please arrive 10 minutes early.\n\n"
                + "Regards,\n"
                + settings.getClinicName();

        sendEmail(studentEmail, subject, body);
    }

    public void sendCancellationNotification(
            String studentEmail,
            String studentName,
            String date,
            String time,
            String reason) {

        String subject = "Appointment Cancelled - CCAMS";

        String body =
                "Dear " + studentName + ",\n\n"
                + "Unfortunately your appointment has been cancelled.\n\n"
                + "Date: " + date + "\n"
                + "Time: " + time + "\n"
                + "Reason: " + reason + "\n\n"
                + "Please log in to CCAMS to book another appointment.\n\n"
                + "Regards,\n"
                + settings.getClinicName();

        sendEmail(studentEmail, subject, body);
    }
}