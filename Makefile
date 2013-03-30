###############################################################################
# File:   Makefile
# Author: hennequi, guibert & bressanr
#
# Created on 06 dec 2012, 14:42
###############################################################################

JC = javac
JFLAGS = -g

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	 SoundEffect.java \
	 Joueur.java \
	 Equipe.java \
	 Sens.java \
	 Bille.java \
	 Queue.java \
	 Poches.java \
	 Table.java \
	 ScorePan.java \
	 Trajectoire.java \
	 Moteur.java \
	 IA.java \
	 Fenetre.java \
	 SelectionEquipe.java \
	 SelectionEquipeFrame.java \
	 MenuListener.java \
	 Menu.java \
	 HistTab.java \
	 History.java \
	 OptionsFrame.java \
	 Options.java \
	 Controller.java \
	 Main.java

GAMENAME = blackBall
TARNAME = ${GAMENAME}
JARNAME = ${GAMENAME}.jar
BROWSER = firefox
JDOCDIR = doc/

default: classes

classes: $(CLASSES:.java=.class)

clean :
	-rm -f *.class

mrproper : clean default

tar : clean
	-rm -Rf ${TARNAME} ${TARNAME}.tar.gz
	-mkdir ${TARNAME}
	-cp -r -t ${TARNAME} ./*.java Makefile ./img ./tmp
	-tar -cvzf ${TARNAME}.tar.gz ${TARNAME}
	-rm -Rf ${TARNAME}

test : classes
	-java Main

jar : mrproper
	-jar -cfe ${JARNAME} Main .

wc : 
	-wc *.java Makefile

doc :
	-rm -Rf ${JDOCDIR}
	-mkdir ${JDOCDIR}
	-javadoc -d ${JDOCDIR} ${CLASSES}

man :
	-${BROWSER} ${JDOCDIR}/index.html &

.PHONY: default clean mrproper tar test jar wc doc man
