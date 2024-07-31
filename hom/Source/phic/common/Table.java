package phic.common;
import java.io.*;
import java.util.Vector;
import phic.*;

/**
 * Table
 * Functionality for reading a text-file as a table of strings or doubles
 * with fixed columns and white-space delimiters
 */
public class Table{
	public Vector rows=new Vector();

	public Table(String tablefile,int width){
		try{
			Reader r=new BufferedReader(new InputStreamReader(Resource.loader.
					getResource(tablefile)));
			StreamTokenizer st=new StreamTokenizer(r);
//st.wordChars('/','/');
			st.wordChars('$','$');  st.wordChars('_','_');
			boolean eof=false;
			int tt;
			while(!eof){
				String[] str=new String[width];
				for(int i=0;i<width;i++){
					switch(st.nextToken()){
					case StreamTokenizer.TT_EOF:
						eof=true;
						break;
					case StreamTokenizer.TT_WORD:
                                        case 34: //hack to get quoted strings!
						str[i]=st.sval;
						break;
					case StreamTokenizer.TT_NUMBER:
						double d=st.nval;
						int p=0;
						if(st.nextToken()==StreamTokenizer.TT_WORD){
							if(st.sval.startsWith("E")){
								String ex=st.sval.substring(1);
								boolean num=true;
								try{
									p=Integer.parseInt(ex);
								} catch(NumberFormatException e){
									num=false;
								}
								if(num){
									st.nextToken();
								}
							}
						}
						str[i]=Double.toString(d*Math.pow(10,p));
						st.pushBack();
					break;
					}
					if(eof){
						break;
					}
				}
				if(!eof){
					rows.add(str);
				}
			}
		} catch(Exception e){
			System.out.println("Failed to initialise table "+tablefile+", due to:");
			e.printStackTrace();
		}
		this.nCols=width;
		this.nRows=rows.size();
                dbls= new double[nRows][nCols];
                for(int i=0;i<nRows;i++)for(int j=0;j<nCols;j++)
                  try{dbls[i][j]=Double.parseDouble(((String[])rows.get(i))[j]);}
                  catch(NumberFormatException e){dbls[i][j]=Double.NaN;}

	}
        double[][] dbls;
	public double getDouble(int row,int col){
		//return Double.parseDouble(line(row)[col]);
                return dbls[row][col];
	}

	public String getString(int row,int col){
		return line(row)[col];
	}

	public int nRows,nCols;

	public String[] line(int num){
		return(String[])rows.elementAt(num);
	}

	public int findRowFromColumn(int col,String txt){
		for(int i=0;i<nRows;i++){
			if(line(i)[col].equals(txt)){
				return i;
			}
		}
		Current.body.error(txt+" not found in table");
		return-1;
	}

	public String[] getColumn(int j){
		String[] s=new String[nRows];
		for(int i=0;i<nRows;i++){
			s[i]=getString(i,j);
		}
		return s;
	}
}
