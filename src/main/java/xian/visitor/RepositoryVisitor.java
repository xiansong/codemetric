package xian.visitor;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jgit.revwalk.RevCommit;

import xian.git.RepositoryAccess;
import xian.rest.model.RevInfo;
import xian.visitor.model.CommitData;

import com.google.common.collect.Lists;

public final class RepositoryVisitor {

	private final RepositoryAccess accesser;

	public RepositoryVisitor(final RepositoryAccess a) {
		accesser = a;
	}

	public RevInfo getCommitData() {
		int size = accesser.getCommits().size();

		ExecutorService service = Executors.newFixedThreadPool(4);
		List<Future<CommitData>> futures = Lists.newArrayListWithCapacity(size);

		for (RevCommit c : accesser.getCommits()) {
			try {
				futures.add(service.submit(new CommitVisitor(accesser
						.getJavaCompilationUnit(c))));
			} catch (Exception e) {
			}
		}

		TDoubleList cycloList = new TDoubleArrayList();
		TDoubleList volumeList = new TDoubleArrayList();
		TDoubleList callList = new TDoubleArrayList();
		TDoubleList ratioList = new TDoubleArrayList();
		TDoubleList interactionList = new TDoubleArrayList();

		for (Future<CommitData> f : futures) {
			try {
				CommitData cd = f.get();
				cycloList.add(cd.getCyclomatics());
				volumeList.add(cd.getVolumes());
				callList.add(cd.getCms().size());
				ratioList.add(cd.getRatio());
				interactionList.add(cd.getInteraction());
			} catch (Exception e) {
			}
		}

		service.shutdown();

		RevInfo info = new RevInfo.Builder().cyclo(cycloList.toArray())
				.volume(volumeList.toArray()).call(callList.toArray())
				.ratio(ratioList.toArray())
				.interaction(interactionList.toArray()).build();
		return info;

	}

}
