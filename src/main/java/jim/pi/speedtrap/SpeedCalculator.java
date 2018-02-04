package jim.pi.speedtrap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.fusesource.jansi.Ansi;

import com.pi4j.component.light.LED;

/**
 * This class is responsible for calculating the speed of the car through the
 * pair of IR sensors.  It will turn on the green light when the car passes the
 * "start line" and will turn on the red line when the car passes the "finish
 * line."  The lights will remain on for ~2.5 seconds after crossing the finish
 * line and will then be turned off.  Currently, the process only calculates
 * scaled speed for a single, specific car. It also assumes a distance of 12
 * inches between the start and finish lines of the speed trap. Perhaps
 * someday, these values could be supplied as a system property to the
 * process?
 * <br/>
 * The conversion factor is the square root of the scale size of the model to
 * the real version of the car.  So, for a model 1/10th actual size, the
 * conversion factor is the square root of 10. 
 * <br/>
 * Actual MPH is calculated as:
 * <br/><code>
 * (distance traveled in inches / milliseconds of travel time) * 56.8181818
 * </code>
 * <br/>Note that the 56.8181818 value is what Google tells me is the speed in
 * MPH of traveling 1 inch / millisecond.
 * <br/>Scaled MPH is calculated as the "real" MPH times the scale size
 * conversion factor.
 * @author jahopki
 *
 */
public class SpeedCalculator {

	private static SpeedCalculator scInstance = null;
	private AnsiConsoleWriter writer = new AnsiConsoleWriter();
	private List<LED> lights = new ArrayList<>();

	private Long startTime;
	private Long stopTime;
	private boolean timerStarted = false;

	/*
	 * unfortunate hard coding
	 */
	private final double scaledConversionFactor = 8.1166; 

	private SpeedCalculator() {} // singleton...

	/**
	 * This constructor protects the intention of a singleton instance of this
	 * class.  The LED parameters are intended to reflect the association of
	 * specific LEDs to the start and finish line of the speed trap.
	 * @param startLineLight
	 * @param finishLineLight
	 * @return
	 */
	public static SpeedCalculator getInstance( LED startLineLight, LED finishLineLight ) {
		SpeedCalculator instance = null;
		if( scInstance != null ) {
			instance = scInstance;
		} else {
			instance = new SpeedCalculator( startLineLight, finishLineLight );
		}

		return instance;
	}

	/**
	 * Starts the timer (if it hasn't already been started).
	 */
	public void startTimer( ) {
		if( timerStarted == false ) {
			startTime = Instant.now().toEpochMilli();
			timerStarted = true;
			writer.write( "starting the timer" );
		} else {
			writer.write( "We can't start a session without terminating the previous session!",
					Ansi.Color.RED,
					Ansi.Attribute.INTENSITY_BOLD );
			terminateSession();
		}
	}

	/**
	 * Stops the timer, performs the calculation of speed of travel, prints the
	 * results to the console, sleeps for 2.5 seconds, then turns off the LEDs
	 * associated with the start and finish lines.
	 */
	public void stopTimer() {
		if( timerStarted == true ) {
			stopTime = Instant.now().toEpochMilli();
			timerStarted = false;
			writer.write( "stopping the timer" );

			/*
			 * assumes 12 inches between IR sensors...
			 * Google tells me that 1 inch / millisecond == 56.8181818 mph...
			 */
			double mph = ( 12d / ( stopTime - startTime )) * 56.8181818;
			writer.write( "actual mph calculation is [" + mph + "]" );
			writer.write( "scaled mph is [" + ( mph * scaledConversionFactor ) + "]" );

			try {
				Thread.sleep( 2500 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			terminateSession();
		} else {
			writer.write( "We can't stop a timer that hasn't been started!",
					Ansi.Color.RED,
					Ansi.Attribute.INTENSITY_BOLD );
		}
	}

	private void terminateSession() {
		writer.write( "terminating calculator session" );
		lights.forEach( light -> {
			writer.write( "shutting down light [" + light.getName() + "]" );
			light.off();
		});
	}

	private SpeedCalculator( LED startLineLight, LED finishLineLight ) {
		lights.add( startLineLight );
		lights.add( finishLineLight );
	}
}
