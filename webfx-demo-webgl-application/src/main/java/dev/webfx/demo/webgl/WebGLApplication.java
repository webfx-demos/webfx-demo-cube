package dev.webfx.demo.webgl;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.scene.DeviceSceneUtil;
import dev.webfx.kit.webgl.*;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.uischeduler.UiScheduler;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.joml.Matrix4d;

public class WebGLApplication extends Application {

    private Node webGLNode;
    private boolean copyVideo;

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Scene scene = DeviceSceneUtil.newScene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        if (!WebGL.supportsWebGL()) {
            borderPane.setCenter(new Text("WebGL is not supported on this platform, sorry!"));
        } else {
            // Reading back the final scene width and height for the browser
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();
            webGLNode = WebGL.createWebGLNode(sceneWidth, sceneHeight);
            borderPane.setCenter(webGLNode);
            UiScheduler.scheduleInAnimationFrame(this::runWebGL, 1);
        }
        primaryStage.show();
    }

    private void runWebGL() {
        WebGLRenderingContext gl = WebGL.getContext(webGLNode);

        // Vertex shader program
        String vsSource = "attribute vec4 aVertexPosition;\n" +
                          "    attribute vec3 aVertexNormal;\n" +
                          "    attribute vec2 aTextureCoord;\n" +
                          "\n" +
                          "    uniform mat4 uNormalMatrix;\n" +
                          "    uniform mat4 uModelViewMatrix;\n" +
                          "    uniform mat4 uProjectionMatrix;\n" +
                          "\n" +
                          "    varying highp vec2 vTextureCoord;\n" +
                          "    varying highp vec3 vLighting;\n" +
                          "\n" +
                          "    void main(void) {\n" +
                          "      gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;\n" +
                          "      vTextureCoord = aTextureCoord;\n" +
                          "\n" +
                          "      // Apply lighting effect\n" +
                          "\n" +
                          "      highp vec3 ambientLight = vec3(0.3, 0.3, 0.3);\n" +
                          "      highp vec3 directionalLightColor = vec3(1, 1, 1);\n" +
                          "      highp vec3 directionalVector = normalize(vec3(0.85, 0.8, 0.75));\n" +
                          "\n" +
                          "      highp vec4 transformedNormal = uNormalMatrix * vec4(aVertexNormal, 1.0);\n" +
                          "\n" +
                          "      highp float directional = max(dot(transformedNormal.xyz, directionalVector), 0.0);\n" +
                          "      vLighting = ambientLight + (directionalLightColor * directional);\n" +
                          "    }";

        // Fragment shader program
        String fsSource = "varying highp vec2 vTextureCoord;\n" +
                          "    varying highp vec3 vLighting;\n" +
                          "\n" +
                          "    uniform sampler2D uSampler;\n" +
                          "\n" +
                          "    void main(void) {\n" +
                          "      highp vec4 texelColor = texture2D(uSampler, vTextureCoord);\n" +
                          "\n" +
                          "      gl_FragColor = vec4(texelColor.rgb * vLighting, texelColor.a);\n" +
                          "    }";

        // Create the shader program
        WebGLProgram shaderProgram = initShaderProgram(gl, vsSource, fsSource);

        // Collect all the info needed to use the shader program.
        // Look up which attribute our shader program is using
        // for aVertexPosition and look up uniform locations.
        ProgramInfo programInfo = new ProgramInfo(
                shaderProgram,
                gl.getAttribLocation(shaderProgram, "aVertexPosition"),
                gl.getAttribLocation(shaderProgram, "aVertexNormal"),
                0, //gl.getAttribLocation(shaderProgram, "aVertexColor"),
                gl.getAttribLocation(shaderProgram, "aTextureCoord"),
                gl.getUniformLocation(shaderProgram, "uProjectionMatrix"),
                gl.getUniformLocation(shaderProgram, "uModelViewMatrix"),
                gl.getUniformLocation(shaderProgram, "uNormalMatrix"),
                gl.getUniformLocation(shaderProgram, "uSampler")
        );

        // Here's where we call the routine that builds all the
        // objects we'll be drawing.
        Buffers buffers = initBuffers(gl);

        // Load texture
        WebGLTexture texture = loadTexture(gl, Resource.toUrl("WebFX-512x512.png", getClass()));

        // Excellent: FX2048.mp4, SpaceFX.mp4, ModernGauge.mp4
        // Ok: Mandelbrot.mp4, DemoFX.mp4
        // Bad (aspect ratio): EnzoClocks.mp4, RayTracer.mp4, TallyCounter.mp4
        MediaView video = setupVideo(Resource.toUrl("FX2048.mp4", getClass()));

        // Flip image pixels into the bottom-to-top order that WebGL expects.
        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);

        // Draw the scene
        new AnimationTimer() {
            double cubeRotation = 0.0;
            long then, deltaTime;

            @Override
            public void handle(long now) {
                deltaTime = now - then;
                then = now;
                if (copyVideo) {
                    updateVideoTexture(gl, texture, video);
                }
                drawScene(gl, programInfo, buffers, texture, cubeRotation);
                cubeRotation += deltaTime * 1d / 1_000_000_000; // convert to seconds
            }
        }.start();
    }

    private static class ProgramInfo {
        final WebGLProgram program;
        final int vertexPosition;
        final int vertexNormal;
        final int vertexColor;
        final int textureCoord;
        final WebGLUniformLocation projectionMatrix;
        final WebGLUniformLocation modelViewMatrix;
        final WebGLUniformLocation normalMatrix;
        final WebGLUniformLocation uSampler;

        public ProgramInfo(WebGLProgram program, int vertexPosition, int vertexNormal, int vertexColor, int textureCoord, WebGLUniformLocation projectionMatrix, WebGLUniformLocation modelViewMatrix, WebGLUniformLocation normalMatrix, WebGLUniformLocation uSampler) {
            this.program = program;
            this.vertexPosition = vertexPosition;
            this.vertexNormal = vertexNormal;
            this.vertexColor = vertexColor;
            this.textureCoord = textureCoord;
            this.projectionMatrix = projectionMatrix;
            this.modelViewMatrix = modelViewMatrix;
            this.normalMatrix = normalMatrix;
            this.uSampler = uSampler;
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
        //WebGLBuffer colorBuffer = initColorBuffer(gl);
        WebGLBuffer textureCoordBuffer = initTextureBuffer(gl);
        WebGLBuffer indexBuffer = initIndexBuffer(gl);
        WebGLBuffer normalBuffer = initNormalBuffer(gl);
        return new Buffers(positionBuffer, normalBuffer, null, textureCoordBuffer, indexBuffer);
    }

    private static class Buffers {
        final WebGLBuffer position;
        final WebGLBuffer normal;
        final WebGLBuffer color;
        final WebGLBuffer textureCoord;
        final WebGLBuffer indices;

        public Buffers(WebGLBuffer position, WebGLBuffer normal, WebGLBuffer color, WebGLBuffer textureCoord, WebGLBuffer indices) {
            this.position = position;
            this.normal = normal;
            this.color = color;
            this.textureCoord = textureCoord;
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

    private WebGLBuffer initNormalBuffer(WebGLRenderingContext gl) {
        WebGLBuffer normalBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, normalBuffer);

        double[] vertexNormals = {
                // Front
                0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0,

                // Back
                0.0, 0.0, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, -1.0,

                // Top
                0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0,

                // Bottom
                0.0, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, -1.0, 0.0,

                // Right
                1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0,

                // Left
                -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0,
        };

        gl.bufferData(
                gl.ARRAY_BUFFER,
                WebGL.createFloat32Array(vertexNormals),
                gl.STATIC_DRAW
                );

        return normalBuffer;
    }

    private WebGLBuffer initTextureBuffer(WebGLRenderingContext gl) {
        WebGLBuffer textureCoordBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, textureCoordBuffer);

        double[] textureCoordinates = {
                // Front
                0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0,
                // Back
                0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0,
                // Top
                0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0,
                // Bottom
                0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0,
                // Right
                0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0,
                // Left
                0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0
        };

        gl.bufferData(
                gl.ARRAY_BUFFER,
                WebGL.createFloat32Array(textureCoordinates),
                gl.STATIC_DRAW
                );

        return textureCoordBuffer;
    }

    // Tell WebGL how to pull out the positions from the position
    // buffer into the vertexPosition attribute.
    private void setPositionAttribute(WebGLRenderingContext gl, Buffers buffers, ProgramInfo programInfo) {
        int numComponents = 3; // pull out 2 values per iteration
        int type = gl.FLOAT; // the data in the buffer is 32bit floats
        boolean normalize = false; // don't normalize
        int stride = 0; // how many bytes to get from one set of values to the next
        // 0 = use type and numComponents above
        int offset = 0; // how many bytes inside the buffer to start from
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers.position);
        gl.vertexAttribPointer(
                programInfo.vertexPosition,
                numComponents,
                type,
                normalize,
                stride,
                offset
                );
        gl.enableVertexAttribArray(programInfo.vertexPosition);
    }

    //
    // Initialize a texture and load an image.
    // When the image finished loading copy it into the texture.
    //
    private WebGLTexture loadTexture(WebGLRenderingContext gl, String url) {
        WebGLTexture texture = initTexture(gl);

        Image image = new Image(url, true);
        image.progressProperty().addListener(observable -> {
            if (image.getProgress() >= 1)
                updateImageTexture(gl, texture, image);
        });

        return texture;
    }

    private boolean isPowerOf2(int value) {
        return (value & (value - 1)) == 0;
    }

    private void updateImageTexture(WebGLRenderingContext gl, WebGLTexture texture, Image image) {
        // Because images have to be downloaded over the internet
        // they might take a moment until they are ready.
        // Until then put a single pixel in the texture so we can
        // use it immediately. When the image has finished downloading
        // we'll update the texture with the contents of the image.
        int level = 0;
        int internalFormat = gl.RGBA;
        int srcFormat = gl.RGBA;
        int srcType = gl.UNSIGNED_BYTE;

        gl.bindTexture(gl.TEXTURE_2D, texture);
        gl.texImage2D(
                gl.TEXTURE_2D,
                level,
                internalFormat,
                srcFormat,
                srcType,
                image
        );

        // WebGL1 has different requirements for power of 2 images
        // vs. non power of 2 images so check if the image is a
        // power of 2 in both dimensions.
        if (isPowerOf2((int) image.getWidth()) && isPowerOf2((int) image.getHeight())) {
            // Yes, it's a power of 2. Generate mips.
            gl.generateMipmap(gl.TEXTURE_2D);
        } else {
            // No, it's not a power of 2. Turn off mips and set
            // wrapping to clamp to edge
            gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
            gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
            gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
        }
    }

    private WebGLTexture initTexture(WebGLRenderingContext gl) {
        WebGLTexture texture = gl.createTexture();
        gl.bindTexture(gl.TEXTURE_2D, texture);

        // Because video has to be download over the internet
        // they might take a moment until it's ready so
        // put a single pixel in the texture so we can
        // use it immediately.
        int level = 0;
        int internalFormat = gl.RGBA;
        int width = 1;
        int height = 1;
        int border = 0;
        int srcFormat = gl.RGBA;
        int srcType = gl.UNSIGNED_BYTE;
        Color color = Color.PURPLE;
        ArrayBuffer pixel = WebGL.createUint8Array(color.getRed() * 255, color.getGreen() * 255, color.getBlue() * 255, 255); // opaque purple
        gl.texImage2D(
                gl.TEXTURE_2D,
                level,
                internalFormat,
                width,
                height,
                border,
                srcFormat,
                srcType,
                pixel
                );

        // Turn off mips and set wrapping to clamp to edge so it
        // will work regardless of the dimensions of the video.
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);

        return texture;
    }

    private void updateVideoTexture(WebGLRenderingContext gl, WebGLTexture texture, MediaView video) {
        int level = 0;
        int internalFormat = gl.RGBA;
        int srcFormat = gl.RGBA;
        int srcType = gl.UNSIGNED_BYTE;
        gl.bindTexture(gl.TEXTURE_2D, texture);
        gl.texImage2D(
                gl.TEXTURE_2D,
                level,
                internalFormat,
                srcFormat,
                srcType,
                video
                );
    }

    private MediaView setupVideo(String url) {
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(url));

        boolean[] playing = { false };
        boolean[] timeupdate = { false };

        mediaPlayer.setMute(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // Waiting for these 2 events ensures
        // there is data in the video
        Runnable checkReady = () -> {
            if (playing[0] && timeupdate[0]) {
                copyVideo = true;
            }
        };
        mediaPlayer.setOnPlaying(() -> {
            playing[0] = true;
            checkReady.run();
        });

        FXProperties.runOnPropertiesChange(() -> {
            timeupdate[0] = true;
            checkReady.run();
        }, mediaPlayer.currentTimeProperty());

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaPlayer.play();
        return mediaView;
    }

    // tell webgl how to pull out the texture coordinates from buffer
    private void setTextureAttribute(WebGLRenderingContext gl, Buffers buffers, ProgramInfo programInfo) {
        int num = 2; // every coordinate composed of 2 values
        int type = gl.FLOAT; // the data in the buffer is 32-bit float
        boolean normalize = false; // don't normalize
        int stride = 0; // how many bytes to get from one set to the next
        int offset = 0; // how many bytes inside the buffer to start from
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers.textureCoord);
        gl.vertexAttribPointer(
                programInfo.textureCoord,
                num,
                type,
                normalize,
                stride,
                offset
                );
        gl.enableVertexAttribArray(programInfo.textureCoord);
    }

    // Tell WebGL how to pull out the normals from
    // the normal buffer into the vertexNormal attribute.
    private void setNormalAttribute(WebGLRenderingContext gl, Buffers buffers, ProgramInfo programInfo) {
        int numComponents = 3;
        int type = gl.FLOAT;
        boolean normalize = false;
        int stride = 0;
        int offset = 0;
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers.normal);
        gl.vertexAttribPointer(
                programInfo.vertexNormal,
                numComponents,
                type,
                normalize,
                stride,
                offset
                );
        gl.enableVertexAttribArray(programInfo.vertexNormal);
    }

    private void drawScene(WebGLRenderingContext gl, ProgramInfo programInfo, Buffers buffers, WebGLTexture texture, double cubeRotation) {
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
        double aspect = WebGL.getWebGLNodeWidth(webGLNode) / WebGL.getWebGLNodeHeight(webGLNode);
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

        Matrix4d normalMatrix4 = new Matrix4d();
        modelViewMatrix4.invert(normalMatrix4).transpose();

        // Tell WebGL how to pull out the positions from the position
        // buffer into the vertexPosition attribute.
        setPositionAttribute(gl, buffers, programInfo);
        //setColorAttribute(gl, buffers, programInfo);
        setTextureAttribute(gl, buffers, programInfo);
        setNormalAttribute(gl, buffers, programInfo);

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
        gl.uniformMatrix4fv(
                programInfo.normalMatrix,
                false,
                normalMatrix4.get(new double[16])
                );

        // Tell WebGL we want to affect texture unit 0
        gl.activeTexture(gl.TEXTURE0);

        // Bind the texture to texture unit 0
        gl.bindTexture(gl.TEXTURE_2D, texture);

        // Tell the shader we bound the texture to texture unit 0
        gl.uniform1i(programInfo.uSampler, 0);

        int vertexCount = 36;
        int type = gl.UNSIGNED_SHORT;
        int offset = 0;
        gl.drawElements(gl.TRIANGLES, vertexCount, type, offset);
    }
}