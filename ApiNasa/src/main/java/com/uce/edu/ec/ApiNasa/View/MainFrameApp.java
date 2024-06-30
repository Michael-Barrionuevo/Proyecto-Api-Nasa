package com.uce.edu.ec.ApiNasa.View;

import com.uce.edu.ec.ApiNasa.Model.Photo;
import com.uce.edu.ec.ApiNasa.Consumer.ConsumirAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.List;

@Component
public class MainFrameApp {

    private final ConsumirAPI consumirAPI;
    private List<Photo> photoList;
    private JLabel lblSequentialTime;
    private JLabel lblParallelTime;

    @Autowired
    public MainFrameApp(ConsumirAPI consumirAPI) {
        this.consumirAPI = consumirAPI;
        loadData();
    }

    private void loadData() {
        photoList = consumirAPI.getPhotos();
    }

    public void showMainFrame() {
        JFrame frame = new JFrame("NASA API Consumer");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JButton viewListButton = new JButton("Ver Lista");
        JButton filterButton = new JButton("Filtrar");
        JButton viewImageButton = new JButton("Ver Imagen");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewListButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(viewImageButton);

        frame.add(buttonPanel, BorderLayout.NORTH);

        viewListButton.addActionListener(e -> showList());
        filterButton.addActionListener(e -> filterData());
        viewImageButton.addActionListener(e -> viewImage());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showList() {
        String[] columnNames = {"ID", "Sol", "Camera", "Image Source", "Earth Date", "Rover"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Photo photo : photoList) {
            Object[] row = new Object[6];
            row[0] = photo.getId();
            row[1] = photo.getSol();
            row[2] = photo.getCamera().getFull_name();
            row[3] = photo.getImg_src();
            row[4] = photo.getEarth_date();
            row[5] = photo.getRover().getName();
            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame listFrame = new JFrame("Lista de Fotos");
        listFrame.setSize(800, 600);
        listFrame.add(scrollPane);
        listFrame.setLocationRelativeTo(null);
        listFrame.setVisible(true);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    String imgSrc = (String) table.getValueAt(row, 3);
                    showImage(imgSrc);
                }
            }
        });
    }

    private void filterData() {
        JFrame filterFrame = new JFrame("Seleccionar Cámara");
        filterFrame.setSize(800, 600);
        filterFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        filterFrame.setLayout(new BorderLayout());

        String[] cameraNames = photoList.stream()
                .map(photo -> photo.getCamera().getFull_name())
                .distinct()
                .toArray(String[]::new);

        JComboBox<String> cameraComboBox = new JComboBox<>(cameraNames);
        cameraComboBox.setPreferredSize(new Dimension(750, 30));

        JButton filterButton = new JButton("Filtrar");
        filterButton.addActionListener(e -> {
            String selectedCamera = (String) cameraComboBox.getSelectedItem();
            if (selectedCamera != null) {
                long startTimeSequential = System.currentTimeMillis();
                List<Photo> filteredList = photoList.stream()
                        .filter(photo -> photo.getCamera().getFull_name().equalsIgnoreCase(selectedCamera))
                        .toList();
                long endTimeSequential = System.currentTimeMillis();
                long sequentialTime = endTimeSequential - startTimeSequential;

                long startTimeParallel = System.currentTimeMillis();
                List<Photo> filteredListParallel = photoList.parallelStream()
                        .filter(photo -> photo.getCamera().getFull_name().equalsIgnoreCase(selectedCamera))
                        .toList();
                long endTimeParallel = System.currentTimeMillis();
                long parallelTime = endTimeParallel - startTimeParallel;

                showFilteredList(filteredList, sequentialTime, parallelTime);
                filterFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(filterFrame, "Seleccione una cámara primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        filterFrame.add(cameraComboBox, BorderLayout.CENTER);
        filterFrame.add(filterButton, BorderLayout.SOUTH);
        filterFrame.setLocationRelativeTo(null);
        filterFrame.setVisible(true);
    }

    private void showFilteredList(List<Photo> filteredList, long sequentialTime, long parallelTime) {
        String[] columnNames = {"ID", "Sol", "Camera", "Image Source", "Earth Date", "Rover"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Photo photo : filteredList) {
            Object[] row = new Object[6];
            row[0] = photo.getId();
            row[1] = photo.getSol();
            row[2] = photo.getCamera().getFull_name();
            row[3] = photo.getImg_src();
            row[4] = photo.getEarth_date();
            row[5] = photo.getRover().getName();
            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame filterFrame = new JFrame("Fotos Filtradas");
        filterFrame.setSize(800, 600);
        filterFrame.add(scrollPane);

        JPanel timePanel = new JPanel();
        lblSequentialTime = new JLabel("Tiempo secuencial: " + sequentialTime + " ms");
        lblParallelTime = new JLabel("Tiempo paralelo: " + parallelTime + " ms");
        timePanel.add(lblSequentialTime);
        timePanel.add(lblParallelTime);

        filterFrame.add(timePanel, BorderLayout.NORTH);
        filterFrame.setLocationRelativeTo(null);
        filterFrame.setVisible(true);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    String imgSrc = (String) table.getValueAt(row, 3);
                    showImage(imgSrc);
                }
            }
        });
    }

    private void viewImage() {
        JFrame imageFrame = new JFrame("Seleccionar Imagen");
        imageFrame.setSize(800, 600);
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imageFrame.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Camera", "Image Source"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Photo photo : photoList) {
            Object[] row = new Object[3];
            row[0] = photo.getId();
            row[1] = photo.getCamera().getFull_name();
            row[2] = photo.getImg_src();
            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    String imgSrc = (String) table.getValueAt(row, 2);
                    showImage(imgSrc);
                }
            }
        });

        JLabel instructionLabel = new JLabel("Selecciona la URL de la imagen que quieres ver:");
        imageFrame.add(instructionLabel, BorderLayout.NORTH);
        imageFrame.add(scrollPane, BorderLayout.CENTER);

        imageFrame.setLocationRelativeTo(null);
        imageFrame.setVisible(true);
    }

    private void showImage(String imgSrc) {
        JFrame imageFrame = new JFrame("Imagen");
        imageFrame.setSize(800, 600);

        try {
            URL url = new URL(imgSrc);
            ImageIcon imageIcon = new ImageIcon(url);
            JLabel imageLabel = new JLabel(imageIcon);
            imageFrame.add(new JScrollPane(imageLabel));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        imageFrame.setLocationRelativeTo(null);
        imageFrame.setVisible(true);
    }
}
