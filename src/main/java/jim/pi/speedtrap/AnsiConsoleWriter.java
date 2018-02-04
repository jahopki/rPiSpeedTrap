package jim.pi.speedtrap;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * This is a very basic wrapper around AnsiConsole.out().println().  Maybe
 * someday we'll replace or augment this with some log4j implementation? 
 * @author Jim Hopkins
 *
 */
public class AnsiConsoleWriter {

	/**
	 * setup the AnsiConsole
	 */
	public AnsiConsoleWriter() {
		AnsiConsole.systemInstall();
	}

	/**
	 * write a message to the console
	 */
	public void write( String msg ) {
		AnsiConsole.out().println( Ansi.ansi().a( msg ).reset() );
	}

	/**
	 * Write a message to the console in the specified color.
	 * @param msg The message to be written
	 * @param color The color to use when writing the message
	 */
	public void write( String msg, Ansi.Color color ) {
		AnsiConsole.out().println( Ansi.ansi().fg( color ).a( msg ).reset() );
	}

	/**
	 * 	Write a message to the console in the specific color and decorating
	 * attribute (ie: bold, italics, etc.).
	 * @param msg The message to be written
	 * @param color The color to use when writing the message
	 * @param attr The decorating attribute to apply to the text
	 */
	public void write( String msg, Ansi.Color color, Ansi.Attribute attr ) {
		AnsiConsole.out().println( Ansi.ansi().fg( color ).a( attr ).a( msg ).reset() );
	}
}
