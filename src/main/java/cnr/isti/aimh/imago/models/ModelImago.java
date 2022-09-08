package cnr.isti.aimh.imago.models;

import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import cnr.isti.aimh.imago.pojo.full.*;
import cnr.isti.aimh.imago.util.Vocabulary;

/**
 * This is the class to create and populate with individuals the ontology of IMAGO project.
 * The model is an extension of CIDOC CRM and LRMoo (the reformulation of FRBRoo)
 */
public class ModelImago {
    


	// Impostare una variabile con l'uri del progetto
	// al momento questo � l'uri temporaneo
	private static String baseURI = "http://www.imagoarchive.it/ontology/";


	// Questa funzione crea e importa l'ontologia OWL
	public static OntModel importModel(String url) {
		OntModel onto = ModelFactory.createOntologyModel(
		        OntModelSpec.OWL_DL_MEM, null );
		onto.read(url); // Legge l'ontologia passata tramite l'url
		return onto; // restituisce l'ontologia letta

	}

	/**
	 * Convert coordinates into WKT format
	 * WKT format of a point is POINT (30 10)
     * 
     * @param lat   latitude
     * @param lon   longitude
     * @return      a string in WKT of a point
	 */
	public static String toWKT(String lat, String lon){
		return "POINT (" + lat + " " + lon + ")";
	}

	/**
     * This function populate the ontology from a list of imported lemmas
     * 
     * @param model     the model imported from the function importModel
     * @param lemmas    the list of lemmas imported from a json
     * @return          the model with the individuals
     *  
     */
	public static OntModel populateModel(OntModel model, List<Root> lemmas) {

        // It initializes Vocabulary class contains all the resources and all
        // the properties of the Ontology
        Vocabulary vocabulary = new Vocabulary(model);

        /* NAMING
        * - The Resources have the r_ prefixed in the name of the variable
        * - The literals have the l_ prefixed in the name of the variable
        * - The properties have the p_ prefixed in the name of the variable
        */

		
		for (Root e : lemmas) { // For any lemma

			int id = e.getId();                         // get the lemma id
			Lemma lemma = e.getLemma();                 // get the lemma content
			String lemmaURI = baseURI + "resources/lemma/" + id; // Make uri of expression creation

			// MAKE RESOURCES
			//  Make author resource getting the iri from the object author inside the object lemma
			// ex. http://www.mirabileweb.it/author/christophorus-bondelmontius-n-1385-ca-m-post-1430-author/22278
			Resource r_author = model.createResource(e.getLemma().getAuthor().getIri());
			// Make work resource getting the iri of the work inside the lemma object
			Resource r_work = model.createResource(e.getLemma().getWork().getIri());
			// Make Creo la risorsa expression creation opera passandogli l'iri precedentemente creato con l'id del lemma
			Resource r_expression_creation = model.createResource(lemmaURI);
			Resource blank_node = model.createResource(); // Why? 
			Literal l_italian_author_name = model.createTypedLiteral(e.getLemma().getAuthor().getAlias().get(0));
			Literal l_work_title = model.createTypedLiteral(e.getLemma().getWork().getTitle());


			// Aggiungo le risorse al modello
			// La risorsa autore � di type E39 Actor, quindi gli passo
			// Come primo argomento la risorsa Autore, come seconto che sto specificando il tipo
			// e come terzo la risorsa di E39 Actor
			model.add(r_author, RDF.type, vocabulary.author);

			// Faccio la stessa cosa con le altre risorse
			model.add(r_work, RDF.type, vocabulary.f2_expression);
			model.add(r_expression_creation, RDF.type, vocabulary.f28_expression_creation);

			
			// Statement s = model.createStatement(s, p, o);
			// model.add(s); add the statement (triple) to the model
			model.add(r_expression_creation, vocabulary.p14_carried_out_by, r_author);
			model.add(r_expression_creation, vocabulary.r17_created, r_work);
			
			// !!!!!!!! model.add(resource_Autore, vocabulary.p1_is_identified_by, blankNode);
			// !!!!!!!! model.add(blankNode, vocabulary.p190_has_symbolic_content, resource_Nome_Autore_Ita);
			// Forse si può fare più semplicemente
			model.add(r_author, vocabulary.p1_is_identified_by, l_italian_author_name);
			model.add(r_work, vocabulary.p102_has_title, l_work_title);

			for(Genre genre : e.getLemma().getGenres()) {
				String genre_iri = genre.getIri();
				Resource r_genre = model.createResource(genre_iri);
				model.add(r_genre, RDF.type, vocabulary.genre);
				model.add(r_work, vocabulary.has_genre, r_genre);
				String genre_name = genre.getName();
				Literal l_genre = model.createTypedLiteral(genre_name);
				model.add(r_genre, vocabulary.has_genre_name, l_genre);
			}

            for(Place place : e.getLemma().getPlaces()) {
            	String place_iri = place.getIri();
            	Resource r_place = model.createResource(place_iri);
            	Resource r_toponym = model.createResource(); // che iri dare? 
				// nei toponimi String toponymURI = e.getName().replaceAll(" ", "_").toLowerCase(); 
            	model.add(r_place, RDF.type, vocabulary.e53_place);
            	model.add(r_toponym, RDF.type, vocabulary.toponym);
            	model.add(r_work, vocabulary.p67_refers_to, r_place);
            	model.add(r_place, vocabulary.is_identified_by_toponym, r_toponym);

            	String toponym_name = place.getName();
            	Literal l_toponym = model.createTypedLiteral(toponym_name);
            	model.add(r_toponym, vocabulary.p190_has_symbolic_content, l_toponym);

            	String lat = place.getLat();
            	String lon = place.getLon();
				// WKT format POINT (30 10)
            	Literal coordinates = model.createTypedLiteral(toWKT(lat, lon));
            	// model.add(e94_space_primitive, p190_has_symbolic_content, coordinates);
            	model.add(r_place, vocabulary.p168_place_is_defined_by, coordinates);
            	model.add(r_work, vocabulary.p106_is_composed_of, r_toponym);
				// Non usiamo la country per il momento

            }

			// id contatore
            int count = 0;
            for(Manuscript manuscript : e.getLemma().getManuscripts()) {

				String library = manuscript.getLibrary().getName();
				String library_place = manuscript.getLibrary().getPlace().getName();
				String signature = manuscript.getSignature();
				String folios = manuscript.getFolios();

				String s_library = library.replaceAll(" ", "_").toLowerCase();
				String s_library_place = library_place.replaceAll(" ", "_").toLowerCase();
				String s_signature = signature.replaceAll(" ", "_").toLowerCase();
				String s_folios = library.replaceAll(" ", "_").toLowerCase();

            	// L'iri del manoscritto non c'e' e va creato
            	// https://imagoarchive.it/ontology/resources/manuscript/biblioteca/segnatura/fogli
				// Se fosse questo ci potrebbero essere due manoscritti con lo stesso iri e annotazioni diverse,
				// a patto che l'annotatore non faccia errori di inserimento
				// String manuscript_iri = baseURI + "resources/manuscript/" + s_library + "/" + s_signature + "/" + s_folios;
            	String manuscript_iri = "https://imagoarchive.it/ontology/resources/manuscript"; // !METTERE UN ID!
            	String manuscript_url = manuscript.getUrl(); // Questo rappresenta il link alla pagina web in cui il manoscritto e' riprodotto

            	Resource r_manuscript = model.createResource(manuscript_iri);
            	// Non importa dire che e' di tipo f5_item, basta dire che e' di tipo manuscript che e' sottoclasse di item
            	// model.add(resource_Manuscript, RDF.type, f5_item);
            	model.add(r_manuscript, RDF.type, vocabulary.manuscript);

            	// f3_manifestation, la risorsa non c'e' e va creata
            	// va assegnato un iri alla manifestation come per esempio
            	// https://imagoarchive.it/ontology/resources/manifestation/biblioteca/segnatura/fogli
            	String manifestation_iri = "https://imagoarchive.it/ontology/resources/manifestation/biblioteca"; // !METTERE UN IRI UNIVOCO!
            	Resource r_manifestation = model.createResource(manifestation_iri);
            	// model.add(r_manifestation, vocabulary.r4_embodies, r_work);
            	model.add(r_manuscript, vocabulary.r7i_is_materialized_in, r_manifestation);
            	model.add(r_expression_creation, vocabulary.r18_created, r_manuscript);

            	String manuscript_author_name = manuscript.getAuthor();
            	Literal l_manuscript_author = model.createTypedLiteral(manuscript_author_name);
            	Resource blankNodeAppellationAuthorLat = model.createResource();
            	// model.add(blankNodeAppellationAuthorLat, vocabulary.p106i_forms_part_of, r_manuscript);
            	// model.add(blankNodeAppellationAuthorLat, vocabulary.p190_has_symbolic_content, l_manuscript_author);

				model.add(r_manuscript, vocabulary.p106i_forms_part_of, l_manuscript_author);

				String manuscript_title = manuscript.getTitle();
            	Literal l_manuscript_title = model.createTypedLiteral(manuscript_title);
            	// Resource blankNodeTitleManuscript = model.createResource();
            	// model.add(r_manuscript, vocabulary.p102_has_title, blankNodeTitleManuscript);
            	// model.add(blankNodeTitleManuscript, vocabulary.p190_has_symbolic_content, l_manuscript_title);
				model.add(r_manuscript, vocabulary.p102_has_title, l_manuscript_title);

            	Resource r_library = model.createResource(manuscript.getLibrary().getIri()); // !METTERE UN IRI UNIVOCO!
				model.add(r_library, RDF.type, vocabulary.library);
            	model.add(r_manuscript, vocabulary.p50_has_current_keeper, r_library);
				Resource r_place_library = model.createResource(manuscript.getLibrary().getPlace().getIri());
				model.add(r_place_library, RDF.type, vocabulary.e53_place);
            	model.add(r_library, vocabulary.p74_has_current_or_former_residence, r_place_library);
				// COORDINATE GEOGRAFICHE
				Literal l_coordinates = model.createTypedLiteral(toWKT(manuscript.getLibrary().getPlace().getLat(), manuscript.getLibrary().getPlace().getLon()));
				// model.add(resource_Coordinates, RDF.type, vocabulary.e94_space_primitive);
				model.add(r_place_library, vocabulary.p168_place_is_defined_by, l_coordinates);

				// String signature = manuscript.getSignature();
            	// String segnatura_iri = "https://imagoarchive.it/ontology/resources/manifestation/biblioteca/segnatura"; // !METTERE UN IRI UNIVOCO!
				String signature_iri = baseURI + "resources/" + s_library_place + "/" + s_library + "/" + s_signature + "/"; 
				Resource r_signature = model.createResource(signature_iri);
            	Literal l_signature = model.createTypedLiteral(signature);
				model.add(r_signature, RDF.type, vocabulary.e42_identifier);
            	model.add(r_signature, vocabulary.p190_has_symbolic_content, l_signature);
            	model.add(r_manuscript, vocabulary.p1_is_identified_by, r_signature);

            	Resource r_folios = model.createResource("https://imagoarchive.it/ontology/resources/manuscript/biblioteca/segnatura/fogli"); // !METTERE UN IRI UNIVOCO! IRI NUMERICO

				model.add(r_folios, RDF.type, folios);
            	// String folios = manuscript.getFolios();
            	// Resource blankNodeFolio = model.createResource();
            	Literal l_folios = model.createTypedLiteral(folios);
            	// model.add(blankNodeFolio, p190_has_symbolic_content, folio);
            	model.add(r_folios, vocabulary.p1_is_identified_by, l_folios);
                model.add(r_manuscript, vocabulary.p46_is_composed_of, r_folios);

				//incipit proemio
				String incipit_dedication = manuscript.getIncipitDedication();
            	String incipit_dedication_iri = "https://imagoarchive.it/ontology/resources/incipit_dedication/"; // !METTERE UN IRI UNIVOCO NUMERICO!
				Resource r_incipit_dedication = model.createResource(incipit_dedication_iri);
            	Literal l_incipit_dedication = model.createTypedLiteral(incipit_dedication);
				model.add(r_incipit_dedication, RDF.type, vocabulary.e90_symbolic_object);
            	model.add(r_incipit_dedication, vocabulary.p190_has_symbolic_content, l_incipit_dedication);
            	model.add(r_incipit_dedication, vocabulary.is_incipit_dedication_of, r_manuscript);

                //explicit proemio
				String explicitProemio = manuscript.getExplicitDedication();
            	String explicitProemio_iri = "https://imagoarchive.it/ontology/resources/manifestation/biblioteca/explicit"; // !METTERE UN IRI UNIVOCO!
				Resource resource_explicit = model.createResource(explicitProemio_iri);
            	Literal explicitProemioLiteral = model.createTypedLiteral(explicitProemio);
				model.add(resource_explicit, RDF.type, e90_symbolic_object);
            	model.add(resource_explicit, p190_has_symbolic_content, explicitProemioLiteral);
            	model.add(resource_Manuscript, is_explicit_dedication_of, resource_explicit);

				//incipit text
				String incipitTesto = manoscritto.getIncipitTesto();
            	String incipitTesto_iri = "https://imagoarchive.it/ontology/resources/manifestation/biblioteca/incipit_text"; // !METTERE UN IRI UNIVOCO!
				Resource resource_Incipit_text = model.createResource(incipitTesto_iri);
            	Literal incipitTestoLiteral = model.createTypedLiteral(incipitTesto);
				model.add(resource_Incipit_text, RDF.type, e90_symbolic_object);
            	model.add(resource_Incipit_text, p190_has_symbolic_content, incipitTestoLiteral);
            	model.add(resource_Manuscript, is_text_incipit_of, resource_Incipit_text);

                //explicit text
				String explicitTesto = manoscritto.getExplicitTesto();
            	String explicitTesto_iri = "https://imagoarchive.it/ontology/resources/manifestation/biblioteca/explicit_text"; // !METTERE UN IRI UNIVOCO!
				Resource resource_explicit_text = model.createResource(explicitTesto_iri);
            	Literal explicitTestoLiteral = model.createTypedLiteral(explicitTesto);
				model.add(resource_explicit_text, RDF.type, e90_symbolic_object);
            	model.add(resource_explicit_text, p190_has_symbolic_content, explicitTestoLiteral);
            	model.add(resource_Manuscript, is_text_explicit_of, resource_explicit_text);



				// DATE
				String date_manuscript = manuscript.getDate().getStartDate() + "-" + manuscript.getDate().getEndDate();
				Resource r_date_manuscript =  model.createResource(baseURI + date_manuscript);
				Literal l_date_manuscript =  model.createLiteral(date_manuscript);
				// model.add(resource_dateManuscript, RDF.type, e52_time_span);
				Resource manifestation_creation = model.createResource("iri manifestation creation"); //IRI NUMERICO
				model.add(manifestation_creation, RDF.type, vocabulary.f30_manifestation_creation);
				model.add(manifestation_creation, vocabulary.r24_created, r_manuscript);

				model.add(manifestation_creation, vocabulary.p4_has_time_span, r_date_manuscript);
				model.add(r_date_manuscript, vocabulary.p170i_time_is_defined_by, l_date_manuscript);

				String sources = "";
				for(Source source : manuscript.getSources()) {
					sources += source.getAbbreviation() + ", <a href='" + source.getIri() + "'>" + source.getName() + "</a>, " + source.getPages() + " <br />";
				}

				model.add(r_manuscript, vocabulary.has_secondary_sources, sources);

            }
			for(PrintEdition print_edition: e.getLemma().getPrintEditions()) {
				String print_edition_iri = "https://imagoarchive.it/ontology/resources/printEdition"; // !METTERE UN ID!
				Resource r_print_edition = model.createResource(print_edition_iri);
            	// Non importa dire che � di tipo f5_item, basta dire che � di tipo manuscript che � sottoclasse di item
            	// model.add(resource_Manuscript, RDF.type, f5_item);
				Resource r_print_edition_creation = model.createResource("iriCreazioneEdizioneStampa");
				model.add(r_print_edition_creation, RDF.type, vocabulary.f30_manifestation_creation);
				
            	model.add(r_print_edition, RDF.type, vocabulary.print_edition);
				model.add(r_print_edition, vocabulary.r4_embodies, r_work);
				model.add(r_print_edition_creation, vocabulary.r24_created, r_print_edition);
				Literal l_author_print_edition = model.createTypedLiteral(print_edition.getAuthor());
				model.add(r_print_edition, vocabulary.p106_is_composed_of, l_author_print_edition);

            	// Resource blankNodeAppellationAuthorPE = model.createResource();
            	// model.add(blankNodeAppellationAuthorPE, p106i_forms_part_of, resource_PrintEdition);
            	// model.add(blankNodeAppellationAuthorPE, p190_has_symbolic_content, authorNamePrintEdition);

            	Literal l_title_print_edition = model.createTypedLiteral(print_edition.getTitle());
            	// Resource blankNodeTitlePrintEdition = model.createResource();
            	model.add(r_print_edition, vocabulary.p102_has_title, l_title_print_edition);

				Resource r_curator = model.createResource("iri" + print_edition.getCurator());
				model.add(r_curator, RDF.type, vocabulary.curator);
				model.add(r_print_edition_creation, vocabulary.has_curator, r_curator);
				Literal l_curator = model.createTypedLiteral(print_edition.getCurator());
				// Resource blankNodeAppellationCuratore = model.createResource();
				model.add(r_curator, vocabulary.p1_is_identified_by, l_curator);

				Resource r_place_print_edition = model.createResource(print_edition.getPlace().getIri());
				model.add(r_place_print_edition, RDF.type, vocabulary.e53_place);
				model.add(r_print_edition_creation, vocabulary.p7_took_place_at, r_place_print_edition);
				// Resource resource_Toponym = model.createResource();
				// model.add(resource_Toponym, RDF.type, toponym);
				// model.add(risorsa_place_pe, is_identified_by_toponym, resource_Toponym);

				String r_place_name_as_appear = print_edition.getPlaceAsAppear();
				// Literal literalToponymName = model.createTypedLiteral(toponymName);
				model.add(r_print_edition, vocabulary.is_composed_of_place_name, r_place_name_as_appear);


      	String lat = edizioneStampa.getLuogo().getLat();
      	String lon = edizioneStampa.getLuogo().getLon();
      	Literal coordinates = model.createTypedLiteral("(" + lat + "," + lon + ")");
      	model.add(e94_space_primitive, p190_has_symbolic_content, coordinates);
      	model.add(risorsa_place_pe, vocabulary.p168_place_is_defined_by, coordinates);

        String dateEdizioneStampa = print_edition.getDatazione().getDataInizio() + "-" + edizioneStampa.getDatazione().getDataFine();
				Resource resource_dateEdizioneStampa =  model.createResource(dateEdizioneStampa);
				model.add(resource_dateEdizioneStampa, RDF.type, vocabulary.e52_time_span);
        model.add(r_print_edition_creation, vocabulary.p4_has_time_span, resource_dateEdizioneStampa);

        String publisher = print_edition.getEditor();
        Literal literal_publisher = model.createTypedLiteral(publisher);
        Resource resource_publisher = model.createResource("iriPublisher");
        model.add(resource_publisher, RDF.type, vocabulary.publisher);
        model.add(r_print_edition_creation, vocabulary.has_publisher, resource_publisher);
        // Resource blankNodePublisher = model.createResource();
        model.add(resource_publisher, vocabulary.p1_is_identified_by, curatorNamePrintEdition);
		// 		model.add(blankNodePublisher, vocabulary.p190_has_symbolic_content, curatorNamePrintEdition);

        String format = print_edition.getFormat();
        Resource resource_format = model.createResource("iriFormat");
        model.add(resource_format, RDF.type, vocabulary.format);
        Literal literal_format = model.createTypedLiteral(format);
        model.add(resource_PrintEdition, vocabulary.r69_specifies_phisical_form, resource_format);
        model.add(resource_format, vocabulary.p1_is_identified_by, literal_format);


        String pages = print_edition.getPages();
        Resource resource_pages = model.createResource("iriPages");
        model.add(resource_pages, RDF.type, vocabulary.e90_symbolic_object);
        Literal literal_pages = model.createTypedLiteral(pages);
        model.add(resource_PrintEdition, vocabulary.p106_is_composed_of, resource_pages);
        model.add(resource_pages, vocabulary.p1_is_identified_by, literal_pages);

        String figures = print_edition.getFigures();
        Literal literal_has_figure = model.createTypedLiteral(figures);
        model.add(resource_PrintEdition, vocabulary.has_figure_note, literal_has_figure);

        String notes = print_edition.getNotes();
        Literal literal_notes = model.createTypedLiteral(notes);
        model.add(resource_PrintEdition, vocabulary.p3_has_note, literal_notes);

        String prefatore = print_edition.getPrefator();
        Literal literal_prefatore = model.createTypedLiteral(prefatore);
        model.add(resource_PrintEdition, vocabulary.has_introduction_note, literal_prefatore);

        String edition_reprint = print_edition.getEdizione();
        Literal literal_edition_reprint = model.createTcreateTypedLiteral(edition_reprint);
        Resource resource_edition = model.createResource("iriEdition");
        model.add(resource_edition, RDF.type, vocabulary.edition);
        model.add(resource_PrintEdition, vocabulary.p2_has_type, resource_edition);
        model.add(resource_edition, vocabulary.p190_has_symbolic_content, literal_edition_reprint);

		String reprint_date = print_edition.getDateEdition();
		model.add(r_print_edition, vocabulary.has_reprint_date, reprint_date);

        String primary_sources = print_edition.getPrimarySources();
        Literal literal_primary_sources = model.createTcreateTypedLiteral(primary_sources);
        Resource resource_primary_sources = model.createResource("iriEdition");
        model.add(resource_primary_sources, RDF.type, vocabulary.primary_sources);
        model.add(resource_PrintEdition, vocabulary.p67_refers_to, resource_primary_sources);
        model.add(resource_primary_sources, vocabulary.p190_has_symbolic_content, literal_primary_sources);

        String ecdotic_typology = edizioneStampa.getEcdotica();
        Literal literal_ecdotic_typology = model.createTcreateTypedLiteral(ecdotic_typology);
        Resource resource_ecdotic_typology = model.createResource("iriEdition");
        model.add(resource_ecdotic_typology, RDF.type, vocabulary.ecdotic_typology);
        model.add(resource_PrintEdition, vocabulary.p2_has_type, resource_ecdotic_typology);
        model.add(resource_ecdotic_typology, vocabulary.p190_has_symbolic_content, literal_ecdotic_typology);

        String sources = "";
		for(Source source : print_edition.getSources()) {
			sources += source.getAbbreviation() + ", <a href='" + source.getIri() + "'>" + source.getName() + "</a>, " + source.getPages() + " <br />";
		}

		model.add(r_print_edition, vocabulary.has_secondary_sources, sources);



			}




		}
		return model;
	}

	public static void main(String[] args) throws IOException {
	OntModel onto = ModelFactory.createOntologyModel(
	        OntModelSpec.OWL_DL_MEM, null );
	onto.read("https://imagoarchive.it/onto/2021_07_27_IMAGO_Ontology.owl");
//    ExtendedIterator iter = onto.listNamedClasses();
//while(iter.hasNext()) {
//  OntClass ontoClass = (OntClass) iter.next();
//  System.out.println(ontoClass.getLocalName());
//}
/* Then you can acces the information using the OntModel methods
Let's access the ontology properties */
System.out.println("Listing the properties");
onto.listOntProperties().forEachRemaining(System.out::println);
// let's access the classes local names and their subclasses
try {
            onto.listNamedClasses().toSet().forEach(c -> {
            	System.out.println("-----------------------");
                System.out.println(c.getLocalName());
                System.out.println("-----------------------");
                System.out.println(c.listDeclaredProperties().toList());
                System.out.println("Listing subclasses of " + c.getLocalName());
                c.listSubClasses().forEachRemaining(System.out::println);
            });
        } catch (Exception e) {
            e.printStackTrace();
   }

}
}
