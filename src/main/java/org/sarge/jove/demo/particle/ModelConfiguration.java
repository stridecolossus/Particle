package org.sarge.jove.demo.particle;

import java.io.*;

import org.sarge.jove.io.DataSource;
import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.core.Command.Pool;
import org.sarge.jove.platform.vulkan.memory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
public class ModelConfiguration {
	@Autowired private LogicalDevice dev;
	@Autowired private AllocationService allocator;
	@Autowired private Pool graphics;

	@Bean
	public static Model model(DataSource data) throws IOException {
		final ModelBuilder builder = new ModelBuilder();
		try(InputStream in = data.input("galaxy.jpg")) {
			return builder.build(in);
		}
	}

	@Bean
	public VertexBuffer vbo(Model model) {
		// Load model to staging buffer
		final VulkanBuffer staging = VulkanBuffer.staging(dev, allocator, model.vertices());

		// Init buffer memory properties
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.TRANSFER_DST)
				.usage(VkBufferUsageFlag.VERTEX_BUFFER)
				.required(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create destination
		final VulkanBuffer buffer = VulkanBuffer.create(dev, allocator, staging.length(), props);

		// Copy staging to buffer
		staging.copy(buffer).submit(graphics);

		// Release staging
		staging.destroy();

		// Create VBO
		return new VertexBuffer(buffer);
	}
}
