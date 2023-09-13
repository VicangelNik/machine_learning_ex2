package org.vicangel.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.vicangel.metrics.EvaluationMetrics;

import com.sun.istack.Nullable;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class FileWriterHandler implements IFileWriter {

  private static final String DEFAULT_FILE_NAME = "output.txt";
  private static final String OUTPUT_FILE_PATH = "src/main/resources/";

  /**
   * Creates the file if the file does not exist. If it exists, file will be overwritten.
   *
   * @param metricsList - the information to be written
   * @param fileName    - the fileName of the output
   */
  @Override
  public void writeToFile(final List<? extends EvaluationMetrics> metricsList,
                          @Nullable String fileName) throws IOException {
    try (final FileWriter myWriter = new FileWriter(OUTPUT_FILE_PATH + getFileName(fileName))) {

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

  @Override
  public void writeToFile(final String evaluationOutput,
                          @Nullable String fileName,
                          boolean mode) throws IOException {
    try (final FileWriter myWriter = new FileWriter(OUTPUT_FILE_PATH + getFileName(fileName),mode)) {
      try {
        myWriter.write(evaluationOutput);
        myWriter.flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private String getFileName(String fileName) {
    return fileName == null ? DEFAULT_FILE_NAME : fileName + ".txt";
  }
}
