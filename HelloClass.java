/* Main class*/

import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;




public class HelloClass
{


    public static void main(String[] args){

            String repoPath;
		    String writePath;
		    Writer writer=null;

		    ArrayList<Branch> branches = new ArrayList<Branch>();
		    ArrayList<Author> authors = new ArrayList<Author>();

            //accept only two arguments  
            if ( args.length != 2 ){
            	System.err.println("you must provide exactly two arguments");
            	System.exit(1);
            }
            repoPath = args[0];
            writePath = args[1];

            System.out.println("Provided path to repository " + repoPath);
            System.out.println("Provided path to writing file " + writePath);
            
            GitUtils gitUtils = new GitUtils(repoPath);
            
            File report_file = new File(writePath);
            String head_html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN" 
            			+ "http://www.w3.org/TR/html4/loose.dtd\">"
            			+ "<html>"
            			+ "<head>"
            			+ "<style>table {font-family: arial, sans-serif;border-collapse: collapse;width: 100%;border: 2px solid black }"
            			+ "td, th {border: 1px solid #dddddd;text-align: left;padding: 8px;}"
            			+ "tr:nth-child(even) {background-color: #6dc7d1;}</style>"
            			+ " <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
            			+ "<title>Report file</title>"
            			+ "</head>" 
            			+ "<body>"
            			+ "<h2>Reporting Directory : "
            			+ repoPath
            			+ "</h2><br>";
            			
        try{

            BufferedWriter bw = new BufferedWriter(new FileWriter(report_file));
			
			bw.write(head_html);
			
			//Count files
			Integer filesCount = gitUtils.countFiles(repoPath);
			String html = "<h4>Files found in the repository    : [ "+filesCount+" ]</h4>";
			//Count lines
			Integer linesCount = gitUtils.countLines(repoPath);
			html += "<h4>Lines found in the repository   : [ "+linesCount+" ]</h4>";
			
			//Commits
			String commitsCount = gitUtils.getCommitCount().replaceAll("\n","");
			html += "<h4>Commits found in the repository : [ "+commitsCount+" ]</h4>";
			

			//Commits per contributor
			ArrayList<Author> contributors = gitUtils.getContributorsCommits();
			html += "<table style=\"text-allign: center; border-spacing: 1px;\">"+
					"<caption> Commit Percentage per Contributor</caption>" +
					"<tr><th>Contributor Name</th><th>Commit Percentage</th></tr>";

			for (Author contributor : contributors){
				System.out.println("Contributor Name = "+contributor.getContributorName() );
				System.out.println("Contributor commit count = " + calculatePercentage(Integer.parseInt(commitsCount),contributor.getCommitCount())+"\n\n");	
				html += "<tr><td>"+contributor.getContributorName()+"</td><td>"+calculatePercentage(Integer.parseInt(commitsCount),contributor.getCommitCount())+"</td></tr>";
			}

			//Commits per branch 
			ArrayList<Branch> bran = gitUtils.getBranchesCommits();
			html += "</table><br><br>";
			html += "<table style=\"text-allign: center; border-spacing: 10px;\">"+
					"<caption> Commit Percentage per Branch</caption>" +
					"<tr><th>Branch Name</th><th>Commit Percentage</th></tr>";

			for ( Branch branch : bran){
				System.out.println("Branch Name ="+ branch.getBranchName());
				System.out.println("Branch whole commit count = " + calculatePercentage(Integer.parseInt(commitsCount),branch.getBranchCommitsCount()));	
				html += "<tr><td>"+branch.getBranchName()+"</td><td>"+calculatePercentage(Integer.parseInt(commitsCount),branch.getBranchCommitsCount())+"</td></tr>";
			}
			html += "</table><br><br>";
			
			html += "<table style=\"text-allign: center; border-spacing: 1px;\">" +
					"<caption> Commit Percentage per Branch per Author</caption>" ;

			System.out.println("\n\n\n");
			
			for ( Branch branch : bran){
				System.out.println(" Branch Name = " + branch.getBranchName()  );
				
				ArrayList<Author> contribs = branch.getBranchContributors();
				Integer commitsInBranch = branch.getBranchCommitsCount();
				html += "<tr><th rowspan=\""+contribs.size()+"\">"+branch.getBranchName()+"</th>";
				for ( Author contr : contribs ) {
					html += "<td>"+contr.getContributorName()+"</td><td>"+calculatePercentage(commitsInBranch,contr.getCommitCount())+"</td></tr><tr>";
					System.out.println("Contributor with name " + contr.getContributorName());
					System.out.println( " Percentage of commits " + calculatePercentage(commitsInBranch,contr.getCommitCount()));
				}
				html += "</tr>";
				System.out.println("\n\n");
							
			} 
			html += "</table><br><br>";
			bw.write(html);
			
			String toHtml = gitUtils.getBranchesTagsCommiters(repoPath);
			bw.write(toHtml);
			
			authors = gitUtils.getAuthors(repoPath);
			
			branches = gitUtils.getBranchesDates(repoPath);
			html = "<table style=\"text-allign: center; border-spacing: 10px;\">"
					+ "<caption> Branches Info</caption>"
					+ "<tr><th>Branches</th><th>Creation Date</th><th>Last Modification Date</th></tr>";
			for (Branch br : branches){
				String name = br.name.replace("refs/remotes/origin/","");
				File branchInfoFile = new File(name+".html");
				BufferedWriter commitWriter = new BufferedWriter(new FileWriter(branchInfoFile));
				commitWriter.write(head_html);
				String commit_html = "<table style=\"text-allign: center; border-spacing: 8px; border: 2px solid black;\"><tr><th>Commit ID</th><th>Date</th><th>Author</th><th>Message</th><th>Tag</th></tr>";
				for(Commit com : br.commits){
					if (com.hasTag())
						commit_html+= "<tr><td>"+com.id.name()+"</td><td>"+com.date+"</td><td>"+com.author+"</td><td>"+com.message+"</td><td>"+com.tag+"</td></tr>";
					else
						commit_html+= "<tr><td>"+com.id.name()+"</td><td>"+com.date+"</td><td>"+com.author+"</td><td>"+com.message+"</td><td>-</td></tr>";
				}
				commitWriter.write(commit_html);
				html += "<tr><td><a href=\" "+name+".html\">"+ name +"</td><td>"+br.startingDate+"</td><td>"+br.lastmodDate+"</td></tr>";
				commitWriter.close();
			}
			html += "</table><br><br>";
			toHtml = gitUtils.getCommitStatistics(repoPath);
			
			html += toHtml;
			html += "<table style=\"text-allign: center; border-spacing: 10px;\">+"
					+ "<caption>Lines per Author</caption>"
					+ "<tr><th>Author</th><th>Lines Added</th><th>Lines Deleted</th><th>Lines Modified</th></tr>";
			authors = gitUtils.getAuthorLines(repoPath, authors);
			
			for (Author author: authors){
				
				html += "<tr><td>"+author.name+"</td><td>"+ author.linesAdded +"</td><td>"+author.linesDeleted+"</td><td>"+author.linesModified+"</td></tr>";
				
			}
			html += "<tr><td>Average</td><td>"+ gitUtils.findAuthorMO(authors,1)+"</td><td>"+gitUtils.findAuthorMO(authors,2)+"</td><td>"+gitUtils.findAuthorMO(authors,3)+"</td></tr></table><br><br>";
		
			
			bw.write(html);
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
        

    }
    private static String calculatePercentage(Integer whole,Integer portion){
		
		double per = (double)portion/(double)whole;
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		per = per*100;
		
		String perToString = String.valueOf(numberFormat.format(per));
		
		return perToString.concat("%");

	}


    
}
