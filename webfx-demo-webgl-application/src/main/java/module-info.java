// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.demo.webgl.application {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.extras.switches;
    requires webfx.extras.util.color;
    requires webfx.kit.launcher;
    requires webfx.kit.util;
    requires webfx.kit.webgl;
    requires webfx.lib.joml;
    requires webfx.platform.console;
    requires webfx.platform.resource;
    requires webfx.platform.typedarray;
    requires webfx.platform.uischeduler;

    // Exported packages
    exports dev.webfx.demo.webgl;
    exports dev.webfx.demo.webgl.cube;
    exports dev.webfx.demo.webgl.smoke;
    exports dev.webfx.demo.webgl.text;
    exports dev.webfx.demo.webgl.text.interpol;

    // Resources packages
    opens dev.webfx.demo.webgl;
    opens dev.webfx.demo.webgl.cube;
    opens dev.webfx.demo.webgl.smoke;
    opens dev.webfx.demo.webgl.text;

    // Provided services
    provides javafx.application.Application with dev.webfx.demo.webgl.WebGLDemo;

}