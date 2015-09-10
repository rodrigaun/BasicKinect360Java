package center.rodrigo.core;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.openni.VideoFrameRef;
import org.openni.VideoStream;

public class Camera extends Component implements VideoStream.NewFrameListener {

    private VideoStream videoStream;
    private VideoFrameRef lastVideoFrame;
    private BufferedImage bufferedImage;
    private int[] imagePixels;

    public Camera(VideoStream videoStream) {
        this.videoStream = videoStream;
        this.videoStream.addNewFrameListener(this);
    }

    public synchronized void paint(Graphics g) {

        if (bufferedImage == null)
            bufferedImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);

        bufferedImage.setRGB(0, 0, 640, 480, imagePixels, 0, 640);
        g.drawImage(bufferedImage, 0, 0, null);

        repaint();
    }

    @Override
    public void onFrameReady(VideoStream arg0) {

        lastVideoFrame = videoStream.readFrame();
        ByteBuffer frameData = lastVideoFrame.getData().order(ByteOrder.LITTLE_ENDIAN);

        if (imagePixels == null || imagePixels.length < lastVideoFrame.getWidth() * lastVideoFrame.getHeight())
            imagePixels = new int[640 * 480];

        int pos = 0;
        while (frameData.remaining() > 0) {
            int red = (int) frameData.get() & 0xFF;
            int green = (int) frameData.get() & 0xFF;
            int blue = (int) frameData.get() & 0xFF;
            imagePixels[pos] = 0xFF000000 | (red << 16) | (green << 8) | blue;
            pos++;
        }
    }

}
