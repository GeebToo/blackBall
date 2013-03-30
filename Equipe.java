import java.awt.Color;
import java.util.LinkedList;
import java.io.Serializable;

/**
 * Une équipe composé d'un nombre variable de joueurs
 * qui jouent a tour de rôle face à une autre équipe.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Equipe implements Serializable
{
   /**
    * Tableau de <code>Joueur</code> composant l'equipe
    */
   public Joueur[] j;

   /**
    * Couleur de l'équipe (ie: couleur des billes que les Joueurs
    * de cette équipe doivent touché en 1er pour ne pas commetre de faute).
    *
    * Au début est égale à <code>blanc</code> tant qu'aucune bille n'a été empoché
    * A la fin, quand une des équipes n'a plus que la noire à empochée,
    * à <code>noire</code>
    */
   public Color c;

   /**
    * Position dans le tableau du <code>Joueur</code> actuel ou du prochain joueur
    * si c'est à l'autre <code>Equipe</code> de jouer.
    */
   public int curr;

   /**
    * Equipe adverse
    */
   private Equipe eqAdv;

   /**
    * Nombre de victoire de cette equipe depuis le debut d'une nouvelle
    * partie.
    */
   public int nbVictoire;

   /**
    * Construit une <code>Equipe</code>.
    * @param nbJ
    *       Nombre de joueur dans l'équipe.
    */
   public Equipe (int nbJ)
   {
      j = new Joueur[nbJ];
      curr = 0;
      initC();
   }

   /**
    * Verifie le coup que vient d'effectuer le Joueur de cette équipe.
    *
    * @param m
    *       Moteur du jeu dans lequelle les données sur le coup sont enregistrés.
    */
   public void verifCoup (Moteur m)
   {
      j[curr].nbCoupRestant--;
      j[curr].nbBilEmpoche += m.nbBDansPoch;

      /* Si la noire a été empochée, fin de partie => verif si l'équipe a perdu */
      if (m.bNoireDansPoch)
      {
         m.t.endGame(this, verifPerdu(m.bBlancDansPoch, m.nbRebond,
                  m.firstBTouche, m.nbBDansPoch), true);
         return; /* Pour commencer la nouvelle partie en allant dans debutCoup() */
      }
      /* Si la noire a deja été empoché et que l'equipe est à la noire,
       * alors l'option pour mettre la blanche a été activé.
       * Si la blanche a été empoché lors ce de coup verif qui a gagné la partie */
      else if (this.c.equals(Color.BLACK) && m.t.noireBille.dansPoche && m.bBlancDansPoch)
      {
         m.t.endGame(this, verifPerduBlanche(m.nbRebond, m.nbBDansPoch), false);
         return; /* Pour commencer la nouvelle partie en allant dans debutCoup() */
      }
      else /* la partie continue, verif si l'équipe a commis une faute */
         verifFautes(m);

      /* Si l'equipe a rentré toutes ses billes, change la couleur de l'equipe à noire */
      if ((this.c.equals(Color.RED) && m.allRougeBDansPoch())
            || (this.c.equals(Color.YELLOW) && m.allJauneBDansPoch()))
      {
         this.c = Color.BLACK;
         final String plur = (Moteur.OPT_nbRebBilleBlanche > 1) ? "s" : " ";
         m.t.cont.fenJeu.scoreP.showMessage("La noire doit être empochée en "
               + Moteur.OPT_nbRebBilleBlanche + " bande" + plur,
               ScorePan.DELAY_MSG_INFO);
      }
   }

   /**
    * Change d"équipe pour le prochain coup.
    *
    * @param m
    *       Moteur du jeu dans lequelle les données sur le coup sont enregistrés.
    * @param nbCoup
    *       Nombre de coup pour l'adversaire. 1 si aucune faute n'a été commise
    *       mais aucune bille n'a été empochée. 2 si une faute a été commise
    *       (ex: bille blanche empochée).
    */
   public void nextEq (Moteur m, int nbCoup)
   {
      /* Si une des equipes n'est pas rouge ou jaune
       * alors la regle des 2 coup n'est pas appliqué */
      if (this.c.equals(Color.BLACK) || eqAdv.c.equals(Color.BLACK)
            || this.c.equals(Color.WHITE) || eqAdv.c.equals(Color.WHITE))
         nbCoup = 1;
      j[curr].nbCoupRestant = 0;
      nextJ();
      eqAdv.j[eqAdv.curr].nbCoupRestant = nbCoup;
      m.eq = eqAdv;
   }

   /**
    * Verifie si l'equipe qui vient d'empochée la noire
    * a perdu la partie.
    * Si la couleur de l'équipe est différente de noire, alors
    * cette équipe avait encore des billes a empoché, donc elle
    * a perdu la partie. Si la bille noire est empoché en meme temps qu'une
    * autre : perdu. Si la premiere bille touché par la bille blanche n'est
    * pas la bille noire, alors perdu.
    * De plus il faut avoir touché au moins le nombre de bande requis
    * par l'option.
    *
    * @param bBlancDansPoch
    *       true si la bille blanche a été empoché lors ce coup.
    *       Si true, alors l'équipe a perdu.
    * @param nbReb
    *       Nombre de rebond effectué lors du dernier coup,
    * @param firstBTouche
    *       Premiere bille touché lors du coup; Doit etre noire sinon faute.
    * @param nbBDansPoch
    *       Nombre de bille empoché lors du coup.
    * @return true si l'équipe a perdu la partie.
    */
   public boolean verifPerdu (boolean bBlancDansPoch, int nbReb, Bille firstBTouche, int nbBDansPoch)
   {
      return (!this.c.equals(Color.BLACK) || bBlancDansPoch || nbBDansPoch != 1
         || (nbReb < Moteur.OPT_nbRebBilleNoire) || !firstBTouche.couleur.equals(Color.BLACK));
   }

   /**
    * Verifie si l'equipe, qui vient d'empochée la blanche après que la noire
    * ait été empoché a un coup précédent, a perdu la partie.
    * Si la bille blanche est empoché en meme temps qu'une
    * autre : perdu.
    * De plus il faut avoir touché au moins le nombre de bande requis
    * par l'option.
    *
    * @param nbReb
    *       Nombre de rebond effectué lors du dernier coup,
    * @param nbBDansPoch
    *       Nombre de bille empoché lors du coup.
    * @return true si l'équipe a perdu la partie.
    */
   public boolean verifPerduBlanche (int nbReb, int nbBDansPoch)
   {
      return nbBDansPoch != 1 || (nbReb < Moteur.OPT_nbRebBilleBlanche);
   }

   /**
    * Verifie les fautes commises pendant le coup précedent.
    * Change l'equipe pour le prochain coup s'il le faut.
    * @param m
    *       Moteur du jeu dans lequelle les données sur le coup sont enregistrés.
    */
   public void verifFautes(Moteur m)
   {
      Bille b;

      /* verif si blanche a été empoché, si oui 2 coup pour l'adversaire */
      if (m.bBlancDansPoch)
      {
        j[curr].nbFaute++;
        nextEq(m, 2);
        if (this.c.equals(Color.WHITE) // Si l'equipe n'avait pas encore de couleur
              && m.nbBDansPoch > 1) // et si une autre B que la blanche a été empochée
           getColor(m.listBDansPoch);
        m.t.cont.fenJeu.scoreP.showMessage("Faute ! La bille blanche a été empochée",
              ScorePan.DELAY_MSG_FAUTE);
        return;
      }

      /* verif 1ere bille touchée par la bille blanche, si couleur definie */
      if (m.firstBTouche != null && (this.c.equals(Color.RED) || this.c.equals(Color.YELLOW)))
         if (m.firstBTouche.couleur != this.c)
         {
            j[curr].nbFaute++;
            nextEq(m, 2);
            m.t.cont.fenJeu.scoreP.showMessage("Faute ! La 1ère bille touchée n'est pas de la bonne couleur.",
                  ScorePan.DELAY_MSG_FAUTE);
            return;
         }

      /* Verif billes empochées */
      while (!m.listBDansPoch.isEmpty())
      {
         b = m.listBDansPoch.remove();
         if (!b.couleur.equals(this.c))
            if (this.c.equals(Color.WHITE)) /* Si l'equipe n'avait pas encore de couleur */
               setColor(b.couleur);
            else
            {
               j[curr].nbFaute++;
               nextEq(m, 2);
               m.t.cont.fenJeu.scoreP.showMessage("Faute ! Une bille de la mauvaise couleur a été empochée.",
                     ScorePan.DELAY_MSG_FAUTE);
               return;
            }
      }

      /* Si le joueur n'a rien empoché et rien touché => faute */
      if (m.nbBDansPoch == 0 && m.firstBTouche == null)
      {
         j[curr].nbFaute++;
         nextEq(m, 2);
         m.t.cont.fenJeu.scoreP.showMessage("Faute ! Aucune bille touchée",
               ScorePan.DELAY_MSG_FAUTE);
         return;
      }

      /* Si il n'y a pas eu de faute */
      if (m.nbBDansPoch > 0) /* Et que des billes ont été empochées : rejoue */
         j[curr].nbCoupRestant = 1;

      /* Si le joueur n'a plus de coup disponible,
       * alors c'est au tour de l'equipe adverse */
      if (j[curr].nbCoupRestant <= 0)
         nextEq(m, 1);
   }

   /**
    * Ajoute un <code>Joueur</code> dans l'équipe.
    *
    * @param pos
    *       position du Joueur dans le tableau j.
    * @param n
    *       Nom du Joueur.
    */
   public void addJ (int pos, String n)
   {
      this.j[pos] = new Joueur(n, false);
   }
   /**
    * Ajoute un <code>Joueur</code> contrôlé par l'IA dans l'équipe.
    *
    * @param pos
    *       position du Joueur dans le tableau j.
    * @param n
    *       Nom du Joueur.
    */
   public void addIA (int pos, String n)
   {
      this.j[pos] = new Joueur(n, true);
   }

   /**
    * Ajoute une équipe adverse a cette equipe.
    * Afin de pouvoir changer d'équipe facilement.
    *
    * @param eq
    *        Equipe adverse.
    */
   public void addEqAdv (Equipe eq)
   {
      this.eqAdv = eq;
   }

   /**
    * Change la couleur de l'équipe.
    * Si la nouvelle couleur est Rouge (ou Jaune) change
    * aussi la couleur de l'équipe adverse à Jaune (ou Rouge).
    *
    * @param col
    *       Nouvelle couleur de l'équipe. Rouge, Jaune, Blanc ou Noire.
    */
   public void setColor (Color col)
   {
      this.c = col;
      if (eqAdv != null)
         if (this.c.equals(Color.RED))
            eqAdv.c = Color.YELLOW;
         else if (this.c.equals(Color.YELLOW))
            eqAdv.c = Color.RED;
   }

   /**
    * Change la couleur de l'equipe par la 1ere bille de couleur
    * rencontré dans la list des billes empochées.
    * Appelé si la bille blanche a été empochée avec une autre bille
    * et que la couleur de l'equipe est blanc.
    *
    * @param l
    *       Liste des billes empochées lors du coup.
    */
   public void getColor (LinkedList<Bille> l)
   {
      Bille b;
      while (!l.isEmpty())
      {
         b = l.remove();
         if (!b.couleur.equals(this.c))
            if (this.c.equals(Color.WHITE)) /* Si l'equipe n'avait pas encore de couleur */
            {
               setColor(b.couleur);
               return;
            }
      }
   }

   /**
    * Passer au joueur suivant.
    * Postcondition: curr indique la position dans le tableau
    * du prochain <code>Joueur</code>.
    */
   public void nextJ ()
   {
      this.curr = getNextJ();
   }

   /**
    * @return la position dans le tableau du prochain <code>Joueur</code>.
    */
   private int getNextJ ()
   {
      if (curr + 1 >= j.length) return 0;
      return curr + 1;
   }

   /**
    * Convertit une <code>Equipe</code> en chaîne de caractères.
    *
    * @return string contenant des infos sur cette équipe
    * et les joueurs la composants
    */
   public String toString ()
   {
      String joueurs = "";
      for (int i = 0, c = j.length; i < c; i++)
         joueurs += "{j["+i+"]}"+j[i].toString();
      return "{"+Bille.couleurToString(this.c)+"}{Vic:"+nbVictoire+" curr:"+curr+"}"
         + joueurs;
   }

   /**
    * Initialise la couleur de l'Equipe. Utilisé au debut de la partie,
    * met les couleurs des équipes a blanc.
    */
   public void initC ()
   {
      this.c = Color.WHITE;
   }

   /**
    * Clone cette equipe.
    * @return Une copie de cette equipe.
    */
   public Equipe clone()
   {
      int l = this.j.length;
      Equipe e = new Equipe(l);
      e.c = this.c;
      e.nbVictoire = this.nbVictoire;
      for (int i = 0; i < l; i++)
         e.j[i] = this.j[i].clone();
      e.curr = this.curr;
      return e;
   }

   /**
    * Retourne l'equipe adverse de cette equipe.
    * @return Equipe représentant les adversaire de cette instance d'equipe.
    */
   public Equipe getEqAdv ()
   {
      return eqAdv;
   }
}
