

<h1>Protovis API</h1>

The Protovis API provides the functionality export data in a format that can be used with the [Protovis](http://vis.stanford.edu/protovis/) visualisation library.

The details of the API are as follows:

## Base URL ##

The base URL for the Lookup API is as follows:

http://beta.ausstage.edu.au/networks/protovis?

Combining this URL with the parameters outlines below retrieves data from the [Navigating Networks Dataset](NavigatingNetworksDataset.md).

## Available Parameters ##

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task   | ego-centric-network / event-centric-network | No |  |
| id     | unique contributor id / event id | No |  |
| radius | Integer between 1 and 3 |	 Yes | 1 |
| simplify | Boolean | Yes | true |
| callback | function name to use for a JSONP request | Yes |  |

### task ###
The task parameter defines the type of export that is undertaken. Currently two tasks are available. They are ego-centric network and event-centric-network.

### id ###
The id parameter defines the unique identifier of the contributor or event that is of interest.

### radius ###
The desired number of edges between the central node and the peripheral nodes of the network.

### simplify ###
This parameter is only valid with event to event 2nd degree network (task = event-centric-network && radius = 2 )

simplify = true : with each of the 1st degree nodes, only events for those contributors involved in the central node are retrieved.

simplify = false: with each of the 1st degree nodes, events for all contributors are retrieved.

### callback ###
When data is requested using the [JSON](http://en.wikipedia.org/wiki/JSON) format the callback parameter specifies the function name to use for a [JSONP](http://en.wikipedia.org/wiki/JSON#JSONP) request.


## Sample Output ##

The following is abbreviated sample output in response to the following API call:


http://beta.ausstage.edu.au/networks/protovis?task=ego-centric-network&id=414321

```
{
    "edges": [
        {
            "firstDate": "1989-07-27",
            "source": 1,
            "target": 7,
            "lastDate": "2001-11-04",
            "value": 14
        },
        {
            "firstDate": "2001-10-21",
            "source": 1,
            "target": 16,
            "lastDate": "2008-05-24",
            "value": 2
        },
        {
            "firstDate": "2005-07-18",
            "source": 1,
            "target": 19,
            "lastDate": "2005-07-20",
            "value": 1
        },
        {
            "firstDate": "2000-03-02",
            "source": 1,
            "target": 20,
            "lastDate": "2000-03-02",
            "value": 1
        },
        ...
        {
            "firstDate": "2010-08-05",
            "source": 114,
            "target": 115,
            "lastDate": "2010-08-21",
            "value": 1
        }
    ],
    "nodes": [
        {
            "id": "414321",
            "functions": [
                "Actor"
            ],
            "nodeUrl": "http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&amp;f_contrib_id=414321",
            "nodeName": "Brad Williams"
        },
        {
            "id": "533",
            "functions": [
                "Director",
                "Set designer",
                "Designer",
                "Costume Designer"
            ],
            "nodeUrl": "http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&amp;f_contrib_id=533",
            "nodeName": "Tim Maddock"
        },
        {
            "id": "611",
            "functions": [
                "Playwright",
                "Director",
                "Assistant Director"
            ],
            "nodeUrl": "http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&amp;f_contrib_id=611",
            "nodeName": "Cath McKinnon"
        },
        ...
        {
            "id": "440145",
            "functions": [
                
            ],
            "nodeUrl": "http:\/\/www.ausstage.edu.au\/indexdrilldown.jsp?xcid=59&amp;f_contrib_id=440145",
            "nodeName": "Bevan Emmett"
        }
    ]
}
```