package co.uk.epicguru.helpers;

public class AlphabeticalHelper {

	public static String[] sort(String[] unsorted){
		
		while(!sorted(unsorted)){
			for(int i = 0; i < unsorted.length; i++){
				if(i < unsorted.length - 2){
					if(unsorted[i].compareTo(unsorted[i + 1]) > 0){
						swap(unsorted, i, i + 1);
					}
				}
			}
		}
		
		return unsorted;
	}
	
	private static void swap(String[] array, int a, int b){
		String aOld = array[a];
		array[a] = array[b];
		array[b] = aOld;
	}
	
	private static boolean sorted(String[] array){
		
		if(array.length < 2)
			return true;
		
		for(int i = 0; i < array.length; i++){
			if(i < array.length - 2){
				if(array[i].compareTo(array[i + 1]) > 0){
					return false;
				}
			}
		}
		return true;
	}
	
}
