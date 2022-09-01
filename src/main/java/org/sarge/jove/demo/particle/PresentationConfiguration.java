package org.sarge.jove.demo.particle;

import java.util.*;

import org.sarge.jove.common.*;
import org.sarge.jove.control.Frame;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.platform.vulkan.render.FrameBuffer.Group;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
class PresentationConfiguration {
	@Autowired private ApplicationConfiguration cfg;

	@Bean
	public static Surface surface(Handle surface, PhysicalDevice dev) {
		return new Surface(surface, dev).cached();
	}

	@Bean
	public Swapchain swapchain(LogicalDevice dev, Surface surface) {
	    final VkPresentModeKHR mode = surface.mode(VkPresentModeKHR.MAILBOX_KHR);
	    final VkSurfaceFormatKHR format = surface.format(VkFormat.B8G8R8_UNORM, VkColorSpaceKHR.SRGB_NONLINEAR_KHR, null);
	    return new Swapchain.Builder(surface)
				.count(cfg.getFrameCount())
				.presentation(mode)
				.format(format)
				.usage(VkImageUsageFlag.TRANSFER_SRC)
				.clear(Colour.BLACK)
				.build(dev);
	}

	@Bean
	static Group frames(Swapchain swapchain, RenderPass pass) {
		return new Group(swapchain, pass, List.of());
	}

	@Bean
	static FrameBuilder builder(Group group, @Qualifier("graphics") Command.Pool pool) {
		return new FrameBuilder(group::buffer, pool::allocate, VkCommandBufferUsage.ONE_TIME_SUBMIT);
	}

	@Bean
	FrameProcessor processor(Swapchain swapchain, FrameBuilder builder, Collection<Frame.Listener> listeners) {
		final var proc = new FrameProcessor(swapchain, builder, cfg.getFrameCount());
		listeners.forEach(proc::add);
		return proc;
	}
}
