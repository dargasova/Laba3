package main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame {
    private final GridBagConstraints gbc = new GridBagConstraints();

    public GUI() throws URISyntaxException {
        // Установка внешнего вида приложения
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Не удалось настроить внешний вид системы");
        }

        // Настройка главного фрейма
        setTitle("Лабораторная работа № 3");
        setLayout(new GridBagLayout());
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        setPreferredSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Настройка размеров и цветов кнопок
        JButton openButton = new JButton("Выбрать файл");
        configureButton(openButton);
        JButton closeButton = new JButton("Выйти из программы");
        configureButton(closeButton);

        // Добавление кнопок
        addComponent(openButton, 0);
        addComponent(closeButton, 1);

        // Обработка событий кнопки закрытия
        closeButton.addActionListener(e -> dispose());

        // Обработка событий кнопки выбора файла
        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int result = fileChooser.showOpenDialog(GUI.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null && selectedFile.exists()) {
                    try {
                        Client client = new Client();
                        HashMap<String, Reactor> reactors = client.readCommonClass(selectedFile.getAbsolutePath());
                        if (reactors != null) {
                            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Reactors");
                            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

                            for (Map.Entry<String, Reactor> entry : reactors.entrySet()) {
                                DefaultMutableTreeNode reactorNode = new DefaultMutableTreeNode(entry.getKey());
                                reactorNode.add(new DefaultMutableTreeNode("Сгорание: " + entry.getValue().burnup));
                                reactorNode.add(new DefaultMutableTreeNode("Класс: " + entry.getValue().reactorClass));
                                reactorNode.add(new DefaultMutableTreeNode("Электрическая мощность: " + entry.getValue().electricalCapacity));
                                reactorNode.add(new DefaultMutableTreeNode("Первая загрузка: " + entry.getValue().firstLoad));
                                reactorNode.add(new DefaultMutableTreeNode("КПД: " + entry.getValue().kpd));
                                reactorNode.add(new DefaultMutableTreeNode("Срок службы: " + entry.getValue().lifeTime));
                                reactorNode.add(new DefaultMutableTreeNode("Тепловая мощность: " + entry.getValue().terminalCapacity));
                                reactorNode.add(new DefaultMutableTreeNode("Тип файла: " + entry.getValue().fileType));
                                treeModel.insertNodeInto(reactorNode, rootNode, rootNode.getChildCount());
                            }

                            JTree tree = new JTree(treeModel);
                            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                            JScrollPane scrollPane = new JScrollPane(tree);

                            JFrame treeFrame = new JFrame("Дерево реакторов");
                            treeFrame.add(scrollPane);
                            treeFrame.setSize(400, 300);
                            treeFrame.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(GUI.this, "Ошибка при чтении данных из файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(GUI.this, "Ошибка при обработке файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(GUI.this, "Выбранный файл не существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else if (result == JFileChooser.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(GUI.this, "Выбор файла отменен", "Информация", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(GUI.this, "Ошибка при выборе файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Упаковка и отображение главного фрейма
        pack();
        setVisible(true);
    }

    private void configureButton(JButton button) {
        Dimension buttonSize = new Dimension(300, 60);
        button.setPreferredSize(buttonSize);
        Color buttonColor = new Color(0, 128, 255);
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
    }

    private void addComponent(Component component, int gridy) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        add(component, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GUI();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }
}