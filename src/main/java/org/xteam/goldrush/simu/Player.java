package org.xteam.goldrush.simu;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Player {

	private File executableFile;
	private int playerId;
	
	private Process process;
	private String name;
	private BufferedReader reader;
	private OutputStreamWriter writer;
	private Position position;
	private Position startposition;
	private Direction direction = Direction.EAST;
	private int goldInHand = 0;
	private int collectedGold = 0;

	public Player(File playerExecutable, int playerId) {
		this.executableFile = playerExecutable;
		this.playerId = playerId;
	}

	public void start(GoldRushMap map) throws IOException {
		process = new ProcessBuilder(this.executableFile.getAbsolutePath())
			.redirectErrorStream(true)
			.start();
		reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		writer = new OutputStreamWriter(process.getOutputStream());
		
		// Read Player name
		name = reader.readLine();
		writer.write(map.getWidth() + " " + map.getHeight() + " "
				+ map.getGoldCount() + "\n");
		writer.flush();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public String getName() {
		return name;
	}
	
	public void setStartPosition(Position pos) {
		this.startposition = pos;
		setPosition(pos);
	}
	
	public Position getStartPosition() {
		return startposition;
	}

	public void setPosition(Position pos) {
		this.position = pos;
	}

	public Position getPosition() {
		return position;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void setDirection(Direction dir) {
		this.direction = dir;
	}
	
	public int getGoldInHand() {
		return goldInHand;
	}
	
	public int getCollectedGold() {
		return collectedGold;
	}

	public void sendEnvironment(PlayerEnvironment playerEnvironment) throws IOException {
		writer.write(position.getX() + " " + position.getY()
				+ " " + playerEnvironment.getPlayerPositions().size() + "\n");
		for (int y = 0; y < playerEnvironment.getHeight(); ++y) {
			StringBuilder builder = new StringBuilder();
			for (int x = 0; x < playerEnvironment.getWidth(); ++x) {
				if (x > 0) {
					builder.append(" ");
				}
				builder.append(playerEnvironment.get(x, y).getCode());
			}
			builder.append("\n");
			writer.write(builder.toString());
		}
		for (Position position : playerEnvironment.getPlayerPositions()) {
			writer.write(position.getX() + " " + position.getY() + "\n");
		}
		writer.flush();
	}

	public String nextCommandCode() throws IOException {
		return reader.readLine();
	}

	public boolean isAtBase() {
		return position.equals(startposition);
	}

	public void dropGoldInBase() {
		this.collectedGold += goldInHand;
		goldInHand = 0;
	}

	public void dropGold(int dropping) {
		this.goldInHand -= dropping;
	}

	public void pick(int quantity) {
		this.goldInHand += quantity;
	}

}