import java.util.Date;

import org.eclipse.jgit.lib.ObjectId;

public class Commit {
	
	ObjectId id;
	Date	date;
	String	message;
	String  author;
	String	tag;
	
	public Commit(ObjectId id, Date date, String message, String author, String tag){
		this.id = id;
		this.date = date;
		this.message = message;
		this.author = author;
		this.tag = tag;
	}
	
	public void print(){
		if (hasTag())
			System.out.println("ID " + this.id.name() + " Date created " + this.date + "Message: " + this.message+
					" Author " + this.author + " --Tag: " + this.tag);
		else
			System.out.println("ID " + this.id.name() + " Date created " + this.date + "Message: " + this.message+
					" Author " + this.author + "Not tag found on this commit");
	}
	
	public Boolean hasTag(){
		if (tag != null)
			return true;
		return false;
	}


}

