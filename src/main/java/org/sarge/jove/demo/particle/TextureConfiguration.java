package org.sarge.jove.demo.particle;

import java.io.IOException;

import org.sarge.jove.io.*;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.common.DescriptorResource;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.core.Command.Pool;
import org.sarge.jove.platform.vulkan.image.*;
import org.sarge.jove.platform.vulkan.image.Image.Descriptor;
import org.sarge.jove.platform.vulkan.memory.MemoryProperties;
import org.sarge.jove.platform.vulkan.pipeline.Barrier;
import org.sarge.jove.platform.vulkan.util.FormatBuilder;
import org.springframework.context.annotation.*;

@Configuration
public class TextureConfiguration {
	@Bean
	public DescriptorResource combined(Sampler sampler, View image) {
		return sampler.resource(image);
	}

	@Bean
	Sampler sampler(LogicalDevice dev) {
		return new Sampler.Builder().build(dev);
	}

	@Bean
	View image(DataSource data, Pool graphics, ApplicationConfiguration cfg) throws IOException {
		// Load texture image
		final var loader = new ResourceLoaderAdapter<>(data, new NativeImageLoader());
		final ImageData image = loader.load(cfg.getTexture());
//		final var loader = new ResourceLoaderAdapter<>(data, new VulkanImageLoader());
//		final ImageData image = loader.load("particle_fire.ktx2");
		final VkFormat format = FormatBuilder.format(image.layout());

		// Create descriptor
		final Descriptor descriptor = new Descriptor.Builder()
				.type(VkImageType.TWO_D)
				.aspect(VkImageAspect.COLOR)
				.extents(image.size())
				.format(format)
				.mipLevels(image.levels().size())
				.build();

		// Init image memory properties
		final var props = new MemoryProperties.Builder<VkImageUsageFlag>()
				.usage(VkImageUsageFlag.TRANSFER_DST)
				.usage(VkImageUsageFlag.SAMPLED)
				.required(VkMemoryProperty.DEVICE_LOCAL)
				.build();

		// Create texture
		final Image texture = new DefaultImage.Builder()
				.descriptor(descriptor)
				.properties(props)
				.build(graphics.device());

		// Prepare texture
		new Barrier.Builder()
				.source(VkPipelineStage.TOP_OF_PIPE)
				.destination(VkPipelineStage.TRANSFER)
				.image(texture)
					.newLayout(VkImageLayout.TRANSFER_DST_OPTIMAL)
					.destination(VkAccess.TRANSFER_WRITE)
					.build()
				.build()
				.submit(graphics);

		// Create staging buffer
		final VulkanBuffer staging = VulkanBuffer.staging(graphics.device(), image.data());

		// Copy staging to texture
		new ImageTransferCommand.Builder()
				.buffer(staging)
				.image(texture)
				.layout(VkImageLayout.TRANSFER_DST_OPTIMAL)
				.region(image)
				.build()
				.submit(graphics);

		// Release staging
		staging.destroy();

		// Transition to sampled image
		new Barrier.Builder()
			.source(VkPipelineStage.TRANSFER)
			.destination(VkPipelineStage.FRAGMENT_SHADER)
			.image(texture)
				.oldLayout(VkImageLayout.TRANSFER_DST_OPTIMAL)
				.newLayout(VkImageLayout.SHADER_READ_ONLY_OPTIMAL)
				.source(VkAccess.TRANSFER_WRITE)
				.destination(VkAccess.SHADER_READ)
				.build()
			.build()
			.submit(graphics);

		// Create texture view
		return new View.Builder(texture)
				.mapping(ComponentMapping.of(image.components()))
				.build();
	}
}
