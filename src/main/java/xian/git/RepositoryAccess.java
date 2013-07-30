package xian.git;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

import com.google.common.collect.Lists;

/**
 * The Class RepositoryAccess.
 * 
 * Create a local repository for access.
 */
public final class RepositoryAccess {

	/**
	 * The Enum Rule.
	 * 
	 * The rule for accessing the repository: using the old repository if
	 * existing or clone the latest one.
	 */
	public enum Rule {
		OLD(0), NEW(1), UNKNOWN(-1);

		private final int value;

		private Rule(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		private static final Map<Integer, Rule> intToRuleMap = new HashMap<Integer, Rule>();

		static {
			for (Rule rule : Rule.values()) {
				intToRuleMap.put(rule.value, rule);
			}
		}

		public static Rule fromInt(final int i) {
			Rule rule = intToRuleMap.get(Integer.valueOf(i));
			if (rule == null)
				return Rule.UNKNOWN;
			return rule;
		}
	}

	/** The Constant rootPath for storing local git repositories. */
	private static final String rootPath;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.home")).append(File.separator)
				.append("git").append(File.separator);
		rootPath = sb.toString();
	}

	private String url;

	private Repository repository;

	/**
	 * Instantiates a new repository access.
	 * 
	 * @param url
	 *            the url
	 * @param rule
	 *            repository access rule
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GitAPIException
	 *             the git api exception
	 */
	public RepositoryAccess(final String url, final Rule rule)
			throws IOException, GitAPIException {
		this.url = url;
		File gitDir = new File(rootPath + getRepositoryName());
		if (rule == Rule.OLD) {
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
	 * Clone the remote Repository to local
	 */
	private void gitClone() throws IOException, GitAPIException {
		File gitDir = new File(rootPath + getRepositoryName());

		try {
			Git.cloneRepository().setURI(url)
					.setDirectory(new File(rootPath + getRepositoryName()))
					.call();
		} catch (GitAPIException e) {
			FileUtils.delete(gitDir, FileUtils.RECURSIVE);
			throw e;
		}

		Git git = Git.open(gitDir);
		repository = git.getRepository();
	}

	/**
	 * Construct the repository name and return.
	 * 
	 * @return the repository name or null
	 */
	public String getRepositoryName() {
		String name = null;
		Pattern pattern = Pattern.compile("[a-zA-Z0-9\\.\\-\\_]+(\\.git)$");
		Matcher matcher = pattern.matcher(url);
		while (matcher.find()) {
			name = matcher.group(0);
		}
		return name.substring(0, name.length() - 4);
	}

	/**
	 * Gets the commits.
	 * 
	 * @return the commits
	 */
	public List<RevCommit> getCommits() {
		RevWalk walk = new RevWalk(repository);
		try {
			walk.markStart(walk.parseCommit(repository.resolve(Constants.HEAD)));
		} catch (Exception e) {
			return Collections.emptyList();
		}
		List<RevCommit> revCommits = Lists.newArrayList();
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
	public List<CompilationUnit> getJavaCompilationUnit(final RevCommit c)
			throws MissingObjectException, IncorrectObjectTypeException,
			CorruptObjectException, IOException {
		List<CompilationUnit> filesList = Lists.newArrayList();
		RevTree tree = c.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathSuffixFilter.create(".java"));

		while (treeWalk.next()) {
			ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
			try (InputStream in = loader.openStream()) {
				CompilationUnit cu = JavaParser.parse(in, "UTF8");
				filesList.add(cu);
			} catch (Exception e) {
			}
		}

		return filesList;
	}

}
