
package jmtapi;

import jmtapi.theory.*;

import java.util.regex.Pattern;
import java.util.Arrays;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;

public class Main{
	private static final Pattern WHITESPACE = Pattern.compile("\\s{1,}");

	private final Synthesizer synth;
	private final MidiChannel[] channels;

	public static void main(String[] args){
		Main main = new Main();
		main.start();

		Measure measure = Measure
			.builder()
			.setTimeSignature(TimeSignature.COMMON_TIME)
			.add(Note.fromString("C4[.25]"))
			.add(Note.fromString("E4[.25]"))
			.add(Note.fromString("G4[.25]"))
			.add(new Chord(Pitch.fromString("C4"), Chord.Quality.MAJOR, Value.QUARTER))
			.build();
		Arrays.stream(measure.getChords()).forEach(main::play);

		// C Major scale, parsing from a string
		Arrays.stream(Main.parse("C4 D4 E4 F4 G4 A4 B4 C5")).forEach(p -> main.play(new Note(p, Value.fromString(".25"))));

		// c natural minor scale, using key signature
		KeySignature ks = new KeySignature(Key.fromString("C"), Mode.HARMONIC_MINOR);
		Pitch scale = new Pitch(ks.getKey().flat(), 4);
		Letter
			.iterator(ks.getKey().getLetter())
			.forEachRemaining(l -> main.play(new Note(scale.getHigherPitch(new Key(l)).apply(ks), Value.fromString(".25"))));

		// circle of fifths
		Pitch pitch = Pitch.fromString("C2");
		Interval circle = new Interval(Interval.Quality.PERFECT, 5);
		do{
			main.play(new Note(pitch = pitch.step(circle), Value.fromString(".25")));
		}while(!Key.isEnharmonic(pitch.getKey(), Key.fromString("C")));

		// chromatic scale
		pitch = Pitch.fromString("C4");
		main.play(new Note(pitch, Value.fromString(".25")));
		do{
			main.play(new Note(pitch = pitch.halfStepUp(), Value.fromString(".25")));
		}while(!Key.isEnharmonic(pitch.getKey(), Key.fromString("C")));

		// c minor arpeggio using degrees
		Key tonic = ks.keyOf(Degree.TONIC);
		Key mediant = ks.keyOf(Degree.MEDIANT);
		Key dominant = ks.keyOf(Degree.DOMINANT);
		Pitch arpeggio;
		main.play(new Note(arpeggio = new Pitch(tonic, 4), Value.fromString(".25")));
		main.play(new Note(arpeggio = arpeggio.getHigherPitch(mediant), Value.fromString(".25")));
		main.play(new Note(arpeggio = arpeggio.getHigherPitch(dominant), Value.fromString(".25")));
		main.play(new Note(arpeggio = arpeggio.getHigherPitch(tonic), Value.fromString(".25")));

		// play all chords on C4
		Pitch c4 = Pitch.fromString("C4");
		main.play(new Chord(c4, Chord.Quality.MAJOR, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.MINOR, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.DIMINISHED, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.AUGMENTED, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.MAJOR_SEVENTH, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.MINOR_SEVENTH, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.DOMINANT_SEVENTH, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.DIMINISHED_SEVENTH, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.HALF_DIMINISHED_SEVENTH, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.MINOR_MAJOR_SEVENTH, Value.HALF));
		main.play(new Chord(c4, Chord.Quality.AUGMENTED_MAJOR_SEVENTH, Value.HALF));

		main.end();
	}

	public Main(){
		try{
			synth = MidiSystem.getSynthesizer();
			channels = synth.getChannels();
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error");
		}
	}

	public void start(){
		try{
			synth.open();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void end(){
		try{
			synth.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void play(Note note){
		play(new Chord(new Note[]{note}));
	}

	public void play(Chord chord){
		System.err.println(chord);
		int bmp = 120;
		int voice = 0;
		int piano = 0;
		int volume = 127;
		channels[voice].programChange(piano);

		for(Pitch pitch : chord.getPitches()){
			channels[voice].noteOn(pitch.getProgramNumber(), volume);
		}
		try{
			Thread.sleep((int) (120000 / bmp * chord.getValue().getDuration()));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		for(Pitch pitch : chord.getPitches()){
			channels[voice].noteOff(pitch.getProgramNumber());
		}
	}

	public static Pitch[] parse(String pitches){
		return Arrays.stream(WHITESPACE.split(pitches))
			.map(Pitch::fromString)
			.toArray(Pitch[]::new);
	}
}