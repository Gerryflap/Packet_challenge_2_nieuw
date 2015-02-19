package protocol;

import javax.naming.ldap.Control;
import javax.swing.plaf.metal.MetalIconFactory;
import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SimpleSimplified implements IMACProtocol {
    public static final int SKIP_COUNT = 10;

    boolean tried = true;
    private int number = -1;
    private int skipNext = 0;
    public SimpleSimplified(){
        number = new Random().nextInt(4);
        System.out.println(number);
    }

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
        //System.out.println(controlInformation);
        if(tried && previousMediumState == MediumState.Collision){
            if(new Random().nextInt(100) < 50){
                this.number = new Random().nextInt(4);
                System.out.println(number);
            }
        }

        if (previousMediumState == MediumState.Idle){
            if(new Random().nextInt(100) < 25){
                return new TransmissionInfo(TransmissionType.NoData, new Random().nextInt(4));
            }
        }


		// No data to send, just be quiet
		if (localQueueLength == 0) {
            if( number == controlInformation%4) {
                tried = true;
                return new TransmissionInfo(TransmissionType.NoData, (controlInformation + 1) % 4 + 4);
            } else {
                tried = false;
                return new TransmissionInfo(TransmissionType.Silent, (controlInformation + 1) % 4);
            }
		}

        if ((number+2)%4 == (controlInformation)%4){
            //System.out.println(controlInformation/4 );
            //System.out.println(controlInformation);

            if(controlInformation/4 == 1) {
                skipNext = SKIP_COUNT;
                //System.out.println("Skip next");
            }
        }
		// Randomly transmit with 60% probability nee
		if (controlInformation%4 == number) {
            //System.out.println("Sent Data");
            tried = true;
            if(skipNext != 0){
                skipNext -= 1;
                //System.out.println("Skipped next");
                return new TransmissionInfo(TransmissionType.Data, (controlInformation+2)%4);

            } else {
                return new TransmissionInfo(TransmissionType.Data, (controlInformation + 1)%4);
            }
		} else {
            tried = false;
			return new TransmissionInfo(TransmissionType.Silent, (controlInformation + 1)%4);
		}

	}

}
