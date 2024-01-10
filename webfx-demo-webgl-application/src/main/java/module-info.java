// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.demo.webgl.application {

    // Direct dependencies modules
    requires javafx.graphics;
    requires webfx.kit.util.scene;
    requires webfx.kit.webgl;
    requires webfx.platform.console;
    requires webfx.platform.uischeduler;

    // Exported packages
    exports dev.webfx.demo.webgl;

    // Provided services
    provides javafx.application.Application with dev.webfx.demo.webgl.WebGLApplication;

}