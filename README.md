# ShiFuMissage

Projet Communication Synchronisée: deadline 28/05 minuit 

## Objectif 
2 utilisateurs veulent échanger des messages de façon synchronisée. Aucun des deux utilisateurs ne peut/doit connaitre le contenu du message de l'autre avant d'avoir envoyé son propre message.
 1. Chaque utilisateur envoie son message codé.
 2. A la réception d'un message codé, on envoie un "accusé de réception" 
 3. Lors qu'on a reçu le message et l'autre et l'accusé de réception de notre message, on envoie le code pour décoder le message. 
 4. A la réception du code, l'application notifie l'utilisateur et affiche le message décodé (ou le met dans la base de SMS). 