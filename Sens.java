/**
 * Enumération qui représente le sens d'une trajectoire.
 * Il peut etre positif (de la gauche vers la droite ou haut vers bas),
 * negatif (droite vers gauche ou bas vers haut), ou nulle.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public enum Sens
{
   /**
    * Sens positif. De la gauche vers la droite en x, ou du
    * haut vers le bas en y.
    */
   POSITIF("+"),
   /**
    * Sens negatif. De la droite vers la gauche en x, ou du
    * bas vers le haut en y.
    */
   NEGATIF("-"),
   /**
    * Sens NULL. Pas de déplacement dans cette direction.
    */
   NULL("=");

   private String s;

   /**
    * Constructeur du Sens d'une trajectoire.
    */
   Sens(String s)
   {
      this.s = s;
   }

   /**
    * Pour savoir si la trajectoire est de sens positif. De la gauche vers la droite
    * en x, ou du haut vers le bas en y.
    *
    * @return true si le sens est positif.
    */
   public boolean isPositif()
   {
      if (this.s == "+") return true;
      else return false;
   }
}
