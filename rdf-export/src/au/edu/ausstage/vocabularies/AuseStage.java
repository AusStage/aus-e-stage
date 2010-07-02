/*
 * This file is part of the AusStage RDF Vocabularies Package
 *
 * The AusStage RDF Vocabularies Package is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage RDF Vocabularies Package is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage RDF Vocabularies Package.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.vocabularies;
 
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;
 
/**
 * Vocabulary definitions from http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology
 */
public class AuseStage {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /*
     * contributors 
     */
    
    /** Specify the nationality of a contributor */
    public static final ObjectProperty nationality = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#nationality");
    
    /** Specify the functions that a contributor can undertake */
    public static final ObjectProperty function = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#function");
    
    /** Specify the number of contributors the specified contributor has worked with */
    public static final ObjectProperty collaboratorCount = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#collaboratorCount");
    
    /*
     * collaborations
     */
    
    /** Specify the details of a collaboration relationships **/
    public static final ObjectProperty collaboration = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#collaboration");
    
    /** Specify the contributor involved in a collaboration **/
    public static final ObjectProperty collaborator = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#collaborator");
    
    /** Specify the number of times two contributors have collaborated */
    public static final ObjectProperty collaborationCount = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#collaborationCount");
    
    /** Specify the relationship between a contributor and a collaboration */
    public static final ObjectProperty hasCollaboration = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#hasCollaboration");
    
    /*
     * events
     */
    
    /*
     * Need to redefine these better
     */
//    
//    /** Specify that a contributor has collaborated on an event */
//    public static final ObjectProperty hasCollaborated = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#hasCollaborated");
//    
//    /** Specify that a contributor collaborates on an event */
//    public static final ObjectProperty collaboration = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#collaboration");
//    
//    /** Specify which event the contributor has collaborated on */
//    public static final ObjectProperty onEvent = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#onEvent");
//    
//    /** Specify the type of function the contributor had when they collaborated on an event */
//    public static final ObjectProperty functionAtEvent = m_model.createObjectProperty("http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#functionAtEvent");
    

}
