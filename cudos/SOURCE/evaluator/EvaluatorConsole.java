
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package evaluator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class EvaluatorConsole extends JTextPane {


	 Action evaluateAction=new AbstractAction("Evaluate"){
	public void actionPerformed(ActionEvent e){
		evaluateLine();
	}
	 };

	 JTextComponent.KeyBinding[] defaultBindings = {
		 new JTextComponent.KeyBinding(
			 KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
			 "Evaluate" )
	 };
	 JTextComponent c = new JTextPane();




	public EvaluatorConsole() {
		Keymap k = c.getKeymap();
		JTextComponent.loadKeymap( k, defaultBindings, new Action[]{evaluateAction} );


/*		Keymap k=addKeymap("MyMap",getKeymap());
		k.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),
			new
		addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar()=='\n')evaluateLine();
			}
		});
*/
		setFont(new Font("Monospaced",Font.PLAIN,12));
	}

	public void evaluateLine(){
		double ans=0;
		String s="";
		try{
			s=getDocument().getText(0,getDocument().getLength());// getText();
		}catch(Exception e){e.printStackTrace();}
			//get current line
		int p = getCaretPosition();
		int a1=s.lastIndexOf('\n',p-1)+1, a2=s.indexOf('\n',p);
		if(a1<=0)a1=0; if(a2<0)a2=s.length();
//System.out.print(a1+"--"+a2);
		s=s.substring(a1,a2);
//System.out.println(":"+s);
		try{
				//create expression and evaluate
			s=evaluate(s);
				//post answer at end
			insert( '\n' + s + '\n', a2, aOutput);
		}catch(ParseException ex){
			insert('\n' + ex.getMessage() + '\n', a2, aError );
		}catch(MathException ex){
			insert('\n' + ex.getMessage() + '\n', a2, aError);
		}catch(StackException ex){
			insert('\n' + ex.getMessage() + '\n', a2, aError);
		}catch(Exception ex){
			System.out.println("unknown exception");
			ex.printStackTrace();
		}
	}
	String evaluate(String s) throws MathException, ParseException, StackException{
		int eq=s.indexOf('=');
		String left=null;
		if(eq>=0){
			left=s.substring(0,eq);
			String right=s.substring(eq+1);
			s=right;
		}
		Expression expr=new Expression(s);
		double ans=expr.value();
		if(left!=null){
			set(left, ans);
		}
		return String.valueOf(ans);
	}
	public void set(String vname , double value) throws EquationException{
		String n=makeword(vname);
		if(n==null)throw new EquationException("Invalid LHS");
		Variable.set(n, value);
	}
	String makeword(String s){
		if(s.length()==0)return null;
		String n=new String();
		char c=s.charAt(0);
		if(Character.isJavaIdentifierStart(c))n+=c;
		else return null;
		for(int i=1;i<s.length();i++){
			c=s.charAt(i);
			if(Character.isIdentifierIgnorable(c))continue;
			if(Character.isJavaIdentifierPart(c))n+=c;
			else return null;
		}
		return n;
	}


		//inserting text

	static SimpleAttributeSet aOutput=new SimpleAttributeSet(),
		aInput=new SimpleAttributeSet(), aError=new SimpleAttributeSet();
	static{
		aOutput.addAttribute( StyleConstants.Foreground, Color.blue );
		aError.addAttribute( StyleConstants.Foreground, Color.red );
		aInput.addAttribute( StyleConstants.Foreground, Color.black );
	}
	void insert(String s,int p,AttributeSet a){
		try{
			getDocument().insertString(p,s,a);
			this.setCaretPosition(p+s.length());
		}catch(Exception e){
			e.printStackTrace();
		}
		setCharacterAttributes(aInput,true);
	}
}
