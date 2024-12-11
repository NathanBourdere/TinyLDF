# TinyLDF : Serveur de Fragments de Données Liées

## Contexte

Wikidata propose un serveur de **Fragments de Données Liées** (LDF) accessible à l'adresse [Wikidata LDF](https://query.wikidata.org/bigdata/ldf). Ce serveur permet de traiter des requêtes basées sur des motifs triples et de renvoyer les résultats en RDF sous forme de pages. 

Un serveur LDF permet de traiter des requêtes SPARQL à l'aide de clients intelligents, comme [Comunica](https://query.comunica.dev/), qui peut être testé en direct. Le but de ce projet est de développer un serveur simple de Fragments de Données Liées capable de gérer des **requêtes basées sur des motifs de quadruplets**, tout en étant déployé sur **Google Cloud** sous forme d'application App Engine.

## Les données utilisées

Les données utilisées proviennent de trois sources différentes :
- [Données biomédicales](https://download.bio2rdf.org/#/current/clinicaltrials/)
- [Digital education ressources](https://github.com/HeardLibrary/digital-scholarship/blob/master/data/rdf/vandy/vandy-triples.csv)
- [Dataset sur les accidents corporels de la route en France](https://www.data.gouv.fr/fr/datasets/bases-de-donnees-annuelles-des-accidents-corporels-de-la-circulation-routiere-annees-de-2005-a-2023/)
- [Dataset sur les aéroports, compagnies aériennes et routes](https://www.kaggle.com/datasets/ahmadrafiee/airports-airlines-planes-and-routes-update-2024?select=airports.csv)
