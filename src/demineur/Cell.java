package demineur;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;

/**
 * Class Cell pour projet Demineur. 
 * @author Xavier 
 */
public final class Cell extends JPanel {
  /** Position x de la cellule */
  private final int x;
  /** Position y de la cellule */
  private final int y;
  /** Taille de la cellule */
  private final int size;
  /** Tableaux de toutes les icones possibles */
  private final ImageIcon[] icons;
  /** Etat piege */
  private final boolean trapped;
  /** Etat explose */
  private boolean exploded;
  /** Etat desamorce */
  private boolean defused;
  /** Etat pour savoir si la cellule a ete cliquee */
  private boolean clicked;
  /** Nombre de voisins pieges */
  private int value;
  /** Label contenant l'image */
  private JLabel img;

  /**
   * Constructeur 
   * @param x Position x
   * @param y Position y
   * @param size Taille de la cellule
   * @param icons Icones
   */
  public Cell(int x, int y, int size, ImageIcon[] icons) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.icons = icons;

    trapped = Math.random() * 100 < 15;
    clicked = false;
    defused = false;
    exploded = false;

    setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    setImage(false);
  }

  /**
   * Selectionne l'image a afficher dans le label
   * @param gameOver Indicateur de fin de jeu
   */
  public void setImage(boolean gameOver) {
    int indexIcon;
    if (clicked) {
      if (!trapped /*|| defused*/) {
        indexIcon = value; // value
      } else {
        indexIcon = 9; // bomb
      }
    } else if (defused) {
      indexIcon = 10; // bombDefused
    } else {
      indexIcon = 11; // Cellule
    }
    if (!trapped && defused && gameOver) {
      indexIcon = 12; // bombWrongDefused
    }
    if (exploded) {
      indexIcon = 13; // bombExploded
    } 

    ImageIcon toResize = icons[indexIcon];
    Image resized = toResize.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
    if (img != null) {
      remove(img);
    } 
    img = new JLabel(new ImageIcon(resized), JLabel.CENTER);
    add(img);
  }

  /** Active une Cellule si elle n'a pas ete desamorcee */
  public void reveal() {
    if (!defused) {
      clicked = true;
    }
  }

  /** Amorce ou desamorce une cellule */
  public void defuse() {
    if(!clicked) {
      defused = !defused;
    }
  }

  /** Fait exploser la bombe */
  public void explode() {
    exploded = true;
  }

  // Getters
  public boolean isTrapped() { return trapped; }
  public boolean isDefused() { return defused; }
  public boolean isClicked() { return clicked; }
  public int getValue() { return value; }
  public JLabel getImg() { return img; }
  
  // Setters
  public void setValue(int value) { this.value = value; }

}
