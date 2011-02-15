<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="es" xml:lang="es">
	<head>
		<!-- METADATOS -->
		<title>LeJOS, Java for Lego Mindstorms</title>
		<meta name="title" content="LeJOS, Java for Lego Mindstorms" />
		<meta name="DC.Title" content="LeJOS, Java for Lego Mindstorms" />
		<meta http-equiv="title" content="LeJOS, Java for Lego Mindstorms" />

		<!-- KEYWORDS -->
		<meta name="keywords" content="LeJOS, NXJ, Icommand, NXT , RCX, lego mindstorm,Java, TinyVM, Lego, Mindstorms, RCX, Robots, Eclipse" />
		<meta http-equiv="keywords"	content="LeJOS, NXJ, Icommand, NXT , RCX, lego mindstorm,Java, TinyVM, Lego, Mindstorms, RCX, Robots, Eclipse" />


		<!-- DESCRIPCION -->
		<meta name="description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />
		<meta http-equiv="description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />
		<meta http-equiv="DC.Description" content="leJOS is a Java based replacement firmware for the Lego Mindstorms RCX microcontroller and NXJ is a Java based replacement firmware for the Lego Mindstorms NXT microcontroller" />

		<!-- OTROS METADATOS -->
		<meta name="VW96.objecttype" content="Document" />
		<META NAME="DC.Language" SCHEME="RFC1766" CONTENT="English" />
		<meta name="distribution" content="global" />
		<meta name="resource-type" content="document" />
		
		<!-- SEARCH ENGINE -->
		<meta name="robots" content="all"><!-- CACHE -->
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="expires" content="0" />

		<!-- WEB EDITOR -->
		<META name="author" CONTENT="Juan Antonio Breña Moral" />		

		<link rel="stylesheet" href="r_css/lejos.css" type="text/css" />

		<!-- ICON -->
		<link rel="shortcut icon" href="lejos.ico" type="image/x-icon" />
	</head>
	<body>	
		<!-- Level 1: Logo -->
		<table width="760" cellpadding="0" cellspacing="0">
			<tr>
				<td class="n1t1r1c1"><img src="r_media/images/lejosLogo.jpg" /><a name="top"></a></td>
				<td class="n1t1r1c2"><a href="http://mindstorms.lego.com/"><img src="r_media/images/legoMindstorms.jpg" /></a></td>
			</tr>
		</table>
		<!-- Level 2: MK options -->
		<table width="760" cellpadding="0" cellspacing="0">
			<tr>
				<!-- MENU -->
				<td rowspan="3" class="n2t1r1c1">
					<ul class="menu">
						<li>Home</li>
						<li>NXT Brick</li>
						<ul class="menu_l1">
							<li><a href="nxj.php"  class="menuLink">leJOS NXJ</a></li>
							<ul class="menu_l2">
								<li><a href="nxt/nxj/api/index.html" class="menuLink">API</a></li>
								<li><a href="nxt/pc/api/index.html" class="menuLink">PC API</a></li>
								<li><a href="nxt/nxj/tutorial/index.htm" class="menuLink" target="_blank">Tutorial</a></li>
								<li><a href="nxj-downloads.php" class="menuLink" >Downloads</a></li>
							</ul>
						</ul>
						<li>RCX Brick</li>
						<ul class="menu_l1">
							<li><a href="rcx.php"  class="menuLink">leJOS RCX</a></li>
							<ul class="menu_l2">
								<li><a href="rcx/api/index.html" class="menuLink">API</a></li>
								<li><a href="rcx/tutorial/index.html" class="menuLink">Tutorial</a></li>
								<li><a class="menuLink" href="rcx-downloads.php" >Downloads</a></li>
								<li><a href="rcx-faq.php"  class="menuLink">FAQ</a></li>
							</ul>		
						</ul>
						<li><a href="http://lejos.sourceforge.net/forum/" class="menuLink">Forum</a></li>
						<li><a href="books.php" class="menuLink">Books</a></li>
						<li><a href="links.php" class="menuLink">Links</a></li>
						<li><a href="contact.php" class="menuLink">Contact</a></li>
					</ul>
					<!-- END MENU -->
				<p><a href="http://sourceforge.net/projects/lejos/"><img src="r_media/images/sourceforgeButton.jpg" /></a></p>
				<p><a href="http://www.java.net/"><img src="r_media/images/java.netButton.jpg" /></a></p>
				</td>
				<td valign="top">
					<a href="nxj.php"><img src="r_media/images/home/nxtButton.jpg" /></a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a href="rcx.php" ><img src="r_media/images/home/rcxButton.jpg" /></a>
				</td>
			</tr>
			<tr><td><img src="r_media/images/home/lejosNews.jpg" /></td></tr>
			<!-- CONTENTS -->
			<tr>
				<td class="n3t1r1c1">
					<table cellpadding="0" cellspacing="0">


<!-- NEWS#1 -->

<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>September 2, 2009 4:16 PM</b><br />
<b>Just in time for the new semester we have leJOS NXJ 0.85</b> ready for <a href="http://sourceforge.net/projects/lejos/files/">download</a>. This is an intermediary release (as indicated by the .05) that previews some upcoming technology, however 0.9 will represent the final push for major changes before we settle down to a stable 1.0 release. Major bug fixes and improvements include:
<ul>
<li>better support and documentation for <b>MAC OS X</b>, including the Fantom USB driver</li>
<li>a <b>Netbeans</b> plugin</li>
<li>improved JVM speed and many more amazing improvements by Andy</li>
<li>support for the new LEGO color sensor in the NXT 2.0 set</li>
<li>now supports the <i>instanceof</i> keyword</li>
<li>detection of rechargeable batteries and improved battery indicator</li>
<li>nanosecond timers and improved timer support with the <a href="http://lejos.sourceforge.net/nxt/nxj/api/index.html">Delay</a> class.</li>
<li>% operation on floats and doubles</li>
<li>Class, including the isAssignableFrom(Class cls) method</li>
<li>display of LCD screen in ConsoleViewer</li>
<li>major speed and accuracy improvements to the Math class from Sven</li>
<li>platform independent <a href="http://lejos.sourceforge.net/nxt/nxj/api/index.html">lejos.robotics</a> packages</li>
<li>new navigation proposal (work in progress) that is platform independent, supports more vehicles, has better localization support, and new concepts of pose controllers and path finders</li>
<li>preliminary support for probabilistic robotics, including general purpose KalmanFilter class using matrix algebra</li>
<li>reworking of the Monte Carlo Localization classes</li>
<li>limited java.awt and java.awt.geom classes</li>
</ul>
See the <a href="http://sourceforge.net/projects/lejos/files/lejos-NXJ-win32/0.8.5beta/RELEASENOTES/download">release notes</a> for a full list of changes. As usual, feel free to visit our forums and discuss anything and everything.
<br>
							</td>
						</tr>


<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>May 23, 2009 09:40 AM</b><br />
<b>Version 0.8 of the leJOS NXJ Eclipse Plugin</b> is also available now. It supports the new version 0.8 of leJOS NXJ on Windows (XP or Vista), MAC OSX and Linux and fixes reported bugs of the previous releases.
<br>Use Eclipse's Update manager to install it from the remote update site as follows:
<ul>
<li><em>Name</em>: leJOS NXJ (or whatever you like)</li>
<li><em>URL</em>: <a href="http://lejos.sourceforge.net/tools/eclipse/plugin/nxj/" class="simpleLink">http://lejos.sourceforge.net/tools/eclipse/plugin/nxj/</a></li>
</li>
</ul>
For configuration and usage, consult the <em>leJOS NXJ</em> topic in Eclipse's <em>Help->Help contents</em> view after installation.
<br>
							</td>
						</tr>

<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>May 22, 2009 11:57 AM</b><br />
<b>leJOS NXJ 0.8</b> has been <a href="http://lejos.sourceforge.net/p_technologies/nxt/nxj/downloads.php">released</a> in anticipation of the upcoming Java One. There are plenty of new features, bug fixes and improvements, including:
<ul>
<li>iCommand is dead! Long live iCommand. PC control is now included in pccomm.jar and the lejos.nxt.remote package (see pcsamples).</li>
<li>RS485 Support in <a href="http://lejos.sourceforge.net/nxt/nxj/api/index.html">lejos.nxt.comm</a></li>
<li>Faster bootup times</li>
<li>Fixed upload reliability problem with some NXT bricks (if it didn't work before it probably works now)</li>
<li>Support for RFID, RCX Rotation Sensor, and EOPD sensors.</li>
<li>Much larger support for Java 1.6 classes (including ArrayList) thanks to Sven Koehler</li>
<li>Support for Generics, Enum classes and foreach loops!</li>
<li>A fully working <a href="http://lejos.sourceforge.net/nxt/nxj/api/index.html">lejos.subsumption</a> package</li>

<li>NXJControl utility for quick control of motors and sensors</li>
<li>Wider support in the <a href="http://lejos.sourceforge.net/nxt/nxj/api/index.html">lejos.navigation</a> packages</li>
<li><a href="http://lejos.sourceforge.net/nxt/nxj/api/index.html">javax.microedition.location</a> for Bluetooth GPS</li>
<li>Lots of sample code in the 'samples' directory</li>
</ul>
<br>
							</td>
						</tr>


<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>December 11, 2008 18:09 PM</b><br />
<b>Version 0.7 of the leJOS NXJ Eclipse Plugin</b> is available now. It supports the new version 0.7 of leJOS NXJ on Windows (XP or Vista), MAC OSX and Linux.
<br>Use Eclipse's Update manager to install it from the remote update site as follows:
<ul>
<li><em>Name</em>: leJOS NXJ (or whatever you like)</li>
<li><em>URL</em>: <a href="http://lejos.sourceforge.net/tools/eclipse/plugin/nxj/" class="simpleLink">http://lejos.sourceforge.net/tools/eclipse/plugin/nxj/</a></li>
</li>
</ul>
For configuration and usage, consult the <em>leJOS NXJ</em> topic in Eclipse's <em>Help->Help contents</em> view after installation.
<br>
							</td>
						</tr>

<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>November 15, 2008 11:36 AM</b><br />
The new <b>leJOS NXJ 0.7</b> is now available for <a href="http://lejos.sourceforge.net/p_technologies/nxt/nxj/downloads.php" class="simpleLink">download</a>. This version includes a Windows installer to make installation a breeze for new users (the compressed-file distribution is also still available). New features include better USB support, no more using a paper clip to upload firmware, faster garbage collection, three new GUI tools, a brand new <a href="http://lejos.sourceforge.net/nxt/nxj/tutorial/index.htm">Tutorial</a>, support for many new sensors, bug fixes and much more <a href="https://sourceforge.net/project/shownotes.php?group_id=9339&release_id=639759"> (full change log)</a>. We also have preliminary packages for the incredible Monte Carlo Localization technique, plus mobile phone remote control. (These packages are still undergoing changes and are not yet supported. For the brave only!) As usual, if you find any bugs please report them to our <a href="http://lejos.sourceforge.net/forum/">forums</a>.<br>
<br>
							</td>
						</tr>



<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>May 06, 2008 4:58 PM</b><br />
<b>NXJ Version 0.6</b> is available for <a href="http://lejos.sourceforge.net/p_technologies/nxt/nxj/downloads.php" class="simpleLink">download</a>. Along with numerous bug fixes, this version includes: Full <b>Mac OSX support</b>, output using System.out and System.err, switch statements, Bluetooth GPS, Bluetooth keyboard (SPP not HID), preliminary javax.bluetooth API, auto-run a program and many more (see <a href="http://sourceforge.net/project/shownotes.php?release_id=597314&group_id=9339" class="simpleLink">notes</a>).  Please report any bugs to the <a href="http://lejos.sourceforge.net/forum" class="simpleLink">forums</a>.
<br>
<br>
							</td>
						</tr>

<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>March 18, 2008 09:36 PM</b><br />
<b>Eclipse Plugin for leJOS NXJ 0.5</b> is available now. Integrated into the popular <a href="http://www.eclipse.org" class="simpleLink">Eclipse</a> platform, it allows for uploading the firmware and developing, compiling and uploading leJOS NXJ programs to the brick.
Based on the latest leJOS NXJ release, it supports the most recent Eclipse version 3.3 (Europa), thus runs on Windows and Linux.
<br>
Use Eclipse's Update manager to install it from the remote update site as follows:
<ul>
<li><em>Name</em>: leJOS NXJ (or whatever you like)</li>
<li><em>URL</em>: <a href="http://lejos.sourceforge.net/tools/eclipse/plugin/nxj/" class="simpleLink">http://lejos.sourceforge.net/tools/eclipse/plugin/nxj/</a></li>
</li>
</ul>
For configuration and usage, consult the <em>leJOS NXJ</em> topic in Eclipse's <em>Help->Help contents</em> view after installation.
<br>
<br>
							</td>
						</tr>

<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>January 19, 2008 11:14 AM</b><br />
<b>Version 0.7 of iCommand</b> is now ready for <a href="http://lejos.sourceforge.net/p_technologies/nxt/icommand/downloads.php" class="simpleLink">download</a>. Linux users who were having problems installing the previous version will have better luck with this since we forgot to include the Bluez package last time. Navigation classes are more refined in this version too. If you find bugs, please report them in the <a href="http://lejos.sourceforge.net/forum" class="simpleLink">forums</a>.
<br>
<br>
							</td>
						</tr>


<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>January 8, 2008 5:34 PM</b><br />
<b>Version 0.5 of leJOS NXJ</b> is now ready for <a href="http://lejos.sourceforge.net/p_technologies/nxt/nxj/downloads.php" class="simpleLink">download</a>. This version has quite a few changes including: A garbage collector (thanks to Janusz Gorecki), I2C writes, Port 4 now works with I2C, multiple NXT brick connections through Bluetooth (thanks to Andy Shaw), returns to menu after program ends, Mindsensors NXTCam, HiTechnic Gyro sensor, support for 64-bit Linux and lots of other general improvements. Feel free to post your comments and bug reports in the <a href="http://lejos.sourceforge.net/forum" class="simpleLink">forums</a>.
<br>
<br>
							</td>
						</tr>

<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>September 3, 2007 3:46 PM</b><br />
New versions of <a href="http://lejos.sourceforge.net/p_technologies/nxt/nxj/downloads.php" class="simpleLink">leJOS NXJ</a> and <a href="http://lejos.sourceforge.net/p_technologies/nxt/icommand/downloads.php" class="simpleLink">iCommand</a> are now available for download! The new beta version of <b>leJOS NXJ 0.4</b> features NXT to NXT Bluetooth comms, a comms proxy to talk via sockets to the outside world, greatly improved MIDP LCD UI and graphics, primitive playing of wav sound samples, a multi-level menu, and lots of other enhancements. We haven't tested everything thoroughly, so if you find any bugs, please let us know in the bugwatch threads for <a href="http://lejos.sourceforge.net/forum/viewtopic.php?p=1132#1132" class="simpleLink">leJOS NXJ</a> and <a href="http://lejos.sourceforge.net/forum/viewtopic.php?p=1133#1133" class="simpleLink">iCommand</a>.<br>
<br>
							</td>
						</tr>

						<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>July 15, 2007 4:55 PM</b><br />
<b>leJOS NXJ 0.3 is out!</b> After a long and painful delay (especially for the developers) the new and improved version of leJOS NXJ is finally available in the <a href="http://lejos.sourceforge.net/p_technologies/nxt/nxj/downloads.php" class="simpleLink">downloads.</a> This new version has many new features including storing programs and data to flash memory, a menu system, a file system, and an explorer that runs on your computer. Let us know what you think in the forums!<br>
<br>
							</td>
						</tr>


						<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>May 31, 2007 9:43 PM</b><br />
<b>There's been a holdup!</b> For those who have been waiting for the next release of leJOS NXJ, we'd like to apologize for the unexpected delay. Owners of <a href="http://www.variantpress.com/books/maximum-lego-nxt" class="simpleLink">Maximum LEGO NXT: Building Robots with Java Brains</a> should ignore the install instructions in the book until the next version is released (for now you can use the slightly more primitive leJOS NXJ 0.2.0). More information on this delay can be found in the forums <a href="http://lejos.sourceforge.net/forum/viewtopic.php?t=167" class="simpleLink">here</a> and <a href="http://lejos.sourceforge.net/forum/viewtopic.php?t=184" class="simpleLink">here</a>. Thanks for your patience.<br>
<br>
							</td>
						</tr>

						<tr>
							<td class="n3t2r"><a href="http://www.variantpress.com/books/maximum-lego-nxt"><img src="r_media/images/newsIcons/maximum_nxt_thumb.jpg" /></a></td>
							<td class="n3t2r">
							<b>April 18, 2007 6:10 PM</b><br />
Announcing <a href="http://www.variantpress.com/books/maximum-lego-nxt" class="simpleLink">Maximum LEGO NXT: Building Robots with Java Brains</a>, a new book for LEGO NXT programmers. This is a follow-up to <b>Core LEGO MINDSTORMS Programming</b>. The book includes 14 complete robot plans and over two dozen projects, with a foreword by the Director of LEGO MINDSTORMS, Søren Lund. Maximum LEGO NXT will be available from the publisher April 25, 2007 or from book stores and online retailers about a week later. For more information visit the <a href="http://www.variantpress.com/books/maximum-lego-nxt" class="simpleLink">website</a>.<br>
<br>
							</td>
						</tr>


						<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>March 30, 2007 8:03 PM</b><br />
leJOS NXJ Alpha 0.2 is now available. This release  contains preliminary Bluetooth support, I2C support (Ultrasonic sensor), and sound support.  There is no menu system yet so you still only get one run per upload.
<a href="p_technologies/nxt/nxj/downloads.php" class="simpleLink">Download Now</a>.

							</td>
						</tr>


						<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>January 10, 2007 12:08 PM</b><br />
				 leJOS NXJ is ready for download. 
				 leJOS NXJ is a full firmware replacement and works for Windows and Linux. 
				 This is a technology preview of things to come. 
				 At the moment there is no Bluetooth, I2C (Ultrasonic sensor), or sound support and we do not have a basic menu system. 
				 You can write Java programs and upload them to the NXT brick via USB. 
				 (see readme for more information) <a href="p_technologies/nxt/nxj/downloads.php" class="simpleLink">Download Now</a>.

							</td>
						</tr>
						<!-- NEWS#2 -->
						<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>October 30, 2006 11:46 PM</b><br />
				 iCommand 0.5 is ready for download. 
				 New features include webcam robotics, compass support, synchronized motors, convenient sensor wrappers, and the ultrasonic sensor can now return multiple pings like radar. 
				 Browse the <a href="p_technologies/nxt/icommand/api/index.html" class="simpleLink">iCommand API</a> or 
				 <a href="p_technologies/nxt/icommand/downloads.php" class="simpleLink">Download</a> Now.

							</td>
						</tr>
						<!-- NEWS#3 -->
						<tr>
							<td class="n3t2r"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td class="n3t2r">
							<b>September 22, 2006 11:08 PM</b><br />
				 Version 0.4 of iCommand is now
				available. The new version copies leJOS commands more precisely (as
				seen in the 
				<a href="p_technologies/nxt/icommand/api/index.html" class="simpleLink">iCommand API</a>) and adds functions for the Ultrasonic
				sensor. It's still not perfect but it is starting to show some
				potential. <a href="p_technologies/nxt/icommand/downloads.php" class="simpleLink">Download</a> Now.

							</td>
						</tr>
						<!-- NEWS#4 -->
						<tr>
							<td valign="top"><img src="r_media/images/newsIcons/nxj.jpg" /></td>
							<td valign="top">
							<b>August 28, 2006 11:13 PM</b><br />
				 Introducing iCommand 0.3, a Java API for
				controlling the Lego NXT brick. iCommand allows you to program your
				robot and control it using your Bluetooth connection. The <a href="p_technologies/nxt/icommand/api/index.html" class="simpleLink">iCommand API</a>
				is modelled on the leJOS API with some differences. Note: This is not
				a firmware replacement. <a href="p_technologies/nxt/icommand/downloads.php" class="simpleLink">Download</a> now.
							</td>
						</tr>						
						<!-- NEWS#5 -->
						<tr>
							<td valign="top"><img src="r_media/images/newsIcons/rcxjava.jpg" /></td>
							<td valign="top">
							<b>August 20, 2006 8:50 AM</b><br />
				 All's well that ends well: the first
				release candidate for leJOS 3.0 is out now! leJOS 3.0 is meant to be
				the final release for the RCX, incorporating all the enhancements made
				to leJOS since the last official release (2.1.0) in 2003 (before we
				hit the road for Java support for the RCX's successor, the NXT). Be
				sure to check it out in the <a href="p_technologies/rcx/downloads.php" class="simpleLink"/>downloads section</a> and provide feedback to the
				authors.
							</td>
						</tr>
						<!-- NEWS#6 -->
						<tr>
							<td valign="top"><img src="r_media/images/newsIcons/nxt_robot.jpg" /></td>
							<td valign="top">
							<b>January 5, 2006 9:53 AM</b><br />
At the January CES, Lego announced the next generation of Lego Mindstorms, called <a href="http://mindstorms.lego.com/" class="simpleLink"/>Lego NXT</a>. 
The kit is scheduled for launch in September 2006 but they will be releasing 100 developer kits in February. We hope members of the leJOS team will receive these kits, and if we do we will be porting leJOS for the NXT so people can continue to develop their next generation robots in Java!
							</td>
						</tr>					
					</table>
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
