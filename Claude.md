# Best Félix — prompt pour Claude Code

Tu vas construire **Best Félix**, une application Android native et personnelle qui m'aide à gérer ma vie : mes habitudes, mes priorités pro, mon épargne, mes finances et mes notes. Je suis développeur, je travaille dans Android Studio et je relirai ton code. Lis tout ce document avant d'écrire la moindre ligne.

## Contexte

- Je vis à Bobo-Dioulasso (Burkina Faso). La monnaie est le **franc CFA (FCFA)**, sans décimales, formaté `125 000 FCFA`.
- Toute l'interface est en **français**.
- L'appli doit marcher **100 % hors ligne**. Pas de compte, pas de serveur, pas d'analytics.
- C'est une MVP : je veux une appli qui marche et que j'ai envie d'ouvrir chaque matin, pas une démo.

## Stack imposée

- Kotlin, Jetpack Compose, Material 3 (thème clair et sombre, dynamic color désactivé pour garder mon identité visuelle).
- Architecture MVVM : `ui/` (écrans + ViewModels), `domain/` (logique pure), `data/` (Room + repositories).
- Room pour le stockage, avec des migrations propres (pas de `fallbackToDestructiveMigration`).
- Hilt pour l'injection de dépendances.
- Navigation Compose avec une barre de navigation en bas.
- Kotlin Coroutines + Flow, `kotlinx.serialization` pour l'export JSON.
- `minSdk 26`, `targetSdk` = la dernière version stable.
- Toute la logique de calcul (XP, séries, pénalités, finances) vit dans `domain/` en **fonctions pures, sans dépendance Android**, et est couverte par des tests unitaires JUnit.

## Navigation : 5 onglets

1. **Aujourd'hui** (écran d'accueil)
2. **Habitudes**
3. **Projets**
4. **Finances**
5. **Notes**

---

## 1. Habitudes à prendre — système de récompense (style Duolingo)

Modèle : nom, « pourquoi c'est important » (optionnel), date de création, jours validés, jours sauvés par un joker, nombre de jokers en stock.

Règles :
- Je valide une habitude pour aujourd'hui en un tap (gros bouton rond). Je peux aussi corriger les 6 jours précédents via une ligne de 7 pastilles (L M M J V S D).
- **Série** = jours consécutifs validés ou sauvés par un joker, en comptant depuis aujourd'hui (ou depuis hier si aujourd'hui n'est pas encore validé).
- **XP** : chaque jour validé rapporte `10 × multiplicateur`, avec `multiplicateur = min(3, 1 + floor(série_à_ce_jour / 7) × 0,5)`. Un jour sauvé par un joker garde la série mais ne rapporte pas d'XP.
- **Journée parfaite** : si toutes les habitudes existantes ce jour-là sont validées, +20 XP bonus.
- **Niveaux** : il faut `50 × n × (n − 1)` XP cumulés pour atteindre le niveau `n` (niveau 2 = 100 XP, niveau 3 = 300, niveau 4 = 600…).
- **Jokers** : chaque fois qu'une série atteint un multiple de 7 jours, +1 joker (maximum 2 en stock, un seul gain par jour). Au lancement de l'appli, si hier n'est pas validé mais avant-hier l'est, et qu'il reste un joker, il est consommé automatiquement pour sauver hier.
- **Badges** de record de série : 3, 7, 21, 30, 66 et 100 jours.
- Les XP se **recalculent toujours à partir de l'historique** (aucun compteur incrémental stocké), pour que corriger un jour passé donne un résultat juste.
- Retour visuel à chaque validation : animation sur le bouton, snackbar « +15 XP », vibration légère.

## 2. Habitudes à abandonner — système de douleur

Modèle : nom, date de début du compteur, liste des rechutes (date + note optionnelle).

Règles, calculées jour par jour depuis la date de début. Le solde de chaque habitude est en **XP**, séparé des XP de récompense de la partie 1 :
- Chaque jour **tenu** (terminé sans rechute) rapporte **+5 XP**. Aujourd'hui ne compte qu'une fois la journée finie.
- Chaque jour **non tenu** (au moins une rechute ce jour-là) efface **−35 XP**, soit l'équivalent de 7 jours d'effort. Plusieurs rechutes le même jour comptent pour un seul jour non tenu.
- Mets ces deux valeurs (5 et 35) dans des constantes nommées de `domain/`, pas en dur dans l'UI.
- Le solde peut devenir **négatif** : affiche-le alors en rouge comme une **dette** (« Dette : 35 XP à rembourser »).
- **Meilleur solde atteint** : le maximum historique du solde, recalculé depuis l'historique.

Affichage de chaque habitude :
- Solde en XP en gros (vert si positif, rouge si négatif), avec le meilleur solde atteint juste en dessous.
- Courbe du solde dans le temps (Canvas Compose), avec une ligne de zéro en pointillés et le dernier point mis en avant. Les chutes doivent se voir.
- Jours tenus d'affilée et nombre de jours non tenus.
- Un **tout petit bouton icône « i »** (ou « ? ») à côté du titre. Il ouvre une explication courte de la règle : « +5 XP par jour tenu. −35 XP par jour non tenu, soit 7 jours d'effort effacés. »
- Bouton rouge « J'ai rechuté ». Il ouvre une bottom sheet qui affiche **d'abord** l'avertissement, puis demande la date et une note. Il faut confirmer. L'avertissement contient :
    - « Rechuter revient à saccager 7 jours d'efforts, soit 35 XP. »
    - Le solde final si je confirme (« Ton solde passera de 120 XP à 85 XP »).
    - Le temps qu'il faudra pour revenir au meilleur solde jamais atteint : `ceil((meilleur − solde_après) / 5)` jours tenus (« Il te faudra 19 jours sans rechute pour revenir à ton record de 180 XP »). Si le solde après reste au-dessus du record, n'affiche pas cette ligne.
- Si une note est saisie, crée aussi une note dans la rubrique « Habitudes à abandonner ».
- Si la rechute tombe un jour déjà non tenu, dis-le dans la sheet (« Ce jour est déjà compté comme non tenu : pas de pénalité en plus ») au lieu d'afficher −35 XP.

## 3. Projets pro et épargne (onglet Projets)

**Projets** : titre, entreprise ou contexte (ex. JDS, PrimeTech, client), « je suis en cours sur… » (la tâche du moment), priorité (haute / moyenne / basse), statut (en cours / en pause / terminé), échéance optionnelle.
- Tri par priorité puis par échéance. Filtre « actifs » / « terminés ».
- Bande de couleur selon la priorité, et une pastille « En retard » si l'échéance est dépassée.

**Objectifs d'épargne** : titre, pour quoi (entreprise, perso…), montant cible en FCFA, échéance optionnelle, liste de versements (date, montant).
- Barre de progression, montant épargné / cible, pourcentage.
- Si une échéance existe, affiche le montant à mettre de côté par mois pour la tenir.
- Bouton « + Versement ».

## 4. Finances

- **Résumé** en trois tuiles : entrées du mois en cours, total que je dois, total qu'on me doit.
- **Crédits** : sens (« Je dois » / « On me doit »), qui, montant, motif, date limite, remboursements partiels (date, montant). Affiche le reste à payer, « Soldé » quand c'est fini, et « En retard » si la date est dépassée. Bouton « Rembourser » ou « Reçu ».
- **Entrées** : montant, date, source. Liste groupée par mois avec le total du mois.

## 5. Bloc-notes

- Chaque note a une rubrique parmi : Habitudes à prendre, Habitudes à abandonner, Finances, Projets pro, Général. Elle a aussi un titre optionnel, un corps, une date de création et une date de modification.
- Filtre par rubrique avec des chips, et le nombre de notes par rubrique.
- Chaque section des autres onglets a un lien « Notes (n) » qui ouvre le bloc-notes filtré sur sa rubrique.
- Recherche plein texte dans les notes.

## Écran « Aujourd'hui »

C'est l'écran du matin : tout ce qui compte, sur un seul écran.
- En-tête : « Salut Félix », la date du jour en toutes lettres, un anneau de progression (habitudes validées / total), le niveau et une barre d'XP.
- Liste compacte des habitudes à valider, avec validation directe.
- Habitudes à abandonner en version compacte : solde en XP, jours tenus d'affilée, meilleur solde.
- Les 3 projets en cours les plus prioritaires, avec leur tâche du moment.
- Les objectifs d'épargne.
- Le résumé des finances.
- Au tout premier lancement : un écran d'accueil qui explique les 5 blocs, avec un bouton « Charger des exemples » (données préfixées « Exemple · ») et un bouton « Partir de zéro ».

## Rappels

- Un rappel quotidien configurable (heure par défaut 6 h 00) : « X habitudes à valider aujourd'hui ».
- Un rappel le soir (21 h 00 par défaut) s'il reste des habitudes non validées, pour protéger la série.
- WorkManager ou AlarmManager, avec la permission `POST_NOTIFICATIONS` demandée proprement sur Android 13+.
- Les deux rappels peuvent être désactivés dans un petit écran Réglages (accessible depuis l'en-tête).

## Sauvegarde (obligatoire)

Les données ne vivent que sur le téléphone, et une désinstallation les efface. Ajoute donc dans Réglages :
- **Exporter** toutes les données en un fichier JSON versionné, via le Storage Access Framework (je choisis où l'enregistrer).
- **Importer** ce fichier, en remplaçant les données après une confirmation explicite.

## Design

- Identité : indigo pour la structure, **or/ambre pour la récompense** (XP, séries, validations), **rouge pour la douleur** (rechutes, dettes, retards), vert pour les réussites (crédit soldé, score positif).
- Un thème clair et un thème sombre soignés, sans simple inversion des couleurs.
- Les chiffres en chiffres tabulaires. Les montants toujours en FCFA avec une espace fine pour les milliers.
- Toutes les créations et modifications passent par des bottom sheets. La suppression se fait depuis la sheet, avec une confirmation.
- Des états vides utiles, qui disent quoi faire, à la place des écrans blancs.
- Accessibilité : tailles tactiles d'au moins 48 dp, `contentDescription` sur les icônes, respect de la taille de police système.

---

## Comment je veux que tu travailles

1. **Commence par un plan** : arborescence des packages, entités Room et leurs relations, liste des écrans, ordre de construction. Montre-le-moi et attends ma validation avant de coder.
2. **Construis par étapes**, et vérifie que le projet compile à chaque fin d'étape :
    1. Squelette : Gradle, Hilt, Room, thème, navigation 5 onglets.
    2. `domain/` : les fonctions pures et leurs **tests unitaires**. Teste au minimum : la série avec et sans joker, les XP avec multiplicateur et bonus de journée parfaite, les niveaux, le solde d'une habitude à abandonner (+5 / −35, plusieurs rechutes le même jour, dette), le meilleur solde atteint, le nombre de jours pour revenir au record, le reste à payer d'un crédit, l'épargne mensuelle nécessaire.
    3. Habitudes à prendre.
    4. Habitudes à abandonner, avec la courbe et l'avertissement de rechute.
    5. Projets et épargne.
    6. Finances.
    7. Notes.
    8. Écran Aujourd'hui.
    9. Rappels.
    10. Export / import.
3. Après chaque étape, résume en 3 lignes ce qui est fait et ce que je dois tester sur mon téléphone.
4. Ne rajoute pas de fonctionnalités que je n'ai pas demandées. Si une règle te paraît ambiguë ou mauvaise, dis-le franchement au lieu d'inventer.
5. Gère les dates avec `java.time.LocalDate` dans le fuseau du téléphone. Aucun calcul de jours avec des millisecondes.