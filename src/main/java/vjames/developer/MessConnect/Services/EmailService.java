package vjames.developer.MessConnect.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.reactive.function.client.WebClient;
import vjames.developer.MessConnect.Entities.Verify;
import vjames.developer.MessConnect.Repositories.VerifyRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EmailService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaMailSender mailSender;
    private final Configuration configuration;
    private final VerifyRepository verifyRepository;

    @Value("${spring.mail.username}")
    private String sendFrom;

    public EmailService(JavaMailSender mailSender, Configuration configuration, VerifyRepository verifyRepository) {
        this.mailSender = mailSender;
        this.configuration = configuration;
        this.verifyRepository = verifyRepository;
    }

    public String requestToCheckingValidEmailAddress(String email) {
        try {
            String apiUrl = "https://emailvalidation.abstractapi.com/v1/" +
                    "?api_key=df6b2a67009d4255bc084306de531aeb&email=" + email;
            WebClient webClient = WebClient.create();
            String responseBody = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("deliverability").asText();
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return null;
        }
    }
    public boolean isDeliverable(String email) {
//        String deliverableStatus = requestToCheckingValidEmailAddress(email);
//        System.out.println(LocalDateTime.now() + "  " + email + " Deliverable status: " + deliverableStatus);
//        if(deliverableStatus.equals("UNKNOWN")) return false;
//        return !deliverableStatus.equals("UNDELIVERABLE");
        return true;
    }
    public void sendVerificationEmail(String to, String name, Map<String, Object> model) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
            Template t = configuration.getTemplate("verification-email.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject("Welcome to MeowCo, let's verify your account to access the application!");
            mimeMessageHelper.setFrom(sendFrom);
            mailSender.send(message);
            System.out.println(LocalDateTime.now() + "  Sent email to " + to + " successfully!");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void sendEmailToResetPassword(String to, Map<String, Object> model) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
            Template t = configuration.getTemplate("reset-password-email.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
             mimeMessageHelper.setTo(to);
             mimeMessageHelper.setText(html, true);
             mimeMessageHelper.setSubject("From MeowCo | Reset your password");
             mimeMessageHelper.setFrom(sendFrom);
             mailSender.send(message);
            System.out.println(LocalDateTime.now() + "  Sent reset password email to " + to + " successfully!");
            //todo
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void refreshVerifyToken(String userEmail, String newToken) {
        verifyRepository.updateVerifyForResendEmailByUserEmail(
                userEmail,
                newToken,
                LocalDateTime.now().plusMinutes(10),
                false,
                null);
        System.out.println(LocalDateTime.now() + "  Updated verify token for email: " + userEmail);
    }
    public String generateVerificationLink(String toEmail) {
        Verify verify = verifyRepository.findByUserEmail(toEmail).orElseThrow();
        String BASE_URL= "http://localhost:8080/api/v1/";
        System.out.println(LocalDateTime.now() + "  Get verify token: " + verify.getVerifyToken() + " of user: " + verify.getUserEmail());
        return BASE_URL+"email/confirm?email=" + verify.getUserEmail() + "&verify-token=" + verify.getVerifyToken();
    }
    public String generateVerificationLink(String toEmail, String newToken) {
        String BASE_URL= "http://localhost:8080/api/v1/";
        System.out.println(LocalDateTime.now() + "  Get verify token: " + newToken + " of user: " + toEmail);
        return BASE_URL+"email/confirm?email=" + toEmail + "&verify-token=" + newToken;
    }

    public boolean isEmailVerified(String userEmail) {
        Verify verify = verifyRepository.findByUserEmail(userEmail).orElseThrow();
        return verify.getIsVerify();
    }
    public boolean isVerifyTokenExpired(String userEmail) {
        Verify verify = verifyRepository.findByUserEmail(userEmail).orElseThrow();
        return verify.getExpireAt().isBefore(LocalDateTime.now());
    }
    public void doVerifyEmail(String userEmail) {
        Verify verify = verifyRepository.findByUserEmail(userEmail).orElseThrow();
        try {
            verifyRepository.updateVerifyStatusById(verify.getId(), true, LocalDateTime.now());
        } catch (Exception ex) {
            System.out.println(LocalDateTime.now() +"   Do verify email failed");
        }
    }
    public Verify getVerifyFromToken(String token) {
        try {
            return verifyRepository.findByVerifyToken(token).orElseThrow();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

}
