package browserApp;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Driver {

	public static ArrayBlockingQueue<Runnable> pool;
	public static ThreadPoolExecutor executor;
	
	public static void main(String[] args) {
		
		new WebServer().start();
		int i = 0;
		
		
		pool = new ArrayBlockingQueue<Runnable>(128);
		executor = new ThreadPoolExecutor(128, 2048, 120, TimeUnit.SECONDS, pool);

		
		while (i <= 0) {
			
			new WebClient(i).start();
			i++;	
		}
	}
	
	public static void ThreadPool (Thread T){
		executor.execute(T);
	}

}
