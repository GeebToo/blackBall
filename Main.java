/**
 * Contient la méthode main.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Main
{
   /**
    * Main: crée un nouveau controller et affiche le menu principal.
    *
    * @param args
    *        command line arguments. Aucun n'est requis ici.
    */
   public static void main (String [] args)
   {
      Controller c = new Controller();
      c.createFrames();
      c.showMenu();
   }
}
