package org.sarge.jove.demo.particle;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import org.sarge.jove.model.Model;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.pipeline.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.scene.*;
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
	static RenderSequence sequence(List<Command> commands, Model model, Camera cam, PushConstantUpdateCommand update) {

		final List<Command> list = new ArrayList<>(commands);
		list.remove(update);

		return buffer -> {
			// TODO - done before render pass starts and before command executed!
			update.data().rewind();
//			update.data().putInt((int) Instant.now().toEpochMilli()); // TODO - time from start of app?
			cam.matrix().buffer(update.data());
//			Matrix.IDENTITY.buffer(update.data());

			final DrawCommand draw = DrawCommand.of(model);
			list.forEach(buffer::add);
			buffer.add(update);
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
		final Runnable task = () -> {
			try {
				proc.render(seq);
			}
			catch(Throwable t) {
				// TODO
				t.printStackTrace();
				System.exit(-1);
			}
		};
		final RenderLoop loop = new RenderLoop(executor);
		loop.start(task);
		return loop;
	}
}
