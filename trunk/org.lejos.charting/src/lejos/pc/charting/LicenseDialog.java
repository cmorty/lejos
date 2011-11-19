package lejos.pc.charting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class LicenseDialog extends JDialog {
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTextArea licenseTextArea = new JTextArea();
    private JTextArea jLabel1 = new JTextArea();
    private JButton OKButton = new JButton();

    public LicenseDialog(Frame parent) {
        this(parent, "About NXT Charting Logger", true);
    }

    public LicenseDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        try {
            
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setSize(new Dimension(539, 325));
        this.setLayout( null );
        this.setModal(true);
        this.setResizable(false);
        jScrollPane1.setBounds(new Rectangle(5, 65, 525, 190));
        jScrollPane1.setAutoscrolls(true);
        licenseTextArea.setText(getLicenseText());
        licenseTextArea.setOpaque(true);
        licenseTextArea.setBackground(new Color(231, 231, 231));
        jLabel1.setText("NXT Charting Logger for LeJOS - http://lejos.sourceforge.net\n ©2011 Kirk Thompson <lejos@mosen.net>, Sven K\u00F6hler " +
            "\n\nUses JFreeChart v1.0.13 from David " +
            "Gilbert, Object Refinery Limited");
        jLabel1.setBounds(new Rectangle(5, 5, 525, 60));
        jLabel1.setOpaque(false);
        jLabel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jLabel1.setEditable(false);
        jLabel1.setFont(new Font("Tahoma", 1, 11));
        OKButton.setText("OK");
        OKButton.setBounds(new Rectangle(225, 260, 80, 30));
        OKButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        OKButton_actionPerformed(e);
                    }
                });
        jScrollPane1.getViewport().add(licenseTextArea, null);
        licenseTextArea.setCaretPosition(0);
        licenseTextArea.setEditable(false);
        this.getContentPane().add(OKButton, null);
        this.getContentPane().add(jLabel1, null);
        this.add(jScrollPane1, null);
       
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        setLocation((screenSize.width - frameSize.width) / 2, 
                          (screenSize.height - frameSize.height) / 2);
    }
    
    private String getLicenseText() {
        return " ===========================================================\n" + 
        " JFreeChart : a free chart library for the Java(tm) platform\n" + 
        " ===========================================================\n" + 
        "\n" + 
        " (C) Copyright 2000-2009, by Object Refinery Limited and Contributors.\n" + 
        "\n" + 
        " Project Info:  http://www.jfree.org/jfreechart/index.html\n" + 
        "\n" + 
        " This library is free software; you can redistribute it and/or modify it\n" + 
        " under the terms of the GNU Lesser General Public License as published by\n" + 
        " the Free Software Foundation; either version 2.1 of the License, or\n" + 
        " (at your option) any later version.\n" + 
        "\n" + 
        " This library is distributed in the hope that it will be useful, but\n" + 
        " WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY\n" + 
        " or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public\n" + 
        " License for more details.\n" + 
        "\n" + 
        " You should have received a copy of the GNU Lesser General Public\n" + 
        " License along with this library; if not, write to the Free Software\n" + 
        " Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,\n" + 
        " USA.\n" + 
        "\n" + 
        " [Java is a trademark or registered trademark of Sun Microsystems, Inc.\n" + 
        " in the United States and other countries.]\n" + 
        "\n" + 
        " ---------------\n" + 
        " JFreeChart.java\n" + 
        " ---------------\n" + 
        " (C) Copyright 2000-2009, by Object Refinery Limited and Contributors.\n" + 
        "\n" + 
        " Original Author:  David Gilbert (for Object Refinery Limited);\n" + 
        " Contributor(s):   Andrzej Porebski;\n" + 
        "                   David Li;\n" + 
        "                   Wolfgang Irler;\n" + 
        "                   Christian W. Zuckschwerdt;\n" + 
        "                   Klaus Rheinwald;\n" + 
        "                   Nicolas Brodu;\n" + 
        "                   Peter Kolb (patch 2603321);\n" + 
        "\n" + 
        " NOTE: The above list of contributors lists only the people that have\n" + 
        " contributed to this source file (JFreeChart.java) - for a list of ALL\n" + 
        " contributors to the project, please see the README.txt file in the distibution\n\n" +
        " ===========================================================\n" + 
        "  GNU LESSER GENERAL PUBLIC LICENSE\n" + 
        "  Version 2.1, February 1999\n" + 
        " ===========================================================\n" + 
        "\n" + 
        " Copyright (C) 1991, 1999 Free Software Foundation, Inc.\n" + 
        "     59 Temple Place, Suite 330, Boston, MA  02111-1307  USA\n" + 
        " Everyone is permitted to copy and distribute verbatim copies\n" + 
        " of this license document, but changing it is not allowed.\n" + 
        "\n" + 
        "[This is the first released version of the Lesser GPL.  It also counts\n" + 
        " as the successor of the GNU Library Public License, version 2, hence\n" + 
        " the version number 2.1.]\n" + 
        "\n" + 
        "			    Preamble\n" + 
        "\n" + 
        "  The licenses for most software are designed to take away your\n" + 
        "freedom to share and change it.  By contrast, the GNU General Public\n" + 
        "Licenses are intended to guarantee your freedom to share and change\n" + 
        "free software--to make sure the software is free for all its users.\n" + 
        "\n" + 
        "  This license, the Lesser General Public License, applies to some\n" + 
        "specially designated software packages--typically libraries--of the\n" + 
        "Free Software Foundation and other authors who decide to use it.  You\n" + 
        "can use it too, but we suggest you first think carefully about whether\n" + 
        "this license or the ordinary General Public License is the better\n" + 
        "strategy to use in any particular case, based on the explanations below.\n" + 
        "\n" + 
        "  When we speak of free software, we are referring to freedom of use,\n" + 
        "not price.  Our General Public Licenses are designed to make sure that\n" + 
        "you have the freedom to distribute copies of free software (and charge\n" + 
        "for this service if you wish); that you receive source code or can get\n" + 
        "it if you want it; that you can change the software and use pieces of\n" + 
        "it in new free programs; and that you are informed that you can do\n" + 
        "these things.\n" + 
        "\n" + 
        "  To protect your rights, we need to make restrictions that forbid\n" + 
        "distributors to deny you these rights or to ask you to surrender these\n" + 
        "rights.  These restrictions translate to certain responsibilities for\n" + 
        "you if you distribute copies of the library or if you modify it.\n" + 
        "\n" + 
        "  For example, if you distribute copies of the library, whether gratis\n" + 
        "or for a fee, you must give the recipients all the rights that we gave\n" + 
        "you.  You must make sure that they, too, receive or can get the source\n" + 
        "code.  If you link other code with the library, you must provide\n" + 
        "complete object files to the recipients, so that they can relink them\n" + 
        "with the library after making changes to the library and recompiling\n" + 
        "it.  And you must show them these terms so they know their rights.\n" + 
        "\n" + 
        "  We protect your rights with a two-step method: (1) we copyright the\n" + 
        "library, and (2) we offer you this license, which gives you legal\n" + 
        "permission to copy, distribute and/or modify the library.\n" + 
        "\n" + 
        "  To protect each distributor, we want to make it very clear that\n" + 
        "there is no warranty for the free library.  Also, if the library is\n" + 
        "modified by someone else and passed on, the recipients should know\n" + 
        "that what they have is not the original version, so that the original\n" + 
        "author's reputation will not be affected by problems that might be\n" + 
        "introduced by others.\n" + 
        "\n" + 
        "  Finally, software patents pose a constant threat to the existence of\n" + 
        "any free program.  We wish to make sure that a company cannot\n" + 
        "effectively restrict the users of a free program by obtaining a\n" + 
        "restrictive license from a patent holder.  Therefore, we insist that\n" + 
        "any patent license obtained for a version of the library must be\n" + 
        "consistent with the full freedom of use specified in this license.\n" + 
        "\n" + 
        "  Most GNU software, including some libraries, is covered by the\n" + 
        "ordinary GNU General Public License.  This license, the GNU Lesser\n" + 
        "General Public License, applies to certain designated libraries, and\n" + 
        "is quite different from the ordinary General Public License.  We use\n" + 
        "this license for certain libraries in order to permit linking those\n" + 
        "libraries into non-free programs.\n" + 
        "\n" + 
        "  When a program is linked with a library, whether statically or using\n" + 
        "a shared library, the combination of the two is legally speaking a\n" + 
        "combined work, a derivative of the original library.  The ordinary\n" + 
        "General Public License therefore permits such linking only if the\n" + 
        "entire combination fits its criteria of freedom.  The Lesser General\n" + 
        "Public License permits more lax criteria for linking other code with\n" + 
        "the library.\n" + 
        "\n" + 
        "  We call this license the \"Lesser\" General Public License because it\n" + 
        "does Less to protect the user's freedom than the ordinary General\n" + 
        "Public License.  It also provides other free software developers Less\n" + 
        "of an advantage over competing non-free programs.  These disadvantages\n" + 
        "are the reason we use the ordinary General Public License for many\n" + 
        "libraries.  However, the Lesser license provides advantages in certain\n" + 
        "special circumstances.\n" + 
        "\n" + 
        "  For example, on rare occasions, there may be a special need to\n" + 
        "encourage the widest possible use of a certain library, so that it becomes\n" + 
        "a de-facto standard.  To achieve this, non-free programs must be\n" + 
        "allowed to use the library.  A more frequent case is that a free\n" + 
        "library does the same job as widely used non-free libraries.  In this\n" + 
        "case, there is little to gain by limiting the free library to free\n" + 
        "software only, so we use the Lesser General Public License.\n" + 
        "\n" + 
        "  In other cases, permission to use a particular library in non-free\n" + 
        "programs enables a greater number of people to use a large body of\n" + 
        "free software.  For example, permission to use the GNU C Library in\n" + 
        "non-free programs enables many more people to use the whole GNU\n" + 
        "operating system, as well as its variant, the GNU/Linux operating\n" + 
        "system.\n" + 
        "\n" + 
        "  Although the Lesser General Public License is Less protective of the\n" + 
        "users' freedom, it does ensure that the user of a program that is\n" + 
        "linked with the Library has the freedom and the wherewithal to run\n" + 
        "that program using a modified version of the Library.\n" + 
        "\n" + 
        "  The precise terms and conditions for copying, distribution and\n" + 
        "modification follow.  Pay close attention to the difference between a\n" + 
        "\"work based on the library\" and a \"work that uses the library\".  The\n" + 
        "former contains code derived from the library, whereas the latter must\n" + 
        "be combined with the library in order to run.\n" + 
        "\n" + 
        "		  GNU LESSER GENERAL PUBLIC LICENSE\n" + 
        "   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n" + 
        "\n" + 
        "  0. This License Agreement applies to any software library or other\n" + 
        "program which contains a notice placed by the copyright holder or\n" + 
        "other authorized party saying it may be distributed under the terms of\n" + 
        "this Lesser General Public License (also called \"this License\").\n" + 
        "Each licensee is addressed as \"you\".\n" + 
        "\n" + 
        "  A \"library\" means a collection of software functions and/or data\n" + 
        "prepared so as to be conveniently linked with application programs\n" + 
        "(which use some of those functions and data) to form executables.\n" + 
        "\n" + 
        "  The \"Library\", below, refers to any such software library or work\n" + 
        "which has been distributed under these terms.  A \"work based on the\n" + 
        "Library\" means either the Library or any derivative work under\n" + 
        "copyright law: that is to say, a work containing the Library or a\n" + 
        "portion of it, either verbatim or with modifications and/or translated\n" + 
        "straightforwardly into another language.  (Hereinafter, translation is\n" + 
        "included without limitation in the term \"modification\".)\n" + 
        "\n" + 
        "  \"Source code\" for a work means the preferred form of the work for\n" + 
        "making modifications to it.  For a library, complete source code means\n" + 
        "all the source code for all modules it contains, plus any associated\n" + 
        "interface definition files, plus the scripts used to control compilation\n" + 
        "and installation of the library.\n" + 
        "\n" + 
        "  Activities other than copying, distribution and modification are not\n" + 
        "covered by this License; they are outside its scope.  The act of\n" + 
        "running a program using the Library is not restricted, and output from\n" + 
        "such a program is covered only if its contents constitute a work based\n" + 
        "on the Library (independent of the use of the Library in a tool for\n" + 
        "writing it).  Whether that is true depends on what the Library does\n" + 
        "and what the program that uses the Library does.\n" + 
        "  \n" + 
        "  1. You may copy and distribute verbatim copies of the Library's\n" + 
        "complete source code as you receive it, in any medium, provided that\n" + 
        "you conspicuously and appropriately publish on each copy an\n" + 
        "appropriate copyright notice and disclaimer of warranty; keep intact\n" + 
        "all the notices that refer to this License and to the absence of any\n" + 
        "warranty; and distribute a copy of this License along with the\n" + 
        "Library.\n" + 
        "\n" + 
        "  You may charge a fee for the physical act of transferring a copy,\n" + 
        "and you may at your option offer warranty protection in exchange for a\n" + 
        "fee.\n" + 
        "\n" + 
        "  2. You may modify your copy or copies of the Library or any portion\n" + 
        "of it, thus forming a work based on the Library, and copy and\n" + 
        "distribute such modifications or work under the terms of Section 1\n" + 
        "above, provided that you also meet all of these conditions:\n" + 
        "\n" + 
        "    a) The modified work must itself be a software library.\n" + 
        "\n" + 
        "    b) You must cause the files modified to carry prominent notices\n" + 
        "    stating that you changed the files and the date of any change.\n" + 
        "\n" + 
        "    c) You must cause the whole of the work to be licensed at no\n" + 
        "    charge to all third parties under the terms of this License.\n" + 
        "\n" + 
        "    d) If a facility in the modified Library refers to a function or a\n" + 
        "    table of data to be supplied by an application program that uses\n" + 
        "    the facility, other than as an argument passed when the facility\n" + 
        "    is invoked, then you must make a good faith effort to ensure that,\n" + 
        "    in the event an application does not supply such function or\n" + 
        "    table, the facility still operates, and performs whatever part of\n" + 
        "    its purpose remains meaningful.\n" + 
        "\n" + 
        "    (For example, a function in a library to compute square roots has\n" + 
        "    a purpose that is entirely well-defined independent of the\n" + 
        "    application.  Therefore, Subsection 2d requires that any\n" + 
        "    application-supplied function or table used by this function must\n" + 
        "    be optional: if the application does not supply it, the square\n" + 
        "    root function must still compute square roots.)\n" + 
        "\n" + 
        "These requirements apply to the modified work as a whole.  If\n" + 
        "identifiable sections of that work are not derived from the Library,\n" + 
        "and can be reasonably considered independent and separate works in\n" + 
        "themselves, then this License, and its terms, do not apply to those\n" + 
        "sections when you distribute them as separate works.  But when you\n" + 
        "distribute the same sections as part of a whole which is a work based\n" + 
        "on the Library, the distribution of the whole must be on the terms of\n" + 
        "this License, whose permissions for other licensees extend to the\n" + 
        "entire whole, and thus to each and every part regardless of who wrote\n" + 
        "it.\n" + 
        "\n" + 
        "Thus, it is not the intent of this section to claim rights or contest\n" + 
        "your rights to work written entirely by you; rather, the intent is to\n" + 
        "exercise the right to control the distribution of derivative or\n" + 
        "collective works based on the Library.\n" + 
        "\n" + 
        "In addition, mere aggregation of another work not based on the Library\n" + 
        "with the Library (or with a work based on the Library) on a volume of\n" + 
        "a storage or distribution medium does not bring the other work under\n" + 
        "the scope of this License.\n" + 
        "\n" + 
        "  3. You may opt to apply the terms of the ordinary GNU General Public\n" + 
        "License instead of this License to a given copy of the Library.  To do\n" + 
        "this, you must alter all the notices that refer to this License, so\n" + 
        "that they refer to the ordinary GNU General Public License, version 2,\n" + 
        "instead of to this License.  (If a newer version than version 2 of the\n" + 
        "ordinary GNU General Public License has appeared, then you can specify\n" + 
        "that version instead if you wish.)  Do not make any other change in\n" + 
        "these notices.\n" + 
        "\n" + 
        "  Once this change is made in a given copy, it is irreversible for\n" + 
        "that copy, so the ordinary GNU General Public License applies to all\n" + 
        "subsequent copies and derivative works made from that copy.\n" + 
        "\n" + 
        "  This option is useful when you wish to copy part of the code of\n" + 
        "the Library into a program that is not a library.\n" + 
        "\n" + 
        "  4. You may copy and distribute the Library (or a portion or\n" + 
        "derivative of it, under Section 2) in object code or executable form\n" + 
        "under the terms of Sections 1 and 2 above provided that you accompany\n" + 
        "it with the complete corresponding machine-readable source code, which\n" + 
        "must be distributed under the terms of Sections 1 and 2 above on a\n" + 
        "medium customarily used for software interchange.\n" + 
        "\n" + 
        "  If distribution of object code is made by offering access to copy\n" + 
        "from a designated place, then offering equivalent access to copy the\n" + 
        "source code from the same place satisfies the requirement to\n" + 
        "distribute the source code, even though third parties are not\n" + 
        "compelled to copy the source along with the object code.\n" + 
        "\n" + 
        "  5. A program that contains no derivative of any portion of the\n" + 
        "Library, but is designed to work with the Library by being compiled or\n" + 
        "linked with it, is called a \"work that uses the Library\".  Such a\n" + 
        "work, in isolation, is not a derivative work of the Library, and\n" + 
        "therefore falls outside the scope of this License.\n" + 
        "\n" + 
        "  However, linking a \"work that uses the Library\" with the Library\n" + 
        "creates an executable that is a derivative of the Library (because it\n" + 
        "contains portions of the Library), rather than a \"work that uses the\n" + 
        "library\".  The executable is therefore covered by this License.\n" + 
        "Section 6 states terms for distribution of such executables.\n" + 
        "\n" + 
        "  When a \"work that uses the Library\" uses material from a header file\n" + 
        "that is part of the Library, the object code for the work may be a\n" + 
        "derivative work of the Library even though the source code is not.\n" + 
        "Whether this is true is especially significant if the work can be\n" + 
        "linked without the Library, or if the work is itself a library.  The\n" + 
        "threshold for this to be true is not precisely defined by law.\n" + 
        "\n" + 
        "  If such an object file uses only numerical parameters, data\n" + 
        "structure layouts and accessors, and small macros and small inline\n" + 
        "functions (ten lines or less in length), then the use of the object\n" + 
        "file is unrestricted, regardless of whether it is legally a derivative\n" + 
        "work.  (Executables containing this object code plus portions of the\n" + 
        "Library will still fall under Section 6.)\n" + 
        "\n" + 
        "  Otherwise, if the work is a derivative of the Library, you may\n" + 
        "distribute the object code for the work under the terms of Section 6.\n" + 
        "Any executables containing that work also fall under Section 6,\n" + 
        "whether or not they are linked directly with the Library itself.\n" + 
        "\n" + 
        "  6. As an exception to the Sections above, you may also combine or\n" + 
        "link a \"work that uses the Library\" with the Library to produce a\n" + 
        "work containing portions of the Library, and distribute that work\n" + 
        "under terms of your choice, provided that the terms permit\n" + 
        "modification of the work for the customer's own use and reverse\n" + 
        "engineering for debugging such modifications.\n" + 
        "\n" + 
        "  You must give prominent notice with each copy of the work that the\n" + 
        "Library is used in it and that the Library and its use are covered by\n" + 
        "this License.  You must supply a copy of this License.  If the work\n" + 
        "during execution displays copyright notices, you must include the\n" + 
        "copyright notice for the Library among them, as well as a reference\n" + 
        "directing the user to the copy of this License.  Also, you must do one\n" + 
        "of these things:\n" + 
        "\n" + 
        "    a) Accompany the work with the complete corresponding\n" + 
        "    machine-readable source code for the Library including whatever\n" + 
        "    changes were used in the work (which must be distributed under\n" + 
        "    Sections 1 and 2 above); and, if the work is an executable linked\n" + 
        "    with the Library, with the complete machine-readable \"work that\n" + 
        "    uses the Library\", as object code and/or source code, so that the\n" + 
        "    user can modify the Library and then relink to produce a modified\n" + 
        "    executable containing the modified Library.  (It is understood\n" + 
        "    that the user who changes the contents of definitions files in the\n" + 
        "    Library will not necessarily be able to recompile the application\n" + 
        "    to use the modified definitions.)\n" + 
        "\n" + 
        "    b) Use a suitable shared library mechanism for linking with the\n" + 
        "    Library.  A suitable mechanism is one that (1) uses at run time a\n" + 
        "    copy of the library already present on the user's computer system,\n" + 
        "    rather than copying library functions into the executable, and (2)\n" + 
        "    will operate properly with a modified version of the library, if\n" + 
        "    the user installs one, as long as the modified version is\n" + 
        "    interface-compatible with the version that the work was made with.\n" + 
        "\n" + 
        "    c) Accompany the work with a written offer, valid for at\n" + 
        "    least three years, to give the same user the materials\n" + 
        "    specified in Subsection 6a, above, for a charge no more\n" + 
        "    than the cost of performing this distribution.\n" + 
        "\n" + 
        "    d) If distribution of the work is made by offering access to copy\n" + 
        "    from a designated place, offer equivalent access to copy the above\n" + 
        "    specified materials from the same place.\n" + 
        "\n" + 
        "    e) Verify that the user has already received a copy of these\n" + 
        "    materials or that you have already sent this user a copy.\n" + 
        "\n" + 
        "  For an executable, the required form of the \"work that uses the\n" + 
        "Library\" must include any data and utility programs needed for\n" + 
        "reproducing the executable from it.  However, as a special exception,\n" + 
        "the materials to be distributed need not include anything that is\n" + 
        "normally distributed (in either source or binary form) with the major\n" + 
        "components (compiler, kernel, and so on) of the operating system on\n" + 
        "which the executable runs, unless that component itself accompanies\n" + 
        "the executable.\n" + 
        "\n" + 
        "  It may happen that this requirement contradicts the license\n" + 
        "restrictions of other proprietary libraries that do not normally\n" + 
        "accompany the operating system.  Such a contradiction means you cannot\n" + 
        "use both them and the Library together in an executable that you\n" + 
        "distribute.\n" + 
        "\n" + 
        "  7. You may place library facilities that are a work based on the\n" + 
        "Library side-by-side in a single library together with other library\n" + 
        "facilities not covered by this License, and distribute such a combined\n" + 
        "library, provided that the separate distribution of the work based on\n" + 
        "the Library and of the other library facilities is otherwise\n" + 
        "permitted, and provided that you do these two things:\n" + 
        "\n" + 
        "    a) Accompany the combined library with a copy of the same work\n" + 
        "    based on the Library, uncombined with any other library\n" + 
        "    facilities.  This must be distributed under the terms of the\n" + 
        "    Sections above.\n" + 
        "\n" + 
        "    b) Give prominent notice with the combined library of the fact\n" + 
        "    that part of it is a work based on the Library, and explaining\n" + 
        "    where to find the accompanying uncombined form of the same work.\n" + 
        "\n" + 
        "  8. You may not copy, modify, sublicense, link with, or distribute\n" + 
        "the Library except as expressly provided under this License.  Any\n" + 
        "attempt otherwise to copy, modify, sublicense, link with, or\n" + 
        "distribute the Library is void, and will automatically terminate your\n" + 
        "rights under this License.  However, parties who have received copies,\n" + 
        "or rights, from you under this License will not have their licenses\n" + 
        "terminated so long as such parties remain in full compliance.\n" + 
        "\n" + 
        "  9. You are not required to accept this License, since you have not\n" + 
        "signed it.  However, nothing else grants you permission to modify or\n" + 
        "distribute the Library or its derivative works.  These actions are\n" + 
        "prohibited by law if you do not accept this License.  Therefore, by\n" + 
        "modifying or distributing the Library (or any work based on the\n" + 
        "Library), you indicate your acceptance of this License to do so, and\n" + 
        "all its terms and conditions for copying, distributing or modifying\n" + 
        "the Library or works based on it.\n" + 
        "\n" + 
        "  10. Each time you redistribute the Library (or any work based on the\n" + 
        "Library), the recipient automatically receives a license from the\n" + 
        "original licensor to copy, distribute, link with or modify the Library\n" + 
        "subject to these terms and conditions.  You may not impose any further\n" + 
        "restrictions on the recipients' exercise of the rights granted herein.\n" + 
        "You are not responsible for enforcing compliance by third parties with\n" + 
        "this License.\n" + 
        "\n" + 
        "  11. If, as a consequence of a court judgment or allegation of patent\n" + 
        "infringement or for any other reason (not limited to patent issues),\n" + 
        "conditions are imposed on you (whether by court order, agreement or\n" + 
        "otherwise) that contradict the conditions of this License, they do not\n" + 
        "excuse you from the conditions of this License.  If you cannot\n" + 
        "distribute so as to satisfy simultaneously your obligations under this\n" + 
        "License and any other pertinent obligations, then as a consequence you\n" + 
        "may not distribute the Library at all.  For example, if a patent\n" + 
        "license would not permit royalty-free redistribution of the Library by\n" + 
        "all those who receive copies directly or indirectly through you, then\n" + 
        "the only way you could satisfy both it and this License would be to\n" + 
        "refrain entirely from distribution of the Library.\n" + 
        "\n" + 
        "If any portion of this section is held invalid or unenforceable under any\n" + 
        "particular circumstance, the balance of the section is intended to apply,\n" + 
        "and the section as a whole is intended to apply in other circumstances.\n" + 
        "\n" + 
        "It is not the purpose of this section to induce you to infringe any\n" + 
        "patents or other property right claims or to contest validity of any\n" + 
        "such claims; this section has the sole purpose of protecting the\n" + 
        "integrity of the free software distribution system which is\n" + 
        "implemented by public license practices.  Many people have made\n" + 
        "generous contributions to the wide range of software distributed\n" + 
        "through that system in reliance on consistent application of that\n" + 
        "system; it is up to the author/donor to decide if he or she is willing\n" + 
        "to distribute software through any other system and a licensee cannot\n" + 
        "impose that choice.\n" + 
        "\n" + 
        "This section is intended to make thoroughly clear what is believed to\n" + 
        "be a consequence of the rest of this License.\n" + 
        "\n" + 
        "  12. If the distribution and/or use of the Library is restricted in\n" + 
        "certain countries either by patents or by copyrighted interfaces, the\n" + 
        "original copyright holder who places the Library under this License may add\n" + 
        "an explicit geographical distribution limitation excluding those countries,\n" + 
        "so that distribution is permitted only in or among countries not thus\n" + 
        "excluded.  In such case, this License incorporates the limitation as if\n" + 
        "written in the body of this License.\n" + 
        "\n" + 
        "  13. The Free Software Foundation may publish revised and/or new\n" + 
        "versions of the Lesser General Public License from time to time.\n" + 
        "Such new versions will be similar in spirit to the present version,\n" + 
        "but may differ in detail to address new problems or concerns.\n" + 
        "\n" + 
        "Each version is given a distinguishing version number.  If the Library\n" + 
        "specifies a version number of this License which applies to it and\n" + 
        "\"any later version\", you have the option of following the terms and\n" + 
        "conditions either of that version or of any later version published by\n" + 
        "the Free Software Foundation.  If the Library does not specify a\n" + 
        "license version number, you may choose any version ever published by\n" + 
        "the Free Software Foundation.\n" + 
        "\n" + 
        "  14. If you wish to incorporate parts of the Library into other free\n" + 
        "programs whose distribution conditions are incompatible with these,\n" + 
        "write to the author to ask for permission.  For software which is\n" + 
        "copyrighted by the Free Software Foundation, write to the Free\n" + 
        "Software Foundation; we sometimes make exceptions for this.  Our\n" + 
        "decision will be guided by the two goals of preserving the free status\n" + 
        "of all derivatives of our free software and of promoting the sharing\n" + 
        "and reuse of software generally.\n" + 
        "\n" + 
        "			    NO WARRANTY\n" + 
        "\n" + 
        "  15. BECAUSE THE LIBRARY IS LICENSED FREE OF CHARGE, THERE IS NO\n" + 
        "WARRANTY FOR THE LIBRARY, TO THE EXTENT PERMITTED BY APPLICABLE LAW.\n" + 
        "EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR\n" + 
        "OTHER PARTIES PROVIDE THE LIBRARY \"AS IS\" WITHOUT WARRANTY OF ANY\n" + 
        "KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE\n" + 
        "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR\n" + 
        "PURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE\n" + 
        "LIBRARY IS WITH YOU.  SHOULD THE LIBRARY PROVE DEFECTIVE, YOU ASSUME\n" + 
        "THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.\n" + 
        "\n" + 
        "  16. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN\n" + 
        "WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY\n" + 
        "AND/OR REDISTRIBUTE THE LIBRARY AS PERMITTED ABOVE, BE LIABLE TO YOU\n" + 
        "FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR\n" + 
        "CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE\n" + 
        "LIBRARY (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING\n" + 
        "RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A\n" + 
        "FAILURE OF THE LIBRARY TO OPERATE WITH ANY OTHER SOFTWARE), EVEN IF\n" + 
        "SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH\n" + 
        "DAMAGES.\n" + 
        "\n" + 
        "		     END OF TERMS AND CONDITIONS\n" ;
    }

    private void OKButton_actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }
}
