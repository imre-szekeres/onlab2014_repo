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