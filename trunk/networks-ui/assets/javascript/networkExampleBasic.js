//at this point I have only considered the people in the first degree network

var events = {
nodes:[
	{id:"66526", nodeName: "Penetrator", startDate:"2005-05-18", venue:"Bakehouse Theatre, Adelaide, SA"},//0
	{id:"75822", nodeName: "The Sea", startDate:"2005-11-17", venue:"Illawarra Performing Arts Centre, Wollongong, NSW"}, //1                                     
    {id:"75329", nodeName: "Gary's House", startDate:"2007-02-20", venue:"Black Box Studio, Bedford Park, SA"},//2
    {id:"73534", nodeName: "Terrorism", startDate:"2007-12-04", venue:"Matthew Flinders Theatre, Bedford Park, SA"},//3    
    {id:"77606", nodeName: "The Cut", startDate:"2008-01-01", venue:"Flinders University Drama Centre, Bedford Park, SA"},//4
    {id:"77519", nodeName: "Wet and Dry", startDate:"2008-07-01", venue:"Flinders University Drama Centre, Bedford Park, SA"},//5
    {id:"437373", nodeName: "King of The West End", startDate:"2008-11-28", venue:"Star Theatre, Hilton, SA"},//6
    {id:"80901", nodeName: "Osama The Hero", startDate:"2009-04-30", venue:"Bakehouse Theatre, Adelaide, SA"},//7
    {id:"81762", nodeName: "The Under-Pants", startDate:"2009-05-09", venue:"Little Theatre, Adelaide, SA"},//8
    {id:"81935", nodeName: "The Memory of Water", startDate:"2009-07-31", venue:"Dunstan Playhouse, Adelaide, SA"},//9
    {id:"82128", nodeName: "Three In The Back, Two in the Head", startDate:"2009-07-09", venue:"Bakehouse Theatre, Adelaide, SA"},//10
    {id:"82378", nodeName: "The Under Room", startDate:"2009-09-29",venue:"Bakehouse Theatre, Adelaide, SA", central: true},//11
    {id:"82950", nodeName: "In Remembrance (of) A Small Death", startDate:"2010-02-28", venue:"La Boheme, Adelaide, SA"},//12
    {id:"86311", nodeName: "The Share", startDate:"2010-05-21", venue:"Bakehouse Theatre, Adelaide, SA"},//13
    {id:"86309", nodeName: "Different Fields", startDate:"2010-05-28", venue:"Opera Studio, Netley, SA, 28 May 2010"},//14
    {id:"84285", nodeName: "Dying City", startDate:"2010-08-05", venue:"Brunswick Mechanics Institute, Brunswick , VIC"},//15
    {id:"75329", nodeName: "The NightWatchman", startDate:"2010-11-25", venue:"Theatreworks, St Kilda, VIC"},//16

    
    
],
edges:[         
	{ id:"405949", name:"Scott Marcus", source:"7", target:"13"},
	{ id:"411926", name:"Dave Hirst", source:"3", target:"4"},
	{ id:"411924", name:"Mathew Crook", source:"2", target:"3"},
    { id:"425534", name:"Naomi Steele", source:"2", target:"3"},
    { id:"425534", name:"Naomi Steele", source:"3", target:"5"},
    { id:"428982", name:"Delia Olam", source:"5", target:"12"},	
    { id:"237655", name:"Cameron Pike", source:"2", target:"4"},	
    { id:"237655", name:"Cameron Pike", source:"4", target:"7"},
    { id:"237655", name:"Cameron Pike", source:"7", target:"13"},        
    { id:"411627", name:"Zoe Ellerton-Ashley", source:"15", target:"16"},
    { id:"440143", name:"Ben Keene", source:"15", target:"16"},    
    { id:"418381", name:"Matt Scholten", source:"15", target:"16"},    
	{ id:"426580", name:"Kat Chan", source:"15", target:"16"},        
    { id:"440144", name:"Amy Bagshaw", source:"15", target:"16"},    	
	{ id:"411924", name:"Mathew Crook", source:"3", target:"13"},
	{ id:"414321", name:"Brad Williams", source:"2", target:"4"},
    { id:"414321", name:"Brad Williams", source:"4", target:"11"},
    { id:"414321", name:"Brad Williams", source:"11", target:"15"},
    { id:"414321", name:"Brad Williams", source:"15", target:"16"},
    { id:"411927", name:"Kate Roxby", source:"3", target:"5"},
    { id:"411927", name:"Kate Roxby", source:"11", target:"12"},
    { id:"411927", name:"Kate Roxby", source:"5", target:"11"},
    { id:"8072", name:"Nathaniel Davidson", source:"10", target:"11"},
    { id:"8072", name:"Nathaniel Davidson", source:"0", target:"10"},    
	{ id:"251747", name:"Ben Flett", source:"7", target:"13"},
    { id:"400988", name:"Cassandra Backler", source:"7", target:"8"},
    { id:"400988", name:"Cassandra Backler", source:"8", target:"11"},
   	{ id:"400988", name:"Cassandra Backler", source:"11", target:"13"},
   	{ id:"409567", name:"Corey McMahon", source:"7", target:"11"},
  	{ id:"409567", name:"Corey McMahon", source:"11", target:"12"},   	
  	{ id:"409567", name:"Corey McMahon", source:"4", target:"7"},
  	{ id:"409567", name:"Corey McMahon", source:"12", target:"13"},   	   	
   	{ id:"8387", name:"Susan Grey-Gardner", source:"9", target:"11"},
  	{ id:"8387", name:"Susan Grey-Gardner", source:"11", target:"14"},   	
  	{ id:"772", name:"Edward Bond", source:"1", target:"11"},   	  	
  	{ id:"437373", name:"Christian Paterson", source:"6", target:"11"}   	  	  	
           ]
};
