# Overlay_Network
# Pour tester:
exécuter "java OverlayNetwork" dans le terminal 
### le résultat: 
```shell
PS C:\Users\pc\Desktop\java> java OverlayNetwork
>> 
Applications enregistrées sur RMI.
Connexion établie entre App1 et App2
Connexion établie entre App2 et App3
Connexion établie entre App3 et App1
App1 => App2 : Hello, App2!
App2 a reçu un message de App1 : Hello, App2!
App2 => App3 : Hello, App3!
App3 a reçu un message de App2 : Hello, App3!
App3 => App1 : Hello, App1!
App1 a reçu un message de App3 : Hello, App1!
```
## à changer / remarques prof:
1. il faut pouvoir lancer plusieurs app et chaque app choisit d'envoyer un message à x app.
2. dans la classe application, definir ça "rebind"     Naming.rebind("rmi://localhost/App1", app1);
3. dans la classe overlaynetwork, définir "ApplicationInterface remoteApp1 = (ApplicationInterface) Naming.lookup("rmi://localhost/App1");"
