import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

/**
 * Moteur du jeu qui contient des methodes utilisées par le calcul
 * de la trajectoire, et Thread qui verifie si des billes sont en mouvement
 * et si oui, les déplacent
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Moteur implements Runnable
{
   /**
    * Force maximum qui puisse étre appliqué à une bille.
    */
   public final static int FORCE_MAX = 4000,
                           PIXEL_MAX = 10;

    public final static float FORCE_MIN = 1000;

   /* Options du jeu */
   /**
    * Difficulté de l'IA.
    * 1 - facile.
    * 2 - moyen.
    * 3 - difficile.
    * plus le niveau de l'IA est élevé, moins elle commet d'erreur.
    * Option du jeu.
    */
   public static int OPT_lvlIA = 3;

   /**
    * Quelle est le coté du triangle au début d'une nouvelle
    * partie. Utilisé par <code>startNewGame()</code> du Controller.
    * 0 - Le triangle est à gauche.
    * 1 - le Triangle est à droite.
    * 2 - Coté choisie aléatoirement.
    */
   public static int OPT_coteDeb = 2;

   /**
    * Permet de choisir quelle equipe commence une partie.
    * 1 - le perdant de la precedente.
    * 2 - la gagnant de la precedente.
    * 0 - Aleatoire.
    * La premiere partie est toujours commencé par l'équipe A.
    * Option du jeu.
    */
   public static int OPT_EqQuiCommencePartie = 1;
   /**
    * Nombre minimum de rebond (bandes) qu'il faut faire pour
    * empoché la noire sans commetre de faute à la fin d'une partie.
    */
   public static int OPT_nbRebBilleNoire = 3;

   /**
    * Faut-il empocher la bille blanche après la noire pour remporter
    * la victoire.
    * 0 - jamais.
    * 1 - toujours.
    * 2 - seulement si les deux équipes ont empochées toutes leurs billes de couleurs
    */
   public static int OPT_EmpocheBlancheApresNoire = 0;

   /**
    * Nombre minimum de rebond (bandes) qu'il faut faire pour
    * empoché la blanche sans commetre de faute à la fin d'une partie,
    * si l'option a été activé.
    */
   public static int OPT_nbRebBilleBlanche = 1;

   /**
    * Changer de coté à chaque partie. Utilisé par
    * <code>initTriangleBilles()</code> de Table.
    * 0 - non, on commence toujours du même coté.
    * 1 - oui, on change de coté a chaque à chaque partie.
    * 2 - change ou pas, choix aléatoire à chaque partie.
    */
   public static int OPT_changeCote = 1;

   /**
    * Option pour l'affichage de la trajectoire. Nombre
    * de rebond de la bille blanche affichée.
    */
   public static int OPT_nbRebond = 3;
   /**
    * Option pour l'affichage de la trajectoire. Nombre
    * de rebond des billes entré en collision avec la bille blanche.
    */
   public static int OPT_nbRebBilTouche = 1;

   /**
    * Options pour desactiver sauvegarde automatique.
    * 0 - activer
    * 1 - desactiver
    */
   public static int OPT_save = 0;

   /**
    * Options pour desactiver le son.
    * 0 - activer
    * 1 - desactiver
    */
   public static int OPT_sound = 0;

   /**
    * Table de jeu qui contient les Billes, les équipes, ...
    */
   public Table t;

   /* Information a propos du dernier coup
    * (ou celui en cours si les billes bougent) */
   /**
    * Equipe qui joue pendant ce coup.
    */
   public Equipe eq;
   /**
    * Premiere bille touchée par la bille blanche lors du dernier coup.
    */
   public Bille firstBTouche;


   /**
    * FIFO: list de billes empochées lors du dernier coup.
    */
   public LinkedList<Bille> listBDansPoch;

   /**
    * Nombre de billes empochées lors du dernier coup.
    * remis à 0 au debut d'un nouveau coup.
    */
   public int nbBDansPoch;

   /**
    * Nombre de bande(s) touchée(s) (de rebond) lors du dernier coup.
    * Remis à 0 au debut d'un nouveau coup.
    */
   public int nbRebond;


   /**
    * boolean qui indique si c'est le premier coup.
    */
   public boolean firstHit;
   /**
    * Les billes sont-elles en mouvement.
    * false quand aucune bille ne bouge.
    * mis a true quand un joueur effectue un tir.
    */
   public boolean bEnMvt;

   /**
    * La bille blanche a-t-elle été empochée?.
    */
   public boolean bBlancDansPoch;
   /**
    * La bille noire a-t-elle été empochée?.
    */
   public boolean bNoireDansPoch;
    /**
    * l'IA est-t-elle entrain de calculer son prochain coup ?.
    */
    public boolean calIA;
    /**
    * boolean qui indique si le jeu est en pause.
    */
    public boolean pauseJeu;

   /**
    * Construit un <code>Moteur</code> et le thread associé
    * puis démarre le thread
    *
    * @param table
    *       Table qui contient les billes.
    */
   public Moteur (Table table)
   {
      this.t = table;

      /* Initialise le coup */
      this.initCoup();
   }

   /**
    * Verifie si au moins une bille bouge.
    *
    * @return false si aucune bille ne bouge
    * @return true si au moins une bille bouge
    **/
   public boolean bIsMoving ()
   {
      /* Si la force de la bille > 0, alors elle bouge */
      if (t.blancheBille.force > 0)
         return true;
      if (t.noireBille.force > 0)
         return true;
      for (int i = 0, c = t.rougeBille.length; i < c; ++i)
         if (t.rougeBille[i].force > 0)
            return true;
      for (int i = 0, c = t.jauneBille.length; i < c; ++i)
         if (t.jauneBille[i].force > 0)
            return true;
      return false;
   }

   /**
    * Methode du Thread qui sert au Déplacement des billes.
    * Tourne indefiniment, tant que bEnMvt est true, deplace les billes.
    */
   public void run ()
   {
      while (true)
      {
         try { 
            Thread.sleep(5); }
         catch (InterruptedException ex) { }
         if(pauseJeu) continue;

         if (!bEnMvt) continue;

         for (int j = 0, c = t.rougeBille.length; j < c; ++j)
            if (t.rougeBille[j].isMoving())
               t.rougeBille[j].bouleTraj.deplace();
         for (int j = 0, c = t.jauneBille.length; j < c; ++j)
            if (t.jauneBille[j].isMoving())
               t.jauneBille[j].bouleTraj.deplace();
         if (t.blancheBille.isMoving())
            t.blancheBille.bouleTraj.deplace();
         else
            this.bEnMvt = bIsMoving();
         if (t.noireBille.isMoving())
            t.noireBille.bouleTraj.deplace();

         /* Si plus aucune bille ne bouge, alors c'est la fin du coup */
         if (!bEnMvt) this.finCoup();
      }
   }

   /**
    * Modifie la trajectoire et la force de la bille blanche
    * en fonction de la position de la souris.
    * Méthode appelé au debut du coup qui met une force et une
    * traj pour que le thread puisse la deplacé ensuite.
    *
    * @param x
    *       Abscisse de la souris.
    * @param y
    *       Ordonnée de la souris.
    * @param f
    *       Force qui a va être appliqué à la bille blanche.
    */
   public void bougerBilleBlanche (float x, float y, int f)
   {
      if(!calIA)
      {
        if (t.help)
        {
          float coef = coefDirecteur (x, y, t.blancheBille.x, t.blancheBille.y);
          float ordo = ordOrigin (coef, x, y);

          if (((coef * t.ia.xSourisIA + ordo) >= t.ia.ySourisIA - 2 ) && 
             ((coef * t.ia.xSourisIA + ordo) <= t.ia.ySourisIA + 2 ))
          {
            x = t.ia.xSourisIA;
            y = t.ia.ySourisIA;
          }
        }
      }
      if(this.firstHit) f = 7000;
      t.blancheBille.bouleTraj = new Trajectoire (t.blancheBille,
            x,
            y,
            t.blancheBille.force, this);
      t.blancheBille.force = f;

      /* Initialise le coup */
      this.initCoup();
      this.bEnMvt = true;
      this.initBilleTouche();
      t.help = false;
      this.firstHit = false;
   }

   /**
    * Affichage des trajectoires des billes en fonction de la position de la queue.
    *
    * @param g
    *       Graphics - le contexte graphique.
    */
   public void paintTrajectoire (Graphics g)
   {
      /* Si des boules sont en mouvement ne pas afficher la trajectoire */
      if (bEnMvt) return;

      /* Initialisation de l'attribut touché des billes */
      this.initBilleTouche();
      t.blancheBille.touche = true;

      /* Creation, verification puis dessin de la trajectoire qui part
       * de la boule blanche et qui va vers l'opposé de la queue */
      Trajectoire traj = new Trajectoire (t.blancheBille.x, t.blancheBille.y,
            symCentrale(t.queue.xSouris, t.queue.bBlanche.x),
            symCentrale(t.queue.ySouris, t.queue.bBlanche.y),
            Color.WHITE, this, true, g, Moteur.OPT_nbRebond, Moteur.OPT_nbRebBilTouche);
      traj.verif();
      traj.paint();
   }

    /**
    * Affiche la trajectoire d'aide (la meilleure trajectoire).
    *
    * @param g
    *       Graphics - le contexte graphique.
    */ 
   public void paintTrajectoireHelp (Graphics g)
   {
      Color c = new Color(116, 180, 246);
      /* Si des boules sont en mouvement ne pas afficher la trajectoire help*/
      if (bEnMvt) return;

      /* Initialisation de l'attribut touché des billes */
      this.initBilleTouche();
      t.blancheBille.touche = true;

      /* Creation, verification puis dessin de la trajectoire d'aide qui part
      * de la boule blanche*/
      Trajectoire traj = new Trajectoire (t.blancheBille.x, t.blancheBille.y,
                                          t.ia.xSourisIA,
                                          t.ia.ySourisIA,
                                          c, this, true, g, 5, 0);
      traj.verif();
      traj.paint();
   }

   /**
    * Initialise les attributs de moteur relatif a un coup.
    * Prepare ces attributs afin d'enregistrer ce qui se passe
    * pendant le coup pour qu'ils puissent ensuite être analysé
    * par la méthode de verifation des coups.
    */
   public void initCoup ()
   {
      this.bEnMvt = false;
      this.bBlancDansPoch = false;
      this.bNoireDansPoch = false;
      this.firstBTouche = null;
      this.listBDansPoch = new LinkedList<Bille>();
      this.nbBDansPoch = 0;
      this.nbRebond = 0;
   }

   /**
    * Initialise l'attribut touché de toutes les billes à false. Utile pour
    * l'affichage de traj (pour ne plus prendre en compte une bille en mouvement).
    */
   public void initBilleTouche ()
   {
      for (int i = 0, c = t.rougeBille.length; i < c; ++i)
         t.rougeBille[i].touche = false;
      for (int i = 0, c = t.jauneBille.length; i < c; ++i)
         t.jauneBille[i].touche = false;
      t.blancheBille.touche = false;
      t.noireBille.touche = false;
   }

   /**
    * Calcul le coefficient directeur de la droite AB.
    *
    * @param xa
    *        Abscisse du point A.
    * @param ya
    *        Ordonnée du point A.
    * @param xb
    *        Abscisse du point B.
    * @param yb
    *        Ordonnée du point B.
    * @return le coefficient directeur de la droite AB.
    */
   public static float coefDirecteur (float xa, float ya, float xb, float yb)
   {
      /* Si la droite est horizontale ou verticale */
      if ((xa == xb) || (ya == yb)) return 0.0f;
      return (ya - yb) / (xa - xb);
   }

   /**
    * Calcul l'ordonnée à l'origine de la droite de coefficient directeur
    * coefDir passant par le point A.
    *
    * @param coefDir
    *       Coefficient directeur de la droite passant par A.
    * @param xa
    *        Abscisse du point A.
    * @param ya
    *        Ordonnée du point A.
    * @return l'ordonnée à l'origine de la droite.
    */
   public static float ordOrigin (float coefDir, float xa, float ya)
   {
      /* Si la droite est horizontale ou verticale */
      if (coefDir == 0.0f) return 0.0f;
      return ya - coefDir*xa; /* b = y - ax */
   }

   /**
    * Calcul le point image de A par rapport au centre de symetrie C.
    *
    * @param xa
    *       Point de départ de la symetrie de C.
    * @param xc
    *       Centre de la symetrie.
    * @return l'image de A par la symetrie de centre C.
    */
   public static float symCentrale (float xa, float xc)
   {
      return 2*xc - xa;
   }

   /**
    * Calcul le sens du mouvement qui va de Depart vers Arrive.
    *
    * @param depart
    *       Coordonnée x (ou y) du point de depart.
    * @param arrive
    *       Coordonnée x (ou y) du point d'arrivé.
    * @return Sens - le sens du mouvement en x (ou y).
    *          Positif, Negatif ou NULL.
    */
   public static Sens getSens (float depart, float arrive)
   {
      if (depart == arrive)
         return Sens.NULL;
      else if (depart < arrive)
         return Sens.POSITIF;
      else
         return Sens.NEGATIF;
   }

   /**
    * Calcul du sinus de l'angle formé par la trajectoire de la
    * bille d'attaque (A) avant contact (en C) avec la trajectoire
    * de la bille visée (B) à l'aide du produit vectoriel.
    *
    * @param xa
    *        Abscisse du point A. Un point de la trajectoire du
    *        centre de la bille d'attaque avant la collision.
    * @param ya
    *        Ordonnée du point A. Un point de la trajectoire du
    *        centre de la bille d'attaque avant la collision.
    * @param xb
    *        Abscisse du point B. Un point de la trajectoire du
    *        centre de la bill viséee apres la collision.
    * @param yb
    *        Ordonnée du point B. Un point de la trajectoire du
    *        centre de la bille visée apres la collision.
    * @param xc
    *        Abscisse du point C. Centre de la bille d'attaque
    *        au moment du contact.
    * @param yc
    *        Ordonnée du point C. Centre de la bille d'attaque
    *        au moment du contact.
    * @return le sinus de l'angle de la collision.
    */
   public static float sinCollision (float xa, float ya, float xb, float yb,
         float xc, float yc)
   {
      return produitVecteur(xa, ya, xb, yb, xc, yc)
               / (normVecteur(xc, yc, xa, ya) * normVecteur(xc, yc, xb, yb));
   }

   /**
    * Calcul le produit vectoriel : CA x CB , à l'aide des coord de A, B et C.
    * Utilisé pour le calcul du sinus de l'angle de la collision.
    *
    * @param xa
    *        Abscisse du point A.
    * @param ya
    *        Ordonnée du point A.
    * @param xb
    *        Abscisse du point B.
    * @param yb
    *        Ordonnée du point B.
    * @param xc
    *        Abscisse du point C.
    * @param yc
    *        Ordonnée du point C.
    * @return le produit vectoriel CA x CB.
    */
   public static float produitVecteur (float xa, float ya, float xb, float yb,
         float xc, float yc)
   {
      return (xa - xc)*(yb - yc) - (ya - yc)*(xb - xc);
   }

   /**
    * Calcul la norme du vecteur AB.
    * @param xa
    *        Abscisse du point A.
    * @param ya
    *        Ordonnée du point A.
    * @param xb
    *        Abscisse du point B.
    * @param yb
    *        Ordonnée du point B.
    * @return float - La norme du vecteur AB.
    */
   public static float normVecteur (float xa, float ya, float xb, float yb)
   {
      return (float) Math.sqrt((xb - xa)*(xb - xa) + (yb - ya)*(yb - ya));
   }

   /**
    * Calcul la rotation de coord x de (x,y) de 90 degres dont le centre est (C).
    *
    * @param x
    *        Abscisse du point qui va faire une rotation de 90 degres.
    * @param y
    *        Ordonnée du point qui va faire une rotation de 90 degres.
    * @param xc
    *        Abscisse du centre de la rotation.
    * @param yc
    *        Ordonnée du centre de la rotation.
    * @param sign
    *        Signe de la rotation. (int) Math.signum(sin)
    * @return float Abscisse du point (x,y) par la rotation de 90 degres de centre (C).
    */
   public static float rotationX (float x, float y, float xc, float yc, int sign)
   {
      return ((float) Math.cos(sign * 90)) * (x - xc)
         - ((float) Math.sin(sign * 90)) * (y - yc) + xc;
   }

   /**
    * Calcul la rotation de coord y de (x,y) de 90 degres dont le centre est (C).
    *
    * @param x
    *        Abscisse du point qui va faire une rotation de 90 degres.
    * @param y
    *        Ordonnée du point qui va faire une rotation de 90 degres.
    * @param xc
    *        Abscisse du centre de la rotation.
    * @param yc
    *        Ordonnée du centre de la rotation.
    * @param sign
    *        Signe de la rotation. (int) Math.signum(sin)
    * @return float Ordonnée du point (x,y) par la rotation de 90 degres de centre (C).
    */
   public static float rotationY (float x, float y, float xc, float yc, int sign)
   {
      return ((float) Math.sin(sign * 90)) * (x - xc)
         - ((float) Math.cos(sign * 90)) * (y - yc) + yc;
   }

   /**
    * Retourne la vitesse d'une bille en fonction de sa force.
    *
    * @param f
    *        La force de la bille.
    * @return int la vitess de la bille.
    */
   public static int getVitesse (int f)
   {
      if (f > Moteur.FORCE_MAX) return Moteur.PIXEL_MAX;
      else return ((f * Moteur.PIXEL_MAX) / Moteur.FORCE_MAX);
   }

   /**
    * Verifie si toutes les billes rouges ont été empochées.
    * 
    * @return true si toutes les billes rouges ont été empochées.
    */
   public boolean allRougeBDansPoch ()
   {
      for (int i = 0, c = t.rougeBille.length; i < c; ++i)
         if (!t.rougeBille[i].dansPoche)
            return false;
      return true;
   }

   /**
    * Verifie si toutes les billes jaunes ont été empochées.
    * 
    * @return true si toutes les billes jaunes ont été empochées.
    */
   public boolean allJauneBDansPoch ()
   {
      for (int i = 0, c = t.jauneBille.length; i < c; ++i)
         if (!t.jauneBille[i].dansPoche)
            return false;
      return true;
   }

   /**
    * Methode appelé au début d'un coup.
    * Si c'est au tour d'un joueur controlé par l'IA,
    * calcule le meilleur coup et l'effectue.
    *
    * @param saveCoup
    *       Faut-il sauvegarder la table avant le coup.
    */
   public void debutCoup (boolean saveCoup)
   {
      /* Sauvegarde de la table */
      if (saveCoup) t.cont.h.saveTable(t);
      /* Si le joueur est une IA */
      if (eq.j[eq.curr].isIA)
      {
         /*L'IA va calculer son prochain coup donc on passe calIA à true*/
         calIA = true;

         /*Trouve le meilleure coup à jouer*/
         t.ia.calculMeilleureCoup();

         /* Déssine la trajectoire du coup que l'IA va jouer */
         try
         {
            int x = (int) symCentrale(t.ia.xSourisIA, t.blancheBille.x);
            int y = (int) symCentrale(t.ia.ySourisIA, t.blancheBille.y);
            t.queue.setSouris(x, y);

            Thread.sleep(800);
         }
         catch (InterruptedException ex) { }

         /*Le calcule est terminé donc on passe calIA à false*/
         calIA = false;
         if (Moteur.OPT_sound == 0) SoundEffect.QUE.play();

         /*Joue le coup*/
         bougerBilleBlanche(t.ia.xSourisIA, t.ia.ySourisIA, t.ia.forceIA);
      }
   }

   /**
    * Methode appelé à la fin d'un coup.
    * Appel une methode qui va verifier le coup et changer l'équipe
    * pour le prochain coup s'il le faut.
    */
   public void finCoup ()
   {
      this.bEnMvt = false;
      this.eq.verifCoup(this);
      this.debutCoup(true);
   }

   /**
    * Convertit le moteur en String. Permet d'afficher des infos
    * a propos de celui ci dans la console.
    */
   public String toString ()
   {
      String firstB = (firstBTouche == null)?"{null}":firstBTouche.toString();
      return "{Option}{lvlIA:"+OPT_lvlIA+" coteDeb:"+OPT_coteDeb+" eqStart:"+OPT_EqQuiCommencePartie
         +"}{nbBandN:"+OPT_nbRebBilleNoire+" nbBandB:"+OPT_nbRebBilleBlanche
         +" BapresN:"+OPT_EmpocheBlancheApresNoire+" changeCote:"+OPT_changeCote+"}"
         +"\n\t{rbBBlanche:"+OPT_nbRebond+" rbBTouche:"+OPT_nbRebBilTouche+"   }\n"
         +"{Moteur}{bEnMvt:"+bEnMvt+" calIA:"+calIA+"J:"+eq.j[eq.curr].nom
         +"}{NinP:"+bNoireDansPoch+" BinP:"+bBlancDansPoch+" nbBinP: "+nbBDansPoch
         +" nbReb:"+nbRebond+"}\n\t{firstBTouche}"+firstB;
   }
}
