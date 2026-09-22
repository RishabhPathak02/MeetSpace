package com.rishabh.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Notifies a user that a booking has been transferred to them.
     *
     * @param toEmail     The new owner's email address
     * @param toName      The new owner's full name
     * @param roomName    The meeting room name
     * @param startTime   Formatted start time string
     * @param endTime     Formatted end time string
     * @param purpose     Booking purpose (can be null)
     */
    @Async
    public void sendBookingTransferEmail(
            String toEmail,
            String toName,
            String roomName,
            String startTime,
            String endTime,
            String purpose) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("📅 A Meeting Room Booking Has Been Transferred to You");
            message.setText(buildTransferEmailBody(toName, roomName, startTime, endTime, purpose));

            mailSender.send(message);
            log.info("Transfer email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send transfer email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendRegisterEmail(
            String toEmail ,
            String toName
    ){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("📅 Welcome to MeetSpace — Your Account Is Ready");

            message.setText(
                    "Hello " + toName + ",\n\n" +

                            "Welcome to MeetSpace! 🎉\n\n" +

                            "Thank you for registering with us. Your account has been successfully created, " +
                            "and you're now ready to book meeting rooms with ease.\n\n" +

                            "You can now:\n" +
                            "• Browse available meeting rooms\n" +
                            "• Check room availability\n" +
                            "• Create and manage your bookings\n" +
                            "• Transfer bookings to other registered users\n\n" +

                            "We’re glad to have you onboard!\n\n" +

                            "Best regards,\n" +
                            "Team MeetSpace"
            );

            mailSender.send(message);
            log.info("Transfer email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send transfer email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildTransferEmailBody(
            String toName,
            String roomName,
            String startTime,
            String endTime,
            String purpose) {

        return String.format(
                "Hi %s,%n%n" +
                "A meeting room booking has been transferred to you. Here are the details:%n%n" +
                "  Room    : %s%n" +
                "  Start   : %s%n" +
                "  End     : %s%n" +
                "  Purpose : %s%n%n" +
                "Please log in to MeetSpace to view and manage your reservation.%n%n" +
                "Regards,%n" +
                "The MeetSpace Team",
                toName,
                roomName,
                startTime,
                endTime,
                purpose != null && !purpose.isBlank() ? purpose : "—"
        );
    }
}
