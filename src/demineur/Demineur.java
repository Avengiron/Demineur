package demineur;

import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

/**
 * Fenetre principale du jeu de Demineur. Au demarrage, la grille de cellule est
 * generee. Une case a 15% de chance d'etre piegee. On calcule pour chaque case
 * une valeur indiquant le nombre de voisins pieges. Le reste se fait lors d'un
 * clic de souris (selon les regles du jeu). A la fin du jeu (victoire ou
 * defaite), une fenetre secondaire apparait et propose de quitter le jeu ou de
 * rejouer, auquel cas, on regenere le plateau.
 * @see https://fr.wikipedia.org/wiki/D%C3%A9mineur_(genre_de_jeu_vid%C3%A9o)
 * @author Xavier
 */
public final class Demineur extends JFrame {
  /** Nombre de colonnes */
  private final int nbCols = 20;
  /** Nombre de lignes */
  private final int nbRows = 15;
  /** Taille de la cellule */
  private final int size = 30;
  /** Tableaux de toutes les icones possibles */
  private final ImageIcon[] icons;
  /** Tableau de cellules */
  private Cell[][] grille;
  /** Liste des cellules piegees */
  private ArrayList<Cell> bombList;
  /** Liste des cellules non piegees */
  private ArrayList<Cell> otherCells;
  /** Fin de jeu */
  private boolean gameOver;
  /** Protection contre les clics multiples */
  private boolean doOnce;
  /** Panel pour contenu principal */
  private JPanel board;
  /** Fenetre principale */
  private static Demineur jeu;

  /** Point d'entree de l'application */
  public static void main(String[] args) {
    jeu = new Demineur();
  }

  /** Construction de la fenetre */
  public Demineur() {
    // Chargement de toutes les icones
    icons = new ImageIcon[14];
    for (int i = 0; i <= 8; i++) {
      icons[i] = new ImageIcon("data\\" + i + ".png");
    }
    icons[9]  = new ImageIcon("data\\bomb.png");
    icons[10] = new ImageIcon("data\\bombDefused.png");
    icons[11] = new ImageIcon("data\\cell.png");
    icons[12] = new ImageIcon("data\\bombWrongDefused.png");
    icons[13] = new ImageIcon("data\\bombExploded.png");

    grille = new Cell[nbCols][nbRows];
    setup();

    // Construction des interactions avec le joueur
    doOnce = false;
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent me) {
        if (!doOnce && !gameOver) {
          doOnce = true;
          if (me.getButton() == MouseEvent.BUTTON1) {
            leftClickAction(me);
          } else if (me.getButton() == MouseEvent.BUTTON3) {
            rightClickAction(me);
          }
          if (gameOver) {
            int x = (me.getX() - 3) / size;
            int y = (me.getY() - 26) / size;
            grille[x][y].explode();
            new Endgame(jeu, false);
          }
          if (victory() && !gameOver) {
            new Endgame(jeu, true);
            gameOver = true;
          }
          refresh();
        }
      }

      @Override
      public void mouseReleased(MouseEvent me) {
        doOnce = false;
      }
    });

    setIconImage(new ImageIcon("data\\icone.png").getImage());
    setTitle("Démineur");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    pack();

    setLocationRelativeTo(null);
    setVisible(true);
  }

  /** Construction de la grille de cellules */
  public void setup() {
    getContentPane().removeAll();
    bombList = new ArrayList<>();
    otherCells = new ArrayList<>();
    board = new JPanel(new GridLayout(nbRows, nbCols));
    
    // Generation de la grille
    for (int j = 0; j < nbRows; j++) {
      for (int i = 0; i < nbCols; i++) {
        grille[i][j] = new Cell(j, i, size, icons);
      }
    }
    
    // Mise a jour des infos de la grille et des listes, 
    // et ajoute la cellule au plateau
    for (int j = 0; j < nbRows; j++) {
      for (int i = 0; i < nbCols; i++) {
        if (grille[i][j].isTrapped()) {
          bombList.add(grille[i][j]);
        } else {
          otherCells.add(grille[i][j]);
          grille[i][j].setValue(computeValue(i, j));
        }
        board.add(grille[i][j]);
      }
    }
    gameOver = false;
    getContentPane().add(board);
  }

  /**
   * Action a faire sur un clic gauche de souris. Si la cellule cliquee est
   * piegee, on declenche la fin du jeu. Sinon, on essaie de reveler la plus
   * grande zone non piegee possible.
   * @param me Event de souris
   */
  private void leftClickAction(MouseEvent me) {
    int x = (me.getX() - 3) / size;
    int y = (me.getY() - 26) / size;

    if (!grille[x][y].isDefused()) {
      grille[x][y].reveal();
      if (grille[x][y].isTrapped()) {
        gameOver();
      } else if (grille[x][y].getValue() == 0) {
        revealSides(x, y);
      }
    }
  }

  /**
   * Action a faire sur un clic droit de souris. Si la cellule a deja ete
   * desamorcee, retire le drapeau. Sinon, indique que la cellule est
   * desamorcee.
   * @param me Event de souris
   */
  private void rightClickAction(MouseEvent me) {
    int x = (me.getX() - 3) / size;
    int y = (me.getY() - 26) / size;
    grille[x][y].defuse();
  }

  /** Actualise l'affichage */
  public void refresh() {
    getContentPane().remove(board);
    for (int j = 0; j < nbRows; j++) {
      for (int i = 0; i < nbCols; i++) {
        grille[i][j].setImage(gameOver);
      }
    }
    getContentPane().add(board);
    repaint();
    validate();
  }

  /**
   * Compte le nombre de voisins pieges.
   * @param x Indice x de la cellule
   * @param y Indice y de la cellule
   * @return Nombre de voisins pieges
   */
  public int computeValue(int x, int y) {
    int compteur = 0;
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        try {
          if (grille[i][j].isTrapped()) {
            compteur++;
          }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
        }
      }
    }
    return compteur;
  }

  /**
   * Pour une cellule ne contenant aucun voisin piege, revele les cellules
   * voisines non piegees de maniere recursive.
   * @param x Position x de la cellule
   * @param y Position y de la cellule
   */
  public void revealSides(int x, int y) {
    grille[x][y].reveal();
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        try {
          if (grille[x][y].getValue() == 0 && !grille[i][j].isClicked()) {
            grille[i][j].reveal();
            revealSides(i, j);
          }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
        }
      }
    }
  }

  /** Revele tout le plateau quand la fin du jeu est detectee */
  public void gameOver() {
    for (int i = 0; i < nbCols; i++) {
      for (int j = 0; j < nbRows; j++) {
        grille[i][j].reveal();
      }
    }
    gameOver = true;
  }

  /**
   * Verifie si toutes les bombes ont ete desamorcees et que toutes les cellules
   * non piegees ont ete revelees.
   * @return Booleen indiquant s'il y a victoire et fin du jeu
   */
  public boolean victory() {
    for (int i = 0; i < bombList.size(); i++) {
      if (!bombList.get(i).isDefused()) {
        return false;
      }
    }
    for (int i = 0; i < otherCells.size(); i++) {
      if (!otherCells.get(i).isClicked()) {
        return false;
      }
    }
    return true;
  }
}
