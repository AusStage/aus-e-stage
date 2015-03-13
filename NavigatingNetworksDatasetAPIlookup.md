

<h1> Lookup API </h1>

The lookup API provide the functionality to lookup a defined dataset for a given contributor. The details of the API are as follows:

## Base URL ##

The base URL for the Lookup API is as follows:

http://beta.ausstage.edu.au/networks/lookup?

Combining this URL with the parameters outlines below retrieves data from the [Navigating Networks Dataset](NavigatingNetworksDataset.md).

## Available Parameters ##

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| [task](NavigatingNetworksDatasetAPIlookup#task.md)   | key-collaborators, system-property, collaborator, collaboration | No |  |
| [id](NavigatingNetworksDatasetAPIlookup#id.md)     | Unique contributor id / system property id | No |  |
| [format](NavigatingNetworksDatasetAPIlookup#format.md) | html xml json | Yes | html |
| [sort](NavigatingNetworksDatasetAPIlookup#sort.md)   | count id name | Yes | count |
| [callback](NavigatingNetworksDatasetAPIlookup#callback.md) | function name to use for a JSONP request | Yes |  |

### task ###
The task parameter defines the type of lookup that is undertaken. For example key-contributors is a lookup that retrieves all of the contributors that a specified contributor has collaborated with.
Collaboration task will return a list of event details by clicking the link between two contributors in the contributor network.

### id ###
The id parameter defines the unique identifier of the contributor that is of interest.

_Note:_ if the task is a `system-property` lookup the following are valid ids

| **id** | **Lookup Value** |
|:-------|:-----------------|
| datastore-create-date | The date and time that the RDF data store was created |

_Note:_ if the task is a `collaboration` lookup, the following are valid ids

| **id** | **Lookup Value** |
|:-------|:-----------------|
| 8374-249989 | 8374 is the first contributor id and 249989 is the second contributor id|



### format ###
The format parameter defines what format the resulting data with be presented in.

  * html - a basic table format for direct inclusion in a HTML page
  * xml  - a list of objects serialised in a basic [XML based format](http://en.wikipedia.org/wiki/XML)
  * json - an array of objects serialised in the [JSON format](http://en.wikipedia.org/wiki/JSON)

### sort ###
The way in which the data is sorted before it is returned

  * count - the number of collaborations (most frequent, to least frequent)
  * id    - by contributor id (ascending order)
  * name  - by name (alphabetical order by last name)

### callback ###
When data is requested using the [JSON](http://en.wikipedia.org/wiki/JSON) format the callback parameter specifies the function name to use for a [JSONP](http://en.wikipedia.org/wiki/JSON#JSONP) request.

## Sample Output ##

The following is abbreviated sample outputs in response to the following base API calls:

### html ###
http://beta.ausstage.edu.au/networks/lookup?task=key-collaborators&id=102

```
<table id="key-collaborators">
  <tr id="key-collaborator-644">
    <td><a href="http://www.ausstage.edu.au/..." title="View Rodney Fisher record in AusStage">Rodney Fisher</a></td>
    <td>15 May 1979 - 19 March 1983</td>
    <td>Playwright <br/> Director <br/> Writer</td>
  </tr>
</table>
```

### xml ###
http://beta.ausstage.edu.au/networks/lookup?task=key-collaborators&id=102&format=xml

```
<collaborators>
  <collaborator id="644">
    <url>http://www.ausstage.edu.au/...</url>
    <name>Rodney Fisher</name>
    <function>Playwright | Director | Writer</function>
    <firstDate>1979-05-15</firstDate>
    <lastDate>1983-03-19</lastDate>
    <collaborations>48</collaborations>
  </collaborator>
</collaborators>
```

### json ###
http://beta.ausstage.edu.au/networks/lookup?task=key-collaborators&id=102&format=json

```
[
  {
    "id":"644",
    "firstDate":"1979-05-15",
    "name":"Rodney Fisher",
    "lastDate":"1983-03-19",
    "collaborations":48,
    "url":"http:\/\/www.ausstage.edu.au\/...",
    "function":"Playwright | Director | Writer"
  }
]
```