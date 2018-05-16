package robotambulance;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import robotambulance.util.Constants;
import robotambulance.util.Direction;

public class PID {
	
	private LightSensor lightSensor = new LightSensor(SensorPort.S1);
	private NXTRegulatedMotor motorG = Motor.C;
	private NXTRegulatedMotor motorD = Motor.B;
	private DifferentialPilot pilot = new DifferentialPilot(56, 112, Motor.C, Motor.B);
	
	private Direction side = null;
	
	public PID() {
		super();
	}

	





	public LightSensor getLightSensor() {
		return lightSensor;
	}







	public NXTRegulatedMotor getMotorG() {
		return motorG;
	}







	public NXTRegulatedMotor getMotorD() {
		return motorD;
	}



	public Direction getSide() {
		return side;		
	}




	public void setSide(Direction side) {
		this.side = side;
	}







	







	public void halfTurn() {
		pilot.rotate(180);
		pilot.travel(Constants.distanceToTravelAfterHalfTurn);
	}







	







	







	public void putOnRightSide() throws InterruptedException {
		motorD.stop();
		motorG.setSpeed(300);
		motorG.forward();
		while(lightSensor.getLightValue()-Constants.offset<0) {
			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
		}
		while(lightSensor.getLightValue()-Constants.offset>=0) {
			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
		}
		motorG.stop();
		
	}







	public void putOnLeftSide() throws InterruptedException {
		motorG.stop();
		motorD.setSpeed(300);
		motorD.forward();
		while(lightSensor.getLightValue()-Constants.offset<0) {
			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
		}
		while(lightSensor.getLightValue()-Constants.offset>=0) {
			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
		}
		motorD.stop();
		
	}


	public void changeSide(Direction side) throws InterruptedException {
		this.side=side;
		if(side==Direction.LEFT) {
			putOnLeftSide();
		}else if(side==Direction.RIGHT) {
			putOnRightSide();			
		}
		
		
		
	}




	public void travel(float distanceToTravel) {
		float distance = 0.f;
		long start,stop;
		int lv;
		int error,integral,lastError,derivative,Turn,powerG,powerD;
		boolean crossline=false;
		boolean b;
		boolean intersection = false;
		
		integral = 0;
		lastError = 0;
		
		while(distance<distanceToTravel) {
		
			start = System.currentTimeMillis();
			lv = getLightSensor().getLightValue();
			error = lv - Constants.offset;
			
			
			integral = integral + error;
			if(error==0)
				integral=0;
			LCD.drawInt(integral, 0, 6);
			
			derivative = error - lastError;
			Turn  = Constants.Kp*error + Constants.Ki*integral + Constants.Kd*derivative;
			Turn = Turn / 100;
			
			if(getSide()==Direction.LEFT) {
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
				getMotorG().setSpeed(-powerG);
				getMotorG().backward();
			}else {
				getMotorG().setSpeed(powerG);
				getMotorG().forward();
			}
			if(powerD < 0) {
				getMotorD().setSpeed(-powerD);
				getMotorD().backward();
			}else {
				getMotorD().setSpeed(powerD);
				getMotorD().forward();
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
				if(!intersection && distanceToTravel>Constants.distanceRiskOfLine && distanceToTravel-Constants.distanceRiskOfLine<distance) {
					distance=distanceToTravel;
				}
				
			}
			
			if(!b) {
				crossline=false;
			}
			
		}
		
	}




	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
