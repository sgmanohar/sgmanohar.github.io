package phic.common;
/**
 * TwoVarTable: This class wraps a table with a header row and a header
 * column that are doubles, and linearly interpolates
 * a value based on the two coordinates. Note the first
 * value in the table does not count.
 */
public class TwoVarTable extends Table{
	/**
	 * Create a two-var table from a text file, with the given number of columns.
	 */
	public TwoVarTable(String file,int width){
		super(file,width);
                initValues();
	}
        /** Stores the values of this table in an array. This is set up in
         * when the TwoVarTable is constructed.
         */
        double values[][];
        /** Called by the constructor to initialise the values from the file */
        protected void initValues(){
          values = new double[nRows][nCols];
          for (int i=0;i<nRows;i++)for(int j=0;j<nCols;j++){
            if(i==0 && j==0) continue;
            values[i][j]=getDouble(i,j);
          }
        }
	/**
	 * Look up a value in the table according to the
	 * x and y coordinate in the table. Linear interpolation is performed if the
	 * value falls between two columns of the table.
	 * @param v1 the horizontal (column) value
	 * @param v2 the vertical (row) value
	 */
	public double lookUp(double v1,double v2){
		//find rows to interpolate
		//as (i,j)-(i+1,j+1)
		int i,j;
		for(i=1;i<nRows-1;i++){
			if(v2<values[i+1][0]){
				break;
			}
		}
		for(j=1;j<nCols-1;j++){
			if(v1<values[0][j+1]){
				break;
			}
		}
		if(i==nRows-1){
			i=nRows-2;
		}
		if(j==nCols-1){
			j=nCols-2;
		}
		double o=values[i][j]; //lookup floor points
		double tmp,fraci,fracj,ri1,ri2;
		//interpolation
		fraci=(v2-(tmp=values[i][0]))/(values[i+1][0]-tmp);
		fracj=(v1-(tmp=values[0][j]))/(values[0][j+1]-tmp);
		ri1=o+(values[i+1][j]-o)*fraci; // value at (i+di,j)
		ri2=(o=values[i][j+1])+(values[i+1][j+1]-o)*fraci; // value at (i+di,j+1)
		return ri1+(ri2-ri1)*fracj; // value at (i+di,j+dj)
	}
        public Curve getXCurve(VDouble yValue){
          if (curveX == null) curveX = new TableCurve(false,yValue);
          return curveX;
        }
        public Curve getYCurve(VDouble xValue){
          if(curveY==null)curveY=new TableCurve(true, xValue);
          return curveY;
        }
        protected TableCurve curveX, curveY;
        class TableCurve extends Curve{
          boolean xaxis; VDouble otherValue;
          TableCurve(boolean xaxis, VDouble otherValue){this.xaxis=xaxis; this.otherValue=otherValue;}
          public double getValue(double x){
            if(xaxis) return lookUp(x,otherValue.get());
            else      return lookUp(otherValue.get(), x);
          }
          public double getInverse(double y){
            throw new IllegalStateException("Noninvertible function - two-variable table.");
          }
        }
}
