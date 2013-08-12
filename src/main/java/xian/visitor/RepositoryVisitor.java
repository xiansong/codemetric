package xian.visitor;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jgit.revwalk.RevCommit;

import xian.git.RepositoryAccess;
import xian.rest.model.RevInfo;
import xian.visitor.model.CommitData;

import com.google.common.collect.Lists;

/**
 * The Class RepositoryVisitor. Get information of all revision
 */
public final class RepositoryVisitor {

	private final RepositoryAccess accesser;

	public RepositoryVisitor(final RepositoryAccess a) {
		accesser = a;
	}

	public RevInfo getCommitData() {
		int size = accesser.getCommits().size();

		ExecutorService service = Executors.newFixedThreadPool(4);
		ArrayList<Future<CommitData>> futures = Lists
				.newArrayListWithCapacity(size);

		for (RevCommit c : accesser.getCommits()) {
			try {
				futures.add(service.submit(new CommitVisitor(accesser
						.getJavaCompilationUnit(c))));
			} catch (Exception e) {
			}
		}

		double[] cycloList = new double[size];
		double[] volumeList = new double[size];
		double[] callList = new double[size];
		double[] ratioList = new double[size];
		double[] interactionList = new double[size];

		for (int i = 0; i < futures.size(); i++) {
			try {
				CommitData cd = futures.get(i).get();
				cycloList[i] = cd.getCyclomatics();
				volumeList[i] = cd.getVolumes();
				ratioList[i] = cd.getRatio();
				callList[i] = cd.getCms().size();
				interactionList[i] = cd.getInteraction();
			} catch (Exception e) {
			}
		}

		service.shutdown();

		RevInfo info = new RevInfo.Builder().cyclo(cycloList)
				.volume(volumeList).call(callList).ratio(ratioList)
				.interaction(interactionList).build();
		return info;

	}

}
