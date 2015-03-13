
---

_NOTE:_ This procedure document is deprecated due to a change in direction for the development of overlays for maps using ABS data.

---


<h1>AbsDataFix - Building Map Overlays using ABS Data</h1>

The AbsDataFix application has been designed to provide a flexible framework for the manipulation of data sourced from the [Australian Bureau of Statistics (ABS)](http://www.abs.gov.au) on the 2006 census.

The data is manipulated in a variety of different ways to build overlays for use with [Google Earth](http://earth.google.com) and other GIS enabled systems. Datasets are also constructed so that the [AusStage Mapping Service](http://beta.ausstage.edu.au/mapping/) can be used to dynamically populate the infoWindows in each overlay.

Outlined below are the steps necessary to use the AbsDataFix application to prepare a dataset sourced from the ABS using the SourcingAbsData procedure.

_Note 1: This procedure assumes that you are familiar with executing commands in a command line environment such as a terminal or command prompt window_

_Note 2: Ensure that all file and directory names do not include spaces_

_Note 3: When specifying file names on the command line ensure that you specify the full path or relative path to the file_

_Note 4: 'state-abbr' in the following instructions should be replaced with the appropriate state abbreviation - sa, qld, act, nsw etc..._



# Step 1. Prepare your System #
  1. Ensure that you have the latest version of [Java](http://java.com) installed and properly configured on your system. The AbsDataFix application was written using Java version 1.6.
  1. These procedures assume that you have created a directory structure that starts with an **abs-data-fix** directory and has the following child directories
    1. **bin** for storing the executable
    1. **input** for storing the input files
    1. **output** for storing the output files
    1. **repository** for storing finalised files

# Step 2. Download and install the AbsDataFix application #
  1. Enter the following URL into the address bar of your browser http://code.google.com/p/aus-e-stage/
  1. Download the AbsDataFix-#.zip file listed under the **Featured Downloads** heading where # is the version number
  1. Extract the contents of the archive and and copy them into the **bin** directory created in step 1

# Step 3. Building First Stage XML File #
  1. Choose one of the following steps to create the required dataset

## Step 3a. Build an Age by Sex Dataset ##
  1. Copy the file downloaded at the end of the SourcingAbsData procedure into the **input** directory.
  1. Name the file _state-abbr_-age-by-sex.csv
  1. Open a terminal window / command prompt
  1. Change directory to the **bin** directory
  1. Execute the following command where:
    1. input-file: is the name of the input file, namely _state-abbr_-age-by-sex.csv
    1. output-file: is the name of the output XML file to create. _state-abbr_-age-by-sex.xml _Note: Specify the **output** directory as part of the path_
> > `java -jar AbsDataFix.jar -fixtype agebysex -input input-file -output output-file`
  1. If an error occurs adjust the parameters and try again
  1. Take note of the file that was successfully created
  1. Move the XML file to the **repository** directory

# Step 4. Start a Collection District Dataset #
Collection District Dataset is an XML based file that is used for further processing and for use by the AusStage Mapping Service for the population of infoWindows in Google Earth.

This step will only need to be undertaken once for each state using the Age by Sex Dataset created in step 3a.

  1. Copy the XML file _state-abbr_-age-by-sex.xml from the **repository** to the **input** directory
  1. If necessary, open a terminal window / command prompt
  1. If necessary, change directory to the **bin** directory
  1. Execute the following command where:
    1. input-file: is _state-abbr_-age-by-sex.xml
    1. output-file: is the name of the output XML file to create _Note: Specify the **output** directory as part of the path_
> > `java -jar AbsDataFix.jar -fixtype databuilder -input input-file -output output-file`
  1. If an error occurs adjust the parameters and try again
  1. Take note of the file that was successfully created

# Step 5. Append additional information to the Collection District Dataset #

The Collection District Dataset requires additional information such as the Statistical Local Area and Local Government Area names. The easiest source of data to use for this step is the MapInfo files available from the [ABS website](http://www.abs.gov.au/AUSSTATS/abs@.nsf/DetailsPage/1259.0.30.0022006)

This step will only need to be undertaken once for each state using the Collection District Dataset created in step 4.

## Step 5.a Download the MapInfo File for the state currently being processed ##

  1. Download the Zip file containing the MapInfo data for the state that is currently being worked on
  1. Extract the files contained in the Zip File
  1. Copy the extracted MID file into the **input** directory

## Step 5.b Use the MapInfo data to complete the Collection District Dataset ##

  1. Move the XML file created in step 4 to the **input** directory
  1. If necessary, open a terminal window / command prompt
  1. If necessary, change directory to the **bin** directory
  1. Execute the following command where:
    1. input-file: is the file copied to the input directory in step 5.b.1
    1. output-file: is the name of the output XML file to create, in this case abs-data-_state-abbr_.xml _Note: Specify the **output** directory as part of the path_
    1. code-file: is the map info file copied into the **input** directory in step 5a.
> > `java -jar AbsDataFix.jar -fixtype appendcdinfo -input input-file -output output-file -codes code-file`
  1. If an error occurs adjust the parameters and try again
  1. Take note of the file that was successfully created

## Step 5.c Store the completed Collection District Dataset ##

  1. Move the XML file created in step 5.b to the **repository** directory

This file will form the basis for the Collection District Dataset that will be used by the AusStage Mapping Service to populate infoWindows and other web pages

# Step 6. Append the Dataset to the Collection District Dataset #

The ABS provides a variety of datasets that we will be making use of to construct overlays. Each new type of dataset will need to be appended to the Collection District Dataset file so that infoWindows and other parts of the Mapping Service can be maintained.

The AbsDataFix application does not undertake this task currently, once the second dataset has been decided upon the application will be amended.

# Step 7. Prepare the KML files #

The MapInfo files provided by the ABS need to be converted into KML so that they can be used with applications such as Google Earth. Details of the way in which this conversion is achieved is available on the MapInfoToKML page.

Once the conversion is complete the KML files are prepared for further processing specific to the AbsDataFix application. This step undertakes these changes and will only need to be undertaken once for each base KML file.

  1. Copy the KML file into the **input** directory
  1. If necessary, open a terminal window / command prompt
  1. If necessary, change directory to the **bin** directory
  1. Execute the following command where:
    1. input-file: is the file copied to the input directory in step 7.1
    1. output-file: is the name of the output KML file to create, _state-abbr_-abs-base.kml _Note: Specify the **output** directory as part of the path_
> > `java -Xmx512m -jar AbsDataFix.jar -fixtype prepkml -input input-file -output output-file`
> > <br />_Note: This may take some time to complete_
  1. If an error occurs adjust the parameters and try again
  1. Take note of the file that was successfully created
  1. Move this file into the **repository** directory.

# Step 8. Compile an overlay #
Combining a prepared KML file with one of the [AbsDataFix##Step\_3._Building\_First\_Stage\_XML\_File First Stage XML Files] will produce the finalised overlay for that dataset._

## Step 8a. Building an Age By Sex Overlay ##
  1. Copy the base KML file, as created in step 7, for the required state from the **repository** directory to the **input** directory
  1. If necessary, copy the Age By Sex dataset, as created in step 3a from the **repository** directory to the **input** directory
  1. If necessary, open a terminal window / command prompt
  1. If necessary, change directory to the **bin** directory
  1. Execute the following command where:
    1. input-file: is the file copied to the input directory in step 8a.1
    1. output-file: is the name of the output XML file to create _Note: Specify the **output** directory as part of the path_
    1. code-file: is the file copied into the **input** directory in step 8a.2
    1. data-set: is one of the following options
      * male: male ages
      * female: female ages
      * total: total population ages
> > `java -Xmx512m -jar AbsDataFix.jar -fixtype mapagebysex -input input-file -output output-file -codes code-file -dataset data-set`
> > <br />_Note: This may take some time to complete_
  1. If an error occurs adjust the parameters and try again
  1. Take note of the file that was successfully created

# Step 9. Add Dublin Core Metadata to the Overlay file #
  1. Copy the KML file created in step 8 to the **input** directory from the **output** directory
  1. If necessary, open a terminal window / command prompt
  1. If necessary, change directory to the **bin** directory
  1. Execute the following command where:
    1. input-file: is the file copied to the input directory in step 9.1
    1. output-file: is the name of the output XML file to create _Note: Specify the **output** directory as part of the path_
> > `java -Xmx512m -jar AbsDataFix.jar -fixtype addmetadata -input input-file -output output-file`
  1. Enter the title of the ABS Data Set used to construct the overlay, for example:<br />Age By Sex in 5 year groups (Male)
  1. Enter a description of the ABS Data Set used to construct the overlay, for example:<br />Average age male population
  1. Enter the 2 - 3 letter State abbreviation, for example:<br />ACT
  1. Enter the full URL to the web page containing more information about the ABS overlays, for example: http://beta.ausstage.edu.au/mapping/absdatasets.jsp
  1. Enter the full URL to the KML file in the Overaly repository, for example: _Note: The full URL to the KML file in the overlay repository has yet to be determined._
  1. Check to ensure the information that you've entered is correct
  1. If the information is correct, enter y
  1. If the information is not correct enter n and repeat steps 9.5 - 9.10
  1. If you wish to exit the application, enter abort
  1. Wait for the file to complete
  1. Copy this file into the **repository** directory and rename it as follows where:
    1. state-abbr: is the state abbreviation for example qld, nsw, sa etc.
    1. dataset: is the dataset used in step 8
> > `abs-state-abbr-dataset-age-by-sex.kml`

# Change Log #
See the source code repository for a [of commit messages http://code.google.com/p/aus-e-stage/source/list](list.md) related to the AbsDataFix App
## Version 1.0.5 ##
  * Added a more informative error message when a file expected to be XML is found not to be
## Version 1.0.4 ##
  * Refined the colour algorithm for the AgeBySex dataset
  * Removed the data table from the HTML used to populate infoWindows
  * Added the list of average ages and population counts to populate infoWindows
  * Added link to the new page in the mapping service about the ABS Datasets
  * Various minor bug fixes
## Version 1.0.3 ##
  * Various minor bugfixes
  * Include the first version of the AddMetadata Fix
## Version 1.0.2 ##
  * Various minor bug fixes
## Version 1.0.1 ##
  * Various minor bug fixes
## Version 1.0 ##
  * Initial Release





