/**
 * 
 * @author Charles Manning
 *
 */
public class LTP {
	// Local tangent plane
	private boolean ltpSet = false; // This means the LTP is valid
	private int ltpLatMinuteInt = 0;
	private float ltpLatMinuteFract = 0;
	private int ltpLonMinuteInt = 0;
	private float ltpLonMinuteFract = 0;
	private float ltpEastingPerMinute = 0;
	private float metresPerMinute = 1855.314f;
	private float ltpNorthing = 0.0f;
	private float ltpEasting = 0.0f;
	private int latitudeMinuteInt = 0;
	private float latitudeMinuteFract = 0;
	private int longitudeMinuteInt = 0;
	private float longitudeMinuteFract = 0;
	private int quality = 0;


	public float getNorthingMetres(){
		return ltpNorthing;
	}

	public float getEastingMetres(){
		return ltpEasting;
	}

	public void reset(){
		ltpNorthing = 0.0f;
		ltpEasting = 0.0f;
		ltpSet = false;
	}

	public void manning(){
		// todo need to get the quality so we only use good fixes this.quality = ggaSentence.getQuality();

		if(this.ltpSet == false && quality > 0){
			this.ltpLatMinuteInt = this.latitudeMinuteInt;
			this.ltpLatMinuteFract = this.latitudeMinuteFract;
			this.ltpLonMinuteInt = this.longitudeMinuteInt;
			this.ltpLonMinuteFract = this.longitudeMinuteFract;

			ltpEastingPerMinute = ((float)Math.cos((((float)this.latitudeMinuteInt) + this.latitudeMinuteFract) *
						0.00029089f  /* radians per minute */)) * 
						metresPerMinute; 
			this.ltpSet = true;
		}

		if(this.ltpSet && quality > 0){
			float latMinDiff = (float)(latitudeMinuteInt - ltpLatMinuteInt) + 
						(latitudeMinuteFract - ltpLatMinuteFract);
			float lonMinDiff = (float)(longitudeMinuteInt - ltpLonMinuteInt) + 
						(longitudeMinuteFract - ltpLonMinuteFract);
			ltpNorthing = latMinDiff * metresPerMinute;
			ltpEasting = lonMinDiff * ltpEastingPerMinute;
		}
	}
}
