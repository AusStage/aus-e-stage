

# Introduction #

Below is a series of instructions and guidance on using the [Export Graph Data](http://beta.ausstage.edu.au/networks/export.jsp) page to export data in  a variety of different formats for use with network visualisation software.

# Getting Started #

Before you can view any of the exported data, you will need to download and install one of the following graph visualisation programs.

  * [Gephi](http://gephi.org/): Open-source network visualization software. Download [here](http://gephi.org/users/download/)
  * [Visone](http://visone.info/doku.php?id=index): Java based network visualization software. Download the Visone web starter [here](http://visone.info/jaws/visone.jnlp). The Visone web starter is the recommended way to run Visone. Each time you run the web starter, it will check for updates and install them before running Visone.

# Exporting the Graph Data #

Using the [the Export Graph Data page](http://beta.ausstage.edu.au/networks/export.jsp)  is relatively simple.

**If you know the contributor id**

  1. If you know the id of the contributor whos network you wish to graph, enter the id in the _Contributor Id_ field.
  1. Then, click the _Lookup_ button situated to the right of the _Contributor Name_ field, this checks that the contributor id relates to a contributor and you should see the contributors name appear in the _Contributor Name_ field.

**If you don't know the contributor id**

  1. If you don't know the id of the contributor who's network you would like to graph, click on the _Lookup_ button situated to the right of the _Contributor Name_ field.
  1. A popup window should appear with a single field for Contributor Name.
  1. Enter the name of the contributor you wish to graph and click the _Search_ button.
  1. After a few moments the window will show matching results.
  1. If the contributor you wish to graph is returned, click the _Choose_ button relating to that contributor.
  1. The popup window will disappear, and both the _Contributor Id_ and _Contributor Name_ fields will be populated.

**After selecting your contributor**

  1. You then need to select the desired data format from the drop down list.  The data format you choose should depend on the software you are using to visualize the graph. Currently the only option is graphml whic is compatible with both Gephi and Visone.
  1. Finally, you need to choose the desired radius of your graph. This relates to how many edges are between the central node (_the contributor_) and the outer lying nodes of the network. For instance, a radius of 2 will give you a network of all the people your selected contributor has worked with, and all the people they have worked with.
  1. Then click the _Export_ button and in a few moments the file will be downloaded to your computer.
  1. The file should appear as **ausstage-graph-_contributorId_-degrees-_numOfDegrees_.graphml** where _contributorId_ is the contributor id you selected and _numOfDegrees_ is the radius you selected.

Mac Users please note: If you are using Chrome or Safari on a Mac computer to download the Network Data you will need to remove the .xml extension that is automatically added to the file by the browser. The best way to do this is to right-click on the file, and select _Get Info_ from the popup menu. In the info window, scroll down to Name & Extension. Remove the .xml extension from the file name.

# Using the Graph Data #
## Gephi ##
The following guide assumes you have completed the following:
  1. Downloaded and Installed [Gephi](http://gephi.org/users/download/)
  1. Used the [the Export Graph Data page](http://beta.ausstage.edu.au/networks/export.jsp) to download a set of network data in .graphml format.
  1. If you are not already familiar with Gephi, it is advised you work through some of the tutorials available [here](http://gephi.org/users/) in order to get the most out of visualizing your network data.

The following steps will guide you through loading your network data into Gephi.
  1. Locate and run Gephi on you computer.
  1. A dialogue box will appear offering you the following options
    * Open Recent
    * New Project
    * Samples
  1. Select _Open Graph File_ under the _New Project_ heading
  1. Another dialogue box will appear titled _Open_. Change the File Format to _GraphML Files (`*`.graphml)_.
  1. Using the _Open_ dialogue box, locate and select the .graphml file you wish to visualize. Then click the _Open_ button.
  1. Another dialogue box will appear titled _Import report_. Click the _OK_ button
  1. Your network data should now be displayed within Gephi as a visualisation.

**Adding two networks to the same visualisation**

Once you have imported one set of data into the visualisation you may wish to add another data set to the visualisation.
This can be done very simply using the following steps.
  1. With your current project in Gephi displayed, go to the File Menu and select _Open_
  1. Locate the other .graphml file you wish to model.
  1. Click _Open_.
  1. The _Import report_ dialogue box will appear.
  1. In the bottom right hand corner of this box will be two options
    * Add full graph
    * Append graph
  1. Ensure _Append graph_ is selected.
  1. Click _OK_
  1. The additional data should now be displayed in the visualisation.

## Visone ##
The following guide assumes you have completed the following:
  1. Downloaded and run the [Visone Java Web Starter](http://visone.info/jaws/visone.jnlp)
  1. Used the [the Export Graph Data page](http://beta.ausstage.edu.au/networks/export.jsp) to download a set of network data in .graphml format.
  1. If you are not already familiar with Visone, it is advised you read through the documentation available [here](http://visone.info/doku.php?id=docs:firststeps:index) in order to get the most out of visualizing your network data.

The following steps will guide you through loading your network data into Visone
  1. Locate and run visone.jnlp on your computer.
    * Depending on your operating system, you may get a message similar to the following
> > > _The application "Visone" from "visone.info" is requesting access to your computer.
> > > The digital signature could not be verified._

> > If this occurs, check the box that will allow this in future and click the _Allow_ button
  1. Once Visone has loaded, go to the _File_ menu and select _Open_.
  1. The _Open_ dialogue box will appear. Use this to locate and select the .graphml file you wish to visualize.
  1. Click the _OK_ button in the _Open_ dialogue box.
  1. Your network data will then be loaded into Visone as a visualisation.
Please Note: Visone initially loads this data very simply, so you may on first load see just one coloured box. Using the  visualization and layout parameters within Visone will alter the visualisation.

**Adding two networks to the same visualisation**

Once you have imported one set of data into the visualisation you may wish to add another data set to the visualisation.
This can be done very simply using the following steps.
  1. With your current project in Visone displayed, go to the File Menu and select _Open_
  1. Locate the other .graphml file you wish to model.
  1. Click _Open_.
  1. You will see Visone has created a separate visualization.
  1. Go to the _Edit_ menu and select _Select All_
  1. Also in the _Edit_ menu select _Copy_
  1. Return to your original visualization by clicking it's title listed above the visualization window.
  1. Go to the _Edit_ menu and select _Paste_.
  1. Both networks are now in the same visualization space for manipulation.