package SubWin;

import Util.Qihu.Category;
import Util.Qihu.MyImageView;
import Util.Qihu.QihuApi;
import Util.Qihu.QihuPicBean;
import Util.Tools;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MulticastSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QihuWin extends Application {
    private Scene scene;
    private SimpleIntegerProperty page = new SimpleIntegerProperty(0);
    private static final int pageSize = 5;
    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
//    private static final ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();
    private final static int[][][] style =
            {{{0, 0, 2, 2}, {2, 0, 1, 1}, {3, 0, 1, 1}, {2, 1, 1, 1}, {3, 1, 1, 1}},
            {{0, 0, 1, 1}, {1, 0, 2, 2}, {3, 0, 1, 1}, {0, 1, 1, 1}, {3, 1, 1, 1}},
            {{0, 0, 1, 1}, {1, 0, 1, 1}, {2, 0, 2, 2}, {0, 1, 1, 1}, {1, 1, 1, 1}}};
    private FlowPane flowPane;
    private List<Category> categorys = QihuApi.getCategories();
    private int  categoryId = 6;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("360壁纸");
        BorderPane borderPane = new BorderPane();
        StackPane stackPane = new StackPane();
        ScrollPane root = new ScrollPane();
        borderPane.setCenter(stackPane);
        stackPane.getChildren().add(root);
        scene = new Scene(borderPane, 1366, 768);
        root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setPadding(Insets.EMPTY);
        root.setFitToWidth(true);
        root.setFitToHeight(true);
        flowPane = new FlowPane();
        flowPane.setPadding(Insets.EMPTY);
        root.setContent(flowPane);

        flowPane.getChildren().add(getImagePane(page.getValue()));
        page.set(page.getValue() + 1);
        flowPane.getChildren().add(getImagePane(page.getValue()));

        flowPane.setAlignment(Pos.TOP_CENTER);
        flowPane.prefWidthProperty().bind(scene.widthProperty());
        flowPane.prefHeightProperty().bind(scene.heightProperty());

        Menu menu = new Menu("分类");
        for(Category category : categorys){
            CheckMenuItem checkMenuItem = new CheckMenuItem(category.getName());
            checkMenuItem.setOnAction(event -> {
                if (!((CheckMenuItem) event.getTarget()).isSelected())
                    ((CheckMenuItem) event.getTarget()).setSelected(true);
                for (MenuItem checkMenuItem1 : menu.getItems()) {
                    if (checkMenuItem1 != event.getSource())
                        ((CheckMenuItem) checkMenuItem1).setSelected(false);
                }
                flowPane.getChildren().clear();
                categoryId = category.getId();
                page.set(0);
                root.setVvalue(0);
                flowPane.getChildren().add(getImagePane(page.getValue()));
                page.set(page.getValue() + 1);
                flowPane.getChildren().add(getImagePane(page.getValue()));
            });
            if(category.getId() == 6)
                checkMenuItem.setSelected(true);
            menu.getItems().add(checkMenuItem);
        }

        borderPane.setTop(new MenuBar(menu));
        scene.getStylesheets().add("MainStyle.css");
        primaryStage.setScene(scene);
//        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(event -> fixedThreadPool.shutdown());
        primaryStage.getIcons().add(new Image("file:res/360icon.png"));
        primaryStage.show();

         //屏幕滚动事件
        root.setOnScroll(event -> {
            if(page.getValue() <= 40) {
                if (root.getVvalue() >= 1) {
                    int oldvalue = page.getValue();
                    System.out.println(oldvalue);
                    page.set(oldvalue + 1);
                    flowPane.getChildren().add(getImagePane(page.getValue()));
                }
            }
        });

//        root.vvalueProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue.intValue() >= 1) {
//                int oldvalue = page.getValue();
//                System.out.println("root " + oldvalue);
//                page.set(oldvalue + 1);
//                flowPane.getChildren().add(getImagePane(page.getValue()));
//            }
//        });
//        // 去掉多余的图片
//        page.addListener((observable, oldValue, newValue) -> {
//            System.out.println( "listener "+ newValue);
//            if(newValue.intValue() >= 5){
//                ObservableList<Node> imgs = ((GridPane)flowPane.getChildren().get(newValue.intValue() - 5)).getChildren();
//                for(int i = 0; i <5; i++) {
//                    ((ImageView) imgs.get(i)).setImage(null);
//                }
//            }
//        });
    }


    private GridPane getImagePane(int page) {
        GridPane grid = new GridPane();
        grid.setPadding(Insets.EMPTY);
        List<ImageView> imgs = new ArrayList<>(Arrays.asList(new ImageView(), new ImageView(), new ImageView(), new ImageView(), new ImageView()));
        int RandomStyle = new Random().nextInt(3);
        for (int i = 0; i < 5; i++) {
            ImageView view = imgs.get(i);
            DoubleBinding width;
            DoubleBinding height;
            if (i == RandomStyle) {
                width = scene.widthProperty().divide(2);
                height = scene.widthProperty().divide(2);
            } else {
                width = scene.widthProperty().divide(4);
                height = scene.widthProperty().divide(4);
            }
            view.fitWidthProperty().bind(width);
            view.fitHeightProperty().bind(height);
            view.setPreserveRatio(true);
            view.setId("image");
            grid.add(view, style[RandomStyle][i][0], style[RandomStyle][i][1], style[RandomStyle][i][2], style[RandomStyle][i][3]);
        }
        Iterator it = imgs.iterator();
        ObservableList<QihuPicBean> res = FXCollections.observableArrayList();
        fixedThreadPool.submit(new Thread(() -> QihuApi.getImages(categoryId, page * pageSize, pageSize, res)));
        res.addListener((ListChangeListener<QihuPicBean>) c -> {
            if (c.next() && c.wasAdded()) {
                List<? extends QihuPicBean> addList = c.getAddedSubList();
                for (QihuPicBean qihuPicBean : addList) {
                    if (it.hasNext()) {
                        ImageView view = (ImageView) it.next();
                        view.setImage(new Image(qihuPicBean.getImg_1024_768(), true));
                        view.setOnMouseClicked(new viewEvent(qihuPicBean));
                        FadeTransition ft = new FadeTransition(Duration.millis(1000), view);
                        ft.setFromValue(0);
                        ft.setToValue(1);
                        ft.play();
                    }
                }
            }
        });
        grid.setPadding(Insets.EMPTY);
//        grid.setGridLinesVisible(true);
        FlowPane.setMargin(grid, new Insets(0, 0, 0, 0));
        return grid;
    }

    // 照片查看
    private class viewEvent implements EventHandler<MouseEvent> {
        QihuPicBean qihuPicBean;
        viewEvent(QihuPicBean qihuPicBean){
            this.qihuPicBean = qihuPicBean;
        }

        @Override
        public void handle(MouseEvent event) {
            if(event.getButton() != MouseButton.PRIMARY)
                return;
            StackPane stackPane  = (StackPane) ((BorderPane) ((ImageView)event.getSource()).getScene().getRoot()).getCenter();

            MyImageView imageView = new MyImageView(qihuPicBean);
            imageView.prefWidthProperty().bind(scene.widthProperty());
            imageView.prefHeightProperty().bind(scene.heightProperty());
            stackPane.getChildren().add(imageView);
            imageView.setOnMouseClicked(event1 -> {
                if(event1.getTarget() == imageView)
                    stackPane.getChildren().remove(imageView);
            });
            popMenu popMenu = new popMenu(qihuPicBean);
            popMenu.addSaveMenu((Stage) stackPane.getScene().getWindow());
            popMenu.setAutoFix(true);
            popMenu.setAutoHide(true);
            imageView.getImageView().setOnMouseClicked(event1 -> {
                if(event1.getButton() == MouseButton.SECONDARY) {
                    popMenu.show(imageView.getImageView(), event1.getScreenX(), event1.getScreenY());
                }
            });
        }
    }

    public static int getPageSize() {
        return pageSize;
    }

    // 右键菜单
    private class popMenu extends ContextMenu {
        QihuPicBean qihuPicBean;
        private popMenu(QihuPicBean qihuPicBean) {
            this.qihuPicBean = qihuPicBean;
            Menu setWallpaper = new Menu("设置为壁纸                ");
            MenuItem image_org = new MenuItem("原图");
            MenuItem image_2560 = new MenuItem("2560X1440");
            MenuItem image_1440 = new MenuItem("1440X900");
            MenuItem image_1024 = new MenuItem("1024X768");
            MenuItem image_800 = new MenuItem("800X600");
            setWallpaper.getItems().addAll(image_org, image_2560,image_1440,image_1024,image_800);
            image_org.setOnAction(event -> new Thread(() -> Tools.changeBackground(qihuPicBean.getUrl())).start());
            image_2560.setOnAction(event -> new Thread(() -> Tools.changeBackground(qihuPicBean.getImg_2560_1440())).start());
            image_1440.setOnAction(event -> new Thread(() -> Tools.changeBackground(qihuPicBean.getImg_1440_900())).start());
            image_1024.setOnAction(event -> new Thread(() -> Tools.changeBackground(qihuPicBean.getImg_1024_768())).start());
            image_800.setOnAction(event -> new Thread(() -> Tools.changeBackground(qihuPicBean.getImg_800_600())).start());
            getItems().add(setWallpaper);
        }

        void addSaveMenu(Stage stage){
            Menu savePic = new Menu("保存到本地");
            MenuItem image_org = new MenuItem("原图");
            MenuItem image_2560 = new MenuItem("2560X1440");
            MenuItem image_1440 = new MenuItem("1440X900");
            MenuItem image_1024 = new MenuItem("1024X768");
            MenuItem image_800 = new MenuItem("800X600");
            savePic.getItems().addAll(image_org, image_2560,image_1440,image_1024,image_800);
            image_org.setOnAction(event -> save(stage,qihuPicBean.getUrl()));
            image_2560.setOnAction(event -> save(stage,qihuPicBean.getImg_2560_1440()));
            image_1440.setOnAction(event -> save(stage,qihuPicBean.getImg_1440_900()));
            image_1024.setOnAction(event -> save(stage,qihuPicBean.getImg_1024_768()));
            image_800.setOnAction(event -> save(stage,qihuPicBean.getImg_800_600()));
            getItems().add(new SeparatorMenuItem());
            getItems().add(savePic);
        }

        private void save(Stage stage, String url){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(new Image(url),null), "png", file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
