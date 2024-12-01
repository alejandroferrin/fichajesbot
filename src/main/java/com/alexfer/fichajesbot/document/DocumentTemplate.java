package com.alexfer.fichajesbot.document;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.DownloadType;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.Type;
import com.alexfer.fichajesbot.domain.User;
import com.alexfer.fichajesbot.util.DateTimeFormatters;
import org.springframework.data.util.Pair;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public abstract class DocumentTemplate {


  protected static final String RETURN = "\n";
  protected static final String SPACE = " ";
  protected static final int FIRST = 0;
  private static final String COMPROBAR_FICHAJES = "Comprobar fichajes del día, asignadas 8h por defecto";

  protected abstract String getDocument(User user, List<Movement> movements, boolean isForAdmin);

  public abstract String getDocument(Map<User, List<Movement>> users, boolean isForAdmin);

  public abstract boolean itApplies(DownloadType type);

  protected static Pair<AtomicInteger, List<ReportRow>> getTotalHoursAndRows(List<Movement> movements) {
    Map<LocalDate, List<Movement>> groupedMovements = movements.stream()
        .collect(Collectors.groupingBy(movement -> movement.getTime().toLocalDate()));
    Map<LocalDate, List<Movement>> sortedGroupedMovements = groupedMovements.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
    AtomicInteger totalHours = new AtomicInteger();
    List<ReportRow> rows = new ArrayList<>();
    sortedGroupedMovements.forEach((date, dailyMovements) -> rows.add(getReportRow(date, dailyMovements, totalHours)));
    return Pair.of(totalHours, rows);
  }


  private static ReportRow getReportRow(LocalDate date, List<Movement> dailyMovements, AtomicInteger totalMinutes) {
    List<Movement> inMovements = dailyMovements.stream()
        .filter(m -> m.getType() == Type.ENTRADA)
        .sorted(Comparator.comparing(Movement::getTime))
        .toList();
    List<Movement> outMovements = dailyMovements.stream()
        .filter(m -> m.getType() == Type.SALIDA)
        .sorted(Comparator.comparing(Movement::getTime))
        .toList();
    int minutes;
    String comment = EMPTY;
    if (inAndOutExistAndMatches(inMovements, outMovements)) {
      LocalDateTime reference = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
      int outSum = outMovements.stream()
          .map(movement -> Duration.between(reference, movement.getTime()).toMinutes())
          .mapToInt(Long::intValue)
          .sum();
      int inSum = inMovements.stream()
          .map(movement -> Duration.between(reference, movement.getTime()).toMinutes())
          .mapToInt(Long::intValue)
          .sum();
      minutes = outSum - inSum;
    } else {
      minutes = 8 * 60;
      comment = COMPROBAR_FICHAJES;
    }
    totalMinutes.getAndAdd(minutes);

    return new ReportRow(date.format(DateTimeFormatters.getDateFormatter()), String.format("%.2f", minutes / 60.0), comment);
  }

  private static boolean inAndOutExistAndMatches(List<Movement> inMovements, List<Movement> outMovements) {
    return
        !inMovements.isEmpty() &&
            !outMovements.isEmpty() &&
            inMovements.size() == outMovements.size() &&
            outMovements.get(FIRST).getTime().isAfter(inMovements.get(FIRST).getTime());
  }

  public record ReportRow(String date, String hours, String comment) {
  }

}
