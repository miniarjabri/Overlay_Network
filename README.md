# Version 3
# Overlay_Network
## Étape 3 : Mise en place des applications de
diffusion/réception de contenu
Cette étape concerne la mise en place des applications de diffusion/réception de contenu. Chaque application
cible doit désormais pouvoir envoyer un contenu à tous les autres applications cibles ou recevoir un message
court envoyé depuis une autre application cible
## dernieres remarques du prof: 
```shell
- routing manager: faire un algorithme qui determine le chemin Dijskstra
- aithorizationManager: l'intégrer dans les autres applications.
- utiliser une topologie plus complexe ou il y a une redondance de chemin pour voir comment il détérmine le meilleur chemin avec l'algo Dijsktra
```
 ✓ c'est fait 
 
## à faire ...
```shell
au lieu d'utiliser 3 Apps déja pretes, on configure un fichier topologie.json qui contient 6 Apps (si on veut changer la topologie, on change juse ce fichier et pas les autres fichiers) tout se fait de maniere dynamique
```

 ✓ c'est fait 

## comment ça marche: 
compiler le projet :
```shell
javac -cp ".;lib/json-20250107.jar" *.java
```
lancer le serveur pour chaque application
```shell
java -cp ".;lib/json-20250107.jar" ApplicationServer App1
java -cp ".;lib/json-20250107.jar" ApplicationServer App2
java -cp ".;lib/json-20250107.jar" ApplicationServer App3
java -cp ".;lib/json-20250107.jar" ApplicationServer App4
java -cp ".;lib/json-20250107.jar" ApplicationServer App5
java -cp ".;lib/json-20250107.jar" ApplicationServer App6
```shell
tester l'envoie d'un message:  => ENA MATEMCHILICH ***probléme***
```shell
java -cp ".;lib/json-20250107.jar" ApplicationClient App1 App3 "Message de test"
```



