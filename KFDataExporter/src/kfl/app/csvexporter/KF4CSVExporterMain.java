package kfl.app.csvexporter;

import kfl.kf4.serializer.KFSerializeFolder;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

public class KF4CSVExporterMain {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Args length has to be 1.");
			return;
		}
		new KF4CSVExporterMain().run(args[0]);
	}

	void run(String name) throws Exception {
		CDirectory baseDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("kf.out");
		CDirectory dir = baseDir.findDirectory(name);
		if (dir == null) {
			System.out.println("database " + name + " is not found.");
			return;
		}
		process(dir);
	}

	void process(CDirectory dir) throws Exception {
		KFSerializeFolder folder = new KFSerializeFolder(dir);
		folder.loadMeta();
		KFDataBuilder builder = new KFDataBuilder();
		builder.build(folder);
		KFCSVExporter exporter = new KFCSVExporter();
		exporter.export(dir, builder.getWorld());
	}
}
