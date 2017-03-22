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
		Sound.MUSIC_MAIN.play();
	}
	
	@AfterClass
	public static void tearDown() {
		audio.Sound.MUSIC_MAIN.stop();
	}

	/**
	 * Test the Play function
	 */
	@Test
	public void testPlay() {
		Assert.assertEquals(true, Sound.MUSIC_MAIN.isRunning());
	}
	
}
