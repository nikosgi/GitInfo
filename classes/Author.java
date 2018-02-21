
public class Author {
	
	String name;
	
	Integer linesAdded;
	Integer linesDeleted;
	Integer linesModified;
	
	Integer commitsCount;
	Integer dayCommits;
	Integer weekCommits;
	Integer monthCommits;
	
	public Author(String name, Integer lA, Integer lD){
		this.name = name;
		this.linesAdded = lA;
		this.linesDeleted = lD;
	}
	public Author(String name){
		this.name = name;
		this.linesAdded= 0;
		this.linesDeleted=0;
		this.linesModified=0;
	}
	public Author(){
		
	}
	
	public void setContributorName(String name){
		this.name = name;
	}

	public String getContributorName(){
		return this.name;
	}

	public void setCommitCount(Integer count){
		this.commitsCount = count;
	}

	public Integer getCommitCount(){
		return this.commitsCount;
	}

	public void setDayCommits(Integer dayCommits){
		this.dayCommits = dayCommits;
	}

	public void setWeekCommits(Integer weekCommits){
		this.weekCommits = weekCommits;
	}

	public void setMonthCommits(Integer monthCommits){
		this.monthCommits = monthCommits;
	}

	public Integer getDayCommits(){
		return this.dayCommits;
	}

	public Integer getWeekCommits(){
		return this.weekCommits;
	}

	public Integer getMonthCommits(){
		return this.monthCommits;
	}
	
	public void print(){
		System.out.println("Author name: " + this.name + " Lines Added (+)" + this.linesAdded + " Lines Deleted (-)" + this.linesDeleted + " Lines Modified (+-)" + this.linesModified);
		
		
	}
	
}
