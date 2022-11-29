package cnr.isti.aimh.imago.models;

import java.util.HashMap;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.function.library.print;
import org.apache.jena.vocabulary.RDF;

import cnr.isti.aimh.imago.pojo.full.*;
import cnr.isti.aimh.imago.util.Vocabulary;

/**
 * This is the class to create and populate with individuals the ontology of IMAGO project.
 * The model is an extension of CIDOC CRM and LRMoo (the reformulation of FRBRoo)
 */
public class ModelImago {
    


	// Base URI of the IMAGO ontology
	private static String baseURI = "https://imagoarchive.it/ontology/";
	private static HashMap<String, Resource> blank_author = new HashMap<String, Resource>();
	private static HashMap<String, Resource> blank_library = new HashMap<String, Resource>();
	private static HashMap<String, Resource> blank_coordinates = new HashMap<String, Resource>();
	
	


	/**
	 * Initialize the model and import the ontology by url
	 * 
	 * @param url 	the url with the ontology
	 * @return 		the ontologic model
	 */
	public static OntModel importModel(String url) {
		OntDocumentManager mgr = new OntDocumentManager();
		// set mgr's properties now
		mgr.addIgnoreImport("http://imagoarchive.it/ilrmoo/");
		mgr.addIgnoreImport("http://erlangen-crm.org/200717/");
		mgr.addIgnoreImport("https://imagoarchive.it/thes/tid/");
		// now use it
		OntModelSpec s = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
		s.setDocumentManager( mgr );
		OntModel onto = ModelFactory.createOntologyModel(s);
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
	 * A function to memorize a blank node in a hashmap
	 * or to get an already present blank node
	 * 
	 * @param map		the hasmap with the blanknodes
	 * @param iri		the iri to check
	 * @param model		the model
	 * @return			the new or getted blank node
	 */
	public static Resource checkBlankNode(HashMap<String, Resource> map, String iri, OntModel model){
		Resource _b = null;
		if(!map.containsKey(iri)){
			_b = model.createResource();
			map.put(iri, _b);

		}else{
			_b = map.get(iri);
		}
		return _b;
	}

	/**
     * This function populate the ontology from a list of imported lemmas
     * 
     * @param model     the model imported from the function importModel
     * @param lemmas    the list of lemmas imported from a json
     * @return          the model with the individuals
	 * @throws URISyntaxException
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
			String lemmaURI = baseURI + "resources/lemma/l" + id; // Make  the IRI of lemma (ie expression creation)

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
			Literal l_abstract = model.createTypedLiteral(lemma.getAbstract());


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

			if(lemma.getAbstract()!=""){
				model.add(r_expression_creation, vocabulary.has_abstract, l_abstract);
			}

			
			Resource _b_author = checkBlankNode(blank_author, lemma.getAuthor().getIri(), model);
			

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
            	Resource r_toponym = model.createResource(baseURI + "resources/toponym/" + place.getName().replaceAll(" ", "_").toLowerCase()); // che iri dare? 
				// nei toponimi String toponymURI = e.getName().replaceAll(" ", "_").toLowerCase(); 
				Resource _b_coordinates = checkBlankNode(blank_coordinates, place_iri, model);
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

            	model.add(r_place, vocabulary.p168_place_is_defined_by, _b_coordinates);
				model.add(_b_coordinates, vocabulary.p190_has_symbolic_content, l_coordinates);
				
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
				String s_folios="";
				if(folios==""){
					s_folios = "all";
					
				}else{
					s_folios = folios.replaceAll(" ", "_").toLowerCase();
				}
				// Define all the iris
				String manuscript_iri = baseURI + "resources/manuscript/" + s_library_place + "/" + s_library + "/" + s_signature + "/" + s_folios;
				String manifestation_iri = baseURI + "resources/manifestation/manuscript/mm-" + m_count;
				String manifestation_creation_iri = baseURI + "resources/manifestation_creation/mmc-" + m_count;
				String signature_iri = baseURI + "resources/signature/ms-" + m_count;
				String folios_iri = baseURI + "resources/folios/mf-" + m_count;
				String incipit_dedication_iri = baseURI + "resources/incipit_dedication/mid-" + m_count;
				String explicit_dedication_iri = baseURI + "resources/explicit_dedication/med-" + m_count;
				String incipit_text_iri =  baseURI + "resources/incipit_text/mit-" + m_count;
				String explicit_text_iri =  baseURI + "resources/explicit_text/met-" + m_count;
				String date_manuscript_iri = baseURI + "resurces/manuscript/date/mdm-" + m_count;

				// Create all the resources
				Resource r_manuscript = null;
            	Resource r_manifestation = null;
				Resource r_library = null;
				Resource r_place_library = null;
				Resource r_signature = null;
				Resource r_folios = null;
				Resource r_incipit_dedication = null;
            	Resource r_explicit_dedication = null;
				Resource r_incipit_text = null;
				Resource r_explicit_text = null;
				Resource r_date_manuscript =  null;
				Resource r_manifestation_creation = null;
				Resource r_toponym_manuscript = null;

				r_manuscript = model.createResource(manuscript_iri);
            	r_manifestation = model.createResource(manifestation_iri);
				r_library = model.createResource(manuscript.getLibrary().getIri());
				r_place_library = model.createResource(manuscript.getLibrary().getPlace().getIri());
				r_signature = model.createResource(signature_iri);
				if(folios!=""){ r_folios = model.createResource(folios_iri); }
				if(incipit_dedication!=""){	 r_incipit_dedication = model.createResource(incipit_dedication_iri); }
            	if(explicit_dedication!=""){	 r_explicit_dedication = model.createResource(explicit_dedication_iri); }
				if(incipit_text!=""){	 r_incipit_text = model.createResource(incipit_text_iri); }
				if(explicit_text!=""){	 r_explicit_text = model.createResource(explicit_text_iri); }
				if(date_manuscript!=""){	 r_date_manuscript =  model.createResource(date_manuscript_iri); }
				r_manifestation_creation = model.createResource(manifestation_creation_iri); 
				r_toponym_manuscript = model.createResource(baseURI + "resources/toponym/" + manuscript.getLibrary().getPlace().getName().replaceAll(" ", "_").toLowerCase());

            	
				// Create all literals
				Literal l_manuscript_author = null;
				Literal l_manuscript_title = null;
				Literal l_coordinates = null;
				Literal l_library = null;
				Literal l_place = null;
				Literal l_signature = null;
				Literal l_folios = null;
				Literal l_incipit_dedication = null;
				Literal l_explicit_dedication = null;
				Literal l_incipit_text = null;
				Literal l_explicit_text = null;
				Literal l_date_manuscript =  null;
				Literal l_sources = null;
				Literal l_url_manuscript = null;
				Literal l_url_manuscript_description = null;
				Literal l_notes = null;

				

				if(manuscript_author_name!=""){ l_manuscript_author = model.createTypedLiteral(manuscript_author_name); }
				if(manuscript_title!=""){ l_manuscript_title = model.createTypedLiteral(manuscript_title); }
				l_coordinates = model.createTypedLiteral(toWKT(manuscript.getLibrary().getPlace().getLat(), manuscript.getLibrary().getPlace().getLon()));
				l_library = model.createTypedLiteral(manuscript.getLibrary().getName());
				l_place = model.createTypedLiteral(manuscript.getLibrary().getPlace().getName());
				l_signature = model.createTypedLiteral(signature);
				if(folios!=""){  l_folios = model.createTypedLiteral(folios); }
				if(incipit_dedication!=""){ l_incipit_dedication = model.createTypedLiteral(incipit_dedication); }
				if(explicit_dedication!=""){ l_explicit_dedication = model.createTypedLiteral(explicit_dedication); }
				if(incipit_text!=""){ l_incipit_text = model.createTypedLiteral(incipit_text); }
				if(explicit_text!=""){ l_explicit_text = model.createTypedLiteral(explicit_text); }
				if(date_manuscript!=""){ l_date_manuscript =  model.createTypedLiteral(date_manuscript); }
				l_sources = model.createTypedLiteral(sources);
				if(manuscript.getNotes()!="") l_notes = model.createTypedLiteral(manuscript.getNotes());
				if(manuscript.getUrl()!=""){
					l_url_manuscript = model.createTypedLiteral(manuscript.getUrl()); 
				}
				if(manuscript.getUrlDescription()!=""){
					l_url_manuscript_description =  model.createTypedLiteral(manuscript.getUrlDescription()); 
				}
				


				// Create all blank nodes
				Resource _b_manuscript_author = null;
				Resource _b_manuscript_title = null;
				Resource _b_folios = null;
				Resource _b_library = null;
				Resource _b_coordinates = null;

				if(manuscript_author_name!=""){ _b_manuscript_author = model.createResource(); }
				if(manuscript_title!=""){ _b_manuscript_title = model.createResource();  }
				if(folios!=""){ _b_folios = model.createResource(); }
				_b_library = checkBlankNode(blank_library, manuscript.getLibrary().getIri(), model);
				_b_coordinates = checkBlankNode(blank_coordinates, manuscript.getLibrary().getPlace().getIri(), model);

				// Declare all the statements rdf:type
				model.add(r_manuscript, RDF.type, vocabulary.manuscript);
				model.add(r_library, RDF.type, vocabulary.library);
				model.add(r_place_library, RDF.type, vocabulary.e53_place);
				model.add(r_signature, RDF.type, vocabulary.e42_identifier);
				if(folios!=""){ model.add(r_folios, RDF.type, vocabulary.folios); }
				if(incipit_dedication!=""){  model.add(r_incipit_dedication, RDF.type, vocabulary.e90_symbolic_object); }
				if(explicit_dedication!=""){ model.add(r_explicit_dedication, RDF.type, vocabulary.e90_symbolic_object); }
				if(incipit_text!=""){ model.add(r_incipit_text, RDF.type, vocabulary.e90_symbolic_object); }
				if(explicit_text!=""){ model.add(r_explicit_text, RDF.type, vocabulary.e90_symbolic_object); }
				model.add(r_manifestation, RDF.type, vocabulary.f3_manifestation);
				model.add(r_manifestation_creation, RDF.type, vocabulary.f30_manifestation_creation);
				if(date_manuscript!=""){ model.add(r_date_manuscript, RDF.type, vocabulary.e52_time_span); }

				// Declare all the triples
				model.add(r_manifestation, vocabulary.r7i_is_materialized_in, r_manuscript);
            	model.add(r_expression_creation, vocabulary.r18_created, r_manuscript);

				if(manuscript_author_name!=""){
					model.add(_b_manuscript_author, vocabulary.p106i_forms_part_of, r_manifestation);
					model.add(_b_manuscript_author, vocabulary.p190_has_symbolic_content, l_manuscript_author);
				}
				if(manuscript_title!=""){
					model.add(r_manuscript, vocabulary.p102_has_title, _b_manuscript_title);
					model.add(_b_manuscript_title, vocabulary.p190_has_symbolic_content, l_manuscript_title);
				}

				model.add(r_manuscript, vocabulary.p50_has_current_keeper, r_library);
            	model.add(r_library, vocabulary.p74_has_current_or_former_residence, r_place_library);
            	// model.add(r_library, vocabulary.p190_has_symbolic_content, l_library);
            	model.add(r_library, vocabulary.p1_is_identified_by, _b_library);
				model.add(_b_library, vocabulary.p190_has_symbolic_content, l_library);

				
				model.add(r_place_library, vocabulary.is_identified_by_toponym, r_toponym_manuscript);
				model.add(r_toponym_manuscript, vocabulary.p190_has_symbolic_content, l_place);
				model.add(r_place_library, vocabulary.p168_place_is_defined_by, _b_coordinates);
				model.add(_b_coordinates, vocabulary.p190_has_symbolic_content, l_coordinates);

				model.add(r_signature, vocabulary.p190_has_symbolic_content, l_signature);
            	model.add(r_manuscript, vocabulary.p1_is_identified_by, r_signature);
				if(folios!=""){
					model.add(r_manuscript, vocabulary.p46_is_composed_of, r_folios);
					model.add(r_folios, vocabulary.p1_is_identified_by, _b_folios);
					model.add(_b_folios, vocabulary.p190_has_symbolic_content, l_folios);
				}

				// incipit dedication
				if(incipit_dedication!=""){	
					model.add(r_incipit_dedication, vocabulary.p190_has_symbolic_content, l_incipit_dedication);
					model.add(r_incipit_dedication, vocabulary.is_incipit_dedication_of,r_manifestation);
				}

                //explicit proemio
				if(explicit_dedication!=""){	
					model.add(r_explicit_dedication, vocabulary.p190_has_symbolic_content, l_explicit_dedication);
					model.add(r_explicit_dedication, vocabulary.is_explicit_dedication_of,r_manifestation);
				}

				//incipit text
            	if(incipit_text!=""){	
					model.add(r_incipit_text, vocabulary.p190_has_symbolic_content, l_incipit_text);
					model.add(r_incipit_text, vocabulary.is_text_incipit_of, r_manifestation);
				}

                //explicit text
            	if(explicit_text!=""){	
					model.add(r_explicit_text, vocabulary.p190_has_symbolic_content, l_explicit_text);
					model.add(r_explicit_text, vocabulary.is_text_explicit_of,r_manifestation);
				}

				
				model.add(r_manifestation_creation, vocabulary.r24_created, r_manifestation);

				if(date_manuscript!=""){	
					model.add(r_manifestation_creation, vocabulary.p4_has_time_span, r_date_manuscript);
					model.add(r_date_manuscript, vocabulary.p170i_time_is_defined_by, l_date_manuscript);
				}

				if(sources!=""){
					model.add(r_manuscript, vocabulary.has_secondary_sources, l_sources);
				}

				if(manuscript.getUrl()!=""){
					model.add(r_manuscript, vocabulary.has_url_manuscript, l_url_manuscript);
				}

				if(manuscript.getUrlDescription()!=""){
					model.add(r_manuscript, vocabulary.has_url_manuscript_description, l_url_manuscript_description);
				}

				if(manuscript.getNotes()!=""){
					model.add(r_manuscript, vocabulary.p3_has_note, l_notes);
				}

            	
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
				String print_edition_iri = baseURI + "resources/print_edition/pe-" + p_count; 
				String print_edition_creation_iri = baseURI + "resources/manifestation_creation/pmc-" + p_count; 
				String curator_iri = baseURI + "resources/print_edition/curator/pc-" + p_count;
				String date_print_edition_iri = baseURI + "resurces/print_edition/date/pd-" + p_count;
				String publisher_iri = baseURI + "resources/print_edition/publisher/ppu-" + p_count;
				String format_iri = baseURI + "resources/print_edition/format/pf-" + p_count;
				String pages_iri = baseURI + "resources/print_edition/pages/ppg-" + p_count;
				String edition_iri = baseURI + "resources/print_edition/edition/" + s_edition;
				String ecdotic_iri = baseURI + "resources/print_edition/edition/" + s_ecdotic;
				String place_as_appear = baseURI + "resources/print_edition/place/ppaa-" + p_count;

				// Create all the resources
				Resource r_print_edition = null;
				Resource r_print_edition_creation = null;
				Resource r_curator = null;
				Resource r_place_print_edition = null;
				Resource r_date_print_edition =  null;
				Resource r_publisher = null;
				Resource r_format = null;
				Resource r_pages = null;
				Resource r_edition = null;
				Resource r_ecdotic = null;
				Resource r_place_as_appear = null;
				Resource r_toponym_print_edition = null;

				r_print_edition = model.createResource(print_edition_iri);
				r_print_edition_creation = model.createResource(print_edition_creation_iri);
				if(print_edition.getCurator()!="") { r_curator = model.createResource(curator_iri); }
				if(print_edition.getPlace().getName()!=null){r_place_print_edition = model.createResource(print_edition.getPlace().getIri()); }
				if(print_edition.getDateString()!=""){r_date_print_edition =  model.createResource(date_print_edition_iri); }
				if(print_edition.getEditor()!=""){ r_publisher = model.createResource(publisher_iri); }
				if(print_edition.getFormat()!=""){ r_format = model.createResource(format_iri); }
				if(print_edition.getPages()!=""){ r_pages = model.createResource(pages_iri); }
				if(edition!=""){ r_edition = model.createResource(edition_iri); }
				if(ecdotic!=""){ r_ecdotic = model.createResource(ecdotic_iri); }
				if(print_edition.getPlaceAsAppear()!=""){ r_place_as_appear = model.createResource(place_as_appear);}
				if(print_edition.getPlace().getName()!=null){
					r_toponym_print_edition = model.createResource(baseURI + "resources/toponym/" + print_edition.getPlace().getName().replaceAll(" ", "_").toLowerCase());
				}
				
				

				// Create all literals
				Literal l_author_print_edition = null;
				Literal l_title_print_edition = null;
				Literal l_curator = null;
				Literal l_place_name_as_appear = null;
				// System.out.println(print_edition.getPlace().getName());
				Literal l_place_print_edition = null;
				Literal l_coordinates = null;
				Literal l_date_print_edition = null;
				Literal l_publisher = null;
				Literal l_format = null;
				Literal l_pages = null;
				Literal l_figure = null;
				Literal l_notes = null;
				Literal l_prefatore = null;
				Literal l_edition = null;
				Literal l_date_edition = null;
				Literal l_primary_sources = null;
				Literal l_ecdotic = null;
				Literal l_sources = null;
				Literal l_other_contents = null;
				

				if(print_edition.getAuthor()!="") l_author_print_edition = model.createTypedLiteral(print_edition.getAuthor());
				if(print_edition.getTitle()!="")  l_title_print_edition = model.createTypedLiteral(print_edition.getTitle());
				if(print_edition.getCurator()!="") l_curator = model.createTypedLiteral(print_edition.getCurator());
				if(print_edition.getPlaceAsAppear()!="") l_place_name_as_appear = model.createTypedLiteral(print_edition.getPlaceAsAppear());
				if(print_edition.getPlace().getName()!=null){
					l_place_print_edition = model.createTypedLiteral(print_edition.getPlace().getName());
					l_coordinates = model.createTypedLiteral(toWKT(print_edition.getPlace().getLat(), print_edition.getPlace().getLon()));
				}
				// Literal l_place_print_edition = model.createTypedLiteral(print_edition.getPlace().getName());
				if(print_edition.getDateString()!="") l_date_print_edition = model.createTypedLiteral(print_edition.getDateString());
				if(print_edition.getEditor()!="") l_publisher = model.createTypedLiteral(print_edition.getEditor());
				if(print_edition.getFormat()!="") l_format = model.createTypedLiteral(print_edition.getFormat());
				if(print_edition.getPages()!="") l_pages = model.createTypedLiteral(print_edition.getPages());
				if(print_edition.getFigures()!="") l_figure = model.createTypedLiteral(print_edition.getFigures());
				if(print_edition.getNotes()!="") l_notes = model.createTypedLiteral(print_edition.getNotes());
				if(print_edition.getPrefator()!="") l_prefatore = model.createTypedLiteral(print_edition.getPrefator());
				if(edition!="") l_edition = model.createTypedLiteral(print_edition.getEdition());
				if(print_edition.getDateEdition()!="") l_date_edition = model.createTypedLiteral(print_edition.getDateEdition());
				if(print_edition.getPrimarySources()!="") l_primary_sources = model.createTypedLiteral(print_edition.getPrimarySources());
				if(ecdotic!="") l_ecdotic = model.createTypedLiteral(ecdotic);
				if(sources!="") l_sources = model.createTypedLiteral(sources);
				if(print_edition.getOtherContents()!="") l_other_contents = model.createTypedLiteral(print_edition.getOtherContents());
				

				// Create all blank nodes
				Resource _b_print_edition_author = null;
				Resource _b_print_edition_title = null;
				Resource _b_curator = null;
				Resource _b_publisher = null;
				Resource _b_format = null;
				Resource _b_pages = null;
				Resource _b_coordinates = null;

				if(print_edition.getAuthor()!="") _b_print_edition_author = model.createResource();
				if(print_edition.getTitle()!="") _b_print_edition_title = model.createResource();
				if(print_edition.getCurator()!="") _b_curator = model.createResource();
				if(print_edition.getEditor()!="") _b_publisher = model.createResource();
				if(print_edition.getFormat()!="") _b_format = model.createResource();
				if(print_edition.getPages()!="") _b_pages = model.createResource();
				_b_coordinates = checkBlankNode(blank_coordinates, print_edition.getPlace().getIri(), model);

				
				// Declare all the statements rdf:type
				model.add(r_print_edition_creation, RDF.type, vocabulary.f30_manifestation_creation);
				model.add(r_print_edition, RDF.type, vocabulary.print_edition);
				if(print_edition.getCurator()!="") model.add(r_curator, RDF.type, vocabulary.curator);
				if(print_edition.getPlace().getName()!=null) model.add(r_place_print_edition, RDF.type, vocabulary.e53_place);
				if(print_edition.getPlaceAsAppear()!="") model.add(r_place_as_appear, RDF.type, vocabulary.e41_appellation);
				if(print_edition.getDateEdition()!="") model.add(r_date_print_edition, RDF.type, vocabulary.e52_time_span);
				if(print_edition.getEditor()!="") model.add(r_publisher, RDF.type, vocabulary.publisher);
				if(print_edition.getFormat()!="") model.add(r_format, RDF.type, vocabulary.format);
				if(print_edition.getPages()!="") model.add(r_pages, RDF.type, vocabulary.e90_symbolic_object);
				if(edition!="") model.add(r_edition, RDF.type, vocabulary.edition);
				if(ecdotic!="") model.add(r_ecdotic, RDF.type, vocabulary.ecdotic_typology);

				// Declare all the triples
				model.add(r_print_edition, vocabulary.r4_embodies, r_work);
				model.add(r_print_edition_creation, vocabulary.r24_created, r_print_edition);
				if(print_edition.getAuthor()!=""){
					model.add(r_print_edition, vocabulary.p106_is_composed_of, _b_print_edition_author);
					model.add(_b_print_edition_author, vocabulary.p190_has_symbolic_content, l_author_print_edition);
				}
				if(print_edition.getTitle()!=""){
					model.add(r_print_edition, vocabulary.p102_has_title, _b_print_edition_title);
					model.add(_b_print_edition_title, vocabulary.p190_has_symbolic_content, l_title_print_edition);
				}
				if(print_edition.getCurator()!=""){
					model.add(r_print_edition_creation, vocabulary.has_curator, r_curator);
					model.add(r_curator, vocabulary.p1_is_identified_by, _b_curator);
					model.add(_b_curator, vocabulary.p190_has_symbolic_content, l_curator);
				}
				if(print_edition.getPlace().getName()!=null){
					model.add(r_print_edition_creation, vocabulary.p7_took_place_at, r_place_print_edition);
					model.add(r_place_print_edition, vocabulary.is_identified_by_toponym, r_toponym_print_edition);
					model.add(r_toponym_print_edition, vocabulary.p190_has_symbolic_content, l_place_print_edition);
					model.add(r_place_print_edition, vocabulary.p168_place_is_defined_by, _b_coordinates);
					model.add(_b_coordinates, vocabulary.p190_has_symbolic_content, l_coordinates);
					model.add(r_place_print_edition, vocabulary.is_identified_in_the_printed_edition_by, r_place_as_appear);
					model.add(r_place_as_appear, vocabulary.p190_has_symbolic_content, l_place_name_as_appear);
				}

				if(print_edition.getDateEdition()!=""){
					model.add(r_print_edition_creation, vocabulary.p4_has_time_span, r_date_print_edition);
					model.add(r_date_print_edition, vocabulary.p170i_time_is_defined_by, l_date_print_edition);
				}
				if(print_edition.getEditor()!=""){
				model.add(r_print_edition_creation, vocabulary.has_publisher, r_publisher);
				model.add(r_publisher, vocabulary.p1_is_identified_by, _b_publisher);
				model.add(_b_publisher, vocabulary.p190_has_symbolic_content, l_publisher);
				}		
				if(print_edition.getFormat()!=""){
					model.add(r_print_edition, vocabulary.r69_specifies_phisical_form, r_format);
					model.add(r_format, vocabulary.p1_is_identified_by, _b_format);
					model.add(_b_format, vocabulary.p190_has_symbolic_content, l_format);
				}
				if(print_edition.getPages()!=""){
					model.add(r_print_edition, vocabulary.p106_is_composed_of, r_pages);
					model.add(r_pages, vocabulary.p1_is_identified_by, _b_pages);
					model.add(_b_pages, vocabulary.p190_has_symbolic_content, l_pages);
				}
				if(print_edition.getFigures()!=""){
					model.add(r_print_edition, vocabulary.has_figure_note, l_figure);
				}
				if(print_edition.getNotes()!=""){
					model.add(r_print_edition, vocabulary.p3_has_note, l_notes);
				}
				if(print_edition.getPrefator()!=""){
					model.add(r_print_edition, vocabulary.has_introduction_note, l_prefatore);
				}
				if(edition!=""){
					model.add(r_print_edition, vocabulary.p2_has_type, r_edition);
					model.add(r_edition, vocabulary.p190_has_symbolic_content, l_edition);
				}
				if(print_edition.getDateEdition()!=""){
					model.add(r_print_edition, vocabulary.has_reprint_date, l_date_edition);
				}
				if(print_edition.getPrimarySources()!=""){
					model.add(r_print_edition, vocabulary.has_primary_source, l_primary_sources);
				}
				if(ecdotic!=""){
					model.add(r_print_edition, vocabulary.p2_has_type, r_ecdotic);
					model.add(r_ecdotic, vocabulary.p190_has_symbolic_content, l_ecdotic);
				}
				if(sources!=""){
					model.add(r_print_edition, vocabulary.has_secondary_sources, l_sources);
				}
				if(print_edition.getOtherContents()!=""){
					model.add(r_print_edition, vocabulary.has_other_contents, l_other_contents);
				}

				
				// TO-DO 

				// -[ ] Other contents


				p_count++;

			}




		}
		return model;
	}


}
