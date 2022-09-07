package org.sarge.jove.demo.particle;

import java.io.*;

import org.sarge.jove.common.Rectangle;
import org.sarge.jove.io.*;
import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.VkShaderStage;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.pipeline.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.context.annotation.*;

@Configuration
class PipelineConfiguration {
	private final LogicalDevice dev;
	private final ResourceLoaderAdapter<InputStream, Shader> loader;

	public PipelineConfiguration(LogicalDevice dev, DataSource classpath) {
		this.dev = dev;
		this.loader = new ResourceLoaderAdapter<>(classpath, new Shader.Loader(dev));
	}

	private Shader shader(String name) {
		return loader.load(String.format("particle.%s.spv", name));
	}

	@Bean
	Shader vertex() throws IOException {
		return shader("vert");
	}

	@Bean
	Shader geometry() throws IOException {
		return shader("geom");
	}

	@Bean
	Shader fragment() throws IOException {
		return shader("frag");
	}

	@Bean
	PipelineLayout pipelineLayout(DescriptorLayout layout) {
		return new PipelineLayout.Builder()
				.add(layout)
				//.add(new PushConstantRange(0, Matrix.IDENTITY.length(), Set.of(VkShaderStage.VERTEX)))
				.build(dev);
	}

//	@Autowired
//	void init(DescriptorSet set, ResourceBuffer uniform) {
//		DescriptorSet.update(dev, Set.of(set));
//	}

	@Bean
	public Pipeline pipeline(RenderPass pass, Swapchain swapchain, Shader vertex, Shader geometry, Shader fragment, PipelineLayout pipelineLayout, Model model) {
		return new Pipeline.Builder()
				.layout(pipelineLayout)
				.pass(pass)
				.viewport(new Rectangle(swapchain.extents()))
				.shader(VkShaderStage.VERTEX, vertex)
				.shader(VkShaderStage.GEOMETRY, geometry)
				.shader(VkShaderStage.FRAGMENT, fragment)
				.input()
					.add(model.layout())
					.build()
				.assembly()
					.topology(model.primitive())
					.build()
				.build(null, dev);
	}
}
