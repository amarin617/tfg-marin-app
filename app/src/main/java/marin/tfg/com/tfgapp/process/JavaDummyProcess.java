package marin.tfg.com.tfgapp.process;

public class JavaDummyProcess {
	private static final int SPIN_COUNT = 5000;
	private static final int NUMTHREADS = 4;

	public static void start() {
		long startWhen = System.nanoTime();
		SpinThread threads[] = new SpinThread[NUMTHREADS];
		for (int i = 0; i < NUMTHREADS; i++) {
			threads[i] = new SpinThread(i);
			threads[i].start();
		}
		for (int i = 0; i < NUMTHREADS; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException ie) {
				System.err.println("join " + i + " failed: " + ie);
			}
		}
		long endWhen = System.nanoTime();
		System.out.println("All threads finished in "
				+ ((endWhen - startWhen) / 1000000) + "ms");
	}

	private static class SpinThread extends Thread {
		private int mTid;

		SpinThread(int tid) {
			mTid = tid;
		}

		public void run() {
			long startWhen = System.nanoTime();
			int tid = mTid;
			int reps = SPIN_COUNT + tid;
			int ret = 0;

			for (int i = 0; i < reps; i++) {
				for (int j = 0; j < 100000; j++) {
					ret += i * j;
				}
			}

			long endWhen = System.nanoTime();
			System.out.println("Thread " + mTid + " finished in "
					+ ((endWhen - startWhen) / 1000000) + "ms (" + ret + ")");
		}
	}
}
