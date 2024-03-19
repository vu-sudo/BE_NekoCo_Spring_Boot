package vjames.developer.MessConnect.Exceptions;

public class PasswordExpiredException extends RuntimeException{
    public PasswordExpiredException(String message){
        super(message);
    }
}
