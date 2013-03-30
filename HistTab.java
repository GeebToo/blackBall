import java.io.Serializable;

/**
 * Class qui contient tout les infos importante d'une table
 * avant un coup.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class HistTab implements Serializable
{
   /**
    * Tableau des billes rouges.
    */
   public Bille[] bRs;
   /**
    * Tableau des billes jaunes.
    */
   public Bille[] bJs;
   /**
    * Bille noire.
    */
   public Bille bN;
   /**
    * Bille blanche.
    */
   public Bille bB;
   /**
    * Equipe A.
    */
   public Equipe eqA;
   /**
    * Equipe B.
    */
   public Equipe eqB;

   /**
    * L'equipe A était-elle en train de jouer ?.
    * Si l'equipe A etait celle qui etait dans le moteur au moment
    * de la sauvegarde, alors cet attribut est à true.
    */
   public boolean eqAisPlaying;

   /**
    * Coté du triangle de bille au moment de la casse. Stocké dans queue.
    */
   public int coteTri;

   /**
    * Abscisse de la deniere bille dans la zone score de l'Equipe A.
    */
   public int xLastBilleDansPocheEqA;
   /**
    * Abscisse de la deniere bille dans la zone score de l'Equipe B.
    */
   public int xLastBilleDansPocheEqB;

   /**
    * Sauvegarde si le jeu est en pause.
    */
   public boolean pause;

   /**
    * Sauvegarde si c'est le premier coup.
    */
   public boolean firstHit;

   /**
    * Constructeur de HistTab.
    * @param bRs
    *       Tableau des billes rouges.
    * @param bJs
    *       Tableau des billes jaunes.
    * @param bN
    *       Bille noire.
    * @param bB
    *       Bille blanche.
    * @param eqA
    *       Equipe A.
    * @param eqB
    *       Equipe B.
    * @param eqPlaying
    *       L'equipe qui est en train de jouer (celle qui est dans moteur).
    * @param xLastBilleDansPocheEqA
    *       Abscisse de la deniere bille dans la zone score de l'Equipe A.
    * @param xLastBilleDansPocheEqB
    *       Abscisse de la deniere bille dans la zone score de l'Equipe B.
    * @param coteTriangleCasse
    *       Cote du triangle de bille au moment de la casse. Attribut de queue.
    */
   public HistTab (Bille[] bRs, Bille[] bJs, Bille bN, Bille bB, Equipe eqA, Equipe eqB,
         Equipe eqPlaying, int xLastBilleDansPocheEqA, int xLastBilleDansPocheEqB, int coteTriangleCasse,
         boolean pauseJeu, boolean firstHit)
   {
      this.eqA =  eqA.clone();
      this.eqB =  eqB.clone();
      if (eqA == eqPlaying) this.eqAisPlaying = true;
      else this.eqAisPlaying = false;
      this.xLastBilleDansPocheEqA = xLastBilleDansPocheEqA;
      this.xLastBilleDansPocheEqB = xLastBilleDansPocheEqB;
      this.bB = bB.clone();
      this.bN = bN.clone();
      this.bRs = new Bille[bRs.length];
      this.bJs = new Bille[bJs.length];
      for (int i = 0, c = this.bRs.length; i < c; ++i)
         this.bRs[i] = bRs[i].clone();
      for (int i = 0, c = this.bJs.length; i < c; ++i)
         this.bJs[i] = bJs[i].clone();

      this.coteTri = coteTriangleCasse;
      this.pause = pauseJeu;
      this.firstHit = firstHit;
   }
}
