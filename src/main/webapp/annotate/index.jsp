<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.File"%>
<%@ page import="org.apache.commons.io.FileUtils"%>
<%@ page import="com.ikon.core.Config"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<!doctype html>
<html>
    <head> 
        <title>Infodocs Annotations</title>                
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
        <style type="text/css" media="screen"> 
			html, body	{ height:100%; }
			body { margin:0; padding:0; overflow:auto; }   
			#flashContent { display:none; }
        </style> 
		
		<link rel="stylesheet" type="text/css" href="css/flexpaper.css" />
		<script type="text/javascript" src="js/jquery.min.js"></script>
		<script type="text/javascript" src="js/jquery.extensions.min.js"></script>
		<script type="text/javascript" src="js/flexpaper.js"></script>
		<script type="text/javascript" src="js/flexpaper_handlers.js"></script>
    </head> 
    <body onload=timeout()>
        <div id="documentViewer" class="flexpaper_viewer" style="position:absolute; bottom:4%"></div>
         <% String uuid = request.getParameter("uuid");
		   String swfFile = "swf/" + uuid + ".swf";
		  %>

        <script type="text/javascript">
        
        //Flex Core App
            $('#documentViewer').FlexPaperViewer(
                   { config : {
					   
                     SWFFile : '<%=swfFile%>', //swf file

                     Scale : 0.6,
                     key : "@25b39f7859c86c82f97$bbcc2092dc86c7d54a8",
                     ZoomTransition : 'easeOut',
                     ZoomTime : 0.5,
                     ZoomInterval : 0.1,
                     FitPageOnLoad : true,
                     FitWidthOnLoad : false,
                     FullScreenAsMaxWindow : false,
                     ProgressiveLoading : false,
                     MinZoomSize : 0.2,
                     MaxZoomSize : 5,
                     SearchMatchAll : false,
                     InitViewMode : 'Portrait',
                     RenderingOrder : 'flash,html',
                     StartAtPage : '',

                     ViewModeToolsVisible : true,
                     ZoomToolsVisible : true,
                     NavToolsVisible : true,
                     CursorToolsVisible : true,
                     SearchToolsVisible : true,
                     localeChain: 'en_US'
                   }}
            );
            
            
          var marks = null;
          var jsonmarks = null;
          var comments = null;
          
		//Save Marks into txt file 
		  function saveMarks(){
				marks = $FlexPaper('documentViewer').getMarkList();
				jsonmarks = JSON.stringify(marks);
				
			  //Ajax call to save Annotation  
			   $.ajax({
					 type:   "POST",
					 url:   "AnnotateServlet",
					 data:   {'marks': jsonmarks, 'uuid': '<%=uuid%>', 'action' : 'saveAnnotation'},
				});   
				alert("Annotations have been saved"); 
				window.close();
           }
           
           function timeout(){
				setTimeout('load()',3000);
			}
			
            //Get marks from txt file		
           function load(){
				$FlexPaper('documentViewer').addMarks(${properties});
			}
			
        </script>
        
         <input type="button" value="Save" onclick="saveMarks()" style="position:absolute;bottom:0%;right:0%;width:100px">
   </body> 
</html> 
