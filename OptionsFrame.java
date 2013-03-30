import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

/**
 * Fenetre qui permet de modifier les options du jeu.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class OptionsFrame extends JFrame
{
   /**
    * Panel de modification des options.
    */
   Options oPan;

   /**
    * Zone qui permet de revenir vers le menu
    */
   public Rectangle zoneMenu;

   /**
    * Controller de l'application. Pour pouvoir changer de fenetre.
    */
   public Controller cont;

   /**
    * Constructeur de OptionsFrame.
    *
    * @param control
    *       Controller de l'application. Pour pouvoir changer de fenetre.
    */
   public OptionsFrame(Controller control)
   {
      this.setTitle("Black Ball - Options");
      this.setSize(Fenetre.WIDTH, Fenetre.HEIGHT);
      this.setLocationRelativeTo(null);
      this.setBackground(Color.BLACK);
      this.setLayout(null);

      this.cont = control;

      oPan = new Options();
      oPan.setSize(new Dimension(Fenetre.WIDTH, Fenetre.HEIGHT));
      this.getContentPane().add(oPan);
      zoneMenu =  new Rectangle((int) 668, (int) 57, (int) 119, (int) 45);

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
                  if (zoneMenu.contains(p))
                     cont.retourMenu(Controller.FEN_OPT);
               }
            }
      );
   }
}
