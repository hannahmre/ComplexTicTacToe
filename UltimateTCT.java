package teamproject;

/*Hannah Moore, Anna Gallagher, Lindsay Kness*/


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;


public class UltimateTCT extends JFrame{
	private JPanel upperPanel;
	private JPanel gamePanel;
	private JPanel subgamePanel;
	private JPanel bottomPanel;
	private JButton player_O;
	private JButton player_X;
	private JButton restart;
	private JButton undo;
	private JLabel wins, wins_field, total_games, total_gamesfield, average_moves, average_movesfield;
	Player player1;
	Player player2;
	private ArrayList<JPanel> subgames = new ArrayList<JPanel>();
	private ArrayList<Integer> wongames =  new ArrayList<Integer>();
	private ArrayList<Integer> x_wins = new ArrayList<Integer>();
	private ArrayList<Integer> o_wins = new ArrayList<Integer>();
	private ArrayList<Integer> x_totalmoves = new ArrayList<Integer>();
	private ArrayList<Integer> o_totalmoves = new ArrayList<Integer>();
	
	private Player current_turn;
	private Player prev_turn;
	
	private Square current_button;
	private Square prev_button;
	private int location;
	
	private int current_grid = -1;
	private int prev_grid = -1;
	private boolean last_move_x_won;
	private boolean last_move_o_won;
	
	private int x_moves = 0;
	private int o_moves = 0;

	


	public UltimateTCT(){
		
		/*Game Layout*/
		super("Tic Tac Toe Game");
		setSize(new Dimension(800,600));
		setLocation(100,100);
		this.setLayout(new BorderLayout());
	
		this.upperPanel = create_upperPanel();
		upperPanel.setLayout(new FlowLayout());
		this.add(upperPanel, BorderLayout.NORTH);
		
		this.gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(3,3));
		
		/*Creates buttons (squares) and adds listeners to them*/
		playersMoves moves = new playersMoves(); 
		for(int i=0; i<9; i++) {
			this.subgamePanel = new JPanel();
			subgames.add(subgamePanel);
			subgamePanel.setLayout(new GridLayout(3,3));
			subgamePanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black));

			for(int j=0; j<9; j++) {
				Square s = new Square();
				s.addActionListener(moves);
				subgamePanel.add(s);
				s.setButton_location(j); //gives each button a location as they are created (0-8)
			}
			gamePanel.add(subgamePanel);
		}
		this.add(gamePanel, BorderLayout.CENTER);

		this.bottomPanel = create_bottomPanel();
		bottomPanel.setLayout(new FlowLayout());
		this.add(bottomPanel, BorderLayout.SOUTH);
		
		/* Creates two players when game starts */
		this.player1 = new Player("X");
		this.player2 = new Player("O");
		
		/* Randomly chooses which player goes first */
		Player[] players = {player1, player2};
		Random r = new Random();
		this.current_turn = players[r.nextInt(2)];
		if(current_turn == player1) {
			player_X.setForeground(Color.BLUE);
		}else
			if(current_turn == player2) {
				player_O.setForeground(Color.GREEN);
			}
	}//end constructor
	
	/*Top Bar Menu*/
	public JPanel create_upperPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JLabel current_player = new JLabel("Now Playing:");
		panel.add(current_player);
		
		this.player_O = new JButton("Player O");
		this.player_X = new JButton("Player X");
		panel.add(player_X);
		panel.add(player_O);
		
		JLabel manage_game = new JLabel("Manage Game:");
		panel.add(manage_game);
		
		this.restart = new JButton("Restart");
		this.undo = new JButton("Undo");
		restart.addActionListener(new buttonListener());
		undo.addActionListener(new buttonListener());
		panel.add(restart);
		panel.add(undo);
		
		return panel;
	}
	
	/*Bottom Bar Menu*/
	public JPanel create_bottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JButton game_stats = new JButton("Playing Stats");
		game_stats.addActionListener(new buttonListener());
		panel.add(game_stats);
		
		wins = new JLabel("Win %:");
		panel.add(wins);
		
		wins_field = new JLabel();
		wins_field.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(1.0f)));
		wins_field.setPreferredSize(new Dimension(100,20));
		panel.add(wins_field);
		
		total_games = new JLabel("Total # of games:");
		panel.add(total_games);
		
		total_gamesfield = new JLabel();
		total_gamesfield.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(1.0f)));
		total_gamesfield.setPreferredSize(new Dimension(100,20));
		panel.add(total_gamesfield);
		
		average_moves = new JLabel("Average # of moves per win:");
		panel.add(average_moves);
		
		average_movesfield = new JLabel();
		average_movesfield.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(1.0f)));
		average_movesfield.setPreferredSize(new Dimension(100,20));
		panel.add(average_movesfield);

		return panel;
	}
	
	private class buttonListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			JButton clickedObject = (JButton) a.getSource();
			/*Restart Button*/
			if(clickedObject.getText()=="Restart") {
				restart();
			}
			/*Undo Button*/
			if(clickedObject.getText()=="Undo") { 
				if(prev_turn != current_turn) {
					current_button.setText(null);
					if(prev_grid != -1) {
						current_turn = prev_turn;
						current_button = prev_button;
						current_grid = prev_grid;
						
						/*If last move won a subgame for X*/
						if(last_move_x_won == true) {
							wongames.remove((wongames.size()-1));
							x_wins.remove((x_wins.size()-1));
							enabler(current_grid);
						}
						/*If last move won a subgame for O*/
						else if (last_move_o_won == true) {
							wongames.remove(wongames.size()-1);
							o_wins.remove(o_wins.size()-1);
							enabler(current_grid);
						}
						else {
							enabler(current_grid);
						}
					}
					else {
						current_grid = -1;
						current_button = null;
						enabler(current_grid);
					}
				}
			}
			/*Playing Stats Button*/
			if(clickedObject.getText()=="Playing Stats") {
				if(current_turn.get()=="X") {
					clickedObject.setForeground(Color.BLUE);
					wins_field.setText("  "+ Double.toString(wins_percent(x_wins, wongames))+" %");
					total_gamesfield.setText("  "+ Integer.toString(x_wins.size()));
					average_movesfield.setText("  "+ Integer.toString(average_moves(x_totalmoves)));
				}
				if(current_turn.get()=="O") {
					clickedObject.setForeground(Color.GREEN);
					wins_field.setText("  "+ Double.toString(wins_percent(o_wins, wongames))+" %");
					total_gamesfield.setText("  "+ Integer.toString(o_wins.size()));
					average_movesfield.setText("  "+ Integer.toString(average_moves(o_totalmoves)));
					}
			}
		}//end of actionPerformed function
	}//end of buttonListener class
	
	public void restart() {
		for(int i=0; i<subgames.size(); i++) {
			Component[] s = subgames.get(i).getComponents();
			subgames.get(i).setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black));
			for(int j=0; j<s.length; j++) {
				s[j].setEnabled(true);
				((JButton) s[j]).setText(null);
			}		
		}	
		current_grid = -1;
		prev_grid = -1;
		current_button = null;
		prev_button = null;
		x_wins.clear();
		o_wins.clear();
		wongames.clear();
		wins_field.setText(null);
		total_gamesfield.setText(null);
		average_movesfield.setText(null);
		x_totalmoves.clear();
		o_totalmoves.clear();
		x_moves = 0;
		o_moves = 0;
	}

	public double wins_percent(ArrayList<Integer> player_wins, ArrayList<Integer> total_wins) {
		double player_total = player_wins.size();
		double overall_total = total_wins.size();
		double percent = Math.round((player_total/overall_total)*100);
		return percent;
	}
	
	public int average_moves(ArrayList<Integer> total_playermoves) {
		int sum = 0;
		for(int i=0; i<total_playermoves.size(); i++) {
			sum += total_playermoves.get(i);
		}
		if(total_playermoves.size()!=0) {
			int average_moves = (sum/total_playermoves.size());
			return average_moves;
		}
		else {
			return 0;
		}
	}
	// takes in a list of subpanels won by "X"
	// OR a list of subpanels won by "O" and determines if the overall game has been won
	public int overallWin(ArrayList<Integer> nums) {
		if(nums.contains(0) && nums.contains(1) && nums.contains(2)) 
			return 1;
		if(nums.contains(3) && nums.contains(4) && nums.contains(5)) 
			return 1;
		if(nums.contains(6) && nums.contains(7) && nums.contains(8)) 
			return 1;
		if(nums.contains(0) && nums.contains(4) && nums.contains(8)) 
			return 1;
		if(nums.contains(2) && nums.contains(4) && nums.contains(6)) 
			return 1;
		if(nums.contains(0) && nums.contains(3) && nums.contains(6)) 
			return 1;
		if(nums.contains(1) && nums.contains(4) && nums.contains(7)) 
			return 1;
		if(nums.contains(2) && nums.contains(5) && nums.contains(8)) 
			return 1;
		return -1;
	}
	
	// takes in the list of the subpanels components (buttons) and checks if the subpanel has been won
	public int hasWonGrid(Component[] buttons) { 
		/*Checks top row win */
		if(((JButton) buttons[0]).getText()!= null 
				&& ((JButton) buttons[0]).getText()==((JButton) buttons[1]).getText() 
				&& ((JButton) buttons[1]).getText() == ((JButton) buttons[2]).getText()) {	
			if(((JButton) buttons[0]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[0]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		/*Checks middle row win*/
		if(((JButton) buttons[3]).getText()!= null 
				&& ((JButton) buttons[3]).getText()==((JButton) buttons[4]).getText() 
				&& ((JButton) buttons[4]).getText() == ((JButton) buttons[5]).getText()) {
			if(((JButton) buttons[3]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[3]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		/*Checks bottom row win*/
		if(((JButton) buttons[6]).getText()!= null 
				&& ((JButton) buttons[6]).getText()==((JButton) buttons[7]).getText() 
				&& ((JButton) buttons[7]).getText() == ((JButton) buttons[8]).getText()) {
			if(((JButton) buttons[6]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[6]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		/*Checks left column*/
		if(((JButton) buttons[0]).getText()!= null 
				&& ((JButton) buttons[0]).getText()==((JButton) buttons[3]).getText() 
				&& ((JButton) buttons[3]).getText() == ((JButton) buttons[6]).getText()) {
			if(((JButton) buttons[0]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[0]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		/*Checks middle column*/
		if(((JButton) buttons[1]).getText()!= null 
				&& ((JButton) buttons[1]).getText()==((JButton) buttons[4]).getText() 					
				&& ((JButton) buttons[4]).getText() == ((JButton) buttons[7]).getText()) {
			if(((JButton) buttons[1]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[1]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		/*Checks right column*/
		if(((JButton) buttons[2]).getText()!= null 
				&& ((JButton) buttons[2]).getText()==((JButton) buttons[5]).getText() 
				&& ((JButton) buttons[5]).getText() == ((JButton) buttons[8]).getText()) {
			if(((JButton) buttons[2]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[2]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		/*Checks one diagonal*/
		if(((JButton) buttons[0]).getText()!= null 
				&& ((JButton) buttons[0]).getText()==((JButton) buttons[4]).getText() 
				&& ((JButton) buttons[4]).getText() == ((JButton) buttons[8]).getText()) {
			if(((JButton) buttons[0]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[0]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		/*Checks other diagonal*/
		if(((JButton) buttons[2]).getText()!= null 
				&& ((JButton) buttons[2]).getText()==((JButton) buttons[4]).getText() 
				&& ((JButton) buttons[4]).getText() == ((JButton) buttons[6]).getText()) {
			if(((JButton) buttons[2]).getText()=="X") {
				System.out.print("Player X Won");
				return 0;
			}
			if(((JButton) buttons[2]).getText()=="O") {
				System.out.print("Player O Won");
				return 1;
			}
		}
		return -2;
	}
	/*Checks for newly won grids*/
	public void checkWinners(){
		boolean flag = false;
		for(int i=0; i<subgames.size(); i++) {
			Component[] s = subgames.get(i).getComponents(); //adds the subpanel's components (the buttons) to a list
			int won = hasWonGrid(s); //checks if the subpanel has won or not
			System.out.println(won);
			if(won == 0) { //if player x won, add the subpanel location to wongames and change the subpanel border to blue
				if(!wongames.contains(i)) {
					last_move_x_won = true;
					last_move_o_won = false;
					flag = true;
					wongames.add(i);
					x_wins.add(i);
					x_totalmoves.add(x_moves);
				}
				subgames.get(i).setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.blue));
				for(int j=0; j<s.length; j++) {
					s[j].setEnabled(false);
				}
				x_moves=0;
			} 
			if(won == 1) {  // if player o won, add the subpanel location to wongames and change the subpanel border to green
				if(!wongames.contains(i)) {
					last_move_o_won = true;
					last_move_x_won = false;
					o_totalmoves.add(o_moves);
					flag = true;
					wongames.add(i);
					o_wins.add(i);
				}
				subgames.get(i).setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.green));
				for(int j=0; j<s.length; j++) {
					s[j].setEnabled(false);
				}
				o_moves=0;
			}
			if(won == -2 && !flag) {
				last_move_o_won = false;
				last_move_o_won = false;
			}

			//checks is the overall game has been won by x or o
			int x_game_over = overallWin(x_wins);
			int o_game_over = overallWin(o_wins);
			Object [] options = {"Restart", "Quit"};

			// if player x won
			if(x_game_over == 1) {
				for(int sub=0; sub<subgames.size(); sub++) {
					Component[] sub_buttons = subgames.get(sub).getComponents();
					for(int j=0; j<sub_buttons.length; j++) {
						sub_buttons[j].setEnabled(false);
					}
				}
				JOptionPane.showMessageDialog(gamePanel, "Player X Won!");
				int x = JOptionPane.showOptionDialog(gamePanel, "Please select an option: ", 
						"Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options, options[0]);
				if(x == JOptionPane.YES_OPTION) {
					restart();
				}
				if(x == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
				break; 
			}
			
			// if player o won
			if(o_game_over == 1) {
				for(int sub=0; sub<subgames.size(); sub++) {
					Component[] sub_buttons = subgames.get(sub).getComponents();
					for(int j=0; j<sub_buttons.length; j++) {
						sub_buttons[j].setEnabled(false);
					}
				}
				JOptionPane.showMessageDialog(gamePanel, "Player O Won!");
				int x = JOptionPane.showOptionDialog(gamePanel, "Please select an option: ", 
						"Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options, options[0]);
				if(x == JOptionPane.YES_OPTION) {
					restart();
				}
				if(x == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
				break;
			}
			
			if(((x_totalmoves.size()+o_totalmoves.size()) == 9) && o_game_over == -1 && x_game_over == -1) {
				for(int sub=0; sub<subgames.size(); sub++) {
					Component[] sub_buttons = subgames.get(sub).getComponents();
					for(int j=0; j<sub_buttons.length; j++) {
						sub_buttons[j].setEnabled(false);
					}
				}
				JOptionPane.showMessageDialog(gamePanel, "The game ended in a tie!");
				int x = JOptionPane.showOptionDialog(gamePanel, "Please select an option: ", 
						"Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, options, options[0]);
				if(x == JOptionPane.YES_OPTION) {
					restart();
				}
				if(x == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
				break;
			}
		}
	}
	public void enabler(int location) {
		//loops through panels
		for(int i=0; i<subgames.size(); i++) {
			Component[] s = subgames.get(i).getComponents();//adds the subpanel's components (the buttons) to a list
			if(location == -1){
				subgames.get(i).setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black));					
				for(int j=0; j<s.length; j++) {
					s[j].setEnabled(true);
				}
			}
			
			// if the panel number(i) does not equal the location number from the button last pressed(location) AND the panel number(i) is not in wongames, turn border black and disable the buttons
			if(i != location && !wongames.contains(i)&& location != -1) { 
				subgames.get(i).setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black));					
				for(int j=0; j<s.length; j++) {
					s[j].setEnabled(false);
				}
			}
			
			// if the panel number(i) equals the location number from the button last pressed(location)...
			if(i == location) {
				if(!wongames.contains(i)) { //if the panel number is not in wongames (the game has not been won yet), set border to red and enable its buttons
					subgames.get(i).setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));			
					for(int j=0; j<s.length; j++) {
						s[j].setEnabled(true);
					}
				}
				else {
					if(wongames.contains(i)) { //if the panel number is in wongames (the game has been won)
						for(int n=0; n<subgames.size(); n++) { //loop through the all the subgames
							if(!wongames.contains(n)) { //if the subgame is not in wongames(the game has not been won), set their borders to red and enable all their buttons
								Component[] s2 = subgames.get(n).getComponents();
								subgames.get(n).setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red)); 
								for(int j=0; j<s2.length; j++) {
									s2[j].setEnabled(true);
								}
							}
						}
					}
					break;
				}
			}	
		}
	}
	
	private class playersMoves implements ActionListener{
		
		/* Sets square to players letter depending on whose turn it is */
		public void actionPerformed(ActionEvent e) {
			Object clickedObject = e.getSource();
			prev_button = current_button;
			current_button = ((Square) clickedObject);
			current_grid= current_button.getButton_location();//sets the button location (0-8)
			if(prev_button!=null)
				prev_grid = prev_button.getButton_location();
		
			if (current_button.getText() != "X" && current_button.getText() != "O") {
				current_button.setSquare(current_turn.get());
				if(current_turn.get() == "X") {
					x_moves += 1;
					current_turn = player2;
					prev_turn = player1;
					current_button.setForeground(Color.BLUE);
					player_O.setForeground(Color.GREEN);
					player_X.setForeground(Color.BLACK);

				}
				else
					if(current_turn.get() == "O") {
						o_moves += 1;
						current_turn = player1;
						prev_turn = player2;
						current_button.setForeground(Color.GREEN);
						player_X.setForeground(Color.BLUE);
						player_O.setForeground(Color.BLACK);
				}
				checkWinners();
				System.out.println(x_totalmoves.size() + o_totalmoves.size());
				enabler(current_grid);
			}	
		}	
	}			
}
	
	
	
	
	


