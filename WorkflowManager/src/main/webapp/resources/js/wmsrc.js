function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
}

/* BEGIN drag-n-drop */
function dndElementOf(label, value, owner, rowClass, bodyClass) {
     return "<div class='drag-n-drop-row " + rowClass + "' id='" + value + "' name='" + value + "' owner='" + owner + "' >" + 
                "<div class='drag-n-drop-body " + bodyClass + "' draggable='true' ondragstart='onElementDragged(event)' >" +
                    label +
                "</div>" +
	        "</div>";
}
/* END  drag-n-drop */

function wait($_conatainer) {
	$_conatainer.css('cursor', 'wait');
}

function nowait($_conatainer) {
	$_conatainer.css('cursor', 'auto');
}

function wrapAsAlertModal($_data, title, modal_label) {
	var modal = "<div class='modal-dialog' >" +
				    "<div class='modal-content' >" + 
				        "<div class='modal-header' >" +
				        	"<button type='button' class='close' data-dismiss='modal' >" +
				        		"<span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>" +
				        	"</button>" + 
				        	"<h4 class='modal-title' id='" + modal_label + "' >" + title + "</h4>" +
				        "</div>" +
				        
				        "<div id='alert-as-modal-body' class='modal-body' >" +
				        	$_data.html() + 
				        "</div>" +
				        
				        "<div class='modal-footer' >" +
				        "</div>" +
				    "</div>" +
	            "</div>";
	return modal;
}

function checkAccess($_data, $_target, modal_label) {
	var $_denial_panel = $_data.find('#access-denied-panel');
	if ($_denial_panel) {
		$_denial_panel = (modal_label ? $(wrapAsAlertModal($_denial_panel, 'Access Denied', modal_label)) : $_denial_panel);
		$_denial_panel.appendTo( $_target );
		return true;
	}
	return false;
}