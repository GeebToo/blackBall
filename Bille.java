import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

/**
 * Une Bille, definit par sa couleur (blanc, noir, rouge ou jaune),
 * la position de son centre et une force.
 * Si sa force est non null, elle possede aussi une trajectoire.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Bille implements Serializable
{
   /**
    * Constante qui représente le rayon de la bille.
    * Par defaut: rayon = 10
    */
   static final int RAYON = 10;

   /* Coordonnées de la bille */
   /**
    * Abscisse du centre de la bille.
    */
   public float x;
   /**
    * Ordonnée du centre de la bille.
    */
   public float y;

   /**
    * Force de la bille.
    * Si pas positif, la bille ne bouge pas.
    * Plus la force est élevé, plus la bille se déplace rapidement.
    */
   public int force;

    /**
    * Nombre de collision de la bille.
    * Utiliser par l'IA pour aider à déterminer la bille qui est touchée en première lors d'un coup
    */
   public int nbreCollision = 0;

   /**
    * Trajectoire de la bille si elle se déplace.
    * trajNull sinon.
    */
   public Trajectoire bouleTraj;

   /**
    * Couleur de la bille.
    * Peut être rouge, jaune, banche ou noire.
    */
   public Color couleur;

   /**
    * Permet de savoir si une bille a déjà été touchée ou pas.
    * Sert pour paintTrajectoire(). Si elle a été touché au moins une fois
    * elle n'intervient plus dans le reste du calcul de la trajectoire.
    */
   public boolean touche;

   /**
    * Savoir si la bille a été empochée.
    */
   public boolean dansPoche;

   /**
    * Construit une <code>Bille</code>.
    *
    * @param col
    *        Couleur de la nouvelle bille. (rouge, jaune, blanc ou noir)
    */
   public Bille(Color col)
   {
      this.initB();
      this.setCouleur(col);
   }

   /**
    * Initialise les attributs de <code>Bille</code>
    * autre que la couleur et la position.
    */
   public void initB ()
   {
      this.touche = false; /* pour l'affichage des trajectoires */
      this.dansPoche = false;
      this.bouleTraj = new Trajectoire(); /* traj null */
      this.force = 0;
   }

   /**
    * Initialise la bille pour une nouvelle partie.
    * Place la bille à la nouvelle position.
    * @param x
    *        Abscisse de la nouvelle position de la bille.
    * @param y
    *        Ordonnée de la nouvelle position de la bille.
    */
   public void initB (float x, float y)
   {
      initB();
      this.setCoord(x, y);
   }
   /**
    * Initialise la bille avec les même attributs que la bille
    * donnée en argument. Utilisé par l'IA pour init ses billes.
    * @param b
    *        Bille dont les attributs seront copiés.
    */
   public void initB (Bille b)
   {
      this.x = b.x;
      this.y = b.y;
      this.force = b.force;
      this.bouleTraj= b.bouleTraj;
      this.couleur = b.couleur;
      this.dansPoche = b.dansPoche;
   }

   /**
    * Convertit une <code>Bille</code> en chaîne de caractères.
    *
    * @return string contenant des infos sur cette bille.
    */
   public String toString()
   {
      return "{Bille}{x:"+this.x+" y:"+this.y+"}{col:"+Bille.couleurToString(couleur)
         +"}{inPoch:"+this.dansPoche+" touch:"+this.touche +" force:"+this.force
         + "}\n\t {Traj}"+this.bouleTraj;
   }
   /**
    * Convertit la couleur de la bille en chaîne de caractères compact.
    *
    * @return string contenant la couleur de la bille.
    */
   public static String couleurToString(Color c)
   {
      if (c.equals(Color.RED)) return "RED";
      if (c.equals(Color.YELLOW)) return "YELLOW";
      if (c.equals(Color.WHITE)) return "WHITE";
      if (c.equals(Color.BLACK)) return "BLACK";
      return c.toString();
   }

   /* Getters */
   /**
    * Retourne l'abscisse du centre de la bille.
    *
    * @return float abscisse (x) de la bille.
    */
   public float getCoordX()
   {
      return this.x;
   }
   /**
    * Retourne l'ordonnée du centre de la bille.
    *
    * @return float ordonnée (y) de la bille.
    */
   public float getCoordY()
   {
      return this.y;
   }
   /**
    * Retourne la force de la bille.
    *
    * @return int force de la bille.
    */
   public int getForce()
   {
      return this.force;
   }

   /* Setters */
   /**
    * Modifie la position de la bille.
    *
    * @param coordX
    *        Abscisse de la nouvelle position de la bille.
    * @param coordY
    *        Ordonnée de la nouvelle position de la bille.
    */
   public void setCoord(float coordX, float coordY)
   {
      this.x = coordX;
      this.y = coordY;
   }
   /**
    * Modifie la couleur de la bille.
    *
    * @param col
    *        Nouvelle couleur de la bille.
    */
   public void setCouleur(Color col)
   {
      this.couleur = col;
   }
   /**
    * Modifie la force de la bille.
    *
    * @param f
    *        Nouvelle force de la bille.
    */
   public void setForce(int f)
   {
      this.force = f;
   }

   /**
    * Affichage de la <code>Bille</code>.
    *
    * @param g
    *       Graphics - pour pouvoir dessiner sur le panel de la table.
    */
   public void paint(Graphics g)
   {
      final int   ovalX = (int) this.x - RAYON,
                  ovalY = (int) this.y - RAYON;
      final int diam = 2 * Bille.RAYON;
      g.setColor(this.couleur);
      g.fillOval( ovalX, ovalY, diam, diam);
   }

   /**
    * Verifie si b est en contact avec cette instance de <code>Bille</code>.
    *
    * @param b
    *       Bille avec laquelle cette instance de bille
    *       doit vérifier si elles sont en contact.
    * @return true  - elles se touchent
    * @return false - elles ne se touchent pas
    */
   public boolean isCollision(Bille b)
   { /* SI la distance qui separe leur centre est inférieur au diametre d'une bille
      * ALORS les billes se touchent */
      final float dx = b.x - this.x;
      final float dy = b.y - this.y;
      /* Calcul du carré de la distance qui separe les centres des 2 billes */
      final float dist = dx * dx + dy * dy;
      return dist <= 4*Bille.RAYON*Bille.RAYON;
   }

   /**
    * Verifie si la bille, dont les coordonnées sont données en paramètre,
    * est en contact avec cette instance de <code>Bille</code>.
    *
    * @param bx
    *       Abscisse de bille avec laquelle cette instance de bille
    *       doit vérifier si elles sont en contact.
    * @param by
    *       Ordonnée de bille avec laquelle cette instance de bille
    *       doit vérifier si elles sont en contact.
    * @return true  - elles se touchent
    * @return false - elles ne se touchent pas
    */
   public boolean isCollision(float bx, float by)
   {
      final float dx = bx - this.x;
      final float dy = by - this.y;
      /* Calcul du carré de la distance qui separe les centres des 2 billes */
      final float dist = dx * dx + dy * dy;
      return dist <= 4*Bille.RAYON*Bille.RAYON;
   }

   /**
    * Verifie si le rectangle qui contient b chevauche
    * celui qui contient cette instance de <code>Bille</code>.
    *
    * @param b
    *       Bille avec laquelle cette instance de bille
    *       doit vérifier si elles se chevauchent.
    * @return true  - les rect. se chevauchent
    * @return false - pas de chevauchement
    */
   public boolean isOverlap(Bille b)
   { /* SI
      * Cote gauche de A  a gauche  du cote droit  de B ET
      * Cote droit  de A  a droite  du cote gauche de B ET
      * Cote  haut  de A  au dessus du cote  bas   de B ET
      * Cote  bas   de A en dessous du cote  haut  de B
      * ALORS les rectangles se chevauchent */

      return ( (this.x - Bille.RAYON) < (b.x + Bille.RAYON) &&
               (this.x + Bille.RAYON) > (b.x - Bille.RAYON) &&
               (this.y - Bille.RAYON) < (b.y + Bille.RAYON) &&
               (this.y + Bille.RAYON) > (b.y - Bille.RAYON) );
   }

   /**
    * Verifie si le rectangle, qui contient la bille dont les coord
    * sont données en paramètre, chevauche celui qui contient
    * cette instance de <code>Bille</code>.
    *
    * @param bx
    *       Abscisse de la bille avec laquelle cette instance de bille
    *       doit vérifier si elles se chevauchent.
    * @param by
    *       Ordonnée de la bille avec laquelle cette instance de bille
    *       doit vérifier si elles se chevauchent.
    * @return true  - les rect. se chevauchent
    * @return false - pas de chevauchement
    */
   public boolean isOverlap(float bx, float by)
   {
      return ( (this.x - Bille.RAYON) < (bx + Bille.RAYON) &&
               (this.x + Bille.RAYON) > (bx - Bille.RAYON) &&
               (this.y - Bille.RAYON) < (by + Bille.RAYON) &&
               (this.y + Bille.RAYON) > (by - Bille.RAYON) );
   }

   /**
    * Verifie si une bille se trouve dans la poche dont les coordonnées
    * sont indiquées en paramètre.
    *
    * @param px
    *       Abscisse du centre de la poche.
    * @param py
    *       Ordonnée du centre de la poche.
    * @return true  - bille dans la poche
    * @return false - bille pas dans la poche
    */
   public boolean isInHole(float px, float py)
   {
      final float dx = px - this.x;
      final float dy = py - this.y;
      /* Calcul du carré de la distance qui separe centre de bille -> poche */
      final float dist = dx * dx + dy * dy;
      return dist <= Bille.RAYON*Bille.RAYON/4;
   }

   /**
    * Verifie si une billes est en mouvement.
    * C'est-à-dire que sa force est supérieur à 0.
    *
    * @return true si la bille est en mouvement.
    */
   public boolean isMoving ()
   {
      if (this.force == 0) return false;
      else if (this.force < 0)
      {
         this.force = 0;
         return false;
      }
      return true;
   }

   /**
    * Clone cette bille.
    * @return Une copie de cette bille.
    */
   public Bille clone()
   {
      Bille b = new Bille (this.couleur);
      b.setCoord(this.x, this.y);
      b.setForce(this.force);
      b.dansPoche = this.dansPoche;
      return b;
   }
}
