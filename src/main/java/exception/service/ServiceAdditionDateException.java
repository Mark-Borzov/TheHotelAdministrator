package exception.service;

public class ServiceAdditionDateException extends RuntimeException {

    public ServiceAdditionDateException(String message) {
        super(message);
    }
}