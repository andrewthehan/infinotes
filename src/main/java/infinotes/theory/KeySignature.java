
package infinotes.theory;

import java.util.List;

public final class KeySignature{
	private final Key key;
	private final Mode mode;

	public KeySignature(Key key, Mode mode){
		this.key = key;
		this.mode = mode;
	}

	public final Degree degreeOf(Key key){
		List<Letter> list = Letter.asList(this.key.getLetter());
		int index = list.indexOf(key.getLetter());
		return Degree.fromValue(index + 1);
	}

	public final Key keyOf(Degree degree){
		List<Letter> list = Letter.asList(key.getLetter());
		Key key = new Key(list.get(degree.getValue() - 1));
		return key.apply(this);
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(key);
		builder.append(mode);
		return builder.toString();
	}

	public final Key getKey(){
		return key;
	}

	public final Mode getMode(){
		return mode;
	}
}