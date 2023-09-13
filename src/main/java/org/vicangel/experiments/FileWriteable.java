package org.vicangel.experiments;

import java.io.IOException;
import java.util.List;

import org.vicangel.metrics.EvaluationMetrics;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public interface FileWriteable {

  void writeToFile(List<? extends EvaluationMetrics> metricsList) throws IOException;

  void writeToFile(final String evaluationOutput, final boolean mode) throws IOException;
}
