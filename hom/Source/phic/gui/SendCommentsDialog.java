package phic.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.CharArrayWriter;
import phic.Current;
import java.io.PrintWriter;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import phic.Resource;     // Used for date formatting.

/**
 * Allows comments to be sent to a server.
 * Requires internet connection
 */

public class SendCommentsDialog extends ModalDialog {
  JPanel mainpanel = new JPanel();
  JPanel lowerpanel = new JPanel();
  JButton sendbutton = new JButton();
  JButton cancelbutton = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea comments = new JTextArea();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel3 = new JPanel();
  JTextArea jTextArea2 = new JTextArea();
  BorderLayout borderLayout3 = new BorderLayout();
  JPanel jPanel4 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  Border border1;
  JPanel jPanel5 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextArea triedtxt = new JTextArea();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel jPanel6 = new JPanel();
  JLabel jLabel3 = new JLabel();
  JScrollPane jScrollPane3 = new JScrollPane();
  JTextArea expectedtxt = new JTextArea();
  BorderLayout borderLayout6 = new BorderLayout();
  JPanel jPanel7 = new JPanel();
  JLabel jLabel4 = new JLabel();
  JScrollPane jScrollPane4 = new JScrollPane();
  JTextArea happenedtxt = new JTextArea();
  BorderLayout borderLayout7 = new BorderLayout();
  GridLayout gridLayout1 = new GridLayout();
  JSplitPane jSplitPane1 = new JSplitPane();
  public SendCommentsDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setTitle("Send problem to author");
    setSize(400,400);
  }
  /**
   * This notifies the user of the exception then tries to send the error to the
   *  author.
   */
  public SendCommentsDialog(Exception e){
    this();
    JOptionPane.showMessageDialog(this,
     "The following error occured: "+e.toString()+"\n"+
     "You may now send this information to the author.",
     "Error report",      JOptionPane.INFORMATION_MESSAGE);
    CharArrayWriter cw= new CharArrayWriter();
    e.printStackTrace(new PrintWriter(cw));
    extraData=new String(cw.toCharArray());
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder(5,5,5,5);
    sendbutton.setToolTipText("Sends the message to the author");
    sendbutton.setText("Send");
    sendbutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sendbutton_actionPerformed(e);
      }
    });
    cancelbutton.setText("Cancel");
    cancelbutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelbutton_actionPerformed(e);
      }
    });
    mainpanel.setLayout(borderLayout1);
    jPanel2.setLayout(borderLayout2);
    comments.setToolTipText("Insert your additional comments here");
    comments.setText("");
    jLabel1.setText("Comments:");
    jTextArea2.setBackground(Color.white);
    jTextArea2.setEnabled(true);
    jTextArea2.setFont(new java.awt.Font("Dialog", 0, 12));
    jTextArea2.setOpaque(false);
    jTextArea2.setEditable(false);
    jTextArea2.setText("This program will attempt to send an e-mail message to the author, " +
    "detailing the current state of the simulation, and what you think " +
    "the problem is. Information about your Java version will also be sent.");
    jTextArea2.setLineWrap(true);
    jTextArea2.setWrapStyleWord(true);
    jPanel3.setLayout(borderLayout3);
    jPanel4.setLayout(borderLayout4);
    mainpanel.setBorder(border1);
    jLabel2.setText("I tried to:");
    jPanel5.setLayout(borderLayout5);
    jLabel3.setText("I expected that:");
    expectedtxt.setToolTipText("Please enter what you feel should happen in the given situation");
    expectedtxt.setText("");
    jPanel6.setLayout(borderLayout6);
    jLabel4.setText("But in fact what happened was:");
    jPanel7.setLayout(borderLayout7);
    happenedtxt.setToolTipText("Please enter what you feel the erroneous values or results are.");
    happenedtxt.setText("");
    jPanel1.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    gridLayout1.setRows(3);
    jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    jSplitPane1.setResizeWeight(0.5);
    jPanel3.setBorder(BorderFactory.createLoweredBevelBorder());
    progresstxt.setText("");
    lowerpanel.setLayout(borderLayout8);
    triedtxt.setToolTipText("Please enter exactly what values you changed and what buttons you " +
    "pressed.");
    jPanel9.setLayout(borderLayout9);
    jLabel5.setText("Optional: your email address");
    fromemail.setToolTipText("If you specify your email address, the author could contact you to " +
    "inform you of implementations of your suggestion");
    fromemail.setText("");
    this.getContentPane().add(mainpanel, BorderLayout.CENTER);
    this.getContentPane().add(lowerpanel,  BorderLayout.SOUTH);
    lowerpanel.add(jPanel8, BorderLayout.CENTER);
    jPanel8.add(cancelbutton, null);
    jPanel8.add(sendbutton, null);
    lowerpanel.add(progresstxt,  BorderLayout.NORTH);
    lowerpanel.add(progressbar,  BorderLayout.WEST);
    jPanel7.add(jScrollPane4, BorderLayout.CENTER);
    jPanel7.add(jLabel4, BorderLayout.NORTH);
    jPanel1.add(jPanel5, null);
    jPanel1.add(jPanel6, null);
    jScrollPane4.getViewport().add(happenedtxt, null);
    jPanel1.add(jPanel7, null);
    jPanel6.add(jScrollPane3, BorderLayout.CENTER);
    jPanel6.add(jLabel3, BorderLayout.NORTH);
    jScrollPane3.getViewport().add(expectedtxt, null);
    jPanel5.add(jScrollPane2, BorderLayout.CENTER);
    jPanel5.add(jLabel2, BorderLayout.NORTH);
    jSplitPane1.add(jPanel4, JSplitPane.BOTTOM);
    jScrollPane2.getViewport().add(triedtxt, null);
    jPanel2.add(jScrollPane1, BorderLayout.CENTER);
    jPanel2.add(jLabel1, BorderLayout.NORTH);
    jPanel4.add(jPanel9,  BorderLayout.SOUTH);
    jPanel9.add(jLabel5,  BorderLayout.NORTH);
    jPanel9.add(fromemail, BorderLayout.CENTER);
    jPanel4.add(jPanel3, BorderLayout.NORTH);
    jPanel3.add(jTextArea2, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(comments, null);
    jPanel4.add(jPanel2, BorderLayout.CENTER);
    mainpanel.add(jSplitPane1,  BorderLayout.CENTER);
    jSplitPane1.add(jPanel1, JSplitPane.TOP);
    progressbar.setVisible(false);
  }
  String extraData = "";
  Thread t;
  JProgressBar progressbar = new JProgressBar();
  JLabel progresstxt = new JLabel();
  JPanel jPanel8 = new JPanel();
  BorderLayout borderLayout8 = new BorderLayout();
  JPanel jPanel9 = new JPanel();
  BorderLayout borderLayout9 = new BorderLayout();
  JLabel jLabel5 = new JLabel();
  JTextField fromemail = new JTextField();
  void sendbutton_actionPerformed(ActionEvent e) {
    t=new Thread(new Runnable(){public void run(){doSend();}});
    t.start();
  }
  public void doSend(){
    progress(10, "Collecting state data");
    CharArrayWriter w = new CharArrayWriter();
    PrintWriter ps = new PrintWriter(w);
    PhicFileDialog.writeVariablesToScript( ps, Current.person);
    String log= "";
    if(getParentFrame() instanceof SimplePhicFrame){
      log = ((SimplePhicFrame)getParentFrame()).statusbar.getText();
    }
    StringBuffer sb = new StringBuffer();
    sb.append("<TRIED>\n"    + triedtxt.getText()    + "\n</TRIED>\n" );
    sb.append("<EXPECTED>\n" + expectedtxt.getText() + "\n</EXPECTED>\n" );
    sb.append("<HAPPENED>\n" + happenedtxt.getText() + "\n</HAPPENED>\n" );
    sb.append("<COMMENTS>\n" + comments.getText()    + "\n</COMMENTS>\n" );
    sb.append("<SCRIPT>\n"   + new String(w.toCharArray()) +"\n</SCRIPT>\n" );
    sb.append("<LOG>\n"      +    log                +"\n</LOG>\n"  );
    if(extraData!=null && extraData.length()>0)
    sb.append("<EXTRA>\n"    +  extraData            + "\n</EXTRA>" );
    sb.append("<CONTEXT>\n\t<HOM_VERSION> "+ Resource.loader.getHOMVersion() + "</HOM_VERSION>");
    putprops(sb, "java.version");
    putprops(sb, "java.vm.name");
    putprops(sb, "java.vm.version");
    putprops(sb, "os.arch");
    putprops(sb, "os.name");
    putprops(sb, "os.version");
    putprops(sb, "user.home");
    putprops(sb, "user.dir");
    mailsend(sb.toString(),fromemail.getText());
  }

  void putprops(StringBuffer sb, String prop){
    String p2=prop.replace('.','_');
    sb.append("\t<"+p2);
    sb.append("> "+System.getProperty(prop));
    sb.append(" </"+p2+">\n");
  }

  public void mailsend2(String message, String from){

  }

  public void mailsend(String message, String from) {
    /**
     * After you have added the above libraries you are ready to start
     * to get into the good stuff!
     */

    /** The first step to be able to send email it to create a connection
     *  to the SMTP port that is listening. Most of the time the SMTP port
     *  for a server is usually 25 but check with your email administrator
     * first to get the right port.
     */
    //Here is the code to make that initial connection:

    Socket smtpSocket = null;
    DataOutputStream os = null;
    BufferedReader is = null;
    String responseline;

    Date dDate = new Date();
    DateFormat dFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
    int port = 25;
    String hostName="smtp.email.msn.com";

    try { // Open port to server
      progress(20,"Opening host");

      smtpSocket = new Socket(hostName, port);

      progress(25,"Logging into server");

      os = new DataOutputStream(smtpSocket.getOutputStream());
      is = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));

      if (smtpSocket != null && os != null && is != null) { // Connection was made.  Socket is ready for use.
        try {


          os.writeBytes("HELO\r\n");
          // You will add the email address that the server
          // you are using know you as.
          os.writeBytes("MAIL From: homphysiology@hotmail.com\r\n");
          wait(is, "250", "HOM address (from) not accepted by server.");

          // Who the email is going to.
          os.writeBytes("RCPT To: homphysiology@hotmail.com\r\n");
          //IF you want to send a CC then you will have to add this
          //os.writeBytes("RCPT Cc: <theCC@anycompany.com>\r\n");

          wait(is, "250", "HOM address (recipient) not accepted by server.");


          // Now we are ready to add the message and the
          // header of the email to be sent out.
          os.writeBytes("DATA\r\n");

          progress(30,"Awaiting response");

          wait(is, "354", "Unable to send data.");
          progress(35,"Sending header");

          os.writeBytes("X-Mailer: Via Java\r\n");
          os.writeBytes("DATE: " + dFormat.format(dDate) + "\r\n");
          if(from==null || from.length()<5) from = "HOM User <homphysiology@hotmail.com>";
          os.writeBytes("From: " + from + "\r\n");
          os.writeBytes("To:  HOM Physiology <homphysiology@hotmail.com>\r\n");
          os.writeBytes("Subject: Your subjectline here\r\n");

          progress(50,"Sending data");
          System.out.println(message);
          os.writeBytes(message + "\r\n");
          os.writeBytes("\r\n.\r\n");

          progress(90,"Awaiting response");
          wait(is, "250", "Message not accepted.");
          os.writeBytes("QUIT\r\n");
          progress(100,"Sent.");
          completed();

        }catch (Exception e) {
          e.printStackTrace();
          error("Cannot send email: "+e.toString());
          progress(0,"Not sent.");
        }
      }
    }catch(Exception e){
      e.printStackTrace();
      error("Cannot connect to server "+hostName+"\n"+e.toString());
      progress(0,"Not sent.");
    }

  }

  void wait(BufferedReader r, String id, String error) throws IOException{
      String z = r.readLine();
      StringBuffer sb=new StringBuffer();
      try{
        while (z != null) {
          System.out.println(z);
          progresstxt.setText(z);
          sb.append(z+'\n');
          if (z.indexOf(id) >= 0)return;
          z=r.readLine();
        }
      }catch(SocketException x){
        throw new IOException(error+" (socket reset)\n"+sb.toString());
      }
      throw new IOException(error+ "\n"+sb.toString());
  }

  void completed(){
    sendbutton.setEnabled(false);
    cancelbutton.setText("Close");
  }

  public void error(String s){
    JOptionPane.showMessageDialog(this, s ,
    "Error sending mail", JOptionPane.ERROR_MESSAGE);
  }

  void cancelbutton_actionPerformed(ActionEvent e) {
    hide();
  }
  public void progress(int n, String s){
    progressbar.setVisible(n>0);
    progressbar.setValue(n);
    progresstxt.setText(s);
  }
}
