package robotambulance.course;

public class Position implements Comparable<Position>{
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
	public boolean equals(Object o) {
		
		if(o!=null && o instanceof Position) {
			Position pos = (Position) o;
			return pos.getFrom().equals(this.getFrom()) 
					|| pos.getTo().equals(this.getTo()) 
					|| pos.getFrom().equals(this.getTo()) 
					|| pos.getTo().equals(this.getFrom());
		}
		return false;
	}
	


	@Override
	public String toString() {
		return "Position [from=" + from + ", to=" + to + ", distanceFrom=" + distanceFrom + "]";
	}




	@Override
	public int compareTo(Position pos) {
		int c1 = from.compareTo(pos.from);
		if(c1==0) {
			int c2 = to.compareTo(pos.getTo());

			return c2;
		}
		return c1;
	}
	
	
	
}
