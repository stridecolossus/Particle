package org.sarge.jove.demo.particle;

import java.nio.ByteBuffer;

import javax.annotation.PostConstruct;

import org.sarge.jove.control.*;
import org.sarge.jove.geometry.*;
import org.sarge.jove.model.Model;
import org.sarge.jove.particle.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.DeviceMemory.Region;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.sarge.jove.util.Randomiser;
import org.springframework.context.annotation.*;

@Configuration
public class ParticleSystemConfiguration {
	private final ApplicationConfiguration cfg;
	private final ParticleSystem sys;
	private final Model model;
	private final Animator animator;

	public ParticleSystemConfiguration(ApplicationConfiguration cfg) {
		this.cfg = cfg;
		this.sys = system(cfg);
		this.animator = new Animator(sys);
		this.model = new ParticleModel(sys);
	}

	private static ParticleSystem system(ApplicationConfiguration cfg) {
		final var sys = new ParticleSystem();
		sys.policy(new IncrementGenerationPolicy(10, cfg.getMax()));
		sys.lifetime(5000L);
		sys.vector(new ConeVectorFactory(Vector.Y, 1, new Randomiser()));
		sys.add(Influence.of(Vector.Y.invert()));
		sys.add(new Plane(Vector.Y, 0).behind(), new ReflectionCollision(0.3f));
		return sys;
	}

	@Bean
	public Animator animator() {
		return animator;
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

		final int len = cfg.getMax() * Point.LAYOUT.length();
		final VulkanBuffer buffer = VulkanBuffer.create(dev, len, props);
		return new VertexBuffer(buffer);
	}

	@PostConstruct
	void start() {
		final Player player = new Player();
		player.add(animator);
		player.play();
	}

	@Bean
	public Frame.Listener update(VertexBuffer vbo) {
		return () -> {
			final Region region = vbo.memory().map();
			final ByteBuffer bb = region.buffer();
			model.vertices().buffer(bb);
			region.unmap();
		};
	}
}
