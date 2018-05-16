package robotambulance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import robotambulance.bluetooth.Client;
import robotambulance.course.Course;
import robotambulance.course.Position;
import robotambulance.course.Vertice;
import robotambulance.util.Direction;

public class Main {

	
//	public static Course getCourses(List<Vertice> vertices, List<Road> roads, Position position, List<Position> victims,
//			List<Position> hospitals) {
//		Course course = null;
//		List<Direction> directions = new ArrayList<>();
//		
//		
//		
//		for (Iterator<Position> iterator = victims.iterator(); iterator.hasNext();) {
//			Position victim = iterator.next();
//			LCD.drawString(position.getTo().getName(),0,2);
//			LCD.drawString(victim.getTo().getName(),3,2);
//			//Button.waitForAnyPress();
//			
//			directions.addAll(getCourseForOneVictim(vertices, roads, position, victim, hospitals));
//			
//		}
//		
//		course = new Course(vertices,roads,position,directions);
//		
//		int i=0;
//		
//		for (Iterator<Direction> iterator = directions.iterator(); iterator.hasNext();) {
//			Direction side = iterator.next();
//			if(side==Direction.LEFT) {
//				LCD.drawString("L", i%10, i/10);
//			} else if(side == Direction.RIGHT){
//				LCD.drawString("R", i%10, i/10);		
//			}else {
//				LCD.drawString("H", i%10, i/10);
//			}
//				
//				
//			i++;
//		}
//		//Button.waitForAnyPress();
//		
//		return course;
//	}
//	
//	
	
//	private static List<Direction> getClosestHospital(List<Vertice> vertices, List<Road> roads, Vertice s, List<Position> hospitals, Position newPosition) {
//		Vertice pred;
//		Vertice from = s.getBackwardNeighbour();
//		Vertice i;
//		LCD.drawString(from.getName(), 5, 7);
//		initDijkstra(vertices,from);
//		List<Vertice> Q = new ArrayList<>();
//		Q.addAll(vertices);
//		List<Vertice> neighbours;
//		List<Direction> directions = new ArrayList<>();
//		while(!Q.isEmpty()) {
//			Vertice v = findMin(Q);
//			Q.remove(v);
//			neighbours = v.getNeighbours();
//			for (Iterator<Vertice> iterator = neighbours.iterator(); iterator.hasNext();) {
//				Vertice n = iterator.next();
//				updateDistance(roads,v,n);
//			}
//		}
//		
//		Vertice hospital = findHospital(hospitals);
//		LCD.drawString(hospital.getName(), 10, 7);
//		i=hospital;
//		while(!i.equals(from)) {
//			pred=i.getPredecessor();
//			if(pred.getLeftNeighbour().equals(i))
//				directions.add(0,Direction.LEFT);
//			if(pred.getRightNeighbour().equals(i))
//				directions.add(0,Direction.RIGHT);
//			
//			i=i.getPredecessor();
//		}
//		
//		if(from.getPredecessor().equals(from.getBackwardNeighbour()))
//			directions.add(0,Direction.HALF_TURN);
//		LCD.drawString("OK", 0, 7);
//		
//		newPosition.setFrom(hospital);
//		newPosition.setTo(hospital.getBackwardNeighbour());
//		newPosition.setDistanceFrom(Course.getDistance(roads, hospital, hospital.getBackwardNeighbour()));
//		
//		return directions;
//		
//	}
	
	

//	private static Vertice findHospital(List<Position> hospitals) {
//		Vertice h = null;
//		
//		for (Iterator<Position> iterator = hospitals.iterator(); iterator.hasNext();) {
//			Position position = iterator.next();
//			if(h==null || position.getFrom().getWeight()<h.getWeight())
//				h=position.getFrom();
//			if(h==null || position.getTo().getWeight()<h.getWeight())
//				h=position.getTo();
//			
//		}
//		
//		return h;
//	}
	
	public static void main(String[] args) {
			
		
		PID pid = new PID();
		
		Client client = new Client();
		Course course = Course.compet2();
		
		Button.waitForAnyPress();
		
		Position position = null;
		try {
			position = client.receivePosition(course);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		List<Direction> directions = new ArrayList<>();
		directions.addAll(client.getDirections());
		

		float distanceToTravel;
		Direction oldside = null;
		//int cptline = 0;	
		boolean halfturn=false;
		Direction dir;
		Vertice neighbour = null;
		while (!directions.isEmpty()) {
			
			
			dir = headAndRemove(directions);
			LCD.drawString(dir.toString(), 0, 0);
			if(dir==Direction.HALF_TURN) {
				pid.halfTurn();
				course.inversePosition(position);
				halfturn = true;
			}
			
			if (halfturn) {
				dir = headAndRemove(directions);
				halfturn=false;
			}
			
			if(dir!=oldside) {
				try {
					pid.changeSide(dir);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oldside=dir;
			}
			
			
			distanceToTravel = Course.getDistance(course.getRoads(),position.getFrom(),position.getTo())-position.getDistanceFrom();
			if(dir==Direction.LEFT) {
				neighbour = position.getTo().getLeftNeighbour();
			}else if(dir==Direction.RIGHT) {
				neighbour = position.getTo().getRightNeighbour();				
			}
			LCD.drawString(""+distanceToTravel, 0, 1);
			LCD.drawString(""+position.getTo().getName()+" "+neighbour.getName(), 0, 3);
			distanceToTravel += Course.getDistance(course.getRoads(),position.getTo(),neighbour);
			
			LCD.drawString(""+distanceToTravel, 0, 2);
			
			pid.travel(distanceToTravel);
			
			position.setFrom(neighbour);
			position.setTo(neighbour.getBackwardNeighbour());
			position.setDistanceFrom(0);
			client.sendPosition(position);
			
			directions.addAll(client.getDirections());
		}
		pid.getMotorG().stop();
		pid.getMotorD().stop();
		
		Button.waitForAnyPress();
		
		
		
		
	}

	private static Direction headAndRemove(List<Direction> directions) {
		Direction dir = directions.get(0);
		directions.remove(0);
		return dir;
	}
}
