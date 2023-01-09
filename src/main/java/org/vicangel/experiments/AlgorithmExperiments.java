package org.vicangel.experiments;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vicangel.metrics.EvaluationMetrics;
import org.vicangel.writer.FileWriterHandler;
import org.vicangel.writer.IFileWriter;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public abstract class AlgorithmExperiments implements FileWriteable {

  private static final Logger LOGGER = Logger.getLogger(AlgorithmExperiments.class.getName());
  private final int availableProcessors = Runtime.getRuntime().availableProcessors() - 2;

  {
    LOGGER.log(Level.INFO, "Available processors for parallel execution: {0}", availableProcessors);
  }

  protected final Executor executor = Executors.newFixedThreadPool(availableProcessors);

  public abstract void performExperiments() throws Exception;

  public abstract String getDefaultWekaOptionsSet();

  @Override
  public void writeToFile(final List<? extends EvaluationMetrics> metricsList) throws IOException {
    final IFileWriter fileWriter = new FileWriterHandler();
    fileWriter.writeToFile(metricsList);
  }
}
