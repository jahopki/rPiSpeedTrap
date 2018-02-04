package jim.pi.speedtrap;

import com.pi4j.component.light.LED;
import com.pi4j.component.light.impl.GpioLEDComponent;
import com.pi4j.component.sensor.impl.GpioSensorComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * ****************************************************************************
 * It is VITALLY important to note that the pin numbers referenced in this
 * project are NOT the raw Broadcom (BCM) GPIO pin numbers,
 * but rather those mapped by the WiringPi / Pi4J enumerations!  And
 * specifically to this project, we're referencing the Model B+
 * pinout configuration.  (ie: http://pi4j.com/pins/model-b-plus.html)
 * ****************************************************************************
 * 
 * This class sets up the GPIO components and maps them to the breadboard
 * used to execute the process.
 * 
 * @author Jim Hopkins
 */
public class BreadboardPins {
	/*
	 * GpioFactory's getInstance() method is not thread safe, so we have to get
	 * an instance once and pass it around for use
	 */
	private static final GpioController GPIO = GpioFactory.getInstance();
	private AnsiConsoleWriter writer;

	private LED greenLed;
	private LED redLed;
	private GpioSensorComponent startLine;
	private GpioSensorComponent stopLine;

	/*
	 * It would probably be a bit more elegant if this values were included as
	 * system properties rather than hard-coded here.
	 */
	private static final Pin GREEN_LED_PIN = RaspiPin.GPIO_01;
	private static final Pin RED_LED_PIN = RaspiPin.GPIO_21;
	private static final Pin START_LINE_PIN = RaspiPin.GPIO_27;
	private static final Pin STOP_LINE_PIN = RaspiPin.GPIO_26;

	@SuppressWarnings("unused")
	private BreadboardPins() {}

	public BreadboardPins( AnsiConsoleWriter writer ) {
		this.writer = writer;
		this.writer.write( "Setting up the breadboard configuration" );
		setupLEDs();
		setupSensors();
	}

	/**
	 * Returns the GpioSensorComponent defined as the starting line for the
	 * timing line
	 * @return GpioSensorComponent
	 */
	public GpioSensorComponent getStartLine() {
		return this.startLine;
	}

	/**
	 * Returns the GpioSensorComponent defined as the finish line for the
	 * timing line
	 * @return GpioSensorComponent
	 */
	public GpioSensorComponent getStopLine() {
		return this.stopLine;
	}

	/**
	 * This method configures / sets-up the IR sensors used to trap the speed
	 * of the car as it moves through the trap.  It's called out elsewhere, but
	 * it's important to note that this process assume exactly 12 inches
	 * between the start line sensor and the finish line sensor.
	 */
	private void setupSensors() {
		/*
		 * With these configurations, the sensors are considered "closed" when
		 * the pin goes HIGH...
		 * We also provision the GpioPinDigitalInput separate from the creation
		 * of the GpioSensorComponent because we need to define a debounce
		 * value for the input pin to prevent phantom readings.  I didn't trial-and-
		 * error the value for this at all; I just picked a fairly arbitrary value and
		 * verified that phantom readings were eliminated.
		 */
		GpioPinDigitalInput startLinePin = GPIO.provisionDigitalInputPin( START_LINE_PIN, PinPullResistance.PULL_UP );
		startLinePin.setDebounce( 125 );
		startLine = new GpioSensorComponent( startLinePin, PinState.LOW, PinState.HIGH );

		GpioPinDigitalInput stopLinePin = GPIO.provisionDigitalInputPin( STOP_LINE_PIN, PinPullResistance.PULL_UP );
		stopLinePin.setDebounce( 125 );
		stopLine = new GpioSensorComponent( stopLinePin, PinState.LOW, PinState.HIGH );
	}

	/**
	 * This method configures / sets-up the two LED components that we use in
	 * this process.  Since we're dealing with "cars" and speed, we use the
	 * names "Green" and "Red" to indicate, respectively, start and finish
	 * line lights.
	 */
	private void setupLEDs() {
		/*
		 * Setup the green & red LEDs to an off state and give them a meaningful
		 * name. The name is absolutely not necessary, but it helps make
		 * debugging a bit cleaner later...
		 */
		greenLed = new GpioLEDComponent( GPIO.provisionDigitalOutputPin( GREEN_LED_PIN, PinState.LOW ));
		greenLed.setName( "green" );
		redLed = new GpioLEDComponent( GPIO.provisionDigitalOutputPin( RED_LED_PIN, PinState.LOW ));
		redLed.setName( "red" );
	}

	public GpioController getGPIOController() {
		return GPIO;
	}
	
	public LED getGreenLight() {
		return greenLed;
	}

	public LED getRedLight() {
		return redLed;
	}

	/**
	 * Shut off any lights and then shutdown the GPIO board
	 */
	public void turnOff() {
		greenLed.off();
		redLed.off();
		GPIO.shutdown();
	}
}
