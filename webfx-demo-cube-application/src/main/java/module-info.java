// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.demo.cube.application {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.extras.util.color;
    requires webfx.kit.launcher;
    requires webfx.kit.util;
    requires webfx.kit.webgl;
    requires webfx.lib.joml;
    requires webfx.platform.console;
    requires webfx.platform.resource;
    requires webfx.platform.typedarray;
    requires webfx.platform.uischeduler;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.demo.cube;
    exports dev.webfx.demo.cube.cubescene;
    exports dev.webfx.demo.cube.smokescene;
    exports dev.webfx.demo.cube.textscene;
    exports dev.webfx.demo.cube.textscene.interpol;

    // Resources packages
    opens dev.webfx.demo.cube;
    opens dev.webfx.demo.cube.cubescene;
    opens dev.webfx.demo.cube.smokescene;
    opens dev.webfx.demo.cube.textscene;

    // Provided services
    provides javafx.application.Application with dev.webfx.demo.cube.CubeApplication;

}