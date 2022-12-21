package org.vicangel.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.vicangel.metrics.EvaluationMetrics;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class FileWriterHandler implements IFileWriter {

  private static final String outputFilePath = "src/main/resources/output.txt";

  /**
   * Creates the file if the file does not exist. If it exists, file will be overwritten.
   *
   * @param metricsList
   */
  @Override
  public void writeToFile(final List<? extends EvaluationMetrics> metricsList) throws IOException {
    try (final FileWriter myWriter = new FileWriter(outputFilePath)) {

      metricsList.forEach(metric -> {
        try {
          myWriter.write(metric.toString() + System.lineSeparator());
          myWriter.flush();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }
}
