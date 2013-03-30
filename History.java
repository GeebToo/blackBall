import java.util.ArrayList;

import java.awt.Color;

import java.io.IOException;
import java.io.Serializable;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * class History : Enregistre la table a chaque coup.
 * Permet de revenir au coups precedents. De plus la partie est
 * sauvegarder dans un fichier à chaque coup, ce qui permet si on qui
 * le jeu de reprendre la partie sauvegardée.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey 
 */
public class History implements Serializable
{
   /**
    * Tableau qui retients les infos de chaque de coup. Position
    * des billes, stats et couleurs des equipes, force et direction du tir, ...
    */
   public ArrayList<HistTab> h;

   /**
    * Tableau qui sauvegarde les options.
    */
   public int[] opt;

   /**
    * Nombre d'option a sauvegarder.
    */
   public static int NB_OPT = 11;

   /**
    * Position actuelle dans la tableau. Si le joueur n'a effectué aucun retour en
    * arriere, curr == max;
    */
   public int curr;

   /**
    * Dernier coup effectué.
    */
   private int max;

   /**
     * Constructeur de History.
     */
   public History()
   {
      h = new ArrayList<HistTab>();
      curr = -1;
      max = -1;
      opt = new int[History.NB_OPT];
   }

   /**
    * Sauvegarde information a propos de la table avant le prochain coup.
    * Ajoute une nouvelle case à <code>h</code> * pour enregitrer
    * tous les infos importantes de la table.
    *
    * @param t
    *       Table a sauvegardé.
    */
   public void saveTable (Table t)
   {
      if (Moteur.OPT_save == 1) return; // si la sauvegarde est desactivé
      /* Si il y a eu des retour en arriere, on supprime tous les coups dans le " futur " */
      if (curr != max)
      {
         h.subList(curr+1, h.size()).clear();
         max = curr;
      }

      /* Sauvegarde du coup */
      h.add(new HistTab(t.rougeBille, t.jauneBille, t.noireBille, t.blancheBille,
               t.eqA, t.eqB, t.m.eq, t.poch.xLastBilleDansPocheEqA,
               t.poch.xLastBilleDansPocheEqB, t.queue.coteTriangleCasse,
               t.m.pauseJeu, t.m.firstHit));
      curr++;
      max++;

      /* Sauvegarde dans le fichier */
      saveToFile();
   }

   /**
    * Remet la table dans l'etat dans laquelle elle etait juste avant
    * le precedent coup.
    *
    * @param t
    *       Table sur laquelle la sauvegarde sera chargé.
    */
   public void loadPrevTable (Table t)
   {
      if (Moteur.OPT_save == 1) return; // si la sauvegarde est desactivé
      if (t.m.bEnMvt || t.m.calIA) return; /* Si en plein coup, on ne fais rien */
      if (max < 0) return; /* si aucune sauvegarde, on ne fait rien */
      if (curr <= 0)
         return; /* Si on est deja au premier coup, on ne revient pas en arriere */

      curr--;
      loadTable(t, h.get(curr));

      /* On lance le coup apres avoir fini de charger la partie */
      t.m.debutCoup(false);
   }

   /**
    * Remet la table dans l'état dans laquelle elle etait juste avant le dernier
    * retour en arriere en effectuant le coup qui a été effectué a ce moment là.
    * Si on est a la sauvegarde la plus recente. Refait le dernier coup et le
    * considere comme le tir du joueur.
    *
    * @param t
    *       Table sur laquelle la sauvegarde sera chargé.
    */
   public void loadNextTable (Table t)
   {
      if (Moteur.OPT_save == 1) return; // si la sauvegarde est desactivé
      if (t.m.bEnMvt || t.m.calIA) return; /* Si en plein coup, on ne fais rien */
      if (max < 0) return; /* si aucune sauvegarde, on ne fait rien */
      if (curr >= max) return; /* On verif que curr n'est pas trop grand */

      curr++;
      loadTable(t, h.get(curr));
      t.m.debutCoup(false);
   }

   /**
    * Remet la table dans un des état précédement sauvegardé.
    * @param t
    *       Table actuelle avec les billes qui sont affichés. La sauvegarde
    *       sera chargé sur cette table.
    * @param ht
    *       Sauvegarde de la table que l'on veut charger.
    */
   public void loadTable (Table t, HistTab ht)
   {
      /* Charge équipe */
      t.eqA = ht.eqA.clone();
      t.eqB = ht.eqB.clone();
      t.eqA.addEqAdv(t.eqB);
      t.eqB.addEqAdv(t.eqA);
      if (ht.eqAisPlaying)
         t.m.eq = t.eqA;
      else
         t.m.eq = t.eqB;
      /* Charge poche */
      t.poch.xLastBilleDansPocheEqA = ht.xLastBilleDansPocheEqA;
      t.poch.xLastBilleDansPocheEqB = ht.xLastBilleDansPocheEqB;
      t.queue.coteTriangleCasse = ht.coteTri;

      /* Charge Billes */
      t.noireBille.initB(ht.bN);
      t.blancheBille.initB(ht.bB);
      for (int i = 0, c = t.rougeBille.length; i < c; i++)
         t.rougeBille[i].initB(ht.bRs[i]);
      for (int i = 0, c = t.jauneBille.length; i < c; i++)
         t.jauneBille[i].initB(ht.bJs[i]);

      t.m.firstHit = ht.firstHit;
      t.cont.setPause(ht.pause);
   }

   /**
    * Sauvegarde la partie en cours dans le fichier blackBall.save.
    */
   public void saveToFile ()
   {
      this.getOptions();
      try{
         // use buffering
         ObjectOutput output = new ObjectOutputStream(
               new BufferedOutputStream(new FileOutputStream("blackBall.save")));
         try{
            /* Ecrit les infos sur la partie dans le fichier */
            output.writeObject(this);
         }
         finally{
            output.close(); /* ferme le fichier */
         }
      }  
      catch(IOException ex) { System.err.println("Error: couldn't write to blackBall.save"); }
   }

   /**
    * Essaye de lire le fichier blackBall.save afin de pouvoir reprendre une
    * partie sauvegardé.
    * @return History representant toutes les infos a propos d'une partie et des coups
    * qui la compose.
    */
   public History loadFromFile ()
   {
      History hNew = new History();
      try{
         //use buffering
         ObjectInput input = new ObjectInputStream (
               new BufferedInputStream(new FileInputStream("blackBall.save")));
         try{
            /* lit la partie sauvegardé dans le fichier. */
            hNew = (History) input.readObject();
         }
         finally{
            input.close(); /* ferme le fichier */
         }
      }
      catch(ClassNotFoundException ex){
         System.err.println("Cannot perform input. Class not found.");
         return null;
      }
      catch(IOException ex){
         System.err.println("Error: couldn't read from blackBall.save");
         return null;
      }

      return hNew;
   }

   /**
    * Recupere les options actuelles (qui sont dans Moteur)
    * et les sauvegarde dans le tableau d'options.
    */
   public void getOptions ()
   {
      opt[0]  = Moteur.OPT_lvlIA;
      opt[1]  = Moteur.OPT_coteDeb;
      opt[2]  = Moteur.OPT_EqQuiCommencePartie;
      opt[3]  = Moteur.OPT_nbRebBilleNoire;
      opt[4]  = Moteur.OPT_EmpocheBlancheApresNoire;
      opt[5]  = Moteur.OPT_nbRebBilleBlanche;
      opt[6]  = Moteur.OPT_changeCote;
      opt[7]  = Moteur.OPT_nbRebond;
      opt[8]  = Moteur.OPT_nbRebBilTouche;
      opt[9]  = Moteur.OPT_save;
      opt[10] = Moteur.OPT_sound;
   }

   /**
    * Met a jour les options du moteur avec les options sauvegardés.
    */
   public void loadOptions ()
   {
      Moteur.OPT_lvlIA                    = opt[0];
      Moteur.OPT_coteDeb                  = opt[1];
      Moteur.OPT_EqQuiCommencePartie      = opt[2];
      Moteur.OPT_nbRebBilleNoire          = opt[3];
      Moteur.OPT_EmpocheBlancheApresNoire = opt[4];
      Moteur.OPT_nbRebBilleBlanche        = opt[5];
      Moteur.OPT_changeCote               = opt[6];
      Moteur.OPT_nbRebond                 = opt[7];
      Moteur.OPT_nbRebBilTouche           = opt[8];
      Moteur.OPT_save                     = opt[9];
      Moteur.OPT_sound                    = opt[10];
   }
}
