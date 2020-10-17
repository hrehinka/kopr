package cviko08.uloha1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SizeSummarizer {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		SizeSummarizerTask task = new SizeSummarizerTask();
		long start = System.nanoTime();
		Future<?> future = executor.submit(task);
		try {
			future.get(1, TimeUnit.SECONDS);
			System.out.println("V�etko sme stihli preh�ada�");
		} catch (TimeoutException e) {
			System.out.println("Skon�ili sme pred�asne");
		} catch (ExecutionException e) {
			// �loha vyhodila v�nimku, vyhod�me ju tie�
			System.err.println("Preh�ad�vanie disku skon�ilo chybou");
		} catch (InterruptedException e) {
			System.err.println("Niekto vypol cely program");
			e.printStackTrace();
		} finally {
			// nastav� interrupt, ak �loha e�te be��
			future.cancel(true);
			executor.shutdown();
			try {
				executor.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long duration = System.nanoTime() - start;
			System.out.println("Celkov� �as " + duration / 1000000.0 + " ms");
			
		}

	}

}
