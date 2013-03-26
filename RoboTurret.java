							import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csu.cs.Robot;

public class RoboTurret
{
	private final int HIGH_CONSTANT = 20;
	private final int LOW_CONSTANT = 10;

	//robot variables
	private Robot robby;
	private int[][][] backgroundFrame;
	private int tolerance = 30;
	private int point;
	private int center;
	private boolean Moved = false;
	private MyPanel difDisplay;

	public static void main(String args[])
	{
		RoboTurret turret = new RoboTurret("");
		turret.findMotion();
	}

	/**
	 * Constructor for Robotic Turret
	 * @param address - IP of Turret
	 */
	public RoboTurret(String address)
	{		
		robby = new Robot();
		//robby.speak("BEGIN");
		//robby.enableMultitasking();
		robby.setTurningSpeed(Robot.HIGH_TURNING_SPEED);
	}

	public void findMotion(){
		System.out.println("Start");
		robby.takePicture();
		backgroundFrame = robby.getPicture().getThreeDArray();
		int newPic[][][] = new int[backgroundFrame.length][backgroundFrame[0].length][3];
		int currentPic[][][] = robby.getPicture().getThreeDArray();
		int level = LOW_CONSTANT;
		point = center = backgroundFrame[0].length / 2;

		JFrame diff = new JFrame("difference");
		difDisplay = new MyPanel(newPic);
		diff.add(difDisplay);
		diff.setSize(backgroundFrame[0].length + 87,backgroundFrame.length + 35);
		diff.setLocation(100,50);
		diff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		diff.setVisible(true);

		while(true){
			robby.takePicture();
			backgroundFrame = robby.getPicture().getThreeDArray();
			robby.takePicture();
			currentPic = robby.getPicture().getThreeDArray();

			int sumPixels = 0, numPixels = 1;

			for(int i = 2; i < currentPic.length - 2; i++){
				for(int j = 2; j < currentPic[i].length - 2; j++){
					int intensity1 = getIntensity(i, j, currentPic);
					int intensity2 = getIntensity(i, j, backgroundFrame);
					int ratio = Math.abs(intensity1 - intensity2);

					newPic[i][j][0] = 0;
					newPic[i][j][1] = 0;
					newPic[i][j][2] = 0;

					if(ratio > tolerance) 
					{
						numPixels++;
						sumPixels += j;
						newPic[i][j][2] = 255;
					}
				}
			}


			point = (sumPixels/numPixels); //return center of movement
			if(numPixels < 100) point = center;
			if(numPixels > 8000 && level < HIGH_CONSTANT)
				level += 2;
			else if(numPixels < 100 && level > LOW_CONSTANT)
				level -= 2;

			if(numPixels > 100 && Math.abs(point - center) > 30){
				for(int i = 0; i < currentPic.length; ++i){
					for(int j = point - 3; j < point + 3; ++j){
						if(i > 0 && i < newPic.length && j > 0 && j < newPic[i].length){
							newPic[i][j][1] = 255;
							newPic[i][j][2] = 0;
						}
					}
				}

				System.out.println("I found motion!\n");
				executeTurn(point);
			}

			difDisplay.repaint();
		}
	}

	private void executeTurn(int point)
	{						
		robby.turn((int)Math.toDegrees(Math.atan( (double)(center - point) / center )));	
	}

	private int getIntensity(int a, int b, int[][][] picture)
	{
		int intensity = 0;

		for(int i = a-2; i <= a+2; i++)
			for(int j = b-2; j <= b+2; j++)
				intensity += (picture[i][j][0] + picture[i][j][1] + picture[i][j][2])/3;				

		return intensity/25;
	}

	private class MyPanel extends JPanel{
		private int[][][] currentDis;
		private double Xsize;
		private double Ysize;

		public MyPanel(int[][][] frame){
			super();
			currentDis = frame;
			Xsize = currentDis.length;
			Ysize = currentDis[0].length;
			repaint();
		}
		
		public void setPic(int[][][] Frame){
			currentDis = Frame;
		}

		public void paintComponent(Graphics g){
			double dx = Xsize / currentDis.length;
			double dy = Ysize / currentDis.length;

			for(int i = 0; i < currentDis.length; ++i){
				for(int j = 0; j < currentDis[i].length; ++j){
					g.setColor(new Color(currentDis[i][j][0],currentDis[i][j][1],currentDis[i][j][2]));
					g.fillRect((int)(j * dy),(int)(i * dx),(int)dy + 1,(int)dx + 1);
				}
			}
		}
	}
}
