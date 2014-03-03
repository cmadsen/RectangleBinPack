package dk.navicon.binpack;

public final class MutableInt {

	// public static final MutableInt MAX_VALUE = new
	// MutableInt(Integer.MAX_VALUE);

	private int value;

	public MutableInt(int i) {
		value = i;
	}

	public int get() {
		return value;
	}

	public void set(int i) {
		value = i;
	}

}
