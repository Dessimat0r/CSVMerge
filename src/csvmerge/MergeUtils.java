/**
 * 
 */
package csvmerge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * CSVMerge - MergeUtils
 * @version 1.0
 * @author <a href="mailto:Dessimat0r@ntlworld.com">Chris Dennett</a>
 */
public class MergeUtils {
	public static void merge(String title, List<File> files, String[] headers, String header, File out) {
		if (files.isEmpty()) throw new IllegalArgumentException("No files given to merge on header");
		try {
    		CSVReader reader = new CSVReader(new FileReader(files.get(0)));
    		try {
        		try {
        			int index = -1;
        	        String[] line = reader.readNext();
            		for (int i = 0; i < line.length; i++) {
            			if (line[i].equals(header)) {
            				index = i;
            				break;
            			}
            		}
        	        reader.close();
        	        if (index >= 0) {
        	        	merge(title, files, headers, index, header, out);
        	        }
                } catch (IOException e) {
                	throw new IllegalStateException(e);
                }
    		} finally {
    			try {
	                reader.close();
                } catch (IOException e) {
	                e.printStackTrace();
                }
    		}
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static final String chopExt(String name) {
		int id1in = name.lastIndexOf('.');
		String id1s = name.substring(0, id1in);
		return id1s;		
	}
	
	public static final String chopId(String name) {
		int id1in = name.indexOf('-');
		String id1s = name.substring(id1in + 1);
		return id1s;
	}
	
	public static void merge(String title, List<File> files, String[] headers, int header, String colname, File out) {
		if (files.isEmpty()) throw new IllegalArgumentException("No files given to merge on header");
		if (header < 0) {
			if (files.isEmpty()) throw new IllegalArgumentException("Header index < 0");
		}
		List<List<String>> columns = new ArrayList<List<String>>(files.size());
		int findex = 0;
		for (File f : files) {
			try {
    			ArrayList<String> column = new ArrayList<String>();
    			columns.add(column);
    			try {
        			CSVReader reader = new CSVReader(new FileReader(f));
        			try {
            			try {
            				boolean first = true;
                    		String[] line;
                    		column.add(headers[findex]);
                    		while ((line = reader.readNext()) != null) {
                    			//System.out.println("Got " + Arrays.toString(line));
                    			
                    			if (first) {
                    				// column headers
                    				first = false;
                    				continue;
                    			}
                    			
                    			//System.out.println("added: " + line[header]);
                    			
                    			column.add(line[header]);            			
                    		}
                    		reader.close();
            			} catch (IOException e2) {
            				throw new IllegalStateException(e2);
            			}   				
        			} finally {
        				try {
        					reader.close();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
    			} finally {
    				findex++;
    			}
			} catch (FileNotFoundException e) {
				throw new IllegalStateException(e);
			}
        	
			try {
	            CSVWriter writer = new CSVWriter(new FileWriter(out));
	            try {
	            	writer.writeNext(new String[] { title });
	            	// all rows
    	            for (int i = 0; i < columns.get(0).size(); i++) {
    	            	String[] row = new String[columns.size()];
    	            	for (int j = 0; j < columns.size(); j++) {
    	            		row[j] = columns.get(j).get(i);
    	            	}
    	            	writer.writeNext(row);
    	            }
	            } finally {
    	            writer.close();
	            }
            } catch (IOException e) {
            	throw new IllegalStateException(e);
            }
			
		}
		
	}
}
