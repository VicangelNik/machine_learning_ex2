package org.vicangel.helpers;

import java.awt.BorderLayout;

import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

/**
 * @author Nikiforos Xylogiannopoulos
 * @see <a href="https://waikato.github.io/weka-wiki/roc_curves/">weka-roc_curves</a>
 */
public class VisualizeRocCurve {

  public static void visualize(Instances instances) throws Exception {

    instances.setClassIndex(instances.numAttributes() - 1);
    // method visualize
    ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
    vmc.setROCString("(Area under ROC = " +
                     Utils.doubleToString(ThresholdCurve.getROCArea(instances), 4) + ")");
    vmc.setName(instances.relationName());
    PlotData2D tempd = new PlotData2D(instances);
    tempd.setPlotName(instances.relationName());
    tempd.addInstanceNumberAttribute();
    // specify which points are connected
    boolean[] cp = new boolean[instances.numInstances()];
    for (int n = 1; n < cp.length; n++) {
      cp[n] = true;
    }
    tempd.setConnectPoints(cp);
    // add plot
    vmc.addPlot(tempd);
    // method visualizeClassifierErrors
    String plotName = vmc.getName();
    final javax.swing.JFrame jf =
      new javax.swing.JFrame("Weka Classifier Visualize: " + plotName);
    jf.setSize(500, 400);
    jf.getContentPane().setLayout(new BorderLayout());

    jf.getContentPane().add(vmc, BorderLayout.CENTER);
    jf.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent e) {
        jf.dispose();
      }
    });

    jf.setVisible(true);
  }
}
