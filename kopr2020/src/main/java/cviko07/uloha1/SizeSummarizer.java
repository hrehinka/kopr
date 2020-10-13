package cviko07.uloha1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class SizeSummarizer {

	public static final String START_DIR = "C:\\Users\\uzivatel\\paz1b";
	public static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		File rootDir = new File(START_DIR);
		System.out.println("Po»et vl·kien :" + THREAD_COUNT);
		ForkJoinPool executor = new ForkJoinPool();

		long start = System.nanoTime();
		File[] files = rootDir.listFiles();
		List<SizeTask> tasks = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				SizeTask task = new SizeTask(files[i]);
				executor.submit(task);
				tasks.add(task);

			}
		}
		for (SizeTask task : tasks) {
			DirSize dirSize = task.join();
			System.out.println("»as: " + (System.nanoTime() - start) / 1000000 + " ms   " + dirSize);
		}
		executor.shutdown();

	}

}
