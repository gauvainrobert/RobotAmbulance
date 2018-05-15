package robotambulance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import robotambulance.bluetooth.Client;
import robotambulance.course.Course;
import robotambulance.course.Position;
import robotambulance.course.Road;
import robotambulance.course.Vertice;
import robotambulance.util.Constants;
import robotambulance.util.Direction;

public class Main {

	
	public static Course getCourses(List<Vertice> vertices, List<Road> roads, Position position, List<Position> victims,
			List<Position> hospitals) {
		Course course = null;
		List<Direction> directions = new ArrayList<>();
		
		
		
		for (Iterator<Position> iterator = victims.iterator(); iterator.hasNext();) {
			Position victim = iterator.next();
			LCD.drawString(position.getTo().getName(),0,2);
			LCD.drawString(victim.getTo().getName(),3,2);
			//Button.waitForAnyPress();
			
			directions.addAll(getCourseForOneVictim(vertices, roads, position, victim, hospitals));
			
		}
		
		course = new Course(vertices,roads,position,directions);
		
		int i=0;
		
		for (Iterator<Direction> iterator = directions.iterator(); iterator.hasNext();) {
			Direction side = iterator.next();
			if(side==Direction.LEFT) {
				LCD.drawString("L", i%10, i/10);
			} else if(side == Direction.RIGHT){
				LCD.drawString("R", i%10, i/10);		
			}else {
				LCD.drawString("H", i%10, i/10);
			}
				
				
			i++;
		}
		//Button.waitForAnyPress();
		
		return course;
	}
	
	private static List<Direction> getCourseForOneVictim(List<Vertice> vertices, List<Road> roads, Position position, Position victim, List<Position> hospitals){
		
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
				updateDistance(roads,v,n);
			}
		}
		
		Vertice origin;
		Vertice to = victim.getTo();
		Vertice from = victim.getFrom();
		Vertice s,pred=null;
		if(to.getWeight()<from.getWeight())
			s=to;
		else
			s=from;
		origin=s;
	
		
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
		
		List<Direction> toClosestHospital = getClosestHospital(vertices, roads, origin, hospitals, position);
		directions.addAll(toClosestHospital);
		
		return directions;
		
	}
	
	private static List<Direction> getClosestHospital(List<Vertice> vertices, List<Road> roads, Vertice s, List<Position> hospitals, Position newPosition) {
		Vertice pred;
		Vertice from = s.getBackwardNeighbour();
		Vertice i;
		LCD.drawString(from.getName(), 5, 7);
		initDijkstra(vertices,from);
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
				updateDistance(roads,v,n);
			}
		}
		
		Vertice hospital = findHospital(hospitals);
		LCD.drawString(hospital.getName(), 10, 7);
		i=hospital;
		while(!i.equals(from)) {
			pred=i.getPredecessor();
			if(pred.getLeftNeighbour().equals(i))
				directions.add(0,Direction.LEFT);
			if(pred.getRightNeighbour().equals(i))
				directions.add(0,Direction.RIGHT);
			
			i=i.getPredecessor();
		}
		
		if(from.getPredecessor().equals(from.getBackwardNeighbour()))
			directions.add(0,Direction.HALF_TURN);
		LCD.drawString("OK", 0, 7);
		
		newPosition.setFrom(hospital);
		newPosition.setTo(hospital.getBackwardNeighbour());
		newPosition.setDistanceFrom(Course.getDistance(roads, hospital, hospital.getBackwardNeighbour()));
		
		return directions;
		
	}
	
	private static void updateDistance(List<Road> roads, Vertice v, Vertice n) {
		float w = Course.getDistance(roads, v, n);
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

	private static Vertice findHospital(List<Position> hospitals) {
		Vertice h = null;
		
		for (Iterator<Position> iterator = hospitals.iterator(); iterator.hasNext();) {
			Position position = iterator.next();
			if(h==null || position.getFrom().getWeight()<h.getWeight())
				h=position.getFrom();
			if(h==null || position.getTo().getWeight()<h.getWeight())
				h=position.getTo();
			
		}
		
		return h;
	}
	
	public static void main(String[] args) {	
		
		
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
		
		Position position = new Position(b2,c1,15.f);
		
		Position hospital1 = new Position(f3,h2,0.f);
		
		List<Position> hospitals = new ArrayList<>();
		hospitals.add(hospital1);
		
		Position victim1 = new Position(e2,d2,0.f);
		Position victim2 = new Position(a1,b1,0.f);
		
		List<Position> victims = new ArrayList<>();
		victims.add(victim1);
		victims.add(victim2);
		
		
					
			
	
		
		Course course = getCourses(vertices, roads, position, victims, hospitals);
		
		
		PID pid = new PID(course);
		
		
		Client client = new Client();
		
		
		Button.waitForAnyPress();
		client.sendPosition(position);
		
		client.closeConnection();
		Button.waitForAnyPress();
		
		int integral = 0;
		int lv;
		int error;
		int Turn;
		int powerG;
		int powerD;
		int lastError = 0;
		int derivative;
		long start,stop;
		float distance;
		boolean intersection = false;
		float distanceToTravel;
		Direction oldside = null;
		int cptline = 0;	
		boolean end = pid.getCourse().getDirections().isEmpty();
		boolean crossline = false;
		boolean b;
		
		boolean travelLastRoad = false;
		boolean halfturn=false;
		while(!end) {
			
			if(!intersection) {
				oldside=pid.getSide();
				pid.setSide(pid.getCourse().getDirections().get(0));
				LCD.drawString(pid.getSide().toString(), 0, 3);
				pid.getCourse().getDirections().remove(0);
				try {
					if(oldside!=pid.getSide()) {
						lastError=0;
						integral=0;
						if(pid.getSide()==Direction.LEFT) {
							pid.putOnLeftSide();					
						}else if(pid.getSide()==Direction.RIGHT){
							pid.putOnRightSide();	
						}else {
							halfturn=true;
							pid.setSide(oldside);
						}
					}
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
			distanceToTravel = Course.getDistance(course.getRoads(),position.getFrom(),position.getTo())-position.getDistanceFrom();

			LCD.drawString(""+distanceToTravel, 0, 0);
			distance = 0.f;
			
			while(distance<distanceToTravel) {
			
				start = System.currentTimeMillis();
				lv = pid.getLightSensor().getLightValue();
				error = lv - Constants.offset;
				
				
				integral = integral + error;
				if(error==0)
					integral=0;
				LCD.drawInt(integral, 0, 6);
				
				derivative = error - lastError;
				Turn  = Constants.Kp*error + Constants.Ki*integral + Constants.Kd*derivative;
				Turn = Turn / 100;
				
				if(pid.getSide()==Direction.LEFT) {
					powerG = Constants.Tp - Turn;
					powerD = Constants.Tp + Turn;
				}else {
					powerG = Constants.Tp + Turn;
					powerD = Constants.Tp - Turn;
				}
				
				if(powerG<-Constants.Tp)
					powerG=Constants.Tp;
				if(powerG>Constants.Tp)
					powerG=Constants.Tp;
				if(powerD<-Constants.Tp)
					powerD=Constants.Tp;
				if(powerD>Constants.Tp)
					powerD=Constants.Tp;
				
				
				if(powerG < 0) {
					pid.getMotorG().setSpeed(-powerG);
					pid.getMotorG().backward();
				}else {
					pid.getMotorG().setSpeed(powerG);
					pid.getMotorG().forward();
				}
				if(powerD < 0) {
					pid.getMotorD().setSpeed(-powerD);
					pid.getMotorD().backward();
				}else {
					pid.getMotorD().setSpeed(powerD);
					pid.getMotorD().forward();
				}
				lastError = error;
				
				
				// sendPosition
				
				try {
					Thread.sleep(Constants.DELAY_LIGHTSENSOR);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					ie.printStackTrace();
				}
				
				
				stop = System.currentTimeMillis();
				distance += (((float)powerG+powerD)/720.f)*5.6f*Math.PI*(stop-start)/1000;
				
				if((b=(integral>=Constants.Kline || integral<=-Constants.Kline)) && !crossline) {
					crossline = true;
					// get Direction()
					if(!intersection && distanceToTravel>Constants.distanceRiskOfLine && distanceToTravel-Constants.distanceRiskOfLine<distance) {
						distance=distanceToTravel;
						cptline++;
						LCD.drawString("line"+cptline, 0, 7);
					}
					
				}
				
				if(!b) {
					crossline=false;
				}
				
				
			}
			
			
			if(halfturn) {
				halfturn=false;
				pid.halfTurn();			
			}else {
				intersection = !intersection;	
			}
			
			
			
			
			position = course.findPosition(pid.getSide(),position.getTo(),intersection);
			
			end = course.getDirections().isEmpty() && !intersection;
			
			if(end && !travelLastRoad) {
				end=false;
				pid.getCourse().getDirections().add(oldside);
				travelLastRoad=true;
			}
			
			
		}
		pid.getMotorG().stop();
		pid.getMotorD().stop();
		
		Button.waitForAnyPress();
		
	}
}
