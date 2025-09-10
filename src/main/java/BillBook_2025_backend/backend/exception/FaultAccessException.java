package BillBook_2025_backend.backend.exception;

public class FaultAccessException extends RuntimeException {
  public FaultAccessException(String message) {
    super(message);
  }
}
