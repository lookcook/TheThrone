public enum Resource {
	ECONOMY (0), MILITARY (1), RELIGION (2), HAPPINESS (3);
	
	int value;
	
	Resource(int value) {
		this.value = value;
	}
	
	public int id() {
		return value;
	}
}
