import java.awt.Color;
import java.awt.Graphics;

/**
 * La Queue permet d'exercer une force sur la bille
 * blanche, cible de la queue. Elle est représenté par
 * une ligne qui part de la souris et qui vas jusqu'à la bille
 * cible.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Queue
{
   /**
    * Abscisse de la souris.
    */
   public float xSouris;
   /**
    * Ordonnée de la souris.
    */
   public float ySouris;

   /**
    * Bille blanche, cible de la queue.
    */
   public Bille bBlanche;
    
   /**
    * C'est la table qui contient la queue.
    */
   public Table t;

   /**
    * Information a propos du coté où se trouvait le triangle
    * de bille au moment de la casse.
    * 0 - pas encore définis.
    * 1 - coté gauche.
    * 2 - Coté droit.
    */
   public int coteTriangleCasse;

   /**
    * Triangle du coté gauche au moment de la casse.
    * Constante utilisé pour <code>coteTriangleCasse</code>..
    */
   public final static int TRI_COTE_G = 1;
   /**
    * Triangle du coté droit au moment de la casse.
    * Constante utilisé pour <code>coteTriangleCasse</code>..
    */
   public final static int TRI_COTE_D = 2;

   /**
    * Construit une <code>Queue</code>.
    *
    * @param b
    *       Bille cible de la queue.
    */
   public Queue(Bille b, Table t)
   {
      this.bBlanche = b;
      this.t = t;
   }

   /**
    * Ajoute la bille blanche a la queue.
    * @param b
    *       La bille blanche de la table.
    */
   public void addBBlanche (Bille b)
   {
      this.bBlanche = b;
   }

   /**
    * Convertit une <code>Queue</code> en chaîne de caractères.
    *
    * @return string contenant des infos a propos de la queue.
    */
   public String toString()
   {
      String cote = " TriCote U";
      if (coteTriangleCasse == Queue.TRI_COTE_G)
         cote = " TriCote G";
      else if (coteTriangleCasse == Queue.TRI_COTE_D)
         cote = " TriCote D";
      return "{Queue}{xSouris:"+this.xSouris+" ySouris:"+this.ySouris+cote+"}";
   }

   /**
    * Retourne l'abscisse de la souris.
    *
    * @return Abscisse de la souris.
    */
   public float getXSouris()
   {
      return xSouris;
   }
   /**
    * Retourne l'ordonnée de la souris.
    *
    * @return Ordonnée de la souris.
    */
   public float getYSouris()
   {
      return ySouris;
   }

   /**
    * Modifie la posiition de la souris. Appelé des que la souris bouge.
    *
    * @param x
    *       Abscisse de la souris.
    * @param y
    *       Ordonnée de la souris.
    */
   public void setSouris(float x, float y)
   {
      this.xSouris = x;
      this.ySouris = y;
   }

   /**
    * Affiche la queue si les billes ne sont pas en mouvement.
    * La queue est une ligne qui va de la bille blanche jusqu'à la souris.
    *
    * @param g
    *       Contexte graphique.
    */
   public void paint(Graphics g)
   {
      g.setColor(Color.BLUE);
      if (!bBlanche.isMoving())
          if (!bBlanche.dansPoche && (t.readyPaint || t.m.calIA))
              g.drawLine((int) xSouris, (int) ySouris, (int) bBlanche.x, (int) bBlanche.y);
          if (bBlanche.dansPoche)
              paintPutBBlanche(g);
   }

   /**
    * Si la bille blanche a été empoché dessine sa remise en jeu
    * à la place de la queue. Remise en jeu à droite ou gauche en fonction
    * de la position du triangle au moment de la casse.
    *
    * @param g
    *       Contexte graphique.
    */
   private void paintPutBBlanche (Graphics g)
   {
      g.setColor(this.bBlanche.couleur);
      final int diam = 2 * Bille.RAYON;
      // si triangle a droite
      if (coteTriangleCasse == Queue.TRI_COTE_D)
         paintPutBBlancheG(g);
      else
         paintPutBBlancheD(g);

      // Affiche la bille blanche sous la souris.
      g.drawOval((int) this.xSouris - Bille.RAYON,
            (int) this.ySouris - Bille.RAYON, diam, diam);
   }

   /**
    * Si la bille blanche a été empoché dessine sa remise en jeu à Gauche.
    * à la place de la queue. Remise en jeu à droite ou gauche en fonction
    * de la position du triangle au moment de la casse.
    *
    * @param g
    *       Contexte graphique.
    */
   private void paintPutBBlancheG (Graphics g)
   {
      /* dessine limite de la zone de remise en jeu. */
      g.drawLine(((int) Table.WIDTH) / 4, (int) Table.ORI_Y_T,
           ((int) Table.WIDTH) / 4, (int) (Table.ORI_Y_T + Table.HEIGHT_T));

      /* si la souris est en dehors de la zone, on dessine un cercle rouge */
      if (((xSouris <= (Table.WIDTH/4)) && (xSouris >= (Table.ORI_X_T + Bille.RAYON))
            && (ySouris >= Table.ORI_Y_T + Bille.RAYON) && (ySouris <= (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON)))
          && (!t.verifCollision(xSouris, ySouris)))
         g.setColor(Color.WHITE);
      else 
         g.setColor(Color.RED);
   }

   /**
    * Si la bille blanche a été empoché dessine sa remise en jeu à Droite.
    * à la place de la queue. Remise en jeu à droite ou gauche en fonction
    * de la position du triangle au moment de la casse.
    *
    * @param g
    *       Contexte graphique.
    */
   private void paintPutBBlancheD (Graphics g)
   {
      g.drawLine((int) (Table.WIDTH / 4) * 3, (int) Table.ORI_Y_T,
            (int) (Table.WIDTH / 4) * 3, (int) (Table.ORI_Y_T + Table.HEIGHT_T));

      /* si la souris est dans la zone, on dessine un cercle blanc */
      if (((xSouris >= (Table.WIDTH / 4) * 3)
               && (xSouris <= Table.ORI_X_T + Table.WIDTH_T - Bille.RAYON)
               && (ySouris>=Table.ORI_Y_T + Bille.RAYON)
               && (ySouris <= (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON)))
            && (!t.verifCollision(xSouris, ySouris)))
         g.setColor(Color.WHITE);
      else
         g.setColor(Color.RED);
   }
}
