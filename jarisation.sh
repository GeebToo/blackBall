#!/bin/bash
JARNAME=blackBall.jar
JARDIR=jar
rm -Rf $JARDIR
mkdir $JARDIR

# Compilation
make mrproper
cp -r -t $JARDIR ./*.class ./sounds ./img
cd $JARDIR
# optimisation des images
cd img
pngnq -vf -s1 *.png
renamexm --yes '-s/-nq8.png$/.png/r' *.png
optipng -o7 *.png
rm -Rf *nq8.png
cd ..
# Creation de l'archive .jar
jar -cfe $JARNAME Main .
cd ..
mv $JARDIR/$JARNAME .
rm -Rf $JARDIR
