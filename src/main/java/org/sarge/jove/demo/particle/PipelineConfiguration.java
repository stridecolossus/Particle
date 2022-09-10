package org.sarge.jove.demo.particle;

import java.io.InputStream;
import java.util.Map;

import org.sarge.jove.common.Rectangle;
import org.sarge.jove.io.*;
import org.sarge.jove.model.Model;
import org.sarge.jove.particle.ParticleSystem;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.LogicalDevice;
import org.sarge.jove.platform.vulkan.pipeline.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.springframework.context.annotation.*;

@Configuration
class PipelineConfiguration {
	private final LogicalDevice dev;
	private final ResourceLoaderAdapter<InputStream, Shader> loader;
	private final VkSpecializationInfo constants;

	public PipelineConfiguration(LogicalDevice dev, DataSource classpath, ApplicationConfiguration cfg, ParticleSystem sys) {
		this.dev = dev;
		this.loader = new ResourceLoaderAdapter<>(classpath, new Shader.Loader(dev));
		this.constants = Shader.constants(Map.of(1, cfg.getSize(), 2, (int) sys.lifetime()));
	}

	private Shader shader(String name) {
		return loader.load(String.format("%s.spv", name));
	}

	@Bean
	Shader vertex() {
		return shader("particle.vert");
	}

	@Bean
	Shader geometry() {
		return shader("particle.geom");
	}

	@Bean
	Shader fragment(ApplicationConfiguration cfg) {
		return shader(String.format("%s.frag", cfg.getShader()));
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
				.shader(VkShaderStage.VERTEX)
					.shader(vertex)
					.constants(constants)
					.build()
				.shader(VkShaderStage.GEOMETRY)
					.shader(geometry)
					.constants(constants)
					.build()
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
