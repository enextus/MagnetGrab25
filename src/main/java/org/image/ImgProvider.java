package org.image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ImgProvider {
	private static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
	private static final String IMG_PROPERTIES_PATH = "/img.properties";
	private static final String IMAGE_DIRECTORY = "/img";
	private static final String ERROR_READING_DIRECTORY = "Failed to read image directory: %s";
	private static final String ERROR_NO_IMAGES = "No images found in the directory: %s";
	private static final Random RANDOM = new Random();

	public static String getRandomImagePath() {

		List<String> imagePaths = new ArrayList<>();

		try (InputStream inputStream = App.class.getResourceAsStream(IMAGE_DIRECTORY + IMG_PROPERTIES_PATH)) {
			assert inputStream != null;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET_UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					imagePaths.add(IMAGE_DIRECTORY + "/" + line);
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException(String.format(ERROR_READING_DIRECTORY, IMAGE_DIRECTORY), e);
		}

		if (imagePaths.isEmpty()) {
			throw new RuntimeException(String.format(ERROR_NO_IMAGES, IMAGE_DIRECTORY));
		}

		return imagePaths.get(RANDOM.nextInt(imagePaths.size()));
	}

	/**
	 * Reads a resource file located at the specified path and returns its content as a string.
	 * The file is read using UTF-8 encoding.
	 *
	 * @param imagePath The path of the resource file.
	 * @return The content of the resource file as a string.
	 * @throws IllegalArgumentException If the resource file is not found.
	 * @throws RuntimeException         If there's an error reading the resource file.
	 */
	public static String readResourceFileToString(String imagePath) throws IOException {

		InputStream inputStream = ImgProcessor.class.getResourceAsStream(imagePath);

		// ReadingAndPrintingBytes(inputStream);

		if (inputStream == null) {
			throw new IllegalArgumentException("Resource file not found: " + imagePath);
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

}
