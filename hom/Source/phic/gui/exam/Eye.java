package phic.gui.exam;
import java.awt.Point;

/**
 * Data for an eye
 */
public class Eye{
	public Point centre=new Point();
	/// Variables

	public Point pupilCentre=new Point();

	public double pupilSize=5;

	public boolean abducentPalsy=false;

	public boolean oculomotorPalsy=false;

	public boolean trochlearPalsy=false;

	public double opticPalsy=0;

	public double sympatheticPalsy=0;

	public double parasympatheticPalsy=0;

	public int strabismusX=0;

	public int strabismusY=0;
        /** muscle palsies */
        public double mr, lr, ir, sr, io, so;
}
