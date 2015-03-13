

# Edge List Export #

Note, that this export format exports the entire list of relationships in the dataset. This results in a large file that may take some time to process and download. It is highly recommended that this API be used sparingly and be used on a fast broadband connection.

Alternative static snapshots of this data may be downloaded from the [downloads](http://code.google.com/p/aus-e-stage/downloads/list) section of this website. These static snapshots are only updated on request.

## Base URL ##

The base URL for the Lookup API is as follows:

http://beta.ausstage.edu.au/networks/export?

## Request Type ##

Get Request

## Available Parameters ##

| **Name** | **Possible Values** | **Optional** | **Default** |
|:---------|:--------------------|:-------------|:------------|
| task    | full-edge-list-with-dups, full-edge-list-no-dups, full-edge-list-with-dups-id-only, full-edge-list-no-dups-id-only <br /> actor-edge-list-with-dups, actor-edge-list-no-dups, actor-edge-list-with-dups-id-only, actor-edge-list-no-dups-id-only| No |  |

## Parameter Definitions ##

### Task: full-edge-list-with-dups ###

A full export of the dataset in the edge list format containing duplicate relationships.

### Task: full-edge-list-no-dups ###

A full export of the dataset in the edge list format that does not contain duplicate relationships.

### Task: full-edge-list-with-dups-id-only ###

A full export of the dataset in the edge list format that contains ids only.

### Task: full-edge-list-no-dups-id-only ###

A full export of the dataset in the edge list format that does not contain duplicate relationships and that contains ids only.

### Task: actor-edge-list-with-dups ###

An export of the dataset in the edge list format containing duplicate relationships between contributors with a function of "actor" or "actor and singer".

### Task: actor-edge-list-no-dups ###

An export of the dataset in the edge list format that does not contain duplicate relationships between contributors with a function of "actor" or "actor and singer".

### Task: actor-edge-list-with-dups-id-only ###

An export of the dataset in the edge list format that contains ids only between contributors with a function of "actor" or "actor and singer".

### Task: actor-edge-list-no-dups-id-only ###

An export of the dataset in the edge list format that does not contain duplicate relationships and that contains ids only between contributors with a function of "actor" or "actor and singer".

## Sample Output ##

### Full edge lists ###

```
id \t given_name \t family_name \t id \t given_name \t family_name \n
```

### Full edge lists (id only) ###

```
id \t id \n
```

### Actor edge lists ###

```
id \t given_name \t family_name \t gender \t id \t given_name \t family_name \t gender \n
```

### Actor edge lists (id only) ###

```
id \t id \n
```