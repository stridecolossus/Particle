package org.sarge.jove.demo.particle;

import java.nio.ByteBuffer;

import org.sarge.jove.control.*;
import org.sarge.jove.io.*;
import org.sarge.jove.model.Model;
import org.sarge.jove.particle.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.DeviceMemory.Region;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.sarge.jove.util.Randomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class ParticleSystemConfiguration {
	private final ParticleSystem sys;
	private final Model model;
	private final Animator animator;

	public ParticleSystemConfiguration(DataSource classpath, @Value("${spring.profiles.active}") String profile) {
		final var loader = new ResourceLoaderAdapter<>(classpath, new ParticleSystemLoader(new Randomiser()));
		this.sys = loader.load(profile + ".xml");
		this.animator = new Animator(sys);
		this.model = new ParticleModel(sys);
	}

	@Bean
	public ParticleSystem system() {
		return sys;
	}

	@Bean
	public Model model() {
		return model;
	}

	@Bean
	public VertexBuffer vbo(LogicalDevice dev) {
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.VERTEX_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.optimal(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		final int len = sys.max() * model.layout().stride();
		final VulkanBuffer buffer = VulkanBuffer.create(dev, len, props);
		return new VertexBuffer(buffer);
	}

	@Bean
	public Animator animator() {
		return animator;
	}

	@Bean
	public Player player() {
		final Player player = new Player();
		player.add(animator);
		player.play();
		return player;
	}

	@Bean
	public Frame.Listener updateVertexBuffer(VertexBuffer vbo) {
		return () -> {
			final Region region = vbo.memory().map();
			final ByteBuffer bb = region.buffer();
			model.vertices().buffer(bb);
			region.unmap();
		};
	}
}
