package phic.gui;
import java.awt.BorderLayout;
import java.beans.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import phic.Resource;
import java.awt.Dimension;

/**
 * This is a panel (or a Dialog box, if showDialog() is used) with an OK button
 * and the capability of showing an HTML document.
 */
public class HTMLMessagePane extends JPanel implements HyperlinkListener{
	public HTMLMessagePane(String htmlResource){
		setLayout(new BorderLayout());
		try{
			ed=new JEditorPane(Resource.loader.getResourceURL(htmlResource));
		} catch(IOException x){
			ed=new JEditorPane("text/plain",
					"Error loading file "+htmlResource+"\n"+x.getMessage());
			x.printStackTrace();
		}
		JScrollPane sp=new JScrollPane(ed);
		ed.setEditable(false);
		ed.addHyperlinkListener(this);
		add(op=new JOptionPane(sp,JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION),BorderLayout.CENTER);

	}

	protected JOptionPane op;

	protected JEditorPane ed;

	public void hyperlinkUpdate(HyperlinkEvent e){
		if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED){
			JEditorPane pane=(JEditorPane)e.getSource();
			if(e instanceof HTMLFrameHyperlinkEvent){
				HTMLFrameHyperlinkEvent evt=(HTMLFrameHyperlinkEvent)e;
				HTMLDocument doc=(HTMLDocument)pane.getDocument();
				doc.processHTMLFrameHyperlinkEvent(evt);
			} else{
				try{
					pane.setPage(e.getURL());
				} catch(Throwable t){
					t.printStackTrace();
				}
			}
		}
	}

	public static void showDialog(String htmlResource,String title){
		final ModalDialog d=new ModalDialog();
		HTMLMessagePane mp=new HTMLMessagePane(htmlResource);
		d.getContentPane().add(mp);
		d.pack();
                Dimension d1 = PhicApplication.frame.getJFrame().getSize();
		d.setPreferredSize(new Dimension(Math.min(640,(int)d1.getWidth()-20),
                  Math.min(480,(int)d1.getHeight()-40)));
		mp.op.addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent e){
				d.hide();
			}
		});
                d.setTitle(title);
		d.show();
	}
}