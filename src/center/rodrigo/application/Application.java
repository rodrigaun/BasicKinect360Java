package center.rodrigo.application;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.SensorType;
import org.openni.VideoMode;
import org.openni.VideoStream;

import com.primesense.nite.NiTE;
import com.primesense.nite.UserTracker;

import center.rodrigo.core.Camera;
import center.rodrigo.core.Depth;
import center.rodrigo.core.Esqueleto;

public class Application {

    private JFrame frame;
    private Device device;
    private Camera camera;
    private Depth depth;
    private Esqueleto esqueleto;
    private SensorType tipo;
    private UserTracker tracker;
    private VideoStream meuVideoStream;
    private List<DeviceInfo> listaDevices;
    private List<VideoMode> listaVideoSuportado;
    private boolean emExecucao = true;

    public Application(String app) {

        if (!verificarKinect())
            System.exit(0);

        frame = new JFrame("Basic Kinect 360");

        switch (app) {
        case "Esqueleto":
            NiTE.initialize();
            tracker = UserTracker.create();
            esqueleto = new Esqueleto(tracker);
            break;

        case "Camera":
            tipo = SensorType.COLOR;
            carregarKinect(1);
            camera = new Camera(meuVideoStream);
            frame.add(camera);
            break;

        case "Depth":
            tipo = SensorType.DEPTH;
            carregarKinect(0);
            depth = new Depth(meuVideoStream);
            frame.add(depth);
            break;
        }

        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void run() {
        while (emExecucao) {
            /*
             * precisamos deste loop infinito. Caso contrario o metodo main
             * acaba e o programa encerra
             */
        }
    }

    public boolean verificarKinect() {

        OpenNI.initialize();
        listaDevices = OpenNI.enumerateDevices();

        if (listaDevices.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kinect Não Encontrado !", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        System.out.println(listaDevices.size() + " Kinect(s) Encontrado(s)");
        return true;
    }

    public void carregarKinect(int i) {
        try {
            device = Device.open(listaDevices.get(0).getUri());
            meuVideoStream = VideoStream.create(device, tipo);
            listaVideoSuportado = meuVideoStream.getSensorInfo().getSupportedVideoModes();
            meuVideoStream.setVideoMode(listaVideoSuportado.get(i));
            meuVideoStream.start();
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
