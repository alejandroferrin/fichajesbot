package com.alexfer.fichajesbot.document;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.DownloadType;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;
import com.alexfer.fichajesbot.util.DateTimeFormatters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
public class DocumentScreen extends DocumentTemplate {

  private static final String SEPARATOR = ", ";
  private static final String LINE_1 = "_________________________________________________";
  private static final String FICHAJES = "FICHAJES";
  private static final String HORAS_TRABAJADAS = "Horas: ";
  private static final String TOTAL_HOURS = "Total: ";
  private static final String USUARIO = "Usuario: ";
  private static final String ID = "Id: ";
  private static final String FECHA = "Fecha: ";
  private static final String TIPO = "Tipo: ";
  private static final String DOT = ".";

  @Override
  protected String getDocument(User user, List<Movement> movements, boolean isForAdmin) {
    var time = movements.stream().findFirst().map(Movement::getTime);
    var report = getReport(movements);
    return
        new StringWriter()
            .append(USUARIO)
            .append(user.getName())
            .append(SEPARATOR)
            .append(RETURN)
            .append(time.map(localDateTime -> String.valueOf(localDateTime.getMonth())).orElse(EMPTY))
            .append(SEPARATOR)
            .append(time.map(localDateTime -> String.valueOf(localDateTime.getYear())).orElse(EMPTY))
            .append(SEPARATOR)
            .append(RETURN)
            .append(report)
            .append(RETURN)
            .append(FICHAJES)
            .append(SEPARATOR)
            .append(RETURN)
            .append(getMovementsTable(movements, isForAdmin))
            .append(RETURN)
            .append(LINE_1)
            .append(RETURN)
            .toString();
  }

  @Override
  public String getDocument(Map<User, List<Movement>> users, boolean isForAdmin) {
    var writer = new StringWriter();
    users.forEach((user, movements) -> writer.append(getDocument(user, movements, isForAdmin)));
    return writer.toString();
  }

  @Override
  public boolean itApplies(DownloadType type) {
    return Objects.equals(DownloadType.PANTALLA, type);
  }


  private static String getReport(List<Movement> movements) {
    var reportWriter = new StringWriter();
    Pair<AtomicInteger, List<ReportRow>> totalHoursAndRows =
        getTotalHoursAndRows(movements);
    totalHoursAndRows.getSecond()
        .forEach(row ->
            reportWriter
                .append(FECHA)
                .append(row.date())
                .append(SEPARATOR)
                .append(HORAS_TRABAJADAS)
                .append(row.hours())
                .append(StringUtils.isEmpty(row.comment()) ? DOT : SEPARATOR + row.comment() + DOT)
                .append(RETURN)
        );
    reportWriter
        .append(TOTAL_HOURS)
        .append(String.format("%.2f", totalHoursAndRows.getFirst().get() / 60.0))
        .append(RETURN);
    return reportWriter.toString();
  }


  private static String getMovementsTable(List<Movement> movements, boolean isForAdmin) {
    StringWriter writer = new StringWriter();
    writer.append(movements.stream().map(mov -> movementToStringScreen(mov, isForAdmin)).collect(Collectors.joining(RETURN)));
    return writer.toString();
  }

  private static String movementToStringScreen(Movement movement, boolean isForAdmin) {
    return new StringWriter()
        .append(isForAdmin ? ID : EMPTY).append(isForAdmin ? String.valueOf(movement.getId()) : EMPTY).append(isForAdmin ? SEPARATOR : EMPTY)
        .append(FECHA)
        .append(movement.getTime() != null ? movement.getTime().format(DateTimeFormatters.getDateTimeFormatterCsv(SEPARATOR)) : EMPTY)
        .append(SEPARATOR)
        .append(TIPO)
        .append(movement.getType() != null ? movement.getType().name() : EMPTY)
        .append(StringUtils.isEmpty(movement.getComment()) ? DOT : SEPARATOR + movement.getComment() + DOT)
        .toString();
  }

}
