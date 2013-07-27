package xian.visitor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jgit.revwalk.RevCommit;

import com.google.common.collect.Lists;

import xian.git.RepositoryAccess;
import xian.git.RepositoryAccess.Rule;
import xian.model.CommitData;

public class RepositoryVisitor {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		RepositoryAccess ra = new RepositoryAccess("https://github.com/xetorthio/jedis.git", Rule.OLD);
		int size = ra.getCommits().size();
		
		long t1 = System.currentTimeMillis();
		ExecutorService service = Executors.newFixedThreadPool(4);
		List<Future<CommitData>> futures = Lists.newArrayListWithCapacity(size);
		
		for(RevCommit c:ra.getCommits()){
			futures.add(service.submit(new CommitVisitor(ra.getJavaCompilationUnit(c))));
		}
		for(Future<CommitData> f:futures){
			if(f!=null)
			System.out.println(f.get().getCms().size());
		}
		service.shutdown();
		System.out.println((System.currentTimeMillis()-t1));
	}

}
