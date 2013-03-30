import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * L'IA du jeu, elle contient des methodes utilisées par le calcul
 * du meilleure coup qu'elle peut jouer.
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class IA
{
   /**
    * La moteur du jeu. Contient un thread qui deplace les billes
    * et des infos sur le coup précédent.
    */
   Moteur m;
   /**
    * Table de jeu qui contient les Billes, c'est une copie de la table que l'utilisateur vois.
    * C'est sur cette table que l'IA fait ses calcule pour trouver le meilleure coup.
    */
   public Table tableIA;
   /**
    * Abscisse de la queue de l'IA.
    */
   public float xSourisIA;
   /**
    * Ordonnée de la queue de l'IA.
    */
   public float ySourisIA;
   /**
    * Force de l'IA.
    */
   public int forceIA;
   /**
    * Nombre aléatoire qui est utilisé pour gérer la niveau de difficulté de l'IA.
    */
   public Random rand = new Random();

   /**
    * Construit une <code>IA</code>.
    *
    * @param m
    *       Le moteur du jeu.
    */
   public IA  (Moteur m)
   {
      this.m = m;
      this.tableIA = new Table(true, null);
      this.tableIA.m.calIA = true;
   }

   /**
    * Calcule toutes les possibilité de tire autour de la bille blanche
    * et détermine laquelle sera la meilleure.
    */
   public void calculMeilleureCoup()
   {
      int scoreMax = -2000,
          scoreTMP,
          force = Moteur.FORCE_MAX,
          time,
          lvl;

      if (Moteur.OPT_lvlIA == 1 && !m.firstHit)
        lvl = 5;
      else if (Moteur.OPT_lvlIA == 2 && !m.firstHit)
        lvl = 2;
      else
        lvl = 1;

      this.tableIA.initBillesIA(m.t);

      /*verifie que la bille blanche n'est pas dans un trou*/
      verification ();

      while(force > 150)
      {
        /*test toutes les trajectoires autour de la bille blanche*/
        for (int i = ((int)m.t.blancheBille.x - 10), c = ((int)m.t.blancheBille.x + 10); i < c; ++i) /*tous les x posibles*/
        {
           for (int a = -Bille.RAYON, b = Bille.RAYON; a <= b; a += (Bille.RAYON * 2)) /*-10 pour le haut & +10 pour le bas*/
           {
              time = 0;
              this.tableIA.initBillesIA(m.t);
              /*trajectoire de la bille blanche*/
              tableIA.m.bougerBilleBlanche(i,m.t.blancheBille.y + a, force);

              /*Boucle du mouvement des billes*/
              while(tableIA.m.bIsMoving() && time < 2000)
              {
                 movingIA();
                 time++;
              }

              /*Evaluation du resultat obtenue*/
              scoreTMP = evaluation();

              /*si le resultat est mieux d'avant, ce resultat devient le meilleur coup*/
              if (scoreMax < scoreTMP)
              {
                 scoreMax = scoreTMP;
                 xSourisIA = i + rand.nextInt(lvl);
                 ySourisIA = m.t.blancheBille.y + a;
                 forceIA = force;
              }
              /*reviens a la position initiale càd remet les toutes les billes dans la position qu'elle avait avant le calcule*/
              reset();
           }
        }
        for (int i = ((int)m.t.blancheBille.y - 10), c = ((int)m.t.blancheBille.y + 10); i < c; ++i)/*tous les y posibles*/
        {
           for (int a = -Bille.RAYON, b = Bille.RAYON; a <= b; a += (Bille.RAYON * 2))/*-10 pour le gauche & +10 pour le droite*/
           {
              time = 0;
              this.tableIA.initBillesIA(m.t);
              /*trajectoire de la bille blanche*/
              tableIA.m.bougerBilleBlanche(m.t.blancheBille.x + a, i, force);

              /*Boucle du mouvement des billes*/
              while(tableIA.m.bIsMoving() && time < 2000)
              {
                 movingIA();
                 time++;
              }

              /*Evaluation du resultat obtenue*/
              scoreTMP = evaluation();

              /*si le resultat est mieux d'avant, ce resultat devient le meilleur coup*/
              if (scoreMax < scoreTMP)
              {
                 scoreMax = scoreTMP;
                 xSourisIA = m.t.blancheBille.x + a;
                 ySourisIA = i + rand.nextInt(lvl);
                 forceIA = force;
              }

              /*reviens a la position initiale càd remet les toutes les billes dans la position qu'elle avait avant le calcule*/
              reset();
           }
        }
        if (lvl == 1) force -= 70;
        else force -= 100;
      }
   }

   /**
   * Calcul la valeur d'un coup.
   *
   * @return la valeur obtenue
   */
   public int evaluation()
   {
      if (m.eq.c.equals(Color.RED))
        return (evalCoupRouge() + rand.nextInt(10));
      else if (m.eq.c.equals(Color.YELLOW))
        return (evalCoupJaune() + rand.nextInt(10));
      else if (m.eq.c.equals(Color.WHITE))
        return (evalCoupBlanc() + rand.nextInt(10));
      else if (m.eq.c.equals(Color.BLACK) && m.t.noireBille.dansPoche && Moteur.OPT_EmpocheBlancheApresNoire == 1)
        return (evalCoupRentrerBlanche() + rand.nextInt(10));
      else if (m.t.eqA.c.equals(Color.BLACK) && m.t.eqB.c.equals(Color.BLACK) && m.t.noireBille.dansPoche && Moteur.OPT_EmpocheBlancheApresNoire == 2)
        return (evalCoupRentrerBlanche() + rand.nextInt(10));
      else
        return (evalCoupNoir() + rand.nextInt(10));
   }

   /**
    * Calcul la valeur d'un coup si le joueur doit rentrer les billes rouges.
    *
    * @return la valeur obtenue.
    */
   public int evalCoupRouge()
   {
      int score = 0;
      if (tableIA.noireBille.dansPoche && !m.t.noireBille.dansPoche)
         score -= 1000;

      if (tableIA.blancheBille.dansPoche)
         score -= 300;

      for (int j = 0, e = tableIA.rougeBille.length; j < e; ++j)
         if (tableIA.rougeBille[j].dansPoche && !m.t.rougeBille[j].dansPoche)
            score += 30;

      for (int j = 0, e = tableIA.jauneBille.length; j < e; ++j)
         if (tableIA.jauneBille[j].dansPoche && !m.t.jauneBille[j].dansPoche)
            score -= 40;

      if (tableIA.m.firstBTouche == null || !tableIA.m.firstBTouche.couleur.equals(Color.RED))
         score -= 50;

      return score;
   }

   /**
    * Calcul la valeur d'un coup si le joueur doit rentrer les billes jaunes.
    *
    * @return la valeur obtenue.
    */
   public int evalCoupJaune()
   {
      int score = 0;
      if (tableIA.noireBille.dansPoche && !m.t.noireBille.dansPoche)
         score -= 1000;

      if (tableIA.blancheBille.dansPoche)
         score -= 300;

      for (int j = 0, e = tableIA.rougeBille.length; j < e; ++j)
         if (tableIA.rougeBille[j].dansPoche && !m.t.rougeBille[j].dansPoche)
            score -= 40;

      for (int j = 0, e = tableIA.jauneBille.length; j < e; ++j)
         if (tableIA.jauneBille[j].dansPoche && !m.t.jauneBille[j].dansPoche)
            score += 30;

      if (tableIA.m.firstBTouche == null || !tableIA.m.firstBTouche.couleur.equals(Color.YELLOW))
         score -= 50;

      return score;
   }

   /**
    * Calcul la valeur d'un coup si le joueur doit rentrer les billes rouges ou les billes jaunes.
    *
    * @return la valeur obtenue.
    */
   public int evalCoupBlanc()
   {
      int score = 0;
      Color c = getColFirstBDansPoch();
      if (c != null)
      {
        if (tableIA.noireBille.dansPoche && !m.t.noireBille.dansPoche)
           score -= 1000;

        if (tableIA.blancheBille.dansPoche)
           score -= 300;

        for (int j = 0, e = tableIA.rougeBille.length; j < e; ++j)
           if (tableIA.rougeBille[j].dansPoche && !m.t.rougeBille[j].dansPoche)
           {
              if (c.equals(Color.RED))
                score += 20;
              else
                score -= 50;
           }

        for (int j = 0, e = tableIA.jauneBille.length; j < e; ++j)
           if (tableIA.jauneBille[j].dansPoche && !m.t.jauneBille[j].dansPoche)
           {
              if (c.equals(Color.YELLOW))
                score += 20;
              else
                score -= 50;
           }

        if (tableIA.m.firstBTouche == null)
           score -= 100;

        return score;
      }
      else
        return score;
   }

   /**
    * Calcul la valeur d'un coup si le joueur doit rentrer la bille noire
    *
    * @return la valeur obtenue.
    */
   public int evalCoupNoir()
   {
      int score = 0;
      if (tableIA.noireBille.dansPoche && !m.t.noireBille.dansPoche && m.nbRebond >= Moteur.OPT_nbRebBilleNoire)
         score += 30;

      if (tableIA.noireBille.dansPoche && !m.t.noireBille.dansPoche && m.nbRebond < Moteur.OPT_nbRebBilleNoire)
         score -= 300;

      if (tableIA.blancheBille.dansPoche)
         score -= 300;

      for (int j = 0, e = tableIA.rougeBille.length; j < e; ++j)
         if (tableIA.rougeBille[j].dansPoche && !m.t.rougeBille[j].dansPoche)
            score -= 300;

      for (int j = 0, e = tableIA.jauneBille.length; j < e; ++j)
         if (tableIA.jauneBille[j].dansPoche && !m.t.jauneBille[j].dansPoche)
            score -= 300;

      if (tableIA.m.firstBTouche == null || !tableIA.m.firstBTouche.couleur.equals(Color.BLACK))
         score -= 300;

      return score;
   }

    /**
    * Calcul la valeur d'un coup si le joueur doit rentrer la bille blanche
    *
    * @return la valeur obtenue.
    */
   public int evalCoupRentrerBlanche()
   {
      int score = 0;
      if (tableIA.blancheBille.dansPoche && !m.t.blancheBille.dansPoche && m.nbRebond >= Moteur.OPT_nbRebBilleBlanche)
         score += 30;
       
      if (tableIA.blancheBille.dansPoche && !m.t.blancheBille.dansPoche && m.nbRebond < Moteur.OPT_nbRebBilleBlanche)
         score -= 3000;

      for (int j = 0, e = tableIA.rougeBille.length; j < e; ++j)
         if (tableIA.rougeBille[j].dansPoche && !m.t.rougeBille[j].dansPoche)
            score -= 3000;

      for (int j = 0, e = tableIA.jauneBille.length; j < e; ++j)
         if (tableIA.jauneBille[j].dansPoche && !m.t.jauneBille[j].dansPoche)
            score -= 3000;

      if (tableIA.m.firstBTouche == null)
         score -= 300;

      return score;
   }

   /**
    * Pour avoir la couleur de la 1ere bille la liste
    * des billes empochées.
    * @return la couleur de la bille.
    */
   public Color getColFirstBDansPoch ()
   {
      Bille b;
      while (!tableIA.m.listBDansPoch.isEmpty())
      {
         b = tableIA.m.listBDansPoch.remove();
         return b.couleur;
      }
      return null;
   }

   /**
    * Verifi que la bille blanche n'est pas dans un trou.
    * Si c'est le cas elle la place de manière aléatoire sur la table.
    */
   public void verification ()
   {
      float xD, yD,
          xG, yG;
      if (m.bBlancDansPoch)
      {
        do
        {
           if (m.t.queue.coteTriangleCasse == 2)
           {
              xD = (float)( (int)(Math.random() * ((Table.ORI_X_T + Bille.RAYON) + 1 
                      - (Table.WIDTH/4) ) )
                      + (Table.WIDTH/4) );
              yD = (float)( (int)(Math.random() * ((Table.ORI_Y_T + Bille.RAYON) + 1 
                      - (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON) ) )
                      + (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON) );

              m.t.blancheBille.x = xD;
              m.t.blancheBille.y = yD;
              m.bBlancDansPoch = false;
              m.t.blancheBille.dansPoche = false;

              tableIA.blancheBille.x = xD;
              tableIA.blancheBille.y = yD;
              tableIA.m.bBlancDansPoch = false;
              tableIA.blancheBille.dansPoche = false;
           }
           else
           {
              xG = (float)( (int)(Math.random() * (((Table.WIDTH/4)*3) + 1 
                      - (Table.ORI_X_T+ Table.WIDTH_T - Bille.RAYON) ) )
                      + (Table.ORI_X_T+ Table.WIDTH_T - Bille.RAYON) );
              yG = (float)( (int)(Math.random() * ((Table.ORI_Y_T + Bille.RAYON) + 1 
                      - (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON) ) )
                      + (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON) );

              m.t.blancheBille.x = xG;
              m.t.blancheBille.y = yG;
              m.bBlancDansPoch = false;
              m.t.blancheBille.dansPoche = false;

              tableIA.blancheBille.x = xG;
              tableIA.blancheBille.y = yG;
              tableIA.m.bBlancDansPoch = false;
              tableIA.blancheBille.dansPoche = false;
            }
        }while(!m.t.verifCollision(m.t.blancheBille.x, m.t.blancheBille.y));
      }
      else
         return;
   }

   /**
    * Permet de réinitialiser toutes les billes
    */
   public void reset ()
   {
      m.firstBTouche = null;
      tableIA.blancheBille.initB(m.t.blancheBille);

      tableIA.noireBille.initB(m.t.noireBille);

      for (int j = 0, d = m.t.rougeBille.length; j < d; ++j)
         tableIA.rougeBille[j].initB(m.t.rougeBille[j]);

      for (int j = 0, d = m.t.jauneBille.length; j < d; ++j)
         tableIA.jauneBille[j].initB(m.t.jauneBille[j]);

      tableIA.m.listBDansPoch.clear();
   }

   /**
    * Fait bouger les billes d'un "pixel"
    */
   public void movingIA ()
   {
      for (int j = 0, d = tableIA.rougeBille.length; j < d; ++j)
         if (tableIA.rougeBille[j].isMoving())
            tableIA.rougeBille[j].bouleTraj.deplace();
      for (int j = 0, d = tableIA.jauneBille.length; j < d; ++j)
         if (tableIA.jauneBille[j].isMoving())
            tableIA.jauneBille[j].bouleTraj.deplace();
      if (tableIA.blancheBille.isMoving())
         tableIA.blancheBille.bouleTraj.deplace();
      if (tableIA.noireBille.isMoving())
         tableIA.noireBille.bouleTraj.deplace();
   }
}
