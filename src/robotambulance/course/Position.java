package robotambulance.course;

public class Position {
	private Vertice from;
	private Vertice to;
	private float distanceFrom;
	
	public Position(Vertice from, Vertice to, float distanceFrom) {
		super();
		this.from = from;
		this.to = to;
		this.distanceFrom = distanceFrom;
	}
	
	
	

	public void setFrom(Vertice from) {
		this.from = from;
	}




	public void setTo(Vertice to) {
		this.to = to;
	}




	public void setDistanceFrom(float distanceFrom) {
		this.distanceFrom = distanceFrom;
	}




	public Vertice getFrom() {
		return from;
	}

	public Vertice getTo() {
		return to;
	}

	public float getDistanceFrom() {
		return distanceFrom;
	}




	@Override
	public String toString() {
		return "Position [from=" + from + ", to=" + to + ", distanceFrom=" + distanceFrom + "]";
	}
	
	
	
}
