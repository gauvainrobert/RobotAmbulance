package robotambulance.course;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import robotambulance.util.Constants;
import robotambulance.util.Direction;

public class Course {
	private List<Vertice> vertices;
	private List<Road> roads;
	private List<Position> victims;
	private List<Position> hospitals;
	
	public List<Position> getVictims() {
		return victims;
	}

	public List<Position> getHospitals() {
		return hospitals;
	}

	public Course(List<Vertice> vertices, List<Road> roads, List<Position> victims, List<Position> hospitals) {
		super();
		this.vertices = vertices;
		this.roads = roads;
		this.victims = victims;
		this.hospitals = hospitals;
	}
	
	public List<Vertice> getVertices() {
		return vertices;
	}
	public List<Road> getRoads() {
		return roads;
	}

	
	public Vertice findHospital(Position origin, List<Position> positionsToAvoid) {
		Vertice h = null;
		djikstra(origin,positionsToAvoid);
		
		for (Iterator<Position> iterator = hospitals.iterator(); iterator.hasNext();) {
			Position position = iterator.next();
			if(h==null || position.getFrom().getWeight()<h.getWeight())
				h=position.getFrom();
			if(h==null || position.getTo().getWeight()<h.getWeight())
				h=position.getTo();
			
		}
		
		return h;
	}
	
	public Vertice findVictim(Position origin, List<Position> positionsToAvoid) {
		Vertice h = null;
		djikstra(origin,positionsToAvoid);
		
		for (Iterator<Position> iterator = victims.iterator(); iterator.hasNext();) {
			Position position = iterator.next();
			if(h==null || position.getFrom().getWeight()<h.getWeight())
				h=position.getFrom();
			if(h==null || position.getTo().getWeight()<h.getWeight())
				h=position.getTo();
		}
		return h;
	}
	
	
	

	public static float getDistance(List<Road> roads, Vertice from, Vertice to, List<Position> positionsToAvoid) {
		float distance=0.f;
		if(positionsToAvoid!=null)
			for (Iterator<Position> iterator = positionsToAvoid.iterator(); iterator.hasNext();) {
				Position position = iterator.next();
				if(position.getFrom().equals(from) || position.getTo().equals(to))
					return Constants.avoidRoad;
				
			}
		
		for (Iterator<Road> iterator = roads.iterator(); iterator.hasNext();) {
			Road road = iterator.next();
			if((road.getFrom().equals(from) && road.getTo().equals(to)) || (road.getFrom().equals(to) && road.getTo().equals(from))) {
				return road.getWeight();				
			}
		}
		return distance;
	}
	

	public Position findPosition(Direction side, Vertice from, boolean intersection) {
		Position position = null;
		if(intersection) {
			if(side==Direction.LEFT) {
				position = new Position(from,from.getLeftNeighbour(),0.f);
			}else {
				position = new Position(from,from.getRightNeighbour(),0.f);			
			}
		}else {
			for (Iterator<Road> iterator = roads.iterator(); iterator.hasNext();) {
				Road road = iterator.next();
				Vertice v=null;
				if(road.getFrom().equals(from))
					v=road.getTo();
				else if(road.getTo().equals(from))
					v=road.getFrom();
				
				if(v!=null && !v.equals(from.getLeftNeighbour()) && !v.equals(from.getRightNeighbour())) {
					return new Position(from,v,0.f);					
				}
				
			}
		}
		return position;
	}
	
	private static void updateDistance(List<Road> roads, Vertice v, Vertice n, List<Position> positionsToAvoid) {
		float w = Course.getDistance(roads, v, n, positionsToAvoid);
		if(n.getWeight() > v.getWeight() + w) {
			n.setWeight(v.getWeight() + w);
			n.setPredecessor(v);
		}
		
	}







	private static Vertice findMin(List<Vertice> vertices) {
		float min = Float.MAX_VALUE;
		Vertice v = null;
		for (Iterator<Vertice> iterator = vertices.iterator(); iterator.hasNext();) {
			Vertice vertice = iterator.next();
			if(vertice.getWeight()<min) {
				v=vertice;
				min = vertice.getWeight();
			}
		}
		
		return v;
	}





	private static void initDijkstra(List<Vertice> vertices, Vertice to) {
		for (Iterator<Vertice> iterator = vertices.iterator(); iterator.hasNext();) {
			Vertice vertice = iterator.next();
			if(!vertice.equals(to))
				vertice.setWeight(Float.MAX_VALUE);
			else
				vertice.setWeight(0);
		}
		
	}
	
	public List<Direction> getCourseForOneDestination(Position position, Position destination,  List<Position> positionsToAvoid){
		
		List<Direction> directions = djikstra(position, positionsToAvoid);
		
//		Vertice origin;
		Vertice to = destination.getTo();
		Vertice from = destination.getFrom();
		Vertice s,pred=null;
		if(to.getWeight()<from.getWeight())
			s=to;
		else
			s=from;
//		origin=s;
	
		
		Vertice olds=s;
		
		
		while(!s.equals(position.getTo())) {
			pred=s.getPredecessor();
			if(pred.getLeftNeighbour().equals(s))
				directions.add(0,Direction.LEFT);
			if(pred.getRightNeighbour().equals(s))
				directions.add(0,Direction.RIGHT);
			olds=s;
			s=s.getPredecessor();
		}
		
		
		if(olds.equals(position.getFrom()))
			directions.add(0,Direction.HALF_TURN);
		
		return directions;
		
	}

	private List<Direction> djikstra(Position position, List<Position> positionsToAvoid) {
		initDijkstra(vertices,position.getTo());
		List<Vertice> Q = new ArrayList<>();
		Q.addAll(vertices);
		List<Vertice> neighbours;
		List<Direction> directions = new ArrayList<>();
		while(!Q.isEmpty()) {
			Vertice v = findMin(Q);
			Q.remove(v);
			neighbours = v.getNeighbours();
			for (Iterator<Vertice> iterator = neighbours.iterator(); iterator.hasNext();) {
				Vertice n = iterator.next();
				updateDistance(roads,v,n,positionsToAvoid);
			}
		}
		return directions;
	}

	public static Course compet1() {
		List<Vertice> vertices = new ArrayList<>();
		Vertice a1 = new Vertice("a1");
		Vertice a2 = new Vertice("a2");
		Vertice a3 = new Vertice("a3");
		Vertice b1 = new Vertice("b1");
		Vertice b2 = new Vertice("b2");
		Vertice b3 = new Vertice("b3");
		Vertice c1 = new Vertice("c1");
		Vertice c2 = new Vertice("c2");
		Vertice c3 = new Vertice("c3");
		Vertice d1 = new Vertice("d1");
		Vertice d2 = new Vertice("d2");
		Vertice d3 = new Vertice("d3");
		Vertice e1 = new Vertice("e1");
		Vertice e2 = new Vertice("e2");
		Vertice e3 = new Vertice("e3");
		Vertice f1 = new Vertice("f1");
		Vertice f2 = new Vertice("f2");
		Vertice f3 = new Vertice("f3");
		Vertice g1 = new Vertice("g1");
		Vertice g2 = new Vertice("g2");
		Vertice g3 = new Vertice("g3");
		Vertice h1 = new Vertice("h1");
		Vertice h2 = new Vertice("h2");
		Vertice h3 = new Vertice("h3");
		
		a1.setLeftNeighbour(a2);
		a1.setRightNeighbour(a3);
		a1.setBackwardNeighbour(b1);

		a2.setLeftNeighbour(a3);
		a2.setRightNeighbour(a1);
		a2.setBackwardNeighbour(e1);
		
		a3.setLeftNeighbour(a1);
		a3.setRightNeighbour(a2);
		a3.setBackwardNeighbour(b3);
		
		b1.setLeftNeighbour(b3);
		b1.setRightNeighbour(b2);
		b1.setBackwardNeighbour(a1);
		
		b2.setLeftNeighbour(b1);
		b2.setRightNeighbour(b3);
		b2.setBackwardNeighbour(c1);
		
		b3.setLeftNeighbour(b2);
		b3.setRightNeighbour(b1);
		b3.setBackwardNeighbour(a3);
		
		c1.setLeftNeighbour(c2);
		c1.setRightNeighbour(c3);
		c1.setBackwardNeighbour(b2);
		
		c2.setLeftNeighbour(c3);
		c2.setRightNeighbour(c1);
		c2.setBackwardNeighbour(d1);
		
		c3.setLeftNeighbour(c1);
		c3.setRightNeighbour(c2);
		c3.setBackwardNeighbour(f1);
		
		d1.setLeftNeighbour(d2);
		d1.setRightNeighbour(d3);
		d1.setBackwardNeighbour(c2);
		
		d2.setLeftNeighbour(d3);
		d2.setRightNeighbour(d1);
		d2.setBackwardNeighbour(e2);
		
		d3.setLeftNeighbour(d1);
		d3.setRightNeighbour(d2);
		d3.setBackwardNeighbour(g1);
		
		e1.setLeftNeighbour(e3);
		e1.setRightNeighbour(e2);
		e1.setBackwardNeighbour(a2);
		
		e2.setLeftNeighbour(e1);
		e2.setRightNeighbour(e3);
		e2.setBackwardNeighbour(d2);
		
		e3.setLeftNeighbour(e2);
		e3.setRightNeighbour(e1);
		e3.setBackwardNeighbour(h3);
		
		f1.setLeftNeighbour(f2);
		f1.setRightNeighbour(f3);
		f1.setBackwardNeighbour(c3);
		
		f2.setLeftNeighbour(f3);
		f2.setRightNeighbour(f1);
		f2.setBackwardNeighbour(g2);
		
		f3.setLeftNeighbour(f1);
		f3.setRightNeighbour(f2);
		f3.setBackwardNeighbour(h2);
		
		g1.setLeftNeighbour(g3);
		g1.setRightNeighbour(g2);
		g1.setBackwardNeighbour(d3);

		g2.setLeftNeighbour(g1);
		g2.setRightNeighbour(g3);
		g2.setBackwardNeighbour(f2);
		
		g3.setLeftNeighbour(g2);
		g3.setRightNeighbour(g1);
		g3.setBackwardNeighbour(h1);
		
		h1.setLeftNeighbour(h3);
		h1.setRightNeighbour(h2);
		h1.setBackwardNeighbour(g3);
		
		h2.setLeftNeighbour(h1);
		h2.setRightNeighbour(h3);
		h2.setBackwardNeighbour(f3);
		
		h3.setLeftNeighbour(h2);
		h3.setRightNeighbour(h1);
		h3.setBackwardNeighbour(e3);
		
		vertices.add(a1);
		vertices.add(a2);
		vertices.add(a3);
		vertices.add(b1);
		vertices.add(b2);
		vertices.add(b3);
		vertices.add(c1);
		vertices.add(c2);
		vertices.add(c3);
		vertices.add(d1);
		vertices.add(d2);
		vertices.add(d3);
		vertices.add(e1);
		vertices.add(e2);
		vertices.add(e3);
		vertices.add(f1);
		vertices.add(f2);
		vertices.add(f3);
		vertices.add(g1);
		vertices.add(g2);
		vertices.add(g3);
		vertices.add(h1);
		vertices.add(h2);
		vertices.add(h3);
		
		
		
		List<Road> roads = new ArrayList<>();
		
		
		Road a1a2 = new Road(a1,a2,25.5f);
		Road a1a3 = new Road(a1,a3,18.5f);
		Road a3a2 = new Road(a3,a2,18.5f);
		Road a1b1 = new Road(a1,b1,70.f);
		Road b2b1 = new Road(b2,b1,18.5f);
		Road b1b3 = new Road(b1,b3,18.5f);
		Road b2b3 = new Road(b2,b3,25.5f);
		Road b2c1 = new Road(b2,c1,120.f);
		Road b3a3 = new Road(b3,a3,70.f);
		Road a2e1 = new Road(a2,e1,170.f);
		Road c1c2 = new Road(c1,c2,25.5f);
		Road c2d1 = new Road(c2,d1,20f);
		Road d1d2 = new Road(d1,d2,25.5f);
		Road d2e2 = new Road(d2,e2,70.f);
		Road e2e1 = new Road(e2,e1,18.5f);
		Road c1c3 = new Road(c1,c3,18.5f);
		Road c3c2 = new Road(c3,c2,18.5f);
		Road d1d3 = new Road(d1,d3,18.5f);
		Road d3d2 = new Road(d3,d2,18.5f);
		Road e2e3 = new Road(e2,e3,18.5f);
		Road e3e1 = new Road(e3,e1,25.5f);
		Road c3f1 = new Road(c3,f1,20f);
		Road d3g1 = new Road(d3,g1,20f);
		Road f1f2 = new Road(f1,f2,18.5f);
		Road f2g2 = new Road(f2,g2,20f);
		Road g1g2 = new Road(g1,g2,18.5f);
		Road g1g3 = new Road(g1,g3,25.5f);
		Road g2g3 = new Road(g2,g3,18.5f);
		Road f1f3 = new Road(f1,f3,25.5f);
		Road f3f2 = new Road(f3,f2,18.5f);
		Road f3h2 = new Road(f3,h2,70f);
		Road g3h1 = new Road(g3,h1,20f);
		Road h2h1 = new Road(h2,h1,18.5f);
		Road h1h3 = new Road(h1,h3,18.5f);
		Road h2h3 = new Road(h2,h3,25.5f);
		Road h3e3 = new Road(h3,e3,170f);
				
		roads.add(a1a2);
		roads.add(a1a3);
		roads.add(a3a2);
		roads.add(a1b1);
		roads.add(b2b1);
		roads.add(b1b3);
		roads.add(b2b3);
		roads.add(b2c1);
		roads.add(b3a3);
		roads.add(a2e1);
		roads.add(c1c2);
		roads.add(c2d1);
		roads.add(d1d2);
		roads.add(d2e2);
		roads.add(e2e1);
		roads.add(c1c3);
		roads.add(c3c2);
		roads.add(d1d3);
		roads.add(d3d2);
		roads.add(e2e3);
		roads.add(e3e1);
		roads.add(c3f1);
		roads.add(d3g1);
		roads.add(f1f2);
		roads.add(f2g2);
		roads.add(g1g2);
		roads.add(g1g3);
		roads.add(g2g3);
		roads.add(f1f3);
		roads.add(f3f2);
		roads.add(f3h2);
		roads.add(g3h1);
		roads.add(h2h1);
		roads.add(h1h3);
		roads.add(h2h3);
		roads.add(h3e3);
		
		Position hospital1 = new Position(e3,h3,0.f);
		Position hospital2 = new Position(a3,b3,0.f);
		
		List<Position> hospitals = new ArrayList<>();
		hospitals.add(hospital1);
		hospitals.add(hospital2);
		
		Position victim1 = new Position(a2,e1,0.f);
		Position victim2 = new Position(f3,h2,0.f);
		Position victim3 = new Position(d2,e2,0.f);
		
		List<Position> victims = new ArrayList<>();
		victims.add(victim3);
		victims.add(victim2);
		victims.add(victim1);
		
		
		return new Course(vertices,roads, victims, hospitals);
		
	}
	
	
	public static Course compet2() {
		List<Vertice> vertices = new ArrayList<>();
		Vertice a1 = new Vertice("a1");
		Vertice a2 = new Vertice("a2");
		Vertice a3 = new Vertice("a3");
		Vertice b1 = new Vertice("b1");
		Vertice b2 = new Vertice("b2");
		Vertice b3 = new Vertice("b3");
		Vertice c1 = new Vertice("c1");
		Vertice c2 = new Vertice("c2");
		Vertice c3 = new Vertice("c3");
		Vertice d1 = new Vertice("d1");
		Vertice d2 = new Vertice("d2");
		Vertice d3 = new Vertice("d3");
		Vertice e1 = new Vertice("e1");
		Vertice e2 = new Vertice("e2");
		Vertice e3 = new Vertice("e3");
		Vertice f1 = new Vertice("f1");
		Vertice f2 = new Vertice("f2");
		Vertice f3 = new Vertice("f3");
		Vertice g1 = new Vertice("g1");
		Vertice g2 = new Vertice("g2");
		Vertice g3 = new Vertice("g3");
		Vertice h1 = new Vertice("h1");
		Vertice h2 = new Vertice("h2");
		Vertice h3 = new Vertice("h3");
		
		a1.setLeftNeighbour(a2);
		a1.setRightNeighbour(a3);
		a1.setBackwardNeighbour(b1);

		a2.setLeftNeighbour(a3);
		a2.setRightNeighbour(a1);
		a2.setBackwardNeighbour(h3);
		
		a3.setLeftNeighbour(a1);
		a3.setRightNeighbour(a2);
		a3.setBackwardNeighbour(c1);
		
		b1.setLeftNeighbour(b2);
		b1.setRightNeighbour(b3);
		b1.setBackwardNeighbour(a1);
		
		b2.setLeftNeighbour(b3);
		b2.setRightNeighbour(b1);
		b2.setBackwardNeighbour(c2);
		
		b3.setLeftNeighbour(b1);
		b3.setRightNeighbour(b2);
		b3.setBackwardNeighbour(d1);
		
		c1.setLeftNeighbour(c3);
		c1.setRightNeighbour(c2);
		c1.setBackwardNeighbour(a3);
		
		c2.setLeftNeighbour(c1);
		c2.setRightNeighbour(c3);
		c2.setBackwardNeighbour(b2);
		
		c3.setLeftNeighbour(c2);
		c3.setRightNeighbour(c1);
		c3.setBackwardNeighbour(e1);
		
		d1.setLeftNeighbour(d2);
		d1.setRightNeighbour(d3);
		d1.setBackwardNeighbour(b3);
		
		d2.setLeftNeighbour(d3);
		d2.setRightNeighbour(d1);
		d2.setBackwardNeighbour(f1);
		
		d3.setLeftNeighbour(d1);
		d3.setRightNeighbour(d2);
		d3.setBackwardNeighbour(f2);
		
		e1.setLeftNeighbour(e2);
		e1.setRightNeighbour(e3);
		e1.setBackwardNeighbour(c3);
		
		e2.setLeftNeighbour(e3);
		e2.setRightNeighbour(e1);
		e2.setBackwardNeighbour(h1);
		
		e3.setLeftNeighbour(e1);
		e3.setRightNeighbour(e2);
		e3.setBackwardNeighbour(g1);
		
		f1.setLeftNeighbour(f3);
		f1.setRightNeighbour(f2);
		f1.setBackwardNeighbour(d2);
		
		f2.setLeftNeighbour(f1);
		f2.setRightNeighbour(f3);
		f2.setBackwardNeighbour(d3);
		
		f3.setLeftNeighbour(f2);
		f3.setRightNeighbour(f1);
		f3.setBackwardNeighbour(g2);
		
		g1.setLeftNeighbour(g3);
		g1.setRightNeighbour(g2);
		g1.setBackwardNeighbour(e3);

		g2.setLeftNeighbour(g1);
		g2.setRightNeighbour(g3);
		g2.setBackwardNeighbour(f3);
		
		g3.setLeftNeighbour(g2);
		g3.setRightNeighbour(g1);
		g3.setBackwardNeighbour(h2);
		
		h1.setLeftNeighbour(h3);
		h1.setRightNeighbour(h2);
		h1.setBackwardNeighbour(e2);
		
		h2.setLeftNeighbour(h1);
		h2.setRightNeighbour(h3);
		h2.setBackwardNeighbour(g3);
		
		h3.setLeftNeighbour(h2);
		h3.setRightNeighbour(h1);
		h3.setBackwardNeighbour(a2);
		
		vertices.add(a1);
		vertices.add(a2);
		vertices.add(a3);
		vertices.add(b1);
		vertices.add(b2);
		vertices.add(b3);
		vertices.add(c1);
		vertices.add(c2);
		vertices.add(c3);
		vertices.add(d1);
		vertices.add(d2);
		vertices.add(d3);
		vertices.add(e1);
		vertices.add(e2);
		vertices.add(e3);
		vertices.add(f1);
		vertices.add(f2);
		vertices.add(f3);
		vertices.add(g1);
		vertices.add(g2);
		vertices.add(g3);
		vertices.add(h1);
		vertices.add(h2);
		vertices.add(h3);
		
		
		
		List<Road> roads = new ArrayList<>();
		
		
		Road a1a2 = new Road(a1,a2,25.5f);
		Road a1a3 = new Road(a1,a3,18.5f);
		Road a3a2 = new Road(a3,a2,18.5f);
		
		Road b1b2 = new Road(b1,b2,18.5f);
		Road b1b3 = new Road(b1,b3,25.5f);
		Road b3b2 = new Road(b3,b2,18.5f);
		
		Road c1c2 = new Road(c1,c2,18.5f);
		Road c1c3 = new Road(c1,c3,25.5f);
		Road c3c2 = new Road(c3,c2,18.5f);
		
		Road d1d2 = new Road(d1,d2,18.5f);
		Road d1d3 = new Road(d1,d3,25.5f);
		Road d3d2 = new Road(d3,d2,18.5f);
		
		Road e1e2 = new Road(e1,e2,18.5f);
		Road e1e3 = new Road(e1,e3,25.5f);
		Road e3e2 = new Road(e3,e2,18.5f);
		
		Road f1f2 = new Road(f1,f2,18.5f);
		Road f1f3 = new Road(f1,f3,18.5f);
		Road f3f2 = new Road(f3,f2,25.5f);
		
		Road g1g2 = new Road(g1,g2,18.5f);
		Road g1g3 = new Road(g1,g3,18.5f);
		Road g3g2 = new Road(g3,g2,25.5f);
		
		Road h1h2 = new Road(h1,h2,18.5f);
		Road h1h3 = new Road(h1,h3,18.5f);
		Road h3h2 = new Road(h3,h2,25.5f);
		
		
		Road a1b1 = new Road(a1,b1,	120.f);
		
		Road b3d1 = new Road(b3,d1,	20.f);
		
		Road d3f2 = new Road(d3,f2,	70.f);
		Road d2f1 = new Road(d2,f1,	70.f);
		Road b2c2 = new Road(b2,c2,	70.f);
		
		Road a3c1 = new Road(a3,c1,	20.f);
		Road c3e1 = new Road(c3,e1,	20.f);
		Road e3g1 = new Road(e3,g1,	20.f);
		Road f3g2 = new Road(f3,g2,	20.f);
		Road g3h2 = new Road(g3,h2,	20.f);
		Road e2h1 = new Road(e2,h1,	70.f);
		Road a2h3 = new Road(a2,h3,	270.f);
		
		
	
		roads.add(a1a2);
		roads.add(a1a3);
		roads.add(a3a2);
		roads.add(b1b2);
		roads.add(b1b3);
		roads.add(b3b2);
		roads.add(c1c2);
		roads.add(c1c3);
		roads.add(c3c2);
		roads.add(d1d2);
		roads.add(d1d3);
		roads.add(d3d2);
		roads.add(e1e2);
		roads.add(e1e3);
		roads.add(e3e2);
		roads.add(f1f2);
		roads.add(f1f3);
		roads.add(f3f2);
		roads.add(g1g2);
		roads.add(g1g3);
		roads.add(g3g2);
		roads.add(h1h2);
		roads.add(h1h3);
		roads.add(h3h2);
		roads.add(a1b1);
		roads.add(b3d1);
		roads.add(d3f2);
		roads.add(d2f1);
		roads.add(b2c2);
		roads.add(a3c1);
		roads.add(c3e1);
		roads.add(e3g1);
		roads.add(f3g2);
		roads.add(g3h2);
		roads.add(e2h1);
		roads.add(a2h3);
			
		Position hospital1 = new Position(b2,c2,0.f);
		
		List<Position> hospitals = new ArrayList<>();
		hospitals.add(hospital1);
		
		Position victim1 = new Position(e2,h1,0.f);
		Position victim2 = new Position(a1,b1,0.f);
		
		List<Position> victims = new ArrayList<>();
		victims.add(victim1);
		victims.add(victim2);
		
		
		return new Course(vertices,roads, victims, hospitals);
		
	}

	public void inversePosition(Position position) {
		float newDistance = Course.getDistance(getRoads(),position.getFrom(),position.getTo(),null)-position.getDistanceFrom();
		position.setDistanceFrom(newDistance);
		Vertice from = position.getFrom();
		Vertice to = position.getTo();
		position.setFrom(to);
		position.setTo(from);
	}
	
	
}
