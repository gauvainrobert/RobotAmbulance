package robotambulance.util;

public class Constants {
	private Constants() {}
	public static final int MIN_NB_VALUE_REQUIRED_TO_DETECT_LINE = 15;
	public static final int DELAY_LIGHTSENSOR = 10;
	public static final int NB_LIGHT_VALUES = 50;
	public static final long TIME_BETWEEN_2_LINES = 0; // mesurer le temps entre 2 petites lignes
	public static final int Kp = 1600; //2000//1600//1600
	public static final int Ki = 30; //80//30//50
	public static final int Kd = 500; //3000//500//50
	public static final int offset = 35; //47//40
	public static final int offsetSimpleLine = 54; //40
	public static final int Tp = 500; //400//400
	public static final int Kline = 200;
	public static float distanceRiskOfLine = 15.f;
	public static double distanceToTravelAfterHalfTurn = -150;
	public static int victims_capacity = 2;
	public static final float distanceTurn = 18.5f;
	public static final float distanceStraightLineIntersection = 25.5f;
}


