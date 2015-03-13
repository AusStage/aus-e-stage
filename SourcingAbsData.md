<h1>Sourcing Census Data</h1>

One of the many and varied goals of the Mapping Service is to develop a series overlays. These overlays will be used to add information to a map to assist users in gaining the most insight from the AusStage data.

One of the first sets overlays that were developing are derived from data obtained from the [Australian Bureau of Statistics](http://www.abs.gov.au/) and in particular the [2006 Census](http://www.abs.gov.au/census).

The purpose of this page is to outline the procedure for collecting data for use with the AbsDataFix application and the construction of overlays.

It is recommended that you print this page or open the link to the ABS in a new window.

_Note 1: Only those datasets that the AbsDataFix application has been configured to work with can be turned into overlays_

_Note 2: Only users who have access to the TableBuilder site will be able to use this procedure_



# Access the ABS TableBuilder website #
  1. Enter the following URL into the address bar of your browser http://www.abs.gov.au/TableBuilder
  1. Click the **Enter TABLEBUILDER** button
  1. Enter your email address into the **Email address** field
  1. Enter your password into the **Password** field
  1. Click the **Login** button

# Construct an Age by Sex Dataset #

## Step 1 - Select the Database ##
  1. Select the **Counting Persons, Please of Usual Residence** database
  1. Click the **Next** button

## Step 2 - Select the Rows ##
  1. Click the arrow to the left of the **Geographic Areas (Please of Usual Residence)** item in the tree on the left hand side of the page
  1. Click the arrow to the left of the **Main ASGC** item in the tree
  1. Locate the Australian state that is of interest
  1. Click the arrow to the right of the name of the state
  1. Select the **CD Level** option in the drop down box that appeared at step 2.4
  1. Click the **Add to Row** button above the tree on the left hand side of the page
  1. Click the **OK** button on the **Entering Large Table Preview Mode** window when it appears
  1. Click the **Collapse All** link above the tree on the left hand side of the page

## Step 3 - Select the Columns ##
  1. Click the arrow to the left of the **Person Variables** item in the tree on the left hand side of the page
  1. Click the arrow to the left of the **People Characteristics** item in the tree
  1. Click the arrow to the right of the **SEXP Sex** item in the tree
  1. Select the **SEXP** option in the drop down box that appeared in step 3.3
  1. Click the **Add to Column** button above the tree on the left hand side of the page
  1. Click the arrow to the right of the **AGEP Age (5 Year Groups)** item in the tree
  1. Select the **AGEP - 5 Year Age Groups** option in the drop down box that appeared in step 3.3
  1. Click the **Add to Column** button above the tree on the left hand side of the page

## Step 4 - Export the Data ##
  1. Select the **Comma Seperated Value (.csv)** item from the **Submit Table** drop down box
  1. Click the **Queue Job** button located to the right of the **Submit Table** drop down box
  1. Enter a name for the table in the pop-up window that appeared in step 4.3
  1. Click the **Queue Job** button
  1. Click the **My Tables** link located to the left of the **Submit Table** drop down box
  1. Periodically refresh the page (approximately once ever 30 seconds) until the **Completed, Click here to download** link appears in the content status of the **Queued Jobs** table
  1. Click the **Completed, Click here to download** link
  1. Save the file in to your computer

## Step 5 - Prepare the Data for Use ##
  1. Extract the csv file from the zip file saved in step 4.8
  1. Copy the csv file to the input directory for use by the AbsDataFix application
  1. Rename the csv file so that the name is all lower case and does not contain spaces

