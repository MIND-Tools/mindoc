<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="$pathToRoot$/adl-documentation.css" />
<title>$name$</title>
</head>
<body>

<hr/>
<div class="nav_bar">
<ul class="nav_bar_links">
<li><a href="$pathToRoot$/overview-summary.html">Overview</a></li>
<li>Package</li>
</ul>
<span class="clear"></span>
</div>
<hr/>

<h1 class="component-name">Package $name$</h1>

$shortDocumentation$<br/>

<p><b>See:</b> <a href="#package_description">Description</a>

<table class="summary">
<tr><th>Component Summary</th></tr>
$adl:{
<tr><td><a href="$pathToRoot$/$it.summaryHtmlFileName$" title="definition $it.name$">$it.name; format="shortName"$</a></td></tr>
}; separator="\n"$
</table>

<table class="summary">
<tr><th>Interface Summary</th></tr>
$itf:{
<tr><td><a href="$pathToRoot$/$it.summaryHtmlFileName$" title="definition $it.name$">$it.name; format="shortName"$</a></td></tr>
}; separator="\n"$
</table>

<a name="package_description"><h1 class="component-name">Package $name$ Description</h1></a>

$documentation$
</body>
</html>