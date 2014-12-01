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
	   return dndElementOf(label, value, owner, rowClass, bodyClass, 'onElementDragged(event)');
}

function dndElementOf(label, value, owner, rowClass, bodyClass, ondragstart) {
	   return "<div class='drag-n-drop-row "+ rowClass +"' id='" + value + "' name='" + value + "' owner='" + owner + "' >" + 
                  "<div class='drag-n-drop-body " + bodyClass + "' draggable='true' ondragstart='" + ondragstart  + "' >" +
                     label +
                  "</div>" +
	          "</div>";
}
/* END  drag-n-drop */