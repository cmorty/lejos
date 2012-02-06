<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	
		<!-- METADATOS -->
		<title>LeJOS, Java for Lego Mindstorms / RCX FAQ</title>
		<meta name="title" content="LeJOS, Java for Lego Mindstorms / RCX FAQ" />
		<meta name="DC.Title" content="LeJOS, Java for Lego Mindstorms / RCX FAQ" />

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
				<td class="n1t1r1c1"><a href="index.php"><img src="r_media/images/lejosLogo.jpg" /></a></td>
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
							<li><a class="menuLink" href="nxj.php" >leJOS NXJ</a>
							<ul class="menu_l2">
								<li><a href="nxt/nxj/api/index.html" class="menuLink" target="_blank">API</a></li>
								<li><a href="nxt/pc/api/index.html" class="menuLink" target="_blank">PC API</a></li>
								<li><a href="nxt/nxj/tutorial/index.htm" class="menuLink" target="_blank">Tutorial</a></li>								
								<li><a href="nxj-downloads.php" class="menuLink" >Downloads</a></li>
							</ul></li>
						</ul></li>
						<li class="selected">RCX Brick
						<ul class="menu_l1">
							<li><a href="rcx.php" class="menuLinkSelected">leJOS RCX</a>
							<ul class="menu_l2">
								<li><a href="rcx/api/index.html" class="menuLink" target="_blank">API</a></li>
								<li><a href="rcx/tutorial/index.html" class="menuLink" target="_blank">Tutorial</a></li>
								<li><a href="rcx-downloads.php" class="menuLink">Downloads</a></li>
								<li class="selected">FAQ</li>
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
								<h1 class="title">leJOS RCX FAQ</h1>
							</td>
							<td align="right"><a href="javascript:decreaseFontSize()"><img src="r_media/images/toolIcons/fuentemenos.gif" /></a><a href="javascript:increaseFontSize()"><img src="r_media/images/toolIcons/fuentemas.gif" /></a></td>
						</tr>
					</table>
					<div id="content">
					
      <table width="495" border="0" cellpadding="0" cellspacing="2">

        <tr> 
          <td valign="top" colspan="2"><i>Note: The regular Wiki FAQ was lost when Sourceforge upgraded their systems and many questions were lost.</i> <br />
            &nbsp;</td>
        </tr>
        <tr valign="top"> 
          <td>Q:</td>

          <td>What is leJOS?</td>
        </tr>
        <tr valign="top"> 
          <td>A:</td>
          <td>leJOS is replacement firmware for the Lego Mindstorms RCX brick 
            - a JVM that fits within the 32kb on the RCX. Yes, you can program 
            a Lego robot with Java!</td>
        </tr>
        <tr> 
          <td valign="top" colspan="2" bgcolor="#FFF021" style="background-color: #FFF021"><img src="assets/images/spacer.gif" width="2" height="2" border="0" /></td>

        </tr>
        <tr> 
          <td valign="top" colspan="2"><img src="assets/images/spacer.gif" width="2" height="8" border="0" /></td>
        </tr>
        <tr valign="top"> 
          <td>Q:</td>
          <td>How do you pronounce leJOS?</td>
        </tr>
        <tr valign="top"> 
          <td>A:</td>

          <td>In English, the word is similar to Legos, except there is a J for 
            Java, so the correct pronunciation would be Ley-J-oss.<br />
            If you are brave and want to pronounce the name in Spanish, there 
            is a word "lejos" which means far, and it is pronounced Lay-hoss.</td>
        </tr>

<tr> 
          <td valign="top" colspan="2" bgcolor="#FFF021" style="background-color: #FFF021"><img src="assets/images/spacer.gif" width="2" height="2" border="0" /></td>
        </tr>

<tr valign="top"> 
          <td>Q:</td>

          <td>How do I learn to use leJOS?</td>
        </tr>
        <tr valign="top"> 
          <td>A:</td>
          <td>There are several options. If you are good with Java you can study the <a href="http://lejos.sourceforge.net/apidocs/index.html">API</a>. You can check out the examples folder that is included with leJOS. You can also read a <a href="http://www.amazon.com/exec/obidos/ASIN/0130093645/lejos0e-20">book</a> on leJOS programming.</td>
        </tr>


        <tr> 
          <td valign="top" colspan="2" bgcolor="#FFF021" style="background-color: #FFF021"><img src="assets/images/spacer.gif" width="2" height="2" border="0" /></td>
        </tr>
        <tr> 
          <td valign="top" colspan="2"><img src="assets/images/spacer.gif" width="2" height="8" border="0" /></td>
        </tr>
        <tr valign="top"> 
          <td>Q:</td>
          <td>How do I tell how much memory my program uses?</td>

        </tr>
        <tr valign="top"> 
          <td>A:</td>
          <td>The same way as Java standard edition:<br />
            &nbsp; &nbsp; <code>long free = Runtime.getRuntime().freeMemory();</code></td>
        </tr>
        <tr> 
          <td valign="top" colspan="2" bgcolor="#FFF021" style="background-color: #FFF021"><img src="assets/images/spacer.gif" width="2" height="2" border="0" /></td>

        </tr>
        <tr> 
          <td valign="top" colspan="2"><img src="assets/images/spacer.gif" width="2" height="8" border="0" /></td>
        </tr>
        <tr valign="top"> 
          <td>Q:</td>
          <td>I just wrote some code I would like to share. Where do I post it?</td>
        </tr>
        <tr valign="top"> 
          <td>A:</td>

          <td>There are three possibilities:<br />
1. If the code is tightly bound to a robot model then the Robots section would be a great place.<br />
2. If it improves the leJOS API then you might want to join the leJOS team and upload it to CVS.<br />
3. If it is an example that demonstrates an aspect of the leJOS API then it can be uploaded to the examples section of CVS.</td>
        </tr>

        <tr> 
          <td valign="top" colspan="2" bgcolor="#FFF021" style="background-color: #FFF021"><img src="assets/images/spacer.gif" width="2" height="2" border="0" /></td>
        </tr>

        <tr> 
          <td valign="top" colspan="2"><img src="assets/images/spacer.gif" width="2" height="8" border="0" /></td>
        </tr>
      </table>
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
