<html>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="java.io.InputStream,
                 java.io.IOException,
                 javax.xml.parsers.SAXParser,
                 java.lang.reflect.*,
                 javax.xml.parsers.SAXParserFactory"
   session="false"
 %>
 
 <%
    /*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
%>
<head>
<title>Axis Happiness Page</title>
</head>
<body bgcolor='#ffffff'>
<%!

    /*
     * Happiness tests for axis. These look at the classpath and warn if things
     * are missing. Normally addng this much code in a JSP page is mad
     * but here we want to validate JSP compilation too, and have a drop-in
     * page for easy re-use
     * @author Steve 'configuration problems' Loughran
     * @author dims
     * @author Brian Ewins
     */


    /**
     * Get a string providing install information.
     * TODO: make this platform aware and give specific hints
     */
    public String getInstallHints(HttpServletRequest request) {

        String hint=
            "<B><I>����:</I></B> Tomcat 4.x �� Java1.4 ��ł́ACATALINA_HOME/common/lib �ɁA"
            +"java.* �������� javax.* �p�b�P�[�W���܂ރ��C�u������z�u����K�v�����邩������܂���B"
            +"<br>���Ƃ��� jaxrpc.jar �� saaj.jar �́A2�̂��̂悤�ȃ��C�u�����ł��B";
          
        return hint;
    }

    /**
     * test for a class existing
     * @param classname
     * @return class iff present
     */
    Class classExists(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * test for resource on the classpath
     * @param resource
     * @return true iff present
     */
    boolean resourceExists(String resource) {
        boolean found;
        InputStream instream=this.getClass().getResourceAsStream(resource);
        found=instream!=null;
        if(instream!=null) {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return found;
    }

    /**
     * probe for a class, print an error message is missing
     * @param out stream to print stuff
     * @param category text like "warning" or "error"
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @return the number of missing classes
     * @throws IOException
     */
    int probeClass(JspWriter out,
                   String category,
                   String classname,
                   String jarFile,
                   String description,
                   String errorText,
                   String homePage) throws IOException {
        try {
            Class clazz = classExists(classname);
            if(clazz == null)  {
               String url="";
               if(homePage!=null) {
                  url="<br>  <a href="+homePage+">"+homePage+"</a>���Q�Ƃ��Ă��������B";
               }
               
               out.write("<p>"+category+":<b>"+jarFile+"</b>�t�@�C�����񋟂���"
                   +classname+"�N���X��������܂���B<br>"
                   +errorText
                   +url
                   +"<p>");
                                     
               return 1;
            } else {
               String location = getLocation(out, clazz);
               if(location == null) {
                  out.write(description + " (" + classname + ") ��������܂����B<br>");
               }
               else {
                  out.write(location + "�ɁA" + description + " (" + classname + ") ��������܂����B<br>");
               }
               return 0;
            }
        } catch(NoClassDefFoundError ncdfe) {
            String url="";
            if(homePage!=null) {
                url="<br>  <a href="+homePage+">"+homePage+"</a> ���Q�Ƃ��Ă��������B";
            }
            out.write("<p>"+category+":<b>"+jarFile+"</b>�t�@�C�����񋟂���"
                    +classname+"�N���X�̈ˑ��֌W�������ł��܂���B<br>"
                    +errorText
                    +url
                    +"<br>���{�I�Ȍ���: "+ncdfe.getMessage()
                    +"<br>���̃G���[�͎��̂悤�ȏꍇ�ɔ�������\��������܂��B"
                    +"'���ʂ�' �N���X�p�X��"+classname+" ���ݒ肳��Ă���ɂ�������炸�A"
                    +" activation.jar �̂悤�Ȉˑ����郉�C�u������webapp�̃N���X�p�X"
                    +"�����ɂ����ݒ肳��Ă��Ȃ��悤�ȏꍇ�B"
                    +"<p>");
            return 1;
        }
    }

    /**
     * get the location of a class
     * @param out
     * @param clazz
     * @return the jar file or path where a class was found
     */

    String getLocation(JspWriter out,
                       Class clazz) {
        try {
            java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            String location = url.toString();
            if(location.startsWith("jar")) {
                url = ((java.net.JarURLConnection)url.openConnection()).getJarFileURL();
                location = url.toString();
            }

            if(location.startsWith("file")) {
                java.io.File file = new java.io.File(url.getFile());
                return file.getAbsolutePath();
            } else {
                return url.toString();
            }
        } catch (Throwable t){
        }
        return "�s���ȏꏊ";
    }

    /**
     * a class we need if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int needClass(JspWriter out,
                   String classname,
                   String jarFile,
                   String description,
                   String errorText,
                   String homePage) throws IOException {
        return probeClass(out,
                "<b>�G���[</b>",
                classname,
                jarFile,
                description,
                errorText,
                homePage);
    }

    /**
     * print warning message if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int wantClass(JspWriter out,
                   String classname,
                   String jarFile,
                   String description,
                   String errorText,
                   String homePage) throws IOException {
        return probeClass(out,
                "<b>�x��</b>",
                classname,
                jarFile,
                description,
                errorText,
                homePage);
    }

    /**
     * probe for a resource existing,
     * @param out
     * @param resource
     * @param errorText
     * @throws Exception
     */
    int wantResource(JspWriter out,
                      String resource,
                      String errorText) throws Exception {
        if(!resourceExists(resource)) {
            out.write("<p><b>�x��</b>: ���\�[�X "+resource+"�������邱�Ƃ��ł��܂���B"
                        +"<br>"
                        +errorText);
            return 0;
        } else {
            out.write(resource+"��������܂����B<br>");
            return 1;
        }
    }


    /**
     *  get servlet version string
     *
     */

    public String getServletVersion() {
        ServletContext context=getServletConfig().getServletContext();
        int major = context.getMajorVersion();
        int minor = context.getMinorVersion();
        return Integer.toString(major) + '.' + Integer.toString(minor);
    }



    /**
     * what parser are we using.
     * @return the classname of the parser
     */
    private String getParserName() {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return "XML Parser�𐶐����邱�Ƃ��ł��܂���B";
        }

        // check to what is in the classname
        String saxParserName = saxParser.getClass().getName();
        return saxParserName;
    }

    /**
     * Create a JAXP SAXParser
     * @return parser or null for trouble
     */
    private SAXParser getSAXParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        if (saxParserFactory == null) {
            return null;
        }
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (Exception e) {
        }
        return saxParser;
    }

    /**
     * get the location of the parser
     * @return path or null for trouble in tracking it down
     */

    private String getParserLocation(JspWriter out) {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return null;
        }
        String location = getLocation(out,saxParser.getClass());
        return location;
    }

    /**
     * Check if class implements specified interface.
     * @param Class clazz
     * @param String interface name
     * @return boolean
     */
    private boolean implementsInterface(Class clazz, String interfaceName) {
        if (clazz == null) {
            return false;
        }
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces.length != 0) {
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i].getName().equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    %>
<html><head><title>Axis Happiness Page</title></head>
<body>
<h1>Axis Happiness Page</h1>
<h2>webapp�̍\���Ɋւ��钲��</h2>
<p>
<h3>�K�{�R���|�[�l���g</h3>
<%
    int needed=0,wanted=0;

    /**
     * the essentials, without these Axis is not going to work
     */

    // need to check if the available version of SAAJ API meets requirements
    String className = "javax.xml.soap.SOAPPart";
    String interfaceName = "org.w3c.dom.Document";
    Class clazz = classExists(className);
    if (clazz == null || implementsInterface(clazz, interfaceName)) {
        needed = needClass(out, "javax.xml.soap.SOAPMessage",
        	"saaj.jar",
            "SAAJ API",
            "�����炭Axis�͓����܂���B",
            "http://xml.apache.org/axis/");
    } else {
        String location = getLocation(out, clazz);
        out.write("<b>�G���[:</b> "+location+"�ɓK�؂łȂ��o�[�W������SAAJ API��������܂����B" +
        "Axis��saaj.jar���ACLASSPATH�ɐݒ肳��Ă���"+location+ "�����O���ɐݒ肵�Ă��������B<br />" +
        "�����炭Axis�͓����܂���B���ڍׂȏ���<a href=\"http://ws.apache.org/axis/java/install.html\">Axis installation instructions</a>���Q�Ƃ��Ă��������B<br />");
    }

    needed+=needClass(out, "javax.xml.rpc.Service",
            "jaxrpc.jar",
            "JAX-RPC API",
            "�����炭Axis�͓����܂���B",
            "http://xml.apache.org/axis/");

    needed+=needClass(out, "org.apache.axis.transport.http.AxisServlet",
            "axis.jar",
            "Apache-Axis",
            "�����炭Axis�͓����܂���B",
            "http://xml.apache.org/axis/");

    needed+=needClass(out, "org.apache.commons.discovery.Resource",
            "commons-discovery.jar",
            "Jakarta-Commons Discovery",
            "�����炭Axis�͓����܂���B",
            "http://jakarta.apache.org/commons/discovery.html");

    needed+=needClass(out, "org.apache.commons.logging.Log",
            "commons-logging.jar",
            "Jakarta-Commons Logging",
            "�����炭Axis�͓����܂���B",
            "http://jakarta.apache.org/commons/logging.html");

    needed+=needClass(out, "org.apache.log4j.Layout",
            "log4j-1.2.8.jar",
            "Log4j",
            "Axis�������Ȃ��\��������܂��B",
            "http://jakarta.apache.org/log4j");

    //should we search for a javax.wsdl file here, to hint that it needs
    //to go into an approved directory? because we dont seem to need to do that.
    needed+=needClass(out, "com.ibm.wsdl.factory.WSDLFactoryImpl",
            "wsdl4j.jar",
            "IBM's WSDL4Java",
            "�����炭Axis�͓����܂���B",
            null);

    needed+=needClass(out, "javax.xml.parsers.SAXParserFactory",
            "xerces.jar",
            "JAXP implementation",
            "�����炭Axis�͓����܂���B",
            "http://xml.apache.org/xerces-j/");

    needed+=needClass(out,"javax.activation.DataHandler",
            "activation.jar",
            "Activation API",
            "�����炭Axis�͓����܂���B",
            "http://java.sun.com/products/javabeans/glasgow/jaf.html");
%>
<h3>�I�v�V���i����R���|�[�l���g</h3>
<%
    /*
     * now the stuff we can live without
     */
    wanted+=wantClass(out,"javax.mail.internet.MimeMessage",
            "mail.jar",
            "Mail API",
            "�����炭Attachments�͋@�\���܂���B",
            "http://java.sun.com/products/javamail/");

    wanted+=wantClass(out,"org.apache.xml.security.Init",
            "xmlsec.jar",
            "XML Security API",
            "XML Security�̓T�|�[�g����܂���B",
            "http://xml.apache.org/security/");

    wanted += wantClass(out, "javax.net.ssl.SSLSocketFactory",
            "jsse.jar or java1.4+ runtime",
            "Java Secure Socket Extension",
            "https�̓T�|�[�g����܂���B",
            "http://java.sun.com/products/jsse/");
    /*
     * resources on the classpath path
     */
    /* broken; this is a file, not a resource
    wantResource(out,"/server-config.wsdd",
            "�T�[�o�[�\���t�@�C��������܂���B"
            +"�쐬���邽�߂�AdminClient�����s���Ă��������B");
    */
    /* add more libraries here */

    out.write("<h3>");
    //is everythng we need here
    if(needed==0) {
       //yes, be happy
        out.write("<i>axis�̃R�A����C�u�����͑��݂��Ă��܂��B </i>");
    } else {
        //no, be very unhappy
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.write("<i>"
                +"axis�̃R�A����C�u����"
                +(needed==1?"��":"��")
                +needed+"�����Ă��܂��B</i>");
    }
    //now look at wanted stuff
    if(wanted>0) {
        out.write("<i>"
                +"axis�̃I�v�V���i������C�u����"
                +(wanted==1?"��":"��")
                +wanted+"�����Ă��܂��B </i>");
    } else {
        out.write("�I�v�V���i����R���|�[�l���g�͑��݂��Ă��܂��B");
    }
    out.write("</h3>");
    //hint if anything is missing
    if(needed>0 || wanted>0 ) {
        out.write(getInstallHints(request));
    }

    %>
    <p>
    <B><I>����:</I></B> �y�[�W�ɑS�Ă̒������ʂ��\�����ꂽ�Ƃ��Ă��A
    �`�F�b�N�ł��Ȃ��\���I�v�V�������������߁A���Ȃ���Web�T�[�r�X������ɋ@�\����ۏ�͂���܂���B
    �����̃e�X�g��<i>�K�v</i>�Ȃ��̂ł����A<i>�\��</i>�Ȃ��̂ł͂���܂���B
    <hr>

    <h2>�A�v���P�[�V������T�[�o�[�Ɋւ��钲��</h2>
    <%
        String servletVersion=getServletVersion();
        String xmlParser=getParserName();
        String xmlParserLocation = getParserLocation(out);

    %>
    <table>
        <tr><td>�T�[�u���b�g�̃o�[�W����</td><td><%= servletVersion %></td></tr>
        <tr><td>XML�p�[�T�[</td><td><%= xmlParser %></td></tr>
        <tr><td>XML�p�[�T�[�̏ꏊ</td><td><%= xmlParserLocation %></td></tr>
    </table>
<% if(xmlParser.indexOf("crimson")>=0) { %>
    <p>
    <b> Axis�Ŏg�p����XML�p�[�T�[�ɂ� Crimson �ł͂Ȃ��A 
        <a href="http://xml.apache.org/xerces2-j/">Xerces 2</a> �𐄏����Ă��܂��B
    </p>
<%    } %>

    <h2>�V�X�e����v���p�e�B�Ɋւ��钲��</h2>
<%
    /**
     * Dump the system properties
     */
    java.util.Enumeration e=null;
    try {
        e= System.getProperties().propertyNames();
    } catch (SecurityException se) {
    }
    if(e!=null) {
        out.write("<pre>");
        for (;e.hasMoreElements();) {
            String key = (String) e.nextElement();
            out.write(key + "=" + System.getProperty(key)+"\n");
        }
        out.write("</pre><p>");
    } else {
        out.write("�V�X�e����v���p�e�B�ɃA�N�Z�X�ł��܂���B<p>");
    }
%>
    <hr>
    �v���b�g�t�H�[��: <%= getServletConfig().getServletContext().getServerInfo()  %>
</body>
</html>

