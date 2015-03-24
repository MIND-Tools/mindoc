<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="frames.css" />
<script>
function updateMainFrame(path) {
  parent.frames[2].location.href=path;
}
</script>
</head>

<body>
<a href="all-def-frame.html" target="package-frame">All definitions</a>
<h2>Packages</h2>
<ul>
$packages: {<li><a href="$it.frameHtmlFileName$" title="$it.name$" target="package-frame" onClick="updateMainFrame(&#39; $it.summaryHtmlFileName$ &#39;)">$it.name$</a></li>}; separator="\n"$
</ul>
</body>
</html>