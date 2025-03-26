package exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import exception.room.RoomNotFoundOnSpecifiedDateException;
import exception.room.RoomStatusCannotBeChangedException;
import exception.service.ServiceAdditionDateException;
import exception.room.RoomUnacceptablePriceException;
import exception.tenant.NoTenantsInTheHotelException;
import exception.tenant.TenantAlreadyExistsException;
import exception.room.RoomHistoryNotFoundException;
import exception.hotel.HotelAlreadyExistsException;
import exception.tenant.TenantSettleErrorException;
import exception.service.ServiceNotFoundException;
import exception.room.NoRoomsInTheHotelException;
import exception.room.RoomAlreadyExistsException;
import exception.tenant.TenantNotFoundException;
import org.springframework.http.ResponseEntity;
import exception.hotel.HotelNotFoundException;
import exception.room.RoomNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import lombok.extern.slf4j.Slf4j;
import dto.ErrorResponseDTO;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HotelNotFoundException.class, NoRoomsInTheHotelException.class})
    public ResponseEntity<ErrorResponseDTO> handleHotelExceptions(
            Exception exception) {
        return createErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HotelAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> hotelAlreadyExistsException(
            HotelAlreadyExistsException exception) {
        return createErrorResponse(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({RoomNotFoundException.class, RoomNotFoundOnSpecifiedDateException.class,
            RoomHistoryNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleRoomExceptions(
            Exception exception) {
        return createErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomUnacceptablePriceException.class)
    public ResponseEntity<ErrorResponseDTO> roomUnacceptablePriceException(
            RoomUnacceptablePriceException exception) {
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoomStatusCannotBeChangedException.class)
    public ResponseEntity<ErrorResponseDTO> roomStatusCannotBeChangedException(
            RoomStatusCannotBeChangedException exception) {
        return createErrorResponse(exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RoomAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> roomAlreadyExistsException(
            RoomAlreadyExistsException exception) {
        return createErrorResponse(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({TenantAlreadyExistsException.class, NoTenantsInTheHotelException.class,
            TenantNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleTenantExceptions(
            Exception exception) {
        return createErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TenantSettleErrorException.class)
    public ResponseEntity<ErrorResponseDTO> tenantSettleErrorException(
            TenantSettleErrorException exception) {
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ServiceAdditionDateException.class, ServiceNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleServiceExceptions(
            Exception exception) {
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(
            Exception exception) {
        return createErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponseDTO> createErrorResponse(
            Exception exception, HttpStatus status) {
        errorLog(exception);
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(exception.getMessage(), status.value());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    // Метод для Создания Лога Ошибки:
    private void errorLog(Exception exception) {
        log.error("Ошибка: {}", exception.getMessage());
    }
}