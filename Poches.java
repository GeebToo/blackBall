import java.awt.Color;
import java.awt.Graphics;

/**
 * Les Poches de la table de billard. Par defaut il y a six poches :
 * dans le coin superieur gauche, une au centre sur le cote superieur,
 * dans le coin superieur droit, dans le coin inferieur gauche,
 * une au centre sur le cote inferieur, et une derniere dans le coin inferieur droit, 
 *
 * Quand une bille a été empoché, elle est mise dans la zone score
 * de l'équipe qui l'a empochée.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Poches
{
   /**
    * Rayon des Poches. Par defaut 14.
    */
   static final int RAYON = 14;
   /**
    * Diametre des Poches. Par defaut 28.
    */
   static final int DIAM = Poches.RAYON * 2;
   /**
    * Nombre de Poches. Par defaut six.
    */
   static final int NB_POCHES = 6;

   /**
    * Ordonnée des billes dans la zone score.
    * Une bille empochée va dans la zone score.
    */
   public final int yBilleDansPoche = (int) Table.ORI_Y -  Bille.RAYON*2;

   /**
    * Abscisse de la derniere bille empochée par l'équipe A
    * dans la zone score.
    */
   public int xLastBilleDansPocheEqA;

   /**
    * Abscisse de la derniere bille empochée par l'équipe A
    * dans la zone score.
    */
   public int xLastBilleDansPocheEqB;

   /**
    * Tableau des abscisses des coins superieur gauche
    * de toutes les poches.
    */
   public int[] xCoinSupGau;
   /**
    * Tableau des ordonnées des coins superieur gauche
    * de toutes les poches.
    */
   public int[] yCoinSupGau;
   /**
    * Tableau des abscisses des centres de toutes les poches.
    */
   public int[] xCentre;
   /**
    * Tableau des ordonnées des centres de toutes les poches.
    */
   public int[] yCentre;

   /**
    * Couleur des poches, utilisé pour paint().
    */
   private Color c;

   /**
    * Construit les six <code>Poches</code>.
    */
   public Poches()
   {
      initLastBDansPoche();
      this.c      = new Color(204, 204, 204);
      xCoinSupGau = new int[Poches.NB_POCHES];
      yCoinSupGau = new int[Poches.NB_POCHES];
      xCentre     = new int[Poches.NB_POCHES];
      yCentre     = new int[Poches.NB_POCHES];

      /* Poche n°0 : superieur gauche */
      this.xCoinSupGau[0] = (int) Table.ORI_X + 5;
      this.yCoinSupGau[0] = (int) Table.ORI_Y + 5;
      this.xCentre[0]     = this.xCoinSupGau[0] + Poches.RAYON;
      this.yCentre[0]     = this.yCoinSupGau[0] + Poches.RAYON;
      /* Poche n°1 : superieur centre */
      this.xCoinSupGau[1] = (((int) Table.WIDTH_T + (int) Table.ORI_X_T)/2) -5;
      this.yCoinSupGau[1] = (int) Table.ORI_Y + 5;
      this.xCentre[1]     = this.xCoinSupGau[1] + Poches.RAYON;
      this.yCentre[1]     = this.yCoinSupGau[1] + Poches.RAYON;
      /* Poche n°2 : superieur droit */
      this.xCoinSupGau[2] = (int) Table.WIDTH_T + (int) Table.ORI_X_T - 5;
      this.yCoinSupGau[2] = (int) Table.ORI_Y + 5;
      this.xCentre[2]     = this.xCoinSupGau[2] + Poches.RAYON;
      this.yCentre[2]     = this.yCoinSupGau[2] + Poches.RAYON;
      /* Poche n°3 : inferieur droit */
      this.xCoinSupGau[3] = (int) Table.WIDTH_T + (int) Table.ORI_X_T - 5;
      this.yCoinSupGau[3] = (int) Table.ORI_Y_T + (int) Table.HEIGHT_T;
      this.xCentre[3]     = this.xCoinSupGau[3] + Poches.RAYON;
      this.yCentre[3]     = this.yCoinSupGau[3] + Poches.RAYON;
      /* Poche n°4 : inferieur centre */
      this.xCoinSupGau[4] = (((int) Table.WIDTH_T + (int) Table.ORI_X_T)/2);
      this.yCoinSupGau[4] = (int) Table.ORI_Y_T + (int) Table.HEIGHT_T;
      this.xCentre[4]     = this.xCoinSupGau[4] + Poches.RAYON;
      this.yCentre[4]     = this.yCoinSupGau[4] + Poches.RAYON;
      /* Poche n°5 : inferieur gauche */
      this.xCoinSupGau[5] = (int) Table.ORI_X + 5;
      this.yCoinSupGau[5] = (int) Table.ORI_Y_T + (int) Table.HEIGHT_T;
      this.xCentre[5]     = this.xCoinSupGau[5] + Poches.RAYON;
      this.yCentre[5]     = this.yCoinSupGau[5] + Poches.RAYON;
   }

   /**
    * Affiche toutes les poches.
    * Utilisé avant d'avoir fondTable.png
    *
    * @param g
    *       Contexte graphique.
    */
   public void paint(Graphics g)
   {
      g.setColor(c);
      for (int i = 0; i < NB_POCHES; ++i)
         g.fillOval(this.xCoinSupGau[i], this.yCoinSupGau[i],
               Poches.DIAM, Poches.DIAM);
   }

   /**
    * Affiche une bille dans la poche indiquée en parametre.
    * Utilisé pour paintTrajectoire().
    *
    * @param g
    *       Contexte graphique.
    * @param col
    *       Couleur de la bille.
    * @param numPoche
    *       Numero de la poche dans laquelle va etre dessiné la bille.
    */
   public void paintBilleDansPoches (Graphics g, Color col, int numPoche)
   {
      g.setColor(col);
      g.drawOval(this.xCentre[numPoche] - Bille.RAYON, this.yCentre[numPoche] - Bille.RAYON,
            Bille.RAYON*2, Bille.RAYON*2);
   }

   /**
    * Verifie si la bille (x,y) va entrer dans un trou.
    *
    * @param x
    *       Abscisse du centre de la bille.
    * @param y
    *       Ordonnée du centre de la bille.
    * @return le numero de la poche dans laquelle va entrer la bille.
    * @return -1 si la bille ne va entrer dans aucune poche.
    */
   public static int inHole(float x, float y)
   {
      if (y <= Table.ORI_Y_T + Bille.RAYON +4)
      {
         /* Poche n°0 : superieur gauche */
         if ((x <= ((int)Table.ORI_X_T+Bille.RAYON+4)))
            return 0;
         /* Poche n°1 : superieur centre */
         else if ((x >= (Table.WIDTH/2)-10) && (x <= (Table.WIDTH/2)+5))
            return 1;
         /* Poche n°2 : superieur droit */
         else if (x >= Table.WIDTH_T + Table.ORI_X_T - Bille.RAYON-4)
            return 2;
      }
      else if (y >= (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON -4))
      {
         /* Poche n°3 : inferieur droit */
         if ((x >= ((int)Table.WIDTH_T + (int)Table.ORI_X_T - Bille.RAYON-4)))
            return 3;
         /* Poche n°4 : inferieur centre */
         else if ((x >= ((int)Table.WIDTH/2)-10) && (x <= ((int)Table.WIDTH/2)+5))
            return 4;
         /* Poche n°5 : inferieur gauche */
         else if ((x <= ((int)Table.ORI_X_T + Bille.RAYON+4)))
            return 5;
      }

      return -1;
   }

   /**
    * Déplace une bille empochée dans la zone de score de l'équipe
    * qui dont c'est le tour.
    *
    * @param b
    *       Bille qui a été empochée.
    * @param m
    *       Moteur qui contient des infos sur le coup, pour les mettre
    *       à jour et savoir quelle équipe à empochée la bille.
    */
   public void putBilleInHole (Bille b, Moteur m)
   {
      if (Moteur.OPT_sound == 0) SoundEffect.SINK.play();
      // Arrete la bille
      b.force = 0;
      b.dansPoche = true;
      b.bouleTraj = new Trajectoire();

      // Place la bille dans la zone de score de l'quipe qui l'a empochée
      b.y = m.t.poch.yBilleDansPoche;
      if (m.t.eqA == m.eq)
      {
         m.t.poch.xLastBilleDansPocheEqA += Bille.RAYON*2;
         b.x = m.t.poch.xLastBilleDansPocheEqA;
      }
      else
      {
         m.t.poch.xLastBilleDansPocheEqB -= Bille.RAYON*2;
         b.x = m.t.poch.xLastBilleDansPocheEqB;
      }

      // Modif infos sur le coup
      m.nbBDansPoch++;
      m.listBDansPoch.add (b);
      if (b.couleur.equals(Color.WHITE))
         m.bBlancDansPoch = true;
      else if (b.couleur.equals(Color.BLACK))
         m.bNoireDansPoch = true;
      // else if (this.c.equals(Color.RED))
      // else if (this.c.equals(Color.YELLOW))
   }

   /**
    * Initialise les attributs de Poches relatif a la position des
    * dernieres billes empochées.
    */
   public void initLastBDansPoche()
   {
      xLastBilleDansPocheEqA = - Bille.RAYON;
      xLastBilleDansPocheEqB = Fenetre.WIDTH + Bille.RAYON;
   }
}
