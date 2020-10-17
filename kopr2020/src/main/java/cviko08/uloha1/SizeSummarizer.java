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
			System.out.println("Vöetko sme stihli prehæadaù");
		} catch (TimeoutException e) {
			System.out.println("SkonËili sme predËasne");
		} catch (ExecutionException e) {
			// ˙loha vyhodila v˝nimku, vyhodÌme ju tieû
			System.err.println("Prehæad·vanie disku skonËilo chybou");
		} catch (InterruptedException e) {
			System.err.println("Niekto vypol cely program");
			e.printStackTrace();
		} finally {
			// nastavÌ interrupt, ak ˙loha eöte beûÌ
			future.cancel(true);
			executor.shutdown();
			try {
				executor.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long duration = System.nanoTime() - start;
			System.out.println("Celkov˝ Ëas " + duration / 1000000.0 + " ms");
			
		}

	}

}
