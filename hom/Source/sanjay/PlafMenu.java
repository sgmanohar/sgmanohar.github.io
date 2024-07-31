package sanjay;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
/**
 * A menu to change look and feel
 *
 */


public class PlafMenu extends JMenu {
	Component component;
	public PlafMenu(Component component) {
		super("Look and Feel");
		this.component = component;
		UIManager.LookAndFeelInfo[] li=UIManager.getInstalledLookAndFeels();
		for(int i=0;i<li.length;i++){
			add(new PlafAction(li[i]));
                        if(li[i].getName().equals("Skin look and feel")) addSkinMenu(li[i].getClassName());
		}
                JFrame.setDefaultLookAndFeelDecorated(true);
                add(new WrapAction());
	}
        class WrapAction extends AbstractAction{
          WrapAction(){ super("Anti-Aliasing"); }
          public void actionPerformed(ActionEvent e){
        	try {
				Class.forName("com.l2fprod.common.swing.plaf.wrap.Wrapper")
				  .getMethod("wrap", new Class[]{}).invoke(null, new Object[]{});
			} catch (Exception e1) {				e1.printStackTrace();			}
          }
        }

	class PlafAction extends AbstractAction{
		UIManager.LookAndFeelInfo inf;
		PlafAction(UIManager.LookAndFeelInfo inf){
			super(inf.getName());
			this.inf=inf;
		}
		public void actionPerformed(ActionEvent e){
			try{
				UIManager.setLookAndFeel(inf.getClassName());
				Component x=PlafMenu.this;while(x.getParent()!=null)x=x.getParent();
				SwingUtilities.updateComponentTreeUI(x);
				x=component;while(x.getParent()!=null)x=x.getParent();
				SwingUtilities.updateComponentTreeUI(x);
				x.repaint();
			}catch(Exception x){
				x.printStackTrace();
				JOptionPane.showMessageDialog(null, x.toString(),
					"Error changing look and feel to "+e.getActionCommand(),
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}

        public void updateInterface(){
          Component x=PlafMenu.this;while(x.getParent()!=null)x=x.getParent();
          SwingUtilities.updateComponentTreeUI(x);
          x=component;while(x.getParent()!=null)x=x.getParent();
          SwingUtilities.updateComponentTreeUI(x);
          x.repaint();
        }

        void addSkinMenu(String cls){
            try{
              Class c=Class.forName("sanjay.SkinSelect");
              final Object o=c.newInstance();
              add(new JMenuItem(new AbstractAction("Select skin"){
              public void actionPerformed(ActionEvent e){
                ((Runnable)o).run();
                updateInterface();
              }   }));
            } catch(ClassNotFoundException ex){}
            catch(NoClassDefFoundError ex){}
            catch(InstantiationException ex){}
            catch(IllegalAccessException ex){}

        }
}
