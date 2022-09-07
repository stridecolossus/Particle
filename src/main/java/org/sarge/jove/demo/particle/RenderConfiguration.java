package org.sarge.jove.demo.particle;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.pipeline.Pipeline;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.RenderLoop;
import org.springframework.context.annotation.*;

@Configuration
public class RenderConfiguration {
	@Bean("pipeline.bind")
	static Command pipeline(Pipeline pipeline) {
		return pipeline.bind();
	}

	@Bean("vbo.bind")
	static Command vbo(VertexBuffer vbo) {
		return vbo.bind(0);
	}

	@Bean
	static RenderSequence sequence(List<Command> commands, Model model) {
		return buffer -> {
			final DrawCommand draw = DrawCommand.of(model);
			commands.forEach(buffer::add);
			buffer.add(draw);
		};
	}

	@Bean
	public RenderPass pass(Swapchain swapchain) {
		final Attachment col = new Attachment.Builder()
				.format(swapchain.format())
				.load(VkAttachmentLoadOp.CLEAR)
				.store(VkAttachmentStoreOp.STORE)
				.finalLayout(VkImageLayout.PRESENT_SRC_KHR)
				.build();

		return new RenderPass.Builder()
				.subpass()
					.colour(col)
					.build()
				.build(swapchain.device());
	}

	@Bean
	public static RenderLoop loop(ScheduledExecutorService executor, FrameProcessor proc, RenderSequence seq) {
		final Runnable task = () -> proc.render(seq);
		final RenderLoop loop = new RenderLoop(executor);
		loop.start(task);
		return loop;
	}
}
