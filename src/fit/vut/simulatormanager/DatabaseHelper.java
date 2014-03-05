package fit.vut.simulatormanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DatabaseHelper {
	static Random random = new Random();
	// Devices state
	private static double lastElevatorVal = 1.0;
	private static double lastEleronVal = 1.0;
	private static int lastRudderVal = 1;
	private static double lastThrottleVal = 1.0;
	private static double lastFlapsVal = 1.0;
	private static boolean lastSpeedBrakesVal = false;
	private static double lastTrimVal = 1.0;

	// PFD state
	private static double lastRollVal = 0.0;
	private static double lastPitchVal = 0.0;
	private static double lastYawVal = 0.0;
	private static double lastIASVal = 0.0;
	private static double lastAltitudeVal = 0.0;
	private static double lastGroundAltitudeVal = 0.0;
	private static double lastVerticalSpeedVal = 0.0;
	private static double lastCompassVal = 0.0;

	public static Map<String, String> getSimulatorIdsNames() {
		Map<String, String> simulatorIdsNames = new HashMap<>();
		Connection connection = null;
		String getDataString = "select \"SimulatorId\", \"SimulatorName\" from simulator";
		PreparedStatement getDataPs = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost/postgres", "postgres",
					"password");
			getDataPs = connection.prepareStatement(getDataString);
			ResultSet rs = getDataPs.executeQuery();
			while (rs.next()) {
				String simulatorId = rs.getString("SimulatorId");
				String simulatorName = rs.getString("SimulatorName");
				simulatorIdsNames.put(simulatorId, simulatorName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return simulatorIdsNames;
	}

	public static boolean getRandomBoolean() {
		return random.nextBoolean();
	}

	public static int getRandomInt(int upperBound) {
		return random.nextInt(upperBound);
	}

	public static double getRandomDouble() {
		return random.nextDouble() * 2 - 1;
	}

	public static String pauseStartSimulationRandomly(Integer simulationId) {
		boolean nextState = getRandomBoolean();
		Connection connection = null;
		String updateDataString = "UPDATE Simulation SET \"IsSimulationOn\" = ? WHERE \"SimulationId\"=?;";
		PreparedStatement getDataPs = null;
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost/postgres", "postgres",
					"password");
			getDataPs = connection.prepareStatement(updateDataString);
			getDataPs.setBoolean(1, nextState);
			getDataPs.setInt(2, simulationId);
			getDataPs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return updateDataString.replaceFirst("\\?", String.valueOf(nextState))
				.replaceFirst("\\?", String.valueOf(simulationId));
	}

	public static String insertNewDevState(Integer simulationId) {
		lastElevatorVal = lastElevatorVal + getRandomDouble();
		lastEleronVal = lastEleronVal + getRandomDouble();
		lastRudderVal = lastRudderVal + getRandomInt(2);
		lastThrottleVal = lastThrottleVal + getRandomDouble();
		lastFlapsVal = lastFlapsVal + getRandomDouble();
		lastSpeedBrakesVal = getRandomBoolean();
		lastTrimVal = lastTrimVal + getRandomDouble();

		Connection connection = null;
		String insertDataString = "insert into SimulationDevicesState (\"Simulation_SimulationId\", \"Elevator\", \"Eleron\", \"Rudder\", \"Throttle\", \"Flaps\", \"SpeedBrakes\", \"Trim\") VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement insertDataPs = null;
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost/postgres", "postgres",
					"password");
			insertDataPs = connection.prepareStatement(insertDataString);
			insertDataPs.setInt(1, simulationId);
			insertDataPs.setDouble(2, lastElevatorVal);
			insertDataPs.setDouble(3, lastEleronVal);
			insertDataPs.setInt(4, lastRudderVal);
			insertDataPs.setDouble(5, lastThrottleVal);
			insertDataPs.setDouble(6, lastFlapsVal);
			insertDataPs.setBoolean(7, lastSpeedBrakesVal);
			insertDataPs.setDouble(8, lastTrimVal);
			insertDataPs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		DecimalFormat df = new DecimalFormat("#.##");
		return insertDataString.replaceFirst("\\?", df.format(simulationId))
				.replaceFirst("\\?", df.format(lastElevatorVal))
				.replaceFirst("\\?", df.format(lastEleronVal))
				.replaceFirst("\\?", df.format(lastRudderVal))
				.replaceFirst("\\?", df.format(lastThrottleVal))
				.replaceFirst("\\?", String.valueOf(lastFlapsVal))
				.replaceFirst("\\?", String.valueOf(lastSpeedBrakesVal));
	}

	public static String insertNewPfdData(Integer simulationId) {
		lastRollVal += getRandomDouble();
		lastPitchVal = Math.abs(lastPitchVal + getRandomDouble());
		lastYawVal = Math.abs(lastYawVal + getRandomDouble());
		lastIASVal = Math.abs(lastIASVal + getRandomDouble() * 10);
		lastAltitudeVal = Math.abs(lastAltitudeVal + getRandomDouble() * 10);
		lastGroundAltitudeVal = Math.abs(lastGroundAltitudeVal
				+ getRandomDouble());
		lastVerticalSpeedVal = Math.abs(lastVerticalSpeedVal
				+ getRandomDouble());
		lastCompassVal = lastCompassVal + getRandomDouble();

		Connection connection = null;
		String insertDataString = "insert into SimulationPfdInfo (\"Simulation_SimulationId\", \"Roll\", \"Pitch\", \"Yaw\", \"IAS\", \"Altitude\", \"GroundAltitude\", \"VerticalSpeed\", \"Compass\") VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement insertDataPs = null;
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost/postgres", "postgres",
					"password");
			insertDataPs = connection.prepareStatement(insertDataString);
			insertDataPs.setInt(1, simulationId);
			insertDataPs.setDouble(2, lastRollVal);
			insertDataPs.setDouble(3, lastPitchVal);
			insertDataPs.setDouble(4, lastYawVal);
			insertDataPs.setDouble(5, lastIASVal);
			insertDataPs.setDouble(6, lastAltitudeVal);
			insertDataPs.setDouble(7, lastGroundAltitudeVal);
			insertDataPs.setDouble(8, lastVerticalSpeedVal);
			insertDataPs.setDouble(9, lastCompassVal);
			insertDataPs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		lastRollVal += getRandomDouble();
		lastPitchVal += getRandomDouble();
		lastYawVal += getRandomDouble();
		lastIASVal += getRandomDouble();
		lastAltitudeVal += getRandomDouble();
		lastGroundAltitudeVal += getRandomDouble();
		lastVerticalSpeedVal += getRandomDouble();
		lastCompassVal += getRandomDouble();
		DecimalFormat df = new DecimalFormat("#.##");
		return insertDataString.replaceFirst("\\?", df.format(simulationId))
				.replaceFirst("\\?", df.format(lastRollVal))
				.replaceFirst("\\?", df.format(lastPitchVal))
				.replaceFirst("\\?", df.format(lastYawVal))
				.replaceFirst("\\?", df.format(lastIASVal))
				.replaceFirst("\\?", df.format(lastAltitudeVal))
				.replaceFirst("\\?", df.format(lastGroundAltitudeVal))
				.replaceFirst("\\?", df.format(lastVerticalSpeedVal))
				.replaceFirst("\\?", df.format(lastCompassVal));
	}
}
