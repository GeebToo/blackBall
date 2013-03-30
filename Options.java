import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Panneau de la fenetre d'option (<code>OptionsFrame</code>). Contient
 * une dizaine de panneau (correspond à une option) qui ont chacun un label
 * et un combobox pour modifier l'option.
 */
public class Options extends JPanel implements ActionListener
{
   /* JComboBox de chacune des options */
   public JComboBox<String> lvlIA;
   public JComboBox<String> coteDeb;
   public JComboBox<String> eqStart;
   public JComboBox<String> nbBandeBnoire;
   public JComboBox<String> empochBlanche;
   public JComboBox<String> nbBandeBblanche;
   public JComboBox<String> changeCote;
   public JComboBox<String> rbBBlanche;
   public JComboBox<String> rbBTouche;
   public JComboBox<String> sound;
   public JComboBox<String> save;

   /**
    * Image de fond du panel de modif les options.
    */
   public Image fondOptions;

   /* Tableau de valeurs pour les JComboBox */
   private String[] tabLvlIA = {"Facile", "Moyen", "Difficile"};
   private String[] tabSens = {"Triangle à Gauche", "Triangle à Droite", "Aléatoire"};
   private String[] tabEqStart = {"Aléatoire", "Perdant", "Gagnant"};
   private String[] tabInt0to10 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
   private String[] tabChangeCote = {"Non", "Oui", "Aléatoire"};
   private String[] tabEmpocheBlanche = {"Jamais", "Toujours", "Si plus de bille de couleur"};
   private String[] tabOnOff = {"Activer", "Désactiver"};

   /**
    * Constructeur de Options.
    */
   public Options()
   {
      this.setBackground(Color.BLACK);
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

      try // Charge l'image de fond
      {
         fondOptions = ImageIO.read(getClass().getResourceAsStream("img/fondOptions.png"));
      }
      catch(IOException e) { }

      JPanel panOpt = new JPanel();
      panOpt.setLayout(new BoxLayout(panOpt, BoxLayout.PAGE_AXIS));
      panOpt.setOpaque(false);
      this.add(Box.createVerticalStrut(130));
      this.add(panOpt);

      this.createComboBox(panOpt);
      this.initOptions();
   }

   /**
    * Initialise les options. Selectionne le champ de chaque JComboBox qui
    * correspond au variables de Moteur.
    */
   public void initOptions ()
   {
      lvlIA.setSelectedIndex(Moteur.OPT_lvlIA - 1);
      coteDeb.setSelectedIndex(Moteur.OPT_coteDeb);
      eqStart.setSelectedIndex(Moteur.OPT_EqQuiCommencePartie);
      nbBandeBnoire.setSelectedIndex(Moteur.OPT_nbRebBilleNoire);
      empochBlanche.setSelectedIndex(Moteur.OPT_EmpocheBlancheApresNoire);
      nbBandeBblanche.setSelectedIndex(Moteur.OPT_nbRebBilleBlanche);
      changeCote.setSelectedIndex(Moteur.OPT_changeCote);
      rbBBlanche.setSelectedIndex(Moteur.OPT_nbRebBilleBlanche);
      rbBTouche.setSelectedIndex(Moteur.OPT_nbRebBilTouche);
      sound.setSelectedIndex(Moteur.OPT_sound);
      save.setSelectedIndex(Moteur.OPT_save);
   }

   /**
    * Méthode appelé lorsqu'une option a été modifié par l'utilisateur.
    * @param e
    *       ActionEvent pour savoir quelle option a été modifié.
    */
   public void actionPerformed(ActionEvent e)
   {
      final Object source = e.getSource();
      if (source == lvlIA)
         setLvlIA();
      else if (source == coteDeb)
         setCoteDeb();
      else if (source == eqStart)
         setEqStart();
      else if (source == nbBandeBnoire)
         setNbBandeBnoire();
      else if (source == empochBlanche)
         setEmpochBlanche();
      else if (source == nbBandeBblanche)
         setNbBandeBblanche();
      else if (source == changeCote)
         setChangeCote();
      else if (source == rbBBlanche)
         setRbBBlanche();
      else if (source == rbBTouche)
         setRbBTouche();
      else if (source == sound)
         setSound();
      else if (source == save)
         setSave();
   }

   /**
    * Met à jour la difficulté de l'IA en fonction du choix de l'utilisateur.
    */
   public void setLvlIA ()
   {
      /* recup option choisie, (ajoute 1 car option def entre 1 et 3) */
      final int o = lvlIA.getSelectedIndex() + 1;
      if (o >= 1 && o <= 3)
         Moteur.OPT_lvlIA = o;
   }

   /**
    * Met à jour le sens de jeu au depart en fonction du choix de l'utilisateur.
    */
   public void setCoteDeb ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = coteDeb.getSelectedIndex();
      Moteur.OPT_coteDeb = o;
   }

   /**
    * Met à jour l'équipe débutant les partie suivante.
    */
   public void setEqStart ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = eqStart.getSelectedIndex();
      Moteur.OPT_coteDeb = o;
   }

   /**
    * Met à jour nombre minimum de bandes qu'il faut faire pour
    * empoché la noire sans commetre de fautre à la fin d'une partie.
    */
   public void setNbBandeBnoire ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = nbBandeBnoire.getSelectedIndex();
      Moteur.OPT_nbRebBilleNoire = o;
   }

   /**
    * Met à jour l'option qui permet d'empocher la blanche pour gagner
    * la partie apres la noire.
    */
   public void setEmpochBlanche ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = empochBlanche.getSelectedIndex();
      Moteur.OPT_EmpocheBlancheApresNoire = o;
   }

   /**
    * Met à jour nombre minimum de bandes qu'il faut faire pour
    * empoché la blanche sans commetre de fautre à la fin d'une partie.
    * Si l'option a été activé.
    */
   public void setNbBandeBblanche ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = nbBandeBblanche.getSelectedIndex();
      Moteur.OPT_nbRebBilleBlanche = o;
   }

   /**
    * Met à jour s'il faut changer de coté à chaque partie.
    */
   public void setChangeCote ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = changeCote.getSelectedIndex();
      Moteur.OPT_changeCote = o;
   }

   /**
    * Met à jour le nombre de rebonds de la bille blanche affichés.
    */
   public void setRbBBlanche ()
   {
      /* On recupere l'option choisie, si elle est valable on modif la var. de Moteur */
      final int o = rbBBlanche.getSelectedIndex();
      if (o >= 0 && o <= 10)  // verif input
         Moteur.OPT_nbRebond = o;
   }

   /**
    * Met à jour le nombre de rebonds des billes touchées.
    */
   public void setRbBTouche ()
   {
      /* On recupere l'option choisie, si elle est valable on modif la var. de Moteur */
      final int o = rbBTouche.getSelectedIndex();
      if (o >= 0 && o <= 10)  // verif input
         Moteur.OPT_nbRebBilTouche = o;
   }

   /**
    * Met à jour l'option sonore.
    */
   public void setSound ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = sound.getSelectedIndex();
      Moteur.OPT_sound = o;
   }

   /**
    * Met à jour l'option de sauvegarde automatique.
    */
   public void setSave ()
   {
      /* recup option choisie, puis modif le var. de Moteur. */
      final int o = save.getSelectedIndex();
      Moteur.OPT_save = o;
   }

   /**
    * Affiche le fond du menu option.
    * @param g
    *       Contexte graphique.
    */
   public void paintComponent (Graphics g)
   {
      g.drawImage(fondOptions, 0, 0, this);
   }

   /**
    * Creation des JComboBox pour pouvoir modif les options.
    * Chaque JComboBox est accompagné d'un JLabel, et les deux sont ajoutés
    * à un panel qui est ensuite ajouté au panel qui contient tous les panels d'options.
    *
    * @param panOpt
    *       panel qui va contenir tous les panels d'options (dans lesquels se trouvent
    *       les JComboBox).
    */
   public void createComboBox (JPanel panOpt)
   {
      JPanel pan;
      JLabel lab;

      /* Ajout JComboBox Difficulte IA */
      lvlIA = new JComboBox<String>(tabLvlIA);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Difficulté de l'ordinateur");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(lvlIA);
      /* Modif JComboBox */
      lvlIA.setEditable(false);
      lvlIA.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox Sens de départ */
      coteDeb = new JComboBox<String>(tabSens);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Sens de la Table");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(coteDeb);
      /* Modif JComboBox */
      coteDeb.setEditable(false);
      coteDeb.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox pour le choix de l'equipe qui debute la partie suivante */
      eqStart = new JComboBox<String>(tabEqStart);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Equipe qui débute la partie suivante");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(eqStart);
      /* Modif JComboBox */
      eqStart.setEditable(false);
      eqStart.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox Nombre de bandes pour empocher la noire */
      nbBandeBnoire = new JComboBox<String>(tabInt0to10);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Nombre de bandes pour empocher la noire");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(nbBandeBnoire);
      /* Modif JComboBox */
      nbBandeBnoire.setEditable(true);
      nbBandeBnoire.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox s'il faut empocher la blanche après la noire */
      empochBlanche = new JComboBox<String>(tabEmpocheBlanche);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("La blanche doit être empochée après la noire");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(empochBlanche);
      /* Modif JComboBox */
      empochBlanche.setEditable(false);
      empochBlanche.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox Nombre de bandes pour empocher la blanche */
      nbBandeBblanche = new JComboBox<String>(tabInt0to10);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Nombre de bandes pour empocher la blanche");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(nbBandeBblanche);
      /* Modif JComboBox */
      nbBandeBblanche.setEditable(true);
      nbBandeBblanche.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox s'il faut changer de cote en debut de partie */
      changeCote = new JComboBox<String>(tabChangeCote);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Changement de coté en début de partie");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(changeCote);
      /* Modif JComboBox */
      changeCote.setEditable(false);
      changeCote.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox pour le nombre de rebonds de la bille Blanche affichés */
      rbBBlanche = new JComboBox<String>(tabInt0to10);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Rebonds de la bille Blanche affichés");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(rbBBlanche);
      /* Modif JComboBox */
      rbBBlanche.setEditable(true);
      rbBBlanche.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox pour le nombre de rebonds des billes touchées */
      rbBTouche = new JComboBox<String>(tabInt0to10);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Rebonds des billes touchées affichés");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(rbBTouche);
      /* Modif JComboBox */
      rbBTouche.setEditable(true);
      rbBTouche.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox pour désactiver les effets sonores */
      sound = new JComboBox<String>(tabOnOff);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Effets sonores");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(sound);
      /* Modif JComboBox */
      sound.setEditable(false);
      sound.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);

      /* Ajout JComboBox pour désactiver les sauvegardes */
      save = new JComboBox<String>(tabOnOff);
      /* Creation du panel de l'option (va contenir JComboBox + JLabel) */
      pan = new JPanel();
      pan.setOpaque(false);
      /* Ajoute label */
      lab = new JLabel("Sauvegarde");
      lab.setForeground(Color.WHITE);
      pan.add(lab);
      pan.add(save);
      /* Modif JComboBox */
      save.setEditable(false);
      save.addActionListener(this);
      /* Ajout au panneau qui contient tous les panneaux d'option */
      panOpt.add(pan);
   }
}
