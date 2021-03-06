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
		
		Position position = null;
		try {
			position = client.receivePosition(course);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		List<Direction> directions = new ArrayList<>();
		directions.addAll(client.getDirections());
		
		//int cptline = 0;	
		boolean halfturn=false;
		Direction dir;
		Vertice neighbour = null;
		while (!directions.isEmpty()) {
			
			
			dir = headAndRemove(directions);
			LCD.drawString(dir.toString(), 0, 0);
			if(dir==Direction.HALF_TURN) {
				try {
					pid.halfTurn();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				course.inversePosition(position);
				halfturn = true;
			}
			
			if (halfturn) {
				dir = headAndRemove(directions);
				halfturn=false;
			}
			
//			if(dir!=oldside) {
//				pid.changeSide(dir);
//				oldside=dir;
//			}
			
			
//			distanceToTravel = Course.getDistance(course.getRoads(),position.getFrom(),position.getTo())-position.getDistanceFrom();
			
			pid.travel();
			
			if(dir==Direction.LEFT) {
				neighbour = position.getTo().getLeftNeighbour();
			}else if(dir==Direction.RIGHT) {
				neighbour = position.getTo().getRightNeighbour();				
			}
			
			position.setFrom(position.getTo());
			position.setTo(neighbour);
			position.setDistanceFrom(0);
			client.sendPosition(position);
			
			
			try {
				intersection(pid, course, position, dir, neighbour);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			position.setFrom(neighbour);
			position.setTo(neighbour.getBackwardNeighbour());
			position.setDistanceFrom(0);
			client.sendPosition(position);
			
			directions.addAll(client.getDirections());
			
			
			
		}
		pid.getMotorG().stop();
		pid.getMotorD().stop();
		
		
		pid.goOut();
		Button.waitForAnyPress();
		
		
	}

	private static void intersection(PID pid, Course course, Position position, Direction dir, Vertice neighbour) throws InterruptedException {
		if(dir==Direction.LEFT) {
			pid.leftTurn();
		}else if(dir==Direction.RIGHT) {
			pid.rightTurn();				
		}
	}

	private static Direction headAndRemove(List<Direction> directions) {
		Direction dir = directions.get(0);
		directions.remove(0);
		return dir;
	}
}
