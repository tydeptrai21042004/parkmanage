package park.management.com.vn.service;

public interface EmailService {
    void sendHtml(String to, String subject, String htmlBody);
    void sendText(String to, String subject, String textBody);
}
