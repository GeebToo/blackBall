import java.awt.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Gestionnaire pour la souris de la fenetre du menu principal
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 **/
class MenuListener implements MouseListener, MouseMotionListener
{
   /**
    * Menu principal.
    */
   public Menu m;

   /**
    * Constructeur du gestionnaire de la souris.
    * @param m
    *       Menu principal.
    */
   public MenuListener (Menu m)
   {
      this.m = m;
   }

   /**
    * Methode appelé lors d'un click de la souris. Si le clic est sur l'un
    * des choux du menu : execute l'action associé.
    * @param ev
    *       Evenemende la souris t lié au click.
    */
   public void mouseClicked (MouseEvent ev)
   {
      //récupération de la position de la souris
      final Point p = ev.getPoint();

      /* Affiche menu selection de joueur */
      if(m.zoneNewPart.contains(p) || m.zoneRegleJouer.contains(p))
         m.cont.showSelectJ();
      else if (m.zoneOption.contains(p)) /* affiche les options */
         m.cont.showOptions();
      else if (m.zoneRegle.contains(p))  /* Affiche regles */
         m.showRegles();
      else if (m.zoneQuit.contains(p)) /* Quitte le jeu */
         m.cont.quit();
      else if (m.zoneRegleMenu.contains(p)) // Affiche Menu
         m.showMenu();
      else if (m.zoneContinuer.contains(p)) /* Continuer partie */
         m.cont.resumeGame();
      else if (m.zoneHelp.contains(p)) // Affiche aide
         m.show_help();
   }

   /**
    * Methode appelé lors d'un mouvement de la souris. Si la souris
    * est sur un des choix du menu : affiche une bille devant.
    * @param ev
    *       Evenement de la souris lié au mouvement.
    */
   public void mouseMoved (MouseEvent ev)
   {
      //récupération de la position de la souris
      final Point p = ev.getPoint();

      if(m.zoneNewPart.contains(p))
         m.afficheBille(1);
      else if (m.zoneOption.contains(p))
         m.afficheBille(2);
      else if (m.zoneRegle.contains(p))
         m.afficheBille(3);
      else if (m.zoneQuit.contains(p))
         m.afficheBille(4);
      else if (m.zoneRegleJouer.contains(p))
         m.afficheBille(5);
      else if (m.zoneRegleMenu.contains(p))
         m.afficheBille(6);
      else if (m.zoneContinuer.contains(p))
         m.afficheBille(7);
      else
         m.afficheBille(0);
      m.repaint();
   }

   public void mouseEntered(MouseEvent ev) {}
   public void mouseDragged(MouseEvent ev) {}
   public void mousePressed(MouseEvent ev) {}
   public void mouseReleased(MouseEvent e) {}
   public void mouseExited(MouseEvent ev) {}
}
