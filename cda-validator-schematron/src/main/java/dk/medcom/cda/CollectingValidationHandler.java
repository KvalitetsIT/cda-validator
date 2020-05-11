package dk.medcom.cda;

import dk.medcom.cda.model.ValidationEntry;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectingValidationHandler {

  public enum Level {
    INFO, WARNING, ERROR
  }

  Map<Level, List<ValidationEntry>> map;

  public CollectingValidationHandler() {

    map = Map.of(Level.INFO, new LinkedList<>(),
            Level.WARNING, new LinkedList<>(),
            Level.ERROR, new LinkedList<>());
  }

  public void handleError(final ValidationEntry validationEntry) {
    map.get(Level.ERROR).add(validationEntry);
  }

  public void handleInfo(final ValidationEntry validationEntry) {
    map.get(Level.INFO).add(validationEntry);
  }

  public void handleWarning(final ValidationEntry validationEntry) {
    map.get(Level.WARNING).add(validationEntry);
  }

  public Map<Level, List<ValidationEntry>> getDiagnostics() {
    return Collections.unmodifiableMap(map);
  }
}