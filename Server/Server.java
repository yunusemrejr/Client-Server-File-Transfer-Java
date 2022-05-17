import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    static ArrayList<MyFile> myFiles=new ArrayList<>();

    public static void main(String[] args) throws IOException {

        int fileId=0;
        JFrame jframe = new JFrame("Server side");
        jframe.setSize(400,400);
        jframe.setLayout(new BoxLayout(jframe.getContentPane(),BoxLayout.Y_AXIS));
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jpanel=new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel,BoxLayout.Y_AXIS));

        JScrollPane jscrollpane= new JScrollPane(jpanel);
        jscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jltitle=new JLabel("file receiver/server view");
        jltitle.setFont(new Font("Arial",Font.BOLD,25));
        jltitle.setBorder(new EmptyBorder(20,0,10,0));
        jltitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jframe.add(jltitle);
        jframe.add(jscrollpane);
        jframe.setVisible(true);

        ServerSocket serverSocket = new ServerSocket(3039);

        while(true){

            try{
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();

                if(fileNameLength>0){
                    byte[] fileNameBytes= new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName= new String(fileNameBytes);

                    int fileContentLength= dataInputStream.readInt();

                    if(fileContentLength>0){
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes,0,fileContentLength);

                        JPanel jpfrow = new JPanel();
                        jpfrow.setLayout(new BoxLayout(jpfrow,BoxLayout.Y_AXIS));

                        JLabel JLabelFileName= new JLabel(fileName);
                        JLabelFileName.setFont(new Font("Arial",Font.BOLD,20));
                        JLabelFileName.setBorder(new EmptyBorder(10,0,10,0));
                        JLabelFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        if(getFileExtention(fileName).equalsIgnoreCase("txt")){
                              jpfrow.setName(String.valueOf(fileId));
                              jpfrow.addMouseListener(getMyMouseListener());
                              jpfrow.add(JLabelFileName);
                              jpanel.add(jpfrow);
                            jframe.validate();
                        }else {
                            jpfrow.setName(String.valueOf(fileId));
                            jpfrow.addMouseListener(getMyMouseListener());
                            jpfrow.add(JLabelFileName);
                            jpanel.add(jpfrow);
                            jframe.validate();

                        }
                        myFiles.add(new MyFile(fileId,fileName,fileContentBytes,getFileExtention(fileName)));

                        fileId++;
                    }

                }
            }catch(IOException error){
                error.printStackTrace();

            }
        }


    }

    public static MouseListener getMyMouseListener(){
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel= (JPanel) e.getSource();
                int fileId= Integer.parseInt(jPanel.getName());

                for(MyFile myFile: myFiles){
                    if(myFile.getId()==fileId){
                        JFrame JFPreview= createFrame(myFile.getName(),myFile.getData(),myFile.getFileExtension());
                        JFPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtention){
        JFrame jFrame = new JFrame("File Downloader");
        jFrame.setSize(400,400);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        JLabel jlTitle= new JLabel("File Downloader");
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setFont(new Font("Arial",Font.BOLD,25));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));

        JLabel jlPrompt= new JLabel("You will be downloading "+ fileName +" with extention: "+fileExtention);
        jlPrompt.setFont(new Font("Arial",Font.BOLD,20));
        jlPrompt.setBorder(new EmptyBorder(20,0,10,0));
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton buttonYes= new JButton("Yes");
        buttonYes.setPreferredSize(new Dimension(150,75));
        buttonYes.setFont(new Font("Arial",Font.BOLD,20));

        JButton buttonNo= new JButton("No");
        buttonNo.setPreferredSize(new Dimension(150,75));
        buttonNo.setFont(new Font("Arial",Font.BOLD,20));

        JLabel LabelFileContent= new JLabel();
        LabelFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel jpButtons = new JPanel();
        jpButtons.setBorder(new EmptyBorder(22,0,11,0));
        jpButtons.add(buttonYes);
        jpButtons.add(buttonNo);

        if(fileExtention.equalsIgnoreCase("txt")){
            LabelFileContent.setText("<html>"+new String(fileData)+"</html>");
        }

        else{
            LabelFileContent.setIcon(new ImageIcon(fileData));
        }

        buttonYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileToBeDownloaded= new File(fileName);
                try{
                    FileOutputStream fileOutputStream= new FileOutputStream(fileToBeDownloaded);
                    fileOutputStream.write(fileData);
                    fileOutputStream.close();

                    jFrame.dispose();
                }catch(IOException error){
                    error.printStackTrace();

                }
            }
        });
        buttonNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        jPanel.add(jlTitle);
        jPanel.add(jlPrompt);
        jPanel.add(LabelFileContent);
        jPanel.add(jpButtons);
        jFrame.add(jPanel);
        return jFrame;

    }
    public static String getFileExtention(String fileName){
        int i = fileName.lastIndexOf('.');
        if(i>0){
            return fileName.substring(i+1);
        }else{
            return "No extension found.";
        }
    }

}
