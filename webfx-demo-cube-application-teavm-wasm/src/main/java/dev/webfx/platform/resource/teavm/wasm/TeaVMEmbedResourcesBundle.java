package dev.webfx.platform.resource.teavm.wasm;

import dev.webfx.platform.resource.spi.impl.teavm.TeaVMResourceBundle;
import org.teavm.classlib.ResourceSupplier;
import org.teavm.classlib.ResourceSupplierContext;

public final class TeaVMEmbedResourcesBundle extends TeaVMResourceBundle implements ResourceSupplier {

    private static final String[] RESOURCE_PATHS = {
        "dev/webfx/demo/cube/cubescene/fragmentShader.glsl",
        "dev/webfx/demo/cube/cubescene/vertexShader.glsl",
        "dev/webfx/demo/cube/smokescene/advectionManualFilteringShader.glsl",
        "dev/webfx/demo/cube/smokescene/advectionShader.glsl",
        "dev/webfx/demo/cube/smokescene/baseVertexShader.glsl",
        "dev/webfx/demo/cube/smokescene/clearShader.glsl",
        "dev/webfx/demo/cube/smokescene/curlShader.glsl",
        "dev/webfx/demo/cube/smokescene/displayShader.glsl",
        "dev/webfx/demo/cube/smokescene/divergenceShader.glsl",
        "dev/webfx/demo/cube/smokescene/gradientSubtractShader.glsl",
        "dev/webfx/demo/cube/smokescene/pressureShader.glsl",
        "dev/webfx/demo/cube/smokescene/splatShader.glsl",
        "dev/webfx/demo/cube/smokescene/vorticityShader.glsl",
        "dev/webfx/demo/cube/textscene/text.svg",
        "dev/webfx/platform/meta/exe/exe.properties"
    };

    // Note: called at TeaVM build time, not at runtime
    @Override
    public String[] supplyResources(ResourceSupplierContext context) {
        return RESOURCE_PATHS;
    }

    public TeaVMEmbedResourcesBundle() {
        super(RESOURCE_PATHS);
    }

}