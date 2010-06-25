/* CVS $Id: $ */
 
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;
 
/**
 * Vocabulary definitions from http://www.w3.org/2006/time 
 * @author Auto-generated by schemagen on 25 Jun 2010 15:11 
 */
public class Time {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2006/time#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final ObjectProperty after = m_model.createObjectProperty( "http://www.w3.org/2006/time#after" );
    
    public static final ObjectProperty before = m_model.createObjectProperty( "http://www.w3.org/2006/time#before" );
    
    public static final ObjectProperty dayOfWeek = m_model.createObjectProperty( "http://www.w3.org/2006/time#dayOfWeek" );
    
    public static final ObjectProperty hasBeginning = m_model.createObjectProperty( "http://www.w3.org/2006/time#hasBeginning" );
    
    public static final ObjectProperty hasDateTimeDescription = m_model.createObjectProperty( "http://www.w3.org/2006/time#hasDateTimeDescription" );
    
    public static final ObjectProperty hasDurationDescription = m_model.createObjectProperty( "http://www.w3.org/2006/time#hasDurationDescription" );
    
    public static final ObjectProperty hasEnd = m_model.createObjectProperty( "http://www.w3.org/2006/time#hasEnd" );
    
    public static final ObjectProperty inDateTime = m_model.createObjectProperty( "http://www.w3.org/2006/time#inDateTime" );
    
    public static final ObjectProperty inside = m_model.createObjectProperty( "http://www.w3.org/2006/time#inside" );
    
    public static final ObjectProperty intervalAfter = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalAfter" );
    
    public static final ObjectProperty intervalBefore = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalBefore" );
    
    public static final ObjectProperty intervalContains = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalContains" );
    
    public static final ObjectProperty intervalDuring = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalDuring" );
    
    public static final ObjectProperty intervalEquals = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalEquals" );
    
    public static final ObjectProperty intervalFinishedBy = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalFinishedBy" );
    
    public static final ObjectProperty intervalFinishes = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalFinishes" );
    
    public static final ObjectProperty intervalMeets = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalMeets" );
    
    public static final ObjectProperty intervalMetBy = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalMetBy" );
    
    public static final ObjectProperty intervalOverlappedBy = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalOverlappedBy" );
    
    public static final ObjectProperty intervalOverlaps = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalOverlaps" );
    
    public static final ObjectProperty intervalStartedBy = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalStartedBy" );
    
    public static final ObjectProperty intervalStarts = m_model.createObjectProperty( "http://www.w3.org/2006/time#intervalStarts" );
    
    public static final ObjectProperty timeZone = m_model.createObjectProperty( "http://www.w3.org/2006/time#timeZone" );
    
    public static final ObjectProperty unitType = m_model.createObjectProperty( "http://www.w3.org/2006/time#unitType" );
    
    public static final DatatypeProperty day = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#day" );
    
    public static final DatatypeProperty dayOfYear = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#dayOfYear" );
    
    public static final DatatypeProperty days = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#days" );
    
    public static final DatatypeProperty hour = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#hour" );
    
    public static final DatatypeProperty hours = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#hours" );
    
    public static final DatatypeProperty inXSDDateTime = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#inXSDDateTime" );
    
    public static final DatatypeProperty minute = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#minute" );
    
    public static final DatatypeProperty minutes = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#minutes" );
    
    public static final DatatypeProperty month = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#month" );
    
    public static final DatatypeProperty months = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#months" );
    
    public static final DatatypeProperty second = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#second" );
    
    public static final DatatypeProperty seconds = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#seconds" );
    
    public static final DatatypeProperty week = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#week" );
    
    public static final DatatypeProperty weeks = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#weeks" );
    
    public static final DatatypeProperty xsdDateTime = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#xsdDateTime" );
    
    public static final DatatypeProperty year = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#year" );
    
    public static final DatatypeProperty years = m_model.createDatatypeProperty( "http://www.w3.org/2006/time#years" );
    
    public static final OntClass DateTimeDescription = m_model.createClass( "http://www.w3.org/2006/time#DateTimeDescription" );
    
    public static final OntClass DateTimeInterval = m_model.createClass( "http://www.w3.org/2006/time#DateTimeInterval" );
    
    public static final OntClass DayOfWeek = m_model.createClass( "http://www.w3.org/2006/time#DayOfWeek" );
    
    public static final OntClass DurationDescription = m_model.createClass( "http://www.w3.org/2006/time#DurationDescription" );
    
    public static final OntClass Instant = m_model.createClass( "http://www.w3.org/2006/time#Instant" );
    
    public static final OntClass Interval = m_model.createClass( "http://www.w3.org/2006/time#Interval" );
    
    public static final OntClass January = m_model.createClass( "http://www.w3.org/2006/time#January" );
    
    public static final OntClass ProperInterval = m_model.createClass( "http://www.w3.org/2006/time#ProperInterval" );
    
    public static final OntClass TemporalEntity = m_model.createClass( "http://www.w3.org/2006/time#TemporalEntity" );
    
    public static final OntClass TemporalUnit = m_model.createClass( "http://www.w3.org/2006/time#TemporalUnit" );
    
    public static final OntClass Year = m_model.createClass( "http://www.w3.org/2006/time#Year" );
    
    public static final Individual Friday = m_model.createIndividual( "http://www.w3.org/2006/time#Friday", DayOfWeek );
    
    public static final Individual Monday = m_model.createIndividual( "http://www.w3.org/2006/time#Monday", DayOfWeek );
    
    public static final Individual Saturday = m_model.createIndividual( "http://www.w3.org/2006/time#Saturday", DayOfWeek );
    
    public static final Individual Sunday = m_model.createIndividual( "http://www.w3.org/2006/time#Sunday", DayOfWeek );
    
    public static final Individual Thursday = m_model.createIndividual( "http://www.w3.org/2006/time#Thursday", DayOfWeek );
    
    public static final Individual Tuesday = m_model.createIndividual( "http://www.w3.org/2006/time#Tuesday", DayOfWeek );
    
    public static final Individual Wednesday = m_model.createIndividual( "http://www.w3.org/2006/time#Wednesday", DayOfWeek );
    
    public static final Individual unitDay = m_model.createIndividual( "http://www.w3.org/2006/time#unitDay", TemporalUnit );
    
    public static final Individual unitHour = m_model.createIndividual( "http://www.w3.org/2006/time#unitHour", TemporalUnit );
    
    public static final Individual unitMinute = m_model.createIndividual( "http://www.w3.org/2006/time#unitMinute", TemporalUnit );
    
    public static final Individual unitMonth = m_model.createIndividual( "http://www.w3.org/2006/time#unitMonth", TemporalUnit );
    
    public static final Individual unitSecond = m_model.createIndividual( "http://www.w3.org/2006/time#unitSecond", TemporalUnit );
    
    public static final Individual unitWeek = m_model.createIndividual( "http://www.w3.org/2006/time#unitWeek", TemporalUnit );
    
    public static final Individual unitYear = m_model.createIndividual( "http://www.w3.org/2006/time#unitYear", TemporalUnit );
    
}
