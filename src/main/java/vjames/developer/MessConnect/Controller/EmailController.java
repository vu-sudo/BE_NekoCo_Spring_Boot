package vjames.developer.MessConnect.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vjames.developer.MessConnect.Entities.Verify;
import vjames.developer.MessConnect.Services.EmailService;
import vjames.developer.MessConnect.Utils.ApplicationResponseData;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<Map<String, Object>> confirm(@RequestParam("email") String email,
                                                       @RequestParam("verify-token") String verifyToken) {
        try {
            Verify verify = emailService.getVerifyFromToken(verifyToken);
            if(verify != null && !emailService.isVerifyTokenExpired(email)) {
                emailService.doVerifyEmail(email);
            } else return ApplicationResponseData.buildResponse(HttpStatus.BAD_REQUEST,"Verification address is " +
                    "invalid!");
            if(emailService.isEmailVerified(email)) {
                return ApplicationResponseData.buildResponse(HttpStatus.FOUND, "Email had been verified!");
            }
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Thank you for your verification!");
        } catch (Exception ex) {
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when" +
                    " trying to confirm email!");
        }
    }
    @PatchMapping("resendVerifyEmail")
    public ResponseEntity<Map<String, Object>> resendEmailToVerify(@RequestParam("email") String email) {
        try {
            if(emailService.isEmailVerified(email)) {
                return ApplicationResponseData.buildResponse(HttpStatus.FOUND, "Email had been verified!");
            }
            String newToken = UUID.randomUUID().toString();
            emailService.refreshVerifyToken(email, newToken);
            String verifyAddress = emailService.generateVerificationLink(email, newToken);
            System.out.println(LocalDateTime.now() + "  Resending verification address: " + verifyAddress);
            emailService.sendVerificationEmail(email, email, Map.of(
                    "username", email,
                    "verifyAddress", verifyAddress
            ));
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Has been resent an email to verify your " +
                    "email!");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when" +
                    " trying to resend email!");
        }
    }

}
