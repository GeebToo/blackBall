import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

/**
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class Trajectoire implements Serializable
{
   /**
    * Abscisse du point de depart de la Trajectoire.
    */
   public float xDepart;
   /**
    * Ordonnée du point de depart de la Trajectoire.
    */
   public float yDepart;
   /**
    * Abscisse du point d'arrivée de la Trajectoire.
    */
   public float xArrive;
   /**
    * Ordonnée du point d'arrivée de la Trajectoire.
    */
   public float yArrive;
   /**
    * Coefficient directeur de la droite Trajectoire.
    */
   public float coefDir;
   /**
    * Ordonnée à l'origine de la droite Trajectoire.
    */
   public float ordo;

   public Sens sensX;
   public Sens sensY;

   /**
   * Couleur pour déssiner la Trajectoire.
   */
   public Color c;
   /**
    * Le moteur du jeu. Contient un thread qui deplace les billes
    * et des infos sur le coup précédent.
    */
   public Moteur m;
   /**
   * Bille pour laquelle la trajectoire est calculé.
   */
   public Bille b;

   /**
   * Variable pour savoir s'il faut déssiner la trajectoire.
   */
   public boolean paint;
   /**
   * Variable pour savoir si les billes sont en mouvement.
   */
   public boolean mouvement;
   /**
   * Variable pour savoir si on est dans une trajectoire null.
   */
   public boolean trajNull;
   /**
    * Contexte graphique.
    * Si la trajectoire doit être affiché.
    */
   Graphics g;

   /**
   * C'est le nombre de rebond qui doit être calculé pour cette trajectoire.
   * Utiliser uniquement quand la trajectoire est déssiner.
   */
   public int nbRebond;
   /**
   * C'est le nombre de rebond qui doit être calculé pour les billes qui entre en collision avec cette trajectoire.
   * Utiliser uniquement quand la trajectoire est déssiner.
   */
   public int nbRebBilTouche;

   /**
   * Contruit une <code>Trajectoire</code>
   *
   * utilisé quand une bille est à l'arrêt.
   */
   public Trajectoire ()  /* Trajectoire nulle */
   {
      this.trajNull = true;
   }
   /**
   * Contruit une <code>Trajectoire</code>
   *
   * Ce contructeur est appelé pour créer la trajectoire qu'une bille en mouvement va suivre.
   *
   *  @param b
   *        Bille pour laquelle la trajectoire va être calculé.
   *  @param xArrive
   *        abscisse du point d'arrivée de la Trajectoire.
   *  @param yArrive
   *        ordonnée du point d'arrivée de la Trajectoire.
   *  @param f
   *        force de la bille b.
   *  @param m
   *        La moteur du jeu.
   */
   public Trajectoire (Bille b, float xArrive, float yArrive, int f, Moteur m)
   {
      this.xDepart = b.x;
      this.yDepart = b.y;
      this.b = b;
      this.xArrive = xArrive;
      this.yArrive = yArrive;
      this.c = b.couleur;
      this.m = m;
      this.b.force = f;

      coefDir = Moteur.coefDirecteur(xDepart, yDepart, xArrive, yArrive);
      ordo = Moteur.ordOrigin(coefDir, xDepart, yDepart);
      sensX = Moteur.getSens (xDepart, xArrive);
      sensY = Moteur.getSens (yDepart, yArrive);

      this.paint = false;
      this.g = null;
      this.trajNull = false;
   }
   /**
   * Contruit une <code>Trajectoire</code>
   *
   * Ce contructeur est appelé pour créer la trajectoire qui est afficher sur la Table.
   *
   *  @param xDepart
   *        abscisse du point de départ de la Trajectoire.
   *  @param yDepart
   *        ordonnée du point de départ de la Trajectoire.
   *  @param xArrive
   *        abscisse du point d'arrivée de la Trajectoire.
   *  @param yArrive
   *        ordonnée du point d'arrivée de la Trajectoire.
   *  @param couleur
   *        couleur de la Trajectoire à déssiner.
   *  @param m
   *        La moteur du jeu.
   *  @param afficherTrajectoire
   *        variable pour savoir s'il faut déssiner la Trajectoire.
   *  @param g
   *        Contexte graphique.
   *  @param nbr
   *        nombre de rebond à calculer.
   *  @param nbrt
   *        nombre de rebond des billes touché par cette Trajectoire.
   */
   public Trajectoire (float xDepart, float yDepart, float xArrive, float yArrive,
            Color couleur, Moteur m, boolean afficherTrajectoire, Graphics g, int nbr, int nbrt)
   {
      this.xDepart = xDepart;
      this.yDepart = yDepart;
      this.xArrive = xArrive;
      this.yArrive = yArrive;
      this.c = couleur;
      this.m = m;

      coefDir = Moteur.coefDirecteur(xDepart, yDepart, xArrive, yArrive);
      ordo = Moteur.ordOrigin(coefDir, xDepart, yDepart);
      sensX = Moteur.getSens (xDepart, xArrive);
      sensY = Moteur.getSens (yDepart, yArrive);

      this.paint = afficherTrajectoire;
      this.g = g;
      this.nbRebond = nbr;
      this.nbRebBilTouche = nbrt;
      this.b = null;
      this.trajNull = false;
   }

   /**
    * Convertit la Trajectoire en String. Permet d'afficher des infos
    * a propos de celui ci dans la console.
    */
   public String toString ()
   {
      if (this.trajNull) return "{null}";
      return "{xDep:"+this.xDepart+" yDep:"+this.yDepart+ "}{xArr:"
         + this.xArrive+" yArr:"+this.yArrive+"}\n\t\t"
         + "{coefDir:"+this.coefDir+" ordo:"+this.ordo+"}";
   }

   /**
   * Méthode qui calcule les coordonnées d'arrivée de la Trajectoire.
   * Elle est utilisé uniquement pour le dessin de la Trajectoire.
   */
   public void verif ()
   {
      float xTmp = xDepart,
            yTmp = yDepart;
      boolean continuer = true;

      /* Si trajectoire == null ou plus de rebond disponible */
      if ((Sens.NULL.equals(sensX) && Sens.NULL.equals(sensY)) || nbRebond == 0)
         continuer = false;

      /* Verification de toute la trajectoire */
      while (continuer) /* tant qu'on a pas rencontré d'obstacle */
      {
         /* Calcul la prochaine position de b */
         xTmp = getNextX (xTmp);
         yTmp = getNextY (yTmp, xTmp);

         /* Verification nouvelle position */
         if (verifCollision(xTmp, yTmp))   continuer = false;
         else if (verifTrous(xTmp, yTmp))  continuer = false;
         else if (verifRebond(xTmp, yTmp)) continuer = false;
      }

      xArrive = xTmp;
      yArrive = yTmp;
   }

   /**
   * Déplace la bille selon la trajectoire et verifie la collision, les rebond, et si elle tombe dans un trou.
   * si la trajectoire est nulle : ne fais rien
   */
   public void deplace ()
   {
      if (trajNull) return;

      float xTmp = b.x,
            yTmp = b.y;

      /* Calcul vitesse de la bille en fonction de sa force */
      int vitesse = Moteur.getVitesse(this.b.force);

      do /* Deplace la bille en fonction de sa vitesse */
      {
         /* Calcul de la prochaine position de b */
         xTmp = b.bouleTraj.getNextX(xTmp, this.b.force);
         yTmp = b.bouleTraj.getNextY(yTmp, xTmp, this.b.force);

         this.b.force--;
         vitesse--;

         /* Verification nouvelle position */
         if (b.bouleTraj.verifCollision(xTmp, yTmp)) return;
         if (b.bouleTraj.verifTrous(xTmp, yTmp))     return;
         if (b.bouleTraj.verifRebond(xTmp, yTmp))  continue;

         /* Déplace la bille */
         b.setCoord(xTmp, yTmp);

      } while (vitesse > 0 && b.force > 0);

      /* Si la bille n'a plus de force (elle ne bouge plus), on anulle sa traj  */
      if (b.force <= 0)
         if (!b.bouleTraj.trajNull)
            b.bouleTraj = new Trajectoire();
   }

   /**
   * Calcul la prochaine valeur en abscisse de la trajectoire.
   * 
   *  @param oldX
   *        C'est la valeur actuelle en abscisse du départ de la Trajectoire.
   *   
   *  @return la future abscisse de départ en fonction de la Trajectoire.
   */
   public float getNextX (float oldX)
   {
      if (Math.abs(coefDir) < 1.0f)
         if (Sens.POSITIF.equals(sensX))
            return oldX + 1.0f;
         else if (Sens.NEGATIF.equals(sensX))
            return oldX - 1.0f;
         else
            return oldX;
      else
         if (sensX.isPositif())
            return oldX + (1.0f / Math.abs(coefDir));
         else
            return oldX - (1.0f / Math.abs(coefDir));
   }
   /**
   * Calcul la prochaine valeur en abscisse de la trajectoire.
   * Prend en compte le ralentissement en fonction de la force.
   * 
   *  @param oldX
   *        C'est la valeur actuelle en abscisse de la bille de la Trajectoire.
   *  @param f
   *        force de la bille.
   *   
   *  @return la future abscisse de la bille en fonction de sa Trajectoire.
   */
   public float getNextX (float oldX, int f)
   {
      if(f > Moteur.FORCE_MIN) f = (int)Moteur.FORCE_MIN;
      if (Math.abs(coefDir) < 1.0f)
         if (Sens.POSITIF.equals(sensX))
            return oldX + (f / Moteur.FORCE_MIN);
         else if (Sens.NEGATIF.equals(sensX))
            return oldX - (f / Moteur.FORCE_MIN);
         else
            return oldX;
      else
         if (sensX.isPositif())
            return oldX + ((f / Moteur.FORCE_MIN) / Math.abs(coefDir));
         else
            return oldX - ((f / Moteur.FORCE_MIN) / Math.abs(coefDir));
   }
   /**
   * Calcul la prochaine valeur en ordonnée de la trajectoire.
   * 
   *  @param oldY
   *        C'est la valeur actuelle en ordonnée du départ de la Trajectoire.
   *  @param nextX
   *        C'est la future valeur en abscisse du départ de la Trajectoire.
   *   
   *  @return la future ordonnée de départ en fonction de la Trajectoire.
   */
   public float getNextY (float oldY, float nextX)
   {
      if (coefDir == 0.0f) /* Si la droite et verticale ou horizontale */
      {
         if (Sens.POSITIF.equals(sensY))
            return oldY + 1.0f;
         else if (Sens.NEGATIF.equals(sensY))
            return oldY - 1.0f;
         //else if (Sens.NULL.equals(sensX))
         //   break; /* si Depart === Arrive (traj NULL) alors on arrete */
         else
            return oldY;
      }
      else /* y = a*x + b */
         return nextX*coefDir + ordo;
   }
   /**
   * Calcul la prochaine valeur en ordonnée de la trajectoire.
   * Prend en compte le ralentissement en fonction de la force.
   * 
   *  @param oldX
   *        C'est la valeur actuelle en abscisse de la bille de la Trajectoire.
   *  @param f
   *        force de la bille.
   *  @param nextX
   *        C'est la future valeur en abscisse de la bille de la Trajectoire.
   *   
   *  @return la future ordonnée de la bille en fonction de sa Trajectoire.
   */
   public float getNextY (float oldY, float nextX, int f)
   {
      if (f > Moteur.FORCE_MIN) f = (int)Moteur.FORCE_MIN;
      if (coefDir == 0.0f) /* Si la droite et verticale ou horizontale */
      {
         if (Sens.POSITIF.equals(sensY))
            return oldY + (f / Moteur.FORCE_MIN);
         else if (Sens.NEGATIF.equals(sensY))
            return oldY - (f / Moteur.FORCE_MIN);
         //else if (Sens.NULL.equals(sensX))
         //   break; /* si Depart === Arrive (traj NULL) alors on arrete */
         else
            return oldY;
      }
      else /* y = a*x + b */
         return nextX*coefDir + ordo;
   }
   /**
   * Calcul la précédente valeur en abscisse de la trajectoire.
   *
   *  @param nextX
   *        C'est la future valeur en abscisse du départ de la Trajectoire.
   *   
   *  @return la précédente abscisse de départ en fonction de la Trajectoire.
   */
   public float getPrevX (float nextX)
   {
      if (Math.abs(coefDir) < 1.0f)
         if (Sens.POSITIF.equals(sensX))
            return nextX - 1.0f;
         else if (Sens.NEGATIF.equals(sensX))
            return nextX + 1.0f;
         else
            return nextX;
      else
         if (sensX.isPositif())
            return nextX - (1.0f / Math.abs(coefDir));
         else
            return nextX + (1.0f / Math.abs(coefDir));
   }
   /**
   * Calcul la précédente valeur en ordonnée de la trajectoire.
   *
   *  @param nextY
   *        C'est la future valeur en ordonnée du départ de la Trajectoire.
   *  @param prevX
   *        C'est la précedente valeur en abscisse du départ de la Trajectoire.
   *   
   *  @return la précédente ordonnée de départ en fonction de la Trajectoire.
   */
   public float getPrevY (float nextY, float prevX)
   {
      if (coefDir == 0.0f) /* Si la droite et verticale ou horizontale */
      {
         if (Sens.POSITIF.equals(sensY))
            return nextY - 1.0f;
         else if (Sens.NEGATIF.equals(sensY))
            return nextY + 1.0f;
         //else if (Sens.NULL.equals(sensX))
         //   break; /* si Depart === Arrive (traj NULL) alors on arrete */
         else
            return nextY;
      }
      else /* y = a*x + b */
         return prevX*coefDir + ordo;
   }


   /**
   * Vérfie si la bille qui va en (x, y) rencontre une autre bille.
   *
   *  @param x
   *        abscisse du point a vérifier.
   *  @param y
   *        ordonnée du point à vérifier.
   *
   *  @return vrai s'il y a collision sinon faux.
   */
   public boolean verifCollision (float x, float y)
   {
      /* Billes rouges */
      for (int j = 0, c = m.t.rougeBille.length; j < c; ++j)
         if (!m.t.rougeBille[j].touche && m.t.rougeBille[j].isOverlap(x, y))
            if (m.t.rougeBille[j].isCollision(x, y))
               if (paint || this.b.x != m.t.rougeBille[j].x || this.b.y != m.t.rougeBille[j].y)
               {
                  getCollision(x, y, m.t.rougeBille[j]);
                  return true;
               }

      /* Billes jaunes */
      for (int j = 0, c = m.t.jauneBille.length; j < c; ++j)
         if (!m.t.jauneBille[j].touche && m.t.jauneBille[j].isOverlap(x, y))
            if (m.t.jauneBille[j].isCollision(x, y))
               if (paint || this.b.x != m.t.jauneBille[j].x || this.b.y != m.t.jauneBille[j].y)
               {
                  getCollision(x, y, m.t.jauneBille[j]);
                  return true;
               }

      /* Bille noire */
      if (!m.t.noireBille.touche && m.t.noireBille.isOverlap(x, y))
         if (m.t.noireBille.isCollision(x, y))
               if (paint || this.b.x != m.t.noireBille.x || this.b.y != m.t.noireBille.y)
               {
                  getCollision(x, y, m.t.noireBille);
                  return true;
               }

      /* Bille blanche */
      if (!m.t.blancheBille.touche && m.t.blancheBille.isOverlap(x, y))
         if (m.t.blancheBille.isCollision(x, y))
            if (paint || this.b.x != m.t.blancheBille.x || this.b.y != m.t.blancheBille.y)
            {
               getCollision(x, y, m.t.blancheBille);
               return true;
            }

      return false;
   }

   /**
   * Modifie les trajectoires des la bille d'attaque (x, y) et de la bille visée (bVisee).
   *
   *  @param x
   *        abscisse de la bille d'attaque.
   *  @param y
   *        ordonnée de la bille d'attaque.
   *  @param bVisee
   *        bille avec laquelle il y a eu collision
   *
   */
   public void getCollision (float x, float y, Bille bVisee)
   {
      if (paint)
      {
         bVisee.touche = true;
         x = getPrevX(x);
         y = getPrevY(y, x);
      }

      /* Calcul destination de la bille visee, symetrie du centre
       * de la bille d'attaque par rapport au centre de la bille rouge */
      float xDestBilVisee = Moteur.symCentrale(bVisee.x, Moteur.symCentrale(x, bVisee.x)),
            yDestBilVisee = Moteur.symCentrale(bVisee.y, Moteur.symCentrale(y, bVisee.y));

      /* Trajectoire de la bille visée */
      if (paint)/*trajectoire*/
      {
         Trajectoire traj = new Trajectoire (bVisee.x, bVisee.y,
            xDestBilVisee, yDestBilVisee, bVisee.couleur, m, paint, g,
            nbRebBilTouche, (nbRebBilTouche-1));
         traj.verif();
         traj.paint(); 
      }
      else/*mouvement*/
         bVisee.bouleTraj = new Trajectoire (bVisee, xDestBilVisee,
               yDestBilVisee, bVisee.force, m);

      if (paint)
      { /* Dessin bille d'attaque au moment du contact */
         g.setColor(this.c);
         g.drawOval((int) x-Bille.RAYON, (int) y-Bille.RAYON,
               2*Bille.RAYON, 2*Bille.RAYON);
      }

      /* Trajectoire de la bille d'attaque après contact */
      getTrajApresColl(x, y, xDestBilVisee, yDestBilVisee, bVisee);

      /* verif si c'est la 1ere bille touchée par la bille blanche */
      if (!paint && m.firstBTouche == null && b == m.t.blancheBille)
         m.firstBTouche = bVisee;
   }

   /**
   * Modifie la trajectoires de la bille d'attaque après collisison.
   *
   *  @param xNewDepart
   *        destination en abscisse de la bille d'attaque.
   *  @param yNewDepart
   *        destination en ordonnée de la bille d'attaque.
   *  @param xDestBilVisee
   *        destination en de la bille visée.
   *  @param yDestBilVisee
   *        destination en ordonnée de la bille visée.
   *  @param bVisee
   *        bille avec laquelle il y a eu collision
   *
   */
   public void getTrajApresColl (float xNewDepart, float yNewDepart,
                         float xDestBilVisee, float yDestBilVisee, Bille bVisee)
   {
      float xNewArrive, yNewArrive;

      /* Calcul du sinus de l'angle au niveau de la collision */
      float sin = Moteur.sinCollision(this.xDepart, this.yDepart,
            xDestBilVisee, yDestBilVisee, xNewDepart, yNewDepart);

      /* Calcul new Arrive */
      if (sin < 0.1 && sin > -0.1) /* Si le choc est pleine bille, on arrete */
      {
         // /!\ Fait tout buggué
         // if (!paint)
            // b.bouleTraj = new Trajectoire();
      }
      else
      {
         /* Rotation dont le centre est la bille d'attaque au moment du contact (C)
          * de 90 degres du point de destination de la bille visée */
         xNewArrive = Moteur.rotationX(xDestBilVisee, yDestBilVisee,
               xNewDepart, yNewDepart, (int) Math.signum(sin));
         yNewArrive = Moteur.rotationY(xDestBilVisee, yDestBilVisee,
               xNewDepart, yNewDepart, (int) Math.signum(sin));

         /* Trajectoire apres collision */
         if (paint)
         {
            Trajectoire traj = new Trajectoire (xNewDepart, yNewDepart,
                  xNewArrive, yNewArrive, this.c, m, paint, g,
                  (nbRebond-1), nbRebBilTouche);
            traj.verif();
            traj.paint(); 
         }
         else
            b.bouleTraj = new Trajectoire (b, xNewArrive,
                  yNewArrive, b.force, m);
      }

      /* Gestion forces apres chocs */
      if (!paint) getForceApresColl(this.b, bVisee, Math.abs(sin));
   }

   /**
   * Modifie la force de la bille de d'attaque et de la bille visée aprec collision.
   * sinChoc doit être compris entre 0 et 1
   *
   *  @param bDeChoc
   *        bille d'attaque.
   *  @param bVisee
   *        bille visee
   *  @param sinChoc
   *        sin de l'angle de choc
   *
   */
   public void getForceApresColl (Bille bDeChoc, Bille bVisee, float sinChoc)
   {
      int newF = (int) (bDeChoc.force * sinChoc);
      bVisee.force += (int) (bDeChoc.force - newF);
      bDeChoc.force = newF;
      if (bVisee.force <= 0) bVisee.bouleTraj = new Trajectoire();
      if (bDeChoc.force <= 0) bDeChoc.bouleTraj = new Trajectoire();
   }

   /**
   * Verifie si la bille qui va en (x,y) rencontre un trou.
   *
   *  @param x
   *        abscisse du point à vérifier
   *  @param y
   *        ordonnée du point à vérifier
   *
   *  @return vrai si elle recontre un trou, faux sinon 
   */
   public boolean verifTrous (float x, float y)
   {
      int numPoche = m.t.poch.inHole(x, y);
      if (numPoche == -1) return false;
      else
         if (paint) m.t.poch.paintBilleDansPoches(g, c, numPoche);
         else
         {
            /* Déplace la bille */
            b.setCoord(x, y);
            b.bouleTraj = new Trajectoire (b,
                  m.t.poch.xCentre[numPoche],
                  m.t.poch.yCentre[numPoche],
                  (int)Moteur.FORCE_MIN, m);

            putBilleInHole();
         }
      return true;
   }

   /**
   * Verifie si la bille qui va en (x,y) rencontre une bande.
   * Et si oui, calcul la nouvelle trajectoire.
   *
   *  @param x
   *        abscisse du point à vérifier
   *  @param y
   *        ordonnée du point à vérifier
   *
   *  @return vrai si elle rencontre une bande, faux sinon.
   *
   */
   public boolean verifRebond (float x, float y)
   {
      if (y < (Table.ORI_Y_T + Bille.RAYON)
            || y > (Table.ORI_Y_T + Table.HEIGHT_T - Bille.RAYON))
      { /* Si rebond sur bande haut ou bas */
         /* Trajectoire apres rebond */
         if (paint)
         {
            final float xPrev = getPrevX(x),
                        yPrev = getPrevY(y, xPrev);
            Trajectoire traj = new Trajectoire (xPrev, yPrev, Moteur.symCentrale(xDepart, xPrev), yDepart,
                  this.c, m, paint, g, (nbRebond-1), nbRebBilTouche);
            traj.verif();
            traj.paint();
         }
         else
         {
            b.bouleTraj = new Trajectoire (b, Moteur.symCentrale(xDepart, x),
                  yDepart, b.force, m);
            m.nbRebond++;
         }

         return true;
      }
      else if (x < (Table.ORI_X_T + Bille.RAYON)
            || x > (Table.ORI_X_T + Table.WIDTH_T - Bille.RAYON))
      { /* Si rebond sur bande gauche ou droite */
         /* Trajectoire apres rebond */
         if (paint)
         {
            final float xPrev = getPrevX(x),
                        yPrev = getPrevY(y, xPrev);
            Trajectoire traj = new Trajectoire (xPrev, yPrev, xDepart, Moteur.symCentrale(yDepart, yPrev),
                  this.c, m, paint, g, (nbRebond-1), nbRebBilTouche);
            traj.verif();
            traj.paint();
         }
         else
         {
            b.bouleTraj = new Trajectoire (b, xDepart,
                  Moteur.symCentrale(yDepart, y), b.force, m);
            m.nbRebond++;
         }

         return true;
      }
      else /* Pas de rebond */
         return false;
   }

   /**
   * Dessin de la trajectoire 
   *
   */
   public void paint ()
   {
      if (!this.paint) return;
      this.g.setColor(this.c);
      this.g.drawLine((int) this.xDepart, (int) this.yDepart,
            (int) this.xArrive, (int) this.yArrive);
   }
   /* Forcer dessin */
   public void paint (Graphics g)
   {
      this.g.setColor(this.c);
      this.g.drawLine((int) this.xDepart, (int) this.yDepart,
            (int) this.xArrive, (int) this.yArrive);
   }

   /**
    * Deplace une bille entrée dans le trous vers la zone de score.
    * Si une bille est entrée dans le troue, appele la methode
    * de poche pour mettre la bille dans le zone de score.
    */
   public void putBilleInHole ()
   {
      if (this.b.isInHole(xArrive, yArrive))
      {
         if (!m.calIA)
            this.m.t.poch.putBilleInHole (this.b, this.m);
         else /*si l'IA calcul on modifie juste les variables de b*/
         {
            this.b.force = 0;
            this.b.dansPoche = true;
            this.b.bouleTraj = new Trajectoire();
            m.listBDansPoch.add (b);
            m.nbBDansPoch++;
         }
      }
   }
}
