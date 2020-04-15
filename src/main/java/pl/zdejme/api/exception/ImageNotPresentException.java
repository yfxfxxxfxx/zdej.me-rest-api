package pl.zdejme.api.exception;

public class ImageNotPresentException extends RuntimeException {
    public ImageNotPresentException(String message) {
        super(message);
    }
}
