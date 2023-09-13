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
  private final String useCase;
  private final int availableProcessors = Runtime.getRuntime().availableProcessors() - 1;
  protected int optionsCounter = 0;
  protected final Executor executor = Executors.newFixedThreadPool(availableProcessors);

  {
    LOGGER.log(Level.INFO, "Available processors for parallel execution: {0}", availableProcessors);
  }

  protected AlgorithmExperiments(String useCase) {
    this.useCase = useCase;
  }

  /**
   * -1 to let one available pcu for the robustness operation of the pc
   */
  public abstract void performExperiments(final boolean useConcurrency) throws Exception;

  protected abstract String getDefaultWekaOptionsSet();

  protected void printNumberOfExperiments(int size) {
    LOGGER.log(Level.INFO, "Number of experiments to be executed: {0}", size);
  }

  @Override
  public void writeToFile(final List<? extends EvaluationMetrics> metricsList) throws IOException {
    final IFileWriter fileWriter = new FileWriterHandler();
    fileWriter.writeToFile(metricsList, useCase);
  }

  @Override
  public void writeToFile(final String evaluationOutput, final boolean mode) throws IOException {
    new FileWriterHandler().writeToFile(evaluationOutput, useCase,mode);
  }
}
