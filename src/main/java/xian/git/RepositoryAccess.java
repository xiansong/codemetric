package xian.git;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.util.FileUtils;

/**
 * The Class RepositoryAccess.
 * 
 * Create a local repository for access.
 */
public class RepositoryAccess {

	public static final int OLD = 0;
	public static final int RENEW = 1;

	/** The Constant rootPath for storing local git repositories. */
	private static final String rootPath = System.getProperty("user.home")
			+ File.separator + "git" + File.separator;

	/** The repository remote url. */
	private String url;

	/** The repository object in this access. */
	private Repository repository;

	/**
	 * Instantiates a new repository access.
	 * 
	 * @throws IOException
	 * @throws GitAPIException
	 * @throws NullPointerException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 */
	public RepositoryAccess(final String url, final int rule)
			throws IOException, InvalidRemoteException, TransportException,
			NullPointerException, GitAPIException {
		this.url = url;
		File gitDir = new File(rootPath + getRepositoryName());
		if (rule == OLD) {
			if (gitDir.exists()) {
				Git git;
				git = Git.open(gitDir);
				repository = git.getRepository();
			} else {
				gitClone();
			}
		} else {
			FileUtils.delete(gitDir, FileUtils.RECURSIVE);
			gitClone();
		}
	}

	/**
	 * Git clone.
	 * 
	 * Clone the remote Repository to local
	 */
	public void gitClone() throws InvalidRemoteException, TransportException,
			NullPointerException, GitAPIException, IOException {
		Git.cloneRepository().setURI(url)
				.setDirectory(new File(rootPath + getRepositoryName())).call();
		File gitDir = new File(rootPath + getRepositoryName());
		Git git;
		git = Git.open(gitDir);
		repository = git.getRepository();
	}

	/**
	 * Construct the repository name and return.
	 * 
	 * @return the repository name
	 * @throws NullPointerException
	 *             the null pointer exception
	 */
	private String getRepositoryName() throws NullPointerException {
		final StringTokenizer st = new StringTokenizer(url, "\\/");
		String name = null;
		while (st.hasMoreTokens()) {
			name = st.nextToken();
		}
		// get the repositoryName from String "repositoryName.git"
		return name.substring(0, name.length() - 4);

	}

	/**
	 * Gets the commits.
	 * 
	 * @return the commits
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<RevCommit> getCommits() throws IOException {
		RevWalk walk = new RevWalk(repository);
		walk.markStart(walk.parseCommit(repository.resolve(Constants.HEAD)));
		List<RevCommit> revCommits = new ArrayList<RevCommit>();
		for (Iterator<RevCommit> itr = walk.iterator(); itr.hasNext();) {
			revCommits.add(itr.next());
		}
		return revCommits;
	}

	/**
	 * Gets the list of java files for a revision as inputstream.
	 * 
	 * @throws ParseException
	 * 
	 */
	public List<CompilationUnit> getJavaInputStream(final RevCommit c)
			throws MissingObjectException, IncorrectObjectTypeException,
			CorruptObjectException, IOException {
		List<CompilationUnit> filesList = new ArrayList<CompilationUnit>();

		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(c.getId());
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathSuffixFilter.create(".java"));

		while (treeWalk.next()) {
			ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
			try (InputStream in = loader.openStream()) {
				CompilationUnit cu = JavaParser.parse(in, "UTF8");
				filesList.add(cu);
			} catch (MissingObjectException moe) {
			} catch (IOException ioe) {
			} catch (ParseException pe) {
			}
		}
		return filesList;

	}

}
