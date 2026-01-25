package com.davidpe.tasker.application.ui.effects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;

/** Small reusable utility to play a type-writer effect on a JavaFX Text node. */
public final class TypeWriterEffect {

  private TypeWriterEffect() {}

  /**
   * Plays a type-writer animation on the given Text node.
   *
   * @param text the full text to type
   * @param textNode the JavaFX Text node to update
   * @param charIntervalSeconds interval between characters in seconds (e.g. 0.08)
   */
  public static void playTypeWriterEffect(String text, Text textNode, double charIntervalSeconds) {
    if (text == null || textNode == null || charIntervalSeconds <= 0) return;

    textNode.setText("");
    StringBuilder currentText = new StringBuilder();

    Timeline timeline = new Timeline();
    for (int i = 0; i < text.length(); i++) {
      final int index = i;
      KeyFrame keyFrame =
          new KeyFrame(
              Duration.seconds(index * charIntervalSeconds),
              e -> {
                currentText.append(text.charAt(index));
                textNode.setText(currentText.toString());
              });
      timeline.getKeyFrames().add(keyFrame);
    }
    timeline.play();
  }
}
