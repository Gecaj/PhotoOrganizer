package com.cislo.photos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends JPanel implements ActionListener {

    private final JFileChooser fileChooser;
    private final JButton sourceButton;
    private final JButton targetButton;
    private final JButton actionButton;
    private Path sourcePath;
    private Path targetPath;
    private static final JFrame frame = new JFrame("Photo organizer");
    private String infoMessage = "Done!\nCopied: %d files\nFailed to move following files: '%s'";

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

        add(buttonPanel, BorderLayout.PAGE_START);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == actionButton) {
            PhotoOrganizer photoOrganizer = new PhotoOrganizer();
            tryMoveFilesToNewLocation(photoOrganizer);
            JOptionPane.showMessageDialog(frame, String.format(infoMessage, photoOrganizer.getMovedPhotos(),
                    photoOrganizer.getFailedFileNames()));
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

    private void tryMoveFilesToNewLocation(PhotoOrganizer photoOrganizer) {
        try {
            System.out.println("sourcePath = " + sourcePath);
            System.out.println("targetPath = " + targetPath);
            photoOrganizer.moveFilesToNewLocation(sourcePath, targetPath);
        } catch (IOException e1) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            createAndShowGUI();
        });
    }
}
