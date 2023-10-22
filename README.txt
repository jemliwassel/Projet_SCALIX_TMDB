# Projet Scala pour l'API TMDB

Ce projet Scala a été créé pour interagir avec l'API TMDB (The Movie Database) afin de rechercher des informations sur les acteurs, les films et les réalisateurs.

## Configuration

1. Assurez-vous d'avoir [SBT](https://www.scala-sbt.org/) installé sur votre système.
2. Obtenez une clé d'API TMDB en vous inscrivant sur [TMDB](https://www.themoviedb.org/). Remplacez la clé API existante dans le fichier `MovieAPI.scala` par votre propre clé.
3. Créez un répertoire `data` dans le projet pour stocker les fichiers de cache.


## Fonctionnalités
Ce projet propose les fonctionnalités suivantes :

1. Recherche d'un acteur par nom et prénom.
2. Récupération de la liste des films dans lesquels un acteur a joué.
3. Recherche du réalisateur d'un film par son ID.

## Architecture du Projet

L'une des décisions clés dans la conception de ce projet a été l'utilisation de `case class` pour modéliser les données récupérées de l'API TMDB. Cette approche a été choisie pour plusieurs raisons :

1. **Clarté et Lisibilité du Code** : Les `case class` sont idéales pour représenter des données simples et immuables. Elles permettent de définir la structure des données de manière concise et lisible, ce qui rend le code plus compréhensible pour les développeurs.

2. **Immutabilité des Données** : Les `case class` sont immuables par défaut, ce qui signifie que les données stockées ne peuvent pas être modifiées une fois qu'elles ont été initialisées. Cela garantit la stabilité des données tout au long de leur cycle de vie, évitant ainsi les effets de bord indésirables.

3. **Interopérabilité avec la Désérialisation JSON** : Scala offre des bibliothèques de désérialisation JSON efficaces, qui fonctionnent parfaitement avec les `case class`. Ces bibliothèques permettent de convertir facilement les réponses JSON de l'API en instances de `case class`, simplifiant ainsi le processus de récupération des données.

4. **Facilité d'Évolution** : Si les données de l'API évoluent ou si de nouvelles fonctionnalités sont ajoutées, il est plus facile d'ajuster les `case class` pour refléter ces changements sans affecter de manière significative le reste du code.




