package org.sarge.jove.demo.particle;

import java.nio.ByteBuffer;
import java.util.Set;

import org.sarge.jove.geometry.Matrix;
import org.sarge.jove.platform.desktop.Window;
import org.sarge.jove.platform.vulkan.*;
import org.sarge.jove.platform.vulkan.core.*;
import org.sarge.jove.platform.vulkan.memory.*;
import org.sarge.jove.platform.vulkan.pipeline.PipelineLayout;
import org.sarge.jove.platform.vulkan.render.*;
import org.sarge.jove.platform.vulkan.render.DescriptorLayout.Binding;
import org.sarge.jove.scene.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
public class CameraConfiguration {
	@Autowired private LogicalDevice dev;

//	private final Matrix projection;
//	private final Camera cam = new Camera();
//	private final OrbitalCameraController controller;

//	public CameraConfiguration(Swapchain swapchain) {
//		projection = Projection.DEFAULT.matrix(0.1f, 100, swapchain.extents());
//		controller = new OrbitalCameraController(cam, swapchain.extents());
//		controller.radius(3);
//		controller.scale(0.25f);
//	}
//
//	@Autowired
//	void init(Window window) {
////		final var bindings = new Action.Bindings();
////		bindings.bind(new Button<T>("ESCAPE", null), null);
//
//		final MouseDevice mouse = window.mouse();
//		mouse.pointer().bind(controller::update);
//		mouse.wheel().bind(controller::zoom);
////		window.keyboard().keyboard().bind(bindings::accept);
//		window.keyboard().keyboard().bind(e -> System.exit(0));
//	}

	@Autowired
	void init(Window window) {
		window.keyboard().keyboard().bind(ignored -> System.exit(0));
	}

//	@Bean
//	@Order
//	public static PushConstantUpdateCommand update(PipelineLayout layout) {
//	    return PushConstantUpdateCommand.of(layout);
//	}

	private final Binding binding = new Binding.Builder()
			.stage(VkShaderStage.VERTEX)
			.stage(VkShaderStage.GEOMETRY)
			.type(VkDescriptorType.UNIFORM_BUFFER)
			.build();

	@Bean
	DescriptorLayout layout() {
		return DescriptorLayout.create(dev, Set.of(binding));
	}

	@Bean
	DescriptorSet descriptor(DescriptorLayout layout) {
		final DescriptorPool pool = new DescriptorPool.Builder()
				.add(VkDescriptorType.UNIFORM_BUFFER, 1)
				.build(dev);

		return pool.allocate(layout).iterator().next();
	}

	@Bean
	Command bind(DescriptorSet set, PipelineLayout layout) {
		return set.bind(layout);
	}

	@Bean
	public ResourceBuffer init(AllocationService service, Swapchain swapchain, DescriptorSet set) {
		final var props = new MemoryProperties.Builder<VkBufferUsageFlag>()
				.usage(VkBufferUsageFlag.UNIFORM_BUFFER)
				.required(VkMemoryProperty.HOST_VISIBLE)
				.required(VkMemoryProperty.HOST_COHERENT)
				.build();

		final VulkanBuffer b = VulkanBuffer.create(dev, service, 2 * Matrix.IDENTITY.length(), props);

		final ResourceBuffer uniform = new ResourceBuffer(b, VkDescriptorType.UNIFORM_BUFFER, 0);
		final Matrix projection = Projection.DEFAULT.matrix(0.1f, 100, swapchain.extents());
		final Camera cam = new Camera();
		final ByteBuffer bb = uniform.buffer();
		cam.matrix().buffer(bb);
		projection.buffer(bb);

		set.set(binding, uniform);

		return uniform;
	}


//	@Bean
//	public FrameListener update(LogicalDevice dev, AllocationService service) { //PushConstantUpdateCommand update) {
//		return (start, end) -> {
////			final Matrix tilt = Rotation.of(Vector.X, MathsUtil.toRadians(-90)).matrix();
////			final Matrix rot = Rotation.of(Vector.Y, MathsUtil.toRadians(120)).matrix();
////			final Matrix model = rot.multiply(tilt);
////			final Matrix matrix = projection.multiply(cam.matrix()); //.multiply(model);
//			final Matrix matrix = Matrix.IDENTITY; // translation(new Vector(-0.1f, -0.1f, 0));
//
//
////					Matrix.IDENTITY; // cam.matrix(); //.multiply(model);
////			update.data().rewind();
//			matrix.buffer(update.data());
//			update.data().rewind();
//		};
//	}
}
