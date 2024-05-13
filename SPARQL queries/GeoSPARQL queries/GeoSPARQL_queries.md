
# Query 1

## The works that mention places located in France
https://api.triplydb.com/s/6Dq-DZ_lG

```
PREFIX : <https://imagoarchive.it/ontology/>
PREFIX wd: <http://www.wikidata.org/entity/>
PREFIX geo: <http://www.opengis.net/ont/geosparql#>
PREFIX ecrm: <http://erlangen-crm.org/200717/>
PREFIX ilrm: <http://imagoarchive.it/ilrmoo/>
PREFIX osm: <https://www.openstreetmap.org/>
PREFIX osm2rdfkey: <https://osm2rdf.cs.uni-freiburg.de/rdf/key#>
PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 

SELECT DISTINCT ?title ?authorName ?toponymName ?wktPlace
  FROM <https://geosparql.isti.cnr.it/fuseki/imago/archive>
WHERE {
  ?exp_cre a ilrm:F28_Expression_Creation ;
  	         ilrm:R17_created ?work ;
  	         ecrm:P14_carried_out_by ?author .	
  ?author a :Author ;
          ecrm:P1_is_identified_by/ecrm:P190_has_symbolic_content ?authorName .
  ?work a ilrm:F2_Expression ;
        ecrm:P102_has_title/ecrm:P190_has_symbolic_content ?title ;
        ecrm:P106_is_composed_of ?toponym .
  ?place :is_identified_by_toponym ?toponym ;
         geo:asWKT ?wktPlace .
  ?toponym ecrm:P190_has_symbolic_content ?toponymName .

  { 
    SELECT ?wktFrance 
    WHERE {
        SERVICE <https://qlever.cs.uni-freiburg.de/api/osm-planet> { 
            ?osm_id osm2rdfkey:wikidata wd:Q142 ;
                    a osm:relation ;
                    geo:hasGeometry ?geometryFrance .
            ?geometryFrance geo:asWKT ?wktFrance .     
        } 
    }
  }
  FILTER(geof:sfWithin(?wktPlace,?wktFrance)). 
} 

```


The query retrieves the work titles and authors along with the toponyms the works mention. The "FROM" clause specifies the graph that is queried. Several ontology prefixes are specified at the beginning of the query to shorten the corresponding IRIs, which are used in the subsequent parts of the query. The "FROM" clause and the prefix declaration is equal also for the other two queries reported in the following.


The \textit{WHERE} clause contains the conditions that need to be satisfied for each result. It involves several semantic-triple patterns connected by the "." or ";" operators. In this clause, in addition to retrieving the work titles, authors and toponyms, the polygons of the places identified by the toponyms are defined as conditions to satisfy. A nested \textit{SELECT} statement allows retrieving the WKT geometry of France (wd:Q142) from the QLever SPARQL server of the University of Freiburg.
Finally, the \textit{FILTER} clause selects the places that are included within the France polygon. \\
The query produced the output reported in Table \ref{tab:Query1}. To verify the correctness of the retrieved information, we manually checked, %with the help of a MOVING expert, the VCs (among the 454) that contained information on vineyard products. Precision and Recall (0.93 and 0.90, respectively), were reasonably high, and F1 (0.91) indicated good retrieval performance. The main reason for Recall not reaching one was the presence of faults (false negatives) by the named entity extraction processes in detecting vineyard-related entities in the event texts. Precision was, instead, negatively affected by citations of vineyard-related products in VCs that did not focus on vineyard products (false positives).

|     | title                                                                         | authorName                   | toponymName                     |
| --- | ----------------------------------------------------------------------------- | ---------------------------- | ------------------------------- |
| 1   | Liber sancti Iacobi                                                           | Opus sine auctore            | Poitiers                        |
| 2   | Liber sancti Iacobi                                                           | Opus sine auctore            | Limosino                        |
| 3   | Liber sancti Iacobi                                                           | Opus sine auctore            | Bordeaux                        |
| 4   | Liber sancti Iacobi                                                           | Opus sine auctore            | Vézelay                         |
| 5   | Liber sancti Iacobi                                                           | Opus sine auctore            | Périgueux                       |
| 6   | Liber sancti Iacobi                                                           | Opus sine auctore            | Tours                           |
| 7   | Epistola ad Fiscannenses (Itinerarium sive Relatio de monasterio Fiscannensi) | Baldericus Burgulianus       | abbazia della Trinità di Fécamp |
| 8   | De Gallica Petri Damiani profectione et eius ultramontano itinere             | Opus sine auctore            | Borgogna                        |
| 9   | Itinerarium expeditionis Romam                                                | Aegidius Li Muisis           | Troyes                          |
| 10  | Narratio itineris navalis ad Terram Sanctam                                   | Opus sine auctore            | Marseille                       |
| 11  | Itinerarium cardinalis                                                        | Iohannes Porta de Annoniaco  | Avignon                         |
| 12  | Itinerarium Gregorii XI Avenione Romam                                        | Petrus Amelii de Brenaco     | Avignon                         |
| 13  | Itinerarium Gregorii XI Avenione Romam                                        | Petrus Amelii de Brenaco     | Marseille                       |
| 14  | Itinerarium in Terram Sanctam                                                 | Simeon Simeonis              | Paris                           |
| 15  | De viis maris                                                                 | Rogerius de Hoveden          | Guascogna                       |
| 16  | Itinerarium                                                                   | Virgilius Bornadus de Brixia | Avignon                         |
| 17  | Itinerarium                                                                   | Virgilius Bornadus de Brixia | Paris                           |
| 18  | Historia dedicationis ecclesiae sancti Remigii apud Remos                     | Anselmus Remensis            | Reims                           |

# Query 2

## The places located within a 0.2-degree buffer around the Via Francigena
https://api.triplydb.com/s/0LDHEc7_A

We report the SPARQL query corresponding to Q2 in our GitHub repository

```
PREFIX : <https://imagoarchive.it/ontology/>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX ecrm: <http://erlangen-crm.org/200717/>
PREFIX ilrm: <http://imagoarchive.it/ilrmoo/>
PREFIX igen: <https://imagoarchive.it/thes/tid/>
PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>
PREFIX geo: <http://www.opengis.net/ont/geosparql#>
PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 

SELECT DISTINCT ?title ?authorName ?wktPlace ?francigena
FROM <https://geosparql.isti.cnr.it/fuseki/imago/archive>
WHERE { 
  BIND("LINESTRING(1.11419 51.26638 0,1.22406 51.18037 0,1.32843 51.13903 0,1.80084 50.97676 0,1.90521 50.9387 0,1.96563 50.89714 0,2.00958 50.84514 0,2.09198 50.8035 0,2.17987 50.76877 0,2.24578 50.72706 0,2.34466 50.68184 0,2.41058 50.61566 0,2.43804 50.56684 0,2.56439 50.52844 0,2.66326 50.50399 0,2.68524 50.46205 0,2.7182 50.42706 0,2.74887 50.40379 0,2.75161 50.37927 0,2.77359 50.3039 0,2.91046 50.24474 0,3.12469 50.21663 0,3.18512 50.14276 0,3.1961 50.0864 0,3.24554 50.04056 0,3.22906 49.97701 0,3.28949 49.91692 0,3.30596 49.85321 0,3.3609 49.77523 0,3.38836 49.6438 0,3.57513 49.58329 0,3.67401 49.55479 0,3.72345 49.5334 0,3.83331 49.46562 0,3.87725 49.41204 0,3.93219 49.3584 0,4.02008 49.25454 0,4.09149 49.21508 0,4.19586 49.16123 0,4.27276 49.1217 0,4.28695 49.0726 0,4.30618 49.05101 0,4.31991 49.0258 0,4.36111 48.96453 0,4.41055 48.91943 0,4.46823 48.87609 0,4.52041 48.84357 0,4.58084 48.79655 0,4.59732 48.72231 0,4.57534 48.66974 0,4.56985 48.64434 0,4.55337 48.60621 0,4.51767 48.54988 0,4.50393 48.47528 0,4.49844 48.43885 0,4.5314 48.38234 0,4.5726 48.31297 0,4.62753 48.27094 0,4.73465 48.22705 0,4.77584 48.19593 0,4.77859 48.16846 0,4.80056 48.12998 0,4.81155 48.10064 0,4.84062 48.08844 0,4.8928 48.08385 0,4.90791 48.03796 0,4.90379 48.00306 0,4.934 47.9856 0,4.97245 47.97273 0,5.00404 47.95526 0,5.05897 47.94238 0,5.20454 47.91385 0,5.27732 47.90281 0,5.31166 47.90465 0,5.32676 47.8586 0,5.32516 47.81186 0,5.31692 47.75834 0,5.38009 47.71955 0,5.4158 47.67703 0,5.46798 47.64004 0,5.53115 47.59931 0,5.559082 47.535979 0,5.58609 47.4825 0,5.595474 47.432732 0,5.802155 47.400208 0,5.84198 47.356036 0,5.940857 47.351385 0,5.97175 47.32067 0,5.99836 47.3003 0,6.001453 47.271076 0,6.02394 47.24229 0,6.05896 47.22959 0,6.06308 47.22213 0,6.07646 47.22493 0,6.08938 47.22213 0,6.09762 47.22061 0,6.10573 47.22064 0,6.11131 47.21642 0,6.11972 47.21169 0,6.12813 47.20884 0,6.16041 47.19642 0,6.15843 47.19204 0,6.14916 47.18784 0,6.14522 47.17997 0,6.15019 47.1739 0,6.14796 47.16882 0,6.15062 47.16386 0,6.13732 47.15803 0,6.12444 47.15021 0,6.11457 47.14192 0,6.11354 47.13357 0,6.12015 47.13123 0,6.12831 47.13164 0,6.12419 47.12942 0,6.12333 47.12487 0,6.11758 47.11342 0,6.132 47.10582 0,6.14281 47.10682 0,6.15097 47.10454 0,6.15929 47.09905 0,6.1683 47.09379 0,6.17981 47.08756 0,6.18358 47.08216 0,6.18955 47.07959 0,6.19534 47.07579 0,6.20864 47.07167 0,6.22997 47.05442 0,6.24066 47.04562 0,6.2486 47.04372 0,6.25692 47.04146 0,6.26722 47.03898 0,6.28031 47.04003 0,6.28615 47.03579 0,6.28602 47.03272 0,6.28791 47.03003 0,6.28997 47.02927 0,6.28881 47.0269 0,6.28915 47.02427 0,6.2943 47.02485 0,6.29928 47.02415 0,6.30443 47.02532 0,6.30426 47.02389 0,6.30271 47.0235 0,6.30306 47.02078 0,6.30859 47.02111 0,6.32537 47.01561 0,6.32623 47.00027 0,6.33078 46.99805 0,6.32829 46.98967 0,6.33164 46.98405 0,6.3343 46.9823 0,6.34417 46.97445 0,6.34494 46.96572 0,6.34014 46.95811 0,6.32812 46.94967 0,6.32263 46.93924 0,6.34391 46.91814 0,6.35095 46.90665 0,6.36374 46.89551 0,6.3652 46.88935 0,6.36503 46.8819 0,6.37331 46.87955 0,6.37704 46.87181 0,6.37387 46.86629 0,6.37069 46.86001 0,6.37215 46.85837 0,6.37619 46.85989 0,6.3791 46.86265 0,6.38073 46.8603 0,6.3785 46.85837 0,6.37687 46.85755 0,6.37567 46.85672 0,6.38048 46.84563 0,6.39232 46.84175 0,6.40005 46.83565 0,6.40863 46.83377 0,6.42253 46.83471 0,6.42906 46.83177 0,6.43798 46.82531 0,6.44519 46.81909 0,6.45944 46.81803 0,6.47008 46.82003 0,6.49412 46.83001 0,6.49858 46.8232 0,6.51283 46.81991 0,6.53017 46.82179 0,6.53463 46.81756 0,6.54373 46.81498 0,6.5566 46.81885 0,6.55094 46.81509 0,6.54596 46.81145 0,6.55214 46.81216 0,6.54665 46.80793 0,6.55137 46.80793 0,6.57076 46.80699 0,6.58664 46.80799 0,6.59317 46.80393 0,6.60244 46.80176 0,6.62681 46.79136 0,6.63333 46.78384 0,6.6281 46.77949 0,6.58767 46.76691 0,6.57497 46.76385 0,6.56579 46.76855 0,6.56192 46.76267 0,6.55334 46.7575 0,6.54785 46.75538 0,6.54338 46.7535 0,6.54132 46.74974 0,6.53789 46.74762 0,6.53841 46.74774 0,6.53257 46.74374 0,6.52999 46.73644 0,6.53274 46.72244 0,6.52742 46.71232 0,6.52193 46.69925 0,6.5215 46.69649 0,6.51918 46.69431 0,6.51351 46.69042 0,6.51214 46.6883 0,6.51077 46.68571 0,6.50836 46.68289 0,6.50776 46.68142 0,6.50631 46.68006 0,6.50613 46.67959 0,6.50725 46.67882 0,6.50759 46.67835 0,6.50751 46.67829 0,6.50639 46.67935 0,6.50768 46.67891 0,6.50665 46.67541 0,6.50648 46.67111 0,6.51145 46.66899 0,6.51557 46.66392 0,6.51592 46.65992 0,6.53034 46.65002 0,6.52965 46.64778 0,6.53343 46.64154 0,6.52416 46.63022 0,6.50957 46.62309 0,6.50871 46.61425 0,6.52313 46.60304 0,6.52253 46.60399 0,6.52364 46.60404 0,6.52553 46.59992 0,6.52854 46.59573 0,6.52579 46.58871 0,6.53849 46.58116 0,6.53961 46.57679 0,6.55162 46.5716 0,6.56387 46.56131 0,6.56227 46.54752 0,6.55197 46.5402 0,6.55368 46.5343 0,6.56055 46.52934 0,6.54716 46.51446 0,6.55952 46.51422 0,6.58973 46.5206 0,6.62704 46.51408 0,6.69273 46.50406 0,6.74789 46.47815 0,6.81381 46.47437 0,6.86874 46.44978 0,6.94839 46.44788 0,6.94564 46.40434 0,6.92642 46.36834 0,6.96212 46.29818 0,7.02077 46.19718 0,7.02884 46.18351 0,7.02421 46.17495 0,7.02524 46.16841 0,7.03399 46.16948 0,7.03794 46.16009 0,7.04635 46.14344 0,7.05854 46.13333 0,7.06918 46.12274 0,7.07897 46.11703 0,7.08297 46.07189 0,7.13241 46.02805 0,7.15988 45.99562 0,7.20932 45.9689 0,7.178793 45.872083 0,7.151241 45.856184 0,7.21206 45.82171 0,7.25601 45.8064 0,7.31918 45.72976 0,7.37411 45.74126 0,7.45925 45.75276 0,7.52517 45.74318 0,7.61558 45.74955 0,7.63412 45.75147 0,7.65335 45.74428 0,7.66433 45.73518 0,7.66639 45.72511 0,7.67498 45.70306 0,7.66983 45.69994 0,7.66948 45.69586 0,7.67669 45.67932 0,7.69832 45.6546 0,7.71274 45.6498 0,7.72132 45.63612 0,7.73609 45.6294 0,7.74501 45.60611 0,7.76321 45.60154 0,7.78278 45.60202 0,7.79617 45.59554 0,7.80372 45.58569 0,7.81471 45.56694 0,7.82913 45.55036 0,7.84732 45.52799 0,7.85385 45.51284 0,7.86621 45.48035 0,7.87239 45.46615 0,7.89539 45.46807 0,7.9232 45.47144 0,7.95169 45.46976 0,8.02585 45.43291 0,8.04164 45.43026 0,8.054 45.4211 0,8.10619 45.40278 0,8.15322 45.37506 0,8.25851 45.34693 0,8.33278 45.32753 0,8.41106 45.32318 0,8.451 45.33602 0,8.51921 45.3058 0,8.60069 45.28097 0,8.67404 45.22533 0,8.70918 45.24521 0,8.73664 45.24521 0,8.75312 45.25004 0,8.852 45.21716 0,8.86974 45.20332 0,8.88862 45.20453 0,8.91368 45.19703 0,8.92616 45.18716 0,8.96186 45.18232 0,9.00444 45.16683 0,9.06211 45.16296 0,9.0937 45.15424 0,9.12242 45.15516 0,9.14062 45.16848 0,9.15412 45.18232 0,9.17564 45.18155 0,9.20356 45.17651 0,9.22828 45.14843 0,9.2324 45.12131 0,9.25804 45.08636 0,9.26799 45.08152 0,9.27692 45.07497 0,9.28321 45.08157 0,9.30507 45.07764 0,9.32441 45.06799 0,9.34902 45.06794 0,9.37442 45.06503 0,9.43565 45.05926 0,9.50019 45.05344 0,9.5478 45.05703 0,9.58808 45.05538 0,9.63089 45.05606 0,9.66087 45.05053 0,9.70619 45.0515 0,9.71088 45.04514 0,9.72953 45.03209 0,9.76936 45.00976 0,9.82212 44.97912 0,9.8806 44.95342 0,9.89318 44.93782 0,9.90932 44.92786 0,9.91996 44.92227 0,9.93232 44.91643 0,9.98359 44.88925 0,10.03578 44.87368 0,10.05077 44.87022 0,10.06519 44.86317 0,10.08934 44.85713 0,10.21419 44.82495 0,10.27565 44.81399 0,10.32371 44.80303 0,10.32234 44.75892 0,10.30963 44.73039 0,10.29727 44.70477 0,10.28274 44.66337 0,10.26603 44.61955 0,10.23101 44.56674 0,10.22827 44.54252 0,10.21591 44.50874 0,10.26329 44.47054 0,10.23925 44.44995 0,10.21659 44.43525 0,10.1445 44.42789 0,10.11978 44.39601 0,10.12802 44.37049 0,10.13969 44.35969 0,10.13763 44.34251 0,10.09368 44.3204 0,10.07515 44.29829 0,10.0724 44.28453 0,10.0415 44.26585 0,9.96139 44.21036 0,9.92546 44.17629 0,9.91653 44.15954 0,9.93644 44.12949 0,9.99961 44.09251 0,10.06416 44.05502 0,10.14289 44.02866 0,10.20195 43.97631 0,10.24177 43.95061 0,10.26924 43.92193 0,10.28984 43.88829 0,10.32554 43.87641 0,10.35987 43.85562 0,10.42442 43.85661 0,10.456 43.84869 0,10.49995 43.84175 0,10.52696 43.83948 0,10.55191 43.82719 0,10.64529 43.81332 0,10.6739 43.81272 0,10.69313 43.80083 0,10.73043 43.77367 0,10.75836 43.7577 0,10.76797 43.74431 0,10.77827 43.73836 0,10.80986 43.73637 0,10.82222 43.72694 0,10.83869 43.72198 0,10.88195 43.72397 0,10.90621 43.68833 0,10.960236 43.637069 0,10.96115 43.5969 0,11.02706 43.56506 0,11.05453 43.5352 0,11.047783 43.469989 0,11.14563 43.47285 0,11.15455 43.46338 0,11.16691 43.43746 0,11.18362 43.4216 0,11.20536 43.41003 0,11.22047 43.38958 0,11.24382 43.3796 0,11.281071 43.342408 0,11.30167 43.343782 0,11.329823 43.325802 0,11.33194 43.31178 0,11.36673 43.2687 0,11.41136 43.2417 0,11.44775 43.19766 0,11.4656 43.16963 0,11.483 43.12764 0,11.49032 43.111 0,11.51298 43.10449 0,11.52694 43.09155 0,11.54319 43.08293 0,11.55555 43.06587 0,11.57272 43.06938 0,11.58851 43.06387 0,11.60568 43.05032 0,11.62422 43.04882 0,11.62765 43.02623 0,11.67366 43.00966 0,11.70455 42.97802 0,11.70547 42.95903 0,11.71829 42.92374 0,11.72859 42.90061 0,11.72172 42.89005 0,11.72744 42.88461 0,11.75125 42.85533 0,11.75743 42.83368 0,11.78215 42.81857 0,11.85081 42.77373 0,11.86729 42.74398 0,11.89498 42.72946 0,11.90437 42.6895 0,11.9181 42.68243 0,11.91398 42.65769 0,11.97853 42.65012 0,11.99913 42.62486 0,12.00874 42.56218 0,12.03758 42.53992 0,12.06802 42.50106 0,12.10762 42.40774 0,12.06024 42.37325 0,12.0623 42.35549 0,12.04879 42.32464 0,12.11471 42.28604 0,12.19986 42.25149 0,12.27584 42.22241 0,12.31155 42.19851 0,12.35366 42.14772 0,12.37014 42.09883 0,12.37037 42.06081 0,12.38136 42.01797 0,12.38021 42.00848 0,12.39097 41.99757 0,12.41981 41.97919 0,12.45002 41.96081 0,12.46925 41.94754 0,12.47348 41.92593 0,12.46387 41.90754 0)"^^geo:wktLiteral AS ?francigena)
  ?exp_cre a ilrm:F28_Expression_Creation ;
  		     ilrm:R17_created ?work ;
  		     ecrm:P14_carried_out_by ?author .	
  ?author a :Author ;
          ecrm:P1_is_identified_by/ecrm:P190_has_symbolic_content ?authorName .
  ?work a ilrm:F2_Expression ;
        ecrm:P102_has_title/ecrm:P190_has_symbolic_content ?title ;
  		  ecrm:P106_is_composed_of ?toponym .
  ?place :is_identified_by_toponym ?toponym ;
         geo:asWKT ?wktPlace .
  ?toponym ecrm:P190_has_symbolic_content ?toponymName .
  ?work :has_genre igen:100021 . # itineraria
 FILTER(geof:sfWithin(
        ?wktPlace,
        geof:buffer(?francigena, 0.2, uom:degree))). 
}
```


The query retrieves the work titles and authors along with the toponyms the works mention. The "FROM" clause specifies the graph that is queried. Several ontology prefixes are specified at the beginning of the query to shorten the corresponding IRIs, which are used in the subsequent parts of the query. The prefix declaration is equal also for the other two queries reported in the following.
The \textit{WHERE} clause, in addition to retrieving the work titles and authors and the toponyms, the polygons of the places identified by the toponyms are defined as conditions to satisfy.
Finally, the \textit{FILTER} clause selects the places that are included within a buffer of 0.2 degrees created around the Francigena polygon. The Via Francigena is an ancient road and pilgrimage route running from Canterbury in England, through France and Switzerland, to Rome and then to Apulia, Italy, where there were ports of embarkation for the Holy Land. \\
The query produced the output reported in Table \ref{tab:Query2}.

|     | title                      | authorName                  | toponymName |
| --- | -------------------------- | --------------------------- | ----------- |
| 1   | Itinerarium cardinalis     | Iohannes Porta de Annoniaco | Viterbo     |
| 2   | Itinerarium cardinalis     | Iohannes Porta de Annoniaco | Vercelli    |
| 3   | Itinerarium cardinalis     | Iohannes Porta de Annoniaco | Piacenza    |
| 4   | Itinerarium cardinalis     | Iohannes Porta de Annoniaco | Siena       |
| 5   | Descriptio orae Ligusticae | Iacobus Bracellus           | Lerici      |
| 6   | Descriptio orae Ligusticae | Iacobus Bracellus           | Sarzana     |
| 7   | Descriptio orae Ligusticae | Iacobus Bracellus           | La Spezia   |

# Query 3
https://api.triplydb.com/s/AC68c4dXy
## The places in Italy that are mentioned in manuscripts written in the fifteenth century

We report the SPARQL query corresponding to Q3 in our GitHub repository.

```
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX ecrm: <http://erlangen-crm.org/200717/>
PREFIX ilrm: <http://imagoarchive.it/ilrmoo/>
PREFIX : <https://imagoarchive.it/ontology/>
PREFIX geo: <http://www.opengis.net/ont/geosparql#>
PREFIX osm: <https://www.openstreetmap.org/>
PREFIX osm2rdfkey: <https://osm2rdf.cs.uni-freiburg.de/rdf/key#>
PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX wd: <http://www.wikidata.org/entity/>
SELECT DISTINCT ?authorName ?title ?toponymName ?wktPlace (group_concat(distinct ?manuscriptString;separator="; ") as ?manList)
	FROM <https://geosparql.isti.cnr.it/fuseki/imago/archive>
		WHERE {
    ?exp_cre a ilrm:F28_Expression_Creation ;
  		     ilrm:R17_created ?work ;
         ecrm:P14_carried_out_by ?author ;
      		 ilrm:R18_created ?manuscript .
  ?author a :Author ;
          ecrm:P1_is_identified_by/ecrm:P190_has_symbolic_content ?authorName .
  ?work a ilrm:F2_Expression ;
        ecrm:P102_has_title/ecrm:P190_has_symbolic_content ?title ;
        ecrm:P106_is_composed_of ?toponym . 
  		?place :is_identified_by_toponym ?toponym ;
         		geo:asWKT ?wktPlace .
  		?toponym ecrm:P190_has_symbolic_content ?toponymName .
		?manifestation ilrm:R7i_is_materialized_in ?manuscript .
   		?manuscript ecrm:P1_is_identified_by/ecrm:P190_has_symbolic_content ?signature ;
		              ecrm:P50_has_current_keeper ?library ;
		              ecrm:P46_is_composed_of/ecrm:P1_is_identified_by/ecrm:P190_has_symbolic_content ?folios .
  		?library ecrm:P74_has_current_or_former_residence ?libraryPlace ;
		  			ecrm:P1_is_identified_by/ecrm:P190_has_symbolic_content ?libraryName .
  		?libraryPlace :is_identified_by_toponym ?top .
  ?top ecrm:P190_has_symbolic_content ?libraryPlaceName .
	  	  ?manifestation_creation ilrm:R24_created  ?manifestation ;
	 							  ecrm:P4_has_time-span/ecrm:P170i_time_is_defined_by ?date_manuscript ;
	    							:has_start_date ?start_date_manuscript ;
	 								:has_end_date ?end_date_manuscript .
		    FILTER("1401-01-01T00:00:00Z"^^xsd:dateTime <= ?start_date_manuscript && ?start_date_manuscript <= "1500-01-01T00:00:00Z"^^xsd:dateTime)
            FILTER("1401-01-01T00:00:00Z"^^xsd:dateTime <= ?end_date_manuscript && ?end_date_manuscript <= "1500-01-01T00:00:00Z"^^xsd:dateTime)
  BIND(CONCAT(?libraryPlaceName, ", ", ?libraryName, ", ",?signature, ", ", ?folios) AS ?manuscriptString)
  
  { 
    SELECT ?wktItaly 
    WHERE {
        SERVICE <https://qlever.cs.uni-freiburg.de/api/osm-planet> { 
            ?osm_id osm2rdfkey:wikidata wd:Q38 ;
                    a osm:relation ;
                    geo:hasGeometry ?geometryItaly .
            ?geometryItaly geo:asWKT ?wktItaly .     
        } 
    } LIMIT 1
  }
  FILTER(geof:sfWithin(?wktPlace,?wktItaly)). 
    
} GROUP BY ?authorName ?title ?toponymName ?wktPlace
```

The query retrieves the toponyms mentioned in manuscripts of the fifteenth century.
In the \textit{WHERE} clause, the polygons of the places identified by the toponyms are retrieved and the range of time is set using the FILTER operator. A nested \textit{SELECT} statement allows retrieving the WKT geometry of Italy (wd:38) from the QLever SPARQL server of the University of Freiburg.
Finally, the \textit{FILTER} clause selects the places that are included within the polygon of Italy.\\
The query produced the output reported in Table \ref{tab:Query3}.


|authorName|title|toponymName|manList|
|---|---|---|---|---|
|1|Burchardus de Monte Sion|Descriptio Terrae Sanctae|Sicilia|Città del Vaticano, Biblioteca Apostolica Vaticana, Pal. lat. 1358, 1r-36v; München, Universitätsbibliothek, 4° 807, 77r-93r; Rimini, Biblioteca Civica Gambalunga, SC-MS 71, 269r-320r; München, Bayerische Staatsbibliothek, Clm 5307, 92r-120r; Wrocław, Biblioteka Uniwersytecka, I.F.277, 4r-10r; Venezia, Biblioteca Nazionale Marciana, lat. X. 24 (3296), 1r-58v; Augsburg, Universitätsbibliothek, II.1.2° 105, 73r-85r; Praha, Národní Knihovna Ceské Republiky, III.H.15 (568), 1-10v; München, Bayerische Staatsbibliothek, Clm 26636, 377r-385r; Warsaw, Biblioteka Narodowa, 8052, 280r-293v; Salzburg, Benediktiner-Erzabtaei Sankt Peter Stiftsbibliothek, b.IX.22, 99r-132r; Leipzig, Universitätsbibliothek, 1305, 31r-43r; Maria Saal, Stiftsbibliothek, 10, 172r-188r; Zwickau,  Ratsschulbibliothek (Stadtarchiv), I, XII, 5, 112v-144v; Bruxelles, KBR (Bibliothèque Royale de Belgique), 9176-77 (7336), 24v-43r; München, Bayerische Staatsbibliothek, Clm 14583, 469v-470v, 476v-485r; Maria Saal, Stiftsbibliothek, 15, 134r-155r; Padova, Biblioteca Universitaria , 1027, 64r-134v; Cambridge, Magdalen College Library, lat. 43, 24r-43r; Oxford, Magdalen College Library, lat. 43, 24r-43r; Klosterneuburg, Bibliothek des Augustiner Chorherrenstiftes, 722 A, 1r-40v; Melk, Stiftsbibliothek, 46 (959; H. 17), 121r-130r ; Sankt Paul im Lavanttal, Bibliothek des Benediktinerstifts, 145/4 (28.1.4), 146r-180r; Wrocław, Biblioteka Uniwersytecka, I.F.221, 232r-242r; Wien, Österreichische Nationalbibliothek, 3341, 1r-15v; Città del Vaticano, Biblioteca Apostolica Vaticana, Barb. lat. 1952, 146r-169v; Salzburg, Benediktiner-Erzabtaei Sankt Peter Stiftsbibliothek, b.X.30, 46r-86v; København, Det Kgl. Bibliotek, Thott 580 4°, 400r-404v; München, Bayerische Staatsbibliothek, Cgm 2928, 125v-128v; Leiden, Universiteitsbibliotheek, B.P.L. 69, 99r-115v; München, Bayerische Staatsbibliothek, Clm 16204, 222r-228r; Praha, Knihovna Metropolitní Kapituly, G.28 (1021), 43v-53r; Wrocław, Biblioteka Uniwersytecka, R 445, 218r-242r; Wien, Österreichische Nationalbibliothek, 4739, 104r-123v; Bruxelles, KBR (Bibliothèque Royale de Belgique), 733-41 (1709), 143r-175r; Wolfenbüttel, Herzog August Bibliothek, Weiss. 41 (4125), 179v-197r; Wolfenbüttel, Herzog August Bibliothek, Helmst. 354 (389), 132r-167r; Milano, Biblioteca Ambrosiana, A 219 inf., 83r-108r; Basel, Universitätsbibliothek, A I 28, 195r-232r|
|2|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Roma|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|3|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Novara|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|4|Iohannes Lagenator de Francofordia|Itinerarius|Acri|Mainz, Martinus-Bibliothek, 214, 125r-132r|
|5|Iacobus Bracellus|Descriptio orae Ligusticae|Chiavari|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|6|Cyriacus Anconitanus|Itinerarium|Roma|Roma, Biblioteca Casanatense, Ms. 3636, 122v-135r, 144v-145r; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 2967, 1r-21v|
|7|Opus sine auctore|Descriptio Lateranensis ecclesiae|Roma|Roma, Biblioteca Vallicelliana, B 51, 97r-116r|
|8|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Vercelli|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|9|Galvanus Flamma|Chronica extravagans|Milano|Milano, Biblioteca Ambrosiana, A 275 inf. , 31r-60v|
|10|Iacobus Bracellus|Descriptio orae Ligusticae|Genova|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|11|Iacobus Bracellus|Descriptio orae Ligusticae|Lerici|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|12|Iacobus Utinensis|De antiquitatibus Aquileiensibus|Aquileia|Città del Vaticano, Biblioteca Apostolica Vaticana, Reg. lat. 1555, 117r-128v; San Daniele del Friuli , Biblioteca Civica Guarneriana, 104, 178r-183r; Milano, Biblioteca Ambrosiana, I 28 sup., 25r-35v|
|13|Petrus Paulus Vergerius|De situ urbis Iustinopolitane|Trieste|Padova, Biblioteca Civica, B.P. 1223, XIIr-XIIIv (pp. 53-56); Oxford, Bodleian Library, Canon. misc. 166, 234r-235v; Padova, Biblioteca Civica, B.P. 1287, 28r-29v|
|14|Opus sine auctore|Chronicon Gradense|Grado|Venezia, Biblioteca Nazionale Marciana, lat. X, 141, 33r-38r|
|15|Cyriacus Anconitanus|Itinerarium|Ancona|Roma, Biblioteca Casanatense, Ms. 3636, 122v-135r, 144v-145r; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 2967, 1r-21v|
|16|Ludovicus de Rupe Cavardi|Itinerarium in Terram Sanctam|Venezia|Paris, Bibliothèque nationale de France, Nal. 497, 31v-48r|
|17|Iacobus Bracellus|Descriptio orae Ligusticae|Savona|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|18|Cyriacus Anconitanus|Itinerarium|Verona|Roma, Biblioteca Casanatense, Ms. 3636, 122v-135r, 144v-145r; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 2967, 1r-21v|
|19|Beda Venerabilis|Nomina regionum atque locorum de Actibus apostolorum|Siracusa|Oxford, Bodleian Library, 175, 85v-87r; Düsseldorf, Universitäts- und Landesbibliothek, B 22, 88v-91r|
|20|Opus sine auctore|Chronica de civitate Ravennae|Ravenna|Modena, Biblioteca Estense Universitaria, lat. 371, 40r-42v|
|21|Guidus Pisanus|Liber compositus de variis historiis pro diversis utilitatibus lectori proventuris|Pisa|Roma, Biblioteca Nazionale Centrale Vittorio Emanuele II, Sess. 286 (2020), 82v-113v; Città del Vaticano, Biblioteca Apostolica Vaticana, Urb. lat. 452, 136r-143v; Modena, Biblioteca Estense Universitaria, lat. 699 (alfa.W.8.14), 174r-203r; Valladolid, Biblioteca Universitaria y Biblioteca Historica de Santa Cruz, 64, 1-61; Napoli, Biblioteca Nazionale Vittorio Emanuele III, Vindobonense Latino 48, 14r-41v|
|22|Guillelmus Gaudensis|Peregrinationes totius Terrae Sanctae|Venezia|Würzburg, Franziskanerkloster (Minoritenkloster), I. 98 (deperditus), ; Bruxelles, KBR (Bibliothèque Royale de Belgique), KB, 73 G 8, 38v-48; Milano, Archivio Storico Civico e Biblioteca Trivulziana, Triv. 359, 121r-123v|
|23|Anonymus Ravennas|Cosmographia|Spoleto|Città del Vaticano, Biblioteca Apostolica Vaticana, Urb. lat. 961, 1r-47v; Basel, Universitätsbibliothek, F V 6, 85r-108v|
|24|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Savona|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|25|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Piacenza|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|26|Iacobus Bracellus|Descriptio orae Ligusticae|Sarzana|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|27|Iacobus Bracellus|Descriptio orae Ligusticae|La Spezia|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|28|Petrus Paulus Vergerius|De situ veteris et inclyte urbis Rome|Aquileia|Padova, Biblioteca Civica, B. P. 1287, 131r-135v; Venezia, Biblioteca Nazionale Marciana, Lat. XIV. 254 , 83v-85v|
|29|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Ventimiglia|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|30|Leo Baptista Albertus|Descriptio urbis Romae|Roma|Chicago (IL), The Newberry Library, MS 102 (52-15), 25r-28v; Oxford, Bodleian Library, Canon. misc. 172 (S.C. 19648), 234r-238r|
|31|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Viterbo|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|32|Opus sine auctore|Descriptio Lateranensis ecclesiae|basilica di San Giovanni in Laterano|Roma, Biblioteca Vallicelliana, B 51, 97r-116r|
|33|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Velletri|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|34|Beda Venerabilis|Nomina regionum atque locorum de Actibus apostolorum|Roma|Oxford, Bodleian Library, 175, 85v-87r; Düsseldorf, Universitäts- und Landesbibliothek, B 22, 88v-91r|
|35|Cyriacus Anconitanus|Itinerarium|Ferrara|Roma, Biblioteca Casanatense, Ms. 3636, 122v-135r, 144v-145r; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 2967, 1r-21v|
|36|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Perugia|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|37|Christophorus Bondelmontius|Liber insularum archipelagi|Gallipoli|Napoli, Biblioteca Nazionale Vittorio Emanuele III, XIII G 30, ; Venezia, Biblioteca Nazionale Marciana, Ital. VI, 19 (6087), ; Atene, Biblioteca Gennadios, 71, 1r-41v; Firenze, Biblioteca Nazionale Centrale, Magliabecchiano XIII 7, 1r-47r; Treviso, Biblioteca comunale di Treviso, 323, ; Città del Vaticano, Biblioteca Apostolica Vaticana, Chig. F. V. 110, ; Città del Vaticano, Biblioteca Apostolica Vaticana, Ross. 702, ; El Escorial, Real Biblioteca del Monasterio de San Lorenzo de El Escorial, f. II 17, ; Ravenna, Biblioteca Classense , lat. 308, ; Città del Vaticano, Biblioteca Apostolica Vaticana, Barb. lat. 270, ; Greenwich, National Maritime Museum, P 20, ; Paris, Bibliothèque nationale de France, lat. 4824, ; Wien, Österreichische Nationalbibliothek , ital. 2639, ; London, British Library, Cotton Vespasian A XIII, 1r-41v; London, Sconosciuta, Collezione privata (ex Baden), 89r-142v; Berlin, Staatsbibliothek - Preußischer Kulturbesitz, Hamilton 108, 1r-79v; Oxford, Corpus Christi College Library, 210, 279-295; Città del Vaticano, Biblioteca Apostolica Vaticana, Ross. 705, ; Madrid, Biblioteca Nacional de España, 18246, 3r-63r; Baltimore (MD), Walters Art Museum, W. 309, 2r-40v; Ann Arbor, University of Michigan Library, Special Collections, 162, ; Padova, Biblioteca Universitaria , 1605, ; Milano, Biblioteca Nazionale Braidense, AC X 31, ; Venezia, Biblioteca Nazionale Marciana, Lat. XIV, 45 (4595), 1-70; Venezia, Biblioteca Nazionale Marciana, Lat. X, 124 (3177), 260r-354v; London, British Library, Arundel 93, 129r-159v; Greenwich, National Maritime Museum, P 13, ; London, Sconosciuta, Collezione privata (ex Phillipps 4473), 1r-82r; Città del Vaticano, Biblioteca Apostolica Vaticana, Chig. F. IV. 74, 1r-23v, 45r-46v; Firenze, Biblioteca Medicea Laurenziana, Plut. 29.25, 2r-47v; Minneapolis (MN), James Ford Bell Collection, 1475/fmA, ; Paris, Bibliothèque nationale de France, lat. nouv. acq. 2383, ; Città del Vaticano, Biblioteca Apostolica Vaticana, Ross. 704, 1r-71v; Venezia, Biblioteca Nazionale Marciana, Lat. X, 215 (3773), 1r-52r; Paris, Bibliothèque nationale de France, Cartes et Plans, Rés. Ge. FF 9351, ; Roma, Biblioteca Casanatense, 106, 1r-69v; Milano, Biblioteca Ambrosiana, A 219 inf., ; Venezia, Biblioteca Nazionale Marciana, Lat. X, 123 (3784), 2-30; Göttweig, Library of Göttweig Abbey, 391 (432),|
|38|Cyriacus Anconitanus|Itinerarium|Bologna|Roma, Biblioteca Casanatense, Ms. 3636, 122v-135r, 144v-145r; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 2967, 1r-21v|
|39|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Genova|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|40|Mapheus Vegius Laudensis|De rebus antiquis memorabilibus basilicae Sancti Petri Romae|Roma|Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 1863, 197r-238r|
|41|Guillelmus Gaudensis|Peregrinationes totius Terrae Sanctae|Acri|Würzburg, Franziskanerkloster (Minoritenkloster), I. 98 (deperditus), ; Bruxelles, KBR (Bibliothèque Royale de Belgique), KB, 73 G 8, 38v-48; Milano, Archivio Storico Civico e Biblioteca Trivulziana, Triv. 359, 121r-123v|
|42|Iacobus Bracellus|Descriptio orae Ligusticae|Cinque Terre|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|43|Petrus Paulus Vergerius|De situ veteris et inclyte urbis Rome|Ravenna|Padova, Biblioteca Civica, B. P. 1287, 131r-135v; Venezia, Biblioteca Nazionale Marciana, Lat. XIV. 254 , 83v-85v|
|44|Iacobus Bracellus|Descriptio orae Ligusticae|Sanremo|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|45|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Firenze|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|46|Blondus Flavius|Roma instaurata|Roma|Bern, Burgerbibliothek, 576, 350v; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 1942, 1r-93r; London, British Library, Add. 17375, 21r-97r; Napoli, Biblioteca Statale Oratoriana dei Girolamini, XXVIII 2-15 (olim cart. 32), 1r-6v; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 1941, 1r-101r; Paris, Bibliothèque nationale de France, lat. 5825B, 1r-76v; Siena, Biblioteca Comunale degli Intronati, K X 35, 1r-123r; Madrid, Biblioteca Nacional de España, 6518, 1r-89v; Padova, Biblioteca Capitolare, D 45, 80r-132r; Forlì, Biblioteca Comunale Aurelio Saffi, Raccolta Piancastelli, Sala O, I 41 (373), ; New Haven, Beinecke Rare Book and Manuscript Library, 779, 1r-98v; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 1279, 1r-57r; Bernkastel-Kues, Bibliothek des St. Nikolaus-Hospitals, 157, 1r-56v; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 1096, 1r-115r; Arezzo, Biblioteca Città di Arezzo, 233, 1r-106v; Paris, Bibliothèque nationale de France, lat. 5825A, 1r-102v; San Daniele del Friuli , Biblioteca Civica Guarneriana, 106, 1r-74v; London, British Library, Harley 4913, 1r-66v; Venezia, Biblioteca Nazionale Marciana, lat. XIV 273 (4346), 1r-60v; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 7310, 121r-158v; London, British Library, Add. 21956, 1r-120r; Messina, Biblioteca Regionale Universitaria, Fondo vecchio, 110, 1r-34v; Foligno, Biblioteca Lodovico Jacobilli del Seminario Vescovile, 37 (olim A II 10), 12r-120r; Città del Vaticano, Biblioteca Apostolica Vaticana, Reg. lat. 827, 1r-111r; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 1943, 1r-144r; Urbino, Biblioteca Universitaria, Fondo dell'Università 104, 1r-68r; Carpentras, Bibliothèque Inguimbertine, 487, 1r-127v; Firenze, Biblioteca Medicea Laurenziana, Pluteo LXXVI 50, 33r-183v; Siena, Biblioteca Comunale degli Intronati, K X 34, 1r-121v; Bruxelles, KBR (Bibliothèque Royale de Belgique), 359-361, 182r-245v; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 1944, 1r-81r; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 10803, 181r-193r; Firenze, Biblioteca Medicea Laurenziana, Ashburnham 291, 3r-88r|
|47|Petrus Paulus Vergerius|De situ veteris et inclyte urbis Rome|Roma|Padova, Biblioteca Civica, B. P. 1287, 131r-135v; Venezia, Biblioteca Nazionale Marciana, Lat. XIV. 254 , 83v-85v|
|48|Opus sine auctore|Chronicon Gradense|Venezia|Venezia, Biblioteca Nazionale Marciana, lat. X, 141, 33r-38r|
|49|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Siena|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|
|50|Bonvicinus de Ripa|De magnalibus urbis Mediolani|Milano|Madrid, Biblioteca Nacional de España, 8828, 1r-15v|
|51|Anonymus Ravennas|Cosmographia|Ravenna|Città del Vaticano, Biblioteca Apostolica Vaticana, Urb. lat. 961, 1r-47v; Basel, Universitätsbibliothek, F V 6, 85r-108v|
|52|Iacobus Bracellus|Descriptio orae Ligusticae|Liguria|Genova, Archivio Storico del Comune di Genova, cod. 360, 78-80; Città del Vaticano, Biblioteca Apostolica Vaticana, Fondo Reginense Latino, 810, 4v-13r; Savona, Biblioteca Civica Anton Giulio Barrili, IX. III 2-13, 53v-56v; Westport, Joseph Kusaila's Collection, , 312-316v; Legnano, Istituto Barbara Melzi, 2 (D VI e AVI 48), 163-167; Modena, Biblioteca Estense Universitaria, Fondo Estense, Alpha W 2, 19 lat. 216 (VI.C.14), 4r-11v; Torino, Biblioteca Nazionale Universitaria, H VI 12, 117v-121v; Genova, Biblioteca Civica Berio, m.r. Cf. Arm. 26 (10.6.65), ; Modena, Biblioteca Estense Universitaria, Fondo Càmpori, App. 72 gamma R. 6. 34, 48r-53v; Leiden, Universiteitsbibliotheek, BPL 19, ; Roma, Biblioteca Casanatense, 665 (C II 8), 123v-127v|
|53|Iohannes Lagenator de Francofordia|Itinerarius|Venezia|Mainz, Martinus-Bibliothek, 214, 125r-132r|
|54|Petrus Paulus Vergerius|De situ urbis Iustinopolitane|Aquileia|Padova, Biblioteca Civica, B.P. 1223, XIIr-XIIIv (pp. 53-56); Oxford, Bodleian Library, Canon. misc. 166, 234r-235v; Padova, Biblioteca Civica, B.P. 1287, 28r-29v|
|55|Anonymus Ravennas|Cosmographia|Benevento|Città del Vaticano, Biblioteca Apostolica Vaticana, Urb. lat. 961, 1r-47v; Basel, Universitätsbibliothek, F V 6, 85r-108v|
|56|Opus sine auctore|Mirabilia urbis Romae|Roma|München, Bayerische Staatsbibliothek, Clm 88, 153r-156v; Basel, Universitätsbibliothek, E II 72, 76r-78r; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 5348, 35r-36r; Praha, Národní Knihovna Ceské Republiky, XI D 7, 126v-130r; München, Bayerische Staatsbibliothek, Clm 4784, 3r-7v; Praha, Knihovna Metropolitní Kapituly, O.L., 125v-126v; Hannover, Niedersächsische Landesarchiv , XIII 859, 95r-95v; Wien, Österreichische Nationalbibliothek, 3476, 9r-15v; Berlin, Staatsbibliothek - Preußischer Kulturbesitz, Lat. fol. 583, 103r-107r; Napoli, Biblioteca Statale Oratoriana dei Girolamini, CXVI (olim Pil. XII, no. II), ; Rimini, Biblioteca Civica Gambalunga, 43, 1r-16v; Brno, Archiv města Brna, A. 101, 188v-190r; Città del Vaticano, Biblioteca Apostolica Vaticana, Barb. lat. 435, 140v-142v; London, British Library, Titus A XIX, 11v-12r; Città del Vaticano, Biblioteca Apostolica Vaticana, Vat. lat. 6898, 44r-56v; München, Bayerische Staatsbibliothek, Clm 14373, 33r-35r; Berlin, Staatsbibliothek - Preußischer Kulturbesitz, theol. lat. 4° 141, 55r-57r; London, British Library, Harley 562, 1r-5r; Stockholm, Riksarkivet, 14 (E 9074), 71r-142v; Roma, Biblioteca Casanatense, 294, 134r-136v; Città del Vaticano, Biblioteca Apostolica Vaticana, Ott. lat. 1267, 96r-100v; Bonn, Universitäts- und Landesbibliothek, S 594, 355r-357v; Milano, Archivio Storico Civico e Biblioteca Trivulziana, 369, 1r-2v; Forlì, Biblioteca Comunale Aurelio Saffi, 454, 235r-246r; Oxford, Bodleian Library, Digby 196, 6r-10v; Bonn, Universitäts- und Landesbibliothek, S 448, 90v-93r; Los Angeles (CA), University of California, 170/656, ; Wien, Österreichische Nationalbibliothek, 3242, 1r-9r; Città del Vaticano, Biblioteca Apostolica Vaticana, Barb. lat. 2687, 30v-33r; London, British Library, Titus D XIX, 11r-20v; Wolfenbüttel, Herzog August Bibliothek, Cod. 32.4 Aug. 4, 1r; Città del Vaticano, Biblioteca Apostolica Vaticana, Urb. lat. 984, 178r-227v; Notre Dame, Indiana, Hesburgh Library, 56, 3r-5v; Roma, Biblioteca Angelica, 627, 45r-50v; Subiaco , Biblioteca del Monumento Nazionale del Monastero di Santa Scolastica, 223, CCXX, 114r-119r; Třeboň, Statní Oblastní Archiv, A 7, 175v; Basel, Universitätsbibliothek, O IV 15, 1r-3r; London, British Library, Cotton Claud. E VIII, 6r-6v; Bruxelles, KBR (Bibliothèque Royale de Belgique), 14024-14028, 1r-8v; München, Bayerische Staatsbibliothek, Clm 249, 195r-199r; Padova, Seminario Maggiore Vescovile, 83, 1r-6v; Stuttgart, Württembergische Landesbibliothek, Cod. hist. 304, 1r-30v; Milano, Archivio Storico Civico e Biblioteca Trivulziana, 964, 81v-85v; Stuttgart, Württembergische Landesbibliothek, HB X 25, 48v-52v; Bamberg, Staatsbibliothek, Theol. 110 (Q VI 58), 211v-214r; Graz, Technische Universität, 593, 1r-3r; Firenze, Biblioteca Medicea Laurenziana, Magl. XXVIII, 53, 8, 1r-50r; Wien, Österreichische Nationalbibliothek, 4761, 260v; Lucca, Biblioteca Capitolare Feliniana, 545, 81r-92r; London, British Library, Harley 2321, 115r-118v; München, Bayerische Staatsbibliothek, Clm 18630, 125r-128v; Troyes, Médiathèque Jacques-Chirac, Ms. nr. 2007, ; München, Bayerische Staatsbibliothek, Clm 7747, 71r-80v; Paris, Bibliothèque nationale de France, lat. 3462, 82v-83v; Roma, Biblioteca Vallicelliana, F 73, 50r-65v; Wrocław, Biblioteka Uniwersytecka, I F 327, 134r-134v; Dresden, Staats- und Universitätsbibliothek , F 93, 423v-429r; München, Bayerische Staatsbibliothek, Clm 7662, 213r-215v; Melk, Stiftsbibliothek, 1564 (482, H 104), 67r-76v; Cambridge, University Library, G.G. IV 25, 68r-71r; Chicago (IL), University Library, 689 (Z. 114 T 75), 222r-230r, 231r-234r; Fermo, Biblioteca Civica Romolo Spezioli , 77 (4 CA 2/77), 57v-60v|
|57|Opus sine auctore|Indulgentiae ecclesiarum urbis Romae|Roma|Chicago (IL), University Library, 689 (Z. 114 T 75), 230r-231r; Kraków, Biblioteka Jagiellońska, germ. quart. 1585, 1r-8v; Berlin, Staatsbibliothek - Preußischer Kulturbesitz, Lat. fol. 583, 107v-109v; Milano, Archivio Storico Civico e Biblioteca Trivulziana, 964, 77r-81r; Uppsala, Universitetsbibliotek, C 239, 121r-130v; Hildesheim, Dombibliothek, Ge. 67, 3v-4r; Wien, Österreichische Nationalbibliothek, 3476, 28v-35v; Basel, Universitätsbibliothek, O IV 15, 5r-9r; Graz, Technische Universität, 851, 193r-197r; London, British Library, Titus D XIX, 27v-44r; Paris, Bibliothèque nationale de France, lat. 3462, 83v-100r; Osnabrück, Diözesanarchiv, Frenswegen 5, 118v-120v; München, Bayerische Staatsbibliothek, Clm 14373, 35r-37v; Wolfenbüttel, Herzog August Bibliothek, 990 Helmst. , 1r-59v; San Marino (CA), The Huntington Library, HM 1342, 100r-113r; Tournai, Bibliothèque de la Ville, 3A (Nr. 26), 4r-9v; Rimini, Biblioteca Civica Gambalunga, 43, 54r-59v; München, Bayerische Staatsbibliothek, Clm 5030, 261r-272v; Graz, Technische Universität, 856, 169r-173r; München, Bayerische Staatsbibliothek, Clm 18630, 120r-123r; London, British Library, Harley 2321, 104r-115r; Notre Dame, Indiana, Hesburgh Library, 56, 5v-8r; London, British Library, Cotton Claud. E. VIII, 263r; Bonn, Universitäts- und Landesbibliothek, S 448, 88v-90v; Ljubljana, Narodna in Univerzitetna Knjiznica, 37, 444r-444v; Napoli, Biblioteca Nazionale Vittorio Emanuele III, XII.F.25, 283v-290v; Graz, Technische Universität, 593, 3v-6v; London, British Library, Harley 562, 5r-18v; München, Bayerische Staatsbibliothek, Clm 18881, 117r-126r; Nürnberg, Staatsarchiv, Cent. III 64, 28r-30v; Innsbruck, Universitäts- und Landesbibliothek Tirol, 763, 303r-309r; Wrocław, Biblioteka Uniwersytecka, R. 262, 48r-52v; München, Bayerische Staatsbibliothek, Clm 672, 42r-68r; Třeboň, Statní Oblastní Archiv, Cod. A. 7, 83v-94r; München, Bayerisches Hauptstaatsarchiv, Nr. 151, 19v; Graz, Technische Universität, 936, 277r-280v; Oxford, Bodleian Library, Digby 196, 6r-10v; Brno, Archiv města Brna, A 101, 191v-193v; München, Bayerische Staatsbibliothek, Clm 18377, 308r-311r; London, British Library, Titus A XIX, 12v-15v; Basel, Universitätsbibliothek, E II 72, 78r-79v; München, Bayerische Staatsbibliothek, Clm 7747, 81r-90v; Dresden, Staats- und Universitätsbibliothek , F 93, 429v-434r; Milano, Archivio Storico Civico e Biblioteca Trivulziana, 369, 2v-6r; München, Bayerische Staatsbibliothek, Clm 21101, 211r-219v; Los Angeles (CA), University of California, 170/656, ; München, Bayerische Staatsbibliothek, Clm 3129, 339r-346v; Wrocław, Biblioteka Uniwersytecka, I F 327, 134v-136v; London, British Library, Cotton. Jul. D. VIII, 15v-20v; Praha, Národní Knihovna Ceské Republiky, XI D 7, 126v-130r; Budapest, Országos Széchényi Könyvtár, 402, 427r-429r; Wolfenbüttel, Herzog August Bibliothek, 32.4 Aug. 4, 1r-9r; Bruxelles, KBR (Bibliothèque Royale de Belgique), 14024-14028, 9r-12v; Berlin, Staatsbibliothek - Preußischer Kulturbesitz, theol. lat. 4° 141, 57r-60r; El Escorial, Real Biblioteca del Monasterio de San Lorenzo de El Escorial, Cod. lat. R. III. 8, 108v-109v; Cambridge, Fitzwilliam Museum, CFM 12, 187v-189r; Wien, Österreichische Nationalbibliothek, 4761, 261r-268r|
|58|Felix Fabri|Evagatorium in Terrae sanctae, Arabiae et Egypti peregrinationem|Venezia|München, Bayerische Staatsbibliothek, Clm 2826, 1r-288v; München, Bayerische Staatsbibliothek, Clm 2827, 1r-301r; Dresden, Staats- und Universitätsbibliothek , A 71, 8r-264r; Ulm, Stadtarchiv, 19555, 1-3, Voll. I-II|
|59|Iohannes Porta de Annoniaco|Itinerarium cardinalis|Pisa|Praha, Národní Knihovna Ceské Republiky, I.C.24 (116), 112-166v|


# Query 4

## The authors who have visited the Holy Land
https://api.triplydb.com/s/DuYhoQLT-

We report the SPARQL query corresponding to Q4 in our GitHub repository\footnote{\url{LINK}}.


```
PREFIX : <https://imagoarchive.it/ontology/>
PREFIX igen: <https://imagoarchive.it/thes/tid/>
PREFIX ecrm: <http://erlangen-crm.org/200717/>
PREFIX ilrm: <http://imagoarchive.it/ilrmoo/>
PREFIX wdt: <http://www.wikidata.org/prop/direct/>
PREFIX wd: <http://www.wikidata.org/entity/>
PREFIX geo: <http://www.opengis.net/ont/geosparql#>
PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>

SELECT DISTINCT ?authorName ?title
FROM <https://geosparql.isti.cnr.it/fuseki/imago/archive>
WHERE {
  ?exp_cre a ilrm:F28_Expression_Creation ;
  		     ilrm:R17_created ?work ;
  		     ecrm:P14_carried_out_by ?author .	
  ?author a :Author ;
          ecrm:P1_is_identified_by/ecrm:P190_has_symbolic_content ?authorName .
  ?work a ilrm:F2_Expression ;
        ecrm:P102_has_title/ecrm:P190_has_symbolic_content ?title ;
        ecrm:P106_is_composed_of ?toponym . 
  ?place :is_identified_by_toponym ?toponym ;
         geo:asWKT ?wktPlace .
  ?toponym ecrm:P190_has_symbolic_content ?toponymName .
  
  ?work :has_genre igen:100026 . # Diario di viaggio genre
  igen:100026  :has_genre_name ?labelGenre.
  
  { 
    SELECT ?coord 
    WHERE {
      SERVICE <https://query.wikidata.org/bigdata/namespace/wdq/sparql> { 
            wd:Q48175 wdt:P625 ?coord.    
      } 
    }
  }
  FILTER(geof:sfIntersects(?wktPlace,geof:buffer(?coord,0.3, uom:degree))). 
}  
```

The query retrieves the authors who wrote works in which they tell their journeys in the Holy Land.
The \textit{WHERE} clause contains the conditions that need to be satisfied for each result. In this clause, the polygons of the places identified by the toponyms are retrieved only for the work belonging to two literary genres, i.e., personal travel diaries and \emph{itineraria}. These two genres have to unique identifies (100026 for travel diaries and 100021 for \emph{itineraria}) that came from a literary genres thesaurus built by the IMAGO scholars on the basis of the subject indexing tool Nuovo Soggettario \cite{cheti2023functionality}. 
A nested \textit{SELECT} statement allows retrieving the coordinates (longitude and latitude) of the Holy Land (wd:Q142) from the Wikidata SPARQL server.
Finally, the \textit{FILTER} clause selects the places that are included within a buffer of 0.3 degrees created around the Holy Land coordinates.\\
The query produced the output reported in Table \ref{tab:Query4}.

|     | authorName                    | title                                                                          |
| --- | ----------------------------- | ------------------------------------------------------------------------------ |
| 1   | Anselmus Adurnus              | Itinerarium Terrae Sanctae                                                     |
| 2   | Antonius de Reboldis          | Itinerarium ad Sepulcrum Domini                                                |
| 3   | Bernardus de Breydenbach      | Peregrinatio in Terram Sanctam                                                 |
| 4   | Bernardus monachus            | Itinerarium in loca sancta                                                     |
| 5   | Felix Fabri                   | Evagatorium in Terrae sanctae, Arabiae et Egypti peregrinationem               |
| 6   | Guilielmus de Boldensele      | Liber de quibusdam ultramarinis partibus et praecipue de Terra Sancta          |
| 7   | Guillelmus Gaudensis          | Peregrinationes totius Terrae Sanctae                                          |
| 8   | Iohannes de Mandavilla        | Itinerarius a terra Anglie in partes Iherosolomitanas et in ulteriores marinas |
| 9   | Ludolphus de Sudheim          | De itinere Terrae Sanctae                                                      |
| 10  | Ludovicus de Rupe Cavardi     | Itinerarium in Terram Sanctam                                                  |
| 11  | Opus sine auctore             | De itinere Frisonum                                                            |
| 12  | Opus sine auctore             | Itinerarium cuiusdam Anglici Terram Sanctam et alia loca sancta visitantis     |
| 13  | Opus sine auctore             | Narratio itineris navalis ad Terram Sanctam                                    |
| 14  | Paulus Waltherus de Guglingen | Itinerarium in Terram Sanctam et ad Sanctam Catharinam                         |
| 15  | Simeon Simeonis               | Itinerarium in Terram Sanctam                                                  |
| 16  | Wilbrandus Oldenburgensis     | Itinerarium Terrae Sanctae                                                     |
