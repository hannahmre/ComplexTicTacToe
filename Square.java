package teamproject;

/*Hannah Moore, Anna Gallagher, Lindsay Kness*/

import javax.swing.JButton;

public class Square extends JButton{
	private int button_location;


	public int getButton_location() {
		return button_location;
	}

	public void setButton_location(int button_location) {
		this.button_location = button_location;
	}

	public void setSquare(String play) {
		this.setText(play);
	}


	


}
