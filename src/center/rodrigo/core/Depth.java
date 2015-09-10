package center.rodrigo.core;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.openni.VideoFrameRef;
import org.openni.VideoStream;

public class Depth extends Component implements VideoStream.NewFrameListener {

	private int width = 640;
	private int height = 480;
	private float mHistogram[];
	private int[] mImagePixels;
	private VideoFrameRef mLastFrame;
	private VideoStream meuVideoStream;
	private BufferedImage mBufferedImage;
	
    public Depth(VideoStream videoStream) {
		this.meuVideoStream = videoStream;
		this.meuVideoStream.addNewFrameListener(this);
	}
	
	
	@Override
    public synchronized void paint(Graphics g) {
        if (mLastFrame == null)
            return;

        if (mBufferedImage == null)
            mBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        mBufferedImage.setRGB(0, 0, width, height, mImagePixels, 0, width);
        g.drawImage(mBufferedImage, 0, 0, null);
    }

	
	@Override
	public void onFrameReady(VideoStream arg0) {

		if (mLastFrame != null) {
			mLastFrame.release();
		}

		mLastFrame = meuVideoStream.readFrame();
		
		ByteBuffer frameData = mLastFrame.getData().order(ByteOrder.LITTLE_ENDIAN);

		if (mImagePixels == null) 
			mImagePixels = new int[mLastFrame.getWidth() * mLastFrame.getHeight()];
		
		calcHist(frameData);
		frameData.rewind();
		int pos = 0;
		while (frameData.remaining() > 0) {
			int depth = (int) frameData.getShort() & 0xFFFF;
			short pixel = (short) mHistogram[depth];
			mImagePixels[pos] = 0xFF000000 | (pixel << 16) | (pixel << 8);
			pos++;
		}
		repaint();
	}

	private void calcHist(ByteBuffer depthBuffer) {

		if (mHistogram == null || mHistogram.length < meuVideoStream.getMaxPixelValue())
			mHistogram = new float[meuVideoStream.getMaxPixelValue()];

		for (int i = 0; i < mHistogram.length; ++i)
			mHistogram[i] = 0;

		int points = 0;
		while (depthBuffer.remaining() > 0) {
			int depth = depthBuffer.getShort() & 0xFFFF;
			if (depth != 0) {
				mHistogram[depth]++;
				points++;
			}
		}

		for (int i = 1; i < mHistogram.length; i++)
			mHistogram[i] += mHistogram[i - 1];

		if (points > 0) {
			for (int i = 1; i < mHistogram.length; i++)
				mHistogram[i] = (int) (256 * (1.0f - (mHistogram[i] / (float) points)));
		}
	}
}
