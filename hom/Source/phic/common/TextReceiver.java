package phic.common;
/**
 * Interface to receive text from the engine - for example, implemented
 * by console class and the frame
 */
public interface TextReceiver{
	void message(String s);

	void error(String s);
}