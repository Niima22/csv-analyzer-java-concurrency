# Analyseur CSV avec Programmation Concurrente en Java

Ce projet illustre l'utilisation de la **programmation concurrente en Java** à travers une application Spring Boot permettant le traitement efficace de fichiers CSV volumineux. L'objectif principal est de démontrer comment l'exécution parallèle de tâches peut améliorer les performances dans le traitement de données en masse.

Projet réalisé dans le cadre du **Master IDLD** – Module : Programmation Concurrente

Par :

**Niima Bettaoui**

**Nouhaila El Ouafi**

---

## Objectifs du projet

* Téléversement de fichiers CSV via une interface web conviviale
* Détection automatique des en-têtes (titres de colonnes)
* Traitement parallèle des lignes du fichier CSV à l’aide de `ExecutorService`
* Possibilité de filtrer les résultats :

  * Sélection de colonnes
  * Application de conditions (commence par, contient, supérieur à…)
  * Choix du nombre de lignes à afficher
* Réinitialisation facile des filtres pour afficher toutes les données
* Traitement fluide même pour les fichiers volumineux

---

## Pourquoi la programmation concurrente ?

Dans un traitement séquentiel classique, les lignes d’un fichier CSV sont traitées une par une sur un seul thread, ce qui peut ralentir considérablement le système.

Dans ce projet :

* Chaque ligne est traitée de manière indépendante en tant que tâche
* Ces tâches sont exécutées en parallèle via un pool de threads (`ExecutorService`)
* Résultat : gain significatif de temps et meilleure utilisation des ressources disponibles

Ce projet constitue ainsi une démonstration pratique des bénéfices de la **concurrence contrôlée**, notamment dans les traitements I/O intensifs.

---

## Outils de Programmation Concurrente Utilisés

Ce projet met en œuvre plusieurs composants de la bibliothèque de concurrence de Java pour assurer un traitement parallèle performant et fiable :

| Outil                | Description                                                                                                          |
| -------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `ExecutorService`    | Gestion d’un pool de threads pour soumettre plusieurs tâches simultanément.                                          |
| `Callable`           | Interface permettant aux tâches de retourner des résultats, contrairement à `Runnable`.                              |
| `Future`             | Représente le résultat d’une tâche asynchrone ; permet de collecter les résultats une fois les traitements terminés. |
| Exécution parallèle  | Les lignes du fichier sont traitées sans blocage, augmentant la réactivité du système.                               |
| Sécurité des threads | Chaque tâche travaille indépendamment, évitant ainsi les conflits liés aux accès concurrents.                        |

---

## Interface Web
![csv](https://github.com/user-attachments/assets/72a56be3-9d3f-48c4-adb4-67e8db419dc0)


L'application web permet :

* L’upload d’un fichier CSV
* La sélection des colonnes à afficher
* L’application de conditions sur les données (ex : commence par une lettre, supérieur à une valeur…)
* Le choix du nombre de lignes à afficher
* La réinitialisation des filtres

L’interface est responsive, simple à utiliser, et adaptée à une démonstration pédagogique.

---

## Exécution locale

```bash
# Cloner le dépôt
git clone https://github.com/Niima22/csv-analyzer-java-concurrency.git
cd csv-analyzer-java-concurrency

# Lancer le serveur Spring Boot
mvn spring-boot:run
```

Accéder à l’application :
[http://localhost:8080](http://localhost:8080)
