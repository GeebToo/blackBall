import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.imageio.ImageIO;
import java.io.IOException;


/**
 * Menu principal de l'application. Premiere fenetre affiché au démarrage de
 * l'application, permet de voir les regles, modifier les options, reprendre
 * une partie sauvegardé ou en commencer une nouvelle.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Menu extends JFrame
{
   /**
    * Titre de la fenetre du menu principal.
    */
   public final static String TITLE = "Black Ball \u00A9";
   /* Zones */
   /**
    * Délimite le zone qui permet d'afficher le menu de selection des equipes.
    */
   public Rectangle zoneNewPart;
   /**
    * Délimite le zone qui permet d'afficher le menu des options.
    */
   public Rectangle zoneOption;
   /**
    * Délimite le zone qui permet d'afficher les Regles.
    */
   public Rectangle zoneRegle;
   /**
    * Délimite le zone qui permet quitter le programme.
    */
   public Rectangle zoneQuit;
   /**
    * Délimite le zone qui permet de continuer une partie sauvegardé.
    */
   public Rectangle zoneContinuer;
   /**
    * Délimite le zone qui permet de lancer une partie depuis le menu Regles.
    */
   public Rectangle zoneRegleJouer;
   /**
    * Délimite le zone qui permet de revenir au menu principale
    * depuis le menu Regles.
    */
   public Rectangle zoneRegleMenu;
   /**
    * Délimite le zone qui permet d'afficher l'aide.
    */
   public Rectangle zoneHelp;

   /**
    * Abscisse de l'image help. Utilisé pour dessiner le bouton.
    */
   public int xHelp;
   /**
    * Ordonnée de l'image help. Utilisé pour dessiner le bouton.
    */
   public int yHelp;

   /* Images */
   /**
    * Image de fond du menu principal.
    */
   public Image fond;
   /**
    * Image de bille dessiné a coté de l'option survolé par la souris.
    */
   public Image bille;
   /**
    * Image utilisé comme effet sur la page des regles, afin de savoir
    * quel bouton est survolé par la souris.
    */
   public Image effet;
   /**
    * Image ajouté au fond pour représenté le choix continuer dans
    * le menu principal. Pour reprendre une partie precedement sauvegardé.
    */
   public Image fondContinuer;
   /**
    * Option du menu principal survolé par la souris.
    */
   public int opt;
   /**
    * Controller de l'application. Pour pouvoir changer de fenetre.
    */
   public Controller cont;

   /**
    * Permet de retenir s'il faut affciher le continuer. A true si une
    * partie a été sauvegardé et qu'on se trouve sur le menu principal
    */
   public boolean paintContinuer;

   /**
    * Constructeur du Menu principal.
    * @param control
    *       Controller de l'application.
    */
   public Menu(Controller control) /* constructeur */
   {
      this.setTitle(Menu.TITLE);
      this.setSize(Fenetre.WIDTH, Fenetre.HEIGHT);
      this.setLocationRelativeTo(null); /* pour centrer la fenetre */
      this.setBackground(Color.BLACK);
      this.setLayout(null);

      try {
         bille = ImageIO.read(getClass().getResourceAsStream("img/fondBille.png"));
         effet = ImageIO.read(getClass().getResourceAsStream("img/fondEffet.png"));
         fondContinuer = ImageIO.read(getClass().getResourceAsStream("img/fondMenuContinuer.png"));
      }
      catch(IOException e) { }

      /* Listeners */
      addWindowListener(new WindowAdapter() {
               public void windowClosing(WindowEvent ev)
               {
                  Controller.exit(0);
               }
      } );
      MenuListener ml = new MenuListener(this);
      this.addMouseListener(ml);
      this.addMouseMotionListener(ml);

      this.cont = control;

      /* Charge fen Menu principal */
      showMenu();

      addKeyListener(new KeyAdapter()
            {
               public void keyTyped(KeyEvent e)
               {
                  final char keyC = e.getKeyChar();
                  if (keyC == 'q')
                     cont.quit();
                  else if (keyC == 'j' || keyC == 'n')
                     cont.showSelectJ();
                  else if (keyC == 'c')
                     cont.resumeGame();
                  else if (keyC == 'r')
                     showRegles();
                  else if (keyC == '?')
                     show_help();
                  else if (keyC == 'o')
                     cont.showOptions();
               }
            } );
   }

   /**
    * Initialise les zones cliquable pour le Menu principal.
    */
   public void initRectMenu()
   {
      zoneNewPart   = new Rectangle((int) 70, (int) 380, (int) 268, (int) 33);
      zoneOption   = new Rectangle((int) 70, (int) 444, (int) 131, (int) 32);
      zoneRegle  = new Rectangle((int) 70, (int) 506, (int) 117, (int) 33);
      zoneQuit = new Rectangle((int) 70, (int) 567, (int) 130, (int) 31);
      if (cont.gameSaved)
         zoneContinuer = new Rectangle((int) 70, (int) 325, (int) 168, (int) 27);
      zoneHelp = new Rectangle(40, 40, 40, 40);
      xHelp = 50;
      yHelp = 50;
   }

   /**
    * Initialise les zones cliquable pour le Menu des Regles.
    */
   public void initRectRegles()
   {
      zoneRegleJouer = new Rectangle((int) 441, (int) 600, (int) 134, (int) 38);
      zoneRegleMenu  = new Rectangle((int) 655, (int) 600, (int) 125, (int) 38);
      zoneHelp = new Rectangle(740, 40, 40, 40);
      xHelp = 750;
   }

   /**
    * Initialise à 0 toutes les zones cliquables.
    */
   public void setZeroRect()
   {
      zoneContinuer = new Rectangle((int) 0, (int) 0, (int) 0, (int) 0);
      zoneNewPart   = new Rectangle((int) 0, (int) 0, (int) 0, (int) 0);
      zoneOption    = new Rectangle((int) 0, (int) 0, (int) 0, (int) 0);
      zoneRegle      = new Rectangle((int) 0, (int) 0, (int) 0, (int) 0);
      zoneQuit     = new Rectangle((int) 0, (int) 0, (int) 0, (int) 0);
      zoneRegleJouer = new Rectangle((int) 0, (int) 0, (int) 0, (int) 0);
      zoneRegleMenu  = new Rectangle((int) 0, (int) 0, (int) 0, (int) 0);
   }

   /**
    * Affiche le menu principal. Ou le menu Regle en fonction du fond
    * chargé.
    * @param g
    *       Contexte graphique.
    */
   public void paint(Graphics g)
   {
      /* Affiche le fond */
      g.drawImage (fond, 0, 0, this);

      /* Affiche une bille sur la zone survolé par la souris */
      if (opt == 1)
         g.drawImage(bille, 30, 380, this);
      else if (opt == 2)
         g.drawImage(bille, 30, 444, this);
      else if (opt == 3) 
         g.drawImage(bille, 30, 506, this);
      else if (opt == 4)
         g.drawImage(bille, 30, 567, this);
      else if (opt == 5)
         g.drawImage(effet, 432, 591, this);
      else if (opt == 6)
         g.drawImage(effet, 638, 591, this);
      else if (opt == 7)
         g.drawImage(bille, 30, 325, this);

      /* Affiche le continuer, s'il y a une partie sauvegardé. */
      if (paintContinuer == true)
         g.drawImage (fondContinuer, 70, 325, this);

      /* Affiche bouton aide */
      g.drawImage(cont.imgHelp.getImage(), xHelp, yHelp, this);

   }

   /**
    * Charger l'image de fond du menu principal.
    */
   public void charger_imgMenu()
   {
      if (cont.gameSaved) paintContinuer = true;
      try {
         fond = ImageIO.read(getClass().getResourceAsStream("img/fondMenu.png"));
      }
      catch(IOException e) { }
   }

   /**
    * Charger l'image de fond des Regles.
    */
   public void charger_imgRegles()
   {
      paintContinuer = false;
      try {
         fond = ImageIO.read(getClass().getResourceAsStream("img/fondRegles.png"));
      }
      catch(IOException e) { }
   }

   /**
    * Methode utilisé pour modifier la position de la bille
    * affiché au survol de la souris.
    * @param nb
    *       N° de l'option survolé.
    */
   public void afficheBille(int nb)
   {
      opt = nb;
   }

   /**
    * Affiche le menu principal. Initialise les zones et charge l'image de fond.
    */
   public void showMenu ()
   {
      setZeroRect();
      initRectMenu();
      charger_imgMenu();
      afficheBille(0);
      repaint();
   }
   /**
    * Affiche les Regles. Initialise les zones et charge l'image de fond.
    */
   public void showRegles ()
   {
      setZeroRect();
      initRectRegles();
      charger_imgRegles();
      afficheBille(0);
      repaint();
   }

   /**
    * Affiche une boite de dialogue qui montre des infos pouvant aider
    * l'utilisateur (touches, ...).
    */
   public void show_help ()
   {
      final String message = "          Aide blackBall [Menu]\n\n"
         + "         * Choix\n"
         + "\n"
         + "     Continuer  - Continue une partie précédemment sauvegardé. Ce choix\n"
         + "                    est caché s'il n'y a aucune partie sauvegardé.\n"
         + "Nouvelle partie - Permet d'aller à la fenêtre de selection des équipes\n"
         + "                    afin de commencer une nouvelle partie.\n"
         + "       Options  - Ouvre la fenêtre des options, pour modifier difficulté\n"
         + "                    de l'IA, le nombre de bandes minimum pour empochées la noire, ...\n"
         + "        Regles  - Permet de voir les règles du billard utilisé par ce jeu.\n"
         + "       Quitter  - Ferme toutes les fenêtres et quitte le jeu.\n"
         + "\n"
         + "Cliquer sur l'un des choix pour effectuer l'action décrite, ou utilisez\n"
         + " l'une des commandes ci-dessous.\n"
         + "\n"
         + "\n"
         + "         * Commande\n"
         + "\n"
         + "   ?  - Montre cette aide\n"
         + "   c  - Continuer.\n"
         + "   n  - Nouvelle partie (ou j)\n"
         + "   o  - Options\n"
         + "   r  - Regles\n"
         + "   q  - Quitter\n"
         + "\n";
      JOptionPane.showMessageDialog(this, message, "Aide blackBall [Menu]",
            0, cont.imgHelp);
   }
}
