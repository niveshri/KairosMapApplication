package io.kairos.maps;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Cube {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ByteBuffer indexBuffer;

    private float vertices[] = {
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 11.0f,
    };

//    private float colors[] = {
//            0.0f, 1.0f, 0.0f,
//            0.0f, 1.0f, 0.0f,
//            1.0f, 0.5f, 0.0f,
//            1.0f, 0.5f, 0.0f,
//            1.0f, 0.0f, 0.0f,
//            1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
//            1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
//            1.0f, 1.0f
//    }
}
