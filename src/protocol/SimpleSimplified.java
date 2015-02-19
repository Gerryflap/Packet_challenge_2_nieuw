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
    public static final int SKIP_COUNT = 14;

    int[] skipList = new int[4];
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

        if(controlInformation/4 == 1) {
            skipList[controlInformation%4] = SKIP_COUNT;
            //System.out.println("Skip next");
        }
		// Randomly transmit with 60% probability nee
		if (controlInformation%4 == number) {
            //System.out.println("Sent Data");
            tried = true;
            int n = 1;
            while(skipList[(number + n + 1)%4] != 0 && n != 4){
                skipList[(number + n)%4] -= 1;
                n += 1;
            }
            skipList[(number+n)%4] -= 1;
            //System.out.println("Skipped next");
            return new TransmissionInfo(TransmissionType.Data, (controlInformation+n)%4);

		} else {
            tried = false;
			return new TransmissionInfo(TransmissionType.Silent, (controlInformation + 1)%4);
		}

	}

}
