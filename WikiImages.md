<h1>Using Images in the Wiki</h1>

There are two ways to use images in our Wiki here on the [Google Project Hosting](http://code.google.com/projecthosting/) website. They are detailed below:

# Using an image already online #

To use an image that is already online use the [Links to images Wiki Syntax](http://code.google.com/p/support/wiki/WikiSyntax#Links_to_images) detailed on the [WikiSyntax](http://code.google.com/p/support/wiki/WikiSyntax) page.

# Uploading an image for use in the Wiki #

Uploading an image for use in the Wiki is slightly more complicated. First you must be familiar with the use of [Subversion](http://subversion.apache.org/).

## Checkout the wiki-assets directory ##

Using Subversion checkout the following section of our repository:

https://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/

Instructions on how to checkout from the repository is available via the following URL once you have successfully logged into this website:

http://code.google.com/p/aus-e-stage/source/checkout

## Add the image to the repository ##

Copy the image that you intend to use into the `wiki-assets` directory and add it to the repository.

_Note:_ Ensure the name of the image **does not** contain spaces and is all in lower case. If necessary replace spaces with a dash so that:

example Image.jpg

Becomes:

example-image.jpg

For example if you were using the command line version of Subversion you would copy the file and then execute the following command in the `wiki-assets` directory:

`svn add example-image.jpg`

## Set the Mime-Type property for the image ##

Once the file is added to the repository update the mime-type using one of the options below

| **File Extension** | **Mime-Type** |
|:-------------------|:--------------|
| .jpg | image/jpeg |
| .png | image/png |
| .gif | image/gif |

For example if you were using the command line version of Subversion you would copy the file and then execute the following command in the `wiki-assets` directory:

`svn propset svn:mime-type 'image/jpeg' example-image.jpg`

Alternatively you can check the documentation for your specific Subversion client on how to have this property automatically added.

## Commit the changes to the repository ##

Once you've copied the necessary files and updated their mime-type properties commit the files to the repository.

For example if you were using the command line version of Subversion you would copy the file and then execute the following command in the `wiki-assets` directory:

`svn ci -m "adding images for use in the Wiki"`

## Browse the Repository ##

After the files have been successfully uploaded to the repository browse the source code repository by using this URL

http://code.google.com/p/aus-e-stage/source/browse/#svn/trunk/wiki-assets

Click on the name of the image that you wish to use that is listed on the right hand side of the page.

Click the **View Raw File** link listed under the **File info** heading on the right hand side of the page

Copy the URL in the address your of your browser and use it as you would for an image that was already online using the [Links to images Wiki Syntax](http://code.google.com/p/support/wiki/WikiSyntax#Links_to_images) detailed on the [WikiSyntax](http://code.google.com/p/support/wiki/WikiSyntax) page.