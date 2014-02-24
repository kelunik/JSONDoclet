package com.kelunik.java.doclet.json;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;

/**
 * Saves infos from javadoc to a json file.
 * 
 * @author Niklas Keller <me@kelunik.com>
 */
public class DocletJSON extends Doclet {
	/**
	 * called by javadoc
	 * 
	 * @param root
	 * @return <code>true</code> for success, <code>false</code> for failure
	 */
	public static boolean start(RootDoc rootDoc) {
		JSONArray data = new JSONArray();
		
		for(ClassDoc classDoc : rootDoc.classes()) {
			data.put(doClass(classDoc));
		}
		
		System.out.println(data.toString(2));
		
		return true;
	}
	
	private static JSONObject doClass(ClassDoc doc) {
		JSONObject data = new JSONObject();
		
		data.put("package", doc.containingPackage().name());
		data.put("name", doc.name());
		data.put("modifiers", doc.modifiers());
		data.put("superclass", doc.superclass().name());
		data.put("fields", doFields(doc.fields()));
		data.put("constructors", doConstructors(doc.constructors()));
		data.put("methods", doMethods(doc.methods()));
		
		return data;
	}

	private static JSONArray doConstructors(ConstructorDoc[] constructors) {
		JSONArray data = new JSONArray();
		
		for(ConstructorDoc doc : constructors) {
			JSONObject constructor = new JSONObject();
			
			constructor.put("name", doc.name());
			constructor.put("modifiers", doc.modifiers());
			constructor.put("comment", doc.commentText());
			constructor.put("params", doParameters(doc.parameters()));
			
			data.put(constructor);
		}
		
		return data;
	}
	
	private static JSONArray doMethods(MethodDoc[] methods) {
		JSONArray data = new JSONArray();
		
		for(MethodDoc doc : methods) {
			JSONObject method = new JSONObject();
			
			method.put("name", doc.name());
			method.put("modifiers", doc.modifiers());
			method.put("comment", doc.commentText());
			method.put("params", doParameters(doc.parameters()));
			
			data.put(method);
		}
		
		return data;
	}
	
	private static JSONArray doFields(FieldDoc[] fields) {
		JSONArray data = new JSONArray();
		
		for(FieldDoc doc : fields) {
			JSONObject field = new JSONObject();
			
			field.put("name", doc.name());
			field.put("modifiers", doc.modifiers());
			field.put("comment", doc.commentText());
			
			data.put(field);
		}
		
		return data;
	}

	private static JSONArray doParameters(Parameter[] params) {
		JSONArray data = new JSONArray();
		
		for(Parameter p : params) {
			JSONObject param = new JSONObject();
			
			param.put("type", p.type());
			param.put("typeName", p.typeName());
			param.put("name", p.name());
			
			// TODO add comment text
			
			data.put(param);
		}
		
		return data;
	}
}