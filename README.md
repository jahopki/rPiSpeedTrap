# rPi Speed Trap

This project uses the [pi4j](http://pi4j.com/ "pi4j") GPIO utility APIs to calculate the speed of a model car (Hot Wheels) between two
IR sensors and blink some LEDs off and on through the process.  (It should be noted that we're actually using the 1.2 SNAPSHOT
version of pi4j located [here](https://oss.sonatype.org/index.html#nexus-search;gav~com.pi4j~pi4j-*~1.2-SNAPSHOT~~).)

There are some key assumptions currently embedded in this project:
* The IR sensors are exactly 12 inches apart from each other
* The model car being used is 1/66 scale size
* You're using a Pi 2 Model B+
* Aside from the alternate repository reference to obtain the 1.2-SNAPSHOT version of pi4j, I assume that you have Maven configured in a manner to resolve the "normal" artifacts included in this project, which include:
* junit : junit : 3.8.1  (even though I haven't actually written any tests ;-P )
	* org.fusesource.jansi : jansi : 1.14
	* com.google.guava : guava : 22.0 
	
As mentioned, we're using the pi4j project to manage GPIO communication.  This includes the integration with wiringPi.
This is a bit different than most (python) GPIO examples as they use the more standard Broadcomm GPIO communication.
The biggest bit of this is that pi4j has tried to abstract the GPIO pinset numbers from the specific hardware vendor, so
the standard pinout sheet found with most Raspberry Pis doesn't apply here; instead we're required to use the [pinout
configuration](http://pi4j.com/pins/model-b-plus.html) defined by the pi4j / wiringPI projects, and again, this is specific
to the Pi Model B Plus.

Currently, the process is hardcoded for specific GPIO inputs / outputs because I haven't (yet) allowed for system properties
(or something similarly configurable) to avoid this.  As it is, here are the GPIO values currently used:
	* LED pin for "green" light (associated with speed trap start IR sensor) -> `RaspiPin.GPIO_01`
	* LED pin for "red" light  (associated with speed trap finish IR sensor) -> `RaspiPin.GPIO_21`
	* Digital input pin associated with start line IR sensor -> `RaspiPin.GPIO_27`
	* Digital input pin associated with start line IR sensor -> `RaspiPin.GPIO_26`
	* The IR sensors are powered and grounded through the 5V channel of a breadboard / Pi GPIO
	* The LEDs are grounded through a resistor and powered by the output pin / channel of the rPi (so they're on the opposite side of the breadboard as the IR sensor input pins)
