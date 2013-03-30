import java.awt.Image;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

/**
 * Controller qui s'occupe de creer les fenetres et de les afficher
 * quand il le faut. Verifie au lancement de l'application s'il y a une
 * sauvegarde presente. Et lance les parties.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Controller
{
   /**
    * Constante représentant la fenetre de jeu. Utilisé pour savoir
    * la fenetre actuellement affiché.
    */
   public static final int FEN_JEU = 1;
   /**
    * Constante représentant la fenetre de Selection des joueurs. Utilisé pour savoir
    * la fenetre actuellement affiché.
    */
   public static final int FEN_SELECT_J = 2;
   /**
    * Constante représentant la fenetre de modif des options. Utilisé pour savoir
    * la fenetre actuellement affiché.
    */
   public static final int FEN_OPT = 3;
   /**
    * Menu principal du jeu.
    */
   public Menu fenMenu;
   /**
    * Fenetre du jeu qui contient la table.
    */
   public Fenetre fenJeu;
   /**
    * Fenetre qui permet la selection des joueurs composant les deux équipes.
    */
   public SelectionEquipeFrame fenSelectJ;
   /**
    * Fenetre qui permet de modifier les options du jeu.
    */
   public OptionsFrame fenOptions;

   /**
    * Attribut a true si une sauvegarde a été trouvé.
    * Propose alors la possibilité de la reprendre dans le menu principal.
    */
   public boolean gameSaved;

   /**
    * Attribut a true si une partie est en cours.
    */
   public boolean partieEnCours;

   /**
    * Historique de la partie. Vide au debut si aucune sauvegarde n'a été trouvé.
    * Sinon contient la derniere sauvegarde. Remis a 0, lors d'une nouvelle partie
    * apres la selection des parties.
    */
   public History h;

   /**
    * Icone des fenetres. Utilisé par createFrames()
    */
   public Image iconFen;
   /**
    * Icone d'aide. Image affiché sur chaque fenetre pour afficher l'aide,
    * et dans chaque boite de dialogue d'aide.
    */
   public ImageIcon imgHelp;

   /**
    * Constructeur du Controller. Creer un nouvelle historique puis recherche
    * une sauvegarde.
    */
   public Controller ()
   {
      this.h = new History();
      verifSave();
   }

   /**
    * Créer toutes les fenetres composant l'application. Et ajoute l'icone.
    */
   public void createFrames ()
   {
      /* Creation des fenetres */
      fenMenu = new Menu(this);
      fenJeu = new Fenetre(this);
      fenSelectJ = new SelectionEquipeFrame(this);
      fenOptions = new OptionsFrame(this);

      try
      {
         /* Chargement de l'icone */
         iconFen = ImageIO.read(getClass().getResourceAsStream("img/iconBlackBall.png"));
         /* Ajout de l'icone */
         fenMenu.setIconImage(iconFen);
         fenJeu.setIconImage(iconFen);
         fenSelectJ.setIconImage(iconFen);
         fenOptions.setIconImage(iconFen);

         /* Chargement Image d'aide */
         imgHelp = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("img/imgAide.png")));
      }
      catch(IOException e) { }
   }

   /**
    * Premiere méthode appelé apres la creation des fenetres, afin
    * d'afficher le menu principal.
    */
   public void showMenu ()
   {
      fenMenu.setVisible(true);
      fenMenu.showMenu();
   }

   /**
    * Affiche le menu de selection des équipes à la place du menu
    * principal. Appelé lorsque le joueur decide de démarrer une nouvelle partie.
    */
   public void showSelectJ ()
   {
      fenMenu.setVisible(false);
      fenSelectJ.setVisible(true);
   }

   /**
    * Affiche le menu d'options à la place du menu principal.
    */
   public void showOptions ()
   {
      fenMenu.setVisible(false);
      fenOptions.setVisible(true);
   }

   /**
    * Quitte le programme.
    */
   public void quit()
   {
      fenJeu.setVisible(false);
      fenMenu.setVisible(false);
      fenSelectJ.setVisible(false);
      Controller.exit(0);
   }
   /**
    * Quitte le programme. Methode appelé par <code>quit()</code>.
    * @param codeRetour
    *       Nombre renvoyé par le programme lors de la sortie (0 si tout va bien).
    */
   public static void exit(int codeRetour)
   {
      System.out.println("Au revoir...");
      System.exit(codeRetour);
   }

   /**
    * Reprendre une partie sauvegardé. Si une sauvegarde a été trouvé,
    * méthode appelé lorsque le joueur clique sur continuer, charge la
    * table dans l'état sauvegardé et reprend la partie.
    * Affiche la fenetre de jeu à la place du menu principal.
    */
   public void resumeGame ()
   {
      if (!gameSaved && !partieEnCours) return; // Si aucune partie sauvegardé, on arrete
      fenMenu.setVisible(false);
      fenJeu.setVisible(true);
      if (gameSaved && !partieEnCours)
      {
         h.loadTable(fenJeu.tablePan, h.h.get(h.curr));
         fenJeu.tablePan.m.debutCoup(false);
      }
      else
         setPause(false);
   }

   /**
    * Commence une nouvelle partie avec les equipes séléctionner dans
    * la fenetre <code>fenSelectJ</code>. Methode appelé lorsque le
    * joueur lance une nouvelle partie apres avoir choisie les équipes.
    * Efface l'historique et en commence un nouveau.
    */
   public void startNewGame ()
   {
      fenJeu.tablePan.addEquipe(fenSelectJ.sPan.getEqA(),
            fenSelectJ.sPan.getEqB());
      fenJeu.tablePan.poch.initLastBDansPoche();

      /* Choix du coté du triangle */
      if (Moteur.OPT_coteDeb == 0)
         fenJeu.tablePan.initBillesG();
      else if (Moteur.OPT_coteDeb == 1)
         fenJeu.tablePan.initBillesD();
      else
         if (fenJeu.tablePan.ia.rand.nextBoolean())
            fenJeu.tablePan.initBillesD();
         else
            fenJeu.tablePan.initBillesG();

      fenSelectJ.setVisible(false);
      fenJeu.setVisible(true);
      setPause(false);

      if (gameSaved) h = new History(); // on écrase l'ancienne sauvegarde
      fenJeu.startGame();
      gameSaved = true;
   }

   /**
    * Verif si une sauvegarde existe dans le fichier. Si oui la charge dans
    * l'historique pour que le joueuer puisse reprendre sa partie.
    */
   public void verifSave ()
   {
      final File f = new File("blackBall.save");
      final History hNew;
      if (f.exists()) // verif si fichier existe
      {
         hNew = h.loadFromFile(); // charge le fichier
         if (hNew != null) // si pas d'erreur lors de la lecture
         {
            gameSaved = true;
            h = hNew;
            h.loadOptions();
         }
         else
            gameSaved = false;
      }
      else
         gameSaved = false;
   }

   /**
    * Retourne au menu principal. Cache la fenetre actuelle, puis montre
    * le affiche le menu principal.
    * @param currFen
    *       Fenetre actuelle affiché. Utilisé une des constantes du Controller.
    */
   public void retourMenu (int currFen)
   {
      if (currFen == Controller.FEN_SELECT_J)
         fenSelectJ.setVisible(false);
      else if (currFen == Controller.FEN_JEU)
      {
         partieEnCours = true;
         if (!fenJeu.tablePan.m.bEnMvt)
            h.saveTable(fenJeu.tablePan);
         setPause(true);
         fenJeu.setVisible(false);
      }
      else if (currFen == Controller.FEN_OPT)
         fenOptions.setVisible(false);
      this.showMenu();
   }

   /**
    * Met le jeu en pause ou enleve la pause en fonction de l'etat actuelle.
    * La pause met les threads en sommeil.
    */
   public void setPause ()
   {
      if (fenJeu.tablePan.m.pauseJeu)
      {
         fenJeu.tablePan.m.pauseJeu = false;
         fenJeu.scoreP.showMessage("Fin de la pause", ScorePan.DELAY_MSG_FAUTE);
      }
      else
      {
         fenJeu.tablePan.m.pauseJeu = true;
         fenJeu.scoreP.showMessage("Pause... Pour reprendre : p", 0);
      }
   }

   /**
    * Met le jeu en pause ou enleve la pause en fonction du parametre.
    * La pause met les threads en sommeil.
    */
   public void setPause (boolean activePause)
   {
      if (!activePause)
      {
         fenJeu.tablePan.m.pauseJeu = false;
         fenJeu.scoreP.showMessage("Fin de la pause", ScorePan.DELAY_MSG_FAUTE);
      }
      else
      {
         fenJeu.tablePan.m.pauseJeu = true;
         fenJeu.scoreP.showMessage("Pause... Pour reprendre : p", 0);
      }
   }
}
