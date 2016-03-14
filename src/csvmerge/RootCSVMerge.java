/**
 * 
 */
package csvmerge;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CSVMerge - CSVMerge
 * @version 1.0
 * @author <a href="mailto:Dessimat0r@ntlworld.com">Chris Dennett</a>
 */
public class RootCSVMerge {
	protected final File dir;
	protected final String header;
	protected final File out;

	/**
	 * 
	 */
	public RootCSVMerge(File dir, final Pattern pattern, String header, File out) {
		this.dir = dir;
		this.header = header;
		this.out = out;

		System.out.println("Got CSV directory: " + dir);

		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("File doesn't exist or isn't directory.");
		}
		
		List<File> rundirs = new ArrayList<File>();
		
		for (File f : dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				boolean accept = file.isDirectory() && pattern.matcher(file.getName()).matches();
				if (!accept) return false;
				
				File csvdir = new File(file, "csv");
				if (!csvdir.exists() || !csvdir.isDirectory()) {
					System.out.println("Warning: csv dir doesn't exist in matched dir: " + file + ", ignoring!");
					return false;
				}
				File simcsv = new File(csvdir, "sim_stats.csv");
				if (!simcsv.exists() || !simcsv.isFile()) {
					System.out.println("Warning: sim_stats.csv doesn't exist in dir: " + csvdir + ", ignoring rundir: " + file + "!");
					return false;
				}				
				
				return accept;
			}
		})) {
			rundirs.add(f);
		}
		Collections.sort(rundirs, new FilenameComparatorWrapper(new ExplorerSC()));	
		
		String[] headers = new String[rundirs.size()];
		
		for (int i = 0; i < rundirs.size(); i++) {
			File f = rundirs.get(i);
			System.out.println("matched: " + f);
			headers[i] = f.getName();
		}
		
		List<File> mergeFiles = new ArrayList<File>();
		
		for (File rundir : rundirs) {
			File csvdir = new File(rundir, "csv");
			File simcsv = new File(csvdir, "sim_stats.csv");
			mergeFiles.add(simcsv);
		}
		
		MergeUtils.merge(header, mergeFiles, headers, header, out);
	}
	
	public static void main(String[] args) {
		System.out.println("==================== LWJNS3 Root CSVMerger ====================");

		if (args.length < 4) {
			System.out.println("Usage: [root dir] [run dir pattern] [csv header] [dest csv file]");
			return;
		}
		
		File csvpath = new File(args[0]);
		System.out.println("csv path: " + csvpath);	
		
		String runpatternstr = args[1];
		System.out.println("run pattern str: " + runpatternstr);			
		
		Pattern runpattern = Pattern.compile(runpatternstr);
		System.out.println("run pattern: " + runpattern);
		
		String header = args[2];
		System.out.println("header: " + header);
		
		File outpath = new File(args[3]);
		System.out.println("out path: " + outpath);	

		try {
			RootCSVMerge merger = new RootCSVMerge(csvpath, runpattern, header, outpath);
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal argument(s) given: " + e.getMessage());
		}
	}

	/**
	 * @return the dir
	 */
	public File getDir() {
		return dir;
	}
}
