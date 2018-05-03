package robotambulance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;


public class Main {

	private static NXTRegulatedMotor motorG = Motor.C;
	private static NXTRegulatedMotor motorD = Motor.B;
	
	private static LightSensor lightSensor = new LightSensor(SensorPort.S1);
	private static int initialSpeed = 100;
	private static int initialLightValue;
	private static Side side = Side.RIGHT;
	private static int differenceColor = 50;
	private static float scaleSpeed = 0.5f;
	private static int nbValueRequiredToChangeSide = 2;
	private static int delayLightSensor = 10;
	private static int nbColors = 50;
	private static int[] colors = new int[nbColors];
	private static int iColors = 0;
	private static int cptNbZero = 0;
	private static int nbZero = 15;
	
	public static void main(String[] args) {
		LCD.drawString("poser le robot sur la parti	e droite de la bande blanche", 0, 0);
		File f = new File("trace.txt");
		f.delete();
		f = new File("side.txt");
		f.delete();
		Button.waitForAnyPress();
		initialLightValue = lightSensor.getNormalizedLightValue();
		try {
			initialisationRobot();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		while (true) {
			try {
				goBackRoad();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
			
		}		
	}
	
	private static void initialisationRobot() throws InterruptedException {
		putRobotOnRightSide();
		motorG.setSpeed(initialSpeed);
		motorD.setSpeed(initialSpeed);
		goForward();
	}



	private static void goForward() throws InterruptedException {
		motorG.forward();
		motorD.forward();
		boolean f = true;
		while(f) {
			changeSide();
		}
			
		
		
	}
	
	private static void insertSide(boolean onRoad, long t) {
		FileOutputStream fos = null;
		File f = new File("side.txt");
		char c=0;
		try {
			fos = new FileOutputStream(f,true);
			switch (side) {
				case LEFT : c='G';
				case RIGHT : c='D';
			}
			if (onRoad)
				c='R';
			
			fos.write(c);
			fos.write(',');
			fos.write((int)t);
			fos.write('.');
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	private static void changeSide() throws InterruptedException {
		long t1 = System.currentTimeMillis();
		long t2;
		boolean onRoad=false;
		goBackRoad();
		onRoad=true;
		t2 = System.currentTimeMillis();
		
		insertSide(onRoad,t2-t1);
		goOutTheRoad();
		onRoad=false;
		t1 = System.currentTimeMillis();
		changeValueSide();
		insertSide(onRoad,t1-t2);
	}



	private static void changeValueSide() {
		side=side==Side.RIGHT?Side.LEFT:Side.RIGHT;
		
	}



	private static void putRobotOnRightSide() throws InterruptedException {
		motorG.setSpeed(300);
		motorG.forward();
		while(Math.abs(initialLightValue-lightSensor.getNormalizedLightValue())<differenceColor) {
			Thread.sleep(delayLightSensor);			
		}
		motorG.stop();
		
	}

	private static boolean goOutTheRoad() throws InterruptedException {
		int lv;
		boolean c1=true;
		int cpt=0;
		boolean c2=Math.abs(initialLightValue-(lv=lightSensor.getNormalizedLightValue()))<differenceColor;
		while(c2) {
			c1=Math.abs(initialLightValue-(lv=lightSensor.getNormalizedLightValue()))<differenceColor;
			if (!c1) cpt++;
			if(side==Side.LEFT)
				motorD.setSpeed((int)((float)initialSpeed*scaleSpeed));
			else
				motorG.setSpeed((int)((float)initialSpeed*scaleSpeed));
			Thread.sleep(delayLightSensor);
			c2=cpt<nbValueRequiredToChangeSide;
		}
		if(side==Side.LEFT)
			motorD.setSpeed(initialSpeed);
		else
			motorG.setSpeed(initialSpeed);
		return c1;
	}

	private static void insertColor(int lv) {
		FileOutputStream fos = null;
		File f = new File("trace.txt");
		
		try {
			fos = new FileOutputStream(f,true);
			if (Math.abs(initialLightValue-lv)<differenceColor)
				fos.write('1');
			else
				fos.write('0');
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int v = Math.abs(initialLightValue-lv)<differenceColor?1:0;
		
		if (v==0)
			cptNbZero++;
		if(cptNbZero>nbZero) {
			cptNbZero=0;
			iColors=0;
		}
		if(iColors==nbColors) {
			cptNbZero=0;
			iColors=0;
		}
		colors[iColors]=v;
		iColors++;
		
		int[] patternSquare = {1,1,1,1,1,1,1,1,1};
		int[] patternDoubleLine = {1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1};
		int[] patternDoubleLine2 = {1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,0,1,1,1};
		int[] patternDoubleLine3 = {1,1,1,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1};
		int[] patternDoubleLine4 = {1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1};
		int[] patternLine1 = {1,1,0,0,1,1};
		int[] patternLine2 = {1,1,0,0,0,1};
		int[] patternLine3 = {1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1};
		int[] patternLine4 = {1,1,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1};
		int[] patternLine5 = {1,1,1,1,1,1,1,1};
		int[] patternLine6 = {1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1};
		int[] patternLine7 = {1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1};
		int[] patternLine8 = {1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1};
		int[] patternLine9 = {1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1};
		int[] patternLine10 = {1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1};
		int[] patternLine11 = {1,1,1,1,1,0,0,0,0,0,0,0,0,1,1};
		int[] patternLine12 = {1,1,1,1,1,0,0,0,0,0,0,0,1,1};
		int[] patternLine13 = {1,1,1,1,1,0,0,0,0,0,0,1,1};
		int[] patternLine14 = {1,1,1,1,1,0,0,0,0,0,1,1};
		int[] patternLine15 = {1,1,1,1,1,0,0,0,0,1,1};
		int[] patternLine16 = {1,1,1,1,1,0,0,0,1,1};
		int[] patternLine17 = {1,1,0,0,1,1,0,0,0,0,1,1,1,1,1};
		
		int[][] patternsLigne = {patternLine1,patternLine2,patternLine3,patternLine4,
				patternLine5,patternLine6,patternLine7,patternLine8,patternLine9,patternLine10,
				patternLine11,patternLine12,patternLine13,patternLine14,patternLine15,patternLine16,patternLine17};
		
		
		
		if(detectPattern(patternSquare)) {
			LCD.drawString("half turn", 0, 0);
			//halfTurn();			
		}
		
		if(detectPattern(patternDoubleLine)) {
			LCD.drawString("ligne 1", 0, 1);
			//doubleLine();
		}
		if(detectPattern(patternDoubleLine2)) {
			LCD.drawString("ligne 2", 0, 2);
			//doubleLine();
		}
		if(detectPattern(patternDoubleLine3)) {
			LCD.drawString("ligne 3", 0, 3);
			//doubleLine();
		}
		if(detectPattern(patternDoubleLine4)) {
			LCD.drawString("ligne 4", 0, 4);
			//doubleLine();
		}
		
		
		for(int i=1; i<=patternsLigne.length; i++) {
			if(detectPattern(patternsLigne[i])) {
				LCD.drawString("ligne "+i,0,5);
			}
		}
		
	}

	
	private static void doubleLine() {
		motorG.stop();
		motorD.stop();
		
	}

	private static boolean detectPattern(int[] pattern) {
		boolean c=false;
		for(int i=0; i<iColors-pattern.length;i++) {
			c=true;
			for(int j=0;j<pattern.length && c; j++)
				c = colors[i+j] == pattern[j] && c;
			if (c)
				return c;
		}
		return c;
	}



	private static void halfTurn() {
		motorG.stop();
		motorD.stop();
		if(side==Side.LEFT)
			side=Side.RIGHT;
		else
			side=Side.LEFT;
		
	}
	


	private static boolean goBackRoad() throws InterruptedException {
		int lv;
		boolean c1=true;
		int cpt=0;
		boolean c2=Math.abs(initialLightValue-(lv=lightSensor.getNormalizedLightValue()))>differenceColor;
		while(c2) {
			c1=Math.abs(initialLightValue-(lv=lightSensor.getNormalizedLightValue()))>differenceColor;
			if (!c1) cpt++;
			if(side==Side.RIGHT)
				motorG.setSpeed((int)((float)initialSpeed*scaleSpeed));
			else
				motorD.setSpeed((int)((float)initialSpeed*scaleSpeed));
			Thread.sleep(delayLightSensor);
			c2=cpt<nbValueRequiredToChangeSide;
		}
		if(side==Side.RIGHT)
			motorG.setSpeed(initialSpeed);
		else
			motorD.setSpeed(initialSpeed);
		return c1;
		
	}




}
