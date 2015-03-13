<h1>Using our Subversion Repository</h1>

One of the many benefits of using the [Google Project Hosting](http://code.google.com/projecthosting/) service is that it provides a [Revision Control](http://en.wikipedia.org/wiki/Revision_control) or Version Control system for our source code. For our project we're using the [Subversion](http://subversion.apache.org/) system.

On the UsefulLinks page there is a list of links for more information about [Subversion](UsefulLinks#Apache_Subversion_Information.md).

The benefits of revision control are many, including the capability to share source code with others and ensure that everyone has the most up to date version of the code. To ensure this it is necessary to use a client application that is able to _checkout_ files from the repository, _update_ the locale copies and _commit_ changes back to the repository.



# Checking out Source Code #
Checking out source code is the process of copying all files from the repository to your local computer in what is known as a _working copy_. A working copy contains all of the required information to keep track of changes to the code and submit these back to the repository when required.

# Updating the Source Code #
Updating is the process of downloading any changes that have occurred since the last _check out_ including changes such as new files, changes to files, and the deletion of files.

When changes to the files in the working copy aren't reflected in the updates the client can merge the changes or alert you to any inconsistencies

# Committing Changes to the Source Code #
Committing is the process of uploading the changes in your local working copy to the repository for other to download including changes such as new files, changes to files, and the deletion of files.

# Structure of our Repository #
The parent directory of our repository is the **trunk** directory. Everything related to our source code is stored in sub directories of this one directory. The current sub directories are:

| **Main Directory** | Purpose |
|:-------------------|:--------|
| abs-data-fix | The AbsDataFix application |
| exchange | The source code for the DataExchangeService |
| exchange-analytics | The ExchangeAnalytics service used to build the analytics report for the ExchangeDataService |
| google-analytics   | the GoogleAnalytics application used to build the analytics reports about the use of the Mapping Service |
| mapping | The source code for the MappingService |
| mobile | The source code for the MobileService |
| root-web | The source code for the http://beta.ausstage.edu.au website |
| terminator | The source code for the AusStageTerminator service |
| xslt-transforms | Various XSLT files used for transforming XML as part of our procedures |

# Checking Out Source Code from Our Repository #
It is recommended that if you check out code from our repository that you do so one main directory at a time, this helps in managing access to the repository.

Directions for checking out source code using the command line subversion client is available on the [Checkout](http://code.google.com/p/aus-e-stage/source/checkout) page of this website.