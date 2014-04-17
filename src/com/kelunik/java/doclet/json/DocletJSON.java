package com.kelunik.java.doclet.json;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

/**
 * Saves infos from javadoc to a json file.
 * 
 * @author Niklas Keller <me@kelunik.com>
 */
public class DocletJSON extends Doclet {
	public static void main(String[] args) {
		System.out.println("DocletJSON v1.0");
	}

	/**
	 * called by javadoc
	 * 
	 * @param root
	 * @return <code>true</code> for success, <code>false</code> for failure
	 */
	public static boolean start(RootDoc rootDoc) {
		JSONArray data = new JSONArray();

		for (ClassDoc classDoc : rootDoc.classes()) {
			data.put(doClass(classDoc));
		}

		String s = data.toString(0);

		try {
			Files.write(Paths.get(new File("output.json").toURI()),
					s.getBytes("utf-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static JSONObject doClass(ClassDoc doc) {
		JSONObject data = new JSONObject();

		data.put("package", doc.containingPackage().name());
		data.put("name", doc.name());
		data.put("modifiers", doc.modifiers());
		data.put("comment", doc.commentText());
		data.put("superclass", doc.superclass() == null ? null : doc
				.superclass().qualifiedName());
		data.put("interfaces", doInterfaces(doc.interfaces()));
		data.put("fields", doFields(doc.fields()));
		data.put("constructors", doConstructors(doc.constructors()));
		data.put("methods", doMethods(doc.methods()));
		data.put("type", getClassType(doc));

		return data;
	}
	
	private static JSONArray doInterfaces(ClassDoc[] docs) {
		JSONArray data = new JSONArray();

		for (ClassDoc doc : docs) {
			data.put(doc.qualifiedName());
		}

		return data;
	}
	
	private static String getClassType(ClassDoc doc) {
		if(doc.isClass()) {
			return "class";
		}
		
		if(doc.isEnum()) {
			return "enum";
		}
		
		if(doc.isInterface()) {
			return "interface";
		}
		
		return "";
	}

	private static JSONArray doConstructors(ConstructorDoc[] constructors) {
		JSONArray data = new JSONArray();

		for (ConstructorDoc doc : constructors) {
			JSONObject constructor = new JSONObject();

			constructor.put("name", doc.name());
			constructor.put("modifiers", doc.modifiers());
			constructor.put("comment", doc.commentText());
			constructor.put("params", doParameters(doc));

			data.put(constructor);
		}

		return data;
	}

	private static JSONArray doMethods(MethodDoc[] methods) {
		JSONArray data = new JSONArray();

		for (MethodDoc doc : methods) {
			JSONObject method = new JSONObject();

			method.put("name", doc.name());
			method.put("modifiers", doc.modifiers());
			method.put("type", doc.returnType().qualifiedTypeName());
			method.put("comment", doc.commentText());
			method.put("params", doParameters(doc));

			Tag[] tags = doc.tags("return");
			
			if (doc.returnType().qualifiedTypeName().equals("void")) {
				method.put("returnComment", "");
			} else if (tags.length == 0) {
				method.put("returnComment", "<i>Fehlender Kommentar!</i>");
			} else {
				method.put("returnComment", tags[0].text());
			}
			
			Tag[] seeTags = doc.tags("see");
			
			for(Tag t : seeTags) {
				t.position();
			}
			
			

			data.put(method);
		}

		return data;
	}

	private static JSONArray doFields(FieldDoc[] fields) {
		JSONArray data = new JSONArray();

		for (FieldDoc doc : fields) {
			JSONObject field = new JSONObject();

			field.put("name", doc.name());
			field.put("modifiers", doc.modifiers());
			field.put("type", doc.type());
			field.put("comment", doc.commentText());

			data.put(field);
		}

		return data;
	}

	private static JSONArray doParameters(ExecutableMemberDoc doc) {
		JSONArray data = new JSONArray();

		ParamTag[] tags = doc.paramTags();
		Map<String, ParamTag> tagMap = new HashMap<>(tags.length);

		for (int i = 0; i < tags.length; i++) {
			if (!tags[i].parameterComment().isEmpty()) {
				tagMap.put(tags[i].parameterName(), tags[i]);
			}
		}

		Parameter[] params = doc.parameters();

		for (Parameter p : params) {
			JSONObject param = new JSONObject();

			param.put("type", p.type());
			param.put("typeName", p.typeName());
			param.put("name", p.name());

			ParamTag tag = tagMap.remove(p.name());

			if (tag == null && !p.name().equals("this$0")) {
				param.put("comment", "<i>Dokumentation fehlt.</i>");
			} else {
				param.put("comment", tag.parameterComment());
			}

			data.put(param);
		}

		return data;
	}
}