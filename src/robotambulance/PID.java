package robotambulance;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PIDController;
import robotambulance.util.Constants;
import robotambulance.util.Direction;

public class PID {
	
	public LightSensor getLightSensorL() {
		return lightSensorL;
	}









	public LightSensor getLightSensorR() {
		return lightSensorR;
	}






















	private LightSensor lightSensorL = new LightSensor(SensorPort.S2);
	private LightSensor lightSensorR = new LightSensor(SensorPort.S1);
	private NXTRegulatedMotor motorG = Motor.C;
	private NXTRegulatedMotor motorD = Motor.B;
	private DifferentialPilot pilot = new DifferentialPilot(56, 112, Motor.C, Motor.B);
	
	private Direction side = null;
	
	public PID() {
		super();
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









	


	public void forward() throws InterruptedException {
		
		pilot.setTravelSpeed(Constants.Tp);
		pilot.setRotateSpeed(1000);
		boolean left = false;
		boolean right = false;
		boolean end = false;
		
		long timeleft = 0;
		long timeright = 0;
		
		while(!end) {
			pilot.forward();
			while(!end && pilot.isMoving()) {
				Thread.sleep(Constants.DELAY_LIGHTSENSOR);
				if(lightSensorL.getLightValue()>Constants.offsetSimpleLine) {
					timeleft = System.currentTimeMillis();
				}
				
				if(lightSensorR.getLightValue()>Constants.offsetSimpleLine) {
					timeright = System.currentTimeMillis();
				}
				
				if(timeleft>2*Constants.DELAY_LIGHTSENSOR) {
					rotateLeft();
				}
				
				if(timeright>2*Constants.DELAY_LIGHTSENSOR) {
					rotateRight();
				}
				
				if(timeleft!=0 && timeright != 0 && Math.abs(timeleft-timeright)<2*Constants.DELAY_LIGHTSENSOR) {
					pilot.stop();
					Sound.twoBeeps();
					end=true;
				}
				
			}
		}
	}












	private void replace() throws InterruptedException {
		if(lightSensorL.getLightValue()>Constants.offset) {
			rotateLeft();
		}
		if(lightSensorR.getLightValue()>Constants.offset) {
			rotateRight();
		}		
	}









	private void rotateLeft() throws InterruptedException {
		while(lightSensorL.getLightValue()>Constants.offset) {
			pilot.rotate(2,true);
		}
		
	}

	private void rotateRight() throws InterruptedException {
		while(lightSensorR.getLightValue()>Constants.offset) {
			pilot.rotate(-2, true);
		}
		
	}

	private void sleepLightSensor() throws InterruptedException {
		Thread.sleep(Constants.DELAY_LIGHTSENSOR);
	}





	







	







//	public void putOnRightSide() throws InterruptedException {
//		motorD.stop();
//		motorG.setSpeed(300);
//		motorG.forward();
//		while(lightSensor.getLightValue()-Constants.offset<0) {
//			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
//		}
//		while(lightSensor.getLightValue()-Constants.offset>=0) {
//			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
//		}
//		motorG.stop();
//		
//	}
//
//
//
//
//
//
//
//	public void putOnLeftSide() throws InterruptedException {
//		motorG.stop();
//		motorD.setSpeed(300);
//		motorD.forward();
//		while(lightSensor.getLightValue()-Constants.offset<0) {
//			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
//		}
//		while(lightSensor.getLightValue()-Constants.offset>=0) {
//			Thread.sleep(Constants.DELAY_LIGHTSENSOR);			
//		}
//		motorD.stop();
//		
//	}


//	public void changeSide(Direction side) throws InterruptedException {
//		this.side=side;
//		if(side==Direction.LEFT) {
//			putOnLeftSide();
//		}else if(side==Direction.RIGHT) {
//			putOnRightSide();			
//		}
//		
//		
//		
//	}




	public void travel(float distanceToTravel, Direction side) {
		float distance = 0.f;
		long start,stop;
		int lvL,lvR;
		int errorL,errorR,lastErrorL,lastErrorR,derivativeL,derivativeR,TurnL,TurnR,powerG,powerD;
		boolean crossline=false;
		boolean b;
		boolean intersection = false;
		
		int Tp = 300;
		int Kp = 1000;
		int Ki = 30;
		int Kd = 0;
		
		long timeleft = 0;
		long timeright = 0;
		
		int error;
		int integralL = 0;
		int integralR = 0;
		lastErrorL=0;
		lastErrorR=0;
		
		boolean end = false;
		
		while(!end && distance<distanceToTravel) {
			
			start = System.currentTimeMillis();
			lvL = lightSensorL.getLightValue();
			lvR = lightSensorR.getLightValue();
			
			
			errorL = lvL - Constants.offset;
			
			errorR = lvR - Constants.offset;
			
			integralL += errorL;
			integralR += errorR;
			

			if(errorL==0)
				integralL = 0;
			if(errorR==0)
				integralR = 0;
			
			
			
			
			derivativeL = errorL - lastErrorL;
			TurnL  = Kp*errorL + Ki*integralL + Kd*derivativeL;
			TurnL = TurnL / 100;
			
			derivativeR = errorR - lastErrorR;
			TurnR  = Kp*errorR + Ki*integralR + Kd*derivativeR;
			TurnR = TurnR / 100;
			
		
			if(side==Direction.LEFT) {
				powerG = Tp - TurnL;
				powerD = Tp + TurnL;
				
			} else {
				powerG = Tp + TurnR;
				powerD = Tp - TurnR;
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
			lastErrorL = errorL;
			lastErrorR = errorR;
			
			if(lightSensorL.getLightValue()>Constants.offsetSimpleLine) {
				timeleft = System.currentTimeMillis();
			}
			
			if(lightSensorR.getLightValue()>Constants.offsetSimpleLine) {
				timeright = System.currentTimeMillis();
			}
			
			if(distance > 20.f && timeleft!=0 && timeright != 0 && Math.abs(timeleft-timeright)<2*Constants.DELAY_LIGHTSENSOR) {
				pilot.stop();
				Sound.twoBeeps();
				end=true;
			}
			
			try {
				Thread.sleep(Constants.DELAY_LIGHTSENSOR);
			} catch (InterruptedException ie) {
				// TODO Auto-generated catch block
				ie.printStackTrace();
			}
			
			
			stop = System.currentTimeMillis();
			distance += (((float)powerG+powerD)/720.f)*5.6f*Math.PI*(stop-start)/1000;
//			
//			if((b=(integral>=Constants.Kline || integral<=-Constants.Kline)) && !crossline) {
//				crossline = true;
//				if(!intersection && distanceToTravel>Constants.distanceRiskOfLine && distanceToTravel-Constants.distanceRiskOfLine<distance) {
//					distance=distanceToTravel;
//				}
//				
//			}
//			
//			if(!b) {
//				crossline=false;
//			}
//			
		}
		
		Sound.beep();
		
	}




	public DifferentialPilot getPilot() {
		return pilot;
	}









	public void setPilot(DifferentialPilot pilot) {
		this.pilot = pilot;
	}









	public void travel() {
		float distance = 0.f;
		long start,stop;
		int lvL,lvR;
		int errorL,errorR,lastErrorL,lastErrorR,derivativeL,derivativeR,TurnL,TurnR,powerG,powerD;
		boolean crossline=false;
		boolean b;
		boolean intersection = false;
		
		long timeleft = 0;
		long timeright = 0;
		
		
		int error;
		int integralL = 0;
		int integralR = 0;
		lastErrorL=0;
		lastErrorR=0;
		boolean end = false;
		while(!end) {
			
			start = System.currentTimeMillis();
			lvL = lightSensorL.getLightValue();
			lvR = lightSensorR.getLightValue();
			
			
			errorL = lvL - Constants.offset;
			
			errorR = lvR - Constants.offset;
			
			integralL += errorL;
			integralR += errorR;
			

			if(errorL==0)
				integralL = 0;
			if(errorR==0)
				integralR = 0;
			
			
			
			
			derivativeL = errorL - lastErrorL;
			TurnL  = Constants.Kp*errorL + Constants.Ki*integralL + Constants.Kd*derivativeL;
			TurnL = TurnL / 100;
			
			derivativeR = errorR - lastErrorR;
			TurnR  = Constants.Kp*errorR + Constants.Ki*integralR + Constants.Kd*derivativeR;
			TurnR = TurnR / 100;
			
		
			powerG = Constants.Tp - TurnL;
			powerD = Constants.Tp - TurnR;

			
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
			lastErrorL = errorL;
			lastErrorR = errorR;
			
			
			
			if(lightSensorL.getLightValue()>Constants.offsetSimpleLine) {
				timeleft = System.currentTimeMillis();
			}
			
			if(lightSensorR.getLightValue()>Constants.offsetSimpleLine) {
				timeright = System.currentTimeMillis();
			}
			
			if(timeleft!=0 && timeright != 0 && Math.abs(timeleft-timeright)<2*Constants.DELAY_LIGHTSENSOR) {
				pilot.stop();
				Sound.twoBeeps();
				end=true;
			}
			
			
			
			
			
			try {
				Thread.sleep(Constants.DELAY_LIGHTSENSOR);
			} catch (InterruptedException ie) {
				// TODO Auto-generated catch block
				ie.printStackTrace();
			}
			
			
			
			
		}
		
	}




	public void changeSide(Direction dir) {
		this.side=dir;		
	}









	public void leftTurn() throws InterruptedException {
		pilot.travel(20);
		travel(20.f,Direction.LEFT);
		pilot.travel(40);
		replace();
	}

	public void rightTurn() throws InterruptedException {
		pilot.travel(20);
		travel(20.f,Direction.RIGHT);
		pilot.travel(40);
		replace();
	}

	public void intersectionStraight() {
		pilot.travel(300);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
