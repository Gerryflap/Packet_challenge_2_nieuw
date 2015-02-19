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
    boolean tried = true;
    private int number = -1;

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
                return new TransmissionInfo(TransmissionType.NoData, (number + 1) % 4);
            }
        }

        if(tried && number == -1){
            tried = false;
            if(previousMediumState == MediumState.Succes){
                number = (controlInformation-1 +4)%4;
                System.out.println(number);
            }
        }

		// No data to send, just be quiet
		if (localQueueLength == 0) {
            if( number == controlInformation || (number == -1 &&  new Random().nextInt(100) < 28)) {
                tried = true;
                return new TransmissionInfo(TransmissionType.NoData, (controlInformation + 1) % 4);
            } else {
                return new TransmissionInfo(TransmissionType.Silent, (controlInformation + 1) % 4);
            }
		}

		// Randomly transmit with 60% probability
		if ((controlInformation == number && number != -1) || (number == -1 &&  new Random().nextInt(100) < 25)) {
            //System.out.println("Sent Data");
            tried = true;
			return new TransmissionInfo(TransmissionType.Data, (controlInformation + 1)%4);
		} else {
			return new TransmissionInfo(TransmissionType.Silent, (controlInformation + 1)%4);
		}

	}

}
