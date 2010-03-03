<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="$pathToRoot$/frames.css" />
</head>

<body>

<a href="$pathToRoot$/$summaryFileName$" target="main-frame">$name$</a>
$if(adl)$
<h2>Components</h2>
<ul>
$adl: {<li><a href="$pathToRoot$/$it.summaryHtmlFileName$" title="$it.name$" target="main-frame">$it.name; format="shortName"$</a></li>}; separator="\n"$
</ul>
$endif$
$if(itf)$
<h2>Interfaces</h2>
<ul>
$itf: {<li><i><a href="$pathToRoot$/$it.summaryHtmlFileName$" title="$it.name$" target="main-frame">$it.name; format="shortName"$</a></i></li>}; separator="\n"$
</ul>
$endif$
</body>
</html>