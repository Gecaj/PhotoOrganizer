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

class UI extends JPanel implements ActionListener {

    private final JFileChooser fileChooser;
    private final JButton sourceButton;
    private final JButton targetButton;
    private final JButton actionButton;
    private final String progressString = "Copied %d / %d files.";
    private Path sourcePath;
    private Path targetPath;
    private static final JFrame frame = new JFrame("Photo organizer");
    private String infoMessage = "Done!\nCopied: %d files\nFailed to move following files: '%s'";
    private Logger logger = LoggerFactory.getLogger(UI.class);
    private final JProgressBar jProgressBar;
    private int filesCount;

    UI() {
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

    void showUI() {
        //Create and set up the window.
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new UI());

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
            actionButton.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            logger.info("sourcePath = " + sourcePath);
            logger.info("targetPath = " + targetPath);
            PhotoOrganizer photoOrganizer = new PhotoOrganizer(sourcePath, targetPath, FileOperation.MOVE);
            jProgressBar.setString(String.format(progressString, 0, filesCount));
            jProgressBar.setStringPainted(true);
            photoOrganizer.addPropertyChangeListener(new ProgressListener(photoOrganizer));
            photoOrganizer.execute();
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

    class ProgressListener implements PropertyChangeListener {
        private final PhotoOrganizer photoOrganizer;

        ProgressListener(PhotoOrganizer photoOrganizer) {
            this.photoOrganizer = photoOrganizer;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!photoOrganizer.isDone()) {
                jProgressBar.setValue(photoOrganizer.getProgress());
                jProgressBar.setString(String.format(progressString, photoOrganizer.getMovedPhotos(), filesCount));
            } else {
                JOptionPane.showMessageDialog(frame, String.format(infoMessage, photoOrganizer.getMovedPhotos(),
                        photoOrganizer.getFailedFileNames()));
                setCursor(null);
            }
        }


    }
}
