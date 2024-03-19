package vjames.developer.MessConnect.Utils;

import java.util.Random;

public class RandomSequenceGenerator {
    private static final int SEQUENCE_LENGTH = 6;
    private static final String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String generateRandomSequenceWithText() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        // Determine the position to insert the text
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
                char randomChar = ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length()));
                stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

}