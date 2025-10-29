// File managed by WebFX (DO NOT EDIT MANUALLY)
package dev.webfx.platform.resource.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import dev.webfx.platform.resource.spi.impl.web.WebResourceBundleBase;

public interface GwtEmbedResourcesBundle extends ClientBundle {

    GwtEmbedResourcesBundle R = GWT.create(GwtEmbedResourcesBundle.class);
    @Source("dev/webfx/demo/cube/cubescene/fragmentShader.glsl")
    TextResource r1();

    @Source("dev/webfx/demo/cube/cubescene/vertexShader.glsl")
    TextResource r2();

    @Source("dev/webfx/demo/cube/smokescene/advectionManualFilteringShader.glsl")
    TextResource r3();

    @Source("dev/webfx/demo/cube/smokescene/advectionShader.glsl")
    TextResource r4();

    @Source("dev/webfx/demo/cube/smokescene/baseVertexShader.glsl")
    TextResource r5();

    @Source("dev/webfx/demo/cube/smokescene/clearShader.glsl")
    TextResource r6();

    @Source("dev/webfx/demo/cube/smokescene/curlShader.glsl")
    TextResource r7();

    @Source("dev/webfx/demo/cube/smokescene/displayShader.glsl")
    TextResource r8();

    @Source("dev/webfx/demo/cube/smokescene/divergenceShader.glsl")
    TextResource r9();

    @Source("dev/webfx/demo/cube/smokescene/gradientSubtractShader.glsl")
    TextResource r10();

    @Source("dev/webfx/demo/cube/smokescene/pressureShader.glsl")
    TextResource r11();

    @Source("dev/webfx/demo/cube/smokescene/splatShader.glsl")
    TextResource r12();

    @Source("dev/webfx/demo/cube/smokescene/vorticityShader.glsl")
    TextResource r13();

    @Source("dev/webfx/demo/cube/textscene/text.svg")
    TextResource r14();

    @Source("dev/webfx/platform/meta/exe/exe.properties")
    TextResource r15();



    final class ProvidedGwtResourceBundle extends WebResourceBundleBase {
        public ProvidedGwtResourceBundle() {
            registerResource("dev/webfx/demo/cube/cubescene/fragmentShader.glsl", () -> R.r1().getText());
            registerResource("dev/webfx/demo/cube/cubescene/vertexShader.glsl", () -> R.r2().getText());
            registerResource("dev/webfx/demo/cube/smokescene/advectionManualFilteringShader.glsl", () -> R.r3().getText());
            registerResource("dev/webfx/demo/cube/smokescene/advectionShader.glsl", () -> R.r4().getText());
            registerResource("dev/webfx/demo/cube/smokescene/baseVertexShader.glsl", () -> R.r5().getText());
            registerResource("dev/webfx/demo/cube/smokescene/clearShader.glsl", () -> R.r6().getText());
            registerResource("dev/webfx/demo/cube/smokescene/curlShader.glsl", () -> R.r7().getText());
            registerResource("dev/webfx/demo/cube/smokescene/displayShader.glsl", () -> R.r8().getText());
            registerResource("dev/webfx/demo/cube/smokescene/divergenceShader.glsl", () -> R.r9().getText());
            registerResource("dev/webfx/demo/cube/smokescene/gradientSubtractShader.glsl", () -> R.r10().getText());
            registerResource("dev/webfx/demo/cube/smokescene/pressureShader.glsl", () -> R.r11().getText());
            registerResource("dev/webfx/demo/cube/smokescene/splatShader.glsl", () -> R.r12().getText());
            registerResource("dev/webfx/demo/cube/smokescene/vorticityShader.glsl", () -> R.r13().getText());
            registerResource("dev/webfx/demo/cube/textscene/text.svg", () -> R.r14().getText());
            registerResource("dev/webfx/platform/meta/exe/exe.properties", () -> R.r15().getText());

        }
    }
}
