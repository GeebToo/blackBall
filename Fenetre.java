import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

/**
 * Fenetre de jeu, contient la Table de billard et une zone Score.
 * Et des infos sur les dimensions de la fenetres.
 * 
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Fenetre extends JFrame implements Runnable
{
   /**
    * Table de billard, qui contient les billes, équipes, la queue,
    * le moteur, ...
    */
   public Table tablePan;
   /**
    * Panel qui affiche des infos sur les equipes (joueurs actuelles,
    * coup restant, billes empochées, ...).
    */
   public ScorePan scoreP;

   /**
    * Controller de l'application. Pour pouvoir changer de fenetre.
    */
   public Controller cont;
   /* dimension de la fenetre */
   /**
    * Largeur de la Fenetre.
    */
   public static final int WIDTH  = 800;
   /**
    * Hauteur de la Fenetre.
    */
   public static final int HEIGHT = 650;
   /**
    * Différence appliqué à l'abscisse de la souris.
    * Pour avoir la pointe de la souris.
    */
   public static final int D_X_SOURIS = 7;
   /**
    * Différence appliqué à l'ordonnée de la souris.
    * Pour avoir la pointe de la souris.
    */
   public static final int D_Y_SOURIS = 27;

   /**
    * Zone utilisé par <code>mouseClicked()</code> pour revenir au menu principal.
    */
   public Rectangle zoneMenu;
   /**
    * Délimite la zone qui permet d'afficher l'aide.
    */
   public Rectangle zoneHelp;

   /**
    * Point d'origine de la zoneMenu.
    */
   public static final Point ORI_ZONE_MENU = new Point(300, 0);
   /**
    * Dimension de la zoneMenu.
    */
   public static final Dimension DIM_ZONE_MENU = new Dimension(220, 60);

   /**
    * Titre de la Fenetre de jeu.
    */
   public static final String TITLE = "Black Ball - Jeu";

   /**
    * Construit une <code>Fenetre</code> de jeu qui contient
    * la table de billard et les scrores
    *
    * @param control
    *       Controller de l'application. Pour pouvoir changer de fenetre.
    */
   public Fenetre (Controller control)
   {
      this.setTitle(Fenetre.TITLE);
      this.setSize(Fenetre.WIDTH, Fenetre.HEIGHT);
      this.setLocationRelativeTo(null); /* pour centrer la fenetre */
      this.setBackground(new Color(233, 233, 233));
      //this.setAlwaysOnTop(true); // garder la fenetre au 1er plan
      this.cont = control;

      /* Panels */
      scoreP = new ScorePan();
      scoreP.setSize(new Dimension(WIDTH, (HEIGHT-50)/3));
      this.getContentPane().add(scoreP);
      this.initTable();

      if (Moteur.OPT_sound == 0) SoundEffect.init();

      /* Thread repaint() */
      Thread t = new Thread(this);
      t.setPriority(Thread.NORM_PRIORITY);
      t.start();

      /* zone pour mouseClicked() */
      zoneMenu = new Rectangle(Fenetre.ORI_ZONE_MENU, Fenetre.DIM_ZONE_MENU);
      zoneHelp = new Rectangle(10, 10, 40, 40);

      /* Listeners */
      addWindowListener(new WindowAdapter()
            {
               public void windowClosing(WindowEvent ev)
               {
                  Controller.exit(0);
               }
            } );
      addKeyListener (new KeyAdapter()
            {
               public void keyTyped (KeyEvent ev)
               {
                  final char keyC = ev.getKeyChar();
                  if (keyC == 'q')
                     cont.quit();
                  else if (keyC == 'i')
                     show_info();
                  else if (keyC == 'I')
                     show_infoIA();
                  else if (keyC == 'r')
                     rebootGame();
                  else if (keyC == '^')
                     cont.h.loadPrevTable(tablePan);
                  else if (keyC == '$')
                     cont.h.loadNextTable(tablePan);
                  else if (keyC == 'h')
                     getHelpIA();
                  else if (keyC == 's')
                     cont.h.saveToFile();
                  else if (keyC == 'm')
                     cont.retourMenu(Controller.FEN_JEU);
                  else if (keyC == '?')
                     show_help();
                  else if (keyC == 'p')
                     cont.setPause();
               }
            }
      );
      addMouseListener(new MouseAdapter()
            {
               public void mousePressed(MouseEvent ev)
               {
                  final int x = ev.getX() - Fenetre.D_X_SOURIS;
                  final int y = ev.getY() - Fenetre.D_Y_SOURIS;
                  if (!tablePan.m.bEnMvt)
                     if (tablePan.blancheBille.dansPoche)
                        verifRemiseEnJeuBlanche(x, y);
                     else /* si on est sur la boule blanche on peut afficher la trajectoire
                         puis la queue en fonction de la souris */
                        if ( (x >= tablePan.blancheBille.x - Bille.RAYON)
                           && (x <= tablePan.blancheBille.x + Bille.RAYON)
                           && (y  >= tablePan.blancheBille.y - Bille.RAYON)
                           && (y <= tablePan.blancheBille.y + Bille.RAYON)
                           && !tablePan.m.pauseJeu)
                              tablePan.readyPaint = true;
               }
               public void mouseReleased(MouseEvent ev)
               {
                  getTir(ev.getX() - Fenetre.D_X_SOURIS,
                     ev.getY() - Fenetre.D_Y_SOURIS);
               }
               public void mouseClicked(MouseEvent ev)
               {
                  final Point p = ev.getPoint();
                  if (zoneMenu.contains(p))
                     cont.retourMenu(Controller.FEN_JEU);
                  else if (zoneHelp.contains(p))
                     show_help();
               }
            } );
      addMouseMotionListener(new MouseMotionAdapter()
            {
               public void mouseMoved(MouseEvent ev)
               {
                  // if (tablePan.m.bBlancDansPoch)
                  if (tablePan.blancheBille.dansPoche)
                    tablePan.queue.setSouris(ev.getX() - Fenetre.D_X_SOURIS,
                                             ev.getY() - Fenetre.D_Y_SOURIS);
               }
               public void mouseDragged(MouseEvent ev)
               {
                   if (!tablePan.m.calIA)
                       tablePan.queue.setSouris(ev.getX() - Fenetre.D_X_SOURIS,
                                                ev.getY() - Fenetre.D_Y_SOURIS);
               }
            } );
   }

   /**
    * Démarre une partie. La table a besoin de 2 equipes avec au moins
    * un joueur. A ajouter avant avec <code>addEquipe()</code> de Table.
    */
   public void startGame ()
   {
      tablePan.m.firstHit = true;
      // this.revalidate();
      this.invalidate();
      this.validate();
      tablePan.m.debutCoup(true);
   }
   /**
    * Creation et Initialisisation d'une nouvelle Table.
    * Créer une nouvelle table et l'ajoute à la fenetre.
    */
   public void initTable ()
   {
      this.tablePan = new Table(false, cont);
      this.tablePan.setLayout(new FlowLayout());
      this.tablePan.setSize(new Dimension(Fenetre.WIDTH, (Fenetre.HEIGHT-50)*2/3));
      this.tablePan.setLocation(0, (Fenetre.HEIGHT-50)/3);
      this.getContentPane().add(tablePan);
      scoreP.addTable(tablePan);
   }

   /**
    * Relance une nouvelle partie.
    * Recreer une nouvelle Table.
    */
   public void rebootGame ()
   {
      if (tablePan.m.bEnMvt || tablePan.m.calIA) return;
      tablePan.initTable();
      startGame();
   }

   /**
    * Methode run() du Thread qui redessine les composants
    * de la fenetres.
    */
   public void run()
   {
      while (true)
      {
         try { Thread.sleep( 9, 1 ); }
         catch (InterruptedException ex) { }

         if (tablePan.m.pauseJeu) continue;
         super.repaint();
      }
   }

   /**
    * Affiche des infos sur les objects dans le terminal,
    * appelé si appuie sur 'i' pendant le jeu.
    */
   public void show_info()
   {
      System.out.println();
      System.out.println();
      System.out.println("{EquipeA}"+tablePan.eqA);
      System.out.println("{EquipeB}"+tablePan.eqB);
      System.out.println(tablePan.m);
      System.out.println(tablePan.queue);
      System.out.println(tablePan.blancheBille);
      System.out.println(tablePan.noireBille);
      System.out.println("  __ ROUGE __");
      for (int i = 0, c = tablePan.rougeBille.length; i < c; ++i)
         System.out.println("["+i+"]"+tablePan.rougeBille[i]);
      System.out.println("  __ JAUNE __");
      for (int i = 0, c = tablePan.jauneBille.length; i < c; ++i)
         System.out.println("["+i+"]"+tablePan.jauneBille[i]);
   }

   /**
    * Affiche des infos sur les objects de l'IA dans le terminal.
    */
   public void show_infoIA()
   {
      System.out.println();
      System.out.println("Info IA");
      System.out.println(tablePan.ia.tableIA.blancheBille);
      System.out.println(tablePan.ia.tableIA.noireBille);
      System.out.println("  __ ROUGE __");
      for (int i = 0, c = tablePan.ia.tableIA.rougeBille.length; i < c; ++i)
         System.out.println("["+i+"]"+tablePan.ia.tableIA.rougeBille[i]);
      System.out.println("  __ JAUNE __");
      for (int i = 0, c = tablePan.ia.tableIA.jauneBille.length; i < c; ++i)
         System.out.println("["+i+"]"+tablePan.ia.tableIA.jauneBille[i]);
   }

   /**
    * Verifie si la bille blanche qui vient d'être empochée
    * peut être remis en jeux à cette endroit
    * @param x
    *       Abscisse de la nouvelle position de la bille blanche
    * @param y
    *       Ordonnée de la nouvelle position de la bille blanche
    */
   public void verifRemiseEnJeuBlanche (int x, int y)
   {
      /* On verifie la position, si incorect, on arrete */
      if (tablePan.queue.coteTriangleCasse == Queue.TRI_COTE_G)
      {
         if (!verifRemiseEnJeuBlancheD(x, y)) return;
      }
      else if (tablePan.queue.coteTriangleCasse == Queue.TRI_COTE_D)
         if (!verifRemiseEnJeuBlancheG(x, y)) return;

      /* si nouvelle position correcte */
      tablePan.blancheBille.setCoord(x, y);
      tablePan.blancheBille.dansPoche = false;
      // tablePan.m.bBlancDansPoch = false;
   }

   /**
    * Verifie si la Bille Blanche empochée peut être replacée à droite.
    * @param x
    *       Abscisse de la nouvelle position de la bille blanche
    * @param y
    *       Ordonnée de la nouvelle position de la bille blanche
    */
   public boolean verifRemiseEnJeuBlancheD (int x, int y)
   {
      /* Lorsque coteTriangleCasse = Gauche */
      if (((x>=(Table.WIDTH/4)*3)
              && (x <= Table.ORI_X_T + Table.WIDTH_T - Bille.RAYON)
              && (y >= Table.ORI_Y_T + Bille.RAYON) 
              && (y <= (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON)))
              && (!tablePan.verifCollision(x, y)))
         return true;
      else return false;
   }

   /**
    * Verifie si la Bille Blanche empochée peut être replacée à gauche.
    * @param x
    *       Abscisse de la nouvelle position de la bille blanche
    * @param y
    *       Ordonnée de la nouvelle position de la bille blanche
    */
   public boolean verifRemiseEnJeuBlancheG (int x, int y)
   {
      /* Lorsque coteTriangleCasse = Droite */
      if (((x<=(Table.WIDTH/4))
               && (x >= Table.ORI_X_T + Bille.RAYON)
              && (y >= Table.ORI_Y_T + Bille.RAYON) 
              && (y <= (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON)))
              && (!tablePan.verifCollision(x, y)))
         return true;
      else return false;
   }

   /**
    * Demande l'aide de l'IA avant d'effectuer un coup.
    * Affiche la trajectoire d'un des meilleurs coup d'apres l'IA. Desactive
    * l'aide si elle a déjà été activé. Méthode appelé lorsqu'un joueur appuie sur
    * la touche ' h '.
    */
   public void getHelpIA ()
   {
      /* Aide indisponible lorsque les billes bougent */
      if (!tablePan.m.bEnMvt && !tablePan.m.calIA)
         if (tablePan.help) /* Si l'aide est deja activé, on la desactive */
            tablePan.help = false;
         else
         {
            tablePan.ia.calculMeilleureCoup(); /* demande à l'ia le meilleur coup */
            tablePan.help = true;
         }
   }
   /**
    * Affiche une boite de dialogue qui montre des infos pouvant aider
    * l'utilisateur (touches, ...).
    */
   public void show_help ()
   {
      final String message = "          Aide blackBall [Fenetre de jeu]\n\n"
         + " ?  -   Affiche cette aide.\n"
         + " p  -   Met en pause le jeu.\n"
         + " q  -   Quitte le jeu.\n"
         + " r  -   Recommence la partie.\n"
         + " s  -   Sauvegarde la partie.\n"
         + " m  -   Retourne au Menu principal\n"
         + " h  -  Demande l'aide l'IA.\n"
         + "      (Affiche la trajectoire du meilleur coup).\n"
         + " ^  -   Aller en arrière dans l'historique de la table\n"
         + " $  -   Aller en avant dans l'historique de la table\n";
      JOptionPane.showMessageDialog(this, message, "Aide blackBall [Fenetre de jeu]",
            0, cont.imgHelp);
   }

   /**
    * Methode appelé lorsqu'un joueur humain a effectué un tir.
    * @param x
    *       Abscisse de la souris.
    * @param y
    *       Ordonnée de la souris.
    */
   public void getTir (int x, int y)
   {
      if (!tablePan.readyPaint) return;
      /*Calcule la force en fonction de la taille de la queue*/
      final double tmp = Math.sqrt(Math.pow((x - tablePan.queue.bBlanche.x), 2)
            + Math.pow((y - tablePan.queue.bBlanche.y), 2));
      double f = Math.pow(tmp, 2) / 25;

      if ( f > Moteur.FORCE_MAX) f = Moteur.FORCE_MAX;
      tablePan.m.bougerBilleBlanche(
            (int) tablePan.m.symCentrale((float)x, tablePan.queue.bBlanche.x),
            (int) tablePan.m.symCentrale((float)y, tablePan.queue.bBlanche.y),
            (int) f);
      if (Moteur.OPT_sound == 0) SoundEffect.QUE.play();
      tablePan.readyPaint = false;/*arrete d'afficher la trajectoire*/
   }
}
