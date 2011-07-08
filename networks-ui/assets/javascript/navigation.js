
var max_zoom = 10;
var min_zoom = 1;

$(document).ready(function(){
//add handlers for pan buttons
	$('#panUp').click(function(){viewer.pan('top')});
	$('#panDown').click(function(){viewer.pan('bottom')});
	$('#panLeft').click(function(){viewer.pan('left')});
	$('#panRight').click(function(){viewer.pan('right')});
	
	$('#zoomIn').click(function(){	$( "#zoomslider" ).slider( "option", "value", $("#zoomslider").slider("value")+0.5 );
									viewer.zoom($( "#zoomslider" ).slider( "value" ));
									});
	$('#zoomOut').click(function(){	$( "#zoomslider" ).slider( "option", "value", $("#zoomslider").slider("value")-0.5 );
									viewer.zoom($( "#zoomslider" ).slider( "value" ));
									});	
	
	$('#recentre').click(function(){viewer.findCentre();});	
	
//set up the zoom slider
	$( "#zoomslider" ).slider({
		orientation: "vertical",
		range: "max",
		min: min_zoom,
		max: max_zoom,
		step: 0.5,
		value: 1,
		slide: function( event, ui ) {
			viewer.zoom(ui.value );
		}
	});
	checkZoomHeight();
	
	//sidebar select item functionality
	$('.selectItem').live('click', function(){
		//seperate the type from the id
		var substr = this.id.split('_');

		if(viewer.className =='ContributorViewerClass'){
			viewer.nodeIndex = substr[0];
			viewer.edgeTIndex = -1;
			viewer.edgeSIndex = -1; 
		}else{
			if (substr[1] == EDGE){
				var index = viewer.findFirstContributorIndex(substr[0]);

				viewer.currentFocus = EDGE;
			    viewer.edgeId = substr[0];
			    viewer.edgeIndex = index
				viewer.nodeIndex = -1;			
			}else {
				viewer.currentFocus = NODE;
				viewer.edgeId = -1;
				viewer.edgeIndex = -1;
  	   	   		viewer.nodeIndex = substr[0];

			}	
		}
		viewer.displayPanelInfo(substr[1]);	
		viewer.render();

	});
})

//return network to centre
EventViewerClass.prototype.findCentre = function(){
	$( "#zoomslider" ).slider( "option", "value", 0 );	
	viewer.capturePanel.transform(pv.Transform.identity.scale(1).translate(0, 0)); 			
	viewer.xAxis.domain(viewer.xAxis.invert(viewer.xAxis(viewer.startDate))
						,viewer.xAxis.invert(viewer.xAxis(viewer.endDate)));				
	viewer.yAxis.domain(viewer.yAxis.invert(viewer.yAxis(0)), 
						viewer.yAxis.invert(viewer.yAxis(viewer.json.nodes.length)));
	$( "#zoomslider" ).slider( "option", "value", 0 );	
	viewer.render();	
}

ContributorViewerClass.prototype.findCentre = function(){
	$( "#zoomslider" ).slider( "option", "value", 0 );	
	viewer.vis.transform(pv.Transform.identity.scale(1).translate(0, 0)); 	
	viewer.render();	
}


//zoom
EventViewerClass.prototype.zoom = function(sliderValue) 
{ 
        //I set the default zoom ratio 
        var zoom_ratio = sliderValue; 
        //I extract some information about the graph before the transformation 
        var x0 = viewer.capturePanel.transform().x; 
        var y0 = viewer.capturePanel.transform().y; 
        var k0 = viewer.capturePanel.transform().k; 
        //if we are fully zoomed in don't continue
        if (k0 != zoom_ratio){
        	var w = viewer.w; 
	        var h = viewer.h;
    	    //I calculate the original values I submitted (the values before and after the rendering are different) 
	        var x0_submitted = (x0/k0) 
    	    var x0_central = ((w/k0) - w)/2; 
	        var delta_x0 = x0_submitted - x0_central; 
    	    var y0_submitted = (y0/k0) 
	        var y0_central = ((h/k0) - h)/2; 
    	    var delta_y0 = y0_submitted - y0_central; 
	        //I calculate the zoom that cannot be less then 1 
    	    var k1 = zoom_ratio; 
	        //I calculate the delta between the zoom in the center and the actual zoom (the pan) 
    	    var delta_x1 = Math.round((delta_x0 * k1) / k0); 
	        var delta_y1 = Math.round((delta_y0 * k1) / k0); 
    	    //so I have the final values 
	        var x1 = ((w/k1) - w)/2 + delta_x1; 
    	    var y1 = ((h/k1) - h)/2 + delta_y1; 
	        //I make the actual transformation 
    	    viewer.capturePanel.transform(pv.Transform.identity.scale(k1).translate(x1, y1)); 
			viewer.render();
			transform();
        }
};


EventViewerClass.prototype.pan = function(type_pan) 
{
		
		//number of pixel to shift 
    	var pan_size = 20;  
        //I extract some information about the graph before the transformation 
        var x0 = viewer.capturePanel.transform().x; 
        var y0 = viewer.capturePanel.transform().y; 
        var k0 = viewer.capturePanel.transform().k; 
        var w = viewer.w; 
        var h = viewer.h; 

        //I calculate the original values I submitted (the values before and after the rendering are different) 
        var x0_submitted = (x0/k0);
        var y0_submitted = (y0/k0); 
        //I check which kind of pan I have to do 

        if (type_pan == 'top') 
                var delta_y = pan_size * k0; 
        else if (type_pan == 'bottom') 
                var delta_y = -1 * pan_size * k0; 
        else if (type_pan == 'left') 
                var delta_x = pan_size * k0; 
        else if (type_pan == 'right') 
                var delta_x = -1 * pan_size * k0; 
        if (delta_x) 
        { 
                var x = delta_x + x0_submitted; 
                var y = 0 + y0_submitted; 
        } 
        if (delta_y) 
        { 
                var x = 0 + x0_submitted; 
                var y = delta_y + y0_submitted; 
        } 
       
        //I make the actual transformation
        
        var t = pv.Transform.identity.scale(k0).translate(x, y).invert(); 
		
		var x1 = viewer.xAxis.invert(t.x + viewer.xAxis(viewer.startDate) * t.k);
		var x2 = viewer.xAxis.invert(t.x + viewer.xAxis(viewer.endDate) * t.k);
		
		var y1 = viewer.yAxis.invert(t.y + viewer.yAxis(0) * t.k);
		var y2 = viewer.yAxis.invert(t.y + viewer.yAxis(viewer.json.nodes.length) * t.k);
        
		if(type_pan =='left' || type_pan =='right'){
			if (x1 >= viewer.startDate && x2 <=viewer.endDate){
        
			    viewer.capturePanel.transform(pv.Transform.identity.scale(k0).translate(x, y)); 
    			viewer.render();
				viewer.xAxis.domain(x1,x2);				
			}
    	}
    	
    	if (type_pan == 'top' || type_pan == 'bottom'){
    		if (y1 >= 0 && y2 <=viewer.json.nodes.length){
			
			    viewer.capturePanel.transform(pv.Transform.identity.scale(k0).translate(x, y)); 
    			viewer.render();
    			viewer.yAxis.domain(y1, y2);
    		
    		}
    	}
    	viewer.render();    	
}; 



ContributorViewerClass.prototype.zoom = function(sliderValue) 
{ 
		
		
        //I set the default zoom ratio 
        var zoom_ratio = sliderValue; 
        //I extract some information about the graph before the transformation 
        var x0 = viewer.vis.transform().x; 
        var y0 = viewer.vis.transform().y; 
        var k0 = viewer.vis.transform().k; 
        var w = viewer.vis.width(); 
        var h = viewer.vis.height(); 
        //I calculate the original values I submitted (the values before and after the rendering are different) 
        var x0_submitted = (x0/k0) 
        var x0_central = ((w/k0) - w)/2; 
        var delta_x0 = x0_submitted - x0_central; 
        var y0_submitted = (y0/k0) 
        var y0_central = ((h/k0) - h)/2; 
        var delta_y0 = y0_submitted - y0_central; 
        //I calculate the zoom that cannot be less then 1 
        var k1 = zoom_ratio; 

        //I calculate the delta between the zoom in the center and the actual zoom (the pan) 
        var delta_x1 = Math.round((delta_x0 * k1) / k0); 
        var delta_y1 = Math.round((delta_y0 * k1) / k0); 
        //so I have the final values 
        var x1 = ((w/k1) - w)/2 + delta_x1; 
        var y1 = ((h/k1) - h)/2 + delta_y1; 
        //I make the actual transformation 
        viewer.vis.transform(pv.Transform.identity.scale(k1).translate(x1, y1)); 
        viewer.render();		
	
}; 


//function that pans in the graph 
ContributorViewerClass.prototype.pan = function(type_pan) 
{
	//number of pixel to shift 
    var pan_size = 20;  
        //I extract some information about the graph before the transformation 
        var x0 = viewer.vis.transform().x; 
        var y0 = viewer.vis.transform().y; 
        var k0 = viewer.vis.transform().k; 
        var w = viewer.vis.width(); 
        var h = viewer.vis.height(); 

        //I calculate the original values I submitted (the values before and after the rendering are different) 
        var x0_submitted = (x0/k0) 
        var y0_submitted = (y0/k0) 
        //I check which kind of pan I have to do 

        if (type_pan == 'top') 
                var delta_y = pan_size * k0; 
        else if (type_pan == 'bottom') 
                var delta_y = -1 * pan_size * k0; 
        else if (type_pan == 'left') 
                var delta_x = pan_size * k0; 
        else if (type_pan == 'right') 
                var delta_x = -1 * pan_size * k0; 
        if (delta_x) 
        { 
                var x = delta_x + x0_submitted; 
                var y = 0 + y0_submitted; 
        } 
        if (delta_y) 
        { 
                var x = 0 + x0_submitted; 
                var y = delta_y + y0_submitted; 
        } 
       
        //I make the actual transformation 
	    viewer.vis.transform(pv.Transform.identity.scale(k0).translate(x, y)); 
    	viewer.render();
}; 




//hide the zoom slider if window height is too small
function checkZoomHeight(){
	if($(window).height()<480){
		$('#zoomslider').hide();
	}else {
		$('#zoomslider').show();	
	}
}

function updateZoomSlider(val){
	if (!val){
		if(viewer.className=='ContributorViewerClass'){
			val = viewer.vis.transform().k; 
		}else {
			var val = viewer.capturePanel.transform().k; 
		}
	}
	$( "#zoomslider" ).slider( "option", "value", val );	
}