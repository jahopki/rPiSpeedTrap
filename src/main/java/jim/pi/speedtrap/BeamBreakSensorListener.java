package jim.pi.speedtrap;

import org.fusesource.jansi.Ansi;

import com.pi4j.component.light.LED;
import com.pi4j.component.sensor.SensorListener;
import com.pi4j.component.sensor.SensorState;
import com.pi4j.component.sensor.SensorStateChangeEvent;

/**
 * This implementation of the pi4j SensorListener interface is used
 * by the Start and Finish line sensors in this project.  I created the
 * separate class for reuse and simplication / clarity of the creation
 * of the Sensors.
 * @author Jim Hopkins
 *
 */
public class BeamBreakSensorListener implements SensorListener {

	/**
	 * An enumeration to more clearly indicate the purpose of the
	 * instance of the BeamBreakSensorListener.
	 */
	public enum LineType{
		START,
		STOP,
		DEFAULT;
	}

	private AnsiConsoleWriter writer = null;
	private LED led = null;
	private LineType type = LineType.DEFAULT;
	private SpeedCalculator calculator;

	@SuppressWarnings("unused")
	private BeamBreakSensorListener() {}

	/**
	 * The only available constructor of this class.
	 * @param writer An instance of AnsiConsoleWriter pre-configured / setup
	 * @param led The LED affiliated with this Listener instance.
	 * @param calculator The SpeedCalculator class associated with the program
	 * @param type An instance of the LineType enumeration to reflect whether
	 * this Listener is affiliated with the start or finish of the timing line.
	 */
	public BeamBreakSensorListener( AnsiConsoleWriter writer, LED led, SpeedCalculator calculator, LineType type ) {
		this.writer = writer;
		this.led = led;
		this.calculator = calculator;
		this.type = type;
	}
	
	@Override
	public void onStateChange( SensorStateChangeEvent event ) {
		/*
		 * When the beam is broken (ie: moves to its OPEN state), we turn on
		 * the LED associated with that beam.  We defer turning off the LED
		 * to the consumer...
		 */
		if( event.getNewState().equals( SensorState.OPEN )) {
			/*
			 * what's up with the LED blink() method???  I can't seem to turn it off?!?!
			 */
//			led.blink( 100 );
			led.on();

			switch( type ) {
				case START:
					calculator.startTimer();
					break;
				case STOP:
					calculator.stopTimer();
					break;
				case DEFAULT:
					writer.write( "invalid LineType encountered! [" + type + "]" );
					break;
			}
		}

		StringBuilder msg =
				new StringBuilder()
				.append( "++++++ state change event detected!  newState [" )
				.append( event.getNewState() )
				.append( "]" );
		writer.write( msg.toString(), Ansi.Color.MAGENTA  );
	}
}
