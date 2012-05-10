<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	
		<!-- METADATOS -->
		<title>LeJOS, Java for Lego Mindstorms / RCX</title>
		<meta name="title" content="LeJOS, Java for Lego Mindstorms / RCX" />
		<meta name="DC.Title" content="LeJOS, Java for Lego Mindstorms / RCX" />

		<!-- KEYWORDS -->
		<meta name="keywords" content="LeJOS, NXJ, Icommand, NXT , RCX, lego mindstorm,Java, TinyVM, Lego, Mindstorms, RCX, Robots" />


		<!-- DESCRIPCION -->
		<meta name="description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />
		<meta name="DC.Description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />

		<!-- OTROS METADATOS -->
		<meta name="VW96.objecttype" content="Document" />
		<meta name="DC.Language" scheme="RFC1766" content="English" />
		<meta name="distribution" content="global" />
		<meta name="resource-type" content="document" />
		
		<!-- SEARCH ENGINE -->
		<meta name="robots" content="all" /><!-- CACHE -->
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="expires" content="0" />

		<!-- WEB EDITOR -->
		<meta name="author" content="Juan Antonio Breña Moral" />

		<link rel="stylesheet" href="r_css/lejos.css" type="text/css" />

		<!-- ICON -->
		<link rel="shortcut icon" href="lejos.ico" type="image/x-icon" />

		<script type="text/javascript" src="r_javascript/esmeta/font/fontSizeManager.js"></script>
	</head>
	<body>	
		<!-- Level 1: Logo -->
		<table width="760" cellpadding="0" cellspacing="0">
			<tr>
				<td class="n1t1r1c1"><a href="index.php" name="top"><img src="r_media/images/lejosLogo.jpg" /></a></td>
				<td class="n1t1r1c2"><a href="http://mindstorms.lego.com/" target="_blank"><img src="r_media/images/legoMindstorms.jpg" /></a></td>
			</tr>
		</table>
		<!-- Level 2: MK options -->
		<table width="760" cellpadding="0" cellspacing="0">
			<tr>
				<td class="n2t1r1c1">
					<!-- MENU -->
					<ul class="menu">
						<li><a href="index.php" class="menuLink">Home</a></li>
						<li>NXT Brick
						<ul class="menu_l1">
							<li><a class="menuLink" href="../nxt/nxj/nxj.php" >leJOS NXJ</a>
							<ul class="menu_l2">
								<li><a href="nxt/nxj/api/index.html" class="menuLink" target="_blank">API</a></li>
								<li><a href="nxt/pc/api/index.html" class="menuLink">PC API</a></li>
								<li><a href="nxt/nxj/tutorial/index.htm" class="menuLink" target="_blank">Tutorial</a></li>								
								<li><a href="nxj-downloads.php" class="menuLink">Downloads</a></li>
							</ul></li>
						</ul></li>
						<li class="selected">RCX Brick
						<ul class="menu_l1">
							<li class="selected">leJOS RCX
							<ul class="menu_l2">
								<li><a href="rcx/api/index.html" class="menuLink" target="_blank">API</a></li>
								<li><a href="rcx/tutorial/index.html" class="menuLink" target="_blank">Tutorial</a></li>
								<li><a href="rcx-downloads.php" class="menuLink" >Downloads</a></li>
								<li><a href="rcx-faq.php" class="menuLink" >FAQ</a></li>
							</ul></li>
						</ul></li>
						<li><a href="http://lejos.sourceforge.net/forum/" class="menuLink" target="_blank">Forum</a></li>
						<li><a class="menuLink" href="books.php">Books</a></li>
						<li><a class="menuLink" href="links.php">Links</a></li>
						<li><a class="menuLink" href="contact.php">Contact</a></li>
					</ul>
					<!-- END MENU -->
				<p><a href="http://sourceforge.net/projects/lejos/"><img src="r_media/images/sourceforgeButton.jpg" /></a></p>
				<p><img src="r_media/images/java.netButton.jpg" /></p>
				</td>
				<td class="n3t1r1c1">
					<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<h1 class="title">LeJOS technology</h1>
							</td>
							<td align="right"><a href="javascript:decreaseFontSize()"><img src="r_media/images/toolIcons/fuentemenos.gif" /></a><a href="javascript:increaseFontSize()"><img src="r_media/images/toolIcons/fuentemas.gif" /></a></td>
						</tr>
					</table>
					<div id="content">
					<p>
					<b><a name="what_is_lejos">What is leJOS?</a></b><br />

leJOS (pronounced like the Spanish word "lejos" for "far") is a 
tiny Java Virtual Machine for the Lego Mindstorms RCX.</p>

<p>leJOS was originally forked out of the <a
 href="http://tinyvm.sourceforge.net" target="_blank">TinyVM project</a>. <br />
It contains a VM for Java bytecodes and additional software to load and
run Java programs.</p>

<p>These are some of the features offered:</p>
<ul>
  <li>Object oriented language (Java)</li>
  <li>Preemptive threads (tasks)</li>
  <li>Arrays, including multi-dimensional ones</li>
  <li>Recursion</li>
  <li>Synchronization</li>
  <li>Exceptions</li>
  <li>Java types including float, long, String, but with some restrictions</li>
  <li>Math class</li>
  <li>Well-documented Robotics API</li>
</ul>


            <p>
            	<b>Documentation and Tutorials</b></p>


            <ul>
              <li><a href="http://lejos.sourceforge.net/tutorial/index.html" target="_blank">The leJOS Tutorial</a> by Matthias Paul Scholz et.al.</li>
              <li><a href="http://www.informatik.fh-muenchen.de/~schieder/usinglejos/" target="_blank">An Introduction to leJOS</a> by Reinhard Schiedermeier, Munich University of Applied Sciences</li>

              <li><a href="http://dudley.wellesley.edu/~anderson/robots/lejos/" target="_blank">leJOS Documentation</a> by Scott Anderson, Wellesley College</li>
            </ul>

</div>
<!-- GO TOP -->
<div align="right"><a href="#top" class="top">Top</a></div><br />
				</td>
			</tr>
		</table>
		<!-- Level 3: Footer -->
		<table width="760" cellpadding="0" cellspacing="0">
			<tr>
				<td class="n4t1r1c1">
					<p>
			<b>Disclaimer:</b> Java is a trademark of Sun Microsystems. 
			Lego Mindstorms is a trademark of the LEGO Group. 
			There is no association between Lego and leJOS or Sun and leJOS, or even between Lego and Sun as far as we know.
					</p>
				</td>
			</tr>
			<tr>
				<td class="n4t1r2c1">Powered by <a href="http://www.esmeta.es/"
					target="_blank" class="esmeta">Esmeta</a> 1997-2009</td>
			</tr>
		</table>
<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-343143-6";
urchinTracker();
</script>
	</body>
</html>
