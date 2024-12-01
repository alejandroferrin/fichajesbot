package com.alexfer.fichajesbot.document;

import com.alexfer.fichajesbot.adapter.in.telegram.persistance.model.DownloadType;
import com.alexfer.fichajesbot.domain.Movement;
import com.alexfer.fichajesbot.domain.User;
import com.alexfer.fichajesbot.util.DateTimeFormatters;
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
public class DocumentCsv extends DocumentTemplate {

  private static final String SEPARATOR = ";";
  private static final String LINE_1 = "_________";
  private static final String FICHAJES = "FICHAJES";
  private static final String ID = "Id";
  private static final String HORA = "Hora";
  private static final String COMENTARIO = "Comentario";
  private static final String TIPO = "Tipo";
  private static final String FECHA = "Fecha";
  private static final String HORAS_TRABAJADAS = "Horas";
  private static final String TOTAL_HOURS = "Total:";
  private static final String USUARIO = "Usuario:";

  @Override
  protected String getDocument(User user, List<Movement> movements, boolean isForAdmin) {
    var time = movements.stream().findFirst().map(Movement::getTime);
    var report = getReport(movements);
    return
        new StringWriter()
            .append(USUARIO)
            .append(SEPARATOR)
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
            .append(LINE_1).append(SEPARATOR).append(LINE_1).append(SEPARATOR).append(LINE_1).append(SEPARATOR).append(LINE_1).append(SEPARATOR).append(LINE_1).append(RETURN)
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
    return Objects.equals(DownloadType.DOCUMENTO, type);
  }

  private static String getReport(List<Movement> movements) {
    var reportWriter = new StringWriter();
    addReportHeader(reportWriter);
    Pair<AtomicInteger, List<ReportRow>> totalHoursAndRows =
        getTotalHoursAndRows(movements);
    totalHoursAndRows.getSecond()
        .forEach(row ->
            reportWriter.append(row.date())
                .append(SEPARATOR)
                .append(row.hours())
                .append(SEPARATOR)
                .append(row.comment())
                .append(SEPARATOR)
                .append(RETURN)
        );
    reportWriter
        .append(TOTAL_HOURS)
        .append(SEPARATOR)
        .append(String.format("%.2f", totalHoursAndRows.getFirst().get() / 60.0))
        .append(RETURN);
    return reportWriter.toString();
  }

  private static void addReportHeader(StringWriter writer) {
    writer.append(FECHA)
        .append(SEPARATOR)
        .append(HORAS_TRABAJADAS)
        .append(SEPARATOR)
        .append(COMENTARIO)
        .append(SEPARATOR)
        .append(RETURN);
  }

  private static String getMovementsTable(List<Movement> movements, boolean isAdmin) {
    StringWriter writer = new StringWriter();
    generateTableHeader(writer, isAdmin);
    writer.append(movements.stream().map(mov -> movementToStringCsv(mov, isAdmin)).collect(Collectors.joining(RETURN)));
    return writer.toString();
  }

  private static void generateTableHeader(StringWriter writer, boolean isAdmin) {
    writer
        .append(isAdmin ? ID : EMPTY).append(isAdmin ? SEPARATOR : EMPTY)
        .append(FECHA)
        .append(SEPARATOR)
        .append(HORA)
        .append(SEPARATOR)
        .append(TIPO)
        .append(SEPARATOR)
        .append(COMENTARIO)
        .append(SEPARATOR)
        .append(RETURN);
  }


  private static String movementToStringCsv(Movement movement, boolean isAdmin) {
    return new StringWriter()
        .append(isAdmin ? String.valueOf(movement.getId()) : EMPTY).append(isAdmin ? SEPARATOR : EMPTY)
        .append(movement.getTime() != null ? movement.getTime().format(DateTimeFormatters.getDateTimeFormatterCsv(SEPARATOR)) : EMPTY)
        .append(SEPARATOR)
        .append(movement.getType() != null ? movement.getType().name() : EMPTY)
        .append(SEPARATOR)
        .append(movement.getComment() == null ? "" : movement.getComment())
        .append(SEPARATOR)
        .toString();
  }


}
