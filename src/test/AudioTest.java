package test;

import audio.Sound;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AudioTest {
	
	/**
	 * Set up the objects to be used in the tests
	 */
	@BeforeClass
	public static void setUp() {
		Sound.MUSIC.play();
	}
	
	@AfterClass
	public static void tearDown() {
		audio.Sound.MUSIC.stop();
	}

	/**
	 * Test the Play function
	 */
	@Test
	public void testPlay() {
		Assert.assertEquals(true, Sound.MUSIC.isRunning());
	}
	
	/**
	 * Test the setVol function
	 */
	@Test
	public void testVol() {
		//TODO
	}
}
