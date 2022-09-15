package org.sarge.jove.demo.particle;

import java.nio.ByteBuffer;

import org.sarge.jove.control.Button.Action;
import org.sarge.jove.control.Player;
import org.sarge.jove.geometry.Matrix;
import org.sarge.jove.geometry.Matrix.Matrix4;
import org.sarge.jove.platform.desktop.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
public class CameraConfiguration {
	@Autowired private LogicalDevice dev;

	private final Camera cam = new Camera();
	private final OrbitalCameraController controller;

	public CameraConfiguration(Swapchain swapchain) {
		controller = new OrbitalCameraController(cam, swapchain.extents());
		controller.radius(3);
		controller.scale(0.25f);
	}

	@Autowired
	void init(Window window, Player player) {
		final MouseDevice mouse = window.mouse();
		mouse.pointer().bind(controller::update);
		mouse.wheel().bind(controller::zoom);

		// TODO
		window.keyboard().keyboard().bind(e -> {
			if(e.id().equals("ESCAPE")) {
				System.exit(0);
			}
			else
			if(e.id().equals("SPACE") && (e.action() == Action.PRESS)) {
				if(player.isPlaying()) {
					player.pause();
				}
				else {
					player.play();
				}
			}
		});
	}

	@Bean
	public Camera camera() {
		return cam;
	}

	@Bean
	public ResourceBuffer init(Swapchain swapchain) {
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.UNIFORM_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.optimal(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		final VulkanBuffer b = VulkanBuffer.create(dev, 1 * Matrix4.IDENTITY.length(), props);

		final ResourceBuffer uniform = new ResourceBuffer(b, VkDescriptorType.UNIFORM_BUFFER, 0);
		final Matrix projection = Projection.DEFAULT.matrix(0.1f, 100, swapchain.extents());
		final ByteBuffer bb = uniform.buffer();
		projection.buffer(bb);

		return uniform;
	}
}
