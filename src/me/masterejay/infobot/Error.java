package me.masterejay.infobot;

/**
 * @author MasterEjay
 */
public class Error extends Exception{
	private static final long serialVersionUID = 1L;

	public Error(String string) {
		super(string);
	}

	public Error() {
		super();
	}
}
