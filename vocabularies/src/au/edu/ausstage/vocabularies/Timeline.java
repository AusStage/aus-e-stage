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
 * Vocabulary definitions from http://motools.sf.net/timeline/timeline.179.n3 
 * @author Auto-generated by schemagen on 25 Jun 2010 15:22 
 */
public class Timeline {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/NET/c4dm/timeline.owl#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final ObjectProperty after = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#after" );
    
    public static final ObjectProperty before = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#before" );
    
    public static final ObjectProperty contains = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#contains" );
    
    /** <p>associates a timeline map to its domain timeline</p> */
    public static final ObjectProperty domainTimeLine = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#domainTimeLine" );
    
    public static final ObjectProperty during = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#during" );
    
    public static final ObjectProperty equals = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#equals" );
    
    public static final ObjectProperty finishedBy = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#finishedBy" );
    
    public static final ObjectProperty finishes = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#finishes" );
    
    public static final ObjectProperty meets = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#meets" );
    
    public static final ObjectProperty metBy = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#metBy" );
    
    public static final ObjectProperty onTimeLine = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#onTimeLine" );
    
    public static final ObjectProperty overlappedBy = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#overlappedBy" );
    
    public static final ObjectProperty overlaps = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#overlaps" );
    
    /** <p>associates a timeline map to its range timeline</p> */
    public static final ObjectProperty rangeTimeLine = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#rangeTimeLine" );
    
    public static final ObjectProperty startedBy = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#startedBy" );
    
    public static final ObjectProperty starts = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#starts" );
    
    /** <p>Relates an interval or an instant to the timeline on which it is defined. 
     *  The 29th of August, 2007 would be linked through this property to the universal 
     *  timeline, whereas "from 2s to 5s on this particular signal" would be defined 
     *  on the signal' timeline.</p>
     */
    public static final ObjectProperty timeline = m_model.createObjectProperty( "http://purl.org/NET/c4dm/timeline.owl#timeline" );
    
    /** <p>refers to a point or an interval on the time line, through an explicit datatype</p> */
    public static final DatatypeProperty at = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#at" );
    
    /** <p>A subproperty of :at, allowing to address a date (beginning of it for an instant, 
     *  all of it for an interval)</p>
     */
    public static final DatatypeProperty atDate = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#atDate" );
    
    /** <p>This property links an instant defined on the universal time line to an XSD 
     *  date/time value</p>
     */
    public static final DatatypeProperty atDateTime = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#atDateTime" );
    
    /** <p>A property enabling to adress a time point P through the duration of the interval 
     *  [0,P] on a continuous timeline</p>
     */
    public static final DatatypeProperty atDuration = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#atDuration" );
    
    /** <p>A subproperty of :at, having as a specific range xsd:int</p> */
    public static final DatatypeProperty atInt = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#atInt" );
    
    /** <p>subproperty of :at, having xsd:float as a range</p> */
    public static final DatatypeProperty atReal = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#atReal" );
    
    /** <p>A subproperty of :at, allowing to address a year (beginning of it for an instant, 
     *  all of it for an interval)</p>
     */
    public static final DatatypeProperty atYear = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#atYear" );
    
    /** <p>A subproperty of :at, allowing to address a year/month (beginning of it for 
     *  an instant, all of it for an interval)</p>
     */
    public static final DatatypeProperty atYearMonth = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#atYearMonth" );
    
    public static final DatatypeProperty beginsAt = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#beginsAt" );
    
    /** <p>A subproperty of :beginsAt, allowing to address the beginning of an interval 
     *  as a date/time</p>
     */
    public static final DatatypeProperty beginsAtDateTime = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime" );
    
    /** <p>A property enabling to adress a start time point P of an interval [P,E] through 
     *  the duration of the interval [0,P] on a continuous timeline</p>
     */
    public static final DatatypeProperty beginsAtDuration = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#beginsAtDuration" );
    
    /** <p>A subproperty of :beginsAt, having xsd:int as a range</p> */
    public static final DatatypeProperty beginsAtInt = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#beginsAtInt" );
    
    /** <p>associate a shift map to a particular delay</p> */
    public static final DatatypeProperty delay = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#delay" );
    
    /** <p>the duration of a time interval</p> */
    public static final DatatypeProperty duration = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#duration" );
    
    /** <p>A subproperty of :duration, having xsd:int as a range</p> */
    public static final DatatypeProperty durationInt = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#durationInt" );
    
    /** <p>A subproperty of :duration, having xsd:duration as a range</p> */
    public static final DatatypeProperty durationXSD = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#durationXSD" );
    
    /** <p>refers to the end of a time interval, through an explicit datatype. time:hasEnd 
     *  can be used as well, if you want to associate the end of the interval to an 
     *  explicit time point resource</p>
     */
    public static final DatatypeProperty end = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#end" );
    
    public static final DatatypeProperty endsAt = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#endsAt" );
    
    /** <p>A subproperty of :endsAt, allowing to address the end of an interval as a 
     *  date/time</p>
     */
    public static final DatatypeProperty endsAtDateTime = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime" );
    
    /** <p>A property enabling to adress an end time point P of an interval [S,P] through 
     *  the duration of the interval [0,P] on a continuous timeline</p>
     */
    public static final DatatypeProperty endsAtDuration = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#endsAtDuration" );
    
    /** <p>A subproperty of :endsAt, having xsd:int as a range</p> */
    public static final DatatypeProperty endsAtInt = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#endsAtInt" );
    
    /** <p>hop size, associated to a uniform windowing map</p> */
    public static final DatatypeProperty hopSize = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#hopSize" );
    
    /** <p>associate an origin map to its origin on the domain physical timeline</p> */
    public static final DatatypeProperty origin = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#origin" );
    
    /** <p>associates a sample rate value to a uniform sampling map</p> */
    public static final DatatypeProperty sampleRate = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#sampleRate" );
    
    /** <p>refers to the beginning of a time interval, through an explicit datatype. 
     *  time:hasBeginning can be used as well, if you want to associate the beginning 
     *  of the interval to an explicit time point resource</p>
     */
    public static final DatatypeProperty start = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#start" );
    
    /** <p>window length, associated to a uniform windowing map</p> */
    public static final DatatypeProperty windowLength = m_model.createDatatypeProperty( "http://purl.org/NET/c4dm/timeline.owl#windowLength" );
    
    /** <p>An instant defined on an abstract timeline</p> */
    public static final OntClass AbstractInstant = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#AbstractInstant" );
    
    /** <p>An interval defined on an abstract time-line.</p> */
    public static final OntClass AbstractInterval = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#AbstractInterval" );
    
    /** <p>Abstract time lines may be used as a backbone for Score, Works, ... This allows 
     *  for TimeLine maps to relate works to a given performance (this part was played 
     *  at this time).</p>
     */
    public static final OntClass AbstractTimeLine = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#AbstractTimeLine" );
    
    /** <p>A continuous timeline, like the universal one, or the one backing an analog 
     *  signal</p>
     */
    public static final OntClass ContinuousTimeLine = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#ContinuousTimeLine" );
    
    /** <p>An instant defined on a discrete timeline</p> */
    public static final OntClass DiscreteInstant = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#DiscreteInstant" );
    
    /** <p>An interval defined on a discrete timeline, like the one backing a digital 
     *  signal</p>
     */
    public static final OntClass DiscreteInterval = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#DiscreteInterval" );
    
    /** <p>A discrete time line (like the time line backing a digital signal</p> */
    public static final OntClass DiscreteTimeLine = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#DiscreteTimeLine" );
    
    /** <p>An instant (same as in OWL-Time)</p> */
    public static final OntClass Instant = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#Instant" );
    
    /** <p>An interval (same as in OWL-Time). Allen's relationships are defined in OWL-Time.</p> */
    public static final OntClass Interval = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#Interval" );
    
    /** <p>A timeline map linking a physical timeline to a relative one (originating 
     *  at some point on the physical timeline)</p>
     */
    public static final OntClass OriginMap = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#OriginMap" );
    
    /** <p>A "physical" time-line (the universal time line (UTC)) is an instance of this 
     *  class. Other time zones consists in instances of this class as well, with 
     *  a "shifting" time line map relating them to the universal time line map.</p>
     */
    public static final OntClass PhysicalTimeLine = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#PhysicalTimeLine" );
    
    /** <p>An instant defined on a relative timeline</p> */
    public static final OntClass RelativeInstant = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#RelativeInstant" );
    
    /** <p>an interval defined on a relative timeline</p> */
    public static final OntClass RelativeInterval = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#RelativeInterval" );
    
    /** <p>Semi infinite time line...canonical coordinate system --&gt; adressed through 
     *  xsd:duration since the instant 0.</p>
     */
    public static final OntClass RelativeTimeLine = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#RelativeTimeLine" );
    
    /** <p>a map just shifting one timeline to another</p> */
    public static final OntClass ShiftMap = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#ShiftMap" );
    
    /** <p>Represents a linear and coherent piece of time -- can be either abstract (such 
     *  as the one behind a score) or concrete (such as the universal time line). 
     *  Two timelines can be mapped using timeline maps.</p>
     */
    public static final OntClass TimeLine = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#TimeLine" );
    
    /** <p>Allows to map two time lines together</p> */
    public static final OntClass TimeLineMap = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#TimeLineMap" );
    
    /** <p>This concept expresses that an instant defined on the universal timeline must 
     *  be associated to a dateTime value</p>
     */
    public static final OntClass UTInstant = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#UTInstant" );
    
    /** <p>an interval defined on the universal time line</p> */
    public static final OntClass UTInterval = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#UTInterval" );
    
    /** <p>Describe the relation between a continuous time-line and its sampled equivalent</p> */
    public static final OntClass UniformSamplingMap = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#UniformSamplingMap" );
    
    /** <p>Describes the relation between a continuous time-line, and a time-line that 
     *  corresponds to its sampled and windowed equivalent</p>
     */
    public static final OntClass UniformSamplingWindowingMap = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#UniformSamplingWindowingMap" );
    
    /** <p>Describes the relation between a discrete time line and its windowed equivalent</p> */
    public static final OntClass UniformWindowingMap = m_model.createClass( "http://purl.org/NET/c4dm/timeline.owl#UniformWindowingMap" );
    
    /** <p>this is the `universal' time line -- can adress time intervals on it using 
     *  date/dateTime -- UTC</p>
     */
    public static final Individual universaltimeline = m_model.createIndividual( "http://purl.org/NET/c4dm/timeline.owl#universaltimeline", PhysicalTimeLine );
    
}