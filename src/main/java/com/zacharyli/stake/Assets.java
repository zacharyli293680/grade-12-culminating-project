package com.zacharyli.stake;

// loads images and audio from the classpath (src/main/resources) and caches them

// imports
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// assets class
public final class Assets {

	private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

	private Assets() {}

	// loads an image from /images on the classpath, e.g. Assets.image("Menu1.png")
	// parameters: String file name
	// return: Image, or null if the file is missing
	public static Image image(String name) {
		return load("/images/" + name);
	}

	// loads a card face from /images/cards by image number 1-52
	// parameters: int image number
	// return: Image
	public static Image card(int imageNum) {
		return load("/images/cards/" + imageNum + ".png");
	}

	// loads the card back
	// parameters: none
	// return: Image
	public static Image cardBack() {
		return load("/images/cards/CardBack.png");
	}

	// opens an audio clip from /audio on the classpath; the caller starts or loops it
	// parameters: String file name
	// return: Clip, or null if the file is missing or unsupported
	public static Clip audio(String name) {
		URL url = Assets.class.getResource("/audio/" + name);
		if (url == null) {
			return null;
		}
		try (AudioInputStream stream = AudioSystem.getAudioInputStream(url)) {
			Clip clip = AudioSystem.getClip();
			clip.open(stream);
			return clip;
		} catch (Exception e) {
			return null;
		}
	}

	// opens a raw resource stream, e.g. the seed players file
	// parameters: String absolute classpath path
	// return: InputStream, or null if missing
	public static InputStream stream(String path) {
		return Assets.class.getResourceAsStream(path);
	}

	private static Image load(String path) {
		return IMAGE_CACHE.computeIfAbsent(path, p -> {
			URL url = Assets.class.getResource(p);
			if (url == null) {
				System.err.println("Missing resource: " + p);
				return null;
			}
			try {
				return ImageIO.read(url);
			} catch (IOException e) {
				System.err.println("Could not read resource: " + p);
				return null;
			}
		});
	}
}
