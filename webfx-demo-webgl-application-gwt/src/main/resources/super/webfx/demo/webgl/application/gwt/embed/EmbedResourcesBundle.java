// File managed by WebFX (DO NOT EDIT MANUALLY)
package webfx.demo.webgl.application.gwt.embed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import dev.webfx.platform.resource.spi.impl.gwt.GwtResourceBundleBase;

public interface EmbedResourcesBundle extends ClientBundle {

    EmbedResourcesBundle R = GWT.create(EmbedResourcesBundle.class);
    @Source("dev/webfx/demo/webgl/cube/fragmentShader.glsl")
    TextResource r1();

    @Source("dev/webfx/demo/webgl/cube/vertexShader.glsl")
    TextResource r2();

    @Source("dev/webfx/demo/webgl/smoke/advectionManualFilteringShader.glsl")
    TextResource r3();

    @Source("dev/webfx/demo/webgl/smoke/advectionShader.glsl")
    TextResource r4();

    @Source("dev/webfx/demo/webgl/smoke/baseVertexShader.glsl")
    TextResource r5();

    @Source("dev/webfx/demo/webgl/smoke/clearShader.glsl")
    TextResource r6();

    @Source("dev/webfx/demo/webgl/smoke/curlShader.glsl")
    TextResource r7();

    @Source("dev/webfx/demo/webgl/smoke/displayShader.glsl")
    TextResource r8();

    @Source("dev/webfx/demo/webgl/smoke/divergenceShader.glsl")
    TextResource r9();

    @Source("dev/webfx/demo/webgl/smoke/gradientSubtractShader.glsl")
    TextResource r10();

    @Source("dev/webfx/demo/webgl/smoke/pressureShader.glsl")
    TextResource r11();

    @Source("dev/webfx/demo/webgl/smoke/splatShader.glsl")
    TextResource r12();

    @Source("dev/webfx/demo/webgl/smoke/vorticityShader.glsl")
    TextResource r13();

    @Source("dev/webfx/demo/webgl/text/text.svg")
    TextResource r14();

    @Source("dev/webfx/platform/meta/exe/exe.properties")
    TextResource r15();



    final class ProvidedGwtResourceBundle extends GwtResourceBundleBase {
        public ProvidedGwtResourceBundle() {
            registerResource("dev/webfx/demo/webgl/cube/fragmentShader.glsl", R.r1());
            registerResource("dev/webfx/demo/webgl/cube/vertexShader.glsl", R.r2());
            registerResource("dev/webfx/demo/webgl/smoke/advectionManualFilteringShader.glsl", R.r3());
            registerResource("dev/webfx/demo/webgl/smoke/advectionShader.glsl", R.r4());
            registerResource("dev/webfx/demo/webgl/smoke/baseVertexShader.glsl", R.r5());
            registerResource("dev/webfx/demo/webgl/smoke/clearShader.glsl", R.r6());
            registerResource("dev/webfx/demo/webgl/smoke/curlShader.glsl", R.r7());
            registerResource("dev/webfx/demo/webgl/smoke/displayShader.glsl", R.r8());
            registerResource("dev/webfx/demo/webgl/smoke/divergenceShader.glsl", R.r9());
            registerResource("dev/webfx/demo/webgl/smoke/gradientSubtractShader.glsl", R.r10());
            registerResource("dev/webfx/demo/webgl/smoke/pressureShader.glsl", R.r11());
            registerResource("dev/webfx/demo/webgl/smoke/splatShader.glsl", R.r12());
            registerResource("dev/webfx/demo/webgl/smoke/vorticityShader.glsl", R.r13());
            registerResource("dev/webfx/demo/webgl/text/text.svg", R.r14());
            registerResource("dev/webfx/platform/meta/exe/exe.properties", R.r15());

        }
    }
}
