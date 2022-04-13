package cnr.isti.aimh.imago.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

import cnr.isti.aimh.imago.pojo.Toponym;

public class JacksonImport {
    
    public static void main(String[] args) throws IOException {
	
		//read json file data to String
		byte[] jsonData = Files.readAllBytes(Paths.get("toponyms.json"));
		
		//create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();
		
		//convert json string to object
//		LemmaPrincipale emp = objectMapper.readValue(jsonData, LemmaPrincipale.class);
		List<Toponym> emp = objectMapper.readValue(jsonData, new TypeReference<List<Toponym>>(){});
		

        OntModel model = Model.populateModel(Model.importModel("https://imagoarchive.it/onto/IMAGO-270222.ttl"), emp);

		// model.write(System.out, "TTL") ;

		// for (Toponym e : emp) {
		// 	  // code block to be executed
		// 		System.out.println("Lemma Object\n"+e );
		// 	}

        // Controllo se il modello � valido
		// InfModel infmodel = ModelFactory.createRDFSModel(model);
		// ValidityReport validity = infmodel.validate();
		// if (validity.isValid()) {
		// 	// System.out.println("VALIDAZIONE - Il modello è valido\n");
		// }
		// else {
		// 	// STAMPA ERRORI DI VALIDAZIONE
		// 	Iterator<Report> reports = validity.getReports();
		// 	while(reports.hasNext()) {
		// 		// System.out.println("\nERRORE - Il modello non � valido: " + reports.next() + "\n");
		// 	}
		// }

        String datasetURL = "https://imagoarchive.it/fuseki/imago";
		String sparqlEndpoint = datasetURL + "/sparql";
		String sparqlUpdate = datasetURL + "/update";
		String graphStore = datasetURL + "/data";
		
		RDFConnection conneg = RDFConnectionFactory.connectPW(datasetURL,"fusekitomcat", "C4mbiam1Adess0" );
		conneg.update("CLEAR DEFAULT" );
        conneg.load(model); // add the content of model to the triplestore

        }
}
