package org.sarge.jove.demo.particle;

import static org.sarge.lib.util.Check.*;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class ApplicationConfiguration {
	private String title;
	private int frames = 2;
	private String data;
	private List<String> features;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = notEmpty(title);
	}

	public int getFrameCount() {
		return frames;
	}

	public void setFrameCount(int frames) {
		this.frames = oneOrMore(frames);
	}

	public String getDataDirectory() {
		return data;
	}

	public void setDataDirectory(String data) {
		this.data = notEmpty(data);
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = List.copyOf(features);
	}
}
