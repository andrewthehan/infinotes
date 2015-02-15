
package infinotes.factory;

import infinotes.music.Duration;
import infinotes.music.KeySignature;
import infinotes.music.TimeSignature;
import infinotes.music.Voice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RhythmFactory{
	private static final Random R = new Random();
	private final KeySignature keySignature;
	private final TimeSignature timeSignature;
	private final Voice voice;
	
	private RhythmFactory(KeySignature keySignature, TimeSignature timeSignature, Voice voice){
		this.keySignature = keySignature;
		this.timeSignature = timeSignature;
		this.voice = voice;
	}
	
	public static RhythmFactory make(KeySignature keySignature, TimeSignature timeSignature, Voice voice){
		return new RhythmFactory(keySignature, timeSignature, voice);
	}
	
	public Duration[] makeRhythm(Duration duration){
		List<Duration> rhythm = new ArrayList<Duration>();
		switch(voice.getStyle()){
			case MELODY:
				for(double soFar = 0; soFar < duration.getValue(); ){
					Duration durationToAdd;
					do{
						switch(R.nextInt(3)){
							case 0: 
								durationToAdd = Duration.HALF;
								break;
							case 1:
								durationToAdd = Duration.QUARTER;
								break;
							case 2:
								durationToAdd = Duration.EIGHTH;
								break;
							default:
								durationToAdd = null;
						}
					}while(soFar + durationToAdd.getValue() > duration.getValue());
					rhythm.add(durationToAdd);
					soFar += durationToAdd.getValue();
				}
				break;
			case HARMONY:
				// assuming numberOfMeasures is always 1 and timeSignature = 4/4
				rhythm.add(Duration.QUARTER);
				rhythm.add(Duration.QUARTER);
				rhythm.add(Duration.QUARTER);
				rhythm.add(Duration.QUARTER);
				break;
		}
		return rhythm.toArray(new Duration[rhythm.size()]);
	}
}