package demineur;

import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

/**
 * Fenetre de fin de jeu. Propose au joueur de quitter le programme, ou de
 * rejouer, auquel cas on regenere le plateau de jeu.
 * @author Xavier
 */
public final class Endgame extends JFrame {

  /**
   * Constructeur de la fenetre
   * @param jeu Fenetre principale contenant le plateau de jeu
   * @param victory Indicateur de victory
   */
  public Endgame(Demineur jeu, boolean victory) {
    JLabel message = new JLabel();
    message.setFont(new Font("Dialog", 1, 24));
    message.setHorizontalAlignment(JLabel.CENTER);
    if (victory) {
      message.setText("Victoire !");
    } else {
      message.setText("Défaite !");
    }

    JButton replayButton = new JButton();
    replayButton.setText("Rejouer");
    replayButton.addActionListener((ActionEvent ae) -> {
      jeu.setup();
      jeu.refresh();
      dispose();
    });

    JButton exitButton = new JButton();
    exitButton.setText("Quitter");
    exitButton.addActionListener((ActionEvent ae) -> {
      System.exit(0);
    });

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);

    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(message, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
              .addComponent(replayButton, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)))
          .addContainerGap())
    );

    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(message, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(replayButton)
            .addComponent(exitButton))
          .addContainerGap())
    );

    setTitle("Fin de jeu");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();

    int x = jeu.getWidth() / 2 - this.getWidth() / 2;
    int y = jeu.getHeight() / 2 - this.getHeight() / 2;
    setLocation(x + jeu.getX(), y + jeu.getY());
    setResizable(false);
    setVisible(true);
  }

}
