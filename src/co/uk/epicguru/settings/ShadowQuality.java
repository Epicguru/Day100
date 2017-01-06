package co.uk.epicguru.settings;

public enum ShadowQuality {
	ULTRA(1f),
	HIGH(0.7f),
	MEDIUM(0.5f),
	LOW(0.25f);
	
	private final float value;
    private ShadowQuality(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
