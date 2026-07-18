package result;

public record ClearResult(String message) {

  public ClearResult() {
    this(null);
  }
}