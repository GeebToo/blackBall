import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * class Table : contient les billes, les deux équipes, la queue,
 * le moteur de jeu et les poches. Initialise et affiche les differents
 * éléments de la table.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Table extends JPanel
{
   /**
    * Nombre de billes par tableau de bille colorés.
    * Par defaut 7.
    */
   public final int NB_BILLE = 7;

   /* Billes */
   /**
    * Tableau de bille rouges.
    */
   public Bille[] rougeBille;
   /**
    * Tableau de bille jaunes.
    */
   public Bille[] jauneBille;
   /**
    * La bille noire.
    */
   public Bille noireBille;
   /**
    * La bille blanche. Cible de la queue.
    */
   public Bille blancheBille;

   /**
    * Thread qui déplace les billes.
    */
   public Thread thMoteur;

   /**
    * L'Equipe A.
    */
   public Equipe eqA;
   /**
    * L'Equipe B.
    */
   public Equipe eqB;

   /**
    * Intelligence Artificielle, qui contient sa propre table pour
    * faire ses tests.
    */
   public IA ia;

   /**
    * Boolean qui sert à afficher la trajectoire d'aide.
    */
   public boolean help = false;

   /**
    * Boolean qui sert à afficher ou non la trajectoire et la queue.
    */
   public boolean readyPaint = false;

   /**
    * La queue. Ligne qui va de la souris vers la bille blanche.
    */
   public Queue queue;

   /**
    * La moteur du jeu. Contient un thread qui deplace les billes
    * et des infos sur le coup précédent.
    */
   public Moteur m;

   /**
    * Les six Poches. Quand une bille est empochées,
    * permet de la déplacer dans la zone de score.
    */
   public Poches poch;

   /**
    * Le Controller du jeu. Afin de sauvegarder chaque coup.
    */
   public Controller cont;

   static final float ORI_X = 0.0f;
   static final float ORI_Y = 200.0f;
   static final float WIDTH = 800.0f;
   static final float HEIGHT = 400.0f;
   /* Taille réèl de la table */
   static final float ORI_X_T = 30.0f;
   static final float ORI_Y_T = 230.0f;
   static final float WIDTH_T = 740.0f;
   static final float HEIGHT_T = 340.0f;

   /**
    * Image de fond de la table.
    */
   public Image tableFond;

   /**
    * Construit une <code>Table</code>.
    * Inititialise les differents éléménts qui en font parti
    * (Billes, queue, poches, ...)
    *
    * @param IA
    *       True si c'est la table de l'IA.
    *       False si c'est la table qui est affiché, et donc
    *       crée une IA.
    * @param control
    *       Controller de l'application.
    */
   public Table (boolean IA, Controller control)
   {
      this.setBackground(new Color(233, 233, 233));
      this.cont = control;

      /* Creation des Billes */
      this.createBilles();
      /* Initialisation Queue */
      this.queue = new Queue(blancheBille, this);
      /* Initialisation Poches */
      this.poch = new Poches();
      /* Initialisation Moteur */
      this.m = new Moteur(this);

      /* Image de fond */
      try
      {
         tableFond = ImageIO.read(getClass().getResourceAsStream("img/fondTable.png"));
      }
      catch(IOException e) { }

      if (!IA)
      {
         this.ia = new IA (m);
         /* Thread moteur */
         thMoteur = new Thread(m);
         thMoteur.setPriority(Thread.NORM_PRIORITY);
         thMoteur.start();
      }
   }

   /**
    * Methode d'affichage du panel. Appel les méthodes
    * d'affiches des composants de la table.
    *
    * @param g
    *       Contexte graphique.
    */
   public void paintComponent (Graphics g)
   {
      super.paintComponent(g);

      /* Affichage de la table */
      //paintTable(g);
      g.drawImage(tableFond, 0, 0, this);


      /* Affichage des billes */
      this.paintBillesEnJeu(g);

      /* Affichage de la queue */
      queue.paint(g);

      /* Affichage de la trajectoire */
      this.paintTrajectoire(g);
   }

   /**
    * Affiche les billes qui sont encore en jeu. Affiche toutes les
    * billes sauf celles empochées qui sont affiché par ScorePan.
    *
    * @param g
    *       Contexte graphique.
    */
   public void paintBillesEnJeu (Graphics g)
   {
      for (int i = 0, c = NB_BILLE; i < c; ++i)
      {
         if (!rougeBille[i].dansPoche)
            rougeBille[i].paint(g);
         if (!jauneBille[i].dansPoche)
            jauneBille[i].paint(g);
      }
      if (!blancheBille.dansPoche)
         blancheBille.paint(g);
      if (!noireBille.dansPoche)
         noireBille.paint(g);
   }

   /**
    * Methode d'affichage de la trajectoire.
    * Si la bille blanche ne bouge pas, n'est pas empochées et l'utilisateur a cliqué
    * affiche la trajectoire.
    *
    * @param g
    *       Contexte graphique.
    */
   public void paintTrajectoire (Graphics g)
   {
      if (readyPaint || m.calIA)
         m.paintTrajectoire(g);
      if(help)
         m.paintTrajectoireHelp(g);
   }

   /**
    * Ancienne méthode d'affichage de la table.
    * Utilisé avant d'avoir l'image de fond.
    *
    * @param g
    *       Contexte graphique.
    */
   public void paintTable (Graphics g)
   {
      /* Affichage de la table */
      /* Bordure */
      g.setColor(new Color(102, 51, 0)); /* marron */
      g.fillRect((int) Table.ORI_X, (int) Table.ORI_Y,
            (int) Table.WIDTH, (int) Table.HEIGHT);
      /* Interieur de la table */
      g.setColor(new Color(34, 139, 34)); /* vert */
      g.fillRect((int) Table.ORI_X_T, (int) Table.ORI_Y_T,
            (int) Table.WIDTH_T, (int) Table.HEIGHT_T);
      /* Affichage des Poches */
      this.poch.paint(g);
   }

   /**
    * Verifie que la point de coord (x,y) est bien sur la table.
    *
    * @param x
    *       Abscisse du Point.
    * @param y
    *       Ordonnée du Point.
    * @return true - le point est sur la table.
    * @return false - le point est en dehors.
    */
   public boolean inTable(float x, float y)
   {
      if (x < Table.ORI_X_T || x > (Table.ORI_X_T + Table.WIDTH_T))
         return false;
      if (y < Table.ORI_Y_T || y > (Table.ORI_Y_T + Table.HEIGHT_T))
         return false;
      else
         return true;
   }

   /**
    * Creation des billes. Créer toutes les billes et initialise
    * leur couleur.
    */
   public void createBilles ()
   {
      /* initialisation des billes */
      rougeBille = new Bille[NB_BILLE];
      jauneBille = new Bille[NB_BILLE];
      for (int i = 0, c = NB_BILLE; i < c; ++i)
      {
         rougeBille[i] = new Bille(Color.RED);
         jauneBille[i] = new Bille(Color.YELLOW);
      }
      blancheBille = new Bille(Color.WHITE);
      noireBille = new Bille(Color.BLACK);
   }

   /**
    * Inititialise les billes en les placant à un endroit
    * choisit aléatoirement sur la table.
    */
   public void initBillesRand ()
   {
      queue.coteTriangleCasse = 0;
      /* Billes placées aléatoirement sur la table */
      for (int i = 0, c = NB_BILLE; i < c; ++i)
      { 
         rougeBille[i].initB(
               (float) Math.round(Table.ORI_X_T+10+(Math.random() * (Table.WIDTH_T-20))),
               (float) Math.round(Table.ORI_Y_T+10+(Math.random() * (Table.HEIGHT_T-20)))
               );
         jauneBille[i].initB(
               (float) Math.round(Table.ORI_X_T+10+(Math.random() * (Table.WIDTH_T-20))),
               (float) Math.round(Table.ORI_Y_T+10+(Math.random() * (Table.HEIGHT_T-20)))
               );
      }
      blancheBille.initB(
            (float) Math.round(Table.ORI_X_T+10+(Math.random() * (Table.WIDTH_T-20))),
            (float) Math.round(Table.ORI_Y_T+10+(Math.random() * (Table.HEIGHT_T-20)))
            );
      noireBille.initB(
            (float) Math.round(Table.ORI_X_T+10+(Math.random() * (Table.WIDTH_T-20))),
            (float) Math.round(Table.ORI_Y_T+10+(Math.random() * (Table.HEIGHT_T-20)))
            );
   }

   /**
    * Initialise les billes en les placant à la même position
    * que les billes qui se trouvent dans la table fourni en argument.
    *
    * @param t
    *       Table de jeu dont la position des billes va être copié
    */
   public void initBillesIA (Table t)
   {
      /* initialisation des billes */
      rougeBille = new Bille[NB_BILLE];
      jauneBille = new Bille[NB_BILLE];

      /* Billes placées aléatoirement sur la table */
      for (int i = 0, c = NB_BILLE; i < c; ++i)
      { 
         rougeBille[i] = t.rougeBille[i].clone();
         jauneBille[i] = t.jauneBille[i].clone();
      }
      blancheBille = t.blancheBille.clone();
      noireBille = t.noireBille.clone();
   }

   /**
    * Inititialise les billes en les placant
    * sous la forme d'un triangle à gauche et la bille blanche en face.
    */
   public void initBillesG ()
   {
      queue.coteTriangleCasse = Queue.TRI_COTE_G;
      /* Billes rouges */
      rougeBille[0].initB(ORI_X_T + (WIDTH_T/4), ORI_Y_T + (HEIGHT_T/2));
      rougeBille[1].initB(ORI_X_T + (WIDTH_T/4) - (Bille.RAYON*2),
            ORI_Y_T + (HEIGHT_T/2) + Bille.RAYON);
      rougeBille[2].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*2),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*2));
      rougeBille[3].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON));
      rougeBille[4].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*3));
      rougeBille[5].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*2));
      rougeBille[6].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*4));

      /* Billes jaunes */
      jauneBille[0].initB(ORI_X_T + (WIDTH_T/4) - (Bille.RAYON*2),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON));
      jauneBille[1].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*2),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*2));
      jauneBille[2].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON));
      jauneBille[3].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*3));
      jauneBille[4] .initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*2));
      jauneBille[5].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2));
      jauneBille[6].initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*4));

      /* Bille noire */
      noireBille.initB(ORI_X_T + (WIDTH_T/4) - ((Bille.RAYON*2)*2),
            ORI_Y_T + (HEIGHT_T/2));
      /* Bille blanche */
      blancheBille.initB(ORI_X_T + ((WIDTH_T/4)*3), ORI_Y_T + (HEIGHT_T/2));
   }
   /**
    * Inititialise les billes en les placant
    * sous la forme d'un triangle à droite et la bille blanche en face.
    */
   public void initBillesD ()
   {
      queue.coteTriangleCasse = Queue.TRI_COTE_D;
      /* Billes rouges */
      rougeBille[0].initB(ORI_X_T + ((WIDTH_T/4)*3), ORI_Y_T + (HEIGHT_T/2));
      rougeBille[1].initB(ORI_X_T + ((WIDTH_T/4)*3) + (Bille.RAYON*2),
            ORI_Y_T + (HEIGHT_T/2) - Bille.RAYON);
      rougeBille[2].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*2),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*2));
      rougeBille[3].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) + Bille.RAYON);
      rougeBille[4].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*3));
      rougeBille[5].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*2));
      rougeBille[6].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*4));

      /* Billes jaunes */
      jauneBille[0].initB(ORI_X_T + ((WIDTH_T/4)*3) + (Bille.RAYON*2),
            ORI_Y_T + (HEIGHT_T/2) + Bille.RAYON);
      jauneBille[1].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*2),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*2));
      jauneBille[2].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON));
      jauneBille[3].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*3),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*3));
      jauneBille[4].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) + (Bille.RAYON*2));
      jauneBille[5].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2));
      jauneBille[6].initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*4),
            ORI_Y_T + (HEIGHT_T/2) - (Bille.RAYON*4));

      /* Bille noire */
      noireBille.initB(ORI_X_T + ((WIDTH_T/4)*3) + ((Bille.RAYON*2)*2),
            ORI_Y_T + (HEIGHT_T/2));
      /* Bille blanche */
      blancheBille.initB(ORI_X_T + ((WIDTH_T/4)), ORI_Y_T + (HEIGHT_T/2));
   }

   /**
    * Ajoute deux équipes à la partie.
    *
    * @param eqA
    *       Equipe A.
    * @param eqB
    *       Equipe B.
    */
   public void addEquipe (Equipe eqA, Equipe eqB)
   {
      /* Initialiasation Equipe */
      this.eqA = eqA;
      this.eqB = eqB;
      this.eqA.addEqAdv(this.eqB);
      this.eqB.addEqAdv(this.eqA);
      /* Initialise couleur des equipes. */
      eqA.initC();
      eqB.initC();

      /* Choisir equipe qui commence (aléatoire) */
      if (ia.rand.nextBoolean())
         this.m.eq = eqA;
      else
         this.m.eq = eqB;
      this.m.eq.j[m.eq.curr].nbCoupRestant = 1;
   }


   /**
    * Verifie si le centre de la bille donnée en parametre
    * est en collision avec l'une des billes qui se trouvent sur la table.
    * Utilisé pour la rmise en jeu de la bille blanche.
    *
    * @param x
    *       Abscisse du centre de la bille.
    * @param y
    *       Ordonnée du centre de la bille.
    * @return true si il y a collision
    */
   public boolean verifCollision (float x, float y)
   {
      /* Billes rouges */
      for (int j = 0, c = rougeBille.length; j < c; ++j)
         if (rougeBille[j].isOverlap(x, y))
            if (rougeBille[j].isCollision(x, y))
               return true;

      /* Billes jaunes */
      for (int j = 0, c = jauneBille.length; j < c; ++j)
         if (jauneBille[j].isOverlap(x, y))
            if (jauneBille[j].isCollision(x, y))
               return true;

      /* Bille noire */
      if (noireBille.isOverlap(x, y))
         if (noireBille.isCollision(x, y))
            return true;

      /* Bille blanche */
      if (blancheBille.isOverlap(x, y))
         if (blancheBille.isCollision(x, y))
            return true;

      return false;
   }

   /**
    * Permet de savoir quelle equipe joue. Utilisé pour afficher
    * le nombre de coup restant a jouer.
    *
    * @return true si c'est au tour de l'équipe A.
    */
   public boolean isEqAPlaying ()
   {
      if (m.eq == eqA) return true;
      return false;
   }

   /**
    * Renvoie le nombre de coup qui reste au joueur actuelle.
    * Utilisé pour afficher le nombre de coup restant à jouer.
    *
    * @return Integer representant le nombre de coup qu'il reste
    * au joueur actuelle.
    */
   public int getNbCoupRestant ()
   {
      return m.eq.j[m.eq.curr].nbCoupRestant;
   }

   /**
    * Initialise la table pour une nouvelle partie. Init la
    * couleurs des equipes, et la position de la derniere bille
    * empoché. Place les billes en triangle du coté choixie
    * en option.
    */
   public void initTable ()
   {
      eqA.initC();
      eqB.initC();
      poch.initLastBDansPoche();
      initTriangleBilles();
      m.firstHit = true;
   }

   /**
    * Initialise les billes en triangle, change le coté du triangle
    * en fonction de l'option choisie. Methode appelé quand une partie
    * est recommencé et à la fin d'une partie
    */
   public void initTriangleBilles()
   {
      if (m.OPT_changeCote == 1)       // changement de cote
         if (queue.coteTriangleCasse == Queue.TRI_COTE_G)
            initBillesD();
         else
            initBillesG();
      else if (m.OPT_changeCote == 0)  // Pas de changement de cote
         if (queue.coteTriangleCasse == Queue.TRI_COTE_G)
            initBillesG();
         else
            initBillesD();
      else                             // aleatoire
         if (ia.rand.nextBoolean())
            initBillesD();
         else
            initBillesG();
   }

   /**
    * Methode appelé a la fin d'une partie, lorsque la bille noire
    * a été empochée. Prepare la table pour la revanche.
    * @param e
    *       Equipe qui a empochée la noire.
    * @param estPerdante
    *       cette equipe a-t-elle perdu la partie en l'empochant.
    * @param noireDansPoch
    *       true si la noire a été empoché lors de ce coup.
    */
   public void endGame (Equipe e, boolean estPerdante, boolean noireDansPoch)
   {
      /* Verif si il faut empoché la blanche avant de terminer la partie.
       * Depend de l'option si la noire vient d'etre empoché par cette equipe
       * et qu'elle n'a pas perdu la partie.
       */
      if (!estPerdante && noireDansPoch && Moteur.OPT_EmpocheBlancheApresNoire > 0)
         if (Moteur.OPT_EmpocheBlancheApresNoire == 1)
         {
            final String plur = (Moteur.OPT_nbRebBilleBlanche > 1) ? "s" : " ";
            cont.fenJeu.scoreP.showMessage("Il reste à empocher la bille blanche en "
                  + Moteur.OPT_nbRebBilleBlanche + " coup" + plur,
                  ScorePan.DELAY_MSG_INFO);
            return;
         }
         else if (Moteur.OPT_EmpocheBlancheApresNoire == 2
               && e.c.equals(Color.BLACK) && e.getEqAdv().c.equals(Color.BLACK))
         {
            final String plur = (Moteur.OPT_nbRebBilleBlanche > 1) ? "s" : " ";
            cont.fenJeu.scoreP.showMessage("Il reste à empocher la bille blanche en "
                  + Moteur.OPT_nbRebBilleBlanche + " coup" + plur,
                  ScorePan.DELAY_MSG_INFO);
            return;
         }

      /* Fini le tour du joueur qui vient d'empocher la noire */
      e.j[e.curr].nbCoupRestant = 0;
      e.nextJ();
      if (estPerdante) e.getEqAdv().nbVictoire++;
      else e.nbVictoire++;

      /* Selectionne le prochain joueur */
      if (Moteur.OPT_EqQuiCommencePartie == 1)
         if (estPerdante) // alors c'est a cette equipe de commencer
            m.eq = e;
         else
            m.eq = e.getEqAdv();
      else if (Moteur.OPT_EqQuiCommencePartie == 2)
         if (!estPerdante) // alors c'est a cette equipe de commencer
            m.eq = e;
         else
            m.eq = e.getEqAdv();
      else /* Aleatoire */
         if (ia.rand.nextBoolean())
            m.eq = e;
         else
            m.eq = e.getEqAdv();

      initTable();
      // super.revalidate();
      super.invalidate();
      super.validate();

      /* Message de fin de partie */
      final String s_eq = (e == eqA)?"A":"B"; 
      final String s_res = (estPerdante)?"perdu":"gagné";
      final String message = "L'Equipe " +s_eq + " a " + s_res + " la partie";
      cont.fenJeu.scoreP.showMessage(message, ScorePan.DELAY_MSG_VIC);

      /* Ensuite apres la fin de plusieurs methodes,
       * debutCoup() est appelé pour commencé la nouvelle partie */
   }
}
