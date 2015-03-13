# AusStage Colour Scheme #

The AusStage colour scheme should be used for all continuing web development. The colours are defined in the following 5 files:

**AusStage Colour Scheme**

A HTML version of the colour scheme for ease of reference, that can be used to view the different colours

[Download Link](http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colour-scheme.html)

**AusStage Colours - Foreground CSS**

A CSS version of the colour scheme which defines styles that can be applied to HTML elements, such as the colour of text

[Download Link](http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colours.css)

**AusStage Colours - Background CSS**

A CSS version of the colour scheme which defines styles that can be applied to the background of HTML elements, such as the background colour of a table cell

[Download Link](http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-background-colours.css)

**AusStage Colours - jQuery UI**

A version of the CSS used with the [jQuery UI](http://jqueryui.com/) library that uses AusStage colours. (Developed using version 1.8.6 of the library)

[Download Link](http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/jquery-ui-1.8.6.custom.css)

**AusStage Colours - KML Styles**

A version of the colour palette based on the CSS version that uses style elements as used in [KML files](http://en.wikipedia.org/wiki/Keyhole_Markup_Language), such as maps intended for use in [Google Earth](http://www.google.com/earth/index.html)

[Download Link](http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/kml-colours.xml)

## Using the Colour Scheme ##

The scheme defines the colours that are used by AusStage. The scheme arranges 193 colours into 16 sets. Three sets address multiple hues from the colour spectrum. Ten sets provide single hues for quantitative displays, and a further three sets support other interface needs. The colours are numbered consecutively from 1 to 193. These numbers are used in code to call colours from the scheme.

### Spectrum Sets - Multiple Hues ###

Three sets provide selections from across the colour spectrum. These are used where colours represent different entities. For example, different contributors can be represented colours of different hue.

**Icon Set 14** - defines 14 base colours, with light and dark pairs of green, orange, red, purple and blue, and teal, gold, silver and black. These 14 colours provide a basis for all subsequent sets.
They are derived from the colour palette used by the [Google Maps Icons](http://code.google.com/p/google-maps-icons/wiki/ColorPalette)

**Icon Cycle 24** - defines 24 colours which cycle through the colour spectrum. The 24 colours are derived from a selection of 11 colours from Icon Set 14, with intermediate colours inserted, to make up 24.

**Icon Cycle 48** - defines 48 colours which cycle through the colour spectrum. The 48 colours are dervied from Icon Cycle 24, with intermediate colours inserted, to make up 48.

Best practice is to use colours from within one set for each application, depending on the number of different colours required.

### Quantitative sets - single hue ###

Ten sets provide variations in shade within single hues. These are used where colours represent quantitative variations in entities. For example, the number of events at each venue is represented by quantitative range from lighter colour (few events) to darker colour (many events).

Five hues - blue, red, green, orange, purple - come in sets of 10 shades and 5 shades. Best practice is to use colours from withon set for each application, depending on the number of different levels required. Five shades are easier for the eye to distinguish than ten.

For mapping, colour associations with AusStage entities are proposed as follows, and suggested practice is to follow these associations in other visualisations.

  * Events - Blues
  * Contributors - Greens
  * Organisations - Reds
  * Venues - Oranges
  * Works - Purples

### Interface sets ###

Three further sets define sets of colours not addressed in the spectrum and quantitative schemes.

**Metallics 10** - provides a metallic spectrum ranging from light silver grey to a dark brown gold. It is derived from two colours in the Icon Set 14 not used in other schemes.

**Monochrome 12** - provides a monochromatic spectrum ranging from white to black, with 10 intervening shades of grey.

**Interface 10** - provides a set of colours for use in interface elements (fonts, backgrounds, navigation, links, buttons, tabs, and so on). It include white and black, three colours for shades within one hue (currently grey) and one contrasting colour for links (currently blue), and pairs of light-dark shades for info-alerts (yellow) and error warnings (red).

### Changing colours ###

The numbers and their arrangement within the scheme are fixed. The project manager may append additional sets of colours to the scheme. The project manager may also decide to change the colour referred to by a number, resulting in a system-wide change in colour wherever that number is called. Any changes will be consistent with the other colours in each set. For example, changes in a single-hue set will not introduce a second hue.

### References ###

This colour scheme builds on work undertaken for AusStage by Mary Moore. It was developed using [ColourSchemer Studio](http://www.colorschemer.com/), version 2.0.1. ColourSchemer Studio supports cultivation of colour palettes, calculation of intermediate colours, and export to html, css and other formats. The ColourSchemer source file is [available for download](http://aus-e-stage.googlecode.com/svn/trunk/common-web-assets/ausstage-colour-scheme-colourschemer-studio.cs).

Other services referred to in the development include:

  * [Google Maps Icons](http://code.google.com/p/google-maps-icons/wiki/ColorPalette)
  * [Color Brewer 2](http://colorbrewer2.org/)
  * [Colour Lovers](http://www.colourlovers.com/)
  * [Color Oracle](http://colororacle.cartography.ch/)