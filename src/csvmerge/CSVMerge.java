/**
 *
 */
package csvmerge;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * CSVMerge - CSVMerge
 * @version 1.0
 * @author <a href="mailto:Dessimat0r@ntlworld.com">Chris Dennett</a>
 */
public class CSVMerge {
	protected final File dir;
	protected final File nodesDir;
	protected final Path csvHeaderPath;
	protected final File out;

	/**
	 *
	 */
	public CSVMerge(File dir, Path csvHeaderPath, File out) {
		this.dir = dir;
		this.csvHeaderPath = csvHeaderPath;
		this.out = out;

		System.out.println("Got directory: " + dir);

		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("File doesn't exist or isn't directory.");
		}
		nodesDir = new File(dir, "nodes");

		System.out.println("Nodes dir: " + nodesDir);

		if (!nodesDir.exists() || !nodesDir.isDirectory()) {
			throw new IllegalArgumentException("Nodes dir doesn't exist or isn't directory.");
		}

		ArrayList<File> nodeDirList = new ArrayList<File>();

		// iterate over dirs and create file list
		for (File cd : nodesDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		})) {
			File nodeStats = new File(cd, "node_stats.csv");
			if (!nodeStats.exists() || !nodeStats.isFile()) {
				System.err.println(nodeStats + " doesn't exist! Expected in " + cd);
				continue;
			}
			nodeDirList.add(cd);
		}
		Collections.sort(nodeDirList, new FilenameComparatorWrapper(new ExplorerSC()));

		System.out.println("csvhp[0]: " + csvHeaderPath.name(0));
		System.out.println("csvhp[0].index: " + csvHeaderPath.index(0));
		System.out.println("csvhp[1]: " + csvHeaderPath.name(1));
		System.out.println("csvhp[2]: " + csvHeaderPath.name(2));

		if (csvHeaderPath.name(0).equals("nodes") && csvHeaderPath.index(0).equals("all") && csvHeaderPath.name(1).equals("node_stats")) {
			String headername = csvHeaderPath.name(2);
			ArrayList<File> files = new ArrayList<File>();
			ArrayList<String> headers = new ArrayList<String>();
			for (File f : nodeDirList) {
				files.add(new File(f, "node_stats.csv"));
				headers.add(MergeUtils.chopId(f.getName()));
			}
			MergeUtils.merge(csvHeaderPath.toPortableString(), files, headers.toArray(new String[headers.size()]), headername, out);
		} else {
			System.err.println("Couldn't parse csv header path: " + csvHeaderPath.toPortableString());
		}
	}

	public static void main(String[] args) {
		System.out.println("==================== LWJNS3 CSVMerger ====================");

		if (args.length < 3) {
			System.out.println("Usage: [csv dir] [csv header] [dest csv file]");
			return;
		}

		File csvpath = new File(args[0]);
		System.out.println("csv path: " + csvpath);

		Path csvheaderpath = new Path(args[1]);
		System.out.println("csv header path: " + csvheaderpath.toPortableString());

		File outpath = new File(args[2]);
		System.out.println("out path: " + outpath);

		try {
			CSVMerge merger = new CSVMerge(csvpath, csvheaderpath, outpath);
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal argument(s) given: " + e.getMessage());
		}
	}

	/**
	 * @return the dir
	 */
	public File getDir() {
		return dir;
	}
}
