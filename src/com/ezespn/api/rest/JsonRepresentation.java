package com.ezespn.api.rest;

import org.json.simple.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

public class JsonRepresentation extends StringRepresentation
{

	public JsonRepresentation(char[] chars) {
		super(chars);
	}

	public JsonRepresentation(CharSequence text, Language language) {
		super(text, language);
	}
	

	public JsonRepresentation(CharSequence text, MediaType mediaType, Language language, CharacterSet characterSet) {
		super(text, mediaType, language, characterSet);
	}

	public JsonRepresentation(CharSequence text, MediaType mediaType, Language language) {
		super(text, mediaType, language);
	}

	public JsonRepresentation(CharSequence text, MediaType mediaType) {
		super(text, mediaType);
	}
	
	public JsonRepresentation(CharSequence text) {
		super(text, MediaType.APPLICATION_JSON);
	}
	
	public JsonRepresentation(JSONObject json) {
		this(json.toJSONString());
		
	}

}
