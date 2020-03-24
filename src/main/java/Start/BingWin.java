package Start;

import Util.Bing.BingApi;
import Util.Bing.BingPicBean;
import Util.Tools;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BingWin extends Application {
    private ListView<BingPicBean> listView;
    private ImageView imageview;
    private Text text;
    private VBox textBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bing今日美图");
        StackPane root = new StackPane();
        root.setPadding(Insets.EMPTY);
        Scene scene = new Scene(root, 1366, 768);

        // 图像显示
        imageview = new ImageView();
        imageview.setPreserveRatio(true);
        imageview.setSmooth(true);
        imageview.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                if (!primaryStage.isFullScreen()) {
                    primaryStage.setFullScreen(true);
                    // 隐藏listview
                    TranslateTransition transition = new TranslateTransition();
                    transition.setNode(listView);
                    transition.setToY(-listView.getHeight());
                    transition.setDuration(Duration.millis(500));
                    transition.play();
                    // 隐藏文字
                    TranslateTransition bottom = new TranslateTransition();
                    bottom.setNode(textBox);
                    bottom.setToY(textBox.getHeight());
                    bottom.setDuration(Duration.millis(500));
                    bottom.play();
                } else {
                    primaryStage.setFullScreen(false);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                // 右键菜单
                popMenu popMenu = new popMenu();
                popMenu.addSaveMenu(primaryStage);
                popMenu.show(imageview, event.getScreenX(), event.getScreenY());
            }
        });
        imageview.fitWidthProperty().bind(root.widthProperty());
        imageview.fitHeightProperty().bind(root.heightProperty());
        root.getChildren().add(imageview);

        // 图像小图预览
        BorderPane borderPane = new BorderPane();
        // 设置面板不触发事件
//        borderPane.setPickOnBounds(false);
        // 将flowpan面板的鼠标点击事件转交给imageview
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getTarget().equals(borderPane))
                imageview.fireEvent(event);
        });
        // 列表
        listView = new ListView<>();
        ObservableList<BingPicBean> items = FXCollections.observableArrayList();
        listView.setItems(items);
        listView.setCellFactory(param -> new ImageCell());
        listView.setOrientation(Orientation.HORIZONTAL);
        listView.setMaxWidth(1142);
        listView.setMaxHeight(100);
        // 点击小图的事件
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            imageview.setImage(newValue.getImage());
            String copyright = newValue.getCopyright();
            text.setText(copyright);
            text.setSmooth(true);
            FadeTransition textFade = new FadeTransition(Duration.millis(500), text);
            textFade.setFromValue(0);
            textFade.setToValue(1);
            //动画
            FadeTransition imageViewFade = new FadeTransition(Duration.millis(200), imageview);
            imageViewFade.setFromValue(0.5);
            imageViewFade.setToValue(1);

            ParallelTransition parallelTransition = new ParallelTransition();
            parallelTransition.getChildren().addAll(
                    textFade,
                    imageViewFade
            );
            parallelTransition.setCycleCount(1);
            parallelTransition.play();
        });
        listView.setBackground(Background.EMPTY);
        borderPane.setTop(listView);
        BorderPane.setAlignment(listView, Pos.TOP_CENTER);


        // 故事面板
        textBox = new VBox();
        textBox.setBackground(new Background(new BackgroundFill(Color.web("#636363"), null, null)));
        textBox.setOpacity(0.8);
        text = new Text();
        text.setFont(Font.font("微软雅黑", FontWeight.BOLD, FontPosture.ITALIC, 14));
        text.wrappingWidthProperty().bind(scene.widthProperty());
        text.setFill(Color.WHITE);

        //添加到面板
        textBox.getChildren().add(text);
        textBox.setPrefHeight(80);
        borderPane.setBottom(textBox);
        BorderPane.setAlignment(textBox, Pos.BOTTOM_CENTER);
        root.getChildren().add(borderPane);


        primaryStage.setScene(scene);
        primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            // 当屏幕退出最大化时恢复listview
            if (!newValue) {
                // 顶部列表
                TranslateTransition top = new TranslateTransition();
                top.setNode(listView);
                top.setToY(0);
                top.setDuration(Duration.millis(500));
                TranslateTransition bottom = new TranslateTransition();
                // 底部列表
                bottom.setNode(textBox);
                // 是相对于textbox的y坐标，此时textbox的y相对于之前的移动等于80
                bottom.setToY(0);
                bottom.setDuration(Duration.millis(500));
                // 并行动画
                ParallelTransition parallelTransition = new ParallelTransition();
                parallelTransition.getChildren().addAll(
                        top,
                        bottom
                );
                parallelTransition.setCycleCount(1);
                parallelTransition.play();
            }
        });
        // 这里路径前面要加file
        primaryStage.getIcons().add(new Image("file:res/icon.jpg"));
        primaryStage.show();
        // 异步填充列表
        new Thread(new Task<BingPicBean>() {
            @Override
            protected BingPicBean call() {
                BingApi.getAllImages(new BingApi.ImageStreamListener() {
                    @Override
                    public void produce(Object obj) {
                        BingPicBean bingPicBean = (BingPicBean) obj;
                        bingPicBean.setImage(new Image(bingPicBean.getUrl()));
                        // 添加到列表
                        Platform.runLater(() -> {
                            items.add(bingPicBean);
                            if(items.size() == 1)
                                listView.getSelectionModel().select(0);
                        });
                        System.out.println("ImageCell 添加图片 " + bingPicBean.getUrl());
                    }

                    @Override
                    public void onComplete() {
                        // 不自动换壁纸
//                        Tools.changeBackground(Objects.requireNonNull(BingApi.getImages(1, 1)).get(0).getUrl());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
                return null;
            }
        }).start();
    }

    // 列表单元填充图片
    private class ImageCell extends ListCell<BingPicBean> {
        @Override
        protected void updateItem(BingPicBean item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                VBox vBox = new VBox();
                ImageView imageview = new ImageView();
                vBox.getChildren().add(imageview);
                Label time = new Label();
                imageview.setImage(item.getImage());
                imageview.setFitHeight(100);
                imageview.setFitWidth(100);
                imageview.setSmooth(true);
                imageview.setPreserveRatio(true);
                time.setText(item.getStartdate());
                time.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.ITALIC, 15));
                vBox.setAlignment(Pos.CENTER);
                vBox.setSpacing(5);
                vBox.getChildren().add(time);
                setGraphic(vBox);
            } else {
                setGraphic(null);
            }
        }


    }

    // 右键菜单
    private class popMenu extends ContextMenu {

        private popMenu() {
            MenuItem setWallpaper = new MenuItem("设置为壁纸                ");
            setWallpaper.setOnAction(event -> new Thread(() -> Tools.changeBackground(listView.getSelectionModel().getSelectedItem().getUrl(), listView.getSelectionModel().getSelectedItem().getCopyright())).start());
            getItems().add(setWallpaper);
        }

        void addSaveMenu(Stage stage) {
            MenuItem savePic = new MenuItem("保存到本地");
            savePic.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("保存");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("PNG", "*.png")
                );
                File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(imageview.getImage(), null), "png", file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            getItems().add(new SeparatorMenuItem());
            getItems().add(savePic);
        }
    }

    // 给列表添加时间
    private String timeAddOne(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = df.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            return df.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
