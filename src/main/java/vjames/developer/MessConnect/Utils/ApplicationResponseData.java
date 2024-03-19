package vjames.developer.MessConnect.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class ApplicationResponseData {
    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus httpStatus, String message, Object value) {
        return ResponseEntity.status(httpStatus.value()).body(
                Map.of(
                        "status", httpStatus.value(),
                        "body", Map.of(
                                "applicationResponseData", value
                        ),
                        "message", message,
                        "responseCreateAt", LocalDateTime.now().toString()
                )
        );
    }
    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus.value()).body(
                Map.of(
                        "status", httpStatus.value(),
                        "body", Map.of(
                                "applicationResponseData", Collections.emptyList()
                        ),
                        "message", message,
                        "responseCreatedAt", LocalDateTime.now().toString()
                )
        );
    }
    public static ResponseEntity<Map<String, Object>> responseInternalError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "message", "Something went wrong!",
                        "responseCreatedAt", LocalDateTime.now().toString()
                )
        );
    }
}
