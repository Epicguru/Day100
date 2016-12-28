package co.uk.epicguru.helpers;

public abstract class AssetDataTag {
	
	public static AssetDataTag MUSIC = new AssetDataTag(){
		public boolean isDataTag(String path) {
			if(path.contains("[MUSIC]"))
				return true;
			return false;
		}		
	};
	
	public abstract boolean isDataTag(String path);
	
}
