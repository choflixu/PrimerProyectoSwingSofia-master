package org.main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DungeonUI {
    private static JTree treePanel;
    private static Dungeon dungeon;
    private int pos;

   private final Button botonNorth = new Button("Norte");
    private final Button botonSur = new Button("Sur");
    private final Button botonEast = new Button("Este");
   private final Button botonOest = new Button("Oeste");
     private final TextArea descText = new TextArea();
   private final TextArea roomText = new TextArea();
    int lineas=0;
    DungeonUI() {
        JFrame f = new JFrame("Panel Example");
        JPanel mainpanel = new JPanel(new BorderLayout());

        JPanel gamePanel = new JPanel(new BorderLayout());
        JPanel roomsPanel = new JPanel(new BorderLayout());
        JPanel descPanel = new JPanel();
        gamePanel.setBackground(new Color(96, 108, 56));
        roomsPanel.setBackground(new Color(40, 54, 24));
        descPanel.setBackground(new Color(67, 40, 24));
        treePanel.setBackground(new Color(255, 230, 167));
        mainpanel.setBounds(40, 80, 850, 400);
        mainpanel.setBackground(Color.white);

        gamePanel.add(roomsPanel, BorderLayout.NORTH);
        gamePanel.add(descPanel, BorderLayout.CENTER);


        mainpanel.add(gamePanel, BorderLayout.EAST);
        mainpanel.add(treePanel, BorderLayout.CENTER);



        descPanel.add(descText);
        roomsPanel.add(roomText, BorderLayout.CENTER);

        roomsPanel.add(botonNorth, BorderLayout.NORTH);
        roomsPanel.add(botonSur, BorderLayout.SOUTH);
        roomsPanel.add(botonEast, BorderLayout.EAST);
        roomsPanel.add(botonOest, BorderLayout.WEST);
        botonNorth.addActionListener(clickBtnNorth(botonNorth));
        botonEast.addActionListener(clickBtnNorth(botonEast));
        botonOest.addActionListener(clickBtnNorth(botonOest));
        botonSur.addActionListener(clickBtnNorth(botonSur));
        cargarHabitacion();
        f.add(mainpanel);
        f.setSize(1000, 1000);
        f.setLayout(null);
        f.setVisible(true);
    }


    public ActionListener clickBtnNorth(Button btn){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Door door : dungeon.getRoom().get(pos).getDoors()) {
                    if (door.getName().equals(btn.getLabel())){
                        List<Room> room=new ArrayList<>();
                        room.addAll(dungeon.getRoom().stream().filter(dun ->dun.getId().equals(door.getDest())).toList());
                        int n=dungeon.getRoom().indexOf(room.get(0));
                        List<String> dests=new ArrayList<>();
                        for (Door doorss : dungeon.getRoom().get(n).getDoors()) {
                            dests.add(doorss.getDest());
                        }
                        if (dests.contains(dungeon.getRoom().get(pos).getId())){
                            pos=n;
                            roomText.insert(" Has ido al "+door.getName()+"\n ",lineas);
                            lineas++;
                            cargarHabitacion();
                        }
                    }
                }

            }
        };
    }


   public static void main(String args[]) {
        File xmlFile = new File("src/main/resources/xmls/mazmorra.xml");
        dungeon = readFile(xmlFile);
        treePanel = createJTreeFromDungeon(dungeon);
        new DungeonUI();
    }

    public void cargarHabitacion() {
        descText.setText(dungeon.getRoom().get(pos).description);

        Set<String> availableDoors = new HashSet<>();

        for (Door door : dungeon.getRoom().get(pos).getDoors()) {
            availableDoors.add(door.getName());
        }

        botonNorth.setEnabled(availableDoors.contains("Norte"));
        botonEast.setEnabled(availableDoors.contains("Este"));
        botonSur.setEnabled(availableDoors.contains("Sur"));
        botonOest.setEnabled(availableDoors.contains("Oeste"));
    }



    private static Dungeon readFile(File file) {
        Dungeon dungeon = new Dungeon();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList roomNodes = doc.getElementsByTagName("room");

            List<Room> rooms = new ArrayList<>();

            for (int i = 0; i < roomNodes.getLength(); i++) {
                List<Door> doors = new ArrayList<>();
                Element roomElement = (Element) roomNodes.item(i);
                String roomId = roomElement.getAttribute("id");
                String description = roomElement.getElementsByTagName("description").item(0).getTextContent();
                Room room = new Room(roomId, null, description);

                NodeList doorNodes = roomElement.getElementsByTagName("door");

                for (int j = 0; j < doorNodes.getLength(); j++) {
                    Element doorElement = (Element) doorNodes.item(j);
                    String doorName = doorElement.getAttribute("name");
                    String destRoomId = doorElement.getAttribute("dest");
                    Door door = new Door(doorName, destRoomId);
                    doors.add(door);

                }
                room.setDoors(doors);

                rooms.add(room);
            }

            dungeon.setRoom(rooms);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dungeon;

    }

    private static JTree createJTreeFromDungeon(Dungeon dungeon) {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Dungeon");


        List<Room> rooms = dungeon.getRoom();


        for (Room room : rooms) {

            DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode("Room: " + room.getId());

            List<Door> doors = room.getDoors();


            for (Door door : doors) {
                DefaultMutableTreeNode doorNode = new DefaultMutableTreeNode("Door: " + door.getName() + " to " + door.getDest());
                roomNode.add(doorNode);
            }


            rootNode.add(roomNode);
        }

        JTree jTree = new JTree(rootNode);

      jTree.expandRow(0);

        return jTree;
    }

}





