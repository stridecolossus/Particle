package org.sarge.jove.demo.particle;

import java.util.Set;

import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.common.DescriptorResource;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.pipeline.PipelineLayout;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.platform.vulkan.render.DescriptorLayout.Binding;
import org.springframework.context.annotation.*;

@Configuration
public class DescriptorConfiguration {
	private final Binding uniform = new Binding.Builder()
			.binding(0)
			.stage(VkShaderStage.GEOMETRY)
			.type(VkDescriptorType.UNIFORM_BUFFER)
			.build();

	private final Binding sampler = new Binding.Builder()
			.binding(1)
			.stage(VkShaderStage.FRAGMENT)
			.type(VkDescriptorType.COMBINED_IMAGE_SAMPLER)
			.build();

	@Bean
	public DescriptorLayout layout(LogicalDevice dev) {
		return DescriptorLayout.create(dev, Set.of(uniform, sampler));
	}

	@Bean
	DescriptorSet descriptor(DescriptorLayout layout, LogicalDevice dev, ResourceBuffer uniform, DescriptorResource combined) {
		final DescriptorPool pool = new DescriptorPool.Builder()
				.add(VkDescriptorType.UNIFORM_BUFFER, 1)
				.add(VkDescriptorType.COMBINED_IMAGE_SAMPLER, 1)
				.build(dev);

		final DescriptorSet set = pool.allocate(layout).iterator().next();

		set.set(this.uniform, uniform);
		set.set(this.sampler, combined);
		DescriptorSet.update(dev, Set.of(set));

		return set;
	}

	@Bean("descriptor.bind")
	public static Command bind(DescriptorSet set, PipelineLayout layout) {
		return set.bind(layout);
	}
}
