import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * class ScorePan: JPanel qui affiche des infos a propos des equipes,
 * du joueurs actuelles et des messages en bas de l'écran.
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class ScorePan extends JPanel
{
   /**
    * Temps d'affichage des messages de fautes. Utilisé pour showMessage().
    */
   public static int DELAY_MSG_FAUTE = 16;
   /**
    * Temps d'affichage des messages informatifs. Utilisé pour showMessage().
    */
   public static int DELAY_MSG_INFO = 32;
   /**
    * Temps d'affichage des messages informatifs. Utilisé pour showMessage().
    */
   public static int DELAY_MSG_VIC = 50;
   /**
    * Image du haut de la fenetre, utilisé pour affcihé des infos
    * sur les équipes
    */
   public Image scoreFond;

   /**
    * Image de bille rouge pour afficher le nombre de coup restant.
    */
   public Image imgBr;
   /**
    * Image de bille jaune pour afficher le nombre de coup restant.
    */
   public Image imgBj;
   /**
    * Image de bille blanche pour afficher le nombre de coup restant.
    */
   public Image imgBb;
   /**
    * Image de bille noire pour afficher le nombre de coup restant.
    */
   public Image imgBn;

   /**
    * Label pour afficher le nom du joueur actuel de l'equipe A.
    */
   public JLabel nomJEqA;
   /**
    * Label pour afficher le nom du joueur actuel de l'equipe B.
    */
   public JLabel nomJEqB;

   /**
    * Message affcihé pour donner des infos aux joueurs à certain moment.
    * Fin de partie, faute commise.
    */
   public JLabel message;

   /**
    * Ordonnée du centre des billes affichées pour indiquer
    * le nombre de coup restant.
    */
   public static int yCoupRest = 20;
   /**
    * Abscisse du centre de la 1ere bille affichée pour indiquer
    * le nombre de coup restant de l'equipe A.
    */
   public static int xCoupRestEqA_1 = 140;
   /**
    * Abscisse du centre de la 2eme bille affichée pour indiquer
    * le nombre de coup restant de l'equipe A.
    */
   public static int xCoupRestEqA_2 = ScorePan.xCoupRestEqA_1 + 30;
   /**
    * Abscisse du centre de la 1ere bille affichée pour indiquer
    * le nombre de coup restant de l'equipe B.
    */
   public static int xCoupRestEqB_1 = 630;
   /**
    * Abscisse du centre de la 2eme bille affichée pour indiquer
    * le nombre de coup restant de l'equipe B.
    */
   public static int xCoupRestEqB_2 = ScorePan.xCoupRestEqB_1 - 30;

   /**
    * Table de jeu qui permet d'avoir des infos sur la partie en cours
    * et les affcihées.
    */
   public Table tablePan;

   /**
    * Construit un ScorePan, Jpanel servant a afficher des infos sur
    * la partie actuelle dans la partie haute de la fenetre.
    */
   public ScorePan()
   {
      /* Chargement image */
      try {
         scoreFond = ImageIO.read(getClass().getResourceAsStream("img/fondScore.png"));
         imgBr = ImageIO.read(getClass().getResourceAsStream("img/imgBilleRouge.png"));
         imgBj = ImageIO.read(getClass().getResourceAsStream("img/imgBilleJaune.png"));
         imgBn = ImageIO.read(getClass().getResourceAsStream("img/imgBilleNoire.png"));
         imgBb = ImageIO.read(getClass().getResourceAsStream("img/imgBilleBlanche.png"));
      }
      catch(IOException e) { }

      this.setLayout(null);

      /* Creation label */
      nomJEqA = new JLabel();
      nomJEqB = new JLabel();
      add(nomJEqA);
      add(nomJEqB);
      nomJEqA.setBounds(100, 60, 100, 20);
      nomJEqB.setBounds(580, 60, 100, 20);

      message = new JLabel();
      add(message);
      message.setBounds(230, 45, 350, 40);
   }

   /**
    * Ajoute la table au panneau de score, pour pouvoir recuperer
    * les infos de la partie en cours. (et ensuite les afficher)
    * @param t
    *       Table qui contient les billes, et les equipes de la partie.
    */
   public void addTable(Table t)
   {
      tablePan = t;
   }

   /**
    * Méthode d'affichage du panel. Affiche le fond et diverse infos sur la partie.
    */
   public void paintComponent(Graphics g)
   {
      g.drawImage(scoreFond, 0, 0, this);
      paintBillesSorties(g);
      paintCoupRestant(g);
      getNomJ();
      /* Image bouton pour afficher l'aide. */
      g.drawImage(tablePan.cont.imgHelp.getImage(), 5, 5, this);
   }

   /**
    * Methode qui affiche les billes qui ont été empochées.
    */
   public void paintBillesSorties(Graphics g)
   {
      for (int i = 0, c = tablePan.NB_BILLE; i < c; ++i)
      {
         if (tablePan.rougeBille[i].dansPoche)
            tablePan.rougeBille[i].paint(g);
         if (tablePan.jauneBille[i].dansPoche)
            tablePan.jauneBille[i].paint(g);
      }
      if (tablePan.blancheBille.dansPoche)
         tablePan.blancheBille.paint(g);
      if (tablePan.noireBille.dansPoche)
         tablePan.noireBille.paint(g);
   }

   /**
    * Recupere les noms des joueurs en train de jouer et modifie
    * les JLabel en consequence.
    */
   public void getNomJ ()
   {
      nomJEqA.setText(tablePan.eqA.j[tablePan.eqA.curr].nom);
      nomJEqB.setText(tablePan.eqB.j[tablePan.eqB.curr].nom);
   }

   /**
    * Afficher le nombre de coup de restant. Affiche une ou deux billes
    * a cote de l'equipe qui joue. Les billes sont de la couleur de l'equipe.
    */
   public void paintCoupRestant (Graphics g)
   {
      final int nbCoup = tablePan.getNbCoupRestant();
      if (tablePan.isEqAPlaying())
      {
         if (nbCoup == 0) return;
         g.drawImage(getImg(), ScorePan.xCoupRestEqA_1, ScorePan.yCoupRest, this);
         if (nbCoup == 2)
            g.drawImage(getImg(), ScorePan.xCoupRestEqA_2, ScorePan.yCoupRest, this);
      }
      else /* si c'est au tour de l'equipe B */
      {
         if (nbCoup == 0) return;
         g.drawImage(getImg(), ScorePan.xCoupRestEqB_1, ScorePan.yCoupRest, this);
         if (nbCoup == 2)
            g.drawImage(getImg(), ScorePan.xCoupRestEqB_2, ScorePan.yCoupRest, this);
      }
   }

   /**
    * @return Image de bille qui correspond à la couleur de l'équipe
    * qui est en train de jouer.
    */
   private Image getImg ()
   {
      if (tablePan.m.eq.c.equals(Color.RED))
         return imgBr;
      else if (tablePan.m.eq.c.equals(Color.YELLOW))
         return imgBj;
      else if (tablePan.m.eq.c.equals(Color.BLACK))
         return imgBn;
      else
         return imgBb;
   }

   /**
    * Affiche un message pendant un certain temps.
    * @param s
    *       message a afficher.
    * @param delay
    *       temps pendant lequel est affiché le message en seconde.
    *       si 0 sera affcihé jusqu'à ce qu'un autre message le remplace.
    */
   public void showMessage (String s, int delay)
   {
      message.setText("<html><div style=\"text-align: center;\">"
            + s + "</html>");
      if (delay > 0)
         new Timer(delay * 1000, new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  message.setText("");
               }
            }
         ).start();
   }
}
