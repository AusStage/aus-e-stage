

$(document).ready(function(){
//add handlers for pan buttons
	$('#panUp').click(function(){pan('top')});
	$('#panDown').click(function(){pan('bottom')});
	$('#panLeft').click(function(){pan('left')});
	$('#panRight').click(function(){pan('right')});
	$('#zoomIn').click(function(){zoom('in');});
	$('#zoomOut').click(function(){zoom('out');});	
	$('#recentre').click(function(){findCentre();});		
})

function findCentre(){
	viewer.vis.transform(pv.Transform.identity.scale(1).translate(0, 0)); 
	viewer.render();	
}

function zoom(type_zoom) 
{ 
        //I set the default zoom ratio 
        var zoom_ratio = 0.5; 
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
        if (type_zoom == 'in') 
                var k1 = k0 + zoom_ratio; 
        else 
        { 
                if ((k0 > 1) && ((k0 - zoom_ratio) > 1)) 
                        var k1 = k0 - zoom_ratio; 
                else 
                        var k1 = 1; 
        } 
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
function pan(type_pan) 
{ 
        //number of pixel to shift 
        var pan_size = 50; 
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