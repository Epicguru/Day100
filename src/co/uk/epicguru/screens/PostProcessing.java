package co.uk.epicguru.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;

// Post processing effects
public class PostProcessing {

	private static final String TAG = "PP";
	private static boolean ready = false;
	public static FrameBuffer fbo;

	public static void beginRender(Batch batch){

		// Clear FBO or batch
		if(ScreenManager.getScreen() == Screen.LOADING)
			Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		else
			Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(ready){
			fbo.begin();
		}

		// Clear FBO or batch
		if(ScreenManager.getScreen() == Screen.LOADING)
			Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		else
			Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Start batch
		batch.setProjectionMatrix(Day100.camera.combined);
		batch.begin();
	}

	public static void start(){		
		if(ready)
			return;

		fbo = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		ready = true;
	}

	public static void end(){		
		if(!ready)
			return;

		ready = false;
		fbo.dispose();
		fbo = null;
	}

	public static void resize(int width, int height){
		if(!ready)
			return;
		fbo = new FrameBuffer(Format.RGB888, width, height, false);
		Log.info(TAG, "Resized FBO");
	}

	public static void endRender(Batch batch){	

		// Stop the batch
		batch.end();

		// Unbind FBO if ready and running
		if(ready)
			fbo.end();

		// Render FBO (again if ready)
		render(batch);
	}

	private static void render(Batch batch){

		if(!ready)
			return;

		// Start batch up again
		batch.begin();
		batch.setShader(null);	// SET SHADER HERE
		//batch.getShader().setUniformf("u_amount", 0.003f / Day100.camera.zoom);
		
		batch.setProjectionMatrix(Day100.UIcamera.combined);
		
		batch.setColor(Color.WHITE);
		batch.draw(fbo.getColorBufferTexture(), 0, 0, fbo.getWidth(), fbo.getHeight(), 0, 0, fbo.getWidth(), fbo.getHeight(), false, true);
		
		// End all
		batch.end();
		
		// Render light, uses its own batch
		if(Day100.map != null && Day100.map.rayHandler != null && Day100.map.world != null){
			Day100.map.rayHandler.setCombinedMatrix(Day100.camera);
			Day100.map.rayHandler.updateAndRender();
		}
		
		
		batch.setProjectionMatrix(Day100.camera.combined);
		batch.setShader(null);
		batch.begin();
	}	
}
