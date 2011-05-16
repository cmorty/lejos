<?php
	echo '<?xml version="1.0" encoding="UTF-8" ?>';
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<!-- METADATOS -->
		<title>LeJOS, Java for Lego Mindstorms</title>
		<meta name="title" content="LeJOS, Java for Lego Mindstorms" />
		<meta name="DC.Title" content="LeJOS, Java for Lego Mindstorms" />
		<meta http-equiv="title" content="LeJOS, Java for Lego Mindstorms" />

		<!-- KEYWORDS -->
		<meta name="keywords" content="LeJOS, NXJ, Icommand, NXT , RCX, lego mindstorm,Java, TinyVM, Lego, Mindstorms, RCX, Robots" />
		<meta http-equiv="keywords"	content="LeJOS, NXJ, Icommand, NXT , RCX, lego mindstorm,Java, TinyVM, Lego, Mindstorms, RCX, Robots" />


		<!-- DESCRIPCION -->
		<meta name="description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />
		<meta http-equiv="description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />
		<meta http-equiv="DC.Description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />

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
								<li><a href="rcx-downloads.php" class="menuLinkSelected">Downloads</a></li>
								<li><a href="rcx-faq.php" class="menuLink">FAQ</a></li>
							</ul></li>		
						</ul></li>
						<li><a href="http://lejos.sourceforge.net/forum/" class="menuLink" target="_blank">Forum</a></li>
						<li><a class="menuLink" href="books.php">Books</a></li>
						<li><a class="menuLink" href="links.php">Links</a></li>
						<li><a class="menuLink" href="contact.php">Contact</a></li>
					</ul>
					<!-- END MENU -->
				<p><img src="r_media/images/sourceforgeButton.jpg" /></p>
				<p><img src="r_media/images/java.netButton.jpg" /></p>
				</td>
				<td class="n3t1r1c1">
					<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<h1 class="title">Windows Intallation of leJOS</h1>
							</td>
							<td align="right"><a href="javascript:decreaseFontSize()"><img src="r_media/images/toolIcons/fuentemenos.gif" /></a><a href="javascript:increaseFontSize()"><img src="r_media/images/toolIcons/fuentemas.gif" /></a></td>
						</tr>
					</table>
					<div id="content">

            <p>The only difference between the Unix distributions and the 
            <code>win32</code> distributions of leJOS is that the latter
            ones contain Windows executables. These executables depend on the CygWin B20.1
            DLL, which is distributed in the <code>bin</code> directory, and are meant
            to be run from a DOS console. To run leJOS from a CygWin console, you should download 
            the Unix distribution, or run <code>make</code>.</p>

            <p>
            To install leJOS:</p>
            <ul>
            <li>Unzip the file you downloaded. A top directory, <code>lejos</code>, is already provided.</li>
            <li>Set <b>RCXTTY</b> to the IR serial port, e.g. <code>set RCXTTY=COM2</code>.</li>

            <li>Both the JDK's and  leJOS' <code>bin</code> directories should be in your <b>PATH</b>.</li>
            </ul>
            <p>Under Windows 95/98, you might want to create a batch file that sets these
            variables, and specify that batch file in the <code>Program</code> properties
            of a DOS console shortcut; or set them in C:\AUTOEXEC.BAT.</p>

            <h3>Testing the Release</h3>
            <p>To run the <code>View</code> example, go to the
            <code>lejos</code> directory and type:</p>

            <pre>
            lejosfirmdl
            cd examples\view
            set CLASSPATH=.
            lejosc *.java
            lejos View
            </pre>

            <p>Press <code>Run</code> to start the program.
            This example allows you to test sensors
            and motors. Press <code>View</code> to select a
            sensor or motor. Press <code>Run</code> to activate or
            passivate a device. Press <code>Prgm</code> to
            change sensor mode or motor power. To exit this
            program, press <code>On/Off</code>.</p>

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
					target="_blank" class="esmeta">Esmeta</a> 1997-2006</td>
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
