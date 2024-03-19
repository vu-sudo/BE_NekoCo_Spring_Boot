package vjames.developer.MessConnect.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vjames.developer.MessConnect.Models.Dtos.UserDto;
import vjames.developer.MessConnect.RequestBodies.LoginReqBody;
import vjames.developer.MessConnect.RequestBodies.RefreshTokenReqBody;
import vjames.developer.MessConnect.RequestBodies.RegisterReqBody;
import vjames.developer.MessConnect.RequestBodies.ResetPasswordReqBody;
import vjames.developer.MessConnect.CustomResponses.RegisterResponse;
import vjames.developer.MessConnect.Services.AuthenticationService;
import vjames.developer.MessConnect.Services.EmailService;
import vjames.developer.MessConnect.Services.TokenService;
import vjames.developer.MessConnect.Utils.ApplicationResponseData;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final EmailService emailService;

    public AuthenticationController(AuthenticationService authenticationService, TokenService tokenService, EmailService emailService) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterReqBody request) {
        try {
            if (request.getEmail() == null)
                return ApplicationResponseData.buildResponse(HttpStatus.BAD_REQUEST, "Missing email field!");
            if (request.getEmail().isEmpty())
                return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "Must assign an valid " +
                        "email");
            if(!authenticationService.isValidEmail(request.getEmail()))
                return  ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "Email is not valid!");
            if (request.getPassword() == null)
                return ApplicationResponseData.buildResponse(HttpStatus.BAD_REQUEST,"Missing password field!");
            if(request.getPassword().isEmpty())
                return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "Password can not empty!");
            if(!authenticationService.isValidPassword(request.getPassword()))
                return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "Invalid password! " +
                        "example valid password: aAbcD123@");
            UserDto existedUser = authenticationService.findingUserByUsername(request);
            if (existedUser != null) {
                if(existedUser.getUsername().equals(request.getEmail())) {
                    return ApplicationResponseData.buildResponse(HttpStatus.FOUND, String.format("User %s is already existed!", existedUser.getUsername()));
                }
            }

            new UserDto();
            UserDto createdUser;
            if(emailService.isDeliverable(request.getEmail())) {
                createdUser = authenticationService.createNewUser(request);
                String verifyEmailLink = emailService.generateVerificationLink(createdUser.getEmail());
                System.out.println(LocalDateTime.now() + "  Sending verify link: " + verifyEmailLink);
                emailService.sendVerificationEmail(createdUser.getEmail(), createdUser.getUsername(), Map.of(
                        "username", createdUser.getUsername(),
                        "verifyAddress", verifyEmailLink
                ));
            } else return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "The email you provided is not valid, please make sure register email is real and valid to delivery");
            return ApplicationResponseData.buildResponse(HttpStatus.CREATED, "Register successfully! An email has " +
                            "been send to your email, please check it!",
                    RegisterResponse.builder()
                            .userId(createdUser.getId())
                            .username(createdUser.getUsername())
                            .build());
        } catch (Exception ex) {
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Register failed!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginReqBody request) {
        try {
            UserDto userDto = authenticationService.findingUserByUsername(request.getUsername());
            if(userDto == null) return ApplicationResponseData.buildResponse(HttpStatus.BAD_REQUEST, "User with " +
                    "email: " + request.getUsername() + " doesn't exist in the system yet!, please try register");
            if(!emailService.isEmailVerified(request.getUsername())) {
                return ApplicationResponseData.buildResponse(HttpStatus.LOCKED, "Need to verify your email before " +
                        "login to the system!", Map.of(
                                "userName", request.getUsername()
                ));
            }
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Login successfully!", authenticationService.authenticate(request));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed! | Make sure" +
                    " your username and password are correct!");
        }
    }
    @PatchMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody RefreshTokenReqBody request) {
        try {
            boolean isRefreshTokenValid = tokenService.isValidRefreshToken(request.getRefreshToken());
            if(!isRefreshTokenValid) {
                return ApplicationResponseData.buildResponse(HttpStatus.BAD_REQUEST, "Refresh token isn't valid or " +
                        "expire!");}
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Refresh AccessToken successfully!",
                    authenticationService.refreshAccessToken(request));
        } catch (Exception ex) {
            return ApplicationResponseData.responseInternalError();
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            tokenService.removeAccessToken(token);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Log out success!");
        } catch (Exception ex) {
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
    @PostMapping("/sendResetPasswordCode")
    public ResponseEntity<Map<String, Object>> sendResetPasswordCode(@RequestParam("email") String email) {
        try {
            UserDto foundedUserDto = authenticationService.findingUserByUsername(email);
            if(foundedUserDto == null) {
                return ApplicationResponseData.buildResponse(HttpStatus.NOT_FOUND, "Email doesn't exist in the system" +
                        " yet! Can't try send email to reset password");
            }
            if(!emailService.isEmailVerified(email)) {
                return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED,
                        "Accounts with email " + email + " doesn't verify yet! Please verify your account's email " +
                                "before do reset password!");
            }
            tokenService.createAndSendResetPassCodeToUserWithId(foundedUserDto.getId(), foundedUserDto.getEmail());
            return ApplicationResponseData.buildResponse(HttpStatus.OK,
                    "Sent reset password email to user: " + foundedUserDto.getUsername() + " successfully!");
        } catch (Exception ex) {
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when try to send reset password code");
        }
    }
    @PatchMapping("/resetPassword")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestParam("passCode") String passCode,
                                                             @RequestBody ResetPasswordReqBody request) {
        try {
            if(request.getUsername().isEmpty()) return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "Username is required!");
            if(request.getNewPassword().isEmpty()) return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "Password is required!");
            UserDto foundedUserDto = authenticationService.findingUserByUsername(request.getUsername());
            if(foundedUserDto == null) {
                return ApplicationResponseData.buildResponse(HttpStatus.NOT_FOUND, "Email doesn't exist in the system" +
                        " yet!");
            }
            if(!tokenService.isResetPasswordCodeExistedInDB(passCode)) return ApplicationResponseData.buildResponse(HttpStatus.NOT_FOUND, "Reset code doesn't exist in the system!");
            if(tokenService.isResetPasswordCodeExpired(passCode)) return ApplicationResponseData.buildResponse(HttpStatus.EXPECTATION_FAILED, "Reset code had been expired!, Please resend email to get new reset code!");
            tokenService.getPassCodeAndUpdatingUserPassword(passCode, request);
            return ApplicationResponseData.buildResponse(HttpStatus.OK, "Reset password successfully!");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ApplicationResponseData.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when try to reset password!");
        }
    }
}
