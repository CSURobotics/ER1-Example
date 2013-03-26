import edu.csu.cs.Robot;



public class RobotTest {
	
	private static Robot polo = new Robot();
	
	public static void main(String[] args) {
	
		//polo.takePicture();
		//polo.displayPicture();
		polo.setMovementSpeed(Robot.HIGH_MOVEMENT_SPEED);
		polo.setTurningSpeed(Robot.HIGH_TURNING_SPEED);
		
		polo.doForwardArcTurn(150);
		//polo.moveBackward(5);
		//polo.turnLeft(15);
		//polo.turnRight(15);
		//polo.moveForward(5);
		//polo.speak("hellos");
			
	}	    
}
