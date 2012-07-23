package skoehler.sensor.example;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.util.Delay;
import skoehler.sensor.api.VectorData;
import skoehler.sensor.device.LcUltrasonic;
import skoehler.sensor.filter.Align;
import skoehler.sensor.filter.HighPass;
import skoehler.sensor.filter.LowPass;
import skoehler.sensor.filter.StatisticsFilter;
import skoehler.sensor.sampling.SampleBuffer;
import skoehler.sensor.sampling.SampleLogger;

public class UsSensorLogged {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UsSensorLogged test=new UsSensorLogged();
	}

	public UsSensorLogged() {
		float[] obstacle=new float[1];
		
		// connect to NXTchartingLogger
		lejos.util.NXTDataLogger dlog = new lejos.util.NXTDataLogger();
		lejos.nxt.comm.NXTConnection conn = Bluetooth.waitForConnection(15000, lejos.nxt.comm.NXTConnection.PACKET);
		try {
		    dlog.startRealtimeLog(conn);
		} catch (IOException e) {
		    // Do nothing
		}

		// Construct sensor stream
		LcUltrasonic sensor=new LcUltrasonic(SensorPort.S1);
		SampleLogger log1=new SampleLogger(sensor,dlog,"Sensor output");
		LowPass range=new LowPass(log1, 0.5f);
		SampleLogger log2=new SampleLogger(range,dlog,"Low-pass");
		
		
		
		// Show range to obstacle;
		while (!Button.ESCAPE.isDown()) {
			LCD.clear();
			log2.fetchSample(obstacle,0);
			LCD.drawString("Range: "+obstacle[0], 0, 1);
			dlog.finishLine();
			Delay.msDelay(150);
		}

	}
	




}
