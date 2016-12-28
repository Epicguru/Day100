package co.uk.epicguru.helpers;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import co.uk.epicguru.main.Log;

public class AssetHelper {
	
	public static ArrayList<String> getAllFiles(FileHandle source, String ending, AssetDataTag... tags){
		ArrayList<String> array = new ArrayList<String>();
		ArrayList<FileHandle> TODO = new ArrayList<FileHandle>();

		final String TAG = "File Finder";
		final String SPLITTER = "\n";
		final String DOT = ".";
		final String SEP = "/";

		Log.error(TAG, "Scanning " + source.path() + " for all '" + ending + "' files...");		

		String[] split = source.readString().split(SPLITTER);
		for(String string : split){
			if(string.contains(DOT)){
				// Is file, or nonsense.
				FileHandle file = Gdx.files.internal(source.path() + SEP + string);
				if(file.extension().equals(ending)){
					boolean valid = true;
					for(AssetDataTag tag : tags){
						if(!tag.isDataTag(file.path())){
							valid = false;
							break;
						}
					}
					if(valid)
						array.add(file.path());					
				}
			}else{
				// Is directory, or nonsense.
				FileHandle file = Gdx.files.internal(source.path() + SEP + string);
				Log.info(TAG, "Scanning - " + file.path());
				TODO.add(file);
			}
		}


		while(!TODO.isEmpty()){
			split = TODO.get(0).readString().split(SPLITTER);
			for(String string : split){
				if(string.contains(DOT)){
					// Is file, or nonsense.
					FileHandle file = Gdx.files.internal(TODO.get(0).path() + SEP + string);
					if(file.extension().equals(ending)){
						boolean valid = true;
						for(AssetDataTag tag : tags){
							if(!tag.isDataTag(file.path())){
								valid = false;
								break;
							}
						}
						if(valid)
							array.add(file.path());
					}
				}else{
					// Is directory, or nonsense.
					FileHandle file = Gdx.files.internal(TODO.get(0).path() + SEP + string);
					Log.info(TAG, "Scanning - " + file.path());
					TODO.add(file);
				}
			}
			TODO.remove(0);
		}
		
		Log.error(TAG, "Done scanning for files! Found " + array.size() + " " + ending + " files.");

		return array;
	}
}
