package controller;


import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenViewController implements Initializable {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Canvas drawCanvas;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private MenuItem menuLoad;
    private FileChooser fileChooser;
    private FileChooser.ExtensionFilter fcFilter;
    private File file;
    private GraphicsContext gc;
    int myScale = 0;
    double rotate = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //drawElement();
    }


    public void onFolderOpen(Event event) {

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
        fcFilter = new FileChooser.ExtensionFilter("CONFIG FILES(*.die)", "*.die");
        fileChooser.getExtensionFilters().add(fcFilter);
        file = fileChooser.showOpenDialog(new Stage());
        System.out.println("file selected...");
        System.out.println(file.getAbsolutePath());
        // drawPolylineFromFile(file);

        drawElement(getCoordFromList(getCoordFromFile(file, 50), 0), getCoordFromList(getCoordFromFile(file, 50), 1));
        // System.out.println("На линии?:" + isInLine());
    }


    public boolean isInLine(double x, double y) {
       /* double x = 92;
        double y  = 115;*/
        double itog;
        boolean inLine = false;
        double[] X = getCoordFromList(getCoordFromFile(file, 50), 0);
        double[] Y = getCoordFromList(getCoordFromFile(file, 50), 1);
        double x1, x2, y1, y2;

        for (int i = 1; i < X.length; i++) {
            x1 = X[i - 1];
            x2 = X[i];
            y1 = Y[i - 1];
            y2 = Y[i];

            System.out.printf("x = %f y = %f", x1, y1);
            System.out.printf("x = %f y = %f\n", x2, y2);
            itog = (y1-y2)*x + (x2-x1)*y + (x1*y2 - x2*y1);
          //  itog = (y - Y[i - 1]) - ((Y[i] - Y[i - 1]) / (X[i] - X[i - 1])) * (x - X[i - 1]);
            if (itog >= -65 & itog <= 65) {
                System.out.println("Итог = " + itog);
                inLine = true;
            }
        }

        return inLine;
    }

    public double getMaxFromArray(double[] arr) {
        double max = 0;
        for (double v : arr) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    public double getMinFromArray(double[] arr) {
        double min = arr[0];
        for (double v : arr) {
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    public void drawElement(double[] x, double[] y) {

        Canvas c = new Canvas();
        c.setWidth(getMaxFromArray(x) + getMinFromArray(x) + 3);
        c.setHeight(getMaxFromArray(y) + getMinFromArray(y) + 3);
        c.setLayoutX(0);
        c.setLayoutY(0);

        gc = c.getGraphicsContext2D();
        System.out.println("max x = " + getMaxFromArray(x));
        System.out.println("max y = " + getMaxFromArray(y));
        System.out.println("min x = " + getMinFromArray(x));
        System.out.println("min y = " + getMinFromArray(y));

        //   gc.strokeRect(c.getLayoutX(),c.getLayoutY(),220,200);


        //gc.strokePolyline(x, y, x.length);
        gc.strokePolygon(x, y, x.length);
        //   gc.strokeOval(50, 50, 150, 150);

        this.mainPane.getChildren().add(c);

        c.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double deltax;
                double deltay;
                deltax = event.getX() - c.getLayoutX();
                deltay = event.getY() - c.getLayoutY();
                c.setLayoutX((event.getX() - deltax));
                c.setLayoutY((event.getY() - deltay));
                System.out.println("deltax = " + deltax);
                System.out.println("deltay = " + deltay);
                System.out.println("x = " + event.getX());
                System.out.println("y = " + event.getY());
                System.out.println("x setLayoutX = " + c.getLayoutX());
                System.out.println("y setLayoutY= " + c.getLayoutY());

            }
        });

        c.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {
                rotate = c.getRotate();
                if (event.isControlDown()) {
                    rotate += event.getDeltaY() / 40;
                    c.setRotate(rotate);
                    System.out.println(rotate);
                }
            }
        });
        c.setOnMouseClicked(new EventHandler<MouseEvent>()

        {
            @Override
            public void handle(MouseEvent event) {
                if (!event.isAltDown()) {
                    rotate = 0;
                    System.out.println("click x " + event.getX());
                    System.out.println("click y " + event.getY());
                    System.out.println("x setLayoutX = " + c.getLayoutX());
                    System.out.println("y setLayoutY= " + c.getLayoutY());

                    Rectangle rectangle = new Rectangle(
                            c.getLayoutX() + c.getWidth() / 2 - 5, c.getLayoutY() + c.getHeight() / 2 - 5, 10, 10);
                    mainPane.getChildren().add(rectangle);
                    double[] cx = new double[]{c.getLayoutX(), c.getLayoutX() + c.getWidth() - 0, c.getLayoutX() + c.getWidth() - 0, c.getLayoutX(), c.getLayoutX()};
                    double[] cy = new double[]{c.getLayoutY(), c.getLayoutY(), c.getLayoutY() + c.getHeight() - 0, c.getLayoutY() + c.getHeight() - 0, c.getLayoutY()};
                    for (double v : cx) {
                        System.out.println("cx = " + v);
                    }
                    for (double v : cy) {
                        System.out.println("cy = " + v);
                    }
                    //gc.strokePolyline(cx,cy, 5);
                    rectangle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            c.setLayoutX((event.getX()) - c.getWidth() / 2);
                            c.setLayoutY((event.getY()) - c.getHeight() / 2);
                        }
                    });
                    rectangle.setOnMouseReleased(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            mainPane.getChildren().removeAll(rectangle);
                        }
                    });
                    rectangle.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            mainPane.getChildren().removeAll(rectangle);
                        }
                    });
                }

                if (event.isAltDown()) {

                    //System.out.println("На линии? " + isInLine(event.getX(), event.getY()));
                    System.out.println("На линии? " +       isInLine(event.getX(), event.getY()));
                }
            }
        });

    }

    public double[] getCoordFromList(Double[] arr, int param) {
        double[] coords = new double[arr.length / 2];

        System.out.println("--------------------");
        int j = 0;
        for (int i = param; i < arr.length; i += 2) {
            coords[j] = arr[i];
            System.out.println(coords[j]);
            j++;
        }
        return coords;
    }

    public Double[] getCoordFromFile(File file, double delt) {

        String str;
        int coutStr;
        double x = 0;
        double y = 0;
        double minX = 0;
        double minY = 0;
        double deltaXY = 0;
        Double cdr[] = null;

        List<Double> myCoords = new LinkedList();
        coutStr = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            str = br.readLine();
            while (str != null) {
                if (str.contains("point=") && !str.contains("cpoint=")) {
                    str.split(" ");
                    x = Double.parseDouble(str.split(" ")[0].substring(6))*3.57;
                    y = Double.parseDouble(str.split(" ")[1])*3.57;
                    minX = minX > x ? x : minX;
                    minY = minY > y ? y : minY;

                    myCoords.add(x);
                    myCoords.add(y);

                    //  System.out.println("minX = " + minX + " minY = " + minY);
                    coutStr++;
                }
                str = br.readLine();
            }

            // deltaXY = minX <= minY ? Math.round(minX) : Math.round(minY);
            br.close();
            System.out.println("minX = " + minX + " minY = " + minY);
            cdr = new Double[myCoords.size()];
            int p = 0;
            for (Double myCoord : myCoords) {
                if (p % 2 == 0) {
                    cdr[p++] = myCoord + (-minX);// + (-deltaXY) + delt;
                } else {
                    cdr[p++] = myCoord + (-minY) + delt;// + (-deltaXY) + delt;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("=================СМЕЩЕНИЕ на :" + -deltaXY + " dp ======================");
        return cdr;
    }

    public void onClearListener(ActionEvent actionEvent) {

        //gc.clearRect(0, 0,800 ,800 );
        mainPane.getChildren().remove(1);

    }

    private void saveToPNG() {
        File f = new File("image.png");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            WritableImage wi = new WritableImage((int) mainPane.getWidth(), (int) mainPane.getHeight());
            mainPane.snapshot(null, wi);
            RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
            ImageIO.write(ri, "png", f);
            System.out.println("сохранено успешно");

        } catch (IOException e) {
            e.printStackTrace();
            f.delete();
            return;
        }
    }

    public void onSaveListener(ActionEvent actionEvent) {
        saveToPNG();
    }
}
