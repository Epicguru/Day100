package co.uk.epicguru.settings;

public enum LightQuality {
	ULTRA(1000),
	HIGH(400),
	MEDIUM(200),
	LOW(100);
	
	private final int value;
    private LightQuality(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
