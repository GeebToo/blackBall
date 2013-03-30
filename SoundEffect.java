import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

/**
 * Gére tout ce qui conserne les effets sonnore du jeu.
 * 
 * @author Jean Guibert, Romain Bressan, Thomas Hennequin-Parey
 */

/**
* Cette emum contient tous les effets sonores du jeu.
*/
public enum SoundEffect {
   SINK("sounds/sink.wav"),	/* son d'une bille qui tombe dans un trou */
   QUE("sounds/queue.wav");	/* son de la queue qui tape la bille */

   private Clip clip;

   /**
   * Contructeur qui charge le son à partir du disque dur.
   */
   SoundEffect(String soundFileName) {
      try {
    	  File dot = new File(".");
    	  
    	  String soundFileLocation = dot.getCanonicalPath() + "/" + soundFileName;
    	  File soundFile = new File( soundFileLocation );
    	  
    	  Class cl = this.getClass();
    	  
         /* emplacement du son sur le disque dur */
         URL url = this.getClass().getResource(soundFileName);

         /* Met le son dans la sortie audio */
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);

         /* génére le son */
         clip = AudioSystem.getClip();
         /* ouvre le son pour qu'il soit prêt à être lu */
         clip.open(audioInputStream);
      } 
      catch (UnsupportedAudioFileException e) { } 
      catch (IOException e) { } 
      catch (LineUnavailableException e) { }
      catch( Exception e ){ }
   }

   /**
   * Méthode qui lit le son debut le debut.
   */
   public void play() 
   {
      if (clip.isRunning())
        clip.stop();   /* arréte le son s'il est déjà en lecture */
      clip.setFramePosition(0); /* remet le son au début */
      clip.start();     /*lit le son */
   }

   /**
   * Pour optimiser le programme, cette méthde pré-charge les sons dans le progromme.
   * quand un son doit être lu, il y aura juste a appeler la methode play().
   */
   static void init() 
   {
      values();
   }
}
