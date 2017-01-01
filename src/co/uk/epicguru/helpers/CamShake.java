package co.uk.epicguru.helpers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CamShake {
	
	public static Vector2 addition = new Vector2();
	private static float radis;
	private static int angle = 0;

	public static void shake(int force)
	{
		radis = force;
	}

	public static void update(float delta)
	{
		angle += 150 + MathUtils.random(0, 60);
		radis *= 0.9f;
		angle *= 0.95f;

		if (radis < 0.05f)
			radis = 0;

		Vector2 offset = new Vector2((float)MathUtils.sin(angle) * radis, (float)MathUtils.cos(angle) * radis);

		addition.x += (int)offset.x;
		addition.y  += (int)offset.y;
		
		addition.x *= 0.9f;
		addition.y *= 0.9f;
	}

}
