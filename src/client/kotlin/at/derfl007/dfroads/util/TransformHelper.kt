package at.derfl007.dfroads.util

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform
import org.joml.Vector2f
import org.joml.Vector3f

object TransformHelper {

    /**
     * Moves all UV vertices to the coordinates of its neighbor (clockwise), essentially rotating the UV
     * @param quarterRotations the number of 90deg clockwise turns the UV should rotate by
     */
    fun rotate(quarterRotations: Int): QuadTransform {
        return QuadTransform { quad: MutableQuadView ->
            if (quarterRotations < 0) return@QuadTransform false
            val uvs = Array(4) {
                quad.copyUv(it, Vector2f())
            }
            for (vertex in 0..3) {
                val nextVertex = (vertex + quarterRotations) % 4
                quad.uv(vertex, uvs[nextVertex])
            }
            true
        }
    }

    /**
     * Translate all vertices by the specified amount in the Y axis
     * @param y The amount the vertices should be moved in the Y axis
     */
    fun translateY(y: Float): QuadTransform {
        return QuadTransform { quad: MutableQuadView ->
            val vertices = Array(4) {
                Vector3f(quad.x(it), quad.y(it) + y, quad.z(it))
            }

            for (vertex in 0..3) {
                quad.pos(vertex, vertices[vertex])
            }

            true
        }
    }
}