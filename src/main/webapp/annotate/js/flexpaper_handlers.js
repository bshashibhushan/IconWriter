/**
 * Handles the event of annotations getting clicked. 
 *
 * @example onMarkClicked(object)
 *
 * @param Object mark that was clicked
 */
function onMarkClicked(mark){
}

/**
 * Handles the event of annotations getting clicked. 
 *
 * @example onMarkCreated(object)
 *
 * @param Object mark that was created
 */
function onMarkCreated(mark){
}

/**
 * Handles the event of annotations getting clicked. 
 *
 * @example onMarkDeleted(object)
 *
 * @param Object mark that was deleted
 */
function onMarkDeleted(mark){
}

/**
 * Handles the event of annotations getting clicked. 
 *
 * @example onMarkChanged(object)
 *
 * @param Object mark that was changed
 */
function onMarkChanged(mark){
}

/**
 * Handles the event of external links getting clicked in the document. 
 *
 * @example onExternalLinkClicked("http://www.google.com")
 *
 * @param String link
 */
function onExternalLinkClicked(link){
   // alert("link " + link + " clicked" );
   window.location.href = link;
}

/**
 * Recieves progress information about the document being loaded
 *
 * @example onProgress( 100,10000 );
 *
 * @param int loaded
 * @param int total
 */
function onProgress(loadedBytes,totalBytes){
}

/**
 * Handles the event of a document is in progress of loading
 *
 */
function onDocumentLoading(){
}

/**
 * Receives messages about the current page being changed
 *
 * @example onCurrentPageChanged( 10 );
 *
 * @param int pagenum
 */
function onCurrentPageChanged(pagenum){
}

/**
 * Receives messages about the document being loaded
 *
 * @example onDocumentLoaded( 20 );
 *
 * @param int totalPages
 */
function onDocumentLoaded(totalPages){
	
}

/**
 * Receives error messages when a document is not loading properly
 *
 * @example onDocumentLoadedError( "Network error" );
 *
 * @param String errorMessage
 */
function onDocumentLoadedError(errMessage){
}