package codemetric;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestExcutor {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService service = Executors.newFixedThreadPool(4);
		List<Future<Integer>> futures = new ArrayList<Future<Integer>>(10);
		
		for(int i=0;i<10;i++){
			futures.add(service.submit(new SumTask(5)));
		}
		
		int sum = 0;
		for(Future<Integer> fu : futures){
			sum+=fu.get().intValue();
		}
		System.out.println(sum);
		service.shutdown();
	}

	static class SumTask implements Callable<Integer> {

		int sum = 0;
		int n;

		public SumTask(int n) {
			this.n = n;
		}

		@Override
		public Integer call() throws Exception {

			for (int i = 0; i < n; i++) {
				sum += i;
			}
			return sum;
		}

	}

}
