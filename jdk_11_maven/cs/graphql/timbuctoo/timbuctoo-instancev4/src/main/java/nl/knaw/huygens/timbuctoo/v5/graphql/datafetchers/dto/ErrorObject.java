package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorObject {
  public static final Logger LOG = LoggerFactory.getLogger(ErrorObject.class);
  private final String dateStamp;
  private final String file;
  private final String method;
  private final String message;
  private final String error;

  private ErrorObject(String dateStamp, String file, String method, String message, String error) {
    this.dateStamp = dateStamp;
    this.file = file;
    this.method = method;
    this.message = message;
    this.error = error;
  }


  public static ErrorObject parse(String errorString) {
    String dateStamp = errorString.substring(0, errorString.indexOf("; ")).trim();
    String file = errorString.contains("file") ? errorString.substring(
        errorString.indexOf("file: ") + 6,
        errorString.indexOf("; method")
    ).trim() : null;
    String method = errorString.substring(errorString.indexOf("method: ") + 8, errorString.indexOf("; message")).trim();
    String message = errorString.substring(errorString.indexOf("message: ") + 9, errorString.indexOf("; error")).trim();

    String errorAsString = errorString.substring(errorString.indexOf("error: ") + 7).trim();

    return new ErrorObject(dateStamp, file, method, message, errorAsString);
  }

  public String getDateStamp() {
    return dateStamp;
  }

  public String getFile() {
    return file;
  }

  public String getMethod() {
    return method;
  }

  public String getMessage() {
    return message;
  }

  public String getError() {
    return error;
  }
}
