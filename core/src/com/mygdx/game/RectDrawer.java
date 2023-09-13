package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class RectDrawer implements Disposable {
    public static final String VERT_SHADER =
            "attribute vec2 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	vColor = a_color;\n" +
                    "	gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" +
                    "}";

    public static final String FRAG_SHADER =
            "#ifdef GL_ES\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	gl_FragColor = vColor;\n" +
                    "}";
    private static final int POSITION_COMPONENTS = 2;
    private static final int COLOR_COMPONENTS = 1;
    private static final int VERT_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;
    private static final int VERTS_PER_RECT = 4;
    private static final int INDS_PER_RECT = 6;
    private static final int MAX_RECTS = (Short.MAX_VALUE - 1) * 2 / VERTS_PER_RECT + 1;

    private final Mesh mesh;
    private Matrix4 proj;
    private final ShaderProgram shader;
    private final float[] verts;
    private final short[] indices;
    private int vertCompIdx;  // vertex component index
    private short vertIdx;  // vertex index, the number of vertices that have been added this batch
    private int indIdx;

    public RectDrawer(int maxRects) {
        if(maxRects > MAX_RECTS) {
            throw new IllegalArgumentException("maxRects is too high for ShapeDrawer.");
        }
        int maxVerts = maxRects * VERTS_PER_RECT;
        int maxInds = maxRects * INDS_PER_RECT;
        verts = new float[maxVerts * VERT_COMPONENTS];
        indices = new short[maxInds];

        mesh = new Mesh(true, maxVerts, maxInds,
                new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log!=null && log.length()!=0)
            System.out.println("Shader Log: "+log);
    }

    public RectDrawer() {
        this(MAX_RECTS);
    }

    public void drawRect(float x, float y, float width, float height, Color color) {
        // automatically flush batch if it gets too many vertices
        if (vertCompIdx == verts.length) {
            flush();
        }

        addVertex(x, y, color); // offset 4
        addVertex(x, y + height, color); // 3
        addVertex(x + width, y, color); // 2
        addVertex(x + width, y + height, color); // 1

        // update index array
        setCurIndex(4);
        setCurIndex(3);
        setCurIndex(2);

        setCurIndex(1);
        setCurIndex(3);
        setCurIndex(2);
    }

    // sets the current index to the given vertex index offset
    private void setCurIndex(int vertOffset) {
        indices[indIdx++] = (short) (vertIdx - vertOffset);
    }

    private void addVertex(float x, float y, Color color) {
        float c = color.toFloatBits();
        verts[vertCompIdx++] = x;
        verts[vertCompIdx++] = y;
        verts[vertCompIdx++] = c;
        vertIdx++;
    }

    public void flush() {
        //if we've already flushed
        if (vertCompIdx == 0)
            return;

        mesh.setVertices(verts);
        mesh.setIndices(indices);

        //no need for depth...
        Gdx.gl.glDepthMask(false);

        //enable blending, for alpha
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //start the shader before setting any uniforms
        shader.bind();

        //update the projection matrix so our triangles are rendered in 2D
        shader.setUniformMatrix("u_projTrans", proj);

        //render the mesh
        mesh.render(shader, GL20.GL_TRIANGLES, 0, indIdx);

        //re-enable depth to reset states to their default
        Gdx.gl.glDepthMask(true);

        //reset index to zero
        vertCompIdx = 0;
        vertIdx = 0;
        indIdx = 0;
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        this.proj = matrix;
    }


    @Override
    public void dispose() {
        mesh.dispose();
        shader.dispose();
    }
}
