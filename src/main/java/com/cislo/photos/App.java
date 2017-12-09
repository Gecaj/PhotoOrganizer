package com.cislo.photos;

import com.cislo.photos.utils.FilesCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Path;

public class App extends JPanel implements ActionListener {

    private final JFileChooser fileChooser;
    private final JButton sourceButton;
    private final JButton targetButton;
    private final JButton actionButton;
    private final String progressString = "Copied %d / %d files.";
    private Path sourcePath;
    private Path targetPath;
    private static final JFrame frame = new JFrame("Photo organizer");
    private String infoMessage = "Done!\nCopied: %d files\nFailed to move following files: '%s'";
    private Logger logger = LoggerFactory.getLogger(App.class);
    private final JProgressBar jProgressBar;
    private int filesCount;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            createAndShowGUI();
        });
    }

    public App() {
        super(new BorderLayout());
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        sourceButton = new JButton("Source");
        targetButton = new JButton("Target");
        actionButton = new JButton("Organize!");
        sourceButton.addActionListener(this);
        targetButton.addActionListener(this);
        actionButton.addActionListener(this);
        actionButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sourceButton);
        buttonPanel.add(targetButton);
        buttonPanel.add(actionButton);

        JPanel progressPanel = new JPanel();
        jProgressBar = new JProgressBar();
        progressPanel.add(jProgressBar);
        add(buttonPanel, BorderLayout.PAGE_START);
        add(progressPanel, BorderLayout.PAGE_END);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == actionButton) {
            tryCopyFilesToNewLocation();
        } else {
            savePath(source);
        }
        activateActionIfEligible();
    }

    private void activateActionIfEligible() {
        if (sourcePath != null && targetPath != null) {
            actionButton.setEnabled(true);
        }
    }

    private void tryCopyFilesToNewLocation() {
        try {
            targetButton.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            logger.info("sourcePath = " + sourcePath);
            logger.info("targetPath = " + targetPath);
            PhotoOrganizer photoOrganizer = new PhotoOrganizer(sourcePath, targetPath);
            jProgressBar.setString(String.format(progressString, 0, filesCount));
            jProgressBar.setStringPainted(true);
            photoOrganizer.addPropertyChangeListener(new ProgressListener(photoOrganizer));
            photoOrganizer.execute();
//            JOptionPane.showMessageDialog(frame, String.format(infoMessage, photoOrganizer.getMovedPhotos(),
//                    photoOrganizer.getFailedFileNames()));
        } catch (Exception e1) {
            logger.error(e1.getMessage());
            e1.printStackTrace();
            System.exit(1);
        }
    }

    private void savePath(Object source) {
        int decision = fileChooser.showOpenDialog(this);
        if (decision == JFileChooser.APPROVE_OPTION) {
            Path path = fileChooser.getSelectedFile().toPath().toAbsolutePath();
            if (source == sourceButton) {
                sourcePath = path;
                try {
                    filesCount = FilesCounter.getFilesCount(sourcePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (source == targetButton) {
                targetPath = path;
            }
        }
    }


    private static void createAndShowGUI() {
        //Create and set up the window.
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new App());

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    class ProgressListener implements PropertyChangeListener {
        private final PhotoOrganizer photoOrganizer;

        public ProgressListener(PhotoOrganizer photoOrganizer) {
            this.photoOrganizer = photoOrganizer;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            System.out.println("propertyName = " + propertyName);
            Object newValue = evt.getNewValue();
            System.out.println("newValue = " + newValue);
            if (!photoOrganizer.hasFinished()) {
                jProgressBar.setValue(photoOrganizer.getProgress());
                jProgressBar.setString(String.format(progressString, photoOrganizer.getMovedPhotos(), filesCount));
            } else {
                JOptionPane.showMessageDialog(frame, String.format(infoMessage, photoOrganizer.getMovedPhotos(),
                        photoOrganizer.getFailedFileNames()));
                setCursor(null);
            }
            if (evt.getPropertyName().equals("state") && evt.getNewValue() == SwingWorker.StateValue.DONE) {
                setCursor(null);
            }
        }


    }
}
