import java.util.ArrayList;
import java.util.Date;

public class Branch {
	
	String	name;
	Date	startingDate;
	Date	lastmodDate;
	ArrayList<Commit> commits;
	ArrayList<Author> contributors;
	
	
	public Branch(){
		
	}
	public Branch(String name, Date sDate, Date lmDate, ArrayList<Commit> coms){
		this.name = name;
		this.startingDate = sDate;
		this.lastmodDate = lmDate;
		this.commits = coms;
	}
	
	public void print(){
		System.out.println("Name " + this.name + " Date created " + this.startingDate + 
				" Last Date Modified " + this.lastmodDate);
		for ( Commit com: this.commits){
			com.print();
		}
	}
	public void setBranchName(String name){
		this.name = name;
	} 

	public void setBranchContributors(ArrayList<Author> contributors){
		this.contributors = contributors;
	}

	public String getBranchName(){
		return this.name;
	}

	public ArrayList<Author> getBranchContributors(){
		return this.contributors;
	}
	public Integer getBranchCommitsCount(){
		Integer count = 0;		
		
		for ( Integer i = 0 ; i < contributors.size() ; i++)
			count += contributors.get(i).getCommitCount();
		return count;
	}
	
	
}
