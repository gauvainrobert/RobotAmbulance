package robotambulance.course;

public class Road {
	private Vertice from;
	private Vertice to;
	private float weight;
	
	public Road(Vertice from, Vertice to, float weight) {
		this.from = from;
		this.to = to;
		this.weight=weight;
	}

	public float getWeight() {
		return weight;
	}

	public Vertice getFrom() {
		return from;
	}

	public Vertice getTo() {
		return to;
	}
	
	
	
}
