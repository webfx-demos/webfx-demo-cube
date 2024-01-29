# webfx-demo-cube

A simple WebGL demo coded with [WebFX](https://github.com/webfx-project/webfx) and realised as followed:

- The cube scene has first been created by following the [WebGL tutorial](https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Tutorial), and then the following additions have been made:

    - multi-textures (applying a different texture on each cube face)
    - mouse steering (rotating the cube with the mouse)
    - keyboard steering (moving the camera with the arrow keys)
    - Code door (adding a double-faces door inside the cube to access this GitHub repo)

We used [JOML](https://github.com/JOML-CI/JOML) for 3D matrix computations (to be more accurate our own GWT-compatible [fork](https://github.com/webfx-libs/webfx-lib-joml/tree/webfx) of JOML).

- The smoke scene is a modification of this original [demo](https://codepen.io/PavelDoGreat/pen/zdWzEL) made by Pavel Dobryakov, with the following additions:

    - transparency management (so it can be displayed over the cube scene)
    - new texture width & height factors [0..1] to have different effect amplitudes

While in the original demo the effect is driven by the mouse, in this demo it is driven by the handwriting pen (see below).

- The text scene is a completely new JavaFX code. The handwriting effect has been realised as followed:

  - This single-stroke [online text](https://www.templatemaker.nl/singlelinetext/) generator (font = Work Sans) has been used to create a SVG version of the text (as a series of SVG paths).
  - The global SVG transform has been applied on each SVG path (using Inkscape Extensions > Modify Path > Apply Transform)
  - The JavaFX code analyses the SVG file and maps each SVG path into a JavaFX Path.
  - The handwriting effect is achieved by increasing the value of each Path.strokeDashArray.
  - The exact position of the pen is computed using some Path interpolation code.

## Running this demo on your local machine

`mvn package -P gwt-compile`

The generated `index.html` file is located at:

`webfx-demo-cube-application-gwt/target/webfx-demo-cube-application-gwt-0.1.0-SNAPSHOT/webfx_demo_cube_application_gwt/index.html`

Locate and open this file from your IDE, or any webserver (pictures & videos will be blocked by CORS if simply using `file://` protocol).