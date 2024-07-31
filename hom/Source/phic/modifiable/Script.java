package phic.modifiable;

import java.util.*;
import evaluator.*;
import phic.Current;
import phic.common.Ticker;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import phic.Person;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/**
 * This is the class that implements scripting. It is based on a collection
 * of Statement  objects which can be executed at set intervals.
 */
public class Script
    implements Ticker, Serializable {
  /** Create a blank script */
  public Script() {
  }

  /**
   * Create a script with the given name and description, and compiles
   * the given expression string. If continuous is true, the script is
   * automatically configured to run continuously during processing.
   */

  public Script(String name, String description, String expr,
                boolean continuous) {
    this.name = name;
    this.description = description;
    setText(expr);
    this.setContinuous(continuous);
  }

  /** The name of the script */
  public String name = "Script" + serial++;
  /** counter for script generation */
  private static int serial = 0;
  /** A description of the script */
  public String description = "";

  /**
   * The time interval over which this script will execute,
   * measured in body seconds. If this is zero, it will execute every cycle of
   * the body clock.
   */
  private double executionInterval = 1;
  /**
   * Gets the execution interval. This is the approximate amount of time that
   * is allowed to elapse between successive calls to the script.
   * This is only used if the script is set to execute continuously
   * (@see #setContinuous(double) )
   *
   * @return the current execution interval, in seconds
   */
  public double getExecutionInterval() {
    return executionInterval;
  }

  /**
   * Sets the execution interval. @see #getExecutionInterval()
   *
   * @param executionInterval the execution interval, in seconds
   */
  public void setExecutionInterval(double executionInterval) {
    this.executionInterval = executionInterval;
  }

  /**
   * If this is false, then the script will only execute once. If it is true,
   * then executeScript(time) is called every clock cycle. That may or may not
   * result in execution of the script, depending on the value of
   * executionInterval.
   */
  private boolean continuous = false;

  /**
   * Sets the script to run continuously, or only when requested using the
   * executeOnce command.
   * Setting the script to run continuously will request a regular notification
   * from the body clock.
   *
   * @param c whether or not the script shall run continuously or not.
   */
  public void setContinuous(boolean c) {
    if (c == true && continuous == false) {
      Current.body.getClock().addTicker(this);
    }
    else if (c == false) {
      Current.body.getClock().removeTicker(this);
    }
    continuous = c;
  }

  /**
   * Returns true if the script is set to run continuously.
   */
  public boolean isContinuous() {
    return continuous;
  }

  /**
   * The collection of Statement objects that are evaluated each time the script
   * is executed.
   */
  Vector statements = new Vector();

  public Statement[] getStatements(){
    return (Statement[])statements.toArray(new Statement[statements.size()]);
  }

  /**
   * Craetes a string from the statements in this Script. Each expression's
   * definition is separated by a semicolon.
   * @return a representation of this script
   */

  public String getText() {
    String s = "";
    for (int i = 0; i < statements.size(); i++) {
      if (i > 0) {
        s += "; ";
      }
      s += ( (Statement) statements.get(i)).getDefinition();
    }
    return s;
  }

  /**
   * Sets the statements in this script. The method parses individual expressions
   * separated by semicolons.
   *
   * Note that the parse exception is not handled: if the parse fails, then
   * the OLD EXPRESSIONS ARE RETAINED. The data passed in the parameter 'text'
   * is lost.
   *
   * @throws Parse exceptions when there is an error in parsing the expressions.
   * @param text the text for the script.
   */
  public void setText(String text) throws ParseException {
    Vector newStatements = new Vector();
    evaluator.Variable.set("elapsedTime", new Double(0)); //need dummy value to compile
    int i = 0;
    while (i < text.length()) {
      int j = text.indexOf(';', i);
      if (j < 0) {
        j = text.length();
      }
      String t = text.substring(i, j);
      if (t.length() > 0) {
        newStatements.add(new Statement(t));
      }
      i = j + 1;
    }
    statements = newStatements;
  }

  /** keep a log of the time elapsed since the script was last actually executed */
  protected double totalElapsedSinceExecute = 0;

  public void tick(double elapsedTime) {
    try {
      executeScript(elapsedTime);
    }
    catch (Exception e) { // in event of error,
      setContinuous(false); // stop this script from running
      e.printStackTrace(); // and dump error to user & console
      Current.body.message("Script " + name + " terminated because of " +
                           e.getMessage());
    }
  }

  /**
   * Executes this script over a given interval. This is called by the Organ
   * cycle for scripts that are continuous.
   *
   * This method may or may not result in the script actually being invoked,
   * depending on whether the total time elapsed (as determined by calls to
   * this function) exceeds the interval at which this script should execute (as
   * given by executionInterval).
   *
   * If executionInterval <= 0, then the script is executed EVERY clock cycle,
   * with the actual elapsed time, regardless of how long has elapsed.
   */
  public final void executeScript(double elapsedTime) throws MathException,
      StackException {
    if (executionInterval > 0) {
      totalElapsedSinceExecute += elapsedTime;
      while (totalElapsedSinceExecute >= executionInterval) {
        totalElapsedSinceExecute -= executionInterval;
        evaluator.Variable.set("elapsedTime", new Double(executionInterval));
        executeOnce();
      }
    }
    else {
      evaluator.Variable.set("elapsedTime", new Double(elapsedTime));
      executeOnce();
    }
  }

  /**
   * Executes the script once. The return value is discarded.
   *
   * @throws MathException thrown if there is a numerical error (e.g. division
   * by zero) during the script.
   * @throws StackException thrown if there is a stack overflow error during
   * the script's execution.
   */
  public final void executeOnce() throws MathException, StackException {
    for (int i = 0; i < statements.size(); i++) {
      ( (Statement) statements.get(i)).evaluate();
    }
  }

  /** Tostring returns the script's name */
  public String toString() {
    return name;
  }

  /**
   * Read a series of script commands from a stream
   * and execute them one by one. Strip comments after all occurrences
   * of # or //. Reads the stream to the very end, or until a line
   * that starts with <stopsymbol>
   */
  public static void executeScriptFromStream(InputStream s, String stopsymbol) throws
      IOException, InvocationTargetException {
    if (stopsymbol == null) {
      stopsymbol = "// End of file";
    }
    BufferedReader r = new BufferedReader(new InputStreamReader(s));
    Script sc = new Script();
    String q = r.readLine();
    int l = 0;
    while (q != null && !q.trim().startsWith(stopsymbol)) {
      l++;
      int k = q.indexOf("//");
      if (k >= 0) {
        q = q.substring(0, k);
      }
      k = q.indexOf("#");
      if (k >= 0) {
        q = q.substring(0, k);
      }
      q = q.trim();
      if (q.length() > 0) {
        sc.setText(q);
        try {
          sc.executeOnce();
        }
        catch (Exception m) {
          throw new InvocationTargetException(m,
                                              "Error in script at line " + l + ": " +
                                              q);
        }
      }
      q = r.readLine();
    }
  }

  /**
   * Creates a script from a sequence of strings.
   * @param s String[] the strings to process, in logical order.
   */
  public Script(String[] s) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < s.length; i++) {
      if (s[i] == null) {
        continue;
      }
      String st = s[i].trim();
      if (st.equals("") || st.equals(";")) {
        continue;
      }
      sb.append(st);
      if (!st.endsWith(";")) {
        sb.append(";");
      }
    }
    setText(sb.toString());
  }

}
