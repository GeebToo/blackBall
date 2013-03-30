import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Fenetre qui permet de choisir la composition des équipes avant de commencer
 * une nouvelle partie.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class SelectionEquipeFrame extends JFrame
{
   /**
    * Couleur du fond de la fenetre.
    */
   final static Color colFond = new Color (1, 127, 126);
   /**
    * Titre de la fenetre de selection des équipes.
    */
   final static String TITLE = "Black Ball \u00A9, Ecran de selection";

   /**
    * JPanel de cette fentre, qui contient les infos a propos des équipes
    * choisie par le joueur.
    */
   SelectionEquipe sPan;

   /**
    * Controller de l'application.
    */
   public Controller cont;

   /**
    * Delimite la zone qui permet de valider le choix des équipes.
    */
   public Rectangle zoneValiderEq;
   /**
    * Délimite ae zone qui permet de revenir en arrière.
    */
   public Rectangle zoneRetour;
   /**
    * Délimite la zone qui permet d'afficher l'aide.
    */
   public Rectangle zoneHelp;

   /**
    * Constructeur de SelectionEquipeFrame.
    *
    * @param control
    *       Controller de l'application.
    */
   public SelectionEquipeFrame (Controller control)
   {
      /* Initialise la fenetre */
      this.setTitle(SelectionEquipeFrame.TITLE);
      this.setSize(Fenetre.WIDTH, Fenetre.HEIGHT);
      this.setLocationRelativeTo(null);
      this.setLayout(null);
      this.setBackground(SelectionEquipeFrame.colFond);
      this.cont = control;

      sPan = new SelectionEquipe();
      sPan.setPreferredSize(new Dimension(Fenetre.WIDTH, Fenetre.HEIGHT-25));
      sPan.setSize(new Dimension(Fenetre.WIDTH, Fenetre.HEIGHT-25));
      this.getContentPane().add(sPan);

      /* Initialise les zones clickable */
      zoneValiderEq = new Rectangle(280, 600, 240, 60);
      zoneRetour = new Rectangle(700, 50, 100, 30);
      zoneHelp = new Rectangle(20, 40, 20, 40);

      /* Listeners */
      addWindowListener(new WindowAdapter()
            {
               public void windowClosing(WindowEvent ev)
               {
                  Controller.exit(0);
               }
            }
      );
      addMouseListener(new MouseAdapter()
            {
               public void mouseClicked(MouseEvent ev)
               {
                  final Point p = ev.getPoint();
                  if (zoneValiderEq.contains(p))
                     cont.startNewGame();
                  else if (zoneRetour.contains(p))
                     cont.retourMenu(Controller.FEN_SELECT_J);
                  else if (zoneHelp.contains(p))
                     show_help();
               }
            }
      );
      addMouseMotionListener(new MouseMotionAdapter()
            {
               public void mouseMoved(MouseEvent ev)
               {
                  if (zoneValiderEq.contains(ev.getPoint()))
                     sPan.paintEffetNewPart = true;
                  else
                     sPan.paintEffetNewPart = false;
                  sPan.repaint();
               }
            }
      );
   }

   /**
    * Affiche une boite de dialogue qui montre des infos pouvant aider
    * l'utilisateur (touches, ...).
    */
   public void show_help ()
   {
      final String message = "          Aide blackBall [Fenêtre de selection des équipes]\n\n"
         + "Vous devez choisir la composition des équipes qui vont s'affronter lors \n"
         + "de la prochaine partie. Chaque ligne représente un joueur, vous pouvez \n"
         + "changer le nom de chaque joueur et choisir si ce joueur sera contrôlé \n"
         + "par l'ordinateur (ou un joueur humain) en cochant (ou décochant) la case \n"
         + "à coté de son nom. L'ajout (et la suppression) de joueurs se fait grâce \n"
         + "aux boutons ' + ' (et ' - ') situés en bas de la fenêtre. Vous pouvez revenir \n"
         + "au menu principal en cliquant sur Retour en haut à droite de la fenêtre.\n\n"
         + "Une fois les équipes choisies, vous pouvez débuter la nouvelle partie à \n"
         + "l'aide d'un clic sur lancer la partie (au centre en bas de la fenêtre).\n";
      JOptionPane.showMessageDialog(this, message, "Aide blackBall [Fenetre de selection des équipes]",
            0, cont.imgHelp);
   }
}
