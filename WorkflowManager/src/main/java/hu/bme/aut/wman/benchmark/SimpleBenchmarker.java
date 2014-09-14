/**
 * SimpleBenchmarker.java
 */
package hu.bme.aut.wman.benchmark;


/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public abstract class SimpleBenchmarker implements Benchmarkable {

	public static final long MAX_I = 500;
	public static final long MAX_J = 100 * 100;
	
	public double execute() throws Exception {
		setup();
		double average = 0;
		for(int i = 1; i < MAX_I; ++i) {
			long t1 = System.nanoTime();
			
			for(int j = 0; j < MAX_J; ++j)
				doExecute();
			
			long t2 = System.nanoTime();
			average += (double) ((t2 - t1) * 1e-6) / MAX_I;
		}
		return average;
	}
	
	public void setup() {}
	protected abstract void doExecute() throws Exception;
}
