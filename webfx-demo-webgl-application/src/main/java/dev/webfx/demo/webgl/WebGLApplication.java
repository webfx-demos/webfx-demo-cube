package dev.webfx.demo.webgl;

import dev.webfx.kit.util.scene.DeviceSceneUtil;
import dev.webfx.kit.webgl.*;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.uischeduler.UiScheduler;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.joml.Matrix4d;

public class WebGLApplication extends Application {

    private Node webGLNode;

    @Override
    public void start(Stage primaryStage) {
        webGLNode = WebGL.createWebGLNode(640, 480);
        BorderPane borderPane = new BorderPane(webGLNode);
        borderPane.setBackground(Background.fill(Color.PURPLE));
        Scene scene = DeviceSceneUtil.newScene(borderPane, 800, 600, Color.CYAN);
        primaryStage.setScene(scene);
        primaryStage.show();
        //if (WebGL.supportsWebGL())
        UiScheduler.scheduleInAnimationFrame(this::runWebGL, 1);
    }

    private void runWebGL() {
        WebGLRenderingContext gl = WebGL.getContext(webGLNode);
        // Set clear color to black, fully opaque
        gl.clearColor(0, 0, 1, 1);
        // Clear the color buffer with specified clear color
        gl.clear(gl.COLOR_BUFFER_BIT);

        // Vertex shader program
        String vsSource = "    attribute vec4 aVertexPosition;\n" +
                          "    attribute vec4 aVertexColor;\n" +
                          "\n" +
                          "    uniform mat4 uModelViewMatrix;\n" +
                          "    uniform mat4 uProjectionMatrix;\n" +
                          "\n" +
                          "    varying lowp vec4 vColor;\n" +
                          "\n" +
                          "    void main(void) {\n" +
                          "      gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;\n" +
                          "      vColor = aVertexColor;\n" +
                          "    }";

        // Fragment shader program
        String fsSource = "varying lowp vec4 vColor;\n" +
                          "\n" +
                          "    void main(void) {\n" +
                          "      gl_FragColor = vColor;\n" +
                          "    }";

        // Create the shader program
        WebGLProgram shaderProgram = initShaderProgram(gl, vsSource, fsSource);

        // Collect all the info needed to use the shader program.
        // Look up which attribute our shader program is using
        // for aVertexPosition and look up uniform locations.
        ProgramInfo programInfo = new ProgramInfo(
                shaderProgram,
                gl.getAttribLocation(shaderProgram, "aVertexPosition"),
                gl.getAttribLocation(shaderProgram, "aVertexColor"),
                gl.getUniformLocation(shaderProgram, "uProjectionMatrix"),
                gl.getUniformLocation(shaderProgram, "uModelViewMatrix")
        );

        // Here's where we call the routine that builds all the
        // objects we'll be drawing.
        Buffers buffers = initBuffers(gl);

        // Draw the scene
        new AnimationTimer() {
            double cubeRotation = 0.0;
            long then, deltaTime;

            @Override
            public void handle(long now) {
                deltaTime = now - then;
                then = now;
                drawScene(gl, programInfo, buffers, cubeRotation);
                cubeRotation += deltaTime * 1d / 1_000_000_000; // convert to seconds
            }
        }.start();
    }

    private static class ProgramInfo {
        final WebGLProgram program;
        final int vertexPosition;
        final int vertexColor;
        final WebGLUniformLocation projectionMatrix;
        final WebGLUniformLocation modelViewMatrix;

        public ProgramInfo(WebGLProgram program, int vertexPosition, int vertexColor, WebGLUniformLocation projectionMatrix, WebGLUniformLocation modelViewMatrix) {
            this.program = program;
            this.vertexPosition = vertexPosition;
            this.vertexColor = vertexColor;
            this.projectionMatrix = projectionMatrix;
            this.modelViewMatrix = modelViewMatrix;
        }
    }

    private WebGLShader loadShader(WebGLRenderingContext gl, int type, String source) {
        WebGLShader shader = gl.createShader(type);

        // Send the source to the shader object
        gl.shaderSource(shader, source);

        // Compile the shader program
        gl.compileShader(shader);

        // See if it compiled successfully

        if (gl.getShaderParameter(shader, gl.COMPILE_STATUS) == null) {
            Console.log("An error occurred compiling the shaders:" + gl.getShaderInfoLog(shader));
            gl.deleteShader(shader);
            return null;
        }

        return shader;
    }

    // Initialize a shader program, so WebGL knows how to draw our data
    private WebGLProgram initShaderProgram(WebGLRenderingContext gl, String vsSource, String fsSource) {
        WebGLShader vertexShader = loadShader(gl, gl.VERTEX_SHADER, vsSource);
        WebGLShader fragmentShader = loadShader(gl, gl.FRAGMENT_SHADER, fsSource);

        // Create the shader program
        WebGLProgram shaderProgram = gl.createProgram();
        gl.attachShader(shaderProgram, vertexShader);
        gl.attachShader(shaderProgram, fragmentShader);
        gl.linkProgram(shaderProgram);

        // If creating the shader program failed, alert
        if (gl.getProgramParameter(shaderProgram, gl.LINK_STATUS) == null) {
            Console.log("Unable to initialize the shader program:" + gl.getProgramInfoLog(shaderProgram));
            return null;
        }

        return shaderProgram;
    }

    private Buffers initBuffers(WebGLRenderingContext gl) {
        WebGLBuffer positionBuffer = initPositionBuffer(gl);
        WebGLBuffer colorBuffer = initColorBuffer(gl);
        WebGLBuffer indexBuffer = initIndexBuffer(gl);
        return new Buffers(positionBuffer, colorBuffer, indexBuffer);
    }

    private static class Buffers {
        final WebGLBuffer position;
        final WebGLBuffer color;
        final WebGLBuffer indices;

        public Buffers(WebGLBuffer position, WebGLBuffer color, WebGLBuffer indices) {
            this.position = position;
            this.color = color;
            this.indices = indices;
        }
    }

    private WebGLBuffer initPositionBuffer(WebGLRenderingContext gl) {
        // Create a buffer for the square's positions.
        WebGLBuffer positionBuffer = gl.createBuffer();

        // Select the positionBuffer as the one to apply buffer
        // operations to from here out.
        gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer);

        // Now create an array of positions for the square.
        double[] positions = {
                // Front face
                -1.0, -1.0, 1.0, 1.0, -1.0, 1.0, 1.0, 1.0, 1.0, -1.0, 1.0, 1.0,

                // Back face
                -1.0, -1.0, -1.0, -1.0, 1.0, -1.0, 1.0, 1.0, -1.0, 1.0, -1.0, -1.0,

                // Top face
                -1.0, 1.0, -1.0, -1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0,

                // Bottom face
                -1.0, -1.0, -1.0, 1.0, -1.0, -1.0, 1.0, -1.0, 1.0, -1.0, -1.0, 1.0,

                // Right face
                1.0, -1.0, -1.0, 1.0, 1.0, -1.0, 1.0, 1.0, 1.0, 1.0, -1.0, 1.0,

                // Left face
                -1.0, -1.0, -1.0, -1.0, -1.0, 1.0, -1.0, 1.0, 1.0, -1.0, 1.0, -1.0,
        };

        // Now pass the list of positions into WebGL to build the
        // shape. We do this by creating a Float32Array from the
        // JavaScript array, then use it to fill the current buffer.
        gl.bufferData(gl.ARRAY_BUFFER, WebGL.createFloat32Array(positions), gl.STATIC_DRAW);

        return positionBuffer;
    }

    private WebGLBuffer initIndexBuffer(WebGLRenderingContext gl) {
        WebGLBuffer indexBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer);

        // This array defines each face as two triangles, using the
        // indices into the vertex array to specify each triangle's
        // position.

        double[] indices = {
                0,
                1,
                2,
                0,
                2,
                3, // front
                4,
                5,
                6,
                4,
                6,
                7, // back
                8,
                9,
                10,
                8,
                10,
                11, // top
                12,
                13,
                14,
                12,
                14,
                15, // bottom
                16,
                17,
                18,
                16,
                18,
                19, // right
                20,
                21,
                22,
                20,
                22,
                23 // left
        };

        // Now send the element array to GL

        gl.bufferData(
                gl.ELEMENT_ARRAY_BUFFER,
                WebGL.createUint16Array(indices),
                gl.STATIC_DRAW
                );

        return indexBuffer;
    }

    // Tell WebGL how to pull out the positions from the position
    // buffer into the vertexPosition attribute.
    private void setPositionAttribute(WebGLRenderingContext gl, WebGLBuffer buffers, int vertexPosition) {
        int numComponents = 3; // pull out 2 values per iteration
        int type = gl.FLOAT; // the data in the buffer is 32bit floats
        boolean normalize = false; // don't normalize
        int stride = 0; // how many bytes to get from one set of values to the next
        // 0 = use type and numComponents above
        int offset = 0; // how many bytes inside the buffer to start from
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers/*.position*/);
        gl.vertexAttribPointer(
                vertexPosition,
                numComponents,
                type,
                normalize,
                stride,
                offset
                );
        gl.enableVertexAttribArray(vertexPosition);
    }

    private WebGLBuffer initColorBuffer(WebGLRenderingContext gl) {
        double[] colors = {
                1.0, 1.0, 1.0, 1.0, // Front face: white
                1.0, 1.0, 1.0, 1.0, // Front face: white
                1.0, 1.0, 1.0, 1.0, // Front face: white
                1.0, 1.0, 1.0, 1.0, // Front face: white
                1.0, 0.0, 0.0, 1.0, // Back face: red
                1.0, 0.0, 0.0, 1.0, // Back face: red
                1.0, 0.0, 0.0, 1.0, // Back face: red
                1.0, 0.0, 0.0, 1.0, // Back face: red
                0.0, 1.0, 0.0, 1.0, // Top face: green
                0.0, 1.0, 0.0, 1.0, // Top face: green
                0.0, 1.0, 0.0, 1.0, // Top face: green
                0.0, 1.0, 0.0, 1.0, // Top face: green
                0.0, 0.0, 1.0, 1.0, // Bottom face: blue
                0.0, 0.0, 1.0, 1.0, // Bottom face: blue
                0.0, 0.0, 1.0, 1.0, // Bottom face: blue
                0.0, 0.0, 1.0, 1.0, // Bottom face: blue
                1.0, 1.0, 0.0, 1.0, // Right face: yellow
                1.0, 1.0, 0.0, 1.0, // Right face: yellow
                1.0, 1.0, 0.0, 1.0, // Right face: yellow
                1.0, 1.0, 0.0, 1.0, // Right face: yellow
                1.0, 0.0, 1.0, 1.0, // Left face: purple
                1.0, 0.0, 1.0, 1.0, // Left face: purple
                1.0, 0.0, 1.0, 1.0, // Left face: purple
                1.0, 0.0, 1.0, 1.0 // Left face: purple
        };

        WebGLBuffer colorBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, colorBuffer);
        gl.bufferData(gl.ARRAY_BUFFER, WebGL.createFloat32Array(colors), gl.STATIC_DRAW);

        return colorBuffer;
    }

    // Tell WebGL how to pull out the colors from the color buffer
    // into the vertexColor attribute.
    private void setColorAttribute(WebGLRenderingContext gl, Buffers buffers, ProgramInfo programInfo) {
        int numComponents = 4;
        int type = gl.FLOAT;
        boolean normalize = false;
        int stride = 0;
        int offset = 0;
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers.color);
        gl.vertexAttribPointer(
                programInfo.vertexColor,
                numComponents,
                type,
                normalize,
                stride,
                offset
                );
        gl.enableVertexAttribArray(programInfo.vertexColor);
    }
    private void drawScene(WebGLRenderingContext gl, ProgramInfo programInfo, Buffers buffers, double cubeRotation) {
        gl.clearColor(0.0, 0.0, 0.0, 1.0); // Clear to black, fully opaque
        gl.clearDepth(1.0); // Clear everything
        gl.enable(gl.DEPTH_TEST); // Enable depth testing
        gl.depthFunc(gl.LEQUAL); // Near things obscure far things

        // Clear the canvas before we start drawing on it.
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        // Create a perspective matrix, a special matrix that is
        // used to simulate the distortion of perspective in a camera.
        // Our field of view is 45 degrees, with a width/height
        // ratio that matches the display size of the canvas
        // and we only want to see objects between 0.1 units
        // and 100 units away from the camera.

        double fieldOfView = (45 * Math.PI) / 180; // in radians
        double aspect = /*gl.canvas.clientWidth*/ 640d / /*gl.canvas.clientHeight*/ 480;
        double zNear = 0.1;
        double zFar = 100.0;

        Matrix4d projectionMatrix4 = new Matrix4d();

        // note: glmatrix.js always has the first argument
        // as the destination to receive the result.
        projectionMatrix4.perspective(fieldOfView, aspect, zNear, zFar);

        // Set the drawing position to the "identity" point, which is
        // the center of the scene.
        Matrix4d modelViewMatrix4 = new Matrix4d();

        // Now move the drawing position a bit to where we want to
        // start drawing the square.
        modelViewMatrix4.translate(0, 0, -6); // amount to translate
        modelViewMatrix4.rotate(
                cubeRotation, 0, 0, 1) // // axis to rotate around (Z)
                .rotate(cubeRotation * 0.7, 0, 1, 0) // axis to rotate around (Y)
                .rotate(cubeRotation * 0.3, 1, 0, 0); // // axis to rotate around (X)

        // Tell WebGL how to pull out the positions from the position
        // buffer into the vertexPosition attribute.
        setPositionAttribute(gl, buffers.position, programInfo.vertexPosition);
        setColorAttribute(gl, buffers, programInfo);

        // Tell WebGL which indices to use to index the vertices
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, buffers.indices);

        // Tell WebGL to use our program when drawing
        gl.useProgram(programInfo.program);

        // Set the shader uniforms
        gl.uniformMatrix4fv(
                programInfo.projectionMatrix,
                false,
                projectionMatrix4.get(new double[16])
                );
        gl.uniformMatrix4fv(
                programInfo.modelViewMatrix,
                false,
                modelViewMatrix4.get(new double[16])
                );

        int vertexCount = 36;
        int type = gl.UNSIGNED_SHORT;
        int offset = 0;
        gl.drawElements(gl.TRIANGLES, vertexCount, type, offset);
    }
}