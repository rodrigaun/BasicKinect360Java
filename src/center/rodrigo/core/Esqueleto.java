package center.rodrigo.core;

import com.primesense.nite.JointType;
import com.primesense.nite.Point2D;
import com.primesense.nite.SkeletonJoint;
import com.primesense.nite.SkeletonState;
import com.primesense.nite.UserData;
import com.primesense.nite.UserTracker;
import com.primesense.nite.UserTrackerFrameRef;

public class Esqueleto implements UserTracker.NewFrameListener {

	private UserTracker userTracker;
	private UserTrackerFrameRef lastTrackerFrame;

    public Esqueleto(UserTracker tracker) {
		this.userTracker = tracker;
		this.userTracker.addNewFrameListener(this);
	}

	private void executa() {
		
		/*percorre todos os usuarios identificados*/
		for (UserData user : lastTrackerFrame.getUsers()) {
			
			/*verifica��o se o esqueleto do usuario foi mapeado*/
			if (user.getSkeleton().getState() == SkeletonState.TRACKED) {

				/*pego a juncao da mao direita do usuario*/
				SkeletonJoint sjMaoDireita = user.getSkeleton().getJoint(JointType.RIGHT_HAND);
				
				/*verifico se os valores estao zerados*/
				if (sjMaoDireita.getPositionConfidence() == 0.0)
					return;
				
				/*pego os pontos da juncao*/
				Point2D<Float> pontoMaoDireita = userTracker.convertJointCoordinatesToDepth(sjMaoDireita.getPosition());
				
				/*mostro "x" e "y" da Mao Direita
				 * sjMaoDireita.getPosition().getZ() --> O valor � representado em mil�metros*/
				System.out.println("X:" + pontoMaoDireita.getX() + 
									"  Y:" + pontoMaoDireita.getY() + 
									"  Z:" + sjMaoDireita.getPosition().getZ());
			}
		}
	}

	@Override
	public void onNewFrame(UserTracker arg0) {
		if (lastTrackerFrame != null) {
			lastTrackerFrame.release();
			lastTrackerFrame = null;
		}

		lastTrackerFrame = userTracker.readFrame();

		/*verifica se h� um novo esqueleto*/
		for (UserData user : lastTrackerFrame.getUsers()) {
			if (user.isNew()) {
				/*se tem novo usuario, come�a a mapear*/
				userTracker.startSkeletonTracking(user.getId());
			}
		}
		executa();
	}

}