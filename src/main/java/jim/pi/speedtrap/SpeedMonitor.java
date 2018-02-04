package jim.pi.speedtrap;

import java.util.concurrent.Future;

import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

import com.pi4j.component.light.LED;

import jim.pi.speedtrap.BeamBreakSensorListener.LineType;

/**
 * This is ultimately the primary controller class for this project.
 * @author Jim Hopkins
 *
 */
public class SpeedMonitor {

	private BreadboardPins pinSet;
	private AnsiConsoleWriter writer;
	private SpeedCalculator calculator;

	public SpeedMonitor( BreadboardPins pinSet, AnsiConsoleWriter parentWriter ) {
		this.pinSet = pinSet;
		this.writer = parentWriter;

		/*
		 * setup the utility to calculate speed...
		 */
		calculator = SpeedCalculator.getInstance( pinSet.getGreenLight(), pinSet.getRedLight() );

		/*
		 * add the listeners to the start / finish lines
		 */
		pinSet.getStartLine().addListener(
				new BeamBreakSensorListener(
						writer,
						pinSet.getGreenLight(),
						calculator,
						LineType.START )
				);

		pinSet.getStopLine().addListener(
				new BeamBreakSensorListener(
						writer,
						pinSet.getRedLight(),
						calculator,
						LineType.STOP )
				);
	}

	/**
	 * This is kind of silly since we expect the class that invoked this method
	 * to maintain the JVM to allow the listeners remain active.  Ultimately,
	 * this method will allow the green and red LEDs to blink every 1/4 second
	 * for 5 seconds to indicate that things are starting up.
	 */
	public void go(){
		Future<?> greenLightBlinker = blinkTheLight( pinSet.getGreenLight() );
		Future<?> redLightBlinker = blinkTheLight( pinSet.getRedLight() );
		while( !greenLightBlinker.isDone() && !redLightBlinker.isDone() ) {
			try {
				Thread.sleep( 100 );
			} catch (InterruptedException e) {
				writer.write( "How in the world did my sleep get interrupted?!?!?!?!", Color.RED, Attribute.INTENSITY_BOLD );
				e.printStackTrace();
			}
		}
	}
	
	private Future<?> blinkTheLight( LED lightToBlink ) {
		/*
		 * blink the light every 1/4 second for 5 seconds
		 */
		return lightToBlink.blink( 250, 5000 );
	}
}
