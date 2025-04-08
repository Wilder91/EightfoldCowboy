package helper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * A camera specifically designed for isometric view projections.
 * This extends OrthographicCamera and provides methods to handle isometric transformations.
 */
public class IsometricCamera extends OrthographicCamera {

    // Standard isometric angle is 30 degrees
    private float angle = 30f;

    // Scaling factor for isometric projection (can be adjusted)
    private float isoScale = 1f;

    /**
     * Creates a new IsometricCamera with the given viewport dimensions
     * @param viewportWidth Width of the viewport
     * @param viewportHeight Height of the viewport
     */
    public IsometricCamera(float viewportWidth, float viewportHeight) {
        super(viewportWidth, viewportHeight);
    }

    /**
     * Sets the isometric angle (standard is 30 degrees)
     * @param angle The angle in degrees
     */
    public void setIsometricAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Sets the scaling factor for the isometric projection
     * @param scale The scaling factor (default is 1.0)
     */
    public void setIsometricScale(float scale) {
        this.isoScale = scale;
    }

    /**
     * Gets the isometric scale factor
     * @return The current isometric scale
     */
    public float getIsometricScale() {
        return isoScale;
    }

    /**
     * Updates the camera with isometric projection
     */
    @Override
    public void update() {
        // Call the standard update first
        super.update();

        // Apply isometric transformation to the combined matrix
        Matrix4 isoTransform = new Matrix4();

        // Preserve the existing projection and translation
        Matrix4 projection = new Matrix4(combined);

        // Apply isometric rotation
        // This rotates around X by the isometric angle
        float angleRad = (float) Math.toRadians(angle);
        isoTransform.setToRotation(Vector3.X, angleRad);
        projection.mul(isoTransform);

        // Then rotate 45 degrees around Y to get the classic isometric look
        isoTransform.setToRotation(Vector3.Y, (float) Math.toRadians(45));
        projection.mul(isoTransform);

        // Apply the isometric scale if needed
        if (isoScale != 1f) {
            projection.scale(isoScale, isoScale, isoScale);
        }

        // Set the final combined matrix
        combined.set(projection);
    }

    /**
     * Converts a screen point to isometric world coordinates
     * @param screenX X coordinate on screen
     * @param screenY Y coordinate on screen
     * @return Vector3 containing the isometric world coordinates (x, y, z)
     */
    public Vector3 screenToIsometricWorld(float screenX, float screenY) {
        Vector3 worldPoint = unproject(new Vector3(screenX, screenY, 0));

        // Apply additional inverse isometric transformation if needed
        // This depends on your specific implementation

        return worldPoint;
    }

    /**
     * Converts isometric world coordinates to screen coordinates
     * @param worldX X coordinate in world space
     * @param worldY Y coordinate in world space
     * @param worldZ Z coordinate in world space
     * @return Vector3 containing the screen coordinates (x, y, 0)
     */
    public Vector3 isometricWorldToScreen(float worldX, float worldY, float worldZ) {
        Vector3 screenPoint = project(new Vector3(worldX, worldY, worldZ));
        return screenPoint;
    }

    /**
     * Helper method to create a preset camera for classic isometric (2:1 ratio) view
     * @param viewportWidth Width of the viewport
     * @param viewportHeight Height of the viewport
     * @return A configured IsometricCamera
     */
    public static IsometricCamera createClassicIsometricCamera(float viewportWidth, float viewportHeight) {
        IsometricCamera camera = new IsometricCamera(viewportWidth, viewportHeight);
        camera.setIsometricAngle(30f); // Classic isometric angle
        camera.setIsometricScale(0.5f); // Adjust scale to match the 2:1 ratio of isometric tiles
        return camera;
    }
}