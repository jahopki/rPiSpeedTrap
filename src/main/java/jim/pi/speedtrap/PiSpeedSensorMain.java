package jim.pi.speedtrap;

import java.util.concurrent.TimeUnit;

import org.fusesource.jansi.Ansi;

import com.google.common.base.Stopwatch;

/**
 * This is the main method of the process.  It's responsible for setting up
 * the GPIO set, basically pausing forever, and waiting for an interrupt
 * to clean up the GPIO before final shutdown.
 *
 */
public class PiSpeedSensorMain {

	/*
	 * This could (and probably should) be injected via IoC instead of directly
	 * instantiated.  As it is, the appearance is best on a terminal that has a
	 * white background and black font.  (But that's just my opinion.)
	 */
	private static AnsiConsoleWriter writer = new AnsiConsoleWriter();

	public static void main( String[] args ) throws InterruptedException {
		Stopwatch timer = Stopwatch.createStarted();
		BreadboardPins pinSet = new BreadboardPins( writer );

		StringBuilder msg = new StringBuilder();
		msg.append( "------------------------------------------" );
		msg.append( " RaspberryPi GPIO via Java8 " );
		msg.append( "-----------------------------------------\n" );
		writer.write( msg.toString(), Ansi.Color.CYAN, Ansi.Attribute.INTENSITY_BOLD );
		msg = new StringBuilder().append( "we're off to the races, now!  Press Ctrl-C to quit.\n\n\n\n\n" );
		writer.write( msg.toString() );

		/*
		 * configure the JVM shutdown hook, primarily for the purpose of invoking
		 * the turnOff() method of the GPIO communication channel
		 */
		Runtime.getRuntime().addShutdownHook( new Thread() {
			public void run() {
				writer.write( "ShutdownHook triggered!", Ansi.Color.CYAN, Ansi.Attribute.INTENSITY_BOLD );
				writer.write( "shutting down GPIO communication", Ansi.Color.CYAN, Ansi.Attribute.INTENSITY_BOLD );
				writer.write( "total runtime of main() method [" + timer.stop().elapsed( TimeUnit.MILLISECONDS ) + "] milliseconds" );
				pinSet.turnOff();
			}
		});

		/*
		 * This is probably a misleading name since the SpeedListener class
		 * isn't an instance of Runnable...  But it moves the primary logic
		 * of the program out of main().
		 */
		SpeedMonitor runner = new SpeedMonitor( pinSet, writer );
		runner.go();

		/*
		 * gah!  I hate this!  there has to be a better way to keep running...
		 */
		for( int secondsRunning = 0; secondsRunning < Integer.MAX_VALUE; secondsRunning++ ) {
			Thread.sleep( Integer.MAX_VALUE );
		}

		/*
		 * we should never get here if we terminate normally (ie: ctrl-c or
		 * pretty much any kill command other than -9);  The shutdown hook
		 * should take care of normal operation.
		 */
		writer.write( "abnormal shutdown of GPIO communication (died without an interrupt)",
				Ansi.Color.CYAN,
				Ansi.Attribute.INTENSITY_BOLD );
		writer.write( "total runtime of main() method [" +
				timer.stop().elapsed( TimeUnit.MILLISECONDS ) +
				"] milliseconds" );
		pinSet.turnOff();
	}
}
