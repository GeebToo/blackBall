import java.io.Serializable;
/**
 * Représente un joueur qui peut être controlé par l'IA ou un
 * humain. Contient des infos sur ses stats lors de la partie
 * (Fautes commises, nombre de Billes empochées) et son nom.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Joueur implements Serializable
{
   /**
    * Nom du <code>Joueur</code>
    * par def: JoueurN (N = n° du joueur)
    * ou IAN si joueur controlé par l'IA
    */
   public String nom;

   /**
    * Nombre de fautes commisent par le <code>Joueur</code>.
    * Une faute donne deux coups à l'équipe adverse.
    */
   public int  nbFaute;
   /**
    * Nombre de bille(s) que le <code>Joueur</code> à empochée(s)
    */
   public int nbBilEmpoche;
   /**
    * Nombre de coups restant au <code>Joueur</code> avant le tour du joueur
    * suivant. Est = 0 si c'est un tour d'un autre joueur.
    * Egale à 2 si le joueur précédent à commis une faute. Sinon = 1
    */
   public int nbCoupRestant;

   /**
    * Joueur contrôlé par un Humain (<code>false</code>)
    * ou par l'IA (<code>true</code>)
    */
   public boolean isIA;

   /**
    * Construit un <code>Joueur</code>.
    *
    * @param s
    *       chaine de caracteres contenant le nom du Joueur.
    * @param ia
    *       true si le joueur est controlé par l'IA.
    *       false si joueur controlé par un humain.
    */
   public Joueur (String s, boolean ia)
   {
      this.nom = s;
      this.isIA = ia;
   }

   /**
    * Convertit un <code>Joueur</code> en chaîne de caractères.
    *
    * @return string contenant des infos sur cette instance de Joueur.
    */
   public String toString ()
   {
      final String IA = (isIA)?"IA":"H";
      return "{n:"+nom+"}{"+IA+"}{CR:"+nbCoupRestant
         +" E:"+nbBilEmpoche+" F:"+nbFaute+"}";
   }

   /**
    * Clone ce Joueur.
    * @return Une copie de cette joueur.
    */
   public Joueur clone()
   {
      Joueur j = new Joueur(this.nom, this.isIA);
      j.nbFaute = this.nbFaute;
      j.nbCoupRestant = this.nbCoupRestant;
      j.nbBilEmpoche = this.nbBilEmpoche;
      return j;
   }
}
