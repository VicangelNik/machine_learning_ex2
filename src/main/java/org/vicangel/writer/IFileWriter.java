package org.vicangel.writer;

import java.io.IOException;
import java.util.List;

import org.vicangel.metrics.EvaluationMetrics;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public interface IFileWriter {

  void writeToFile(final List<? extends EvaluationMetrics> metricsList) throws IOException;
}
