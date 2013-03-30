import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Panel de la fenetre de selection des equipes. Contient les infos necessaires
 * à la creation des équipes (nombre de joueurs, nom des jouers et sont-ils controlé
 * par l'IA ou un humain).
 *
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */
public class SelectionEquipe extends JPanel implements ActionListener
{
   /* Gestion de Selection des joueurs et noms */
   /**
    * Tableau de champs texte contenant les noms des joueurs de l'équipe A.
    */
   public ArrayList<JTextField> nEqA;
   /**
    * Tableau de champs texte contenant les noms des joueurs de l'équipe B.
    */
   public ArrayList<JTextField> nEqB;

   /**
    * Tableau de cases à cocher des joueurs de l'équipe A. Si la case
    * d'un joueur est coché alors ce joueur est controlé par l'IA.
    */
   public ArrayList<JCheckBox> iaEqA;
   /**
    * Tableau de cases à cocher des joueurs de l'équipe B. Si la case
    * d'un joueur est coché alors ce joueur est controlé par l'IA.
    */
   public ArrayList<JCheckBox> iaEqB;

   /**
    * Tableau contenant les panel representant les champs
    * de chaque joueur de l'equipe A.
    */
   public ArrayList<JPanel> panJoueursEqA;
   /**
    * Tableau contenant les panel representant les champs
    * de chaque joueur de l'equipe B.
    */
   public ArrayList<JPanel> panJoueursEqB;

   /**
    * Image de la fenetre de selection des équipes.
    */
   private Image fondSelection;
   /**
    * Icone d'aide. Image affiché sur chaque fenetre pour afficher l'aide,
    * et dans chaque boite de dialogue d'aide.
    */
   private Image imgHelp;

   /* Boutons Joueur */
   /**
    * Bouton pour ajouter un joueur à l'equipe A.
    */
   private JButton butAddJA;
   /**
    * Bouton pour ajouter un joueur à l'equipe B.
    */
   private JButton butAddJB;
   /**
    * Bouton pour enlever un joueur à l'equipe A.
    */
   private JButton butRmJA;
   /**
    * Bouton pour enlever un joueur à l'equipe B.
    */
   private JButton butRmJB;

   /* Panels */
   /**
    * Panel contenant les champs relatif à l'equipe A.
    */
   private JPanel panEqA;
   /**
    * Panel contenant les champs relatif à l'equipe B.
    */
   private JPanel panEqB;
   /**
    * Panel contenant les boutons enlever et ajouter joueur de l'equipe A.
    */
   private JPanel panBasEqA;
   /**
    * Panel contenant les boutons enlever et ajouter joueur de l'equipe B.
    */
   private JPanel panBasEqB;

   /**
    * Faut-il afficher l'effet autour de Nouvelle partie.
    */
   public boolean paintEffetNewPart;

   /**
    * Image de l'effet afficher autour de nouvelle partie. Afficher seulement
    * quand nouvelle partie est survolé par la souris.
    */
   public Image imgEffetLancer;

   /**
    * Constructeur de SelectionEquipe, initialise le panel
    * avec le minimum requis pour jouer : deux équipes de 1 joueur.
    */
   public SelectionEquipe()
   {
      this.setBackground(SelectionEquipeFrame.colFond);
      this.setLayout(new GridBagLayout());

      /* Place 4 panel dans le panel principal à l'aide de GridBagLayout */
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.insets = new Insets(0,20,10,0);
      gbc.anchor = GridBagConstraints.LAST_LINE_START;
      this.add(panBasEqA = new JPanel(), gbc);

      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 1;
      gbc.weighty = 0.1;
      gbc.insets = new Insets(0,0,10,20);
      gbc.anchor = GridBagConstraints.LAST_LINE_END;
      this.add(panBasEqB = new JPanel(), gbc);

      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.ipadx = 170;
      gbc.ipady = 400;
      gbc.insets = new Insets(120,10,50,10);
      // gbc.gridheight = 10;
      gbc.anchor = GridBagConstraints.LINE_START;
      /* Add scroll bar */
      JScrollPane scrollPane = new JScrollPane(panEqA = new JPanel());
      JPanel panAvecScroll = new JPanel();
      panAvecScroll.setLayout(new BorderLayout());
      panAvecScroll.add(scrollPane);
      panAvecScroll.setOpaque(false);
      scrollPane.setOpaque(false);
      scrollPane.getViewport().setOpaque(false);
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBorder(null);
      this.add(panAvecScroll, gbc);

      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.ipadx = 170;
      gbc.ipady = 400;
      gbc.insets = new Insets(120,10,50,10);
      // gbc.gridheight = 10;
      gbc.anchor = GridBagConstraints.LINE_END;
      /* Add scroll bar */
      scrollPane = new JScrollPane(panEqB = new JPanel());
      panAvecScroll = new JPanel();
      panAvecScroll.setLayout(new BorderLayout());
      panAvecScroll.add(scrollPane);
      panAvecScroll.setOpaque(false);
      scrollPane.setOpaque(false);
      scrollPane.getViewport().setOpaque(false);
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBorder(null);
      this.add(panAvecScroll, gbc);

      panBasEqA.setOpaque(false);
      panBasEqB.setOpaque(false);
      panEqA.setOpaque(false);
      panEqB.setOpaque(false);
      panEqA.setLayout(new BoxLayout(panEqA, BoxLayout.PAGE_AXIS));
      panEqB.setLayout(new BoxLayout(panEqB, BoxLayout.PAGE_AXIS));

      /* Creation des listes pour chaque champ de Joueur */
      this.nEqA = new ArrayList<JTextField>();
      this.nEqB = new ArrayList<JTextField>();
      this.iaEqA = new ArrayList<JCheckBox>();
      this.iaEqB = new ArrayList<JCheckBox>();
      this.panJoueursEqA = new ArrayList<JPanel>();
      this.panJoueursEqB = new ArrayList<JPanel>();

      /* Ajoute un joueur dans chaque équipe qui ne sont pas enlevable,
       * car c'est le minimum pour jouer */
      addJoueurEqA();
      addJoueurEqB();

      /* Ajouter les boutons Ajouter et Enlever un joueeur pour chaque équipe */
      butAddJA = new JButton("+");
      butAddJA.setToolTipText("Ajouter un joueur à l'équipe A.");
      panBasEqA.add(butAddJA);
      butAddJA.addActionListener(this);

      butRmJA = new JButton("-");
      butRmJA.setToolTipText("Enlever un joueur à l'équipe A.");
      panBasEqA.add(butRmJA);
      butRmJA.addActionListener(this);

      butAddJB = new JButton("+");
      butAddJB.setToolTipText("Ajouter un joueur à l'équipe B.");
      panBasEqB.add(butAddJB);
      butAddJB.addActionListener(this);

      butRmJB = new JButton("-");
      butRmJB.setToolTipText("Enlever un joueur à l'équipe B.");
      panBasEqB.add(butRmJB);
      butRmJB.addActionListener(this);

      /* Charge l'image de fond */
      try
      {
         fondSelection = ImageIO.read(getClass().getResourceAsStream("img/fondSelection.png"));
         imgEffetLancer = ImageIO.read(getClass().getResourceAsStream("img/effetLancer.png"));
         /* Chargement Image d'aide */
         imgHelp = ImageIO.read(getClass().getResourceAsStream("img/imgAide.png"));
      }
      catch(IOException e) { }
   }

   /**
    * Methode appelé lorsqu'un des boutons est actionné. Appel la methode
    * correspondant au bouton.
    * @param e
    *       Evenement lié à l'action.
    */
   public void actionPerformed(ActionEvent e)
   {
      Object source = e.getSource();
      if (source == butAddJA)
         addJoueurEqA();
      else if (source == butAddJB)
         addJoueurEqB();
      else if (source == butRmJA)
         rmJoueurEqA();
      else if (source == butRmJB)
         rmJoueurEqB();
   }

   /**
    * Ajoute un champ joueur à l'équipe A.
    */
   public void addJoueurEqA ()
   {
      /* Création des nouveaux objets avec valeur par defaut */
      JTextField tf = new JTextField("Joueur"+(nEqA.size()+nEqB.size()+1));
      JCheckBox cb = new JCheckBox();
      JPanel pan = new JPanel();

      /* On ajoute les nouveaux objets aux listes */
      nEqA.add(tf);
      iaEqA.add(cb);
      panJoueursEqA.add(pan);

      /* On ajoute les champs (nom + IA) au panel */
      pan.add(tf);
      pan.add(cb);
      pan.setOpaque(false);

      /* Puis on ajoute le panel du joueur au panel de l'équipe A */
      panEqA.add(pan);

      /* TF style */
      tf.setColumns(10);
      tf.setBackground(SelectionEquipeFrame.colFond);
      tf.setForeground(Color.WHITE);
      tf.setMargin(new Insets(2, 5, 2, 5));

      cb.setToolTipText("Cocher pour que le joueur soit controllé par l'IA.");
      cb.setOpaque(false);
      // super.revalidate();
      super.invalidate();
      super.validate();
   }

   /**
    * Ajoute un champ joueur à l'équipe B.
    */
   public void addJoueurEqB ()
   {
      /* Création des nouveaux objets avec valeur par defaut */
      JTextField tf = new JTextField("Joueur"+(nEqA.size()+nEqB.size()+1));
      JCheckBox cb = new JCheckBox();
      JPanel pan = new JPanel();

      /* On ajoute les nouveaux objets aux listes */
      nEqB.add(tf);
      iaEqB.add(cb);
      panJoueursEqB.add(pan);

      /* On ajoute les champs (nom + IA) au panel */
      pan.add(tf);
      pan.add(cb);
      pan.setOpaque(false);

      /* Puis on ajoute le panel du joueur au panel de l'équipe A */
      panEqB.add(pan);

      /* TF style */
      tf.setColumns(10);
      tf.setBackground(SelectionEquipeFrame.colFond);
      tf.setForeground(Color.WHITE);
      tf.setMargin(new Insets(2, 5, 2, 5));

      cb.setToolTipText("Cocher pour que le joueur soit controllé par l'IA.");
      cb.setOpaque(false);
      // super.revalidate();
      this.invalidate();
      this.validate();
   }

   /**
    * Enleve un champ joueur à l'équipe A. S'il y en a plus d'un.
    */
   public void rmJoueurEqA ()
   {
      final int last = panJoueursEqA.size() - 1;
      final JPanel lastPan = panJoueursEqA.get(last);
      if (last <= 0) return;

      /* On enleve le panneau */
      panEqA.remove(lastPan);
      /* On enleve les champs (nom + IA) au panel */
      lastPan.remove(iaEqA.get(last));
      lastPan.remove(nEqA.get(last));

      /* On enleve les objets des listes */
      panJoueursEqA.remove(last);
      iaEqA.remove(last);
      nEqA.remove(last);
      // super.revalidate();
      super.invalidate();
      super.validate();
   }

   /**
    * Enleve un champ joueur à l'équipe B. S'il y en a plus d'un.
    */
   public void rmJoueurEqB ()
   {
      final int last = panJoueursEqB.size() - 1;
      final JPanel lastPan = panJoueursEqB.get(last);
      if (last <= 0) return;

      /* On enleve le panneau */
      panEqB.remove(lastPan);
      /* On enleve les champs (nom + IA) au panel */
      lastPan.remove(iaEqB.get(last));
      lastPan.remove(nEqB.get(last));

      /* On enleve les objets des listes */
      panJoueursEqB.remove(last);
      iaEqB.remove(last);
      nEqB.remove(last);
      // super.revalidate();
      super.invalidate();
      super.validate();
   }

   /**
    * Obtenir l'équipe A et les joueurs la composant en fonction des infos
    * contenue dans ce panel. Appelé quand le joueur a valider le choix des équipes.
    * @return Equipe représentant l'equipe B, pret a etre ajouter à la table.
    */
   public Equipe getEqA()
   {
      /* Equipe A */
      final int nbJ = panJoueursEqA.size();
      Equipe eqA = new Equipe(nbJ);
      for(int i = 0; i < nbJ; ++i)
         if (iaEqA.get(i).isSelected())
            eqA.addIA(i, nEqA.get(i).getText());
         else
            eqA.addJ(i, nEqA.get(i).getText());
      return eqA;
   }

   /**
    * Obtenir l'équipe B et les joueurs la composant en fonction des infos
    * contenue dans ce panel. Appelé quand le joueur a valider le choix des équipes.
    * @return Equipe représentant l'equipe B, pret a etre ajouter à la table.
    */
   public Equipe getEqB()
   {
      /* Equipe B */
      final int nbJ = panJoueursEqB.size();
      Equipe eqB = new Equipe(nbJ);
      for(int i = 0; i < nbJ; ++i)
         if (iaEqB.get(i).isSelected())
            eqB.addIA(i, nEqB.get(i).getText());
         else
            eqB.addJ(i, nEqB.get(i).getText());
      return eqB;
   }

   /**
    * Methode d'affiche du panel de selection des équipes.
    * Affiche le fond.
    * @param g
    *       Contexte graphique.
    */
   public void paintComponent (Graphics g)
   {
      g.drawImage(fondSelection, 0, -20, this);
      g.drawImage(imgHelp, 10, 10, this);
      if (paintEffetNewPart)
         g.drawImage(imgEffetLancer, 255, 575, this);
   }
}
