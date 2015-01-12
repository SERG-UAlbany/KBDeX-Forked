/*
 * KFGroup.java
 * Created on Jul 21, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.csvexporter.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class KFGroup extends KFOwnerObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<KFAuthor> members = new ArrayList<KFAuthor>();

	@Override
	public String getType() {
		return "group";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the authors
	 */
	public List<KFAuthor> getMembers() {
		return members;
	}

	/**
	 * @param members
	 *            the authors to set
	 */
	public void addMember(KFAuthor author) {
		this.members.add(author);
	}

	@Override
	public String toString() {
		return "((group)" + getName() + ")";
	}

	@Override
	public String listToString(String name, List<? extends KFElement> list) {
		StringBuffer buf = new StringBuffer();
		buf.append("{'" + name + "':[");
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				buf.append(", ");
			}
			buf.append("{'id':'" + list.get(i).getIdAsString());
			buf.append("', 'username':'" + list.get(i).getShortDescrption()
					+ "'}");
		}
		buf.append("]}");
		return buf.toString();
	}

	public static List<String> header() {
		return Arrays.asList("id", "name", "members");
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		strings.add(getIdAsString());
		strings.add(getName());
		strings.add(listToString("members", members));
		return strings;
	}

}
