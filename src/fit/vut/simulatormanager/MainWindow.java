package fit.vut.simulatormanager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

public class MainWindow extends JFrame {
	JTextArea textArea;
	int selectedSimulationId = 0;
	String simulatorName = "";
	DatabaseHelper dbHelp = new DatabaseHelper();
	JButton btnStartGeneratingFake;
	JButton btnStopGeneratingFake;
	Map<String, String> simulatorIdsNames;
	private JTextField txtEnterSimulationNumber;
	private ScheduledExecutorService ses = Executors
			.newSingleThreadScheduledExecutor();
	private JButton btnNewButton;
	private JLabel lblEnterSimulationId;

	public MainWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				unscheduleEverySecondExecution();
			}
		});
		setTitle("Fake data generator");
		setMinimumSize(new Dimension(800, 600));

		positionMainWindowInCenter();
		initializeTextArea();
		initializeStartButton();
		initializeStopButton();
		initializePauseButton();
		initializeInputSimulationNumber();

		JScrollPane scrollPane = new JScrollPane();

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				scrollPane,
																				GroupLayout.PREFERRED_SIZE,
																				751,
																				GroupLayout.PREFERRED_SIZE)
																		.addContainerGap())
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addGroup(
																								groupLayout
																										.createSequentialGroup()
																										.addComponent(
																												lblEnterSimulationId)
																										.addPreferredGap(
																												ComponentPlacement.RELATED,
																												601,
																												Short.MAX_VALUE))
																						.addGroup(
																								Alignment.TRAILING,
																								groupLayout
																										.createSequentialGroup()
																										.addComponent(
																												txtEnterSimulationNumber,
																												GroupLayout.PREFERRED_SIZE,
																												122,
																												GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												ComponentPlacement.RELATED,
																												GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.addComponent(
																												btnStartGeneratingFake,
																												GroupLayout.PREFERRED_SIZE,
																												163,
																												GroupLayout.PREFERRED_SIZE)
																										.addGap(36)
																										.addComponent(
																												btnStopGeneratingFake)
																										.addGap(41)
																										.addComponent(
																												btnNewButton,
																												GroupLayout.PREFERRED_SIZE,
																												171,
																												GroupLayout.PREFERRED_SIZE)
																										.addGap(33)))
																		.addGap(621)))));
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(lblEnterSimulationId)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																txtEnterSimulationNumber,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																btnStartGeneratingFake)
														.addComponent(
																btnStopGeneratingFake)
														.addComponent(
																btnNewButton))
										.addGap(18)
										.addComponent(scrollPane,
												GroupLayout.PREFERRED_SIZE,
												414, GroupLayout.PREFERRED_SIZE)
										.addContainerGap(203, Short.MAX_VALUE)));
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		getContentPane().setLayout(groupLayout);
	}

	private void initializePauseButton() {
		lblEnterSimulationId = new JLabel("Enter simulation id:");
		btnNewButton = new JButton("Pause/start simulation");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String generatedQuery = DatabaseHelper
						.pauseStartSimulationRandomly(Integer
								.valueOf(txtEnterSimulationNumber.getText()));
				appendStringToTextArea(generatedQuery);
				System.out.println("Started or paused simulation (random)");
			}
		});
	}

	private void initializeInputSimulationNumber() {
		txtEnterSimulationNumber = new JTextField();
		txtEnterSimulationNumber.setToolTipText("enter");
		txtEnterSimulationNumber.setColumns(10);
	}

	private void positionMainWindowInCenter() {
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		double centerY = screenSize.getHeight() / 2.0;
		double centerX = screenSize.getWidth() / 2.0;
		double halfOfCurrentWidth = getSize().getWidth() / 2.0;
		double halfOfCurrentHeight = getSize().getHeight() / 2.0;
		setLocation((int) (centerX - halfOfCurrentWidth),
				(int) (centerY - halfOfCurrentHeight));
	}

	private void initializeStopButton() {
		btnStopGeneratingFake = new JButton("Stop simulating fake data");
		btnStopGeneratingFake.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ses.shutdown();
			}
		});
	}

	private void initializeStartButton() {
		btnStartGeneratingFake = new JButton("Start simulating fake data");
		btnStartGeneratingFake.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String simulationIdStr = txtEnterSimulationNumber.getText();
				try {
					selectedSimulationId = Integer.parseInt(simulationIdStr);
					if (selectedSimulationId < 1) {
						JOptionPane.showMessageDialog(null,
								"Please, set simulation id");
					} else {
						startGeneratingFakeData();
					}
				} catch (NumberFormatException e) {
					JOptionPane
							.showMessageDialog(null,
									"Please, enter valid integer number for simulation id");
				}
			}
		});
	}

	private void initializeTextArea() {
	}

	private void startGeneratingFakeData() {
		unscheduleEverySecondExecution();
		appendStringToTextArea("Starting to generate fake data on simulator "
				+ txtEnterSimulationNumber.getText());
		scheduleEverySecondExecution();
	}

	private void appendStringToTextArea(String appendedString) {
		textArea.setText(textArea.getText() + appendedString + "\n");
	}

	private void unscheduleEverySecondExecution() {
		ses.shutdown();
	}

	private void scheduleEverySecondExecution() {
		ses = Executors.newSingleThreadScheduledExecutor();
		final Runnable beeper = new Runnable() {
			public void run() {
				try {
					String generatedQuery = DatabaseHelper
							.insertNewDevState(Integer
									.valueOf(txtEnterSimulationNumber.getText()));
					appendStringToTextArea(generatedQuery);
					generatedQuery = DatabaseHelper.insertNewPfdData(Integer
							.valueOf(txtEnterSimulationNumber.getText()));
					appendStringToTextArea(generatedQuery);
					System.out.println("Inserted fake data into DB");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		ses.scheduleAtFixedRate(beeper, 0, 1, TimeUnit.SECONDS);
		/*
		 * ses.scheduleAtFixedRate(new Callable<Object>() { public Object call()
		 * throws Exception { System.out.println("Executed!"); String
		 * generatedQuery = DatabaseHelper .pauseStartSimulationRandomly(Integer
		 * .valueOf(txtEnterSimulationNumber.getText()));
		 * appendStringToTextArea(generatedQuery);
		 * 
		 * return null; } }, 1, TimeUnit.SECONDS);
		 */
	}
}
