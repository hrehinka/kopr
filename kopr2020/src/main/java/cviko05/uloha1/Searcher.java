package cviko05.uloha1;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Searcher implements Runnable {

	public static final File POISON_PILL = new File("poison.pill");
	private BlockingQueue<File> queue;
//	private BlockingQueue<File> unprocessedDirs;
	private static AtomicInteger dirCounter = new AtomicInteger(1);
	private static Semaphore dirsInDequesSemaphore = new Semaphore(1);
	private BlockingDeque<File>[] deques;
	private int myId;

	public Searcher(BlockingQueue<File> queue, BlockingDeque<File>[] deques, int myId) {
		this.queue = queue;
		this.deques = deques;
		this.myId = myId;

	}

	public void run() {
		try {
			while (true) {
				dirsInDequesSemaphore.acquire();// bud ma pusti a znizi o 1 alebo ma nepusti lebo je tam 0
				File dir = deques[myId].pollFirst();// neblokovana operacia
				int where = myId;
				// kradnutie
				while (dir == null) {
					where = (where + 1) % deques.length;
					// pri if sme mali first ale tym ze je while tak posledny
					dir = deques[where].pollLast();
				}
				if (dir == POISON_PILL) {
					break;
				}
				search(dir.listFiles());
				int count = dirCounter.decrementAndGet();
				if (count == 0) {
					for (int i = 0; i < WordCounter.SEARCHERS_COUNT; i++) {
						deques[myId].offerFirst(POISON_PILL);
						dirsInDequesSemaphore.release();
					}
					for (int i = 0; i < WordCounter.ANALYZERS_COUNT; i++) {
						queue.offer(POISON_PILL);
					}
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void search(File[] dir) {
		for (int i = 0; i < dir.length; i++) {
			if (dir[i].isDirectory()) {
				dirCounter.incrementAndGet();
				deques[myId].offerFirst(dir[i]);
				dirsInDequesSemaphore.release();
			} else {
				if (dir[i].getName().endsWith(".java")) {
					queue.offer(dir[i]);
				}
			}
		}
	}
}
