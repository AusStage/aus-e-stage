//at this point I have only considered the people in the first degree network

var events = {
nodes:[


	{id:"74583", nodeName: "Lear", startDate:"2005-05-18", venue:"Performance Space, Wollongong, NSW"},//0
	{id:"66526", nodeName: "Penetrator", startDate:"2005-05-18", venue:"Bakehouse Theatre, Adelaide, SA"},//1
	{id:"75822", nodeName: "The Sea", startDate:"2005-11-17", venue:"Illawarra Performing Arts Centre, Wollongong, NSW"},//2
	{id:"53637", nodeName: "Julius Caesar", startDate:"2006-08-05", venue:"Little Theatre, Adelaide, SA"},//3
	{id:"75329", nodeName: "Gary’s House", startDate:"2007-02-22", venue:"Black Box Studio, Bedford Park, SA"},//4
	{id:"75329", nodeName: "The Jungle", startDate:"2007-07-19", venue:"Matthew Flinders Theatre, Bedford Park, SA"},//5
	{id:"73534", nodeName: "Terrorism", startDate:"2007-12-04", venue:"Matthew Flinders Theatre, Bedford Park, SA"},//6
	{id:"77606", nodeName: "The Cut", startDate:"2008-01-01", venue:"Flinders University Drama Centre, Bedford Park, SA"},//7
	{id:"77519", nodeName: "Wet And Dry", startDate:"2008-07-01", venue:"Flinders University Drama Centre, Bedford Park, SA"},//8
	{id:"85077", nodeName: "King Of the West End", startDate:"2008-11-28", venue:"Star Theatre, Hilton, SA"},//9
	{id:"79291", nodeName: "After The End", startDate:"2009-02-25", venue:"Bakehouse Theatre, Adelaide, SA"},//10
	{id:"80901", nodeName: "Osama The Hero", startDate:"2009-04-30", venue:"Bakehouse Theatre, Adelaide, SA"},//11
	{id:"81762", nodeName: "The Under-Pants", startDate:"2009-05-09", venue:"Little Theatre, Adelaide, SA"},//12
	{id:"82128", nodeName: "Three In the Back Two In The Head", startDate:"2009-07-09", venue:"Bakehouse Theatre, Adelaide, SA"},//13
	{id:"81935", nodeName: "The Memory of Water", startDate:"2009-07-31", venue:"Dunstan Playhouse, Adelaide, SA"},//14
	{id:"82520", nodeName: "Oleanna", startDate:"2009-10-17", venue:"Little Theatre, Adelaide, SA"},//15
	{id:"82378", nodeName: "The Under Room", startDate:"2009-10-29", venue:"Bakehouse Theatre, Adelaide, SA", central: true},//16
	{id:"82950", nodeName: "In Rememberance Of A Small Death", startDate:"2010-02-28", venue:"La Boheme, Adelaide, SA"},//17
	{id:"86311", nodeName: "The Share", startDate:"2010-05-21", venue:"Bakehouse Theatre, Adelaide, SA"},//18
	{id:"86309", nodeName: "Different Fields", startDate:"2010-05-28", venue:"Opera Studio, Netley, SA, 28 May 2010"},//19
	{id:"86297", nodeName: "Two", startDate:"2010-06-23", venue:"Directors Hotel, Adelaide, SA"},//20
	{id:"86276", nodeName: "Rust And Bone", startDate:"2010-07-30", venue:"State Theatre Company Rehearsal Room 2, Adelaide, SA"},//21
	{id:"84285", nodeName: "Dying City", startDate:"2010-08-05", venue:"Brunswick Mechanics Institute, Brunswick , VIC"},//22
	{id:"88155", nodeName: "The Nightwatchman", startDate:"2010-11-24", venue:"Theatreworks, St Kilda, Vic"}//23
	    
],
edges:[         

	{ id:"440144", name:"Amy Bagshaw", source:"22", target:"23", role:"Stage Manager sdfsdf sdfdsfd sdfsdfd sdfdsfdf"},
	{ id:"426508", name:"Katherine Chan", source:"22", target:"23", role:"Designer"},
	{ id:"418381", name:"Matt Scholten", source:"22", target:"23", role:"Director"},
	{ id:"440143", name:"Ben Keene", source:"22", target:"23", role:"Composer"},
	{ id:"411627", name:"Zoe Ellerton-Ashley", source:"22", target:"23", role:"Actor"},
	{ id:"189", name:"Daniel Keene", source:"18", target:"23", role:"Playwright"},
	{ id:"414313", name:"Lydia Nicholson", source:"7", target:"17", role:"Actor"},
	{ id:"4774", name:"Catherine Fitzgerald", source:"6", target:"14", role:"Director"},
	{ id:"432654", name:"Luke Ashby", source:"9", target:"18", role:"Sound Designer"},
	{ id:"411926", name:"David Hirst", source:"6", target:"7", role:"Actor"},
	{ id:"411927", name:"Kate Roxby", source:"5", target:"6", role:"Actor"},
	{ id:"409378", name:"Chris Asimos", source:"5", target:"6", role:"Actor"},
	{ id:"11014", name:"Anne Thompson", source:"4", target:"7", role:"Director"},
	{ id:"425534", name:"Naomi Steele", source:"4", target:"6", role:"Director"},
	{ id:"425534", name:"Naomi Steele", source:"6", target:"8", role:"Actor"},
	{ id:"411924", name:"Matthew Crook", source:"4", target:"6", role:"Actor"},
	{ id:"411924", name:"Matthew Crook", source:"6", target:"18", role:"Actor"},
	{ id:"235824", name:"Nick Pelomis", source:"1", target:"10", role:"Actor"},
	{ id:"8067", name:"Michael Allen", source:"3", target:"13", role:"Actor"},
	{ id:"400988", name:"Cassandra Backler", source:"3", target:"11", role:"Designer"},
	{ id:"1152", name:"Brant Eustice", source:"3", target:"12", role:"Actor"},
	{ id:"1152", name:"Brant Eustice", source:"12", target:"15", role:"Actor"},
	{ id:"8387", name:"Susan Grey-Gardner", source:"10", target:"14", role:"Lighting Designer"},
	{ id:"772", name:"Edward Bond", source:"0", target:"2", role:"Playwright"},
	{ id:"409567", name:"Corey McMahon", source:"6", target:"7", role:"Director"},
	{ id:"8072", name:"Nat Davidson", source:"1", target:"13", role:"Actor"},
	{ id:"411927", name:"Kate Roxby", source:"6", target:"8", role:"Actor"},
	{ id:"414321", name:"Brad Williams", source:"4", target:"7", role:"Actor"},
	{ id:"237655", name:"Cameron Pike", source:"4", target:"7", role:"Actor"},
	{ id:"405949", name:"Scott Marcus", source:"5", target:"11", role:"Actor"},
	{ id:"251747", name:"Ben Flett", source:"2", target:"11", role:"Lighting Designer"},
	{ id:"251747", name:"Ben Flett", source:"11", target:"18", role:"Lighting Designer"},
	{ id:"405949", name:"Scott Marcus", source:"11", target:"18", role:"Actor"},
	{ id:"237655", name:"Cameron Pike", source:"7", target:"11", role:"Actor"},
	{ id:"237655", name:"Cameron Pike", source:"11", target:"18", role:"Actor"},
	{ id:"400988", name:"Cassandra Backler", source:"11", target:"12", role:"Designer"},
	{ id:"400988", name:"Cassandra Backler", source:"12", target:"15", role:"Designer"},
	{ id:"409567", name:"Corey McMahon", source:"7", target:"11", role:"Director"},
	{ id:"8072", name:"Nat Davidson", source:"13", target:"16", role:"Actor"},
	{ id:"411927", name:"Kate Roxby", source:"8", target:"16", role:"Actor"},
	{ id:"414321", name:"Brad Williams", source:"7", target:"16", role:"Actor"},
	{ id:"400988", name:"Cassandra Backler", source:"15", target:"16", role:"Designer"},
	{ id:"409567", name:"Corey McMahon", source:"11", target:"16", role:"Director"},
	{ id:"8387", name:"Susan Grey-Gardner", source:"14", target:"16", role:"Lighting Designer"},
	{ id:"772", name:"Edward Bond", source:"2", target:"16", role:"Playwright"},
	{ id:"437373", name:"Christian Paterson", source:"9", target:"16", role:"Sound Designer"},
	{ id:"411927", name:"Kate Roxby", source:"16", target:"17", role:"Actor"},
	{ id:"414321", name:"Brad Williams", source:"16", target:"22", role:"Actor"},
	{ id:"400988", name:"Cassandra Backler", source:"16", target:"18", role:"Designer"},
	{ id:"409567", name:"Corey McMahon", source:"16", target:"17", role:"Director"},
	{ id:"8387", name:"Susan Grey-Gardner", source:"16", target:"19", role:"Lighting Designer"},
	{ id:"428982", name:"Delia Olam", source:"8", target:"17", role:"Director"},
	{ id:"409567", name:"Corey McMahon", source:"17", target:"18", role:"Producer"},
	{ id:"409567", name:"Corey McMahon", source:"18", target:"21", role:"Director"},
	{ id:"400988", name:"Cassandra Backler", source:"18", target:"20", role:"Designer"},
	{ id:"414321", name:"Brad Williams", source:"22", target:"23", role:"Actor"},	
	{ id:"400988", name:"Cassandra Backler", source:"20", target:"21", role:"Designer"}
	
           ]
};













