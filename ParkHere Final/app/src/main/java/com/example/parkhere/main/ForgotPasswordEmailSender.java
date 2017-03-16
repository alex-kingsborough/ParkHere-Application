package com.example.parkhere.main;

import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordEmailSender {

    public void sendEmail(String host, String port,
                          final String userName, final String password, String toAddress,
                          String subject, String message) throws AddressException,
            MessagingException {

        //SET SMTP SERVER PROPERTIES
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        //CREATE A NEW SESSION WITH AN AUTHENTICATOR
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };

        Session session = Session.getInstance(properties, auth);

        //CREATE A NEW EMAIL MESSAGE
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        //SEND STYLED EMAIL MESSAGE
        msg.setContent(message, "text/html");
        Transport.send(msg);
    }

    public boolean sendEmailToUser(String username, String temporaryPassword) {
        //SMTP SERVER INFORMATION
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "csci310parkhere@gmail.com";
        String password = "CS3102016!";

        //OUTGOING MESSAGE INFORMATION
        String mailTo = username;
        String subject = "Temporary Password for Your ParkHere Account";
        String message = "<body style=\"margin: 0; padding: 0;\">"
                + "<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">"
                + "<tr> <td align=\"center\" bgcolor=\"#3ABEFF\" style=\"padding: 40px 0 30px 0;\">"
                + "<img src= \"http://parkhere.000webhostapp.com/parkhere/logo.png\" width=\"300\" height=\"100\" style=\"display: block;\" />"
                + "</td> </tr>"
                + "<tr> <td bgcolor=\"#F2F7F2\" style=\"padding: 40px 30px 40px 30px;\">"
                + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"
                + "<tr> <td> Hi ParkHere User!</td> </tr>"
                + "<tr> <td style=\"padding: 20px 0 30px 0;\"> We're so happy to have you as part of our ParkHere family."
                + " Enter the following temporary password in your mobile application to regain access to your account."
                + "<br/><br/> <b>Temporary Password: " + temporaryPassword + "</b> </td> </tr>"
                + "<tr> <td> Be sure to reset your password in settings upon successfully logging in, and as always, happy parking! </td> </tr>"
                + "</table>"
                + "</td> </tr>"
                + "<tr> <td bgcolor=\"#2176FF\" style=\"padding: 30px 30px 30px 30px;\">"
                + "<font color=\"white\">ParkHere Group Project <br/> CSCI 310, University of Southern California, Fall 2016 <br/>"
                + "To be used for educational purposes only; no copyright infringement intended.</font>"
                + "</td> </tr>"
                + "</table>"
                + "</body>";
        //SEND EMAIL
        try {
            sendEmail(host, port, mailFrom, password, mailTo, subject, message);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getLocalizedMessage());
            return false;
        }
    }
}