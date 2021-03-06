package cnr.isti.aimh.imago.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class Vocabulary {

    
	// Prefixes
	public static final String foaf = "http://xmlns.com/foaf/0.1/";
	public static final String efrbroo = "http://erlangen-crm.org/efrbroo/";
    public static final String ilrmoo = "http://imagoarchive.it/ilrmoo/";
	public static final String cnt = "http://www.w3.org/2011/content#";
	public static final String ecrm = "http://erlangen-crm.org/200717/";
	public static final String crminf = "https://dlnarratives.eu/crminf/";
	public static final String dc = "http://purl.org/dc/elements/1.1/";
	public static final String dctypes = "http://purl.org/dc/dcmitype/";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String dcterms = "http://purl.org/dc/terms/";
	public static final String narra = "https://dlnarratives.eu/ontology#";
	public static final String wikidata = "http://wikidata.org/entity/";
	public static final String time = "http://www.w3.org/2006/time#";
	public static final String imago = "https://www.imagoarchive.it/ontology/";

  // Resources eCRM
  public Resource e19_physical_object;
  public Resource e35_title;
  public Resource e39_actor;
  public Resource e41_appellation;
  public Resource e42_identifier;
  public Resource e52_time_span;
  public Resource e53_place;
  public Resource e90_symbolic_object;
  public Resource e94_space_primitive;

  // Resources eFRBRoo
  public Resource f2_expression;
  public Resource f3_manifestation;
  public Resource f5_item;
  public Resource f11_corporate_body;
  public Resource f28_expression_creation;
  public Resource f30_manifestation_creation;

  // Resources imago
  public Resource folios;
  public Resource genre;
  public Resource toponym;
  public Resource curator;
  public Resource manuscript;
  public Resource print_edition;

  // Properties eCRM
  public Property p1_is_identified_by;
  public Property p2_has_type;
  public Property p14_carried_out_by;
  public Property p46_is_composed_of;
  public Property p50_has_current_keeper;
  public Property p67_refers_to;
  public Property p74_has_current_or_former_residence;
  public Property p102_has_title;
  public Property p106_is_composed_of;
  public Property p106i_forms_part_of;
  public Property p168_place_is_defined_by;
  public Property p190_has_symbolic_content;

  // Properties eFRBRoo
  public Property r4_embodies;
  public Property r7i_is_materialized_in;
  public Property r15_has_fragment;
  public Property r17_created;
  public Property r18_created;

  // Properties imago
  public Property has_genre;
  public Property has_genre_name;
  public Property has_textual_place;
  public Property has_vdl_explanation;
  public Property is_identified_by_toponym;
  public Property is_incipit_dedication_of;
  public Property is_explicit_dedication_of;
  public Property is_text_incipit_of;
  public Property is_text_explicit_of;


  public Vocabulary(Model model){

    // Resources eCRM
    this.e19_physical_object = model.getResource(Vocabulary.ecrm + "E19_Physical_Object");
    this.e35_title = model.getResource(Vocabulary.ecrm + "E35_Title");
    this.e39_actor = model.getResource(Vocabulary.ecrm + "E39_Actor");
    this.e41_appellation = model.getResource(Vocabulary.ecrm + "E41_Appellation");
    this.e42_identifier = model.getResource(Vocabulary.ecrm + "E42_Identifier");
    this.e52_time_span = model.getResource(Vocabulary.ecrm + "E52_Time_Span");
    this.e53_place = model.getResource(Vocabulary.ecrm + "E53_Place");
    this.e90_symbolic_object = model.getResource(Vocabulary.ecrm + "E90_Symbolic_Object");
    this.e94_space_primitive = model.getResource(Vocabulary.ecrm + "E94_Space_Primitive");

    // Resources eFRBRoo
    this.f2_expression = model.getResource(Vocabulary.ilrmoo + "F2_Expression");
    this.f3_manifestation = model.getResource(Vocabulary.ilrmoo + "F3_Manifestation");
    this.f5_item = model.getResource(Vocabulary.ilrmoo + "F5_Item");
    this.f11_corporate_body = model.getResource(Vocabulary.ilrmoo + "F11_Corporate_Body");
    this.f28_expression_creation = model.getResource(Vocabulary.ilrmoo + "F28_Expression_Creation");
    this.f30_manifestation_creation = model.getResource(Vocabulary.ilrmoo + "F30_Manifestation_Creation");

    // Resources imago
    this.folios = model.getResource(Vocabulary.imago + "Folios");
    this.genre = model.getResource(Vocabulary.imago + "Genre");
    this.toponym = model.getResource(Vocabulary.imago + "Toponym");
    this.curator = model.getResource(Vocabulary.imago + "Curator");
    this.manuscript = model.getResource(Vocabulary.imago + "Manuscript");
    this.print_edition = model.getResource(Vocabulary.imago + "PrintEdition");

    // Properties eCRM
    this.p1_is_identified_by = model.getProperty(Vocabulary.ecrm + "P1_is_identified_by");
    this.p2_has_type = model.getProperty(Vocabulary.ecrm + "P2_has_type");
    this.p14_carried_out_by = model.getProperty(Vocabulary.ecrm + "P14_carried_out_by");
    this.p46_is_composed_of = model.getProperty(Vocabulary.ecrm + "P46_is_composed_of");
    this.p50_has_current_keeper = model.getProperty(Vocabulary.ecrm + "P50_has_current_keeper");
    this.p67_refers_to = model.getProperty(Vocabulary.ecrm + "P67_refers_to");
    this.p74_has_current_or_former_residence = model.getProperty(Vocabulary.ecrm + "P74_has_current_or_former_residence");
    this.p102_has_title = model.getProperty(Vocabulary.ecrm + "P102_has_title");
    this.p106_is_composed_of = model.getProperty(Vocabulary.ecrm + "P106_is_composed_of");
    this.p106i_forms_part_of = model.getProperty(Vocabulary.ecrm + "P106i_forms_part_of");
    this.p168_place_is_defined_by = model.getProperty(Vocabulary.ecrm + "P168_place_is_defined_by");
    this.p190_has_symbolic_content = model.getProperty(Vocabulary.ecrm + "P190_has_symbolic_content");

    // Properties eFRBRoo

    this.r4_embodies = model.getProperty(Vocabulary.ilrmoo + "R4embodies");
    this.r7i_is_materialized_in = model.getProperty(Vocabulary.ilrmoo + "R7i_isMaterializedIn");
    this.r15_has_fragment = model.getProperty(Vocabulary.ilrmoo + "R15_has_fragment");
    this.r17_created = model.getProperty(Vocabulary.ilrmoo + "R17_created");
    this.r18_created = model.getProperty(Vocabulary.ilrmoo + "R18_created");

    // Properties imago
    this.has_genre = model.getProperty(Vocabulary.imago + "has_genre");
    this.has_genre_name = model.getProperty(Vocabulary.imago + "has_genre_name");
    this.has_textual_place = model.getProperty(Vocabulary.imago + "has_textual_place");
    this.has_vdl_explanation = model.getProperty(Vocabulary.imago + "has_vdl_explanation");
    this.is_identified_by_toponym = model.getProperty(Vocabulary.imago + "isIdentifiedByToponym");
    this.is_incipit_dedication_of = model.getProperty(Vocabulary.imago + "is_incipit_dedication_of");
    this.is_explicit_dedication_of = model.getProperty(Vocabulary.imago + "is_explicit_dedication_of");
    this.is_text_incipit_of = model.getProperty(Vocabulary.imago + "is_text_incipit_of");
    this.is_text_explicit_of = model.getProperty(Vocabulary.imago + "is_text_explicit_of");

  }

    
}
