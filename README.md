# Overlay_Network

## 🏗️ Architecture du Projet
L'application repose sur plusieurs composants clés :

- **ApplicationInterface** : Interface RMI définissant les méthodes accessibles à distance.
- **Application** : Représente un nœud du réseau, stocke ses voisins et gère l'envoi/réception des messages.
- **ApplicationServer** : Enregistre les applications sur le réseau et définit leurs connexions.
- **ApplicationClient** : Permet d'envoyer un message d'une application à une autre en utilisant le routage.
- **RoutingManager** : Trouve le chemin optimal pour la transmission des messages.
- **AuthorizationManager** : Vérifie si un message respecte les règles avant d'être transmis.

## 📜 Fonctionnalités
✅ Mise en place d'un **réseau de recouvrement statique**
✅ **Envoi et réception de messages** entre applications via Java RMI
✅ **Routage intelligent** basé sur l'algorithme BFS
✅ **Filtrage des messages** en fonction des règles de sécurité
✅ **Gestion des voisins** pour assurer une transmission efficace

## ⚙️ Installation et Exécution

### 🚀 Étapes d'installation
1. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/miniarjabri/Overlay_Network.git
   cd Overlay_Network
   ```
2. **Compiler le projet** :
   ```bash
   javac *.java
   ```
3. **Démarrer le registre RMI** :
   ```bash
   start rmiregistry
   ```
4. **Lancer le serveur** :
   ```bash
   java ApplicationServer <AppName>
   ```
5. **Exécuter le client** :
   ```bash
   java ApplicationClient <sourceAppName> <destAppName> <message>
   ```

## 🚀 Améliorations Futures
- Implémentation d'une **topologie dynamique** permettant l'ajout de nouveaux nœuds.
- Optimisation du **routage et de la gestion des chemins**.
- Intégration d'un **système de logs et d'affichage en temps réel**.
- Développement d'une **interface graphique** pour gérer le réseau plus intuitivement.

## 🤝 Contributeurs
👩‍💻 **Miniar Jabri**  
👨‍💻 **Mohamed Slama**  
👩‍💻 **Asma Bahri**  

