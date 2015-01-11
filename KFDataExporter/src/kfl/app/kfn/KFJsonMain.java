package kfl.app.kfn;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import kfl.app.kfn.gson.KFAttachment;
import kfl.app.kfn.gson.KFAuthor;
import kfl.app.kfn.gson.KFCommunity;
import kfl.app.kfn.gson.KFContains;
import kfl.app.kfn.gson.KFContribution;
import kfl.app.kfn.gson.KFDrawing;
import kfl.app.kfn.gson.KFJson;
import kfl.app.kfn.gson.KFLink;
import kfl.app.kfn.gson.KFNote;
import kfl.app.kfn.gson.KFObject;
import kfl.app.kfn.gson.KFRecord;
import kfl.app.kfn.gson.KFShape;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

import com.google.gson.Gson;

public class KFJsonMain {

	public static void main(String[] args) throws Exception {
		new KFJsonMain().run();
	}

	void run() throws Exception {
		CDirectory baseDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("kf.out");
		CDirectory dir = baseDir.findOrCreateDirectory("test");
		process(dir);
	}

	private KFJson data;
	private Map<ZID, KFObject> objects = new LinkedHashMap<ZID, KFObject>();
	private Set<String> unsupportedTypes = new HashSet<String>();

	void process(CDirectory dir) throws Exception {
		File metaFile = dir.findOrCreateFile("meta.txt").toJavaFile();
		Properties prop = new Properties();
		prop.load(new FileInputStream(metaFile));
		KFCommunity community = new KFCommunity();
		community.title = prop.getProperty("name");

		data = new KFJson();
		data.community = community;

		KFSerializeFolder folder = new KFSerializeFolder(dir);
		folder.process("objects", new KFTupleProcessor() {
			public void processOne(ZID id, ZTuple tuple) throws Exception {
				processObject(id, tuple.getString("Object"), tuple);
			}
		});
		folder.process("links", new KFTupleProcessor() {
			public void processOne(ZID id, ZTuple tuple) throws Exception {
				processLink(tuple.getString("Link"), tuple);
			}
		});

		File jsonFile = dir.findOrCreateFile("data.json").toJavaFile();
		Gson gson = new Gson();
		String json = gson.toJson(data);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				jsonFile), "UTF8");
		osw.write(json);
		osw.close();
	}

	@SuppressWarnings("unchecked")
	private void processObject(ZID id, String type, ZTuple t)
			throws Exception {
		KFContribution contribution;
		if (type.equals("author")) {
			// System.out.println(t.toString());
			KFAuthor author = new KFAuthor();
			author._id = id.toString();
			author.email = t.getString("unam");
			author.name = t.getString("fnam") + " " + t.getString("lnam");
			author.hashedPassword = t.getString("pass");
			author.role = t.getString("type");
			author.type = "Author";
			data.authors.add(author);
			objects.put(id, author);
			return;
		} else if (type.equals("note")) {
			// System.out.println(t.toString());
			KFNote note = new KFNote();
			contribution = note;
			note.type = "Note";
			note._id = id.toString();
			note.title = t.getString("titl");
			note.body = t.getString("text");
			data.contributions.add(note);
		} else if (type.equals("view")) {
			contribution = new KFContribution();
			contribution.type = "View";
			contribution.title = t.getString("titl");
		} else if (type.equals("scaffold")) {
			contribution = new KFContribution();
			contribution.type = "Scaffold";
			contribution.title = t.getString("text");
		} else if (type.equals("support")) {
			contribution = new KFContribution();
			contribution.type = "Support";
			contribution.title = t.getString("text");
		} else if (type.equals("drawing")) {
			// System.out.println(t.toString());
			contribution = new KFDrawing();
			contribution.type = "Drawing";
			contribution.title = t.getString("titl") /* + id */;
			// if (t.has("preview")) {
			// byte[] preview = t.getRaw("preview");
			// save(preview, id+"preview.png");
			// }
			// if (t.has("thumbnail")) {
			// byte[] thumbnail = t.getRaw("thumbnail");
			// save(thumbnail, id+"thumbnail.png");
			// }
		} else if (type.equals("attachment")) {
			// System.out.println(t.keySet());
			// { Object = "attachment"; crea = date(1.251138324512E9)/* Mon Aug
			// 24 14:25:24 EDT 2009 */; file = "Picture 1.png"; mime =
			// "image/png"; modi = date(1.251138324512E9)/* Mon Aug 24 14:25:24
			// EDT 2009 */; stat = "active"; titl = "Picture 1.png";
			// userFileName = "Picture 1.png"; }
			KFAttachment attachment = new KFAttachment();
			contribution = attachment;
			attachment.title = t.getString("titl");
			attachment.mime = t.getString("mime");
			attachment.type = "Attachment";
			attachment.url = t.getString("file");
			attachment.originalName = t.getString("file");

		} else if (type.equals("shape")) {
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175088758E9)/* Tue Apr 22 09:58:08 EDT 2014 */; modi =
			// date(1.398175088758E9)/* Tue Apr 22 09:58:08 EDT 2014 */;
			// pen-width = int32(1); point1 = point(0, 0); point2 = point(22,
			// 129); shape = "line"; stat = "active"; }
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175287848E9)/* Tue Apr 22 10:01:27 EDT 2014 */; modi =
			// date(1.398175287848E9)/* Tue Apr 22 10:01:27 EDT 2014 */;
			// pen-width = int32(1); points = [point(124, 28), point(113, 28),
			// point(104, 28), point(98, 27), point(94, 23), point(92, 19),
			// point(93, 14), point(100, 8), point(113, 3), point(125, 0),
			// point(139, 0), point(150, 2), point(163, 13), point(171, 31),
			// point(172, 48), point(170, 60), point(163, 75), point(155, 87),
			// point(147, 96), point(131, 106), point(117, 109), point(97, 110),
			// point(75, 110), point(53, 107), point(27, 99), point(14, 96),
			// point(8, 94), point(6, 92), point(4, 91), point(2, 91), point(1,
			// 91), point(0, 90)]; shape = "brush"; stat = "active"; }
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175371966E9)/* Tue Apr 22 10:02:51 EDT 2014 */; modi =
			// date(1.398175371966E9)/* Tue Apr 22 10:02:51 EDT 2014 */;
			// pen-width = int32(1); point1 = point(0, 0); point2 = point(103,
			// 101); shape = "rect"; stat = "active"; }
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175373736E9)/* Tue Apr 22 10:02:53 EDT 2014 */; modi =
			// date(1.398175373736E9)/* Tue Apr 22 10:02:53 EDT 2014 */;
			// pen-width = int32(1); point1 = point(0, 0); point2 = point(164,
			// 115); shape = "oval"; stat = "active"; }
			// System.out.println(t.toString());
			KFShape shape = new KFShape();
			contribution = shape;
			shape.type = "Shape";
			shape.shapeType = t.getString("shape");

			if (t.has("color")) {
				shape.color = parseColor(t.getInt32("color"));
			}
			if (t.has("fill")) {
				shape.fill = parseColor(t.getInt32("fill"));
			}

			if (shape.shapeType.equals("brush")) {
				shape.points = t.getList("points");
			} else {// line, rect, or oval
				shape.point1 = t.getPoint("point1");
				shape.point2 = t.getPoint("point2");
			}
			shape.penWidth = t.getInt32("pen-width");
		} else if (type.equals("text")) {
			// System.out.println(t.toString());
			// pending
			return;
		} else if (type.equals("historicalNote")) {
			// pending
			return;
		} else if (type.equals("backpack")) {
			// System.out.println(t.toString());
			contribution = new KFContribution();
			contribution.type = "View";
			ZID authorId = t.getZID("author");
			Object authorObj = objects.get(authorId);
			if (authorObj == null) {
				System.out.println("author is null for backpack");
				return;
			}
			KFAuthor author = (KFAuthor) authorObj;
			contribution.authors.add(author._id);
			contribution.title = "BackPack: " + author.name;
		} else if (type.equals("session")) {
			return;
		} else {
			warnIfNotShowBefore("unsupported object: ", type, t);
			contribution = new KFContribution();
			contribution.type = type;
		}
		contribution._id = id.toString();
		contribution.created = t.getTime("crea");

		if (!contribution.type.equals("Shape")) {
			data.contributions.add(contribution);
		}
		objects.put(id, contribution);
	}

	private void processLink(String type, ZTuple t) {
		if (type.equals("predates")) {
			return;
		}
		KFObject to = objects.get(t.getZID("to"));
		KFObject from = objects.get(t.getZID("from"));
		if (to == null || from == null) {
			System.out.println("missing link: type=" + type + ", from=" + from
					+ ", to=" + to);
			return;
		}
		KFLink link = new KFLink();
		link.type = type;
		link.from = t.getZID("from").toString();
		link.to = t.getZID("to").toString();
		if (type.equals("contains") && from.type.equals("View")) {
			// System.out.println(t.toString());
			if (to.type.equals("Shape")) {// Direct Shape will be converted to
											// Drawing
				KFShape shape = (KFShape) to;
				KFDrawing drawing = new KFDrawing();
				drawing._id = to._id;
				drawing.title = "FromShape:" + to._id;
				drawing.type = "Drawing";
				drawing.addShape(shape, new Point(0, 0));
				data.contributions.add(drawing);
				objects.put(t.getZID("to"), drawing);
				to = drawing;
			}
			link.type = "onviewref";
			KFContains contains = new KFContains();
			Point p = t.getPoint("location");
			contains.x = p.x;
			contains.y = p.y;
			contains.z = t.getDouble("z");
			if (t.has("mode") && t.getString("mode").equals("in place")) {
				contains.showInPlace = true;
			} else if (!t.has("mode") && to.type.equals("Drawing")) {
				contains.showInPlace = true;
			}
			if (t.has("locked")) {
				contains.fixed = t.getBool("locked");
			}
			link.data = contains;
		} else if (type.equals("contains") && from.type.equals("Scaffold")) {
			// do nothing
		} else if (type.equals("contains") && from.type.equals("Drawing")
				&& to.type.equals("Shape")) {
			// System.out.println(t.toString());
			KFDrawing drawing = (KFDrawing) from;
			KFShape shape = (KFShape) to;
			Point location = t.getPoint("location");
			drawing.addShape(shape, location);
			return;
		} else if (type.equals("owns") && from.type.equals("Author")) {
			KFAuthor author = (KFAuthor) from;
			KFContribution contribution = (KFContribution) to;
			contribution.authors.add(author._id);
			return;
		} else if ((type.equals("read") || type.equals("modified") || type
				.equals("created")) && from.type.equals("Author")) {
			KFRecord record = new KFRecord();
			KFAuthor author = (KFAuthor) from;
			KFContribution contribution = (KFContribution) to;
			record.authorId = author._id;
			record.targetId = contribution._id;
			record.type = type;
			if (type.equals("modified")) {
				record.type = "edit";
			}
			if (type.equals("created")) {
				record.type = "create";
			}
			record.timestamp = t.getTime("crea");
			data.records.add(record);
			return;
		} else if (type.equals("buildson")) {
			// not necessary to do
		} else if (type.equals("references")) {
			// not necessary to do
		} else if (type.equals("supports")) {
			// support to note
			// pending
		} else if (type.equals("describes")) {
			// keyword to note
			// pending
			return;
		} else {
			String concreteType = type + "From:" + from.type + "To:" + to.type;
			warnIfNotShowBefore("unsupported link type: ", concreteType, t);
		}
		data.links.add(link);
	}

	private Color parseColor(int color) {
		int b = (color & 0x000000FF);// 0
		int g = ((color >> 8) & 0x000000FF);// 0
		int r = ((color >> 16) & 0x000000FF);// 0
		int a = ((color >> 24) & 0x000000FF);// 255
		return new Color(r, g, b, a);
	}

	@SuppressWarnings("unused")
	private void save(byte[] data, String name) throws Exception {
		FileOutputStream os = new FileOutputStream(name, false);
		os.write(data);
		os.close();
	}

	private void warnIfNotShowBefore(String msg, String type, ZTuple t) {
		if (unsupportedTypes.contains(type)) {
			return;
		}
		unsupportedTypes.add(type);
		System.out.println(msg + type);
		System.out.println(t.toString());
	}
}
