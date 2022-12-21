package org.vicangel.experiments;

import java.io.IOException;
import java.util.List;

import org.vicangel.metrics.EvaluationMetrics;
import org.vicangel.writer.FileWriterHandler;
import org.vicangel.writer.IFileWriter;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public abstract class AlgorithmExperiments implements FileWriteable{

  public abstract void performExperiments() throws Exception;

//  abstract writeToFile(List<? extends AlgorithmExperiments> experiment)

  @Override
  public void writeToFile(final List<? extends EvaluationMetrics> metricsList) throws IOException {
    final IFileWriter fileWriter = new FileWriterHandler();
    fileWriter.writeToFile(metricsList);
  }
}
