package robotambulance.course;

import java.util.ArrayList;
import java.util.List;

public class Vertice implements Comparable<Vertice>{
	private String name;
	private Vertice leftNeighbour;
	private Vertice rightNeighbour;
	private Vertice backwardNeighbour;
	private Vertice predecessor;
	private float weight = 0.f;
	
	public Vertice(String name) {
		this.name=name;
		this.leftNeighbour=null;
		this.rightNeighbour=null;
		this.backwardNeighbour=null;
		this.predecessor = null;
	}

	

	
	
	public Vertice getPredecessor() {
		return predecessor;
	}





	public void setPredecessor(Vertice predecessor) {
		this.predecessor = predecessor;
	}





	public Vertice getBackwardNeighbour() {
		return backwardNeighbour;
	}




	public void setBackwardNeighbour(Vertice backwardNeighbour) {
		this.backwardNeighbour = backwardNeighbour;
	}




	public float getWeight() {
		return weight;
	}


	public void setWeight(float weight) {
		this.weight = weight;
	}


	public void setLeftNeighbour(Vertice leftNeighbour) {
		this.leftNeighbour = leftNeighbour;
	}



	public void setRightNeighbour(Vertice rightNeighbour) {
		this.rightNeighbour = rightNeighbour;
	}



	public Vertice getLeftNeighbour() {
		return leftNeighbour;
	}

	public Vertice getRightNeighbour() {
		return rightNeighbour;
	}

	public String getName() {
		return name;
	}
	
	
	public List<Vertice> getNeighbours() {
		List<Vertice> n = new ArrayList<>();
		n.add(leftNeighbour);
		n.add(rightNeighbour);
		n.add(backwardNeighbour);		
		return n;
	}

	
	
	@Override
	public boolean equals(Object o) {
		if(o!=null && o instanceof Vertice) {
			Vertice v = (Vertice) o;		
			return name.equals(v.getName());		
		}
		return false;		
	}





	@Override
	public String toString() {
		return "Vertice [name=" + name + "]";
	}





	@Override
	public int compareTo(Vertice v) {
		return name.compareTo(v.getName());
	}
	
	
}
