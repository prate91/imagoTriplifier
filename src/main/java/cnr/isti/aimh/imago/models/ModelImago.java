package cnr.isti.aimh.imago.models;

import java.util.HashMap;
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
    


	// Base URI of the IMAGO ontology
	private static String baseURI = "https://www.imagoarchive.it/ontology/";
	private static HashMap<String, Resource> blank_author = new HashMap<String, Resource>();


	/**
	 * Initialize the model and import the ontology by url
	 * 
	 * @param url 	the url with the ontology
	 * @return 		the ontologic model
	 */
	public static OntModel importModel(String url) {
		OntModel onto = ModelFactory.createOntologyModel(
		        OntModelSpec.OWL_DL_MEM, null );
		onto.read(url); // Jena function to read the remote ontology 
		return onto;

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
		return "POINT (" + lon + " " + lat + ")";
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
		* - The Blank Nodes have the _b_ prefixed in the name of the variable
        */

		int m_count = 1;
		int p_count = 1;
		for (Root e : lemmas) { // For any lemma

			int id = e.getId();                         // get the lemma id
			Lemma lemma = e.getLemma();                 // get the lemma content
			String lemmaURI = baseURI + "resources/lemma/" + id; // Make  the IRI of lemma (ie expression creation)

			////////////////////
			// MAKE RESOURCES //
			////////////////////
			
			//  Make author resource getting the iri from the object author inside the object lemma
			// ex. http://www.mirabileweb.it/author/christophorus-bondelmontius-n-1385-ca-m-post-1430-author/22278
			Resource r_author = model.createResource(lemma.getAuthor().getIri());
			// Make work resource getting the iri of the work inside the lemma object
			// ex. http://www.mirabileweb.it/title/descriptio-insulae-cretae-title/4834
			Resource r_work = model.createResource(lemma.getWork().getIri());
			// Make the resource Expression Creation. As IRI we pass the lemma IRI created above
			// ex. https://https://www.imagoarchive.it/ontology/resources/lemma/0000
			Resource r_expression_creation = model.createResource(lemmaURI);


			
			// Literal l_italian_author_name = model.createTypedLiteral(lemma.getAuthor().getAlias().get(0));
			Literal l_italian_author_name = model.createTypedLiteral(lemma.getAuthor().getName());
			Literal l_work_title = model.createTypedLiteral(lemma.getWork().getTitle());


			///////////
			// LEMMA //
			///////////

			// Creating resources 

			// Create literals 

			// Create blank nodes

			model.add(r_author, RDF.type, vocabulary.author);
			model.add(r_work, RDF.type, vocabulary.f2_expression);
			model.add(r_expression_creation, RDF.type, vocabulary.f28_expression_creation);
			model.add(r_expression_creation, vocabulary.p14_carried_out_by, r_author);
			model.add(r_expression_creation, vocabulary.r17_created, r_work);

			Resource _b_author = null;
			if(!blank_author.containsKey(lemma.getAuthor().getIri())){
				_b_author = model.createResource();
				blank_author.put(lemma.getAuthor().getIri(), _b_author);

			}else{
				_b_author = blank_author.get(lemma.getAuthor().getIri());
			}
			

			model.add(r_author, vocabulary.p1_is_identified_by, _b_author);
			model.add(_b_author, vocabulary.p190_has_symbolic_content, l_italian_author_name);
			// Forse si può fare più semplicemente
			// model.add(r_author, vocabulary.p1_is_identified_by, l_italian_author_name);

			Resource _b_work = model.createResource();
			model.add(r_work, vocabulary.p102_has_title, _b_work);
			model.add(_b_work, vocabulary.p190_has_symbolic_content, l_work_title);


			
			for(Genre genre : lemma.getGenres()) { // for any genre in lemma
				Resource r_genre = model.createResource(genre.getIri());
				Literal l_genre = model.createTypedLiteral(genre.getName());
				model.add(r_genre, RDF.type, vocabulary.genre);
				model.add(r_work, vocabulary.has_genre, r_genre);
				model.add(r_genre, vocabulary.has_genre_name, l_genre);
			}

            for(Place place : lemma.getPlaces()) {
            	String place_iri = place.getIri();
            	Resource r_place = model.createResource(place_iri);
            	Resource r_toponym = model.createResource(place.getName().replaceAll(" ", "_").toLowerCase()); // che iri dare? 
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
            	Literal l_coordinates = model.createTypedLiteral(toWKT(lat, lon));
            	// model.add(e94_space_primitive, p190_has_symbolic_content, coordinates);
            	model.add(r_place, vocabulary.p168_place_is_defined_by, l_coordinates);
            	model.add(r_work, vocabulary.p106_is_composed_of, r_toponym);
				// Non usiamo la country per il momento

            }

			
            for(Manuscript manuscript : lemma.getManuscripts()) {

				// Get the strings from json
				String library = manuscript.getLibrary().getName();
				// System.out.println(library);
				String library_place = manuscript.getLibrary().getPlace().getName();
				String signature = manuscript.getSignature();
				String folios = manuscript.getFolios();
				String manuscript_author_name = manuscript.getAuthor();
				String manuscript_title = manuscript.getTitle();
				String incipit_dedication = manuscript.getIncipitDedication();
				String explicit_dedication = manuscript.getExplicitDedication();
				String incipit_text = manuscript.getIncipitText();
				String explicit_text = manuscript.getExplicitText();
				String date_manuscript = manuscript.getDateString();
				String sources = "";
				for(Source source : manuscript.getSources()) {
					sources += source.getName() + ", <a href='" + source.getIri() + "'>" + source.getDescription() + "</a>, " + source.getSpecific() + " <br />";
				}

				
				// Parse the strings to make IRIs
				String s_library = library.replaceAll(" ", "_").toLowerCase();
				String s_library_place = library_place.replaceAll(" ", "_").toLowerCase();
				String s_signature = signature.replaceAll(" ", "_").toLowerCase();
				String s_folios = folios.replaceAll(" ", "_").toLowerCase();


				// Define all the iris
				String manuscript_iri = baseURI + "resources/manuscript/" + s_library_place + "/" + s_library + "/" + s_signature + "/" + s_folios;
				String manifestation_iri = baseURI + "resources/manifestation/manuscript/m-" + m_count;
				String manifestation_creation_iri = baseURI + "resources/manifestation_creation/m-" + m_count;
				String signature_iri = baseURI + "resources/signature/m-" + m_count;
				String folios_iri = baseURI + "resources/folios/m-" + m_count;
				String incipit_dedication_iri = baseURI + "resources/incipit_dedication/m-" + m_count;
				String explicit_dedication_iri = baseURI + "resources/explicit_dedication/m-" + m_count;
				String incipit_text_iri =  baseURI + "resources/incipit_text/m-" + m_count;
				String explicit_text_iri =  baseURI + "resources/explicit_text/m-" + m_count;
				String date_manuscript_iri = baseURI + "resurces/manuscript/date/m-" + m_count;

				// Create all the resources
				Resource r_manuscript = model.createResource(manuscript_iri);
            	Resource r_manifestation = model.createResource(manifestation_iri);
				Resource r_library = model.createResource(manuscript.getLibrary().getIri());
				Resource r_place_library = model.createResource(manuscript.getLibrary().getPlace().getIri());
				Resource r_signature = model.createResource(signature_iri);
				Resource r_folios = model.createResource(folios_iri);
				Resource r_incipit_dedication = model.createResource(incipit_dedication_iri);
            	Resource r_explicit_dedication = model.createResource(explicit_dedication_iri);
				Resource r_incipit_text = model.createResource(incipit_text_iri);
				Resource r_explicit_text = model.createResource(explicit_text_iri);
				Resource r_date_manuscript =  model.createResource(date_manuscript_iri);
				Resource r_manifestation_creation = model.createResource(manifestation_creation_iri); 

            	
				// Create all literals
				Literal l_manuscript_author = model.createTypedLiteral(manuscript_author_name);
				Literal l_manuscript_title = model.createTypedLiteral(manuscript_title);
				Literal l_coordinates = model.createTypedLiteral(toWKT(manuscript.getLibrary().getPlace().getLat(), manuscript.getLibrary().getPlace().getLon()));
				Literal l_library = model.createTypedLiteral(manuscript.getLibrary().getName());
				Literal l_place = model.createTypedLiteral(manuscript.getLibrary().getPlace().getName());
				Literal l_signature = model.createTypedLiteral(signature);
				Literal l_folios = model.createTypedLiteral(folios);
				Literal l_incipit_dedication = model.createTypedLiteral(incipit_dedication);
				Literal explicitProemioLiteral = model.createTypedLiteral(explicit_dedication);
				Literal l_incipit_text = model.createTypedLiteral(incipit_text);
				Literal l_explicit_text = model.createTypedLiteral(explicit_text);
				Literal l_date_manuscript =  model.createTypedLiteral(date_manuscript);
				Literal l_sources = model.createTypedLiteral(sources);


				// Create all blank nodes
				Resource _b_manuscript_author = model.createResource();
				Resource _b_manuscript_title = model.createResource();
				Resource _b_folios = model.createResource();


				// Declare all the statements rdf:type
				model.add(r_manuscript, RDF.type, vocabulary.manuscript);
				model.add(r_library, RDF.type, vocabulary.library);
				model.add(r_place_library, RDF.type, vocabulary.e53_place);
				model.add(r_signature, RDF.type, vocabulary.e42_identifier);
				model.add(r_folios, RDF.type, vocabulary.folios);
				model.add(r_incipit_dedication, RDF.type, vocabulary.e90_symbolic_object);
				model.add(r_explicit_dedication, RDF.type, vocabulary.e90_symbolic_object);
				model.add(r_incipit_text, RDF.type, vocabulary.e90_symbolic_object);
				model.add(r_explicit_text, RDF.type, vocabulary.e90_symbolic_object);
				model.add(r_manifestation_creation, RDF.type, vocabulary.f30_manifestation_creation);
				model.add(r_date_manuscript, RDF.type, vocabulary.e52_time_span);

				// Declare all the triples
				model.add(r_manuscript, vocabulary.r7i_is_materialized_in, r_manifestation);
            	model.add(r_expression_creation, vocabulary.r18_created, r_manuscript);

				model.add(_b_manuscript_author, vocabulary.p106i_forms_part_of, r_manuscript);
            	model.add(_b_manuscript_author, vocabulary.p190_has_symbolic_content, l_manuscript_author);
				model.add(r_manuscript, vocabulary.p102_has_title, _b_manuscript_title);
            	model.add(_b_manuscript_title, vocabulary.p190_has_symbolic_content, l_manuscript_title);
				model.add(r_manuscript, vocabulary.p50_has_current_keeper, r_library);
            	model.add(r_library, vocabulary.p74_has_current_or_former_residence, r_place_library);
            	model.add(r_library, vocabulary.p190_has_symbolic_content, l_library);
				model.add(r_place_library, vocabulary.p190_has_symbolic_content, l_place);
				model.add(r_place_library, vocabulary.p168_place_is_defined_by, l_coordinates);
				model.add(r_signature, vocabulary.p190_has_symbolic_content, l_signature);
            	model.add(r_manuscript, vocabulary.p1_is_identified_by, r_signature);
				model.add(r_folios, vocabulary.p1_is_identified_by, _b_folios);
                model.add(r_manuscript, vocabulary.p46_is_composed_of, r_folios);
				model.add(_b_folios, vocabulary.p190_has_symbolic_content, l_folios);

				// incipit dedication	
            	model.add(r_incipit_dedication, vocabulary.p190_has_symbolic_content, l_incipit_dedication);
            	model.add(r_manifestation, vocabulary.is_incipit_dedication_of, r_incipit_dedication);

                //explicit proemio
            	model.add(r_explicit_dedication, vocabulary.p190_has_symbolic_content, explicitProemioLiteral);
            	model.add(r_manifestation, vocabulary.is_explicit_dedication_of, r_explicit_dedication);

				//incipit text
            	model.add(r_incipit_text, vocabulary.p190_has_symbolic_content, l_incipit_text);
            	model.add(r_manifestation, vocabulary.is_text_incipit_of, r_incipit_text);

                //explicit text
            	model.add(r_explicit_text, vocabulary.p190_has_symbolic_content, l_explicit_text);
            	model.add(r_manifestation, vocabulary.is_text_explicit_of, r_explicit_text);

				
				model.add(r_manifestation_creation, vocabulary.r24_created, r_manifestation);
				model.add(r_manuscript, vocabulary.r7i_is_materialized_in, r_manifestation);

				model.add(r_manifestation_creation, vocabulary.p4_has_time_span, r_date_manuscript);
				model.add(r_date_manuscript, vocabulary.p170i_time_is_defined_by, l_date_manuscript);

				model.add(r_manuscript, vocabulary.has_secondary_sources, l_sources);

            	
				// TO-DO 

				// -[ ] Link al manoscritto
				// -[ ] Link alla descrizione del manoscritto
				// -[ ] Decorazione / apparato iconografico
				// -[ ] Altre eventuali notizie (notes)


				m_count++;

            }
			for(PrintEdition print_edition: lemma.getPrintEditions()) {

				// Get the strings from json
				String edition = print_edition.getEdition();
				String ecdotic = print_edition.getEcdotic();
				String sources = "";
				for(Source source : print_edition.getSources()) {
					sources += source.getName() + ", <a href='" + source.getIri() + "'>" + source.getDescription() + "</a>, " + source.getSpecific() + " <br />";
				}

				// Parse the strings to make IRIs
				String s_edition = edition.replaceAll(" ", "_").toLowerCase();
				String s_ecdotic = ecdotic.replaceAll(" ", "_").toLowerCase();

				// Define all the iris
				String print_edition_iri = baseURI + "resources/print_edition/p-" + p_count; 
				String print_edition_creation_iri = baseURI + "resources/manifestation_creation/p-" + p_count; 
				String curator_iri = baseURI + "resources/print_edition/curator/p-" + p_count;
				String date_print_edition_iri = baseURI + "resurces/print_edition/date/m-" + p_count;
				String publisher_iri = baseURI + "resources/print_edition/publisher/p-" + p_count;
				String format_iri = baseURI + "resources/print_edition/format/p-" + p_count;
				String pages_iri = baseURI + "resources/print_edition/pages/p-" + p_count;
				String edition_iri = baseURI + "resources/print_edition/edition/" + s_edition;
				String ecdotic_iri = baseURI + "resources/print_edition/edition/" + s_ecdotic;

				// Create all the resources
				Resource r_print_edition = model.createResource(print_edition_iri);
				Resource r_print_edition_creation = model.createResource(print_edition_creation_iri);
				Resource r_curator = model.createResource(curator_iri);
				Resource r_place_print_edition = model.createResource(print_edition.getPlace().getIri());
				Resource r_date_print_edition =  model.createResource(date_print_edition_iri);
				Resource r_publisher = model.createResource(publisher_iri);
				Resource r_format = model.createResource(format_iri);
				Resource r_pages = model.createResource(pages_iri);
				Resource r_edition = model.createResource(edition_iri);
				Resource r_ecdotic = model.createResource(ecdotic_iri);

				// Create all literals
				Literal l_author_print_edition = model.createTypedLiteral(print_edition.getAuthor());
				Literal l_title_print_edition = model.createTypedLiteral(print_edition.getTitle());
				Literal l_curator = model.createTypedLiteral(print_edition.getCurator());
				Literal l_place_name_as_appear = model.createTypedLiteral(print_edition.getPlaceAsAppear());
				Literal l_coordinates = model.createTypedLiteral(toWKT(print_edition.getPlace().getLat(), print_edition.getPlace().getLon()));
				Literal l_date_print_edition = model.createTypedLiteral(print_edition.getDateString());
				Literal l_publisher = model.createTypedLiteral( print_edition.getEditor());
				Literal l_format = model.createTypedLiteral(print_edition.getFormat());
				Literal l_pages = model.createTypedLiteral(print_edition.getPages());
				Literal l_figure = model.createTypedLiteral(print_edition.getFigures());
				Literal l_notes = model.createTypedLiteral(print_edition.getNotes());
				Literal l_prefatore = model.createTypedLiteral(print_edition.getPrefator());
				Literal l_edition = model.createTypedLiteral(print_edition.getEdition());
				Literal l_date_edition = model.createTypedLiteral(print_edition.getDateEdition());
				Literal l_primary_sources = model.createTypedLiteral(print_edition.getPrimarySources());
				Literal l_ecdotic = model.createTypedLiteral(ecdotic);
				Literal l_sources = model.createTypedLiteral(sources);
				

				// Create all blank nodes
				Resource _b_print_edition_author = model.createResource();
				Resource _b_print_edition_title = model.createResource();
				Resource _b_curator = model.createResource();
				Resource _b_publisher = model.createResource();
				Resource _b_format = model.createResource();
				Resource _b_pages = model.createResource();

				
				// Declare all the statements rdf:type
				model.add(r_print_edition_creation, RDF.type, vocabulary.f30_manifestation_creation);
				model.add(r_print_edition, RDF.type, vocabulary.print_edition);
				model.add(r_curator, RDF.type, vocabulary.curator);
				model.add(r_place_print_edition, RDF.type, vocabulary.e53_place);
				model.add(r_date_print_edition, RDF.type, vocabulary.e52_time_span);
				model.add(r_publisher, RDF.type, vocabulary.publisher);
				model.add(r_format, RDF.type, vocabulary.format);
				model.add(r_pages, RDF.type, vocabulary.e90_symbolic_object);
				model.add(r_edition, RDF.type, vocabulary.edition);
				model.add(r_ecdotic, RDF.type, vocabulary.ecdotic_typology);

				// Declare all the triples
				model.add(r_print_edition, vocabulary.r4_embodies, r_work);
				model.add(r_print_edition_creation, vocabulary.r24_created, r_print_edition);	
				model.add(r_print_edition, vocabulary.p106_is_composed_of, _b_print_edition_author);
				model.add(_b_print_edition_author, vocabulary.p190_has_symbolic_content, l_author_print_edition);
            	model.add(r_print_edition, vocabulary.p102_has_title, _b_print_edition_title);
            	model.add(_b_print_edition_title, vocabulary.p190_has_symbolic_content, l_title_print_edition);
				model.add(r_print_edition_creation, vocabulary.has_curator, r_curator);
				model.add(r_curator, vocabulary.p1_is_identified_by, _b_curator);
				model.add(_b_curator, vocabulary.p190_has_symbolic_content, l_curator);
				model.add(r_print_edition_creation, vocabulary.p7_took_place_at, r_place_print_edition);
				model.add(r_print_edition, vocabulary.is_composed_of_place_name, l_place_name_as_appear);
				model.add(r_place_print_edition, vocabulary.p168_place_is_defined_by, l_coordinates);
				model.add(r_print_edition_creation, vocabulary.p4_has_time_span, r_date_print_edition);
				model.add(r_date_print_edition, vocabulary.p170i_time_is_defined_by, l_date_print_edition);
				model.add(r_print_edition_creation, vocabulary.has_publisher, r_publisher);
				model.add(r_publisher, vocabulary.p1_is_identified_by, _b_publisher);
				model.add(_b_publisher, vocabulary.p190_has_symbolic_content, l_publisher);
				model.add(r_print_edition, vocabulary.r69_specifies_phisical_form, r_format);
				model.add(r_format, vocabulary.p1_is_identified_by, _b_format);
				model.add(_b_format, vocabulary.p190_has_symbolic_content, l_format);
				model.add(r_print_edition, vocabulary.p106_is_composed_of, r_pages);
				model.add(r_pages, vocabulary.p1_is_identified_by, _b_pages);
				model.add(_b_pages, vocabulary.p1_is_identified_by, l_pages);
				model.add(r_print_edition, vocabulary.has_figure_note, l_figure);
				model.add(r_print_edition, vocabulary.p3_has_note, l_notes);
				model.add(r_print_edition, vocabulary.has_introduction_note, l_prefatore);
				model.add(r_print_edition, vocabulary.p2_has_type, r_edition);
				model.add(r_edition, vocabulary.p190_has_symbolic_content, l_edition);
				if(print_edition.getDateEdition()!=""){
					model.add(r_print_edition, vocabulary.has_reprint_date, l_date_edition);
				}
				model.add(r_print_edition, vocabulary.has_primary_source, l_primary_sources);
				model.add(r_print_edition, vocabulary.p2_has_type, r_ecdotic);
				model.add(r_ecdotic, vocabulary.p190_has_symbolic_content, l_ecdotic);
				model.add(r_print_edition, vocabulary.has_secondary_sources, l_sources);

				
				// TO-DO 

				// -[ ] Other contents


				p_count++;

			}




		}
		return model;
	}


}
