// SSZ: This script is used to fix SVG figures that miss the "preserveAspectRatio" attribute
// which is not written by GraphViz during the Dot to SVG conversion. This missing attribute
// leads to a wrong display of the SVG element in Google Chrome.
// This scripts navigates in the DOM to find the svgFigure class elements, get the SVG file
// inner document, top node, and add the good attribute to the SVG node.

// Solution inspired by:
// http://stackoverflow.com/questions/8496241/how-to-access-the-content-of-the-embed-tag-in-html
// http://dahlström.net/svg/html/get-embedded-svg-document-script.html

// wait until all the resources are loaded
window.addEventListener("load", findSVGElements, false);

// will help forcing reload on history back
// source: http://stackoverflow.com/questions/2638292/after-travelling-back-in-firefox-history-javascript-wont-run
window.onunload = function(){}; 

//fetches the document for the given embedding_element
function getSubDocument(embedding_element)
{
	if (embedding_element.contentDocument) 
	{
		return embedding_element.contentDocument;
	} 
	else 
	{
		var subdoc = null;
		try {
			subdoc = embedding_element.getSVGDocument();
		} catch(e) {}
		return subdoc;
	}
}

function findSVGElements()
{
	var elms = document.querySelectorAll('.svgFigure');
	for (var i = 0; i < elms.length; i++)
	{
		var subdoc = getSubDocument(elms[i])
		if (subdoc) {
			var svg = subdoc.getElementsByTagName('svg');
			for (var j = 0; j < svg.length; j++)
			{
				svg[j].setAttribute("preserveAspectRatio", "xMinYMin meet");
			}
		}
	}
}
