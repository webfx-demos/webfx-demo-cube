// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.demo.webgl.application {

    // Direct dependencies modules
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.kit.util;
    requires webfx.kit.util.scene;
    requires webfx.kit.webgl;
    requires webfx.lib.joml;
    requires webfx.platform.console;
    requires webfx.platform.resource;
    requires webfx.platform.typedarray;
    requires webfx.platform.uischeduler;

    // Exported packages
    exports dev.webfx.demo.webgl;

    // Resources packages
    opens dev.webfx.demo.webgl;

    // Provided services
    provides javafx.application.Application with dev.webfx.demo.webgl.WebGLApplication;

}