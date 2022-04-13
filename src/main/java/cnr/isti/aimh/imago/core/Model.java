package cnr.isti.aimh.imago.core;

import java.util.HashMap;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import cnr.isti.aimh.imago.pojo.Toponym;
import cnr.isti.aimh.imago.util.Vocabulary;

public class Model {
    
    // Impostare una variabile con l'uri del progetto
	// al momento questo � l'uri temporaneo
	private static String baseURI = "https://imagoarchive.it/ontology/";
	
	
	// Questa funzione crea e importa l'ontologia OWL
	public static OntModel importModel(String url) {
		OntModel onto = ModelFactory.createOntologyModel(
		        OntModelSpec.OWL_DL_MEM, null );
		onto.read(url); // Legge l'ontologia passata tramite l'url
		return onto; // restituisce l'ontologia letta
		
	}
	
	// Questa funzione popola l'ontologia
	// Viene passato il modello con l'ontologia e la lista con il json parsato
	public static OntModel populateModel(OntModel model, List<Toponym> toponyms) {
		
		// Vengono dichiarate le risorse da creare
		Resource resource_Toponym = null; 
		Resource resource_Toponym_it = null; 
		Resource resource_Toponym_en = null; 
		Literal literal_Toponym = null; 
		Literal literal_Toponym_it = null; 
		Literal literal_Toponym_en = null; 
		Resource resource_Work = null; 
		Resource resource_Coordinates = null; 
		Resource resource_Fragment = null;
		Resource resource_Place = null;
		Resource resource_Pleiades= null;
		Literal literal_Context = null;
		Literal literal_TexualPlace = null;
		Literal literal_VDL = null;

		int fragmentID = 1;
		HashMap<String, Resource> places = new HashMap<String, Resource>();
		Vocabulary vocabulary = new Vocabulary(model);

		
		// Inizio a scorrere i lemmi
		for (Toponym e : toponyms) {
			
			// System.out.println(e);
			

			// String toponymURI = baseURI + "resources/" + e.getName().replaceAll(" ", "_").toLowerCase(); 
			String toponymURI = e.getName().replaceAll(" ", "_").toLowerCase(); 
			// String toponymURI_it = baseURI + "resources/" + e.getLabelIta().replaceAll(" ", "_").toLowerCase(); 
			// String toponymURI_en = baseURI + "resources/" + e.getLabelEng().replaceAll(" ", "_").toLowerCase(); 
			String fragmentURI= baseURI + "resources/fragment" + fragmentID;
			fragmentID++;

			
			// CREAZIONE RISORSE
			resource_Toponym = model.createResource(toponymURI);
			literal_Toponym = model.createLiteral(e.getName(), "la");
			// resource_Toponym_it = model.createResource(toponymURI_it);
			literal_Toponym_it = model.createLiteral(e.getLabelIta(), "it");
			// resource_Toponym_en = model.createResource(toponymURI_en);
			literal_Toponym_en = model.createLiteral(e.getLabelEng(), "en");
			resource_Work = model.createResource(e.getWork().getIri());
			resource_Place = model.createResource(e.getIriWD());
			resource_Coordinates = model.createResource(baseURI + e.getLatitude() + "," + e.getLongitude());
			resource_Fragment = model.createResource(fragmentURI);
			literal_Context =  model.createTypedLiteral(e.getContext());
			literal_TexualPlace = model.createTypedLiteral(e.getPlaceText());
			literal_VDL = model.createTypedLiteral(e.getVDLlemma());
			resource_Pleiades = model.createResource(e.getPleiades());

			resource_Toponym.addProperty(RDFS.label, literal_Toponym);
			if(e.getLabelIta()!="") {
				resource_Toponym.addProperty(RDFS.label, literal_Toponym_it);
			}
			if(e.getLabelEng()!="") {
				resource_Toponym.addProperty(RDFS.label, literal_Toponym_en);
			}
			
			// Triple RDF.type
			model.add(resource_Toponym, RDF.type, vocabulary.toponym);
			model.add(resource_Work, RDF.type, vocabulary.f2_expression);
			model.add(resource_Place, RDF.type, vocabulary.e53_place);
			model.add(resource_Pleiades, RDF.type, vocabulary.e53_place);
			model.add(resource_Coordinates, RDF.type, vocabulary.e94_space_primitive);
			model.add(resource_Fragment, RDF.type, vocabulary.e90_symbolic_object);
			model.add(resource_Place, OWL.sameAs, resource_Pleiades);

			if(places.get(e.getIriWD())!=null){
				// System.out.println(e.getIriWD());
				model.add(resource_Toponym, OWL.sameAs, places.get(e.getIriWD()));
			}
			// model.add(resource_Toponym, OWL.sameAs, resource_Toponym_it);
			// model.add(resource_Toponym, OWL.sameAs, resource_Toponym_en);

			// Aggiungo le triple
			model.add(resource_Work, vocabulary.p67_refers_to, resource_Place);
			model.add(resource_Place, vocabulary.is_identified_by_toponym, resource_Toponym);
			model.add(resource_Pleiades, vocabulary.is_identified_by_toponym, resource_Toponym);
			model.add(resource_Place, vocabulary.p168_place_is_defined_by, resource_Coordinates);
			model.add(resource_Work, vocabulary.p106_is_composed_of, resource_Toponym);
			model.add(resource_Fragment, vocabulary.p106_is_composed_of, resource_Toponym);
			model.add(resource_Work, vocabulary.r15_has_fragment, resource_Fragment);
			model.add(resource_Fragment, vocabulary.p190_has_symbolic_content, literal_Context);
			model.add(resource_Fragment, vocabulary.has_textual_place, literal_TexualPlace);
			model.add(resource_Toponym, vocabulary.has_vdl_explanation, literal_VDL);
			
			places.put(e.getIriWD(), resource_Toponym);
		
		}
		return model;
	}
	


}
