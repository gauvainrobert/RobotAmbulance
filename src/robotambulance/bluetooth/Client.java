package robotambulance.bluetooth;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import robotambulance.course.Course;
import robotambulance.course.Position;
import robotambulance.course.Vertice;
import robotambulance.util.Direction;
import robotambulance.util.Semaphore;

public class Client {

	private BTConnection connection = Bluetooth.waitForConnection();
	private DataInputStream inputStream = connection.openDataInputStream();
	private DataOutputStream outputStream = connection.openDataOutputStream();
	private Semaphore semOut = new Semaphore(1);
	
	public void sendPosition(Position position) {
		semOut.acquire();
		try {
			outputStream.writeUTF("position");
			outputStream.flush();
			outputStream.writeUTF(position.getFrom().getName());
			outputStream.flush();
			outputStream.writeUTF(position.getTo().getName());
			outputStream.flush();
			outputStream.writeUTF(""+position.getDistanceFrom());
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		semOut.release();
		
	}
	
	public List<Direction> getDirections() {
		List<Direction> dirs = new ArrayList<>();
		Direction direction1,direction2;
		String s1=null;
		String s2=null;
		semOut.acquire();
		try {
			outputStream.writeUTF("direction");
			outputStream.flush();
			s1=inputStream.readUTF();
			s2=inputStream.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		semOut.release();
		
		direction1 = stringToDirection(s1);
		direction2 = stringToDirection(s2);
		if(direction1!=null)
			dirs.add(direction1);
		if(direction2!=null)
			dirs.add(direction2);
		
		return dirs;
	}
	
	private Direction stringToDirection(String s) {
		
		if(s.equals("LEFT")) {
			return Direction.LEFT;
			
		}else if(s.equals("RIGHT")) {
			return Direction.RIGHT;
		}else if(s.equals("HALF_TURN")) {
			return Direction.HALF_TURN;
		}else{
			
			return null;
		}
		
		
	}

	public void closeConnection() {
		connection.close();		
	}

	public Position receivePosition(Course course) throws IOException {
		Position position = new Position(new Vertice(""),new Vertice(""),0);
		String v1,v2;
		float distanceFrom;
		v1 = inputStream.readUTF();
		v2 = inputStream.readUTF();
		distanceFrom = new Float(inputStream.readUTF());
		
		Vertice from = null;
		Vertice to = null;
		
		position.setFrom(new Vertice(v1));
		position.setTo(new Vertice(v2));
		position.setDistanceFrom(distanceFrom);
		
		for (Iterator<Vertice> iterator = course.getVertices().iterator(); iterator.hasNext() && (from ==null || to == null);) {
			Vertice v = iterator.next();
			if(v.equals(position.getFrom())) {
				position.setFrom(v);				
			}
			if(v.equals(position.getTo())) {
				position.setTo(v);				
			}
		}
		
		
		
		return position;
	}
	
	

}
