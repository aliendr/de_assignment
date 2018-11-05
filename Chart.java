package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;


import java.util.Locale;

import static java.lang.Math.abs;
import static java.lang.StrictMath.exp;


public class Chart extends Application {

    /**you can change h or interval**/
    private static double h=0.3;
    private static double X=3;
    private static double x0=0;
    private static double y0=0;
    private static int N;

    /**creating arrays for storing points**/
    private static double[] xPoints = new double[100000];
    private static double[] Exact = new double[100000];
    private static double[] Euler = new double[100000];
    private static double[] Improved_Euler = new double[100000];
    private static double[] Runga_Kutta = new double[100000];

    /**calculate value of given function**/
    private static double func(double x, double y){
        return y*(-2)+4*x;
    }

    /**value of exact solution**/
    private static double function(double x){
        return exp(-2*x)+2*x-1;
    }

    /**plotting solution graphs**/
    private XYChart.Series defined_chart(double[] x, double[] y, String name) {
        XYChart.Series serie = new XYChart.Series();
        serie.setName(name);
        for (int i = 0; i < (int)((X-x0)/h+1); i++) {
            serie.getData().add(new XYChart.Data(x[i],y[i]));
        }
        return serie;
    }

    private XYChart.Series error_chart(double[] x, double[] y, String name) {
        XYChart.Series serie = new XYChart.Series();
        serie.setName(name);
        for (int i = 0; i < x.length; i++) {
            serie.getData().add(new XYChart.Data(x[i],y[i]));
        }
        return serie;
    }

    /** fill arrays with points **/
    private static void calculating_points(double h){
        xPoints[0]=x0;
        for (int i = 1; i < (int)((X-x0)/h+1); i++) {
            xPoints[i]=h*i;
        }
        xPoints[(int)((X-x0)/h)]=X;

        //Exact
        for (int i = 0; i < (int)((X-x0)/h+1); i++) {
            Exact[i] = function(xPoints[i]);
        }

        //just Euler
        Euler[0]=y0;
        for (int i = 1; i < (int)((X-x0)/h+1); i++) {
            Euler[i] = Euler[i-1]+h*(func(xPoints[i-1],Euler[i-1]));
        }

        //improved Euler
        Improved_Euler[0]=y0;
        for (int i = 1; i < (int)((X-x0)/h+1); i++) {
            Improved_Euler[i] = Improved_Euler[i-1]
                    +h*(func(xPoints[i-1]+(h/2),Improved_Euler[i-1]+ (h/2)*func(xPoints[i-1],Improved_Euler[i-1])));
        }

        double k1,k2,k3,k4,temp;
        //Runga Kutta
        Runga_Kutta[0]=y0;
        for (int i = 1; i < (int)((X-x0)/h+1); i++) {

            k1=func(xPoints[i-1],Runga_Kutta[i-1]);
            k2=func(xPoints[i-1]+(h/2),Runga_Kutta[i-1]+(h*k1)/2);
            k3=func(xPoints[i-1]+(h/2),Runga_Kutta[i-1]+(h*k2)/2);
            k4=func(xPoints[i-1]+h,Runga_Kutta[i-1]+h*k3);
            temp=h*(k1+2*k2+2*k3+k4)/6;
            Runga_Kutta[i]=Runga_Kutta[i-1]+temp;
        }
    }

    /**local errors graphs**/
    private static double[][] local_errors_arr(){
        double[][] loc_er=new double[3][(int)((X-x0)/h+1)];
        for (int i = 0; i < (int)((X-x0)/h+1); i++) {
            loc_er[0][i]=abs(Exact[i]-Euler[i]);
            loc_er[1][i]=abs(Exact[i]-Improved_Euler[i]);
            loc_er[2][i]=abs(Exact[i]-Runga_Kutta[i]);
        }
        return loc_er;
    }

    //helping function
    private static double[][] local_e(double hh){
        calculating_points(hh);
        double[][] loc_er=new double[3][(int)((X-x0)/hh+1)];
        for (int i = 0; i < (int)((X-x0)/hh+1); i++) {
            loc_er[0][i]=abs(Exact[i]-Euler[i]);
            loc_er[1][i]=abs(Exact[i]-Improved_Euler[i]);
            loc_er[2][i]=abs(Exact[i]-Runga_Kutta[i]);
        }
        return loc_er;
    }

    private static double maxValue(double[] c) {
        double max = c[0];
        for (int ktr = 0; ktr < c.length; ktr++) {
            if (c[ktr] > max) {
                max = c[ktr];
            }
        }
        return max;
    }

    /**global errors graph**/
    private static double[][] global_errors_arr(){
        double h0=(X-x0)/N, hh=h0;
        double[][] er=new double[3][(int)(N/h0)];

        er[0][0]=y0; er[1][0]=y0; er[2][0]=y0;
        for (int i = 1; i < (int)(N/h0); i++) {
            double[][] temp=local_e(hh);
            er[0][i]=maxValue(temp[0]); //euler
            er[1][i]=maxValue(temp[1]); //improved
            er[2][i]=maxValue(temp[2]); //runga
            hh+=h0;
        }
        return er;
    }

    /**defining the axes**/
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();

    /**line chart with those axes**/
    private final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);


    @Override
    public void start(Stage stage) throws Exception {

        Group root = new Group();

        /** fill arrays with points **/
        calculating_points(h);

        lineChart.setTitle("Numerical methods in DE");
        stage.setTitle("Numerical methods in DE");

        /**putting theese all into one chart**/
        lineChart.getData().add(defined_chart(xPoints, Exact, "Exact"));
        lineChart.getData().add(defined_chart(xPoints, Euler, "Euler"));
        lineChart.getData().add(defined_chart(xPoints, Improved_Euler, "Improved Euler    "));
        lineChart.getData().add(defined_chart(xPoints, Runga_Kutta, "Runga Kutta"));

        /**buttons for user control**/
        BorderPane box3 = new BorderPane();
        TextField text3 = new TextField(String.format("%.2f", X));
        Label label3 = new Label("Limit X");
        box3.setLeft(label3);
        box3.setRight(text3);

        BorderPane box4 = new BorderPane();
        TextField text4 = new TextField(String.format("%.2f", h));
        Label label4 = new Label("H");
        box4.setLeft(label4);
        box4.setRight(text4);

        //for global error
        BorderPane box5 = new BorderPane();
        TextField text5 = new TextField(String.format("%d", N));
        Label label5 = new Label("N");
        box5.setLeft(label5);
        box5.setRight(text5);

        ToggleButton typeBut = new ToggleButton("Error");
        ToggleGroup group = new ToggleGroup();
        RadioButton localError = new RadioButton("Local");
        RadioButton globalError = new RadioButton("Global");
        localError.setToggleGroup(group);
        globalError.setToggleGroup(group);
        typeBut.setSelected(false);
        Button but = new Button();
        but.setText("Update");

        //when buttons pressed data on chart is updating
        but.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                X = Double.parseDouble(text3.getText());
                h = Double.parseDouble(text4.getText());
                N = Integer.parseInt(text5.getText());

                /** putting new points in the arrays**/
                calculating_points(h);

                /**removing previous graphs**/
                lineChart.getData().clear();

                if(typeBut.isSelected())
                {
                   if(localError.isSelected())
                    {
                        double[][] e=local_errors_arr();
                        lineChart.getData().add(defined_chart(xPoints, e[0], "Euler Error"));
                        lineChart.getData().add(defined_chart(xPoints, e[1], "Improved Euler Error"));
                        lineChart.getData().add(defined_chart(xPoints, e[2], "Runge Error"));
                    }
                    else
                    {
                        double h0=(X-x0)/N,hh=h0;
                        double[] num_axe= new double[(int)(N/h0)];
                        System.out.println(N);
                        System.out.println(X);
                        System.out.println(x0);
                        System.out.println(h0);
                        System.out.println(num_axe.length);
                        num_axe[0]=x0;
                        for (int i = 1; i < num_axe.length; i++) {
                            num_axe[i]=hh;
                            hh+=h0;
                        }

                        double[][] e=global_errors_arr();
                        lineChart.getData().add(error_chart(num_axe, e[0], "Global Runge Error"));
                        lineChart.getData().add(error_chart(num_axe, e[1], "Global Improved Euler Error"));
                        lineChart.getData().add(error_chart(num_axe, e[2], "Global Euler Error"));

/*                         double step =  ((limitX-x0) / N);
                        double temp[][] = new double[2][(int) (N/step)];
                        for (int i = 1; i <=temp[0].length; i++) {
                            h = i*step;
                            data = initData();
                            data = initDataForError(data);
                            temp[0][i-1] = h;
                            temp[1][i-1] = findMax(data);
                        }
                        graph.getData().addAll(getChart(temp[0],temp[1],"Global error"));

*/
                    }

                }
                else
                {
                    /**putting new points into one chart**/
                    lineChart.getData().add(defined_chart(xPoints, Exact, "Exact"));
                    lineChart.getData().add(defined_chart(xPoints, Euler, "Euler"));
                    lineChart.getData().add(defined_chart(xPoints, Improved_Euler, "Improved Euler   "));
                    lineChart.getData().add(defined_chart(xPoints, Runga_Kutta, "Runga Kutta"));


                }
            }
        });

        VBox dataVBox = new VBox();
        dataVBox.getChildren().addAll(box3, box4,box5);

        VBox typeVBox = new VBox();
        typeVBox.getChildren().addAll(but, typeBut,localError,globalError);

        HBox topBox = new HBox();
        topBox.setSpacing(5);
        topBox.getChildren().addAll(typeVBox, dataVBox);
        topBox.setPadding(new Insets(0, 10, 10, 10));

        BorderPane bord = new BorderPane();
        bord.setTop(topBox);

        bord.setCenter(lineChart);


        root.getChildren().add(bord);
        stage.setScene(new Scene(root, 505, 600));
        stage.show();
    }



    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }

  }

