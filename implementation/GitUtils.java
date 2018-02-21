/*git related class*/

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;


public class GitUtils
{
	String git_lsTree = "git ls-tree -r";
	String git_lsFiles = "git ls-files";
	String cat_command = "cat";
	String git_commit_count = "git rev-list --all --count";
	String git_contributors_commits = "git shortlog -s -n --all";
	String git_contributors_commits_in_branch = "git shortlog -s -n";
	String git_branch = "git branch";
	String git_braches_commits = "git rev-list --count";
	String git_commits_day = "git shortlog -s --since='1\\sday' --branches='*' ";
	String git_commits_week = "git shortlog";
	String git_commits_month = "git shortlog -s --since='1\\smonth' --branches='*' ";


	String repositoryPath = "";	

	ArrayList<Author> Author;


	public GitUtils(String repoPath){
		this.repositoryPath = repoPath;
	}


public String getFilesCount(ArrayList<Branch> branches){
		
		String commandOutput;
		String preparedCommand = git_lsTree; 
		int countFiles = 0;
		
		//String commandOutput = CommandLine.executeCommand(preparedCommand,repositoryPath);
		
		//find out count of lines in commandOutput (count of lines in output equals to files count)
		//because 
	
	
		for ( Branch branch : branches ){
			preparedCommand = git_lsTree;
			preparedCommand = preparedCommand + " " +branch.getBranchName() + " " +"--name-only";
						
			commandOutput = CommandLine.executeCommand(preparedCommand,repositoryPath);

			String[] lines = commandOutput.split("\r\n|\r|\n");
			countFiles += lines.length;		
		
		}
		return String.valueOf(countFiles);
	} 

	public Integer getLinesCount(){

		String commandOutput;
		Integer linesCount = 0;

		String command = git_lsFiles;

		commandOutput = CommandLine.executeCommandAlt(command,repositoryPath);

		String[] files = commandOutput.split("\r\n|\n|\r");
			
		for ( String file : files) {
			command = cat_command + " " +file;
			String commandOutput2 = CommandLine.executeCommandAlt(command,repositoryPath.replace("/.git","/"));
			String[] lines = commandOutput2.split("\r\n|\n|\r");
			System.out.println(linesCount);
			linesCount += lines.length;
		}		
		
		return linesCount;
	}

	public String getCommitCount(){
		String commandOutput;
		Integer commitsCount = 0;
		String command;
	
		command = git_commit_count ;			

		commandOutput = CommandLine.executeCommand(command,repositoryPath);
		
		return commandOutput;
	}

	public ArrayList<Author> getContributorsCommits(){
		String commandOutput;
		String command;
		ArrayList<Author> contributors;
		String[] infos;
		Author contributor;
		
		command = git_contributors_commits;
		commandOutput = CommandLine.executeCommand(command,repositoryPath);

		contributors = getContributorsArrayList(commandOutput);
				
		return contributors;				
						
	}

	private ArrayList<Author> getContributorsArrayList(String commandOutput){
                String[] lines = commandOutput.split("\n\r|\n|\r");
		String[] infos;
                ArrayList<Author> contributors = new ArrayList<Author>();
		Author contributor;

                for ( String line : lines){

                        infos = line.split("\t");
                        contributor = new Author();

                        Integer commitsCount = Integer.parseInt(infos[0].replaceAll("\\s+",""));
                        String contributorName = infos[1];

                        contributor.setCommitCount( commitsCount );
                        contributor.setContributorName( contributorName);

                        contributors.add(contributor);
                }
		return contributors;


	}


	public ArrayList<Branch> getBranchesCommits(){
		String commandOutput;
		String command;
		ArrayList<Branch> branches = new ArrayList<Branch>();
		String[] infos;
		ArrayList<Author> contributors ;
		String branch_name = "";

		command = git_branch;
		commandOutput = CommandLine.executeCommand(command,repositoryPath);

		String[] branchNames = commandOutput.split("\n\r|\n|\r");
		
		for ( String branchName : branchNames ) {
			Branch branch = new Branch();
			
			String pattern = "(\\s\\W*)([a-zA-Z\\s_1-9]*)";
			Pattern r = Pattern.compile(pattern);			
			Matcher m = r.matcher(branchName);
			
			if ( m.find() )
				branch_name = m.group(2);

			branch.setBranchName(branch_name);
					
			command = git_contributors_commits_in_branch+ " " + branch_name;
			commandOutput = CommandLine.executeCommand(command,repositoryPath);
			
			contributors = getContributorsArrayList(commandOutput);
			branch.setBranchContributors(contributors);
			
			branches.add(branch);
		}
		return branches;
	}
	
	public static Repository openRepository(String path) throws IOException {
		
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setGitDir(new File(path));
        //builder.setGitDir(new File("/petetpant1/software_technology"));
        	
		return builder
                 .readEnvironment() // scan environment GIT_* variables
	             .findGitDir() // scan up the file system tree
	             .build();
		
	}
	public String getBranchesTagsCommiters(String path) throws IOException, GitAPIException{
		ArrayList<Author> authors = new ArrayList<Author>();
		String html = "<table style=\"text-allign: center; border-spacing: 10px;\">"
				+ "<caption> Number of Branches, Tags, Commiters</caption>" 
				+ "<tr><th># of Branches</th><th># of Tags</th><th># of Commiters</th></tr>";
		try (Repository rep = openRepository(path)) {
			
			try (Git git = new Git(rep)) {
				int count = 0;
				System.out.println("Now including branches:");
				List<Ref> call_branches = git.branchList().setListMode(ListMode.REMOTE).call();
                for (Ref ref : call_branches) {
                	count++;
                    System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
                }
                html += "<tr><td>"+count+"</td>";
                count = 0;
                System.out.println("Now including tags:");
                List<Ref> call_tags = git.tagList().call();
                for (Ref ref : call_tags) {
                	count++;
                    System.out.println("Tag: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
                }
                
                html += "<td>"+count+"</td>";
                count = 0;
                System.out.println("Now including commiters:");
                Iterable<RevCommit> commits = git.log().all().call();
                
                ArrayList<String> commitAuthors = new ArrayList<String>() ;
                for (RevCommit commit : commits) {
                	
                	commitAuthors.add(commit.getAuthorIdent().getName());
                	
                }
                Set<String> uniqueAuthors = new HashSet<String>(commitAuthors);
                for (String auth : uniqueAuthors){
                	Author newAuthor = new Author(auth);
                	authors.add(newAuthor);
                    System.out.println("Commiter : " + auth);
                    count++;
                }
                html += "<td>"+count+"</td></tr></table><br><br>";
                System.out.println(count + " Commiters");
                
                
			}catch(GitAPIException e){
            	System.err.println("Can't get branch list");
            }	
		}catch(IOException e){
			System.err.println("Cant's open git repository");
		}
		return html;
        
	}

	
	public ArrayList<Branch> getBranchesDates(String path) throws IOException, GitAPIException{
		Boolean first = false;
		Date dateFirst = null, dateLast= null;
		ArrayList<Branch> branches = new ArrayList<Branch>();
		try(Repository rep = openRepository(path)){
	    	try(Git git = new Git(rep)){
	    		
	    		List<Ref> br = git.branchList().setListMode(ListMode.REMOTE).call();
	    		
	    		for (Ref branch : br) {
	    			String branchName = branch.getName();
	    			
	    			System.out.println("Commits of branch: " + branchName);
	    			System.out.println("-------------------------------------");
	    			
	    			Iterable<RevCommit> commits = git.log().add(rep.resolve(branchName)).call();
	    			Iterator<RevCommit> iterator = commits.iterator();
	    		
	    			first = true;
	    			while(iterator.hasNext()){
	    				RevCommit commit = iterator.next();
	    				
	    				if(first){
	    					dateLast = new Date(commit.getCommitTime() * 1000L);
	    					first = false;
	    				}else if(!iterator.hasNext()){
	    					dateFirst = new Date(commit.getCommitTime() * 1000L);
	    					
	    	        	}
	    			}
	    			ArrayList<Commit> comList = new ArrayList<Commit>();
	    			comList = this.getCommitsOfBranch(path, branchName);
	    			
	    			branches.add( new Branch(branchName,dateFirst,dateLast,comList));
	    		}
	    	
	    	}catch(GitAPIException e){
	    		System.err.println("Can't get branch list");
	    	}	
		}catch(IOException e){
			System.err.println("Cant's open git repository");
		}
	    return branches;
	}	    	
	public ArrayList<Commit> getCommitsOfBranch(String path, String branchName){
		
		
		ArrayList<Commit> comList= new ArrayList<Commit>();
		
		try(Repository rep = openRepository(path)){
	    	try(Git git = new Git(rep)){
	    			    			
	    		Iterable<RevCommit> commits = git.log().add(rep.resolve(branchName)).call();
	    		
    			for (RevCommit commit : commits){
    				comList.add(new Commit(commit.getId(),new Date(commit.getCommitTime() * 1000L),
    						commit.getShortMessage(), commit.getAuthorIdent().getName(), getCommitTag(path,commit)));
    				
    			}	    	
	    	}catch(GitAPIException e){
	    		System.err.println("Can't get branch list");
	    	}	
		}catch(IOException e){
			System.err.println("Cant's open git repository");
		}
		
		return comList;
	}
	
	public String getCommitTag(String path, RevCommit commit){
		
		try(Repository rep = openRepository(path)){
			try(Git git = new Git(rep)){
	    			    			
	    		List<Ref> tags = git.tagList().call();
	    		for (Ref ref : tags) {
	    		    System.out.println("Tag: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
	    		
	    		    LogCommand log = git.log();
	    		    
	    		    Ref peeledRef = rep.peel(ref);
	    		    if(peeledRef.getPeeledObjectId() != null) {
	    		    	log.add(peeledRef.getPeeledObjectId());
	    		    } else {
	    		    	log.add(ref.getObjectId());
	    		    }
	    		    Iterable<RevCommit> logs = log.call();
	    	        for (RevCommit rev : logs) {
	    	        	if (rev.equals(commit))
	    	        		return ref.getName();
	    	            
	    	        }	    		    
	    		}
	    	}catch(GitAPIException e){
	    		System.err.println("Can't get branch list");
	    	}	
		}catch(IOException e){
			System.err.println("Cant's open git repository");
		}
		return null;
	}
	
	public ArrayList<Author> getAuthorLines(String path, ArrayList<Author> authors) throws IOException, GitAPIException{
		RevCommit previous;
		try (Repository rep = openRepository(path)) {
			try(Git git = new Git(rep)){


				RevWalk walk = new RevWalk(rep);
				Iterable<RevCommit> logs = git.log().all().call();
				Iterator<RevCommit> i = logs.iterator();
				
				previous = i.next();
				
				Integer count =0;
				Integer parents=1;
				
				while (i.hasNext()) {
					count++;
					
				    RevCommit commit = walk.parseCommit( previous);
				    RevCommit commit2 = walk.parseCommit( commit.getParent(parents-1));
				    
				    previous = i.next();
				    
				    
				    System.out.println( commit.getFullMessage() );

					


		            System.out.println("Printing diff between tree: " + commit.getName() + " and " + commit2.getName());

		            ObjectReader reader = rep.newObjectReader();
		            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		            oldTreeIter.reset(reader, commit.getTree());
		            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		            newTreeIter.reset(reader, commit2.getTree());
		            
		            DiffFormatter diffForm = new DiffFormatter( System.out );
		            diffForm.setRepository( git.getRepository() );
		            diffForm.setContext( 0 );

		            
		            List<DiffEntry> diffs= diffForm.scan( newTreeIter, oldTreeIter );
		            for( DiffEntry entry : diffs ) {
		            	  FileHeader fileHeader = diffForm.toFileHeader( entry );
		            	  EditList infoList = fileHeader.toEditList();
		            	  authors = updateAuthorLines(infoList,authors,commit);
		            }
		            
		            diffForm.close();
		            
				}
				walk.close();
				return authors;
			}
			
		}
		
	}
    public ArrayList<Author> updateAuthorLines(EditList infoList, ArrayList<Author> authors, RevCommit commit){
    	
    	Author commitAuthor;
    	
    	System.out.println(commit.getAuthorIdent().getName());
    	Integer authorIdx = 0;
    	for (Author auth : authors){
    		if (auth.name.equals(commit.getAuthorIdent().getName()))
    			break;
    		authorIdx ++;
    		
    	}
    	
    	if (authorIdx != -1){
    		commitAuthor = authors.get(authorIdx);
    	}else{
    		System.out.println("Can't find specific author");
    		return authors;
    	}
    	
    	for (Edit edit : infoList){
    		if (edit.getBeginA() == edit.getEndA() && edit.getBeginB() < edit.getEndB()){
    			//This is an insert edit
    			commitAuthor.linesAdded += (edit.getEndB() - edit.getBeginB());	
    			
    			
    		}else if (edit.getBeginA() < edit.getEndA() && edit.getBeginB() == edit.getEndB()){
    			//This is a delete edit
    			commitAuthor.linesDeleted += (edit.getEndA() - edit.getBeginA());
    		}else if (edit.getBeginA() < edit.getEndA() && edit.getBeginB() < edit.getEndB()){
    			//This is a modify edit
    			commitAuthor.linesModified += (edit.getEndB() - edit.getBeginB());
    			commitAuthor.linesDeleted += (edit.getEndA() - edit.getBeginA());
    			commitAuthor.linesAdded += (edit.getEndB() - edit.getBeginB());
    			
    			
    		}
    		    		
    	}
    	authors.set(authorIdx, commitAuthor);
    	return authors;
    	
    }
    public float findAuthorMO(ArrayList<Author> authors,Integer choice){
    	Integer sum =0;
    	for ( Author auth: authors){
    		switch (choice){
    		
    		case 1: sum+= auth.linesAdded;break;
    		case 2: sum+= auth.linesDeleted;break;
    		case 3: sum+= auth.linesModified;break;
    			
    		}
    		
    	}
    	return sum/ authors.size();
    }
    
    public ArrayList<Author> getAuthors(String path){
    	ArrayList<Author> authors = new ArrayList<Author>();
    	try (Repository rep = openRepository(path)) {
    		
			try (Git git = new Git(rep)) {
              
                Iterable<RevCommit> commits = git.log().all().call();
                int count = 0;
                ArrayList<String> commitAuthors = new ArrayList<String>() ;
                for (RevCommit commit : commits) {
                	
                	commitAuthors.add(commit.getAuthorIdent().getName());
                	
                }
                Set<String> uniqueAuthors = new HashSet<String>(commitAuthors);
                for (String auth : uniqueAuthors){
                	Author newAuthor = new Author(auth);
                	authors.add(newAuthor);
                    System.out.println("Commiter : " + auth);
                    count++;
                }
                System.out.println(count + " Commiters");
                
                
			}catch(GitAPIException e){
            	System.err.println("Can't get branch list");
            }	
		}catch(IOException e){
			System.err.println("Cant's open git repository");
		}
		return authors;
    }
    
    public Integer countFiles(String path) throws IOException, GitAPIException{
        Integer count=0;
        try (Repository rep = openRepository(path)) {
    		
        	try (Git git = new Git(rep)) {
              
				Ref head = rep.getRef("HEAD");

        // a RevWalk allows to walk over commits based on some filtering that is defined
				RevWalk walk = new RevWalk(rep);

				RevCommit commit = walk.parseCommit(head.getObjectId());
				RevTree tree = commit.getTree();
				
        // 	now use a TreeWalk to iterate over all files in the Tree recursively
        // 	you can set Filters to narrow down the results if needed
				TreeWalk treeWalk = new TreeWalk(rep);
				treeWalk.addTree(tree);
				treeWalk.setRecursive(false);
				while (treeWalk.next()) {
					count ++ ;
					if (treeWalk.isSubtree()) {
				        System.out.println("dir: " + treeWalk.getPathString());
				        treeWalk.enterSubtree();
				    } else {
				        System.out.println("file: " + treeWalk.getPathString());
				    }
					
				    
				}
				treeWalk.close();
			}
        }
		return count;
	}
    public Integer countLines(String path) throws IOException, GitAPIException{
        Integer count=0;
        try (Repository rep = openRepository(path)) {
    		
        	try (Git git = new Git(rep)) {
              
				Ref head = rep.getRef("HEAD");
				RevWalk walk = new RevWalk(rep);

				RevCommit commit = walk.parseCommit(head.getObjectId());
				RevTree tree = commit.getTree();
				
				TreeWalk treeWalk = new TreeWalk(rep);
				treeWalk.addTree(tree);
				treeWalk.setRecursive(false);
				while (treeWalk.next()) {
					count ++ ;
					if (treeWalk.isSubtree()) {
				        System.out.println("dir: " + treeWalk.getPathString());
				        treeWalk.enterSubtree();
				    } else {
				        System.out.println("file: " + treeWalk.getPathString());
				    }
					BlameResult result = git.blame().setFilePath(treeWalk.getPathString()).call();
        			RawText rawText = result.getResultContents();
        			count += rawText.size();				    
				}
				treeWalk.close();
			}
        }
        System.out.println(count);
		return count;
	}

    public String getCommitStatistics(String path){
		
    	String html = "<table style=\"text-allign: center; border-spacing: 10px;\">"
    			+ "<caption> Average Commits</caption>"
    			+ "+<tr><th>Average Commits / Day</th><th>Average Commits / Week</th><th>Average Commits / Month</th></tr>";
		
		try(Repository rep = openRepository(path)){
	    	try(Git git = new Git(rep)){
	    			    			
	    		RevCommit youngestCommit = null;    
	    		List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();
	    		try(RevWalk walk = new RevWalk(git.getRepository())) {
	    		    for(Ref branch : branches) {
	    		        RevCommit commit = walk.parseCommit(branch.getObjectId());
	    		        if (youngestCommit == null){
	    		        	youngestCommit = commit;
	    		        }else{
	    		        	if(commit.getAuthorIdent().getWhen().compareTo(youngestCommit.getAuthorIdent().getWhen()) > 0)
	    		        		youngestCommit = commit;
	    		        }
	    		    }
	    		}
	    		RevCommit eldestCommit = null; 
	    		Iterable<RevCommit> commits = git.log().call();
	    		
	    		Integer commitCounter = 0;
    			for (RevCommit commit : commits){
    				commitCounter ++;
    				if (youngestCommit == null){
	    		        eldestCommit = commit;
	    		    }else{
	    		    	if(commit.getAuthorIdent().getWhen().compareTo(youngestCommit.getAuthorIdent().getWhen()) < 0)
	    		    		 eldestCommit = commit;
	    		    }
    			}	    
	    		Long diff = (youngestCommit.getCommitTime() * 1000L) - (eldestCommit.getCommitTime() * 1000L);
	    		Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    			Float averageCommits = ((float)commitCounter/ (float)this.getAuthors(path).size());
    			DecimalFormat formatNumber = new DecimalFormat("#.000");
    			html += "<tr><td>"+ formatNumber.format((float)averageCommits/days) +"%</td><td>"+formatNumber.format((float)averageCommits*7/days)+"%</td><td>"+formatNumber.format((float)averageCommits*30/days)+"%</td></tr>";
    			html += "</table><br><br>";
    			System.out.println(days+"days " + averageCommits);
    			
	    	}catch(GitAPIException e){
	    		System.err.println("Can't get branch list");
	    	}	
		}catch(IOException e){
			System.err.println("Cant's open git repository");
		}
		return html;
		
		
	}


}



